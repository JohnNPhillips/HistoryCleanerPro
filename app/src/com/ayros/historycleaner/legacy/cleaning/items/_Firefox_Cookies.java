package com.ayros.historycleaner.legacy.cleaning.items;

import java.util.List;

import com.ayros.historycleaner.legacy.Globals;
import com.ayros.historycleaner.legacy.cleaning.Category;
import com.ayros.historycleaner.legacy.cleaning.CleanItem;
import com.ayros.historycleaner.legacy.helpers.DBHelper;
import com.ayros.historycleaner.legacy.helpers.Logger;

public class _Firefox_Cookies extends CleanItem
{
	public _Firefox_Cookies(Category parent)
	{
		super(parent);
	}
	
	@Override
	public String getDisplayName()
	{
		return "Cookies";
	}
	
	@Override
	public String getPackageName()
	{
		return "org.mozilla.firefox";
	}
	
	@Override
	public List<String []> getSavedData()
	{
		String path = _Firefox_History.getFirefoxDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox data path to view cookies");
			return null;
		}

		return DBHelper.queryDatabase
		(
			Globals.getContext(),
			path + "/cookies.sqlite",
			new String[] { "Domain", "Cookie Name", "Cookie Value" },
			"moz_cookies",
			new String[] { "baseDomain", "name", "value" },
			null
		);
	}
	
	@Override
	public boolean clean()
	{
		String path = _Firefox_History.getFirefoxDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox data path to clear cookies");
			return false;
		}

		return DBHelper.updateDatabase
		(
			Globals.getContext(),
			path + "/cookies.sqlite",
			new String[]
			{
				"DELETE FROM moz_cookies;",
			}
		);
	}
}