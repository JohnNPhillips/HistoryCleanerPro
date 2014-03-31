package com.ayros.historycleaner.cleaning.items;

import java.lang.reflect.Method;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.R;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;

public class _System_Cache extends CleanItem
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
	public boolean clean()
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
					
					return true;
				}
				catch (Exception e)
				{
					return false;
				}
			}
		}

		return false;
	}
}
