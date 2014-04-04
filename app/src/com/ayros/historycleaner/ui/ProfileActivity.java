package com.ayros.historycleaner.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ayros.historycleaner.ProfileAdapter;
import com.ayros.historycleaner.R;
import com.ayros.historycleaner.cleaning.Profile;
import com.ayros.historycleaner.cleaning.ProfileList;
import com.ayros.historycleaner.helpers.Logger;

public class ProfileActivity extends Activity implements OnClickListener
{
	protected class ConfirmLoadListener implements DialogInterface.OnClickListener
	{
		private Profile profile;

		public ConfirmLoadListener(Profile p)
		{
			profile = p;
		}

		public void onClick(DialogInterface dialog, int whichButton)
		{
			ProfileList.get(null).copyFrom(profile);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		showProfileList();
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_profile, menu);

		try
		{
			ProfileActivity.this.invalidateOptionsMenu();
		}
		catch (NoSuchMethodError e)
		{
			Logger.errorST("Could not call invalidOptionsMenu method (ProfileActivity)");
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.clean_menu_rate:
				Uri uri = Uri.parse("market://details?id=" + getPackageName());
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try
				{
					startActivity(goToMarket);
				}
				catch (ActivityNotFoundException anfe)
				{
					Toast.makeText(this, "Error: Couldn't open browser", Toast.LENGTH_SHORT).show();
					Logger.errorST("Couldn't open brownser", anfe);
				}
				return true;

			case R.id.clean_menu_help:
				Intent intent = new Intent(this, HelpActivity.class);
				startActivity(intent);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void showProfileList()
	{
		ListView lv = (ListView)findViewById(R.id.profile_list);

		List<Profile> profList = ProfileList.getClonedList(false);

		Profile[] profData = profList.toArray(new Profile[profList.size()]);

		ProfileAdapter adapter = new ProfileAdapter(this, R.layout.profile_list_item, profData, this);

		lv.setAdapter(adapter);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		showProfileList();
	}

	@Override
	public void onClick(View v)
	{
		final Profile p = (Profile)v.getTag();
		Button btn = (Button)v;

		if (btn.getText().equals(getResources().getString(R.string.profile_list_row_load)))
		{
			AlertDialog.Builder confirmLoad = new AlertDialog.Builder(ProfileActivity.this);
			confirmLoad.setTitle("Load Profile");
			confirmLoad
				.setMessage("Do you want to load this profile? (It will replace the currently selected items)");

			confirmLoad.setPositiveButton(android.R.string.yes, new ConfirmLoadListener(p));
			confirmLoad.setNegativeButton(android.R.string.no, null);

			confirmLoad.show();
		}
		else if (btn.getText().equals(getResources().getString(R.string.profile_list_row_settings)))
		{
			final Dialog profileSettings = new Dialog(ProfileActivity.this);
			profileSettings.setTitle("Profile Settings");

			profileSettings.setContentView(R.layout.profile_item_settings);

			Button saveButton = (Button)profileSettings
				.findViewById(R.id.profile_item_settings_btnSave);
			saveButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Toast.makeText(ProfileActivity.this, "Changes saved successfully!",
						Toast.LENGTH_SHORT).show();
					// p.saveChanges();
					ProfileActivity.this.showProfileList();
					profileSettings.dismiss();
				}
			});

			Button cancelButton = (Button)profileSettings
				.findViewById(R.id.profile_item_settings_btnCancel);
			cancelButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					profileSettings.dismiss();
				}
			});

			Button deleteButton = (Button)profileSettings
				.findViewById(R.id.profile_item_settings_btnDelete);
			deleteButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					ProfileList.delete(p);
					profileSettings.dismiss();
					ProfileActivity.this.showProfileList();
				}
			});

			profileSettings.show();
		}

	}
}
