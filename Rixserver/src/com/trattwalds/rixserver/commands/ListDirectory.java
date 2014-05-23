package com.trattwalds.rixserver.commands;

import java.util.List;

import com.trattwalds.rixserver.Client;
import com.trattwalds.rixserver.CommandLine;

public class ListDirectory extends Command {

	@Override
	public void execute(Client client, List<String> arguments) {
		client.send("".getBytes());
	}

	@Override
	public void onReceiveData(byte[] array, int numRead) {
		CommandLine.putln(new String(array));
	}
}
