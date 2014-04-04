package com.ayros.historycleaner.ui;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.R;
import com.ayros.historycleaner.cleaning.CategoryList;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.cleaning.CleanListener;
import com.ayros.historycleaner.cleaning.Cleaner;
import com.ayros.historycleaner.cleaning.Cleaner.CleanResults;
import com.ayros.historycleaner.cleaning.Profile;
import com.ayros.historycleaner.cleaning.ProfileList;
import com.ayros.historycleaner.helpers.Logger;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Shell;

public class CleanActivity extends Activity implements OnClickListener
{
	protected class ConfirmOverwriteListener implements DialogInterface.OnClickListener
	{
		private String profileName;

		public ConfirmOverwriteListener(String profName)
		{
			profileName = profName;
		}

		public void onClick(DialogInterface dialog, int whichButton)
		{
			Profile newProf = ProfileList.create(profileName);
			catList.saveProfile(newProf);
		}
	}

	private static final String ACTION_VIEW_ITEMS = "View Items";

	private CategoryList catList = null;
	private Profile autoClean = null;

	@Override
	public void onStart()
	{
		super.onStart();

		if (autoClean != null)
		{
			catList.loadProfile(autoClean);
			autoClean = null;
			cleanItems(catList, true);
		}
	}

	public String getTip()
	{
		String[] tips = new String[] { "TIP: Long pressing on an item will allow you to view the data to be cleared",
			"TIP: Is there an application you wished was supported? Leave us a message and we'll see if we can add it!",
			"TIP: You can add shortcuts on your homescreen so you can clear your history in one click" };

		return tips[(int)(Math.random() * tips.length)];
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean);

		Globals.setContext(getApplicationContext());
		ProfileList.load();

		Intent intent = getIntent();

		if (intent.hasExtra(ShortcutActivity.SHORTCUT_PROFILE_NAME)
			&& intent.getStringExtra(ShortcutActivity.SHORTCUT_PROFILE_NAME) != null)
		{
			boolean launchedFromHistory = (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0;
			if (launchedFromHistory)
			{
				Intent splashIntent = new Intent(this, SplashActivity.class);
				startActivity(splashIntent);
				finish();
				return;
			}

			String profileName = intent.getStringExtra(ShortcutActivity.SHORTCUT_PROFILE_NAME);
			autoClean = ProfileList.get(profileName);
			if (autoClean == null)
			{
				Toast.makeText(this, "Can't find profile!", Toast.LENGTH_LONG).show();
				finish();
			}
			else
			{
				catList = new CategoryList();

				LinearLayout catView = (LinearLayout)findViewById(R.id.categories);
				catView.addView(catList.makeCategoriesView(CleanActivity.this));
			}
		}
		else
		{
			catList = new CategoryList();

			LinearLayout catView = (LinearLayout)findViewById(R.id.categories);
			catView.addView(catList.makeCategoriesView(this));

			catList.loadProfile(ProfileList.get(null));
			catList.registerContextMenu(this);

			Button cleanButton = (Button)findViewById(R.id.button_clear);
			cleanButton.setOnClickListener(this);

			if (Logger.isDebugMode() || Logger.isLogToFileMode())
			{
				if (Logger.isDebugMode())
				{
					Toast.makeText(this, "Debug mode is on.", Toast.LENGTH_SHORT).show();
				}
				if (Logger.isLogToFileMode())
				{
					Toast.makeText(this, "Log-to-file mode is on.", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				final Toast t = Toast.makeText(this, getTip(), Toast.LENGTH_LONG);
				t.show();

				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						t.show();
					}
				}, 2000);
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		int id = catList.getItemByView(v).getUniqueId();

		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Actions");
		menu.add(ContextMenu.NONE, id, 0, ACTION_VIEW_ITEMS);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_clean, menu);

		try
		{
			this.invalidateOptionsMenu();
		}
		catch (NoSuchMethodError e)
		{
			Logger.error("Could not call invalidOptionsMenu method (CleanActivity)");
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if (item.getTitle().equals(ACTION_VIEW_ITEMS))
		{
			int itemId = item.getItemId();

			CleanItem ci = catList.getItemByUniqueId(itemId);
			if (ci != null)
			{
				Globals.itemDataView = ci;

				Intent intent = new Intent(this, DataViewActivity.class);
				startActivity(intent);

				return true;
			}
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		List<CleanItem> items;
		switch (item.getItemId())
		{
			case R.id.clean_menu_save_as_profile:
				showSaveProfileDialog();
				return true;

			case R.id.clean_menu_select_all:
				items = catList.getAllItems(false);
				for (CleanItem ci : items)
				{
					// Only check item if there is no warning message (don't accidently clean something sensitive)
					if (ci.getWarningMessage() == null)
					{
						ci.setChecked(true);
					}
				}
				return true;

			case R.id.clean_menu_select_none:
				items = catList.getAllItems(false);
				for (CleanItem ci : items)
				{
					ci.setChecked(false);
				}
				return true;

			case R.id.clean_menu_rate:
				Uri uri = Uri.parse("market://details?id=" + getPackageName());
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try
				{
					startActivity(goToMarket);
				}
				catch (ActivityNotFoundException e)
				{
					Toast.makeText(this, "Error: Couldn't open browser", Toast.LENGTH_SHORT).show();
				}
				return true;

			case R.id.clean_menu_help:
				Intent intent = new Intent(this, HelpActivity.class);
				startActivity(intent);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void showSaveProfileDialog()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Save Profile");
		alert.setMessage("Enter a name for the profile to be saved to.");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		if (Globals.saveProfileText != null)
		{
			input.setText(Globals.saveProfileText);
		}
		alert.setView(input);

		alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				final String newName = input.getText().toString().trim();

				if (newName.length() == 0)
				{
					Toast.makeText(CleanActivity.this, "Error: You must enter a name for a new profile!", Toast.LENGTH_LONG).show();
				}
				else if (ProfileList.get(newName) != null)
				{
					AlertDialog.Builder confirmOverwrite = new AlertDialog.Builder(CleanActivity.this);
					confirmOverwrite.setTitle("Overwrite?");
					confirmOverwrite.setMessage("A profile with this name already exists, do you want to overwrite it?");

					confirmOverwrite.setPositiveButton(android.R.string.yes, new ConfirmOverwriteListener(newName));
					confirmOverwrite.setNegativeButton(android.R.string.no, null);

					confirmOverwrite.show();
				}
				else
				{
					Profile newProf = ProfileList.create(newName);
					catList.saveProfile(newProf);
				}
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dialog.dismiss();
			}
		});

		alert.show();
	}

