package com.trattwalds.rixserver.commands;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trattwalds.rixserver.Client;
import com.trattwalds.rixserver.Server;

public abstract class Command {
	private static Map<String, Class<? extends Command>> classes = new HashMap<String, Class<? extends Command>>();

	public static void execute(String commandString, List<String> arguments) {
		Class<? extends Command> c = classes.get(commandString);
		// If no class was found
		if (c == null) {
			// Logger.debug("No class found mapped as '" + commandString
			// + "', ignoring");
			return;
		}
		Constructor<?> ctor;
		try {
			ctor = c.getConstructor();
			Command command = (Command) ctor.newInstance();
			Client selectedClient = Server.getInstance().getSelectedClient();

			// If we have a selected client, then set command of that client to
			// the called command
			if (selectedClient != null) {
				selectedClient.setCommand(command);
			}

			command.execute(selectedClient, arguments);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract void execute(Client client, List<String> arguments);

	public abstract void onReceiveData(byte[] array, int numRead);

	public static void add(String command, Class<? extends Command> clazz) {
		classes.put(command, clazz);
	}

	public String getId() {
		String classpath = this.getClass().getName();
		String[] classpathSplit = classpath.split("\\.");
		String className = classpathSplit[classpathSplit.length - 1]
				.toLowerCase();
		return className;
	}

	public static String reassemble(List<String> arguments) {
		String argString = "";
		for (String argument : arguments) {
			argString += argument + " ";
		}
		argString = argString.substring(0, argString.length()-1);
		
		return argString;
	}
}
