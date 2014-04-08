package com.ayros.historycleaner.legacy.cleaning.items;

import android.content.pm.PackageManager;

import com.ayros.historycleaner.legacy.Globals;
import com.ayros.historycleaner.legacy.cleaning.Category;
import com.ayros.historycleaner.legacy.cleaning.CleanItem;

public class _System_RecentCalls extends CleanItem
{
	public _System_RecentCalls(Category parent)
	{
		super(parent);
	}
	
	@Override
	public String getDisplayName()
	{
		return "Recent Calls";
	}

	@Override
	public String getPackageName()
	{
		return "com.android.phone";
	}

	@Override
	public boolean isApplicable()
	{
		PackageManager pm = Globals.getContext().getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
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
		Globals.getContext().getContentResolver().delete(android.provider.CallLog.Calls.CONTENT_URI, null, null);
		
		return true;
	}
}