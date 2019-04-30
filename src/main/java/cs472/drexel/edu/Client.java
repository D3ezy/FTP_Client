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
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.*;
import java.io.InputStream;

public class Client {

	private static Logger LOGGER = null;
	private FileHandler logfile;
	private boolean isConnected;
	private DataConnection d;
	private boolean inPassiveMode;
	private Socket s;
	private static InputStreamReader input;
	private static DataOutputStream output;

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
			this.isConnected = true;
			output = new DataOutputStream(s.getOutputStream());
			input = new InputStreamReader(s.getInputStream());
			this.doProtocol("connecting to " + host);
			this.doProtocol("Connected.\r\n");
		} catch(UnknownHostException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			this.isConnected = false;
		} catch(IOException x) {
			LOGGER.log(Level.SEVERE, x.toString(), x);
		}
	}

	public void doProtocol(String cmd) {
		String response = null;
		try {
			output.writeBytes(cmd);
			String rem = cmd.replace("\r\n", "");
			LOGGER.info("Sent: " + rem);
			BufferedReader r = new BufferedReader(input);
			response = r.readLine();
			LOGGER.info("Received: " + response);
			output.flush();
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		return;
	}

	// functions below for use with port/pasv commands for active/passive mode
	// returns client socket port number
	public void enterPassiveMode() {
		String response;
		String host;
		int port;
		try {
			output.writeBytes("pasv\r\n");
			LOGGER.info("Sent: pasv");
			BufferedReader r = new BufferedReader(input);
			response = r.readLine(); // read the response back from the server to get the IP it's listening on, create the data connection there
			String[] args = response.split("");
			host = args[0];
			port = Integer.parseInt(args[1]);
			LOGGER.info("enterPassiveMode: passive mode host: " + host);
			LOGGER.info("enterPassiveMode: passive mode port: " + port);
			d = new DataConnection(host, port);
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		} catch(NumberFormatException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}

	protected int getPort() {
		System.out.println("Client socket port number: " + s.getPort());
		return s.getLocalPort();
	}

	// returns client socket host address 
	protected String getHost() {
		System.out.println("Client socket port number: " + s.getInetAddress());
		return s.getInetAddress().toString();
	}

	protected boolean inPassive() {
		return this.inPassiveMode;
	}

	private static String intToHex(int n) {
		return Integer.toHexString(n);
	}
	
}
