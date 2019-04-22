/*
 * Client.java
 * 
 * Author: Matthew Dey
 * Date Created: April 15th, 2019
 * Drexel University
 * CS 472 - HW2 - Computer Networks
 */

package cs472.drexel.edu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.*;
import java.io.InputStream;

public class Client {

	private static Logger LOGGER = null;
	private boolean isConnected;
	private Socket s;
	private static DataInputStream input;
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
	
	// default constructor
	Client(String host) {
		try {
			this.s = new Socket(host, 21);
			this.isConnected = true;
			output = new DataOutputStream(s.getOutputStream());
			input = new DataInputStream(s.getInputStream());
			this.doProtocol("");
		} catch(UnknownHostException e) {
			System.out.println(e);
			LOGGER.log(Level.SEVERE, e.toString(), e);
			this.isConnected = false;
		} catch(IOException x) {
			System.out.println(x);
			LOGGER.log(Level.SEVERE, x.toString(), x);
		}
	}

	// alternative constructor host and port (other than default provided)
	Client(String host, String log) {
		try {
			this.s = new Socket(host, 21);
			this.isConnected = true;
			output = new DataOutputStream(s.getOutputStream());
			input = new DataInputStream(s.getInputStream());
			this.doProtocol("");
		} catch(UnknownHostException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			this.isConnected = false;
		} catch(IOException x) {
			LOGGER.log(Level.SEVERE, x.toString(), x);
		}
	}

	public void doProtocol(String cmd) {
		try {
			output.writeChars(cmd);
			String response = input.readLine();
			output.flush();
			System.out.println(response);
			LOGGER.info(response);
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return;
		}
		return;
	}
	
}
