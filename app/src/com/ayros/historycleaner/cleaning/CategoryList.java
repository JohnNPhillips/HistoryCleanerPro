package com.ayros.historycleaner.cleaning;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ayros.historycleaner.R;
import com.ayros.historycleaner.cleaning.SimpleDatabaseItem.DBQuery;
import com.ayros.historycleaner.cleaning.items._System_BrowserHistory;
import com.ayros.historycleaner.cleaning.items._System_Cache;
import com.ayros.historycleaner.cleaning.items._System_Calls;
import com.ayros.historycleaner.cleaning.items._System_Clipboard;
import com.ayros.historycleaner.cleaning.items._System_FrequentContacts;
import com.ayros.historycleaner.cleaning.items._System_SMS;

public class CategoryList
{
	private List<Category> cats;
	
	public CategoryList()
	{
		cats = new ArrayList<Category>();
		
		Category cat;
		
		// ------------------
		// ----- System -----
		// ------------------
		cat = new Category("System");
		cat.addItem(new _System_Cache(cat));
		cat.addItem(new _System_BrowserHistory(cat));
		cat.addItem(new _System_Clipboard(cat));
		cat.addItem(new _System_FrequentContacts(cat));
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Google Play History", "com.android.vending", "/databases/suggestions.db",
			new DBQuery
			(
				new String[] { "Search Term" },
				"suggestions",
				new String[] { "query" }
			),
			new String[] { "DELETE FROM suggestions;" }
		));
		cat.addItem(new _System_Calls(cat, "Outgoing Calls", CallLog.Calls.OUTGOING_TYPE));
		cat.addItem(new _System_Calls(cat, "Incoming Calls", CallLog.Calls.INCOMING_TYPE));
		cat.addItem(new _System_Calls(cat, "Missed Calls", CallLog.Calls.MISSED_TYPE));
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Settings Searches", "com.android.settings", "/databases/search_index.db",
			new DBQuery
			(
				new String[] { "Search Term", "Timestamp" },
				"saved_queries",
				new String[] { "query", "timestamp" }
			),
			new String[] { "DELETE FROM saved_queries;" }
		)
		{
			@Override
			public boolean isApplicable()
			{
				return Build.VERSION.SDK_INT >= 21; // Lollipop
			}
		});
		cat.addItem(new _System_SMS(cat));
		cats.add(cat);
		
		// ------------------------
		// ----- Adobe Reader -----
		// ------------------------
		cat = new Category("Adobe Reader");
		cat.addItem(new com.ayros.historycleaner.cleaning.items._AdobeReader_Recent(cat));
		cats.add(cat);
		
		// ------------------
		// ----- Amazon -----
		// ------------------
		cat = new Category("Amazon");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Recently Viewed", "com.amazon.mShop.android", "/databases/history.db",
			new DBQuery
			(
				new String[] { "Type", "Amazon Product ID" },
				"history",
				new String[] { "type", "asin" }
			),
			new String[] { "DELETE FROM history;" }
		));
		cats.add(cat);
		
		// ------------------
		// ----- Chrome -----
		// ------------------
		cat = new Category("Chrome");

		cat.addItem(new SimpleFileItem(cat, "Cache", "com.android.chrome", "/cache/*", true));

		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Cookies", "com.android.chrome", "/app_chrome/Default/Cookies",
			new DBQuery
			(
				new String[] { "Domain", "Cookie Name", "Cookie Value" },
				"cookies",
				new String[] { "host_key", "name", "value" }
			),
			new String[] { "DELETE FROM cookies;" }
		));

		cat.addItem(new SimpleDatabaseItem
		(
			cat, "History", "com.android.chrome", "/app_chrome/Default/History",
			new DBQuery
			(
				new String[] { "Title", "Visit Count", "URL" },
				"urls",
				new String[] { "title", "visit_count", "url" }
			),
			new String[]
			{
				"DELETE FROM visits;",
				"DELETE FROM visit_source;",
				"DELETE FROM segments;",
				"DELETE FROM segment_usage;",
				"DELETE FROM urls;",
				"DELETE FROM keyword_search_terms;",
			}
		));

		cat.addItem(new SimpleFileItem(cat, "Local Storage", "com.android.chrome", "/app_chrome/Default/Local Storage/*", true));

		cat.addItem(new SimpleFileItem(cat, "Open Tabs", "com.android.chrome", "/app_tabs/*", true));

		cats.add(cat);

		// -----------------------
		// ----- Chrome Beta -----
		// -----------------------

		cat = new Category("Chrome Beta");

		cat.addItem(new SimpleFileItem(cat, "Cache", "com.chrome.beta", "/cache/*", true));

		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Cookies", "com.chrome.beta", "/app_chrome/Default/Cookies",
			new DBQuery
			(
				new String[] { "Domain", "Cookie Name", "Cookie Value" },
				"cookies",
				new String[] { "host_key", "name", "value" }
			),
			new String[] { "DELETE FROM cookies;" }
		));

		cat.addItem(new SimpleDatabaseItem
		(
			cat, "History", "com.chrome.beta", "/app_chrome/Default/History",
			new DBQuery
			(
				new String[] { "Title", "Visit Count", "URL" },
				"urls",
				new String[] { "title", "visit_count", "url" }
			),
			new String[]
			{
				"DELETE FROM visits;",
				"DELETE FROM visit_source;",
				"DELETE FROM segments;",
				"DELETE FROM segment_usage;",
				"DELETE FROM urls;",
				"DELETE FROM keyword_search_terms;",
			}
		));

		cat.addItem(new SimpleFileItem(cat, "Local Storage", "com.chrome.beta", "/app_chrome/Default/Local Storage/*", true));

		cat.addItem(new SimpleFileItem(cat, "Open Tabs", "com.chrome.beta", "/app_tabs/*", true));

		cats.add(cat);

		// -------------------
		// ----- Clipper -----
		// -------------------
		cat = new Category("Clipper");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Saved Clipboards", "org.rojekti.clipper", "/databases/Clipper2.sqlite3",
			new DBQuery
			(
				new String[] { "Text" },
				"clippings",
				new String[] { "contents" },
				"list_id = 1"
			),
			new String[] { "DELETE FROM clippings WHERE list_id = 1;" }
		));
		cats.add(cat);

		// --------------------
		// ----- Clipper+ -----
		// --------------------
		cat = new Category("Clipper Plus");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Saved Clipboards", "fi.rojekti.clipper", "/databases/Clipper2.sqlite3",
			new DBQuery
			(
				new String[] { "Text" },
				"clippings",
				new String[] { "contents" },
				"list_id = 1"
			),
			new String[] { "DELETE FROM clippings WHERE list_id = 1;" }
		));
		cats.add(cat);

		// --------------------------
		// ----- Dictionary.com -----
		// --------------------------
		cat = new Category("Dictionary.com");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Search History", "com.dictionary", "/databases/DictionaryDB.sqlite3",
			new DBQuery
			(
				new String[] { "Word" },
				"history",
				new String[] { "RecentName" }
			),
			new String[] { "DELETE FROM Recents;" }
		));
		cats.add(cat);
		
		// ---------------------------
		// ----- Dolphin Browser -----
		// ---------------------------
		cat = new Category("Dolphin Browser");
		
		String[] dolphinCacheFiles = new String[]
		{
			"/cache/*",
			"/databases/dolphin_webviewCache.db",
			"/databases/dolphin_webviewCache.db-journal",
			"/app_favicons/*"
		};
		cat.addItem(new SimpleFileItem(cat, "Cache", "mobi.mgeek.TunnyBrowser", dolphinCacheFiles, true));
		
		cat.addItem(new SimpleFileItem(cat, "Cookies","mobi.mgeek.TunnyBrowser", new String[] {"/app_webview/Cookies", "/app_webview/Cookies-journal"}, true));
		
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Downloads", "mobi.mgeek.TunnyBrowser", "/databases/downloads.db",
			new DBQuery
			(
				new String[] { "File Path", "URL" },
				"downloads",
				new String[] { "_data", "uri" }
			),
			new String[]
			{
				"UPDATE sqlite_sequence SET seq='0' WHERE name='downloads';",
				"DELETE FROM downloads;"
			}
		));
		
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "History", "mobi.mgeek.TunnyBrowser", "/databases/browser.db",
			new DBQuery
			(
				new String[] { "Title", "URL" },
				"history",
				new String[] { "title", "url" }
			),
			new String[]
			{
				"UPDATE sqlite_sequence SET seq='0' WHERE name='history';",
				"DELETE FROM history;"
			}
		));
		
		String[] dolphinLS = new String[] { "/app_databases/localstorage/*", "/app_databases/localstorage_jetpack/*", "/app_webview/Local Storage/*" };
		cat.addItem(new SimpleFileItem(cat, "Local Storage", "mobi.mgeek.TunnyBrowser", dolphinLS, true));
		
		cat.addItem(new SimpleFileItem(cat, "Open Tabs", "mobi.mgeek.TunnyBrowser", "/app_tabstate/*", true));
		
		cats.add(cat);
		
		// --------------------------------
		// ----- Dolphin Browser Mini -----
		// --------------------------------
		cat = new Category("Dolphin Browser Mini");
		cat.addItem(new SimpleFileItem(cat, "Cache", "com.dolphin.browser", "/cache/*", true));
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Cookies", "com.dolphin.browser", "/databases/webviewCookiesChromium.db",
			new DBQuery
			(
				new String[] { "Domain", " Cookie Name", "Cookie Value" },
				"cookies",
				new String[] { "host_key", "name", "value" }
			),
			new String[] { "DELETE FROM cookies;" }
		));
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "History", "com.dolphin.browser", "/databases/bookmark.db",
			new DBQuery
			(
				new String[] { "Title", "URL" },
				"bookmarks",
				new String[] { "title", "url" },
				"type = '0'"
			),
			new String[] { "DELETE FROM bookmarks WHERE type = '0';" }
		));
		cat.addItem(new SimpleFileItem(cat, "Local Storage", "com.dolphin.browser", "/app_databases/localstorage/*", true));
		
		cats.add(cat);
		
		// ----------------
		// ----- eBay -----
		// ----------------
		cat = new Category("eBay (Official)");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Search History", "com.ebay.mobile", "/databases/suggestions.db",
			new DBQuery
			(
				new String[] { "Search" },
				"suggestions",
				new String[] { "query" }
			),
			new String[] { "DELETE FROM suggestions;" }
		));
		cats.add(cat);
		

		// -------------------
		// ----- Firefox -----
		// -------------------
		cat = new Category("Firefox");
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._Firefox_Cache(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._Firefox_Cookies(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._Firefox_History(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._Firefox_LocalStorage(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._Firefox_OpenTabs(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._Firefox_SearchWidget(cat));
		cats.add(cat);

		// ------------------------
		// ----- Firefox Beta -----
		// ------------------------
		cat = new Category("Firefox Beta");
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxBeta_Cache(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxBeta_Cookies(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxBeta_History(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxBeta_LocalStorage(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxBeta_OpenTabs(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxBeta_SearchWidget(cat));
		cats.add(cat);

		// ---------------------------
		// ----- Firefox Nightly -----
		// ---------------------------
		cat = new Category("Firefox Nightly");
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxNightly_Cache(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxNightly_Cookies(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxNightly_History(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxNightly_LocalStorage(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxNightly_OpenTabs(cat));
		cat.addItem(new com.ayros.historycleaner.cleaning.items.firefox._FirefoxNightly_SearchWidget(cat));
		cats.add(cat);

		// -----------------
		// ----- Gmail -----
		// -----------------
		cat = new Category("Gmail");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Search History", "com.google.android.gm", "/databases/suggestions.db",
			new DBQuery
			(
				new String[] { "Suggestion" },
				"suggestions",
				new String[] { "query" }
			),
			new String[] { "DELETE FROM suggestions;" }
		));
		cats.add(cat);
		
		// ---------------------------
		// ----- Google Calendar -----
		// ---------------------------
		cat = new Category("Google Calendar");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Search History", "com.google.android.calendar", "/databases/suggestions.db",
			new DBQuery
			(
				new String[] { "Suggestion" },
				"suggestions",
				new String[] { "query" }
			),
			new String[] { "DELETE FROM suggestions;" }
		));
		cats.add(cat);
		
		// ----------------
		// ----- iMDB -----
		// ----------------
		cat = new Category("IMDb");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "History", "com.imdb.mobile", "/databases/history.db",
			new DBQuery
			(
				new String[] { "ID", "Title" },
				"history",
				new String[] { "const_id", "label" }
			),
			new String[] { "DELETE FROM history;" }
		));
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Suggestions", "com.imdb.mobile", "/databases/IMDbSuggestions",
			new DBQuery
			(
				new String[] { "Query", "Suggestion" },
				"Suggestions",
				new String[] { "imdb_query", "suggest_text_1" }
			),
			new String[] { "DELETE FROM Suggestions;" }
		));
		cats.add(cat);
		
		// -----------------------------
		// ----- Lightning Browser -----
		// -----------------------------
		cat = new Category("Lightning Browser");
		
		cat.addItem(new SimpleFileItem(cat, "Cache", "acr.browser.barebones", "/app_webview/Cache/*", true));
		
		String[] lightningCookies = new String[] { "/app_webview/Cookies", "/app_webview/Cookies-journal" };
		cat.addItem(new SimpleFileItem(cat, "Cookies", "acr.browser.barebones", lightningCookies, true));
		
		String[] lightningHistory = new String[] { "/files/history.html", "/databases/historyManager", "/databases/historyManager-journal" };
		cat.addItem(new SimpleFileItem(cat, "History", "acr.browser.barebones", lightningHistory, true));
		
		cat.addItem(new SimpleFileItem(cat, "Local Storage", "acr.browser.barebones", "/app_webview/Local Storage", true));

		cats.add(cat);

		// ----------------------------------
		// ----- Lightning Browser Plus -----
		// ----------------------------------
		cat = new Category("Lightning Browser Plus");
		
		cat.addItem(new SimpleFileItem(cat, "Cache", "acr.browser.lightning", "/app_webview/Cache/*", true));

		String[] lightningPlusCookies = new String[] { "/app_webview/Cookies", "/app_webview/Cookies-journal" };
		cat.addItem(new SimpleFileItem(cat, "Cookies", "acr.browser.lightning", lightningPlusCookies, true));

		String[] lightningPlusHistory = new String[] { "/files/history.html", "/databases/historyManager", "/databases/historyManager-journal" };
		cat.addItem(new SimpleFileItem(cat, "History", "acr.browser.lightning", lightningPlusHistory, true));

		cat.addItem(new SimpleFileItem(cat, "Local Storage", "acr.browser.lightning", "/app_webview/Local Storage", true));

		cats.add(cat);

		// ---------------------------
		// ----- Maxthon Browser -----
		// ---------------------------
		cat = new Category("Maxthon Browser");
		cat.addItem(new SimpleFileItem(cat, "Cache", "com.mx.browser", "/cache/*", true));
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Cookies", "com.mx.browser", "/databases/webviewCookiesChromium.db",
			new DBQuery
			(
				new String[] { "Domain", " Cookie Name", "Cookie Value" },
				"cookies",
				new String[] { "host_key", "name", "value" }
			),
			new String[] { "DELETE FROM cookies;" }
		));
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "History", "com.mx.browser", "/databases/mxbrowser_default.db",
			new DBQuery
			(
				new String[] { "Title", "URL", "Visits" },
				"history",
				new String[] { "title", "url", "visits" }
			),
			new String[] { "DELETE FROM history;" }
		));
		cats.add(cat);
		
		// --------------------------------------
		// ----- Merriam-Webster Dictionary -----
		// --------------------------------------
		cat = new Category("Merriam-Webster Dictionary");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Recent Words", "com.merriamwebster", "/databases/mw.db",
			new DBQuery
			(
				new String[] { "Words" },
				"recent_searches",
				new String[] { "search_query" }
			),
			new String[]
			{
				"UPDATE sqlite_sequence SET seq='0' WHERE name='recent_searches';",
				"DELETE FROM recent_searches;"
			}
		));
		cats.add(cat);
		
		// -----------------------
		// ----- ONE Browser -----
		// -----------------------
		cat = new Category("ONE Browser");
		cat.addItem(new com.ayros.historycleaner.cleaning.items._ONEBrowser_Cache(cat));
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Cookies", "com.tencent.ibibo.mtt", "/databases/webview_x5.db",
			new DBQuery
			(
				new String[] { "Domain", "Name", "Value" },
				"cookies",
				new String[] { "domain", "name", "value" }
			),
			new String[] { "DELETE FROM cookies;" }
		));
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "History", "com.tencent.ibibo.mtt", "/databases/database",
			new DBQuery
			(
				new String[] { "Title", "URL" },
				"history",
				new String[] { "NAME", "URL" }
			),
			new String[]
			{
				"UPDATE sqlite_sequence SET seq='0' WHERE name='history';",
				"DELETE FROM history;"
			}
		));
		cat.addItem(new com.ayros.historycleaner.cleaning.items._ONEBrowser_LocalStorage(cat));
		cats.add(cat);

		// ----------------
		// ----- Waze -----
		// ----------------
		cat = new Category("Waze");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "History", "com.waze", "/user.db",
			new DBQuery
			(
				new String[] { "Name" },
				"RECENTS",
				new String[] { "name" }
			),
			new String[]
			{
				"UPDATE sqlite_sequence SET seq='0' WHERE name='RECENTS';",
				"DELETE FROM RECENTS;",
				"UPDATE FAVORITES SET access_time = '0';",
			}
		));
		cats.add(cat);

		// ---------------------
		// ----- Wikipedia -----
		// ---------------------
		cat = new Category("Wikipedia");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Search History", "org.wikipedia", "/databases/wikipedia.db",
			new DBQuery
			(
				new String[] { "Search Term" },
				"recentsearches",
				new String[] { "text" }
			),
			new String[] { "DELETE FROM recentsearches;" }
		));
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Browsing History", "org.wikipedia", "/databases/wikipedia.db",
			new DBQuery
			(
				new String[] { "Page Title", "Timestamp" },
				"history",
				new String[] { "title", "timestamp" }
			),
			new String[]
			{
				"DELETE FROM history;",
				"DELETE FROM pageimages;",
			}
		));
		cats.add(cat);
		
		// ----------------
		// ----- Yelp -----
		// ----------------
		cat = new Category("Yelp");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Searches", "com.yelp.android", "/databases/yelp",
			new DBQuery
			(
				new String[] { "Search" },
				"searchterms",
				new String[] { "searchterm" }
			),
			new String[]
			{
				"UPDATE sqlite_sequence SET seq='0' WHERE name='searchterms';",
				"UPDATE sqlite_sequence SET seq='0' WHERE name='business_search';",
				"DELETE FROM searchterms;",
				"DELETE FROM business_search;"
			}
		));
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Recently Viewed", "com.yelp.android", "/databases/yelp",
			new DBQuery
			(
				new String[] { "Yelp Business ID", "Other Info" },
				"recently_viewed_businesses",
				new String[] { "business_id", "business_json" }
			),
			new String[]
			{
				"UPDATE sqlite_sequence SET seq='0' WHERE name='recently_viewed_businesses';",
				"DELETE FROM recently_viewed_businesses;"
			}
		));
		cats.add(cat);
		
		// -------------------
		// ----- Youtube -----
		// -------------------
		cat = new Category("Youtube");
		cat.addItem(new SimpleDatabaseItem
		(
			cat, "Searches", "com.google.android.youtube", "/databases/history.db",
			new DBQuery
			(
				new String[] { "Search Term" },
				"suggestions",
				new String[] { "query" }
			),
			new String[] { "DELETE FROM suggestions;" }
		));
		cats.add(cat);
		
		for (int i = cats.size() - 1; i >= 0; i--)
		{
			cat = cats.get(i);
			List<CleanItem> items = cat.getItems();
			for (int z = items.size() - 1; z >= 0; z--)
			{
				if (!items.get(z).isApplicable())
				{
					items.remove(z);
				}
			}
			
			if (items.size() == 0)
			{
				cats.remove(i);
			}
		}
	}
	
	/**
	 * Returns a list of all items contained within profile.
	 * 
	 * @param p
	 * @return
	 */
	public List<CleanItem> getProfileItems(Profile profile)
	{
		List<CleanItem> items = new ArrayList<CleanItem>();
		
		for (String itemName : profile.getItemNames())
		{
			CleanItem item = getItemByUniqueName(itemName);
			
			if (item != null)
			{
				items.add(item);
			}
		}
		
		return items;
	}
	
	public CleanItem getItemByView(View v)
	{
		for (CleanItem item : getAllItems(false))
		{
			ViewGroup itemView = item.getView();
			View nameView = itemView.findViewById(R.id.item_name);
			
			if (v.equals(nameView))
			{
				return item;
			}
		}
		
		return null;
	}
	
	public CleanItem getItemByUniqueId(int uniqueId)
	{
		for (CleanItem ci : getAllItems(false))
		{
			if (ci.getUniqueId() == uniqueId)
			{
				return ci;
			}
		}
		
		return null;
	}
	
	public CleanItem getItemByUniqueName(String uniqueName)
	{
		for (CleanItem ci : getAllItems(false))
		{
			if (ci.getUniqueName().equals(uniqueName))
			{
				return ci;
			}
		}
		
		return null;
	}
	
	public List<CleanItem> getAllItems(boolean enabledOnly)
	{
		List<CleanItem> items = new ArrayList<CleanItem>();
		for (Category cat : cats)
		{
			for (CleanItem item : cat.getItems())
			{
				if (!enabledOnly || item.isChecked())
				{
					items.add(item);
				}
			}
		}
		
		return items;
	}
	
	public void loadProfile(Profile p)
	{
		if (p != null)
		{
			List<CleanItem> items = getAllItems(false);
			for (CleanItem item : items)
			{
				item.setChecked(p.isSelected(item) && item.isApplicable());
			}
		}
	}
	
	public View makeCategoriesView(Context c)
	{
		ViewGroup catLayout = (ViewGroup)View.inflate(c, R.layout.category_list, null);
		
		for (int i = 0; i < cats.size(); i++)
		{
			Category cat = cats.get(i);
			
			catLayout.addView(cat.getCategoryView(c));
		}
		
		return catLayout;
	}
	
	public void registerContextMenu(Fragment f)
	{
		for (CleanItem item : getAllItems(false))
		{
			ViewGroup itemView = item.getView();
			
			if (itemView != null)
			{
				TextView itemName = (TextView)itemView.findViewById(R.id.item_name);
				f.registerForContextMenu(itemName);
			}
		}
	}
	
	public boolean saveProfile(Profile p)
	{
		if (p != null)
		{
			p.selectedItems.clear();
			for (CleanItem ci : getAllItems(true))
			{
				p.selectedItems.add(ci.getUniqueName());
			}
			
			return p.saveChanges();
		}
		
		return false;
	}
}
