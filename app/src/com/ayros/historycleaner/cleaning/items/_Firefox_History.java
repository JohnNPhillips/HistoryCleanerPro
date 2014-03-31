package com.ayros.historycleaner.cleaning.items;

import java.util.List;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.DBHelper;
import com.ayros.historycleaner.helpers.RootHelper;
import com.stericson.RootTools.RootTools;

public class _Firefox_History extends CleanItem
{
	public _Firefox_History(Category parent)
	{
		super(parent);
	}
	
	@Override
	public String getDisplayName()
	{
		return "History";
	}
	
	@Override
	public String getPackageName()
	{
		return "org.mozilla.firefox";
	}
	
	public static String getFirefoxDataPath()
	{
		String profiles = RootHelper.getFileContents("/data/data/org.mozilla.firefox/files/mozilla/profiles.ini");
		String[] lines = profiles.split("\n");
		for (String line : lines)
		{
			if (line.contains("Path="))
			{
				String folder = "/data/data/org.mozilla.firefox/files/mozilla/" + line.replace("Path=", "");
				if (RootTools.exists(folder))
				{
					return folder;
				}
				else
				{
					return null;
				}
			}
		}
		
		return null;
	}
	
	@Override
	public List<String []> getSavedData()
	{
		String path = getFirefoxDataPath();
		if (path == null)
		{
			return null;
		}

		return DBHelper.queryDatabase
		(
			Globals.getContext(),
			path + "/browser.db",
			new String[] { "Title", "URL" },
			"history",
			new String[] { "title", "url" },
			null
		);
	}
	
	@Override
	public boolean clean()
	{
		String path = getFirefoxDataPath();
		if (path == null)
		{
			return false;
		}

		return DBHelper.updateDatabase
		(
			Globals.getContext(),
			path + "/browser.db",
			new String[]
			{
				"UPDATE sqlite_sequence SET seq='0' WHERE name='history';",
				"DELETE FROM history;",
			}
		);
	}
}