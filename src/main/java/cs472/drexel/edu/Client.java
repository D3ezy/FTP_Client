/*
 * Client.java
 * 
 * Author: Matthew Dey
 * Date Created: April 15th, 2019
 * Drexel University
 * CS 472 - HW2 - Computer Networks
 * 
 * 
 * Commented out all of the java.util.Logging statements
 * 
 */

package cs472.drexel.edu;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.util.regex.*;

public class Client {

	private boolean pasvActive;
	private boolean eprtActive;
	private boolean portActive;
	private boolean epsvActive;
	private static Logger LOGGER = null;
	private String hostname;
	private ServerSocket ss;
	private int serverport;
	private String dataHost;
	private int dataPort;
	private Socket s;
	private static InputStreamReader input;
	private static OutputStreamWriter output;
	private static BufferedReader reader;

	// alternative constructor host and log file provided (uses default port)
	Client(String host, String log) {
		try {
			LOGGER = new Logger(log);
			this.s = new Socket(host, 21);
			// initialize new data readers from socket streams
			output = new OutputStreamWriter(s.getOutputStream());
			input = new InputStreamReader(s.getInputStream());
			reader = new BufferedReader(input);
			hostname = host;
			pasvActive = false; // set all transfer methods to false (nothing has been activated yet)
			eprtActive = false;
			portActive = false;
			epsvActive = false;
			LOGGER.log(reader.readLine());
			this.login();
		} catch(UnknownHostException e) {
			LOGGER.log(e.toString());
		} catch(IOException x) {
			LOGGER.log(x.toString());
			LOGGER.log("Unable to connection socket, please try a different IP address.");
			return;
		}
	}

	/*
		Sends user command to FTP Server to authenticate the user. Reads the response
		from the server (which should be asking for a password).
	*/
	public void user(String un) {
		String response = null;
		try {
			output.write("user " + un +"\r\n");
			output.flush();
			LOGGER.log("Sent: user " + un);
			response = reader.readLine();
			LOGGER.log("Received: " + response);
		} catch(IOException e) {
			LOGGER.log(e.toString());
			return;
		}
		return;
	}

	/*
		Sends "pass" command to the FTP Server, which finishes the authetication handshake. The user
		will get a reply after this function is called that they are either logged in or not recognized.
	*/
	public void pass(String pw) {
		String response = null;
		try {
			output.write("pass " + pw + "\r\n");
			output.flush();
			LOGGER.log("Sent: pass " + pw);
			response = reader.readLine();
			LOGGER.log("Received: " + response);
		} catch(IOException e) {
			LOGGER.log(e.toString());
			return;
		}
		return;
	}


	/*
		Sends the "port" command to the FTP Server, parses the TCP header and port number
		from the command and asks the server to try to connect to the client on the specified
		port and host for data transfer (instead of the default). Makes the PORT command the active
		data transfer command. 
	*/
	public void port(String tcp) {
		String response;
		try {
			// send port command
			output.write("port " + tcp + "\r\n");
			output.flush();
			LOGGER.log("Sent: port " + tcp);
			response = reader.readLine();
			LOGGER.log("Received: " + response);
			// create a listening socket on the port from the port command
			String[] values = tcp.split(",");
			serverport = getPort(values[4], values[5]);
			ss = new ServerSocket(serverport);
			// set port as the active data connection type
			pasvActive = false;
			portActive = true;
			eprtActive = false;
			epsvActive = false;
		} catch (IOException e) {
			LOGGER.log(e.toString());
		}
		return;
	}

