package com.ayros.historycleaner.cleaning.items;

import android.net.Uri;
import android.os.Build;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItemStub;
import com.ayros.historycleaner.helpers.Logger;
import com.google.common.base.Optional;

import java.io.IOException;

public class _System_SMS extends CleanItemStub
{
	public _System_SMS(Category parent)
	{
		super(parent);
	}

	@Override
	public String getDisplayName()
	{
		return "SMS History";
	}

	@Override
	public String getPackageName()
	{
		return "com.android.mms";
	}

	@Override
	public Optional<String> getWarningMessage()
	{
		return Optional.of("This will delete ALL previous text messages. Be sure this is what you want to do.");
	}

	@Override
	public boolean isApplicable()
	{
		if (!super.isApplicable())
		{
			return false;
		}

		// Can't clear SMS on KitKat unless app is the default messenger
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
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
		try
		{
			int count = Globals.getContext().getContentResolver().delete(Uri.parse("content://sms/"), null, null);
			Logger.debug("Deleted " + count + " text messages");
		}
		catch (Exception e)
		{
			throw new IOException("Couldn't delete SMS", e);
		}
	}
}