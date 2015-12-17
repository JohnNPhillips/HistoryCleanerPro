package com.ayros.historycleaner;

import android.content.Context;

import com.ayros.historycleaner.cleaning.CleanItem;

public class Globals
{
	// -> CleanActivity
	public static String saveProfileText = "";

	// -> DataViewActivity
	public static CleanItem itemDataView = null;
	
	private static Context context = null;
	
	public static void setContext(Context c)
	{
		context = c;
	}
	
	public static Context getContext()
	{
		return context;
	}
}
