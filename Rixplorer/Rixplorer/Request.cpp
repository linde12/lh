#include "Request.h"

using namespace std;
Request::Request(void) {
	currentSize = 0;
	totalSize = 0;
	contentSize = 0;
	bHasHeader = false;

	this->buffer = string();
	this->header = string();
}


Request::~Request(void) {
}

bool Request::isFinished() {
	return (totalSize == contentSize) && (totalSize != 0 && contentSize != 0);
}

long Request::getContentSize() {
	return contentSize;
}

long Request::getCurrentSize() {
	return currentSize;
}

void Request::pop(string &dst) {
	dst = string(this->buffer);
	this->buffer = string();
	currentSize = 0;
}

void Request::push(string &buffer, int length) { // Has memleak inside somewhere
		string oldBuffer = this->buffer;
		string newBuffer = buffer;

		// Merge oldBuffer with newBuffer
		this->buffer = oldBuffer;
		this->buffer += newBuffer;

		// Increment current buffer size
		currentSize += length;

		// If we have a header, increase totalSize
		totalSize += length;

		int buflen = this->buffer.length();

		// Check if we have the header or not
		if (bHasHeader == false) {
			int dblNewlinePos = this->buffer.find("\n\n");
			if (dblNewlinePos != string::npos) { // See if header was found
				bHasHeader = true;

				// Header plus the two new line characters
				int headerSize = dblNewlinePos + 2;
				// Copy the header(including the newline characters) to this->header
				this->header = this->buffer.substr(0, headerSize);

				// Remove the header from the buffer
				this->buffer = this->buffer.substr(headerSize, 
													this->buffer.size() -
													headerSize);

				// Decrement totalSize by headerSize
				totalSize = currentSize - headerSize;
				currentSize = totalSize;

				setHeaderInformation();
			}
		}
}

std::string Request::getCommand() {
	return command;
}

bool Request::hasHeader() {
	return bHasHeader;
}

void Request::setHeaderInformation() {
	int headerLength = header.size();
	int firstNewlinePos = this->header.find("\n");
	this->command = this->header.substr(0, firstNewlinePos);
	this->contentSize = atol(this->header.substr(firstNewlinePos+1, 
											this->header.find("\n", firstNewlinePos+1)).data());

}