package com.ayros.historycleaner.cleaning;

import java.util.ArrayList;
import java.util.List;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.helpers.DBHelper;
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
				return new ArrayList<String[]>();
			}

			return DBHelper.queryDatabase(Globals.getContext(), dbFile, headings, table, colNames, where);
		}
	}

	String displayName;
	String packageName;
	String dbFile;
	DBQuery dbQuery;
	String[] cleanQueries;

	public SimpleDatabaseItem(Category parent, String displayName, String packageName, String dbFileRelative, DBQuery dbQuery, String[] cleanQueries)
	{
		super(parent);

		this.displayName = displayName;
		this.packageName = packageName;
		this.dbFile = getDataPath() + dbFileRelative;
		this.dbQuery = dbQuery;
		this.cleanQueries = cleanQueries;
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
	public boolean clean()
	{
		if (!RootTools.exists(dbFile))
		{
			return true;
		}

		return DBHelper.updateDatabase(Globals.getContext(), dbFile, cleanQueries);
	}
}
