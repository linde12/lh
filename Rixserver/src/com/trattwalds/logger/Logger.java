package com.trattwalds.logger;

public class Logger {
	public static final int ERROR = 0;
	public static final int INFO = 1;
	public static final int DEBUG = 2;

	private static int level = DEBUG;

	public static void setLevel(int level) {
		Logger.level = level;
	}

	public static void debug(String string) {
		if (level >= 2)
			System.out.println("[DEBUG]: " + string);
	}

	public static void info(String string) {
		if (level >= 1)
			System.out.println("[INFO]: " + string);
	}

	public static void error(String string) {
		if (level >= 0)
			System.out.println("[ERROR]: " + string);
	}
}
