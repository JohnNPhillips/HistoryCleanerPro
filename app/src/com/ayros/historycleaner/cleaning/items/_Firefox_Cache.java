package com.ayros.historycleaner.cleaning.items;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.RootHelper;

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
			return false;
		}
		
		return RootHelper.deleteFileOrFolder(path + "/Cache/*", false);
	}
}