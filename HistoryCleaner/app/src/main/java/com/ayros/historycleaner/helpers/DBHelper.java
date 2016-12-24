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

	public static boolean updateDatabase(Context c, String db, String[] queries)
	{
		DatabaseModifier dm = new DatabaseModifier(c, db);
		if (!dm.open())
		{
			Logger.errorST("Could not update database {" + db + "}");
			dm.clean();
			return false;
		}

		for (String q : queries)
		{
			if (!dm.exec(q))
			{
				Logger.errorST("Could not execute query {" + q + "} on database {" + db + "}");
				dm.clean();
				return false;
			}
		}

		boolean result = dm.saveChanges();
		dm.clean();

		return result;
	}
}
