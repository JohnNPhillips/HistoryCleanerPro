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
		
		CommandCapture cc = new CommandCapture(0, cmd);

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

		return true;
	}

	public static boolean fileOrFolderExists(String path)
	{
		// Remove trailing slash if it exists (for directories)
		if (path.charAt(path.length() - 1) == '/')
		{
			path = path.substring(0, path.length() - 1);
		}

		int i = path.lastIndexOf('/');
		if (i == -1)
		{
			return false;
		}

		String parentDir = path.substring(0, i);

		List<String> fileList = getFilesList(parentDir);

		return fileList.contains(path.substring(i + 1));
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
			return new ArrayList<String>();
		}
		else
		{
			return Arrays.asList(ls.split("\n"));
		}
	}

	public static String getFileContents(String path)
	{
		Logger.debug("Getting file contents: " + path);
		
		String tempFile = Globals.getContext().getCacheDir().getAbsolutePath() + "/_file_" + Helper.randomString(8) + ".txt";

		if (!RootTools.copyFile(path, tempFile, false, true))
		{
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
			return !failOnNonexistant;
		}

		return RootTools.deleteFileOrDirectory("\"" + path + "\"", false);
	}
}
