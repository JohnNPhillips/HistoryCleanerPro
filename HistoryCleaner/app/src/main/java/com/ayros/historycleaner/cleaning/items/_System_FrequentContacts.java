package com.ayros.historycleaner.cleaning.items;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.PackageManager;
import android.os.Build;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.helpers.DBHelper;
import com.stericson.RootTools.RootTools;

public class _System_FrequentContacts extends CleanItem
{
	public _System_FrequentContacts(Category parent)
	{
		super(parent);
	}

	@Override
	public String getDisplayName()
	{
		return "Frequently Called";
	}

	@Override
	public String getPackageName()
	{
		return "com.android.phone";
	}

	@Override
	public boolean isApplicable()
	{
		// Frequent contacts isn't available until Honeycomb
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
		{
			return false;
		}
		
		PackageManager pm = Globals.getContext().getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
	}

	@Override
	public List<String[]> getSavedData()
	{
		if (!RootTools.exists("/data/data/com.android.providers.contacts/databases/contacts2.db"))
		{
			return new ArrayList<String[]>();
		}

		return DBHelper.queryDatabase
		(
			Globals.getContext(),
			"/data/data/com.android.providers.contacts/databases/contacts2.db",
			new String[] { "Name", "Times Contacted" },
			"raw_contacts",
			new String[] { "display_name", "times_contacted" },
			"times_contacted>'0'"
		);
	}

	@Override
	public boolean clean()
	{
		if (!RootTools.exists("/data/data/com.android.providers.contacts/databases/contacts2.db"))
		{
			return true;
		}

		return DBHelper.updateDatabase
		(
			Globals.getContext(),
			"/data/data/com.android.providers.contacts/databases/contacts2.db",
			new String[]
			{
				"UPDATE contacts SET times_contacted='0';",
				"UPDATE contacts SET last_time_contacted='0';",
				"UPDATE raw_contacts SET times_contacted='0';",
				"UPDATE raw_contacts SET last_time_contacted='0';",
				"DELETE FROM data_usage_stat;"
			}
		);
	}
}
