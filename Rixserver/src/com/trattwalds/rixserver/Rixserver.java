package com.trattwalds.rixserver;

import java.util.List;

import com.trattwalds.logger.Logger;
import com.trattwalds.rixserver.commands.Command;
import com.trattwalds.rixserver.commands.Commands;

public class Rixserver {
	public static void main(String[] args) {
		new Rixserver();
	}

	/*
	 * Initializes the console listener and starts the server listener
	 */
	public Rixserver() {
		Commands.load();
		Logger.setLevel(Logger.ERROR);
		startCommandListener();

		new Server(37810) {
			@Override
			public void onConnect(String ip, String name) {
				CommandLine.putln(name + " (" + ip + ") connected.");
			}

		};

	}

	public static void startCommandListener() {
		CommandLine console = new CommandLine() {

			@Override
			public String onReceiveCommand(String command,
					List<String> arguments) {
				Command.execute(command, arguments);
				return null;
			}

			@Override
			public void onRawData(String line) {
				Client client = Server.getInstance().getSelectedClient();
				client.sendRaw((line + "\r\n").getBytes());
				if (line.equals("exit")) {
					CommandLine.setRawMode(false);
					CommandLine.putInput();
				}
			}
		};
		console.start();
	}
}
