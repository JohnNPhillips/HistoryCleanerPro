package com.ayros.historycleaner.cleaning.items.firefox;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.Logger;
import com.ayros.historycleaner.helpers.RootHelper;
import com.stericson.RootTools.RootTools;

public class _Firefox_LocalStorage extends CleanItem
{
	public _Firefox_LocalStorage(Category parent)
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
		return "org.mozilla.firefox";
	}

	@Override
	public boolean clean()
	{
		String path = _Firefox_History.getFirefoxDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox data path to clear local storage");
			return false;
		}
		else if (path.length() == 0)
		{
			return true;
		}

		if (!RootTools.exists(path + "/webappsstore.sqlite"))
		{
			return true;
		}

		return RootHelper.deleteFileOrFolder(path + "/webappsstore.sqlite*", false);
	}
}