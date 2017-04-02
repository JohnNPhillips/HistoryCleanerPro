package com.ayros.historycleaner.cleaning;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.ayros.historycleaner.helpers.Logger;
import com.ayros.historycleaner.helpers.RootHelper;

public class SimpleFileItem extends CleanItemStub
{
	String displayName;
	String packageName;
	String[] clearFiles;
	boolean relativeFiles;

	public SimpleFileItem(Category parent, String displayName, String packageName, String clearFile, boolean relativeFiles)
	{
		this(parent, displayName, packageName, new String[] { clearFile }, relativeFiles);
	}

	public SimpleFileItem(Category parent, String displayName, String packageName, String[] clearFiles, boolean relativeFiles)
	{
		super(parent);

		this.displayName = displayName;
		this.packageName = packageName;
		this.clearFiles = clearFiles;
		this.relativeFiles = relativeFiles;
	}

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	@Override
	public String getPackageName()
	{
		return packageName;
	}

	@Override
	public List<String[]> getSavedData()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clean() throws IOException
	{
		for (String file : clearFiles)
		{
			if (relativeFiles)
			{
				file = getDataPath() + file;
			}

			try
			{
				RootHelper.deleteFileOrFolder(file);
			}
			catch (FileNotFoundException e)
			{
				Logger.debug("SimpleFileItem not found: " + file + " (most likely the app was already cleaned");
			}
		}
	}
}
