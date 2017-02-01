package com.ayros.historycleaner.cleaning.items;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItemStub;
import com.ayros.historycleaner.helpers.Helper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class _System_Calls extends CleanItemStub
{
	private final String display;
	private final int type;

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
	public List<String[]> getSavedData() throws IOException
	{
		Cursor c;
		try
		{
			String whereClause = "TYPE='" + type + "'";
			String[] columns = { CallLog.Calls.DATE, CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME };
			c = Globals.getContext().getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,
					columns,
					whereClause,
					null,
					null);
		}
		catch (SecurityException e)
		{
			throw new IOException(e);
		}

		return Helper.cursorToDataView(c, ImmutableSet.of(0));
	}

	@Override
	public void clean() throws IOException
	{
		String whereClause = "TYPE='" + type + "'";
		try
		{
			Globals.getContext().getContentResolver().delete(android.provider.CallLog.Calls.CONTENT_URI, whereClause, null);
		}
		catch (SecurityException e)
		{
			throw new IOException("Couldn't clear call history", e);
		}
	}

	@Override
	public Set<String> getRequiredPermissions()
	{
		return ImmutableSet.of(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG);
	}
}