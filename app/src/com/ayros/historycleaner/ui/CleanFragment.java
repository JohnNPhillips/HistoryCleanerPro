package com.ayros.historycleaner.ui;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
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

public class CleanFragment extends Fragment implements OnClickListener, OnProfileUpdated
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

	private Profile autoCleanProfile = null;
	private String displayTip = null;

	//
	// Life-cycle Methods
	//

	public static CleanFragment newInstance()
	{
		CleanFragment fragment = new CleanFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		fragment.displayTip = getTip();

		return fragment;
	}

	public static CleanFragment newInstance(Profile autoCleanProfile)
	{
		CleanFragment fragment = new CleanFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);

		fragment.autoCleanProfile = autoCleanProfile;

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.frag_clean, container, false);

		ProfileList.load();

		catList = new CategoryList();

		LinearLayout catView = (LinearLayout)rootView.findViewById(R.id.clean_categories);
		catView.addView(catList.makeCategoriesView(this.getActivity()));

		return rootView;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		catList.loadProfile(ProfileList.get(null));
		catList.registerContextMenu(this);

		Button cleanButton = (Button)getView().findViewById(R.id.clean_btnClear);
		cleanButton.setOnClickListener(this);

		// Context is needed in Logger class
		if (Globals.getContext() == null)
		{
			Globals.setContext(this.getActivity());
		}

		if (Logger.isDebugMode() || Logger.isLogToFileMode())
		{
			if (Logger.isDebugMode())
			{
				Toast.makeText(getActivity(), "Debug mode is on.", Toast.LENGTH_SHORT).show();
			}
			if (Logger.isLogToFileMode())
			{
				Toast.makeText(getActivity(), "Log-to-file mode is on.", Toast.LENGTH_SHORT).show();
			}
		}
		else if (autoCleanProfile != null)
		{
			catList.loadProfile(autoCleanProfile);
			autoCleanProfile = null;
			cleanItems(catList, true);
		}
		else if (displayTip != null)
		{
			Toast.makeText(getActivity(), displayTip, Toast.LENGTH_LONG).show();
			displayTip = null;
		}
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

	//
	// Other Methods
	//

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	public void cleanItems(final CategoryList categoryList, final boolean exitOnFinish)
	{
		// Lock orientation
		final int prevOrientation = getActivity().getRequestedOrientation();
		WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
		int REVERSE_PORTRAIT = Build.VERSION.SDK_INT >= 9 ?
			ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		int REVERSE_LANDSCAPE = Build.VERSION.SDK_INT >= 9 ?
			ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		switch (wm.getDefaultDisplay().getOrientation())
		{
			case Surface.ROTATION_0:
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
			case Surface.ROTATION_90:
				getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			case Surface.ROTATION_180:
				getActivity().setRequestedOrientation(REVERSE_PORTRAIT);
				break;
			default:
				getActivity().setRequestedOrientation(REVERSE_LANDSCAPE);
		}

		if (categoryList.getAllItems(true).size() == 0)
		{
			Toast.makeText(getActivity(), "Please select at least one item to clear!", Toast.LENGTH_LONG).show();
			if (exitOnFinish)
			{
				getActivity().finish();
			}
			else
			{
				getActivity().setRequestedOrientation(prevOrientation);
			}
			return;
		}

		Cleaner itemCleaner = new Cleaner(categoryList.getAllItems(true));

		if (itemCleaner.isRootRequired())
		{
			if (!RootTools.isRootAvailable())
			{
				Toast.makeText(getActivity(), "Error: This app requires root access", Toast.LENGTH_LONG).show();
				if (exitOnFinish)
				{
					getActivity().finish();
				}
				else
				{
					getActivity().setRequestedOrientation(prevOrientation);
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
					Toast.makeText(getActivity(), "Error: Could not obtain root access! This app requires root!", Toast.LENGTH_LONG).show();
					Logger.errorST("Root access denied", rde);
					if (exitOnFinish)
					{
						getActivity().finish();
					}
					else
					{
						getActivity().setRequestedOrientation(prevOrientation);
					}
					return;
				}
				catch (Exception e)
				{
					Toast.makeText(getActivity(), "Error: There was a problem when trying to gain root access", Toast.LENGTH_LONG).show();
					Logger.errorST("Problem starting root shell", e);
					if (exitOnFinish)
					{
						getActivity().finish();
					}
					else
					{
						getActivity().setRequestedOrientation(prevOrientation);
					}
					return;
				}
			}
		}

		final ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setTitle("Clearing History...");

		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setIndeterminate(false);
		pd.setMax(categoryList.getAllItems(true).size());

		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);

		pd.show();

		itemCleaner.cleanAsync(getActivity(), new CleanListener()
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
				try
				{
					pd.cancel();
				}
				catch (Exception e)
				{
					Logger.errorST("Problem closing progress dialog upon cleaning completion", e);
				}

				final AlertDialog.Builder resultsDialog = new AlertDialog.Builder(getActivity());
				resultsDialog.setTitle("Cleaning Results");
				resultsDialog.setMessage(results.toString());
				resultsDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();

						if (exitOnFinish)
						{
							getActivity().finish();
						}
						else
						{
							getActivity().setRequestedOrientation(prevOrientation);
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

	public static String getTip()
	{
		String[] tips = new String[]
		{
			"TIP: Long pressing on an item will allow you to view the data to be cleared",
			"TIP: Is there an application you wished was supported? Leave us a message and we'll see if we can add it!",
			"TIP: You can add shortcuts on your homescreen so you can clear your history in one click"
		};

		return tips[(int)(Math.random() * tips.length)];
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.menu_clean, menu);
	}

	@Override
	public void onClick(View v)
	{
		cleanItems(catList, false);
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

				Intent intent = new Intent(getActivity(), DataViewActivity.class);
				startActivity(intent);

				return true;
			}
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		int id = catList.getItemByView(v).getUniqueId();

		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Actions");
		menu.add(ContextMenu.NONE, id, 0, ACTION_VIEW_ITEMS);
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
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onProfileUpdated()
	{
		catList.loadProfile(ProfileList.get(null));
	}

	public void showSaveProfileDialog()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Save Profile");
		alert.setMessage("Enter a name for the profile to be saved to.");

		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity());
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
					Toast.makeText(getActivity(), "Error: You must enter a name for a new profile!", Toast.LENGTH_LONG).show();
				}
				else if (ProfileList.get(newName) != null)
				{
					AlertDialog.Builder confirmOverwrite = new AlertDialog.Builder(getActivity());
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
					((OnProfileUpdated)getActivity()).onProfileUpdated();
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
}
