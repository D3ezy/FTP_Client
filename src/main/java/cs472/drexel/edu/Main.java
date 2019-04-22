/*
 * Main.java
 * 
 * Author: Matthew Dey
 * Date Created: April 15th, 2019
 * Drexel University
 * CS 472 - HW2 - Computer Networks
 */

package cs472.drexel.edu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.*;

public class Main {

	private static final Logger LOGGER = Logger.getLogger( Main.class.getName() );
	public static void main(String[] args) {

		// arg check
		if (args.length != 1) {
			LOGGER.log(Level.SEVERE, "Usage: java Client <ip addr>");
			System.exit(1);
		} 

		Client c = new Client(args[0]);
		// c.doProtocol();
		showMenu(c);
	}

	public static void showMenu(Client c) {

		Scanner input = new Scanner(System.in);
		boolean isRunning = true;

		while (isRunning) {	
			switch(input.nextLine().toUpperCase()) {
				case "USER":
					c.doProtocol("user\r\n");
					break;
				/* The argument field is a Telnet string identifying the user.
				The user identification is that which is required by the
				server for access to its file system.  This command will
				normally be the first command transmitted by the user after
				the control connections are made (some servers may require
				this).  Additional identification information in the form of
				a password and/or an account command may also be required by
				some servers.  Servers may allow a new USER command to be
				entered at any point in order to change the access control
				and/or accounting information.  This has the effect of
				flushing any user, password, and account information already
				supplied and beginning the login sequence again.  All
				transfer parameters are unchanged and any file transfer in
				progress is completed under the old access control
				parameters. */
				case "PASS":
				/* The argument field is a Telnet string specifying the user's
				password.  This command must be immediately preceded by the
				user name command, and, for some sites, completes the user's
				identification for access control.  Since password
				information is quite sensitive, it is desirable in general
				to "mask" it or suppress typeout.  It appears that the
				server has no foolproof way to achieve this.  It is
				therefore the responsibility of the user-FTP process to hide
				the sensitive password information. */
				case "CWD":
				/* This command allows the user to work with a different
				directory or dataset for file storage or retrieval without
				altering his login or accounting information.  Transfer
				parameters are similarly unchanged.  The argument is a
				pathname specifying a directory or other system dependent
				file group designator. */
				case "PASV":
				/* This command requests the server-DTP to "listen" on a data
				port (which is not its default data port) and to wait for a
				connection rather than initiate one upon receipt of a
				transfer command.  The response to this command includes the
				host and port address this server is listening on. */
				case "EPSV":
				case "PORT":
				/* The argument is a HOST-PORT specification for the data port
				to be used in data connection.  There are defaults for both
				the user and server data ports, and under normal
				circumstances this command and its reply are not needed.  If
				this command is used, the argument is the concatenation of a
				32-bit internet host address and a 16-bit TCP port address.
				This address information is broken into 8-bit fields and the
				value of each field is transmitted as a decimal number (in
				character string representation).  The fields are separated
				by commas.  A port command would be:
	
				   PORT h1,h2,h3,h4,p1,p2
	
				where h1 is the high order 8 bits of the internet host
				address. */
				case "EPRT":
				case "RETR":
				/* This command causes the server-DTP to transfer a copy of the
				file, specified in the pathname, to the server- or user-DTP
				at the other end of the data connection.  The status and
				contents of the file at the server site shall be unaffected. */
				case "STOR":
				/* This command causes the server-DTP to accept the data
				transferred via the data connection and to store the data as
				a file at the server site.  If the file specified in the
				pathname exists at the server site, then its contents shall
				be replaced by the data being transferred.  A new file is
				created at the server site if the file specified in the
				pathname does not already exist. */
				case "PWD":
					c.doProtocol("pwd\r\n");
					break;
				/* This command causes the name of the current working
				directory to be returned in the reply.  See Appendix II. */
				case "SYST":
				/* This command is used to find out the type of operating
            	system at the server.  The reply shall have as its first
            	word one of the system names listed in the current version
            	of the Assigned Numbers document [4]. */
				case "LIST":
				/* This command causes a list to be sent from the server to the
				passive DTP.  If the pathname specifies a directory or other
				group of files, the server should transfer a list of files
				in the specified directory.  If the pathname specifies a
				file then the server should send current information on the
				file.  A null argument implies the user's current working or
				default directory.  The data transfer is over the data
				connection in type ASCII or type EBCDIC.  (The user must
				ensure that the TYPE is appropriately ASCII or EBCDIC).
				Since the information on a file may vary widely from system
				to system, this information may be hard to use automatically
				in a program, but may be quite useful to a human user. */
				case "HELP":
				/* This command shall cause the server to send helpful
				information regarding its implementation status over the
				control connection to the user.  The command may take an
				argument (e.g., any command name) and return more specific
				information as a response.  The reply is type 211 or 214.
				It is suggested that HELP be allowed before entering a USER
				command. The server may use this reply to specify
				site-dependent parameters, e.g., in response to HELP SITE. */
					printMenu();
					break;
				case "QUIT":
					/* This command terminates a USER and if file transfer is not
					in progress, the server closes the control connection.  If
					file transfer is in progress, the connection will remain
					open for result response and the server will then close it.
					If the user-process is transferring files for several USERs
					but does not wish to close and then reopen connections for
					each, then the REIN command should be used instead of QUIT.
					An unexpected close on the control connection will cause the
					server to take the effective action of an abort (ABOR) and a
					logout (QUIT). */
					isRunning = false;
					break;
				default:
					System.out.println("CMD_ERROR: Input not recognized, please try again. For usage type HELP");
					System.out.println();
			}
		}

		return;
	}

	public static void printMenu() {
		ArrayList<String> cmd_col1 = new ArrayList<>(Arrays.asList("USER","PASS","CWD"));
		ArrayList<String> cmd_col2 = new ArrayList<>(Arrays.asList("LIST","PASV","EPSV"));
		ArrayList<String> cmd_col3 = new ArrayList<>(Arrays.asList("PORT","EPRT", "RETR"));
		ArrayList<String> cmd_col4 = new ArrayList<>(Arrays.asList("STOR","PWD", "SYST"));
		ArrayList<String> cmd_col5 = new ArrayList<>(Arrays.asList("QUIT", "HELP",""));

		for (int i = 0; i < cmd_col1.size(); i++) {
			System.out.printf("%-5s%-5s%-5s%-5s%-5s", cmd_col1.get(i), cmd_col2.get(i), cmd_col3.get(i), cmd_col4.get(i), cmd_col5.get(i));
			System.out.println();
		}

		System.out.println();
		return;
	}

}
