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

public class Main {

	public static void main(String[] args) {
		Client c = new Client();
		showMenu(c);
	}

	public static void showMenu(Client c) {

		Scanner input = new Scanner(System.in);
		boolean isRunning = true;

		while (isRunning) {
			printMenu();
			switch(input.nextLine().toUpperCase()) {
				case "USER":
				case "PASS":
				case "CWD":
				case "PASV":
				case "EPSV":
				case "PORT":
				case "EPRT":
				case "RETR":
				case "STOR":
				case "PWD":
				case "SYST":
				case "LIST":
				case "HELP":
					// Displays HELP menu
				case "QUIT":
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
