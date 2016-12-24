package com.ayros.historycleaner.cleaning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.database.DatabaseTest;
import com.ayros.historycleaner.helpers.DBHelper;
import com.ayros.historycleaner.helpers.database.QueryResult;
import com.ayros.historycleaner.helpers.database.RootDatabase;
import com.google.common.collect.Lists;
import com.stericson.RootShell.RootShell;
import com.stericson.RootTools.RootTools;

public class SimpleDatabaseItem extends CleanItem
{
	public static class DBQuery
	{
		String[] headings;
		String table;
		String[] colNames;
		String where;

		public DBQuery(String[] headings, String table, String[] colNames)
		{
			this(headings, table, colNames, null);
		}

		public DBQuery(String[] headings, String table, String[] colNames, String where)
		{
			this.headings = headings;
			this.table = table;
			this.colNames = colNames;
			this.where = where;
		}

		public List<String[]> query(String dbFile, boolean successIfNonexistant)
		{
			if (!RootTools.exists(dbFile) && successIfNonexistant)
			{
				return new ArrayList<>();
			}

			return DBHelper.queryDatabase(dbFile, headings, table, colNames, where);
		}
	}

	String displayName;
	String packageName;
	String dbFile;
	DBQuery dbQuery;
	String[] cleanQueries;
	List<DatabaseTest> cleanPreconditions;

	public SimpleDatabaseItem(Category parent, String displayName, String packageName, String dbFileRelative, DBQuery dbQuery, String[] cleanQueries, List<DatabaseTest> cleanPreconditions)
	{
		super(parent);

		this.displayName = displayName;
		this.packageName = packageName;
		this.dbFile = getDataPath() + dbFileRelative;
		this.dbQuery = dbQuery;
		this.cleanQueries = cleanQueries;
		this.cleanPreconditions = cleanPreconditions;
	}

	public SimpleDatabaseItem(Category parent, String displayName, String packageName, String dbFileRelative, DBQuery dbQuery, String[] cleanQueries)
	{
		this(parent, displayName, packageName, dbFileRelative, dbQuery, cleanQueries, Lists.<DatabaseTest>newArrayList());
	}

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	@Override
	public String getPackageName()
	{
		return packageName;
	}

	@Override
	public List<String[]> getSavedData()
	{
		return dbQuery.query(dbFile, true);
	}

	@Override
	public boolean clean() throws IOException
	{
		if (!RootTools.exists(dbFile))
		{
			return true;
		}

		RootDatabase db = new RootDatabase(dbFile, Globals.getRootShell());
		for (DatabaseTest precondition : cleanPreconditions)
		{
			if (!precondition.passes(db))
			{
				return false;
			}
		}

		return DBHelper.updateDatabase(dbFile, cleanQueries);
	}
}
