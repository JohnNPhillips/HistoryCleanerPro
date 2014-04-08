package com.ayros.historycleaner.legacy.cleaning;

import java.util.List;

import com.ayros.historycleaner.legacy.helpers.RootHelper;

public class SimpleFileItem extends CleanItem
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
		return null;
	}

	@Override
	public boolean clean()
	{
		for (String file : clearFiles)
		{
			if (relativeFiles)
			{
				file = getDataPath() + file;
			}
			
			RootHelper.deleteFileOrFolder(file, false);
		}

		return true;
	}
}
