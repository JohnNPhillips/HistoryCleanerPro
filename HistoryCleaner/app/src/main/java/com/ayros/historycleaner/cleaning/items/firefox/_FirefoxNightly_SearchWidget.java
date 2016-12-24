package com.ayros.historycleaner.cleaning.items.firefox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.DBHelper;
import com.ayros.historycleaner.helpers.Logger;

public class _FirefoxNightly_SearchWidget extends CleanItem
{
	public _FirefoxNightly_SearchWidget(Category parent)
	{
		super(parent);
	}

	@Override
	public String getDisplayName()
	{
		return "Search Widget History";
	}

	@Override
	public String getPackageName()
	{
		return "org.mozilla.fennec";
	}

	@Override
	public List<String []> getSavedData() throws IOException
	{
		String path = FirefoxUtils.getFirefoxDataPath(getPackageName());

		return DBHelper.queryDatabase
		(
			path + "/browser.db",
			new String[] { "Search Query", "Timestamp" },
			"searchhistory",
			new String[] { "query", "date" },
			null
		);
	}
	
	@Override
	public void clean() throws IOException
	{
		String path = FirefoxUtils.getFirefoxDataPath(getPackageName());

		DBHelper.updateDatabase
		(
			path + "/browser.db",
			new String[]
			{
				"UPDATE sqlite_sequence SET seq='0' WHERE name='searchhistory';",
				"DELETE FROM searchhistory;",
			}
		);
	}
}