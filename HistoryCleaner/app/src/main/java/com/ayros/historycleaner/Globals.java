package com.ayros.historycleaner;

import android.content.Context;

import com.ayros.historycleaner.cleaning.CleanItemStub;
import com.ayros.historycleaner.helpers.Logger;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Shell;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Globals
{
	// -> CleanActivity
	public static String saveProfileText = "";

	// -> DataViewActivity
	public static CleanItemStub itemDataView = null;

	private static Shell rootShell = null;
	
	private static Context context = null;
	
	public static void setContext(Context c)
	{
		context = c;
	}
	
	public static Context getContext()
	{
		return context;
	}

	public static Shell getRootShell()
	{
		if (rootShell == null || rootShell.isClosed)
		{
			try
			{
				rootShell = RootShell.getShell(true);
			}
			catch (TimeoutException | IOException | RootDeniedException e)
			{
				Logger.errorST("Could not get root access", e);
			}
		}
		return rootShell;
	}
}
