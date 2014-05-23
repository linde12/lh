#pragma once
#include <iostream>
class Filesystem
{
public:
	Filesystem(void);
	~Filesystem(void);
	void cd(std::string path);
	void ls(std:: string &buffer);
	void cp(std::string src, std::string dst);
	void mv(std::string src, std::string dst);
	void rm(std::string src);
private:
	std::string directory;
};

