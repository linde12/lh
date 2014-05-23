package com.trattwalds.rixserver.commands;

import java.util.List;

import com.trattwalds.rixserver.Client;

public class Kill extends Command {

	@Override
	public void execute(Client client, List<String> arguments) {
		client.close();
	}

	@Override
	public void onReceiveData(byte[] array, int numRead) {
	}

}
