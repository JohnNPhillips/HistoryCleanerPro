package com.ayros.historycleaner.cleaning;

import com.ayros.historycleaner.cleaning.Cleaner.CleanResults;

public interface CleanListener
{
	public void progressChanged(Cleaner.CleanProgressEvent cpe);
	public void cleaningComplete(CleanResults results);
}
