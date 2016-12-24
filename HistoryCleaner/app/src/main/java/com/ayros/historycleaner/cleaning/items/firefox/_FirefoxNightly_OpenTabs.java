package com.ayros.historycleaner.cleaning.items.firefox;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItemStub;
import com.ayros.historycleaner.helpers.RootHelper;

import java.io.IOException;

public class _FirefoxNightly_OpenTabs extends CleanItemStub
{
	public _FirefoxNightly_OpenTabs(Category parent)
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
		return "org.mozilla.fennec";
	}

	@Override
	public void clean() throws IOException
	{
		String path = FirefoxUtils.getFirefoxDataPath(getPackageName());

		RootHelper.deleteFileOrFolder(path + "/sessionstore.js");
	}
}