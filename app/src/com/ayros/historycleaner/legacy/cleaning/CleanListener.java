package com.ayros.historycleaner.legacy.cleaning;

import com.ayros.historycleaner.legacy.cleaning.Cleaner.CleanResults;

public interface CleanListener
{
	public void progressChanged(Cleaner.CleanProgressEvent cpe);
	public void cleaningComplete(CleanResults results);
}
