package com.ayros.historycleaner.legacy.cleaning.items;

import com.ayros.historycleaner.legacy.cleaning.Category;
import com.ayros.historycleaner.legacy.cleaning.CleanItem;
import com.ayros.historycleaner.legacy.helpers.Logger;
import com.ayros.historycleaner.legacy.helpers.RootHelper;

public class _Firefox_Cache extends CleanItem
{
	public _Firefox_Cache(Category parent)
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
		return "org.mozilla.firefox";
	}

	@Override
	public boolean clean()
	{
		String path = _Firefox_History.getFirefoxDataPath();
		if (path == null)
		{
			Logger.error("Could not get FireFox data path to clear cache");
			return false;
		}
		
		return RootHelper.deleteFileOrFolder(path + "/Cache", false);
	}
}