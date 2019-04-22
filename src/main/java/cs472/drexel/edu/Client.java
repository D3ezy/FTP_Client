/*
 * Client.java
 * 
 * Author: Matthew Dey
 * Date Created: April 15th, 2019
 * Drexel University
 * CS 472 - HW2 - Computer Networks
 */

package cs472.drexel.edu;

import java.net.Socket;
import java.util.logging.*;

public class Client {

	private Logger logger;
	private Socket s;
	
	Client() {
		// create a new logger for the client
		this.logger = Logger.getLogger(Client.class.getName());
	}
	
}
