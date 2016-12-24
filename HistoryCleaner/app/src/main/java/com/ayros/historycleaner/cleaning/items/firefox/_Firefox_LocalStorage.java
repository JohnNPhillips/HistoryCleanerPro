package com.ayros.historycleaner.cleaning.items.firefox;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItemStub;
import com.ayros.historycleaner.helpers.RootHelper;
import com.stericson.RootTools.RootTools;

import java.io.IOException;

public class _Firefox_LocalStorage extends CleanItemStub
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
	public void clean() throws IOException
	{
		String path = FirefoxUtils.getFirefoxDataPath(getPackageName());

		if (!RootTools.exists(path + "/webappsstore.sqlite"))
		{
			return;
		}

		RootHelper.deleteFileOrFolder(path + "/webappsstore.sqlite*");
	}
}