package com.ayros.historycleaner;

import java.util.concurrent.CountDownLatch;

import android.app.Activity;

public abstract class UIRunner
{
	private class UIRunnable implements Runnable
	{
		@Override
		public void run()
		{
			action(data);
			
			if (latch != null)
			{
				latch.countDown();
			}
		}
	}
	
	CountDownLatch latch = null;
	Object data = null;
	
	Activity activity;
	
	public UIRunner(Activity a, Object data)
	{
		this.activity = a;
		this.data = data;
	}
	
	public void run()
	{
		latch = null;
		activity.runOnUiThread(new UIRunnable());
	}
	
	public void runAndWait()
	{
		latch = new CountDownLatch(1);
		activity.runOnUiThread(new UIRunnable());
		try
		{
			latch.await();
		}
		catch (InterruptedException e)
		{
		}
	}
	
	public abstract void action(Object data);
}
