package com.ayros.historycleaner.cleaning.items;

import java.util.ArrayList;
import java.util.List;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.RootHelper;
import com.stericson.RootTools.RootTools;

public class _ONEBrowser_LocalStorage extends CleanItem
{
	public _ONEBrowser_LocalStorage(Category parent)
	{
		super(parent);
	}
	
	@Override
	public String getDisplayName()
	{
		return "Local Storage";
	}
	
	@Override
	public String getPackageName()
	{
		return "com.tencent.ibibo.mtt";
	}
	
	@Override
	public List<String[]> getSavedData()
	{
		List<String[]> ret = new ArrayList<String[]>();
		List<String> fileList = RootHelper.getFilesList(getDataPath() + "/app_databases/*.localstorage");
		if (fileList.size() == 0)
		{
			ret.add(new String[] {});
			ret.add(new String[] { "There are no sites using local storage." });
		}
		else
		{
			ret.add(new String[] { "File List" });
			for (String file : fileList)
			{
				ret.add(new String[] { file });
			}
		}
		
		return ret;
	}
	
	@Override
	public boolean clean()
	{
		return RootTools.deleteFileOrDirectory(getDataPath() + "/app_databases/*.localstorage", false);
	}
}
