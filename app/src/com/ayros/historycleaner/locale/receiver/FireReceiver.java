/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.ayros.historycleaner.locale.receiver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.CategoryList;
import com.ayros.historycleaner.cleaning.CleanItem;
import com.ayros.historycleaner.cleaning.Cleaner;
import com.ayros.historycleaner.cleaning.Profile;
import com.ayros.historycleaner.cleaning.ProfileList;
import com.ayros.historycleaner.locale.Constants;
import com.ayros.historycleaner.locale.bundle.BundleScrubber;
import com.ayros.historycleaner.locale.bundle.PluginBundleManager;
import com.stericson.RootTools.execution.Shell;

/**
 * This is the "fire" BroadcastReceiver for a Locale Plug-in setting.
 * 
 * @see com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class FireReceiver extends BroadcastReceiver
{
	/**
	 * @param context
	 *            {@inheritDoc}.
	 * @param intent
	 *            the incoming
	 *            {@link com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING}
	 *            Intent. This should contain the
	 *            {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} that was
	 *            saved by {@link EditActivity} and later broadcast by Locale.
	 */
	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		/*
		 * Always be strict on input parameters! A malicious third-party app
		 * could send a malformed Intent.
		 */

		if (!com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction()))
		{
			if (Constants.IS_LOGGABLE)
			{
				Log.e(Constants.LOG_TAG,
					String.format(Locale.US, "Received unexpected Intent action %s", intent.getAction()));
			}
			return;
		}

		BundleScrubber.scrub(intent);

		final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
		BundleScrubber.scrub(bundle);

		if (PluginBundleManager.isBundleValid(bundle))
		{
			String profileName = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_CLEAN_PROFILE);

			Globals.setContext(context);
			ProfileList.load();

			Profile profile = ProfileList.get(profileName);
			if (profile == null)
			{
				Toast.makeText(context, "History Cleaner: Could not find profile " + profileName, Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(context, "Cleaning profile " + profileName, Toast.LENGTH_LONG).show();

				CategoryList catList = new CategoryList();
				List<CleanItem> cleanItems = catList.getProfileItems(profile);
				Cleaner cleaner = new Cleaner(cleanItems);

				if (cleaner.isRootRequired())
				{
					try
					{
						Shell.startRootShell();
					}
					catch (Exception e)
					{
						Toast.makeText(context, "Error: Could not aquire root access, cannot clean profile " + profileName, Toast.LENGTH_LONG).show();
						return;
					}
				}

				Cleaner.CleanResults results = cleaner.cleanNow(null);

				if (results.failure.size() > 0)
				{
					Toast.makeText(context, results.toString(), Toast.LENGTH_LONG).show();
				}

				try
				{
					Shell.closeRootShell();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}