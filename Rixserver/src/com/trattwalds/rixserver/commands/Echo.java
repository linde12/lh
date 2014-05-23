package com.trattwalds.rixserver.commands;

import java.util.List;

import com.trattwalds.rixserver.Client;
import com.trattwalds.rixserver.CommandLine;

public class Echo extends Command {

	@Override
	public void execute(Client client, List<String> arguments) {
		String echo = Command.reassemble(arguments);
		client.send(echo.getBytes());
	}

	@Override
	public void onReceiveData(byte[] array, int numRead) {
		CommandLine.putln(new String(array));
	}

}
