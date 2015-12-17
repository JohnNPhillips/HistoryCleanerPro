package com.ayros.historycleaner.cleaning.items.firefox;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.Logger;
import com.ayros.historycleaner.helpers.RootHelper;

public class _FirefoxBeta_Cache extends CleanItem
{
	public _FirefoxBeta_Cache(Category parent)
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
		return "org.mozilla.firefox_beta";
	}

	@Override
	public boolean clean()
	{
		String path = _FirefoxBeta_History.getFirefoxBetaDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox Beta data path to clear cache");
			return false;
		}
		else if (path.length() == 0)
		{
			return true;
		}

		return RootHelper.deleteFileOrFolder(path + "/Cache", false);
	}
}