package com.trattwalds.rixserver.commands;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.trattwalds.logger.Logger;
import com.trattwalds.rixserver.Client;

public class DownloadFile extends Command {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	FileOutputStream fs = null;
	String filename = null;
	long filesize = 0;
	long writtenSize = 0;

	@Override
	public void execute(Client client, List<String> arguments) {
		String argument = Command.reassemble(arguments);
		client.send(argument.getBytes());
	}

	@Override
	public void onReceiveData(byte[] array, int numRead) {
		try {
			baos.write(array, 0, numRead);
			String sBuffer = new String(baos.toByteArray(), 0, baos.size());

			if (filename == null && filesize == 0 && sBuffer.contains("\n\n")) {
				String[] dataSplit = sBuffer.split("\n\n");
				String header = dataSplit[0];

				// Remove header from sBuffer
				int destSize = baos.size() - (header.length() + 2);
				byte[] destArr = new byte[destSize];
				System.arraycopy(baos.toByteArray(), header.length() + 2,
						destArr, 0, destSize);
				baos.reset();
				baos.write(destArr);

				// Split the header on newline
				String[] headerSplit = header.split("\n");

				// Set filename and length from header
				filename = headerSplit[0];
				// TODO handle exceptions
				filesize = Integer.parseInt(headerSplit[1]);
			}

			if (filename != null && filesize != 0) {
				// Create file if not already created
				if (fs == null) {
					Logger.debug("Filename: " + filename);
					Logger.debug("Filesize: " + filesize);
					fs = new FileOutputStream(filename);
				}

				// Receive data and write to file
				writtenSize += baos.size();
				baos.writeTo(fs);
				baos.reset();

				// Close file if we've written everything and the file is open
				if (filesize == writtenSize && fs != null) {
					fs.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
