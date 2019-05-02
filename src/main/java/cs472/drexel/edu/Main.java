/*
 * Main.java
 * 
 * Author: Matthew Dey
 * Date Created: April 15th, 2019
 * Drexel University
 * CS 472 - HW2 - Computer Networks
 */

 /*

	Local IP: 10.0.0.80


	IP ADDR: 10.246.250.233
	USER: cs472
	PASS: pass

	Test file: C:\Users\matth\Desktop\dey_test.txt

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
		if (args.length != 2) {
			LOGGER.log(Level.SEVERE, "Usage: java -jar .\\build\\libs\\CS472-FTPClient-all-x.x.jar <ip addr> <log file>");
			System.exit(1);
		} 

		Client c = new Client(args[0], args[1]);
		showMenu(c);
	}

	public static void showMenu(Client c) {

		Scanner input = new Scanner(System.in);
		boolean isRunning = true;
		String cmd = null;

		while (isRunning) {	
			cmd = input.nextLine();			// get input from cmd 
			String[] args = cmd.split(" "); // parse command by whitespace (for now).
			switch(args[0].toUpperCase()) {
				case "USER":
					try {
						c.user(args[1]);
					} catch(ArrayIndexOutOfBoundsException e) {
						LOGGER.log(Level.WARNING,"USER cmd: No username supplied",e);
					}
					break;
				case "PASS":
					try {
						c.pass(args[1]);
					} catch(ArrayIndexOutOfBoundsException e) {
						LOGGER.log(Level.WARNING,"PASS cmd: Invalid password.",e);
					}
					break;
				case "CWD":
					try {
						c.cwd(args[1]);
					} catch (ArrayIndexOutOfBoundsException e) {
						LOGGER.log(Level.WARNING,"CWD cmd: Not a valid directory.", e);
					}
					break;
				case "PASV": // passive FTP
					try {
						c.pasv();
					} catch (Exception e) { // placeholder
						e.printStackTrace();
					}
					break;
				case "EPSV":
					try {
						if (args.length < 2) {
							c.epsv("");
						} else if (args.length == 2) {
							c.epsv(args[1]);
						} else {
							LOGGER.log(Level.WARNING, "EPSV cmd: Invalid EPSV cmd. Usage: EPSV<space><net-prt> OR EPSV<space>ALL");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case "PORT": // active FTP
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
					try {
						if (args.length == 2) {
							c.port(args[1]);
						} else {
							LOGGER.log(Level.WARNING, "PORT cmd: Invalid PORT cmd, please specify correct TCP address following the PORT command.");
						}
					} catch (IllegalArgumentException e) {
						LOGGER.log(Level.SEVERE, e.toString(), e);
					}
					break;
				case "EPRT":
					try {
						if (args.length == 2) {
							c.eprt(args[1]);
						} else {
							LOGGER.log(Level.WARNING, "EPRT cmd: Invalid EPRT cmd. Usage: EPRT<space><d><net-prt><d><net-addr><d><tcp-port><d>");
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					break;
				case "RETR":
					try {
						c.retr(args[1]);
					} catch (IllegalArgumentException e) {
						LOGGER.log(Level.WARNING,"RETR cmd: Not a valid file argument.", e);
					}
					break;
				case "STOR":
					try {
						c.stor(args[1]);
					} catch(IllegalArgumentException e) {
						LOGGER.log(Level.WARNING,"STOR cmd: Not a valid file argument.", e);
					} catch(ArrayIndexOutOfBoundsException z) {
						LOGGER.log(Level.WARNING, "STOR cmd: no file provided.");
					}
					break;
				case "PWD":
					c.pwd();
					break;
				case "SYST":
					c.systemInfo();
					break;
				case "LIST":
					try {
						if (args.length < 2) {
							c.list("");
						} else {
							c.list(args[1]);
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						LOGGER.log(Level.WARNING, "LIST cmd: Invalid Diirectory: Unable to List Files.", e);
					}
					break;
				case "HELP":
					// c.doProtocol("help\r\n");
					printMenu();
					break;
				case "QUIT":
					c.quit();
					LOGGER.info("Goodbye.");
					input.close();
					isRunning = false;
					break;
				default:
					System.out.println("CMD_ERROR: Input not recognized, please try again. For usage type HELP");
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
