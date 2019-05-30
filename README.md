# cs472-ftpClient
HW2 - FTP Client for CS 472 (Computer Networks) Drexel
Author: Matthew Dey
Date: 05-01-2019

TO RUN:

This build uses Gradle for dependency compiling. The Gradle build has a task to create a "fatJar" that can be run with the given parameters below:

java -jar .\build\libs\CS472-FTPClient-all-0.1.jar <ip addr> <log file>

Example: 
java -jar .\build\libs\CS472-FTPClient-all-0.1.jar 10.0.0.80 C:\Users\matth\Desktop\client.log

A sample client.log file is included in the submission directory.

Answers to HW Questions:

1. Think about the conversation of FTP – how does each side validate the other (on the
connection and data ports – think of each separately)? How do they trust that they’re getting
a connection from the right person?

The two don't actually validate eachother in this instance. One can connect to the FTP server without even logging in. It is when they want to perform certain commands that a login is required. The is a form of application authentication. FTP works under the Presentation Layer of the OSI Model or the Application Layer under the TCP/IP Reference model. So this would mean that the Transport layer of TCP handshaking still applies to FTP. FTP uses a connection oriented handshake over TCP. This is the form of handshake that is required. Another important feature that FTP has the PORT command, which asks the FTP site to reach out to the client machine on an active listening port. This could be considered a way to authenticate that the client is who they say they are. FTP does not check the boundary conditions by which is connects to the client, and it is a unique 3-way protocol. There is an assumed trust factor to those who are implementing and using the protocol. FTP also uses Implicit SSL and has versions of Secure-FTP (SFTP/FTPS). But these are different renditions of the protocol. As the generic FTP sits, on the Application layer you can set some security mesaures, but other than TCP communication handshakes, there isn't much of a security. The protocol has some level of trust in which ports are selected and which the client connects to. 

2. How does your client know that it’s sending the right commands in the right order? How
does it know the sender is trustworthy? 

My client knows that its sending the right commands in the right order for two different reasons. The first reason is because of the protocol's RFC. RFC 959/2428 both give
a writeup on FTP as well as the commands. This allowed me to know what ports to open on which sockets and how to setup the data connection. I knew what commands needed to follow
others after reading the RFC and was able to implement them in Java. The other reason is because of the "ACK"'s that were sent from the server after a connection was established. The acknowledgments allowed the client to know which status codes it was given and how to handle them when it was received from the server. The FTP client does not know that the sender is trustworthy aside from authentication.Any connecting client that can login (which you can see the username and password by sniffing the connection between client and server) has access to the FTP commands and is available to use them. It is up to the security of the server, as we are just sending data over the FTP protocol unencrypted. The ACKs with responding status codes are really what tell the client how to respond. Since this protocol is deterministic of the state that the server is in. The client knows the state based on the return code of the ACK. 

Main.java

The Main.java program implements the following FTP functions on the Menu:

USER   HELP    SYST
PASS    PORT    CWD
RETR    EPRT    PWD
STOR    PASV    LIST
QUIT    EPSV

The main method instantiates a new connection and a scanner object to take input from the command line and act on the client based on the command given. Selecting "HELP" will print out a menu of available commands that you can use. Both objects use a Logger object that outputs all commands sent and received to the indicated log file as an input parameter.

Client.java

The default mode for my FTP client is passive mode, because I had trouble getting PORT to work through my Windows firewall. The client object has a few input stream attirbutes as well as socket connections. The mode is based off of boolean attributes. When one mode is enabled it is set to "true" and the others are set to "false" each function of the protocol will check to see if any mode has been enabled. If it has not, it will default to PASV. 

References: 
RFC 959 - https://tools.ietf.org/html/rfc959
RFC 2428 - https://tools.ietf.org/html/rfc2428

Answers to HW4 Questions:

1. What are the security considerations with port_mode? With pasv_mode? Why would I
want one or the other (think about some of the problems that you had with the client and
the server – and who calls who)? Think of the conversation between client and server. 

Logging

2. Why is logging an important part of security?
3. Do you see any problems with concurrent servers and log files? (dealing with multiple
processes or threads writing to the log file at the same time)? Brainstorm how to solve
this problem. 

Securing the connection with SSL/TLS 

4. What are the different issues with securing the connections with IMPLICIT mode (which
is a separate server listening with TLS always at port 990 by default) and EXPLICIT
mode (which is the same server waiting on port 21 for special commands to turn TLS on
and off)? What are the “it depends” part of the tradeoffs?

Analyzing the conversation

5. Why is the 3 person method of FTP (as originally defined in the RFC) really insecure?
Think of what you could do to cause trouble with the approach and what you can do in
your clients and servers to stop that from happening. Do you have to do any checking in
your program(s) with PORT/PASV to make sure that it isn’t happening (that YOU ARE
talking to the same host)? Think about the data channel as well as the control channel.

EXTRA CREDIT (worth up to 10 points): Think of the conversation of FTP and compare it to
other file transfer protocols

 SFTP – offers the service on port 22 and data and commands share the same channel
– better or worse?
 BitTorrent – offers files from a large number of hosts.

What are the good points and bad points of each approach (FTP, SFTP, BitTorrent)?

Analyzing the Operation of the Server

6. Do you think there are events that you’ve logged which show that someone is trying to
break into your server? Do you have to add other log entries and checking to make sure
that attacks aren’t happening? Brainstorm some attacks against your FTP server. For at
least one of them, implement additional code to check for them and log appropriate
entries. 
