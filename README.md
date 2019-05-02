# cs472-ftpClient
HW2 - FTP Client for CS 472 (Computer Networks) Drexel
Author: Matthew Dey
Date: 05-01-2019

TO RUN:

This build uses Gradle for dependency compiling. The Gradle build has a task to create a "fatJar" that can be run with the given parameters below:

java -jar .\build\libs\CS472-FTPClient-all-0.1.jar <ip addr> <log file>

Example: 
java -jar .\build\libs\CS472-FTPClient-all-0.1.jar 10.0.0.80 C:\Users\matth\Desktop\client.log


Answers to HW Questions:

1. Think about the conversation of FTP – how does each side validate the other (on the
connection and data ports – think of each separately)? How do they trust that they’re getting
a connection from the right person?

The two don't actually validate eachother in this instance. One can connect to the FTP server without even logging in. It is when they want to perform certain commands that a login is required. This is the form of handshake that is required. Another important feature that FTP has the PORT command, which asks the FTP site to reach out to the client machine on an active listening port. This could be considered a way to authenticate that the client is who they say they are. 

2. How does your client know that it’s sending the right commands in the right order? How
does it know the sender is trustworthy? 

My client knows that its sending the right commands in the right order for two different reasons. The first reason is because of the protocol's RFC. RFC 959/2428 both give
a writeup on FTP as well as the commands. This allowed me to know what ports to open on which sockets and how to setup the data connection. I knew what commands needed to follow
others after reading the RFC and was able to implement them in Java. The other reason is because of the "ACK"'s that were sent from the server after a connection was established. The acknowledgments allowed the client to know which status codes it was given and how to handle them when it was received from the server. The FTP client does not know that the sender is trustworthy aside from authentication.Any connecting client that can login (which you can see the username and password by sniffing the connection between client and server) has access to the FTP commands and is available to use them. It is up to the security of the server, as we are just sending data over the FTP protocol unencrypted. 

Main.java

The Main.java program implements the following FTP functions on the Menu:

USER   HELP    SYST
PASS    PORT    CWD
RETR    EPRT    PWD
STOR    PASV    LIST
QUIT    EPSV

The main method instantiates a new connection and a scanner object to take input from the command line and act on the client based on the command given. Selecting "HELP" will print out a menu of available commands that you can use. Both objects use the java.util.Logger library that outputs all commands sent and received to the indicated log file as an
input parameter.

Client.java

The default mode for my FTP client is passive mode, because I had trouble getting PORT to work through my Windows firewall. The client object has a few input stream attirbutes as well as socket connections. The mode is based off of boolean attributes. When one mode is enabled it is set to "true" and the others are set to "false" each function of the protocol will check to see if any mode has been enabled. If it has not, it will default to PASV. 

References: 
RFC 959 - https://tools.ietf.org/html/rfc959
RFC 2428 - https://tools.ietf.org/html/rfc2428
