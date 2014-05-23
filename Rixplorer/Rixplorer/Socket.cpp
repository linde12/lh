#include "Socket.h"

Socket::Socket(){//constructor
	//start up WSA(Windows Socket Application)
	if(WSAStartup(MAKEWORD(2,2), &wsaData) != NO_ERROR){
		cout << "[Error on line " << __LINE__ << " in file " << __FILE__ "]: WSAStartup() error\n";
		WSACleanup();
		exit(1);
	}

	//Make an internet socket, that uses socket stream and the TCP protocol of the IP-Layer
	// kSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	kSocket = WSASocket(AF_INET, SOCK_STREAM, IPPROTO_TCP, 0, 0, 0);

	if(kSocket == INVALID_SOCKET){
		cout << "[Error on line " << __LINE__ << " in file " << __FILE__ "]: socket() error\n";
		WSACleanup();
		exit(2);
	}
}

Socket::~Socket(){//destructor
	WSACleanup();
}

SOCKET Socket::getSocket() {
	return this->kSocket;
}

/* Send function */
bool Socket::Send(char *buffer, int size){
	if(send(kSocket, buffer, size, 0) != SOCKET_ERROR)
		return true;
	else
		return false;
}

/* Receive function */
int Socket::Receive(std::string &buffer, int size, int flags){
	char *cBuffer = new char[size];
	memset(cBuffer, 0, size);
	unsigned const int receiveSize = recv(kSocket, cBuffer, size, flags);

	
	// Set the C++ buffer
	buffer = std::string(cBuffer, receiveSize);

	if(receiveSize != SOCKET_ERROR || WSAGetLastError() == WSAEWOULDBLOCK)// the OR is for nonblocking sockets, nonblockingsockets sets wsagetlasterror to WSAEWOULDBLOCK
		return receiveSize;
	else
		return NULL;
}

/* Connect function */
bool Socket::Connect(char *ip, int port, unsigned short type){
	hostent *ent;
	in_addr *address;
	ent = gethostbyname(ip);
	Socket::type = type;
	kAddress.sin_family = AF_INET;
	kAddress.sin_port = htons(port);

	address = (in_addr *)ent->h_addr;
	//memcpy(&kAddress.sin_addr.S_un.S_addr, &address->S_un.S_addr, sizeof(address->S_un.S_addr));
	kAddress.sin_addr.S_un.S_addr = address->S_un.S_addr;

	if(connect(kSocket, (sockaddr *)&kAddress, sizeof(kAddress)) != 0){
		cout << "[Error on line " << __LINE__ << " in file " << __FILE__ "]: connect() error\n";
		WSACleanup();
		return false;
	}else{
		if(Socket::type==NONBLOCKING){//if nonblocking socket, set it to be a nonblocking socket
			u_long iMode=1;
			ioctlsocket(kSocket,FIONBIO,&iMode);
		}
		return true;
	}
}

/* isConnected function */
bool Socket::isConnected(){//for servers
	char dummy;
	if((recv(kSocket, &dummy,1, MSG_PEEK) != 0 && recv(kSocket, &dummy,1, MSG_PEEK) != SOCKET_ERROR) || WSAGetLastError() == WSAEWOULDBLOCK){//recv() returns -1 the connection is closed(not gracefully tho), and 0 if its gracefully closed. WSAEWOUDBLOCK will be returned by WSAGetLastError if non-blocking socket.
			return true;																													 //See http://msdn.microsoft.com/en-us/library/ms740121%28VS.85%29.aspx for further reference.
	}else
		return false;
}

void Socket::setID(int sockid){
	kSocket = sockid;//Warning, you are only passing the socket here, not the struct for example so you can't
					 //access for example the data in the "original" sockets sockaddr_in from the dummysocket using this.
}

int Socket::getID(){
	return kSocket;
}

/* Close function */
void Socket::Close(){
	closesocket(kSocket);
}
