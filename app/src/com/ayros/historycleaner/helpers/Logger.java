package com.ayros.historycleaner.helpers;

public class Logger
{
	private static final boolean DEBUG_MODE = false;

	public static void error(String message)
	{
		System.err.println("ERROR: " + message);
	}

	public static void errorST(String message)
	{
		error(message);
		printStack();
	}

	public static void errorST(String message, Exception e)
	{
		error(message);
		System.err.println("Inner Exception: " + e.toString());

		System.err.print("Stack Trace: ");
		e.printStackTrace();
	}

	public static void debug(String message)
	{
		if (DEBUG_MODE)
		{
			System.out.println("DEBUG: " + message);
		}
	}

	public static void printStack()
	{
		Thread.dumpStack();
	}
}
