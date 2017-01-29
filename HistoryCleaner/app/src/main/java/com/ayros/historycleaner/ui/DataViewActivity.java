package com.ayros.historycleaner.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.R;
import com.ayros.historycleaner.helpers.Logger;
import com.stericson.RootTools.RootTools;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Shell;

public class DataViewActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_view);

		if (Globals.itemDataView.isRootRequired())
		{
			if (!RootTools.isRootAvailable())
			{
				Logger.errorST("Root is required to view item data, but root access is not available");
				displayMessageInTable("Error: It does not appear that your phone is rooted. You need root access to use this feature.");
				return;
			}

			if (!Shell.isRootShellOpen())
			{
				try
				{
					Shell.startRootShell();
				}
				catch (RootDeniedException rde)
				{
					Logger.errorST("Could not attain required root access to view item data");
					displayMessageInTable("Error: Could not attain root access! It is required to view this item's data.");
					return;
				}
				catch (Exception e)
				{
					Logger.errorST("Error when trying to start root shell");
					displayMessageInTable("Error: There was a problem when trying to gain root access");
					return;
				}
			}
		}

		List<String[]> data;
		try {
			data = Globals.itemDataView.getSavedData();
		}
		catch (UnsupportedOperationException e)
		{
			displayMessageInTable("Viewing data is not supported for this item.");
			return;
		}
		catch (IOException e)
		{
			Logger.errorST("Couldn't get saved item for item " + Globals.itemDataView.getUniqueName(), e);
			String emailHref = getMailToHref(
					"Error getting data for: " + Globals.itemDataView.getUniqueName(),
					"Stack Trace: " + Log.getStackTraceString(e));
			displayHtmlMessage(String.format("Couldn't get saved data for %s. " +
					"Most likely the app was updated in a way that changed how they store their data. " +
					"If you send me an email at ayros@email.com, I should be able to fix it. " +
					"Clicking <a href='%s'>this link</a> will allow you to send me an email with all the necessary information to debug this.",
					Globals.itemDataView.getUniqueName(),
					emailHref));
			return;
		}

		if (data.size() <= 1)
		{
			displayMessageInTable("There are no items.");
		}
		else
		{
			setTableData(data);
		}
	}

	private String getMailToHref(String subject, String body)
	{
		StringBuilder href = new StringBuilder();
		href.append("mailto:ayros@email.com");
		href.append("?subject=" + TextUtils.htmlEncode(subject));
		href.append("&body=" + TextUtils.htmlEncode(body));

		return href.toString();
	}

	private void displayHtmlMessage(String message)
	{
		setPageBody("<p>" + message + "</p>");
	}

	private void displayMessageInTable(String message)
	{
		List<String[]> rows = new ArrayList<String[]>();
		rows.add(new String[] { message });
		setTableData(rows);
	}

	private void appendCell(StringBuilder html, String data, boolean heading, boolean even)
	{
		String extra = (even && !heading) ? " class='evenRow'" : "";

		html.append((heading ? "<th" + extra + ">" : "<td nowrap" + extra + ">") + TextUtils.htmlEncode(data) + (heading ? "</th>" : "</td>"));
	}

	private void setTableData(List<String[]> data)
	{
		StringBuilder table = new StringBuilder();

		table.append("<table>");
		for (int i = 0; i < data.size(); i++)
		{
			table.append("<tr>");

			for (String text : data.get(i))
			{
				appendCell(table, text, i == 0, i % 2 == 0);
			}

			table.append("</tr>");
		}
		table.append("</table>");

		setPageBody(table.toString());
	}

	private void setPageBody(String body)
	{
		StringBuilder html = new StringBuilder();

		html.append("<html><head>");
		html.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />");

		html.append("<style type='text/css'>");
		html.append("th { text-align:left; background-color: #999999; }");
		html.append(".evenRow { background-color: #E5E5E5; } ");
		html.append("table, th, td { border: 1px solid black; }");
		html.append("</style>");
		html.append("</head>");

		html.append("<body>" + body + "</body>");

		html.append("</html>");

		WebView wv = (WebView)findViewById(R.id.data_webview);
		wv.loadData(html.toString(), "text/html", "utf-8");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
