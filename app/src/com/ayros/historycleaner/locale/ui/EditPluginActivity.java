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

package com.ayros.historycleaner.locale.ui;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.R;
import com.ayros.historycleaner.cleaning.ProfileList;
import com.ayros.historycleaner.locale.bundle.BundleScrubber;
import com.ayros.historycleaner.locale.bundle.PluginBundleManager;

/**
 * This is the "Edit" activity for a Locale Plug-in.
 * <p>
 * This Activity can be started in one of two states:
 * <ul>
 * <li>New plug-in instance: The Activity's Intent will not contain
 * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE}.</li>
 * <li>Old plug-in instance: The Activity's Intent will contain
 * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} from a previously saved
 * plug-in instance that the user is editing.</li>
 * </ul>
 * 
 * @see com.twofortyfouram.locale.Intent#ACTION_EDIT_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class EditPluginActivity extends AbstractPluginActivity
{
	List<String> profileNames;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (Globals.getContext() == null)
		{
			Globals.setContext(this);
		}
		
		if (!ProfileList.isLoaded())
		{
			ProfileList.load();
		}
		profileNames = ProfileList.getNamesList(false);
		
		BundleScrubber.scrub(getIntent());
		
		final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
		BundleScrubber.scrub(localeBundle);
		
		setContentView(R.layout.activity_edit_plugin);
		
		Spinner profileSpinner = (Spinner)findViewById(R.id.editPlugin_profileSpinner);
		profileSpinner.setAdapter(getSpinnerAdapter());
		
		if (savedInstanceState == null)
		{
			if (PluginBundleManager.isBundleValid(localeBundle))
			{
				String profile = localeBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_CLEAN_PROFILE);
				
				int index = indexOfProfileName(profile);
				if (index != -1)
				{
					profileSpinner.setSelection(index);
				}
			}
		}
	}
	
	private int indexOfProfileName(String profile)
	{
		for (int i = 0; i < profileNames.size(); i++)
		{
			if (profileNames.get(i).equals(profile))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	private SpinnerAdapter getSpinnerAdapter()
	{
		return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, profileNames);
	}
	
	@Override
	public void finish()
	{
		if (!isCanceled())
		{
			Spinner profileSpinner = (Spinner)findViewById(R.id.editPlugin_profileSpinner);
			int index = profileSpinner.getSelectedItemPosition();
			
			if (index >= 0)
			{
				String profileName = profileNames.get(index);
				
				Intent resultIntent = new Intent();
				
				/*
				 * This extra is the data to ourselves: either for the Activity
				 * or the BroadcastReceiver. Note that anything placed in this
				 * Bundle must be available to Locale's class loader. So storing
				 * String, int, and other standard objects will work just fine.
				 * Parcelable objects are not acceptable, unless they also
				 * implement Serializable. Serializable objects must be standard
				 * Android platform objects (A Serializable class private to
				 * this plug-in's APK cannot be stored in the Bundle, as
				 * Locale's classloader will not recognize it).
				 */
				Bundle resultBundle = PluginBundleManager.generateBundle(getApplicationContext(), profileName);
				resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
				
				/*
				 * The blurb is concise status text to be displayed in the
				 * host's UI.
				 */
				String blurb = generateBlurb(getApplicationContext(), profileName);
				resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, blurb);
				
				setResult(RESULT_OK, resultIntent);
			}
		}
		
		super.finish();
	}
	
	/**
	 * @param context
	 *            Application context.
	 * @param message
	 *            The toast message to be displayed by the plug-in. Cannot be
	 *            null.
	 * @return A blurb for the plug-in.
	 */
	static String generateBlurb(final Context context, final String message)
	{
		final int maxBlurbLength = context.getResources().getInteger(
			com.twofortyfouram.locale.api.R.integer.twofortyfouram_locale_maximum_blurb_length);
		
		if (message.length() > maxBlurbLength)
		{
			return message.substring(0, maxBlurbLength);
		}
		
		return message;
	}
}