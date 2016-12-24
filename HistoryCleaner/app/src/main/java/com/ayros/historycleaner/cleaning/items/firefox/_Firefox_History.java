package com.ayros.historycleaner.cleaning.items.firefox;

import java.io.IOException;
import java.util.List;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItemStub;
import com.ayros.historycleaner.helpers.DBHelper;

public class _Firefox_History extends CleanItemStub
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

	@Override
	public List<String []> getSavedData() throws IOException
	{
		String path = FirefoxUtils.getFirefoxDataPath(getPackageName());

		return DBHelper.queryDatabase
		(
			path + "/browser.db",
			new String[] { "Title", "URL" },
			"history",
			new String[] { "title", "url" },
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
				"UPDATE sqlite_sequence SET seq='0' WHERE name='history';",
				"DELETE FROM history;",
			}
		);
	}
}