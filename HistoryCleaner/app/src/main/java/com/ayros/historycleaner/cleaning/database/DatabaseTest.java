package com.ayros.historycleaner.cleaning.database;

import com.ayros.historycleaner.helpers.database.RootDatabase;

import java.io.IOException;

public interface DatabaseTest
{
	boolean passes(RootDatabase db) throws IOException;
}
