package com.ayros.historycleaner.legacy.cleaning.items;

import java.util.ArrayList;
import java.util.List;

import com.ayros.historycleaner.legacy.cleaning.Category;
import com.ayros.historycleaner.legacy.cleaning.CleanItem;
import com.ayros.historycleaner.legacy.helpers.PrefsModifier;
import com.stericson.RootTools.RootTools;

public class _AdobeReader_Recent extends CleanItem
{
	public _AdobeReader_Recent(Category parent)
	{
		super(parent);
	}

	@Override
	public String getDisplayName()
	{
		return "Recently Opened";
	}

	@Override
	public String getPackageName()
	{
		return "com.adobe.reader";
	}

	@Override
	public List<String[]> getSavedData()
	{
		PrefsModifier pm = new PrefsModifier(getDataPath() + "/shared_prefs/com.adobe.reader.preferences.xml");

		List<String[]> list = new ArrayList<String[]>();
		list.add(new String[] { "File Path" });

		int index = 0;
		String val;
		while ((val = pm.getValue("recentFile" + index)) != null)
		{
			list.add(new String[] { val });
			index++;
		}

		return list;
	}

	@Override
	public boolean clean()
	{
		return RootTools.deleteFileOrDirectory(getDataPath() + "/shared_prefs/com.adobe.reader.preferences.xml", false);
	}
}