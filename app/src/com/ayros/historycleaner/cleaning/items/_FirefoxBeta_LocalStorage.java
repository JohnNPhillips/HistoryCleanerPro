package com.ayros.historycleaner.cleaning.items;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.Logger;
import com.ayros.historycleaner.helpers.RootHelper;
import com.stericson.RootTools.RootTools;

public class _FirefoxBeta_LocalStorage extends CleanItem
{
	public _FirefoxBeta_LocalStorage(Category parent)
	{
		super(parent);
	}

	@Override
	public String getDisplayName()
	{
		return "Local Storage";
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
			Logger.error("Could not get FireFox Beta data path to clear local storage");
			return false;
		}

		if (!RootTools.exists(path + "/webappsstore.sqlite"))
		{
			return true;
		}

		return RootHelper.deleteFileOrFolder(path + "/webappsstore.sqlite*", false);
	}
}