	/* 
	        AF Number   Protocol
        ---------   --------
        1           Internet Protocol, Version 4 [Pos81a]
		2           Internet Protocol, Version 6 [DH96]
		EPRT |1|132.235.1.2|6275|
        EPRT |2|1080::8:800:200C:417A|5282|
	*/
	/*
		From the update RFC 2428, implements the "eprt" command which parses
		the configuration sent by the user delineated by "|" pipe symbols. Examples are 
		above.

		NOTE: This function terminates the communication connection due to firewall restrictions.
	*/
	public void eprt(String tcp) {
		String[] values = tcp.split("\\|");
		String response;
		try {
			output.write("eprt " + tcp + "\r\n");
			output.flush();
			LOGGER.log("Sent: eprt " + tcp);
			response = reader.readLine();
			LOGGER.log("Received: " + response);
			serverport = Integer.parseInt(values[3]);
			System.out.println(serverport);
			ss = new ServerSocket(serverport);
			pasvActive = false;
			portActive = false;
			eprtActive = true;
			epsvActive = false;
		} catch(NumberFormatException e) {
			LOGGER.log("Illegal use of EPRT command. Please use the same delimiter and only numerical values. Usage: EPRT<space><d><net-prt><d><net-addr><d><tcp-port><d>");
			return;
		} catch (IOException e) {
			LOGGER.log(e.toString());
			return;
		} 
	}


	/*
		Retrieves the specified file from the server. Response code depends on if the file exists or not on
		the server. Opens a data connection depending on the active data connection type (active, passive, eprt, epsv).
		Accepts the data from the data connection and writes the buffer from a file until there is no more data to be read.
	*/
	public void retr(String filename) {
		String response;
		try {
			File f = new File("./" + filename);
			LOGGER.log("File created: " + filename);
			String path = this.pwd() + "/" + filename;
			if (!eprtActive && !portActive && !epsvActive && !pasvActive) { // if none of the port, eprt, epsv, or pasv commands have been used already, default to pasv.
				this.pasv();
			}
			if (portActive || eprtActive) { // if active mode is enabled
				output.write("RETR " + path + "\r\n");
				output.flush();
				Socket ftpServer = ss.accept();
				response = reader.readLine();
				LOGGER.log("Received: " + response);
				BufferedInputStream fromServer = new BufferedInputStream(ftpServer.getInputStream());
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
				byte[] bufSize = new byte[4096];
				int bytesRead;
				while((bytesRead = fromServer.read(bufSize)) != -1) {
					LOGGER.log("Bytes read from server: " + bytesRead);
					out.write(bufSize, 0, bytesRead);
				}
				out.flush();
				out.close();
				fromServer.close();
				ftpServer.close();
			} else { // if pasv is enabled
				output.write("RETR " + path + "\r\n");
				output.flush();
				Socket data_trans = new Socket(this.dataHost, this.dataPort);
				response = reader.readLine();
				LOGGER.log("Received: " + response);
				BufferedInputStream fromServer = new BufferedInputStream(data_trans.getInputStream());
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
				byte[] bufSize = new byte[4096];
				int bytesRead;
				while((bytesRead = fromServer.read(bufSize)) != -1) {
					LOGGER.log("Bytes read from server: " + bytesRead);
					out.write(bufSize, 0, bytesRead);
				}
				out.flush();
				out.close();
				fromServer.close();
				data_trans.close();
			}
			pasvActive = false;
			response = reader.readLine();
			LOGGER.log("Received: " + response);
		} catch (IOException e) {
			LOGGER.log(e.toString());
		}
	}

