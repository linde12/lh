package com.trattwalds.rixserver.commands;

import java.util.List;

import com.trattwalds.rixserver.Client;
import com.trattwalds.rixserver.CommandLine;
import com.trattwalds.rixserver.Server;

public class ListClients extends Command {

	@Override
	public void execute(Client client, List<String> arguments) {
		Server server = Server.getInstance();
		String clientString = CommandLine.RESPONSE_SIGN
				+ "Index\t\tName\t\tIP Address" + System.lineSeparator();
		List<Client> clients = server.getClients();
		for (int i = 0; i < clients.size(); i++) {
			Client current = clients.get(i);
			clientString += CommandLine.RESPONSE_SIGN + i + "\t\t"
					+ current.getName() + "\t\t" + current.getIP()
					+ System.lineSeparator();
		}

		CommandLine.put(clientString);
	}

	@Override
	public void onReceiveData(byte[] array, int numRead) {

	}

}
