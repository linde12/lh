#include "Filesystem.h"
#include <iostream>
#include <Windows.h>

using namespace std;
Filesystem::Filesystem(void)
{
	directory = "C:\\Users\\L\\*";
}


Filesystem::~Filesystem(void)
{
}


void cd(std::string path) {

}
void Filesystem::ls(std:: string &buffer) {
	HANDLE hFind;
	WIN32_FIND_DATA foundData;

	hFind = FindFirstFile(this->directory.c_str(), &foundData);

	if (hFind == INVALID_HANDLE_VALUE) {
		cout << "Invalid handle" << endl;
		return;
	}

	while (FindNextFile(hFind, &foundData) != 0) {
		buffer += foundData.cFileName;
		buffer += "\n";
	}
}
void cp(std::string src, std::string dst) {

}
void mv(std::string src, std::string dst) {

}
void rm(std::string src) {

}