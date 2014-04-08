package com.ayros.historycleaner.legacy.cleaning.items;

import com.ayros.historycleaner.legacy.cleaning.Category;
import com.ayros.historycleaner.legacy.cleaning.CleanItem;
import com.ayros.historycleaner.legacy.helpers.Logger;
import com.ayros.historycleaner.legacy.helpers.RootHelper;

public class _Firefox_OpenTabs extends CleanItem
{
	public _Firefox_OpenTabs(Category parent)
	{
		super(parent);
	}

	@Override
	public String getDisplayName()
	{
		return "Open Tabs";
	}

	@Override
	public String getPackageName()
	{
		return "org.mozilla.firefox";
	}

	@Override
	public boolean clean()
	{
		String path = _Firefox_History.getFirefoxDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox data path to clear open tabs");
			return false;
		}

		return RootHelper.deleteFileOrFolder(path + "/sessionstore.js", false);
	}
}