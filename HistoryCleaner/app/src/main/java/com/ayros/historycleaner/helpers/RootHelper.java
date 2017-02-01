package com.ayros.historycleaner.helpers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.ayros.historycleaner.Globals;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootTools.RootTools;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;

public class RootHelper
{
	public static ExecutionResult runAndWait(String cmd, Shell shell) throws IOException
	{
		return runAndWait(cmd, shell, true);
	}

	public static ExecutionResult runAndWait(String cmd, Shell shell, boolean exceptionOnFailure) throws IOException
	{
		Logger.debug("Run&Wait: " + cmd);

		CommandCapture cc = new CommandCapture(0, cmd);
		shell.add(cc);

		if (!waitForCommand(cc))
		{
			throw new IOException(String.format("Error waiting for command to finish executing {%s}", cmd));
		}

		if (exceptionOnFailure && cc.getExitCode() != 0)
		{
			throw new IOException(String.format("Unsuccessful exit code (%d) when executing command {%s}", cc.getExitCode(), cmd));
		}

		return new ExecutionResult(cc.toString(), cc.getExitCode());
	}

	public static String runAndWait(String cmd) throws IOException
	{
		try
		{
			return runAndWait(cmd, RootTools.getShell(true)).getOutput();
		}
		catch (RootDeniedException | TimeoutException e)
		{
			throw new IOException(e);
		}
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
						cmd.wait(100);
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

	public static boolean fileOrFolderExists(String path) throws IOException
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

	public static List<String> getFilesList(String path) throws IOException
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

	public static String getFileContents(String path) throws IOException
	{
		return RootHelper.runAndWait("cat " + path);
	}

	public static void deleteFileOrFolder(String path) throws IOException
	{
		Logger.debug("Deleting File: " + path);

		if (!path.contains("*") && !fileOrFolderExists(path))
		{
			throw new FileNotFoundException("File/folder not found: " + path);
		}

		// Escape spaces
		path = path.replace(" ", "\\ ");

		if (!RootTools.deleteFileOrDirectory(path, false))
		{
			throw new IOException("Failed to delete file or directory " + path);
		}
	}

	public static long getDirectorySizeKB(String dir)
	{
		try
		{
			String output = RootHelper.runAndWait(String.format("du -k '%s'| tail -n 1", dir));
			String kb = output.split("\\s")[0];
			return Long.parseLong(kb);
		}
		catch (Exception e)
		{
			// This method is only used for data viewing, just fail silently on any exceptions
			return -1;
		}
	}
}