	public void cleanItems(final CategoryList categoryList, final boolean exitOnFinish)
	{
		if (categoryList.getAllItems(true).size() == 0)
		{
			Toast.makeText(CleanActivity.this, "Please select at least one item to clear!", Toast.LENGTH_LONG).show();
			if (exitOnFinish)
			{
				finish();
			}
			return;
		}

		Cleaner itemCleaner = new Cleaner(categoryList.getAllItems(true));

		if (itemCleaner.isRootRequired())
		{
			if (!RootTools.isRootAvailable())
			{
				Toast.makeText(this, "Error: This app requires root access", Toast.LENGTH_LONG).show();
				if (exitOnFinish)
				{
					finish();
				}
				return;
			}

			if (!Shell.isRootShellOpen())
			{
				try
				{
					Shell.startRootShell();
				}
				catch (RootDeniedException rde)
				{
					Toast.makeText(this, "Error: Could not obtain root access! This app requires root!", Toast.LENGTH_LONG).show();
					Logger.errorST("Root access denied", rde);
					if (exitOnFinish)
					{
						finish();
					}
					return;
				}
				catch (Exception e)
				{
					Toast.makeText(this, "Error: There was a problem when trying to gain root access", Toast.LENGTH_LONG).show();
					Logger.errorST("Problem starting root shell", e);
					if (exitOnFinish)
					{
						finish();
					}
					return;
				}

			}
		}

		final ProgressDialog pd = new ProgressDialog(CleanActivity.this);
		pd.setTitle("Clearing History...");

		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setIndeterminate(false);
		pd.setMax(categoryList.getAllItems(true).size());

		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);

		pd.show();

		itemCleaner.cleanAsync(this, new CleanListener()
		{
			@Override
			public void progressChanged(Cleaner.CleanProgressEvent cpe)
			{
				pd.setMessage("Cleaning " + cpe.item.getUniqueName());
				pd.setProgress(cpe.itemIndex + 1);
			}

			@Override
			public void cleaningComplete(CleanResults results)
			{
				pd.cancel();

				final AlertDialog.Builder resultsDialog = new AlertDialog.Builder(CleanActivity.this);
				resultsDialog.setTitle("Cleaning Results");
				resultsDialog.setMessage(results.toString());
				resultsDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();

						if (exitOnFinish)
						{
							finish();
						}
					}
				});

				resultsDialog.show();

				try
				{
					RootTools.closeAllShells();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public void onClick(View v)
	{
		cleanItems(catList, false);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		if (catList != null)
		{
			catList.saveProfile(ProfileList.get(null));
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		catList.loadProfile(ProfileList.get(null));
	}

	@Override
	public void onDestroy()
	{
		if (catList != null)
		{
			catList.saveProfile(ProfileList.get(null));
		}

		try
		{
			RootTools.closeAllShells();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		super.onDestroy();
	}
}