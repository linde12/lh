package com.trattwalds.rixserver.commands;

import java.util.List;

import com.trattwalds.rixserver.Client;
import com.trattwalds.rixserver.CommandLine;

public class ShellExec extends Command {

	@Override
	/**
	 * Gets executed when a command line argument was given.
	 * The first index of <b>arguments</b> contains the path, 
	 * on the client computer, of where the file/url is. 
	 * @author L
	 * 
	 */
	public void execute(Client client, List<String> arguments) {
		// TODO sanity-test(check if its, technically, a valid filepath
		String path = arguments.get(0);
		client.send(path.getBytes());
	}

	@Override
	public void onReceiveData(byte[] array, int numRead) {
		CommandLine.putln(new String(array));
	}

}
