package com.ayros.historycleaner.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.R;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, OnProfileUpdated
{
	private static final String CLEAN_TAB_TITLE = "Clean";
	private static final String PROFILE_TAB_TITLE = "Profiles";

	SectionsPagerAdapter sectionsPagerAdapter;
	ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set global context
		Globals.setContext(getApplicationContext());

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		viewPager = (ViewPager)findViewById(R.id.main_pager);
		viewPager.setAdapter(sectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		Tab cleanTab = actionBar.newTab();
		cleanTab.setText(CLEAN_TAB_TITLE);
		cleanTab.setTabListener(this);
		actionBar.addTab(cleanTab);

		Tab profileTab = actionBar.newTab();
		profileTab.setText(PROFILE_TAB_TITLE);
		profileTab.setTabListener(this);
		actionBar.addTab(profileTab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.main_menu_rate:
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

			case R.id.main_menu_help:
				Intent intent = new Intent(this, HelpActivity.class);
				startActivity(intent);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onProfileUpdated()
	{
		for (Fragment f : getSupportFragmentManager().getFragments())
		{
			if (f instanceof OnProfileUpdated)
			{
				((OnProfileUpdated)f).onProfileUpdated();
			}
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			switch (position)
			{
				case 0:
					return CleanFragment.newInstance();
				case 1:
					return ProfileFragment.newInstance();
				default:
					return null;
			}
		}

		@Override
		public int getCount()
		{
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			switch (position)
			{
				case 0:
					return CLEAN_TAB_TITLE;
				case 1:
					return PROFILE_TAB_TITLE;
				default:
					return null;
			}
		}
	}
}
