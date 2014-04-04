package com.ayros.historycleaner.helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ayros.historycleaner.Globals;

public class Logger
{
	private static boolean debugMode = false;
	private static boolean hasCheckedForDebug = false;

	private static boolean logToFile = false;
	private static boolean hasCheckedForLogToFile = false;

	public static void error(String message)
	{
		System.err.println("ERROR: " + message);
		if (isLogToFileMode())
		{
			logToFile("ERROR: " + message);
		}
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
		if (isLogToFileMode())
		{
			logToFile("Inner Exception: " + e.toString());
		}

		System.err.print("Stack Trace: ");
		e.printStackTrace();
	}

	public static void debug(String message)
	{
		if (isDebugMode())
		{
			System.out.println("DEBUG: " + message);
			if (isLogToFileMode())
			{
				logToFile("DEBUG: " + message);
			}
		}
	}

	public static boolean isDebugMode()
	{
		if (debugMode || hasCheckedForDebug)
		{
			return debugMode;
		}
		else
		{
			String filesDir = Globals.getContext().getApplicationContext().getFilesDir().getAbsolutePath();

			File checkFile = new File(filesDir + "/debugmode.txt");

			hasCheckedForDebug = true;
			debugMode = checkFile.exists();

			System.out.println("Is Debug Mode: " + debugMode);

			return debugMode;
		}
	}

	public static boolean isLogToFileMode()
	{
		if (logToFile || hasCheckedForLogToFile)
		{
			return logToFile;
		}
		else
		{
			String filesDir = Globals.getContext().getApplicationContext().getFilesDir().getAbsolutePath();

			File checkFile = new File(filesDir + "/logtofile.txt");

			hasCheckedForLogToFile = true;
			logToFile = checkFile.exists();

			System.out.println("Is Log-To-File Mode: " + logToFile);

			return logToFile;
		}
	}

	private static void logToFile(String msg)
	{
		String filesDir = Globals.getContext().getApplicationContext().getFilesDir().getAbsolutePath();

		FileWriter logWriter = null;
		try
		{
			logWriter = new FileWriter(filesDir + "/log.txt", true);
			logWriter.write(msg);
			logWriter.write('\n');
			logWriter.flush();
		}
		catch (IOException e)
		{
		}
		finally
		{
			try
			{
				if (logWriter != null)
				{
					logWriter.close();
				}
			}
			catch (IOException e)
			{
			}
		}
	}

	public static void printStack()
	{
		Thread.dumpStack();
	}
}
