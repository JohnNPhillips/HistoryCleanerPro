package com.ayros.historycleaner.cleaning.items.firefox;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.Logger;
import com.ayros.historycleaner.helpers.RootHelper;

public class _FirefoxBeta_OpenTabs extends CleanItem
{
	public _FirefoxBeta_OpenTabs(Category parent)
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
		return "org.mozilla.firefox_beta";
	}

	@Override
	public boolean clean()
	{
		String path = _FirefoxBeta_History.getFirefoxBetaDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox Beta data path to clear open tabs");
			return false;
		}
		else if (path.length() == 0)
		{
			return true;
		}

		return RootHelper.deleteFileOrFolder(path + "/sessionstore.js", false);
	}
}