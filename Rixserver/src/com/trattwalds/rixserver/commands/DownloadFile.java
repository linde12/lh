package com.trattwalds.rixserver.commands;

import java.nio.ByteBuffer;
import java.util.List;

import com.trattwalds.rixserver.Client;

public class DownloadFile extends Command {
	ByteBuffer buffer;
	long fileLength = 0;
	@Override
	public void execute(Client client, List<String> arguments) {
		String argument = Command.reassemble(arguments);
		client.send(argument.getBytes());
	}

	@Override
	public void onReceiveData(byte[] array, int numRead) {
		buffer.put(array);
		String dataStr = new String(buffer.array());
		if (fileLength == 0 && dataStr.contains("\n")) {
			dataStr = dataStr.split("\n")[0];
			byte[] dst = new byte[buffer.array().length - (dataStr.length() + 1)];
			buffer.get(dst, dataStr.length() + 1, buffer.array().length - (dataStr.length() + 1));
			buffer.clear();
			buffer.put(dst);
			fileLength = new Integer(dataStr);
		} else {
			
		}
		System.out.print(new String(array));
	}

}
