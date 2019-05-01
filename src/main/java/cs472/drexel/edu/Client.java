/*
 * Client.java
 * 
 * Author: Matthew Dey
 * Date Created: April 15th, 2019
 * Drexel University
 * CS 472 - HW2 - Computer Networks
 */

package cs472.drexel.edu;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.*;
import java.io.InputStream;
import java.util.Scanner;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;


public class Client {

	private static Logger LOGGER = null;
	private FileHandler logfile;
	private String dataHost;
	private int dataPort;
	private Socket s;
	private static InputStreamReader input;
	private static OutputStreamWriter output;

	static {
		InputStream stream = Client.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
			LOGGER = Logger.getLogger(Client.class.getName());
  		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// alternative constructor host and port (other than default provided)
	Client(String host, String log) {
		try {
			this.logfile = new FileHandler(log, true);
			LOGGER.addHandler(logfile);
			SimpleFormatter formatter = new SimpleFormatter();  
			logfile.setFormatter(formatter);
			this.s = new Socket(host, 21);
			output = new OutputStreamWriter(s.getOutputStream());
			input = new InputStreamReader(s.getInputStream());
			BufferedReader r = new BufferedReader(input);
			LOGGER.info(r.readLine());
			this.login();
		} catch(UnknownHostException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		} catch(IOException x) {
			LOGGER.log(Level.SEVERE, x.toString(), x);
		}
	}

	public void user(String un) {
		String response = null;
		try {
			output.write("user " + un +"\r\n");
			output.flush();
			LOGGER.info("Sent: user " + un);
			BufferedReader r = new BufferedReader(input);
			response = r.readLine();
			LOGGER.info("Received: " + response);
			while(r.ready() && (response = r.readLine()) != null) {
				LOGGER.info("Received: " + response);
			}
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		return;
	}

	public void pass(String pw) {
		String response = null;
		try {
			output.write("pass " + pw + "\r\n");
			output.flush();
			LOGGER.info("Sent: pass " + pw);
			BufferedReader r = new BufferedReader(input);
			response = r.readLine();
			LOGGER.info("Received: " + response);
			while(r.ready() && (response = r.readLine()) != null) {
				LOGGER.info("Received: " + response);
			}
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		return;
	}

	public void port() {
		return;
	}


	public void retr(String filepath) {
	}

	public void stor(String filepath) throws FileNotFoundException, IOException {
		File outfile = new File(filepath);
		if (outfile.isDirectory()) {
			LOGGER.log(Level.WARNING, "Supplied file is a directory.");
			return;
		}
		String filename = outfile.getName();
		this.stor(new FileInputStream(outfile), filename);
		return;
	}

	private void stor(FileInputStream is, String filename) throws IOException {
		BufferedInputStream in = new BufferedInputStream(is);
		this.pasv();
		BufferedReader r = new BufferedReader(input);
		String response = r.readLine();
		this.parseHostPort(response);
		output.write("STOR " + filename);
		Socket datasocket = new Socket(this.dataHost, this.dataPort);
		BufferedOutputStream output = new BufferedOutputStream(datasocket.getOutputStream());
    	byte[] buffer = new byte[4096];
    	int bytesRead = 0;
    	while ((bytesRead = in.read(buffer)) != -1) {
     	 output.write(buffer, 0, bytesRead);
    	}
    	output.flush();
    	output.close();
    	input.close();
		response = r.readLine();
		return;
	}

	public void list(String directory) {
		try {
			this.pasv();
			BufferedReader r = new BufferedReader(input);
			String response = r.readLine();
			this.parseHostPort(response);
			output.write("LIST " + directory + "\r\n");
			Socket dataSocket = new Socket(this.dataHost, this.dataPort);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}

	public void pasv() throws IOException { 
		output.write("pasv\r\n");
		output.flush();
		}

	public void cwd(String directory) {
		String response = null;
		try {
			output.write("cwd " + directory + "\r\n");
			output.flush();
			LOGGER.info("Sent: cwd " + directory);
			BufferedReader r = new BufferedReader(input);
			response = r.readLine();
			LOGGER.info("Received: " + response);
			while(r.ready() && (response = r.readLine()) != null) {
				LOGGER.info("Received: " + response);
			}
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		return;
	}


	public void pwd() {
		String response = null;
		try {
			output.write("pwd\r\n");
			output.flush();
			LOGGER.info("Sent: pwd");
			BufferedReader r = new BufferedReader(input);
			response = r.readLine();
			LOGGER.info("Received: " + response);
			while(r.ready() && (response = r.readLine()) != null) {
				LOGGER.info("Received: " + response);
			}
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		return;
	}

	public void help() {
		String response = null;
		try {
			output.write("help\r\n");
			output.flush();
			LOGGER.info("Sent: help");
			BufferedReader r = new BufferedReader(input);
			response = r.readLine();
			LOGGER.info("Received: " + response);
			while(r.ready() && (response = r.readLine()) != null) {
				LOGGER.info("Received: " + response);
			}
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		return;
	}

	public void systemInfo() {
		String response = null;
		try {
			output.write("syst\r\n");
			output.flush();
			LOGGER.info("Sent: syst");
			BufferedReader r = new BufferedReader(input);
			response = r.readLine();
			LOGGER.info("Received: " + response);
			while(r.ready() && (response = r.readLine()) != null) {
				LOGGER.info("Received: " + response);
			}
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		return;
	}

	// returns client socket port number
	private static int getPortPASV(String num1, String num2) {
		int toNum1 = Integer.parseInt(num1);
		int toNum2 = Integer.parseInt(num2);
		int port = (toNum1 * 256) + toNum2;
		return port;
	}

	private int getPort() {
		//System.out.println("Client socket port number: " + s.getPort());
		return s.getLocalPort();
	}

	// returns client socket host address 
	private String getHost() {
		//System.out.println("Client socket port number: " + s.getInetAddress());
		return s.getInetAddress().toString();
	}

	private void parseHostPort(String s) {
		String[] paren = s.split("[\\(\\)]");
		String[] numbers = paren[1].split(",");
		this.dataHost = numbers[0] + "." + numbers [1] +"." + numbers[2] + "." + numbers [3];
		this.dataPort = getPortPASV(numbers[4], numbers[5]);
		// System.out.println(this.dataHost);
		// System.out.println(this.dataPort);
		return;
	}

	public void login() {
		Scanner s = new Scanner(System.in);
		String username, password;
		System.out.print("Username: ");
		username = s.nextLine();
		System.out.print("Password: ");
		password = s.nextLine();
		this.user(username);
		this.pass(password);
		return;
		
	}

	public void quit() {
		try {
			output.write("quit\r\n");
			this.s.close();
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}
}
