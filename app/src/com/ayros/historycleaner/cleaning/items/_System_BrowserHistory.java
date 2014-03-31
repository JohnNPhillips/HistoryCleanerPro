package com.ayros.historycleaner.cleaning.items;

import android.provider.Browser;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;

public class _System_BrowserHistory extends CleanItem
{
	public _System_BrowserHistory(Category parent)
	{
		super(parent);
	}

	@Override
	public String getDisplayName()
	{
		return "Browser History (default)";
	}

	@Override
	public String getPackageName()
	{
		return "com.android.browser";
	}

	@Override
	public boolean runOnUIThread()
	{
		return true;
	}

	@Override
	public boolean isRootRequired()
	{
		return false;
	}

	@Override
	public boolean clean()
	{
		Browser.clearHistory(Globals.getContext().getContentResolver());
		Browser.clearSearches(Globals.getContext().getContentResolver());

		return true;
	}
}