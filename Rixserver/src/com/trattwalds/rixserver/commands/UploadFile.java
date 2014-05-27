package com.trattwalds.rixserver.commands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.trattwalds.rixserver.Client;

public class UploadFile extends Command {

	private static final int BUFFER_SIZE = 1024;

	@Override
	public void execute(Client client, List<String> arguments) {
		// TODO: Length checking
		try {
			String filepath = Command.reassemble(arguments);
			File file = new File(filepath);

			// Retrieve the filename
			String[] filenameSplit = filepath.split("\\" + File.separator);
			String filenameArg = filenameSplit[filenameSplit.length - 1] + ";";

			BufferedInputStream inStream = new BufferedInputStream(
					new FileInputStream(file));
			byte[] buffer = new byte[BUFFER_SIZE];
			int len;
			
			// Send the file header and the filename with ; as separator to the client
			client.sendHeader(filenameArg.length() + file.length());
			client.sendRaw(filenameArg.getBytes());
			while ((len = inStream.read(buffer)) != -1) {
				client.sendRaw(buffer, len);
				buffer = new byte[BUFFER_SIZE];
			}
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceiveData(byte[] array, int numRead) {

	}

}
