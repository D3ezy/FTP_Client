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
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;


public class Client {

	private static Logger LOGGER = null;
	private FileHandler logfile;
	private String dataHost;
	private int dataPort;
	private Socket s;
	private boolean inPassive;
	private static InputStreamReader input;
	private static OutputStreamWriter output;
	private static BufferedReader reader;

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
			reader = new BufferedReader(input);
			LOGGER.info(reader.readLine());
			inPassive = false;
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
			response = reader.readLine();
			LOGGER.info("Received: " + response);
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
			response = reader.readLine();
			LOGGER.info("Received: " + response);
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		return;
	}

	public void port() {
		// get active client port
		// parse client port to TCP math
		// 
		return;
	}


	public void retr(String filename) {
		try {
			File f = new File("./" + filename);
			String path = this.pwd() + "/" + filename;
			if(inPassive) {
				String response = this.pasv();
				this.parseHostPort(response);
			} else {
				this.port();
			}
			output.write("RETR " + path + "\r\n");
			Socket data_trans = new Socket(this.dataHost, this.dataPort);
			BufferedInputStream in = new BufferedInputStream(data_trans.getInputStream());
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
			byte[] buf = new byte[4096];
			int read;
			while(in.available() > 0) {
				read = in.read(buf);
				out.write(buf, 0, read);
			}
			out.flush();
			out.close();
			in.close();
			data_trans.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}

	public void stor(String filepath) {
		try {
			File f = new File(filepath);
			String filename = f.getName();
			FileInputStream is = new FileInputStream(f);
			BufferedInputStream in = new BufferedInputStream(is);
			if(inPassive) {
				String response = this.pasv();
				this.parseHostPort(response);
			} else {
				this.port();
			}
			output.write("STOR " + filename+ "\r\n");
			Socket data_trans = new Socket(this.dataHost, this.dataPort);
			BufferedOutputStream out = new BufferedOutputStream(data_trans.getOutputStream());
			byte[] buf = new byte[4096];
			int read;
			while(in.available() > 0) {
				read = in.read(buf);
				out.write(buf, 0, read);	
			}
			out.flush();
			out.close();
			in.close();
			is.close();
			data_trans.close();
		} catch(FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		} catch(IOException x) {
			LOGGER.log(Level.SEVERE, x.toString(), x);
		}
		return;
	}

	public void list(String directory) {
		try {
			String response = this.pasv();
			this.parseHostPort(response);
			if (directory.equals("")) {
				output.write("list\r\n");
				output.flush();
			} else {
				output.write("LIST " + directory);
				output.flush();
			}
			// response = reader.readLine();
			// LOGGER.info("Received: " + response);
			Socket data_trans = new Socket(this.dataHost, this.dataPort);
			data_trans.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}

	public String pasv() { 
		String response = null;
		try {
			output.write("pasv\r\n");
			output.flush();
			response = reader.readLine();
			LOGGER.info("Received: " + response);
			inPassive = true;
			return response;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		return response;

	}

	public void cwd(String directory) {
		String response = null;
		try {
			output.write("cwd " + directory + "\r\n");
			output.flush();
			LOGGER.info("Sent: cwd " + directory);
			response = reader.readLine();
			LOGGER.info("Received: " + response);
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		return;
	}


	public String pwd() {
		String response = null;
		try {
			output.write("pwd\r\n");
			output.flush();
			LOGGER.info("Sent: pwd");
			response = reader.readLine();
			System.out.println(response);
			LOGGER.info("Received: " + response);
			return null;
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return null;
		}
	}

	public void help() {
		String response = null;
		try {
			output.write("help\r\n");
			output.flush();
			LOGGER.info("Sent: help");
			response = reader.readLine();
			LOGGER.info("Received: " + response);
			while(reader.ready() && (response = reader.readLine()) != null) {
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
			response = reader.readLine();
			LOGGER.info("Received: " + response);
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
