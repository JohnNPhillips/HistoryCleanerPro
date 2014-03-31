package com.ayros.historycleaner.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.ayros.historycleaner.R;

public class HelpActivity extends Activity
{
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		WebView wv = (WebView)findViewById(R.id.help_webview);
		wv.getSettings().setJavaScriptEnabled(true);
		
        wv.loadUrl("file:///android_asset/www/help.html");   // now it will not fail here
	}
}
