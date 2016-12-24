package com.ayros.historycleaner.cleaning.items.firefox;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItemStub;
import com.ayros.historycleaner.cleaning.SimpleFileItem;
import com.ayros.historycleaner.helpers.RootHelper;

import java.io.IOException;

public class FirefoxFileItem extends CleanItemStub
{
    String displayName;
    String packageName;
    String clearFile;

    public FirefoxFileItem(Category parent, String displayName, String packageName, String clearFile)
    {
        super(parent);

        this.displayName = displayName;
        this.packageName = packageName;
        this.clearFile = clearFile;
    }

	@Override
	public String getPackageName()
	{
		return packageName;
	}

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	@Override
	public void clean() throws IOException
	{
		String path = FirefoxUtils.getFirefoxDataPath(getPackageName());

		RootHelper.deleteFileOrFolder(path + clearFile);
	}
}
