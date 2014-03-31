package com.ayros.historycleaner.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ayros.historycleaner.Globals;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

public class RootHelper
{
	public static String runAndWait(String cmd)
	{
		Logger.debug("Run&Wait: " + cmd);

		CommandCapture cc = new CommandCapture(0, false, cmd);

		try
		{
			Shell.runRootCommand(cc);
		}
		catch (Exception e)
		{
			Logger.errorST("Exception when trying to run shell command", e);

			return null;
		}

		if (!waitForCommand(cc))
		{
			return null;
		}

		return cc.toString();
	}

	private static boolean waitForCommand(Command cmd)
	{
		while (!cmd.isFinished())
		{
			synchronized (cmd)
			{
				try
				{
					if (!cmd.isFinished())
					{
						cmd.wait(2000);
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			if (!cmd.isExecuting() && !cmd.isFinished())
			{
				Logger.errorST("Error: Command is not executing and is not finished!");
				return false;
			}
		}

		Logger.debug("Command Finished!");
		return true;
	}

	public static boolean fileOrFolderExists(String path)
	{
		Logger.debug("File or folder exists: " + path);

		// Remove trailing slash if it exists (for directories)
		if (path.charAt(path.length() - 1) == '/')
		{
			path = path.substring(0, path.length() - 1);
		}

		int i = path.lastIndexOf('/');
		if (i == -1)
		{
			Logger.debug("Could not find path folder (invalid filename?)");
			return false;
		}

		String parentDir = path.substring(0, i);

		List<String> fileList = getFilesList(parentDir);

		boolean exists = fileList.contains(path.substring(i + 1));
		Logger.debug("Exists: " + (exists ? "true" : "false"));

		return exists;
	}

	public static List<String> getFilesList(String path)
	{
		Logger.debug("Getting file list: " + path);

		String ls = runAndWait("ls " + path);
		if (ls == null)
		{
			Logger.errorST("Error: Could not get list of files in directory: " + path);
			return new ArrayList<String>();
		}

		if (ls.equals("\n") || ls.equals(""))
		{
			Logger.debug("No files in directory");
			return new ArrayList<String>();
		}
		else
		{
			List<String> files = Arrays.asList(ls.split("\n"));
			for (String file : files)
			{
				Logger.debug("Directory List: " + file);
			}

			return files;
		}
	}

	public static String getFileContents(String path)
	{
		Logger.debug("Getting file contents: " + path);

		String tempFile = Globals.getContext().getCacheDir().getAbsolutePath() + "/_file_" + Helper.randomString(8) + ".txt";

		if (!RootTools.copyFile(path, tempFile, false, true))
		{
			Logger.debug("Could not copy " + path + " to a temp directory for reading");
			return null;
		}

		if (runAndWait("busybox chmod 777 " + tempFile) == null)
		{
			Logger.error("Could not set file attributes to read contents: " + tempFile);
			return null;
		}

		String data = Helper.getFileContents(tempFile);
		RootTools.deleteFileOrDirectory(tempFile, false);

		return data;
	}

	public static boolean deleteFileOrFolder(String path, boolean failOnNonexistant)
	{
		Logger.debug("Deleting File: " + path);

		if (!path.contains("*") && !fileOrFolderExists(path))
		{
			Logger.debug("File doesn't exist - failOnNonexistant: " + failOnNonexistant);
			return !failOnNonexistant;
		}

		// Escape spaces
		path = path.replace(" ", "\\ ");

		return RootTools.deleteFileOrDirectory(path, false);
	}
}
