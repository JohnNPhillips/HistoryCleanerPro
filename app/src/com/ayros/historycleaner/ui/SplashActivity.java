package com.ayros.historycleaner.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.cleaning.ProfileList;

public class SplashActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		new Thread()
		{
			@Override
			public void run()
			{
				Globals.setContext(SplashActivity.this.getApplicationContext());
				ProfileList.load();
				
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				intent.putExtra("profile", ProfileList.LAST_VIEW);
				startActivity(intent);
				finish();
			}
		}.start();
	}
}
