package com.ayros.historycleaner.cleaning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ayros.historycleaner.UIRunner;
import com.ayros.historycleaner.helpers.Logger;
import com.google.common.base.Optional;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootTools.RootTools;

public class Cleaner
{
	public class CleanResults
	{
		private final List<CleanItem> successes = new ArrayList<>();
		private final Map<CleanItem, Exception> failures = new HashMap<>();

		public void addSuccess(CleanItem item)
		{
			Logger.debug("Cleaned Successfully: " + item.getUniqueName());
			successes.add(item);
		}

		public void addFailure(CleanItem item, Exception e)
		{
			Logger.debug("Cleaning failure: " + item.getUniqueName());
			failures.put(item, e);
		}

		public boolean hasFailures()
		{
			return !failures.isEmpty();
		}

		public String getErrorReport()
		{
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<CleanItem, Exception> entry : failures.entrySet())
			{
				sb.append("Item Name: " + entry.getKey().getUniqueName() + "\n");
				sb.append("Stack Trace: " + Log.getStackTraceString(entry.getValue()));
				sb.append("\n\n");
			}

			return sb.toString();
		}
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			if (failures.size() == 0)
			{
				sb.append("Cleaned all " + successes.size() + " items successfully!");
			}
			else
			{
				sb.append("Cleaned " + successes.size() + " items successfully\n");
				sb.append("Encountered errors with " + failures.size() + " items:");
				for (CleanItem failureItem : failures.keySet())
				{
					sb.append("\n" + failureItem.getUniqueName());
				}
			}
			
			return sb.toString();
		}
	}
	
	public class CleanProgressEvent
	{
		public CleanItem item = null;
		public int itemIndex = 0;
		public int totalItems = 0;
		
		public CleanProgressEvent(CleanItem item, int itemIndex, int totalItems)
		{
			this.item = item;
			this.itemIndex = itemIndex;
			this.totalItems = totalItems;
		}
	}
	
	private List<CleanItem> cleanList;

	public Cleaner(List<CleanItem> itemList)
	{
		cleanList = new ArrayList<>(itemList);
	}

	/**
	 * Cleans all items asynchronously. If an activity is provided, all cleaning
	 * and events will be ran on its UI thread. Also, if a root shell is
	 * required, it must already be running.
	 * 
	 * @param cl
	 * @return
	 */
	public void cleanAsync(final Optional<? extends Activity> activity, final Optional<CleanListener> cl)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				final UIRunner uiRunner = new UIRunner(activity);
				final CleanResults results = new CleanResults();

				if (isRootRequired() && !RootTools.isAccessGiven())
				{
					Logger.errorST("Root shell isn't started, cannot clean items");
					
					for (CleanItem item : cleanList)
					{
						results.addFailure(item, new RootDeniedException("Root shell not started"));
					}
				}
				else
				// If root shell is needed, it is started
				{
					for (int i = 0; i < cleanList.size(); i++)
					{
						final CleanItem item = cleanList.get(i);
						
						Logger.debug("About to clean item: " + item.getUniqueName());

						// Send progressChanged message
						if (cl.isPresent())
						{
							final CleanProgressEvent progressEvent = new CleanProgressEvent(item, i, cleanList.size());
							uiRunner.runAndWait(new Runnable()
							{
								@Override
								public void run()
								{
									cl.get().progressChanged(progressEvent);
								}
							});
						}
						
						// Clean item running on UI thread if necessary
						uiRunner.runAndWait(new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									item.clean();
									results.addSuccess(item);
								}
								catch (Exception e)
								{
									Logger.errorST("Exception cleaning item " + item.getUniqueName(), e);
									results.addFailure(item, e);
								}
							}
						});
						item.postClean();
					}
				}
				
				// Send cleaningComplete event
				if (cl.isPresent())
				{
					uiRunner.runAndWait(new Runnable()
					{
						@Override
						public void run()
						{
							cl.get().cleaningComplete(results);
						}
					});
				}
			}
		}.start();
	}

	/**
	 * Cleans all items and sends all events on the current thread. Assumes
	 * current thread is a UI thread if it is needed for any items. Also, if a
	 * root shell is required, it must already be running.
	 * 
	 * @param cl
	 * @return
	 */
	public CleanResults cleanNow(Optional<CleanListener> cl)
	{
		CleanResults results = new CleanResults();
		
		if (isRootRequired() && !RootTools.isAccessGiven())
		{
			Logger.errorST("Root shell isn't started, cannot clean items");
			
			for (CleanItem item : cleanList)
			{
				results.addFailure(item, new RootDeniedException("Root shell not running"));
			}
		}
		else
		{
			for (int i = 0; i < cleanList.size(); i++)
			{
				CleanItem item = cleanList.get(i);
				
				if (cl.isPresent())
				{
					cl.get().progressChanged(new CleanProgressEvent(item, i, cleanList.size()));
				}
				
				try
				{
					item.clean();
					results.addSuccess(item);
				}
				catch (Exception e)
				{
					Logger.errorST("Exception cleaning item " + item.getUniqueName(), e);
					results.addFailure(item, e);
				}
				
				item.postClean();
			}
		}

		if (cl.isPresent())
		{
			cl.get().cleaningComplete(results);
		}
		
		return results;
	}

	public boolean isRootRequired()
	{
		for (CleanItem item : cleanList)
		{
			if (item.isRootRequired())
			{
				return true;
			}
		}
		
		return false;
	}
}