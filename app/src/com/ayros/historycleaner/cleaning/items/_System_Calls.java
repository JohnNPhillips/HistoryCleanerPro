package com.ayros.historycleaner.cleaning.items;

import android.content.pm.PackageManager;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;

public class _System_Calls extends CleanItem
{
	String display;
	int type;

	public _System_Calls(Category parent, String display, int type)
	{
		super(parent);

		this.display = display;
		this.type = type;
	}

	@Override
	public String getDisplayName()
	{
		return display;
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
		String whereClause = "TYPE='" + type + "'";
		Globals.getContext().getContentResolver().delete(android.provider.CallLog.Calls.CONTENT_URI, whereClause, null);

		return true;
	}
}