	/*
		Stores a file on the FTP Server, opens a data connection and reads the specified file (if it exists) and sends
		those byes to the data connection opened by the server (based on the active data transfer type).
	*/
	// by default use passive mode, unless the user specifies eprt, port, or epsv
	public void stor(String filepath) {
		String response;
		try {
			File f = new File(filepath);
			String filename = f.getName();
			FileInputStream is = new FileInputStream(f);
			BufferedInputStream in = new BufferedInputStream(is);
			if (!eprtActive && !portActive && !epsvActive && !pasvActive) { // if none of the port, eprt, epsv, or pasv commands have been used already, default to pasv.
				this.pasv();
			}
			if (portActive || eprtActive) { // if active mode is enabled
				// send stor command and start listening on port command specified addresses.
				output.write("STOR " + filename + "\r\n");
				output.flush();
				Socket ftpServer = ss.accept();
				response = reader.readLine();
				LOGGER.log("Received: " + response);
				BufferedOutputStream out = new BufferedOutputStream(ftpServer.getOutputStream());
				byte[] bufSize = new byte[4096];
				int read;
				while(in.available() > 0) {
					read = in.read(bufSize);
					out.write(bufSize, 0, read);	
				}
				// close connections.
				out.flush();
				out.close();
				in.close();
				is.close();
				ftpServer.close();
			} else { // passive modes are enabled
				output.write("STOR " + filename + "\r\n");
				output.flush();
				Socket data_trans = new Socket(this.dataHost, this.dataPort);
				response = reader.readLine();
				LOGGER.log("Received: " + response);
				BufferedOutputStream out = new BufferedOutputStream(data_trans.getOutputStream());
				byte[] bufSize = new byte[4096];
				int read;
				while(in.available() > 0) {
					read = in.read(bufSize);
					out.write(bufSize, 0, read);	
				}
				// close connections.
				out.flush();
				out.close();
				in.close();
				is.close();
				data_trans.close();
			}
			pasvActive = false;
			response = reader.readLine();
			LOGGER.log("Received: " + response);
		} catch(FileNotFoundException e) {
			LOGGER.log(e.toString());
		} catch(IOException x) {
			LOGGER.log(x.toString());
		}
		return;
	}

	/*
		Opens a data connection to retrieve a list of files in the directory specified from the server. Think LS command
		over Linux. Depending on the active type of data connection the server communication let's the client know when the 
		data is sent. 
	*/
	public void list(String directory) {
		String response;
		try {
			if (!eprtActive && !portActive && !epsvActive && !pasvActive) { // if none of the port, eprt, epsv, or pasv commands have been used already, default to pasv.
				this.pasv();
			}
			if (portActive || eprtActive) { // if active mode is enabled
				if (directory.equals("")) {
					output.write("list\r\n");
					output.flush();
				} else {
					output.write("LIST " + directory + "\r\n");
					output.flush();
				}
				Socket ftpServer = ss.accept();
				BufferedInputStream fromServer = new BufferedInputStream(ftpServer.getInputStream());
				response = reader.readLine();
				LOGGER.log("Received: " + response);
				String received;
				byte[] bufSize = new byte[4096]; // Create a buffer to read in from data socket
				int bytesRead;
				while((bytesRead = fromServer.read(bufSize)) != -1) {
					received = new String(bufSize,0,bytesRead);
					LOGGER.log("Directory Listing. Received: " + bytesRead + " bytes.");
					System.out.println(received);
				}
				fromServer.close();
				ftpServer.close();
			} else {
				if (directory.equals("")) {
					output.write("list\r\n");
					output.flush();
				} else {
					output.write("LIST " + directory + "\r\n");
					output.flush();
				}
				Socket data_trans = new Socket(this.dataHost, this.dataPort);
				BufferedInputStream fromServer = new BufferedInputStream(data_trans.getInputStream());
				response = reader.readLine();
				LOGGER.log("Received: " + response);
				String received;
				byte[] bufSize = new byte[4096]; // Create a buffer to read in from data socket
				int bytesRead;
				while((bytesRead = fromServer.read(bufSize)) != -1) {
					received = new String(bufSize,0,bytesRead);
					LOGGER.log("Directory Listing. Received: " + bytesRead + " bytes.");
					System.out.println(received);
				}
				fromServer.close();
				data_trans.close();
			}
			pasvActive = false;
			response = reader.readLine();
			LOGGER.log("Received: " + response);
		} catch (IOException e) {
			LOGGER.log(e.toString());
		}
	}

	/*
		Allows the user to enter "extended passive mode"
	*/
	public void epsv(String arg) {
		String response = null;
		try {
			if (arg.equals("")) {
				output.write("epsv\r\n");
				output.flush();
			} else { // epsv allows the ALL argument
				output.write("epsv " + arg + "\r\n");
				output.flush();
			}
			response = reader.readLine();
			LOGGER.log("Received: " + response);
			// get the port from the response of the epsv command
			this.getEPSVPort(response);
			// changes the active data transfer method to epsv
			pasvActive = false;
			portActive = false;
			eprtActive = false;
			epsvActive = true;
			return;
		} catch(IOException e) {
			LOGGER.log(e.toString());
		}
		return;
	}

