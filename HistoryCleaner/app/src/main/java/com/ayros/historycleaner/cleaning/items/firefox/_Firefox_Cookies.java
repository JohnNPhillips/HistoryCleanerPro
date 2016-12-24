package com.ayros.historycleaner.cleaning.items.firefox;

import java.util.ArrayList;
import java.util.List;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.DBHelper;
import com.ayros.historycleaner.helpers.Logger;

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
		else if (path.length() == 0)
		{
			return new ArrayList<String []>();
		}

		return DBHelper.queryDatabase
		(
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
		else if (path.length() == 0)
		{
			return true;
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