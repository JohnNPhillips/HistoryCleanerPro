package com.ayros.historycleaner.cleaning.database;

import com.ayros.historycleaner.helpers.database.RootDatabase;

import java.io.IOException;

public class TableExistsTest implements DatabaseTest
{
	private final String tableName;

	public TableExistsTest(String tableName)
	{
		this.tableName = tableName;
	}

	@Override
	public boolean passes(RootDatabase db) throws IOException
	{
		return db.tableExists(tableName);
	}
}
