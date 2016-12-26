package com.ayros.historycleaner.cleaning;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.ayros.historycleaner.UIRunner;
import com.ayros.historycleaner.helpers.Logger;
import com.stericson.RootTools.RootTools;

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
		public CleanItemStub item = null;
		public int itemIndex = 0;
		public int totalItems = 0;
		
		public CleanProgressEvent(CleanItemStub item, int itemIndex, int totalItems)
		{
			this.item = item;
			this.itemIndex = itemIndex;
			this.totalItems = totalItems;
		}
	}
	
	private List<CleanItemStub> cleanList;
	private boolean cleanResult;
	
	public Cleaner(List<CleanItemStub> itemList)
	{
		cleanList = new ArrayList<CleanItemStub>(itemList);
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
				
				if (isRootRequired() && !RootTools.isAccessGiven())
				{
					Logger.errorST("Root shell isn't started, cannot clean items");
					
					for (CleanItemStub item : cleanList)
					{
						results.failure.add(item.getUniqueName());
					}
				}
				else
				// If root shell is needed, it is started
				{
					for (int i = 0; i < cleanList.size(); i++)
					{
						CleanItemStub item = cleanList.get(i);
						
						Logger.debug("About to clean item: " + item.getUniqueName());
						
						// Send progressChanged message
						if (cl != null)
						{
							CleanProgressEvent cpe = new CleanProgressEvent(item, i, cleanList.size());
							
							// If activity is provided, run event on UI thread
							if (activity != null)
							{
								new UIRunner<CleanProgressEvent>(activity, cpe)
								{
									@Override
									public void action(CleanProgressEvent event)
									{
										cl.progressChanged(event);
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
							new UIRunner<CleanItemStub>(activity, item)
							{
								@Override
								public void action(CleanItemStub item)
								{
									try
									{
										item.clean();
										Cleaner.this.cleanResult = true;
									}
									catch (Exception e)
									{
										Logger.errorST("Exception cleaning item " + item.getUniqueName(), e);
										Cleaner.this.cleanResult = false;
									}
								}
							}.runAndWait();
						}
						else
						{
							try
							{
								item.clean();
								cleanResult = true;
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
						new UIRunner<CleanResults>(activity, results)
						{
							@Override
							public void action(CleanResults results)
							{
								cl.cleaningComplete(results);
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
		
		if (isRootRequired() && !RootTools.isAccessGiven())
		{
			Logger.errorST("Root shell isn't started, cannot clean items");
			
			for (CleanItemStub item : cleanList)
			{
				results.failure.add(item.getUniqueName());
			}
		}
		else
		{
			for (int i = 0; i < cleanList.size(); i++)
			{
				CleanItemStub item = cleanList.get(i);
				
				if (cl != null)
				{
					cl.progressChanged(new CleanProgressEvent(item, i, cleanList.size()));
				}
				
				try
				{
					item.clean();
					results.success.add(item.getUniqueName());
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
		for (CleanItemStub item : cleanList)
		{
			if (item.isRootRequired())
			{
				return true;
			}
		}
		
		return false;
	}
}