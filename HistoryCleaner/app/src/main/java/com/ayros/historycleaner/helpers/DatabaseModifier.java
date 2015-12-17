package com.ayros.historycleaner.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stericson.RootTools.RootTools;

public class DatabaseModifier
{
	private Context context;
	private String path;
	private String tempFile;
	private boolean open = false;
	private String[] suffixes = new String[] { "", "-journal", "-shm", "-wal" };

	public DatabaseModifier(Context c, String path)
	{
		this.context = c;
		this.path = path;
	}

	public boolean open()
	{
		if (open)
		{
			return true;
		}

		if (!RootTools.exists(path))
		{
			Logger.errorST("Database doesn't exist: " + path);
			return false;
		}

		String fn = "_db_" + ((int)(Math.random() * 900000) + 100000) + ".db";
		tempFile = context.getCacheDir().getAbsolutePath() + "/" + fn;

		for (String s : suffixes)
		{
			if (RootTools.exists(path + s))
			{
				RootTools.copyFile(path + s, tempFile + s, false, true);

				if (RootHelper.runAndWait("chmod 777 " + tempFile + s) == null)
				{
					Logger.errorST("Could not chmod database file " + tempFile + s);
					return false;
				}
			}
		}

		return (open = true);
	}

	public boolean exec(String query)
	{
		if (!open)
		{
			return false;
		}

		SQLiteDatabase db = null;
		try
		{
			db = SQLiteDatabase.openDatabase(tempFile, null, SQLiteDatabase.OPEN_READWRITE);
			db.execSQL(query);
			db.close();

			return true;
		}
		catch (Exception e)
		{
			Logger.errorST("Could not execute query on database: " + path, e);
			
			if (db != null)
			{
				db.close();
			}

			return false;
		}
	}

	public List<String[]> query(String table, String[] columns, String where)
	{
		if (!open)
		{
			return null;
		}

		SQLiteDatabase db = null;
		Cursor cursor = null;
		try
		{
			db = SQLiteDatabase.openDatabase(tempFile, null, SQLiteDatabase.OPEN_READWRITE);
			cursor = db.query(table, columns, where, null, null, null, null);

			List<String[]> rows = new ArrayList<String[]>();
			if (cursor.moveToFirst())
			{
				do
				{
					int cols = cursor.getColumnCount();
					String[] row = new String[cols];

					for (int i = 0; i < cols; i++)
					{
						row[i] = cursor.isNull(i) ? "" : cursor.getString(i);
					}

					rows.add(row);
				}
				while (cursor.moveToNext());

				return rows;
			}
			else
			{
				return new ArrayList<String[]>();
			}
		}
		catch (Exception e)
		{
			Logger.errorST("Problem querying database: " + path, e);
			return null;
		}
		finally
		{
			if (db != null)
			{
				db.close();
			}
			if (cursor != null)
			{
				cursor.close();
			}
		}
	}

	public void clean()
	{
		for (String s : suffixes)
		{
			File tf = new File(tempFile + s);
			if (tf.exists())
			{
				tf.delete();
			}
		}

		open = false;
	}

	public boolean saveChanges()
	{
		if (!open)
		{
			return true;
		}

		for (String s : suffixes)
		{
			if (RootTools.exists(tempFile + s))
			{
				// Ensure any newly created files from editing database have original user as the owner
				if (!RootTools.exists(path + s) && !s.equals(""))
				{
					if (!RootTools.copyFile(path, path + s, false, true))
					{
						Logger.errorST("Could not copy dummy file when trying to save database: " + path);
						return false;
					}
				}

				// Overwrite the files with cat to preserve file ownership
				if (RootHelper.runAndWait("cat " + tempFile + s + " > " + path + s) == null)
				{
					Logger.errorST("Error: Could not pipe modified database back to original location");
					return false;
				}
			}
			else if (!RootHelper.deleteFileOrFolder(path + s, false))
			{
				Logger.errorST("Could not delete file: " + path + s);
				
				return false;
			}
		}

		return true;
	}
}
