#include <iostream>

using namespace std;
void main() {
    // Coded outside IDE, dummy code for later use in project
    
    string file = "C:\\Users\\L\\Desktop\\test.txt";
    string buffer, header;
    FILE *pFile = fopen(file.c_str(), "rb");
    // obtain file size:
    fseek (pFile , 0 , SEEK_END);
    long lFileSize = ftell (pFile);
    rewind (pFile);

    // Write header
    header = lFileSize + "\n";
    socket->Send((char *)header.c_str(), strlen(header.c_str()));

    int len;
    if (file != NULL) {
        while ((len = fread((char *)buffer.c_str(), 1, BUFFER_SIZE, pFile)) > 0) {
            socket->Send(buffer.c_str(), BUFFER_SIZE);
        }
    }
}