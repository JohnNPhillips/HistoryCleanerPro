package com.ayros.historycleaner.cleaning.items;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.DBHelper;
import com.ayros.historycleaner.helpers.RootHelper;

public class _ONEBrowser_Cache extends CleanItem
{
	public _ONEBrowser_Cache(Category parent)
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
		return "com.tencent.ibibo.mtt";
	}
	
	@Override
	public boolean clean()
	{
		if (!RootHelper.deleteFileOrFolder(getDataPath() + "/cache/*", false))
		{
			return false;
		}
		
		return DBHelper.updateDatabase
		(
			Globals.getContext(),
			getDataPath() + "/databases/webviewCache_x5.db",
			new String[]
			{
				"DELETE FROM cache;"
			}
		);
	}
}
