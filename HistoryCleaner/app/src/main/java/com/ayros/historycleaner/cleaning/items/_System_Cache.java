package com.ayros.historycleaner.cleaning.items;

import java.io.IOException;
import java.lang.reflect.Method;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.R;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItemStub;

public class _System_Cache extends CleanItemStub
{
	public _System_Cache(Category parent)
	{
		super(parent);
	}

	@Override
	public String getDisplayName()
	{
		return "App Caches";
	}

	@Override
	public Drawable getIcon()
	{
		return Globals.getContext().getResources().getDrawable(R.drawable.system_cache);
	}

	@Override
	public String getPackageName()
	{
		return "";
	}

	@Override
	public boolean isApplicable()
	{
		return true;
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
	public void clean() throws IOException
	{
		PackageManager pm = Globals.getContext().getPackageManager();
		
		Method[] methods = pm.getClass().getDeclaredMethods();
		for (Method m : methods)
		{
			if (m.getName().equals("freeStorageAndNotify"))
			{
				try
				{
					m.invoke(pm, Long.MAX_VALUE, null);

					return;
				}
				catch (Exception e)
				{
					throw new IOException("Couldn't clear system cache", e);
				}
			}
		}

		throw new IOException("Couldn't find freeStorageAndNotify method");
	}
}