	/*
		Allows the users to enter "Passive Mode"
	*/
	public void pasv() { 
		String response = null;
		try {
			output.write("pasv\r\n");
			output.flush();
			response = reader.readLine();
			LOGGER.log("Received: " + response);
			this.parseHostPort(response);
			pasvActive = true;
			portActive = false;
			eprtActive = false;
			epsvActive = false;
			return;
		} catch (IOException e) {
			// LOGGER.log(Level.SEVERE, e.toString(), e);
			LOGGER.log(e.toString());
		}
		return;

	}

	/*
		Allows the user to change the current working directory of the FTP Site
	*/
	public void cwd(String directory) {
		String response = null;
		try {
			output.write("cwd " + directory + "\r\n");
			output.flush();
			LOGGER.log("Sent: cwd " + directory);
			response = reader.readLine();
			LOGGER.log("Received: " + response);
		} catch(IOException e) {
			LOGGER.log(e.toString());
			return;
		}
		return;
	}

	/*
		Sends the "print working directory" (pwd) command to the FTP site.
	*/
	public String pwd() {
		String response = null;
		try {
			output.write("pwd\r\n");
			output.flush();
			LOGGER.log("Sent: pwd");
			response = reader.readLine();
			LOGGER.log("Received: " + response);
			Pattern p = Pattern.compile("\"([^\"]*)\"");
			Matcher m = p.matcher(response);
			while (m.find()) {
  				return m.group(1);
			}
			return null;
		} catch(IOException e) {
			LOGGER.log(e.toString());
			return null;
		}
	}

	// Not used because there are many more commands the this prints out on the FTP side than
	// are implemented. See "help" in Main.java.
	public void help() {
		String response = null;
		try {
			output.write("help\r\n");
			output.flush();
			LOGGER.log("Sent: help");
			response = reader.readLine();
			LOGGER.log("Received: " + response);
			while(reader.ready() && (response = reader.readLine()) != null) {
				LOGGER.log("Received: " + response);
			}
		} catch(IOException e) {
			LOGGER.log(e.toString());
			return;
		}
		return;
	}

	/*
		Runs the system command and returns the system information from the
		FTP site.
	*/
	public void systemInfo() {
		String response = null;
		try {
			output.write("syst\r\n");
			output.flush();
			LOGGER.log("Sent: syst");
			response = reader.readLine();
			LOGGER.log("Received: " + response);
		} catch(IOException e) {
			LOGGER.log(e.toString());
			return;
		}
		return;
	}

	// returns client socket port number
	private static int getPort(String num1, String num2) {
		int toNum1 = Integer.parseInt(num1);
		int toNum2 = Integer.parseInt(num2);
		int port = (toNum1 * 256) + toNum2;
		return port;
	}

	// Parses the host and port addresses returned by
	// PASV
	private void parseHostPort(String s) {
		String[] paren = s.split("[\\(\\)]");
		String[] numbers = paren[1].split(",");
		dataHost = numbers[0] + "." + numbers [1] +"." + numbers[2] + "." + numbers [3];
		dataPort = getPort(numbers[4], numbers[5]);
		return;
	}

	// parses the EPASV Port returned by the server.
	private void getEPSVPort(String str) {
		String[] bars = str.split("[\\|\\|]");
		dataPort = Integer.parseInt(bars[3]);
		dataHost = hostname;
		return;
	}

	/*
		Cleaner UI for Logging in to the FTP Site. Sends
		the USER and PASS commands for the user.
	*/
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

	/*
		Used to terminate connections and quit the server. Normally 
		the connection will wait until it is no longer receiving data, however since
		this program is not multi-threaded, you cannot quit until the transfer is done.
	*/
	public void quit() {
		try {
			output.write("quit\r\n");
			this.s.close();
		} catch(IOException e) {
			LOGGER.log(e.toString());
		}
	}
}
