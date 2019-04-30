/*
 * DataConnection.java
 * 
 * Author: Matthew Dey
 * Date Created: April 30th, 2019
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

public class DataConnection {
    
    private static Logger LOGGER = null;
	private Socket s;
	private static InputStreamReader input;
    private static DataOutputStream output;
    
    // use same log file from the client connection
    static {
		InputStream stream = Client.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(stream);
			LOGGER = Logger.getLogger(Client.class.getName());
  		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    // default port constructor (port: 20)
    /* From RFC959: "The mechanics of transferring data consists of setting up the data
       connection to the appropriate ports and choosing the parameters
       for transfer.  Both the user and the server-DTPs have a default
       data port.  The user-process default data port is the same as the
       control connection port (i.e., U).  The server-process default
       data port is the port adjacent to the control connection port
       (i.e., L-1)." */
    DataConnection(String host) {
        try {
			this.s = new Socket(host, 20);
			output = new DataOutputStream(s.getOutputStream());
            input = new InputStreamReader(s.getInputStream());
            LOGGER.info("DataConnection: Estabishing a connection for data stream " + host +  ":20");
		} catch(UnknownHostException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		} catch(IOException x) {
			LOGGER.log(Level.SEVERE, x.toString(), x);
		} catch (SecurityException s) {
			LOGGER.log(Level.SEVERE, s.toString(), s);
		}
    }

    // newly supplied data port alternate constructor
    DataConnection(String host, int port) {
        try {
			this.s = new Socket(host, port);
			output = new DataOutputStream(s.getOutputStream());
            input = new InputStreamReader(s.getInputStream());
            LOGGER.info("DataConnection: Estabishing a connection for data stream " + host +  ":" + port);
		} catch(UnknownHostException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		} catch(IOException x) {
			LOGGER.log(Level.SEVERE, x.toString(), x);
		} catch (SecurityException s) {
			LOGGER.log(Level.SEVERE, s.toString(), s);
		}
    }

    // the protocol for this object is to transfer bytes to the data transfer socket
    public void sendData() {
        
    }

    // the protocol for this function is to receive data from the data transfer socket
    public void receiveData() {
    }
}