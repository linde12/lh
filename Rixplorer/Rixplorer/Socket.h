/*
#
#	Purpose: Simple TCP client
#	Author: Oscar Linde
#	Version: 1.1
#	Release: 2012-04-04
#
*/
#define NONBLOCKING 1
#define BLOCKING 0
#pragma once
#pragma comment(lib, "Ws2_32.lib")
#include <iostream>
#include <WinSock2.h>
using namespace std;

class Socket
{
	WSADATA wsaData;
	SOCKET kSocket;
	sockaddr_in kAddress;
	unsigned short type;
public:
		/* Initializes the socket, starts WSA */
	Socket();
	~Socket();
		/* Returns kSocket */
	SOCKET getSocket();
		/* Sends data through TCP stream arg1 with length of arg2, returns true on success otherwise false */
	bool Send(char *, int);
		/* Recieves data from TCP stream to arg1 with the max-length of arg2 and mode of arg3. Returns bytes received. */
	int Receive(std::string &, int, int);
		/* Makes a TCP connection to a server socket with ip/hostname arg1, port arg2 and mode arg3(blocking or nonblocking socket) */
	bool Connect(char *, int, unsigned short);
		/* Returns true if there is a connection to a valid TCP stream, otherwise returns false */
	bool isConnected();
		/* Sets socket ID to arg1 */
	void setID(int);
		/* Returns socket ID from socket */
	int getID();
		/* Closes the TCP stream and calls WSACleanup() */
	void Close();
};
