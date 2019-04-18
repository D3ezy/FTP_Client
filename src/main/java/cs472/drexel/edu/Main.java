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
		showMenu();
	}

	public static void showMenu() {

		Scanner input = new Scanner(System.in);
		boolean isRunning = true;

		while (isRunning) {
			printMenu();
			switch(input.nextLine()) {
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
				case "QUIT":
					isRunning = false;
					break;
				default:
			}
		}

		return;
	}

	public static void printMenu() {
		ArrayList<String> cmds = new ArrayList<>(Arrays.asList("USER","PASS","CWD","QUIT","PASV","EPSV","PORT",
																"EPRT", "RETR","STOR","PWD", "SYST", "LIST", "HELP"));
		return;
	}

}
