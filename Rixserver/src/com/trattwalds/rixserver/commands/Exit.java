package com.trattwalds.rixserver.commands;

import java.util.List;

import com.trattwalds.rixserver.Client;

public class Exit extends Command {

	@Override
	public void execute(Client client, List<String> arguments) {
		System.exit(0);
	}

	@Override
	public void onReceiveData(byte[] array, int numRead) {

	}

}
