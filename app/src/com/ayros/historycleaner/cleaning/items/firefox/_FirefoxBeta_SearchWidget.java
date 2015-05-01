package com.ayros.historycleaner.cleaning.items.firefox;

import java.util.ArrayList;
import java.util.List;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.DBHelper;
import com.ayros.historycleaner.helpers.Logger;

public class _FirefoxBeta_SearchWidget extends CleanItem
{
	public _FirefoxBeta_SearchWidget(Category parent)
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
		return "org.mozilla.firefox_beta";
	}

	@Override
	public List<String []> getSavedData()
	{
		String path = _FirefoxBeta_History.getFirefoxBetaDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox Beta data path to view search widget history");
			return null;
		}
		else if (path.length() == 0)
		{
			return new ArrayList<String []>();
		}

		return DBHelper.queryDatabase
		(
			Globals.getContext(),
			path + "/browser.db",
			new String[] { "Search Query", "Timestamp" },
			"searchhistory",
			new String[] { "query", "date" },
			null
		);
	}
	
	@Override
	public boolean clean()
	{
		String path = _FirefoxBeta_History.getFirefoxBetaDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox Beta data path to clear search widget history");
			return false;
		}
		else if (path.length() == 0)
		{
			return true;
		}

		return DBHelper.updateDatabase
		(
			Globals.getContext(),
			path + "/browser.db",
			new String[]
			{
				"UPDATE sqlite_sequence SET seq='0' WHERE name='searchhistory';",
				"DELETE FROM searchhistory;",
			}
		);
	}
}