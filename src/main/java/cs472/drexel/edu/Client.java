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
	
	// default constructor, used for testing
	// no port or log file provided
	Client(String host) {
		try {
			this.s = new Socket(host, 21);
			this.isConnected = true;
			output = new DataOutputStream(s.getOutputStream());
			input = new InputStreamReader(s.getInputStream());
			this.doProtocol("");
		} catch(UnknownHostException e) {
			// System.out.println(e);
			LOGGER.log(Level.SEVERE, e.toString(), e);
			this.isConnected = false;
		} catch(IOException x) {
			// System.out.println(x);
			LOGGER.log(Level.SEVERE, x.toString(), x);
		} catch (SecurityException s) {
			LOGGER.log(Level.SEVERE, s.toString(), s);
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
	
}
