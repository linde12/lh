#pragma once
#include <iostream>
#include <Windows.h>
class Request
{
public:
	Request(void);
	~Request(void);
	bool isFinished(); // Styrd av en long counter
	bool hasHeader();
	long getContentSize(); // Length of content size defined in header
	long getCurrentSize(); // Length of internal buffer
	long getTotalSize(); // Length of internal buffer
	void pop(std::string &dst);
	void push(std::string &buffer, int length);
	std::string getCommand();
	std::string buffer;
private:
	long currentSize;
	long totalSize;
	long contentSize;
	std::string command;
	std::string header;
	bool bHasHeader;
	void setHeaderInformation();
};

