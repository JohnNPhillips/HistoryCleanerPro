package com.ayros.historycleaner.cleaning.items;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Toast;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.R;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItemStub;
import com.ayros.historycleaner.helpers.RootHelper;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.stericson.RootTools.RootTools;
import com.twofortyfouram.spackle.AndroidSdkVersion;

public class _System_Cache extends CleanItemStub
{
	private static final String WARNING_TEXT = "Google removed official API support for this in Android 6.0+. " +
			"This now forcefully clears cache directories using root access, " +
			"so there is a chance it could cause some background applications to crash. " +
			"Use at your own risk. " +
			"(As a precautionary measure, it does not clear caches of system apps)";

	private static final long MINIMUM_KB = 16;
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
	public Optional<String> getWarningMessage()
	{
		return useNewVersion() ? Optional.of(WARNING_TEXT) : super.getWarningMessage();
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
	public List<String[]> getSavedData() throws IOException, UnsupportedOperationException
	{
		if (!useNewVersion())
		{
			throw new UnsupportedOperationException();
		}

		PackageManager pm = Globals.getContext().getPackageManager();

		List<String[]> dataTable = new ArrayList<>();
		for (Map.Entry<ApplicationInfo, String> entry : getCleanableApplications(pm).entrySet())
		{
			String cacheDir = entry.getValue();
			long sizeKb = RootHelper.getDirectorySizeKB(cacheDir);
			if (sizeKb >= MINIMUM_KB)
			{
				ApplicationInfo appInfo = entry.getKey();

				String appName = appInfo.loadLabel(pm).toString();
				dataTable.add(new String[]{appName, Long.toString(sizeKb), cacheDir});
			}
		}

		Collections.sort(dataTable, new Comparator<String[]>()
		{
			@Override
			public int compare(String[] row1, String[] row2)
			{
				return row1[0].compareTo(row2[0]);
			}
		});
		dataTable.add(0, new String[]{"Application", "Size (kilobytes)", "Directory"});

		return dataTable;
	}

	@Override
	public void clean() throws IOException
	{
		if (useNewVersion())
		{
			cleanNew();
		}
		else
		{
			cleanOld();
		}
	}

	private void cleanOld() throws IOException
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

	private void cleanNew() throws IOException
	{
		PackageManager pm = Globals.getContext().getPackageManager();
		Map<ApplicationInfo, String> appsToCacheDirs = getCleanableApplications(pm);

		for (String cacheDir : getCleanableApplications(pm).values())
		{
			long sizeKb = RootHelper.getDirectorySizeKB(cacheDir);
			if (sizeKb >= MINIMUM_KB)
			{
				RootHelper.deleteFileOrFolder(String.format("%s/*", cacheDir));
			}
		}
	}

	private Map<ApplicationInfo, String> getCleanableApplications(PackageManager pm)
	{
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		Map<ApplicationInfo, String> appsToCacheDirs = new HashMap<>();
		for (int i = packages.size() - 1; i >= 0; i--)
		{
			ApplicationInfo appInfo = packages.get(i);
			if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
			{
				appsToCacheDirs.put(appInfo, String.format("%s/cache", appInfo.dataDir));
			}
		}

		return appsToCacheDirs;
	}

	private boolean useNewVersion()
	{
		return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}
}
