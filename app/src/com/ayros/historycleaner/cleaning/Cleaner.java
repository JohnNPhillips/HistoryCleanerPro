package com.ayros.historycleaner.cleaning;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.ayros.historycleaner.UIRunner;
import com.ayros.historycleaner.helpers.Logger;
import com.stericson.RootTools.execution.Shell;

public class Cleaner
{
	public class CleanResults
	{
		public List<String> success = new ArrayList<String>();
		public List<String> failure = new ArrayList<String>();
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			if (failure.size() == 0)
			{
				sb.append("Cleaned all " + success.size() + " items successfully!");
			}
			else
			{
				sb.append("Cleaned " + success.size() + " items successfully\n");
				sb.append("Encountered errors with " + failure.size() + " items:");
				for (String itemName : failure)
				{
					sb.append("\n" + itemName);
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
	private boolean cleanResult;
	
	public Cleaner(List<CleanItem> itemList)
	{
		cleanList = new ArrayList<CleanItem>(itemList);
	}
	
	/**
	 * Cleans all items asynchronously. If an activity is provided, all cleaning
	 * and events will be ran on its UI thread. Also, if a root shell is
	 * required, it must already be running.
	 * 
	 * @param cl
	 * @return
	 */
	public void cleanAsync(final Activity activity, final CleanListener cl)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				CleanResults results = new CleanResults();
				
				if (isRootRequired() && !Shell.isRootShellOpen())
				{
					Logger.errorST("Root shell isn't started, cannot clean items");
					
					for (CleanItem item : cleanList)
					{
						results.failure.add(item.getUniqueName());
					}
				}
				else
				// If root shell is needed, it is started
				{
					for (int i = 0; i < cleanList.size(); i++)
					{
						CleanItem item = cleanList.get(i);
						
						Logger.debug("About to clean item: " + item.getUniqueName());
						
						// Send progressChanged message
						if (cl != null)
						{
							CleanProgressEvent cpe = new CleanProgressEvent(item, i, cleanList.size());
							
							// If activity is provided, run event on UI thread
							if (activity != null)
							{
								new UIRunner(activity, cpe)
								{
									@Override
									public void action(Object data)
									{
										cl.progressChanged((CleanProgressEvent)data);
									}
								}.runAndWait();
							}
							else
							{
								cl.progressChanged(cpe);
							}
						}
						
						// Clean item running on UI thread if necessary
						cleanResult = false;
						if (item.runOnUIThread() && activity != null)
						{
							new UIRunner(activity, item)
							{
								@Override
								public void action(Object data)
								{
									try
									{
										Cleaner.this.cleanResult = ((CleanItem)data).clean();
									}
									catch (Exception e)
									{
										Logger.errorST("Exception cleaning item " + ((CleanItem)data).getUniqueName(), e);
										Cleaner.this.cleanResult = false;
									}
								}
							}.runAndWait();
						}
						else
						{
							try
							{
								cleanResult = item.clean();
							}
							catch (Exception e)
							{
								Logger.errorST("Exception cleaning item " + item.getUniqueName(), e);
								cleanResult = false;
							}
						}
						item.postClean();
						
						// Add result to clean results
						if (cleanResult)
						{
							Logger.debug("Cleaned Successfully: " + item.getUniqueName());
							results.success.add(item.getUniqueName());
						}
						else
						{
							Logger.debug("Cleaning failure: " + item.getUniqueName());
							results.failure.add(item.getUniqueName());
						}
					}
				}
				
				// Send cleaningComplete event
				if (cl != null)
				{
					// If activity is provided, run event on UI thread
					if (activity != null)
					{
						new UIRunner(activity, results)
						{
							@Override
							public void action(Object data)
							{
								cl.cleaningComplete((CleanResults)data);
							}
						}.runAndWait();
					}
					else
					{
						cl.cleaningComplete(results);
					}
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
	public CleanResults cleanNow(CleanListener cl)
	{
		CleanResults results = new CleanResults();
		
		if (isRootRequired() && !Shell.isRootShellOpen())
		{
			Logger.errorST("Root shell isn't started, cannot clean items");
			
			for (CleanItem item : cleanList)
			{
				results.failure.add(item.getUniqueName());
			}
		}
		else
		{
			for (int i = 0; i < cleanList.size(); i++)
			{
				CleanItem item = cleanList.get(i);
				
				if (cl != null)
				{
					cl.progressChanged(new CleanProgressEvent(item, i, cleanList.size()));
				}
				
				try
				{
					if (item.clean())
					{
						results.success.add(item.getUniqueName());
					}
					else
					{
						results.failure.add(item.getUniqueName());
					}
				}
				catch (Exception e)
				{
					Logger.errorST("Exception cleaning item " + item.getUniqueName(), e);
					results.failure.add(item.getUniqueName());
				}
				
				item.postClean();
			}
		}
		
		if (cl != null)
		{
			cl.cleaningComplete(results);
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