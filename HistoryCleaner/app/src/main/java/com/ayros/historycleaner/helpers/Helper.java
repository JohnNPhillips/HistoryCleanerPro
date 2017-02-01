package com.ayros.historycleaner.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.R;

public class Helper
{
	public static final String CHARSET_ALPHA_LOWER = "abcdefghijklmnopqrstuvwxyz";

	static List<String> packageNames = null;
	static List<PackageInfo> packageList = null;

	public static boolean isPackageInstalled(String name)
	{
		if (packageNames == null)
		{
			loadPackages(true);
		}

		return packageNames.contains(name);
	}

	public static Drawable getPackageIcon(String name)
	{
		try
		{
			return Globals.getContext().getPackageManager().getApplicationIcon(name);
		}
		catch (Exception e)
		{
			return Globals.getContext().getResources().getDrawable(R.drawable.default_icon);
		}
	}

	private static void loadPackages(boolean force)
	{
		if (packageList == null || force == true)
		{
			packageNames = new ArrayList<String>();

			packageList = Globals.getContext().getPackageManager().getInstalledPackages(0);
			for (PackageInfo pi : packageList)
			{
				packageNames.add(pi.packageName);
			}
		}
	}

	public static String randomString(int len)
	{
		return randomString(len, CHARSET_ALPHA_LOWER);
	}

	public static String randomString(int len, String charset)
	{
		String out = "";

		for (int i = 0; i < len; i++)
		{
			out += charset.charAt((int) (Math.random() * charset.length()));
		}

		return out;
	}

	public static String urlEncode(String text)
	{
		try
		{
			return URLEncoder.encode(text, "UTF-8");
		}
		catch (Exception e)
		{
			return "";
		}
	}

	public static String urlDecode(String text)
	{
		try
		{
			return URLDecoder.decode(text, "UTF-8");
		}
		catch (Exception e)
		{
			return "";
		}
	}

	public static String convertStreamToString(InputStream is)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line).append("\n");
			}
			is.close();

			return sb.toString();
		}
		catch (Exception e)
		{
			Logger.errorST("Could not convert stream to string", e);
			return null;
		}
	}

	public static String getFileContents(String filePath)
	{
		try
		{
			File fl = new File(filePath);
			FileInputStream fin = new FileInputStream(fl);

			return convertStreamToString(fin);
		}
		catch (Exception e)
		{
			Logger.errorST("Could not read file contents: " + filePath, e);
			return null;
		}
	}

	public static List<String[]> cursorToDataView(Cursor c, @Nullable Set<Integer> dateColumns)
	{
		List<String[]> rows = new ArrayList<>();
		String[] headers = new String[c.getColumnCount()];
		for (int i = 0; i < headers.length; i++)
		{
			headers[i] = c.getColumnName(i);
		}
		rows.add(headers);

		if (c.moveToFirst())
		{
			while (!c.isAfterLast())
			{
				String[] row = new String[c.getColumnCount()];
				for (int i = 0; i < row.length; i++)
				{
					row[i] = c.getString(i);
					try
					{
						if (dateColumns.contains(i))
						{
							row[i] = new Date(Long.parseLong(row[i])).toString();
						}
					}
					catch (Exception e) {}
				}
				rows.add(row);
				c.moveToNext();
			}
		}
		c.close();

		return rows;
	}
}
