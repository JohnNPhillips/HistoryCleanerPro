package com.ayros.historycleaner.helpers.database;

import com.ayros.historycleaner.helpers.RootHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.stericson.RootShell.execution.Shell;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RootDatabase
{
	String path;
	Shell shell;

	public RootDatabase(String path, Shell shell)
	{
		this.path = path;
		this.shell = shell;
	}

	public List<String> listTables() throws IOException
	{
		List<String> tables = Lists.newArrayList(executeSQLCommand(".tables").split("[ \t]"));
		for (int i = 0; i < tables.size(); i++)
		{
			tables.set(i, tables.get(i).trim());
		}
		tables.removeAll(Collections.singleton(""));
		return tables;
	}

	public QueryResult select(String table, List<String> columns) throws IOException
	{
		return select(table, columns, null);
	}

	public QueryResult select(String table, List<String> columns, String where) throws IOException
	{
		if (where == null)
		{
			where = "1";
		}
		String csvOutput = executeSQLCommand(String.format("SELECT %s FROM %s WHERE %s", Joiner.on(", ").join(columns), table, where), "-csv");
		return QueryResult.parseSqliteCSV(columns, csvOutput);
	}

	public boolean tableExists(String tableName) throws IOException
	{
		return listTables().contains(tableName);
	}

	private String executeSQLCommand(String command, String extraArgs) throws IOException
	{
		String rootCommand = String.format("sqlite3 %s %s \"%s\"", extraArgs, path, command);

		return RootHelper.runAndWait(rootCommand, shell).trim();
	}

	private String executeSQLCommand(String command) throws IOException
	{
		return executeSQLCommand(command, "");
	}
}