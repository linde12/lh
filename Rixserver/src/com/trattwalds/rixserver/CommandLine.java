package com.trattwalds.rixserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.trattwalds.rixserver.io.Reader;

public abstract class CommandLine implements Runnable {
	public static final String RESPONSE_SIGN = "<<";
	public static final String INPUT_SIGN = ">>";
	public static String TARGET = "";
	private static boolean bRawData;
	private static Reader reader;
	private Thread thread;

	public CommandLine() {
		try {
			if (reader == null) {
				reader = new Reader(System.in);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// Read from STDIN
		try {
			String line = null;
			putInput();
			List<String> arguments;
			while ((line = reader.readLine()) != null) {
				if (bRawData) {
					onRawData(line);
					continue;
				}

				arguments = new ArrayList<String>();
				// For now just split at space, single-word arguments
				String[] split = line.split(" ");

				for (int i = 0; i < split.length; i++) {
					if (i != 0) {
						arguments.add(split[i]);
					}
				}

				if (split.length > 0) {
					// split[0] contains the command string
					String returnValue = onReceiveCommand(split[0], arguments);

					if (returnValue != null) {
						put(returnValue);
					}
				}

				putInput();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void onRawData(String line);

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public static void put(String string) {
		System.out.print(string);
	}

	public static void putln(String string) {
		System.out.println(System.lineSeparator() + string);
		putInput();
	}

	public static void putInput() {
		System.out.print(TARGET + INPUT_SIGN);
	}

	public abstract String onReceiveCommand(String command,
			List<String> arguments);

	public static void setRawMode(boolean value) {
		bRawData = value;
	}
}
