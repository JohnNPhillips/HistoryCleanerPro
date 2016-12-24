package com.ayros.historycleaner.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.helpers.database.QueryResult;
import com.ayros.historycleaner.helpers.database.RootDatabase;
import com.google.common.collect.Lists;

public class DBHelper
{
	public static List<String[]> queryDatabase(String dbPath, String[] headings, String table, String[] cols, String where)
	{
		RootDatabase db = new RootDatabase(dbPath, Globals.getRootShell());
		List<String[]> queryOutput = new ArrayList<>();

		QueryResult res;
		try
		{
			res = db.select(table, Lists.newArrayList(cols), where);
		}
		catch (IOException e)
		{
			queryOutput.add(new String[]{"Error: Could not read from database (IOException)"});
			queryOutput.add(new String[]{});
			return queryOutput;
		}

		queryOutput.add(headings);
		for (List<String> rowValues : res.getRowList())
		{
			queryOutput.add(rowValues.toArray(new String[0]));
		}

		return queryOutput;
	}

	public static boolean updateDatabase(String dbPath, String[] queries) throws IOException
	{
		return updateDatabase(dbPath, Lists.newArrayList(queries));
	}

	public static boolean updateDatabase(String dbPath, List<String> queries) throws IOException
	{
		RootDatabase db = new RootDatabase(dbPath, Globals.getRootShell());

		for (String query : queries)
		{
			if (!db.runCommand(query))
			{
				Logger.errorST("Could not execute query {" + query + "} on database {" + dbPath + "}");
				return false;
			}
		}

		return true;
	}
}
