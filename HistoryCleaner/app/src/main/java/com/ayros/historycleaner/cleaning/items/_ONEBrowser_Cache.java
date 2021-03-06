package com.ayros.historycleaner.cleaning.items;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItemStub;
import com.ayros.historycleaner.helpers.DBHelper;
import com.ayros.historycleaner.helpers.RootHelper;

import java.io.IOException;

public class _ONEBrowser_Cache extends CleanItemStub
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
	public void clean() throws IOException
	{
		RootHelper.deleteFileOrFolder(getDataPath() + "/cache/*");
		
		DBHelper.updateDatabase
		(
			getDataPath() + "/databases/webviewCache_x5.db",
			new String[]
			{
				"DELETE FROM cache;"
			}
		);
	}
}
