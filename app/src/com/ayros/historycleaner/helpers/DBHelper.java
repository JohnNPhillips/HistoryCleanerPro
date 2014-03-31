package com.ayros.historycleaner.helpers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class DBHelper
{
	public static List<String[]> queryDatabase(Context c, String db, String[] headings, String table, String[] cols, String where)
	{
		List<String[]> rows = new ArrayList<String[]>();
		rows.add(headings);

		DatabaseModifier dm = new DatabaseModifier(c, db);
		if (!dm.open())
		{
			dm.clean();
			rows.clear();
			rows.add(new String[] { "Error: Could not read from database (Error = 0)" });
			rows.add(new String[] {});
			return rows;
		}

		List<String[]> queryRows = dm.query(table, cols, where);
		dm.clean();
		if (queryRows == null)
		{
			rows.clear();
			rows.add(new String[] { "Error: Could not read from database (Error = 1)" });
			rows.add(new String[] {});
			return rows;
		}

		for (String[] row : queryRows)
		{
			rows.add(row);
		}

		return rows;
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
