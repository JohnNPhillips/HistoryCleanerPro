package com.ayros.historycleaner.cleaning.items;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.Logger;
import com.ayros.historycleaner.helpers.RootHelper;

public class _FirefoxNightly_Cache extends CleanItem
{
	public _FirefoxNightly_Cache(Category parent)
	{
		super(parent);
	}

	@Override
	public String getDisplayName()
	{
		return "Cache";
	}

	@Override
	public String getPackageName()
	{
		return "org.mozilla.fennec";
	}

	@Override
	public boolean clean()
	{
		String path = _FirefoxNightly_History.getFirefoxNightlyDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox Nightly data path to clear cache");
			return false;
		}
		else if (path.length() == 0)
		{
			return true;
		}

		return RootHelper.deleteFileOrFolder(path + "/Cache", false);
	}
}