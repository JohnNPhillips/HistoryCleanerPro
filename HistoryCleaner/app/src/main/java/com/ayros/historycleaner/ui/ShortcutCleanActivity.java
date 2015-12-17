package com.ayros.historycleaner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.Profile;
import com.ayros.historycleaner.cleaning.ProfileList;

public class ShortcutCleanActivity extends FragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_shortcut_clean);

		Intent intent = getIntent();
		if (intent.hasExtra(ShortcutActivity.SHORTCUT_PROFILE_NAME) && intent.getStringExtra(ShortcutActivity.SHORTCUT_PROFILE_NAME) != null)
		{
			boolean launchedFromHistory = (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0;
			if (launchedFromHistory)
			{
				startActivity(new Intent(this, MainActivity.class));
				finish();
			}
			else
			{
				String profileName = intent.getStringExtra(ShortcutActivity.SHORTCUT_PROFILE_NAME);

				Globals.setContext(this);
				ProfileList.load();

				Profile profile = ProfileList.get(profileName);
				if (profile == null)
				{
					Toast.makeText(this, "History Cleaner: Could not find profile " + profileName, Toast.LENGTH_LONG).show();
					finish();
				}
				else
				{
					Toast.makeText(this, "Cleaning profile " + profileName, Toast.LENGTH_LONG).show();

					FragmentManager fragmentManager = getSupportFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(android.R.id.content, CleanFragment.newInstance(profile));
					fragmentTransaction.commit();
				}
			}
		}
	}
}