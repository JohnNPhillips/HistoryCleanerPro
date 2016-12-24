package com.ayros.historycleaner.cleaning.items.firefox;

import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.SimpleDatabaseItem;

import java.io.IOException;

public class FirefoxDatabaseItem extends SimpleDatabaseItem
{
	private final String dbFileRelative;

	public FirefoxDatabaseItem(Category parent, String displayName, String packageName, String dbFileRelative, SimpleDatabaseItem.DBQuery dbQuery, String[] cleanQueries)
	{
		super(parent, displayName, packageName, dbFileRelative, dbQuery, cleanQueries);

		this.dbFileRelative = dbFileRelative;
	}

	@Override
	public String getDatabaseFile() throws IOException
	{
		return FirefoxUtils.getFirefoxDataPath(getPackageName()) + dbFileRelative;
	}
}
