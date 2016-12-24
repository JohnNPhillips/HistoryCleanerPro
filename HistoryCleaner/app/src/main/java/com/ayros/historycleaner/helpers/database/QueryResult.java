package com.ayros.historycleaner.helpers.database;

import com.google.common.collect.ImmutableList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class QueryResult
{
	private final ImmutableList<String> columns;
	private final ImmutableList<CSVRecord> rows;

	private QueryResult(List<String> columns, List<CSVRecord> rows)
	{
		this.columns = ImmutableList.copyOf(columns);
		this.rows = ImmutableList.copyOf(rows);
	}

	public static QueryResult parseSqliteCSV(List<String> columns, String csv) throws IOException
	{
		CSVParser parser = new CSVParser(new StringReader(csv), CSVFormat.DEFAULT);
		List<CSVRecord> rows = parser.getRecords();
		for (CSVRecord rec : parser)
		{
			if (rec.size() != columns.size())
			{
				throw new IOException("CSV row contained invalid number of columns");
			}
		}

		return new QueryResult(columns, rows);
	}

	public List<String> getColumns()
	{
		return columns;
	}

	public List<List<String>> getRowList()
	{
		List<List<String>> rowList = new ArrayList<>();
		for (CSVRecord row : rows)
		{
			List<String> rowValues = new ArrayList<>();
			for (String val : row)
			{
				rowValues.add(val);
			}
			rowList.add(rowValues);
			row.toMap();
		}

		return rowList;
	}
}