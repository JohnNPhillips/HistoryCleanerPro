package com.ayros.historycleaner;

import java.util.concurrent.CountDownLatch;

import android.app.Activity;

import com.ayros.historycleaner.helpers.Logger;
import com.google.common.base.Optional;

public class UIRunner
{
	private final Optional<? extends Activity> activity;

	public UIRunner(Optional<? extends Activity> a)
	{
		this.activity = a;
	}

	public void runAndWait(final Runnable runnable)
	{
		if (activity.isPresent())
		{
			final CountDownLatch latch = new CountDownLatch(1);
			activity.get().runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					runnable.run();
					latch.countDown();
				}
			});
			try
			{
				latch.await();
			}
			catch (InterruptedException e)
			{
				Logger.errorST("Interrupted", e);
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		}
		else
		{
			runnable.run();
		}
	}
}
