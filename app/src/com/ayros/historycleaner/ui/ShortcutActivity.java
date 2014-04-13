package com.ayros.historycleaner.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.R;
import com.ayros.historycleaner.cleaning.ProfileList;

public class ShortcutActivity extends Activity
{
	public static final String SHORTCUT_PROFILE_NAME = "com.ayros.historycleaner.profile_name";
	String selectedProfile = "<none>";

	protected String[] getProfileNames()
	{
		List<String> namesList = ProfileList.getNamesList(false);

		if (namesList.size() > 0)
		{
			return namesList.toArray(new String[namesList.size()]);
		}
		else
		{
			return new String[] { "<none>" };
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Globals.setContext(ShortcutActivity.this.getApplicationContext());
		ProfileList.load();

		final Dialog dialog = new Dialog(this);
		dialog.setTitle("Select Profile");
		dialog.setContentView(R.layout.shortcut_create);

		final TextView txtSelected = (TextView)dialog.findViewById(R.id.shortcut_create_txtSelected);
		txtSelected.setText("Selected Profile: " + selectedProfile);

		final Button btnCancel = (Button)dialog.findViewById(R.id.shortcut_create_btnCancel);
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.cancel();
				ShortcutActivity.this.finish();
			}
		});

		final Button btnOk = (Button)dialog.findViewById(R.id.shortcut_create_btnOk);
		btnOk.setOnClickListener(new OnClickListener()
		{
			@SuppressLint("InlinedApi")
			@Override
			public void onClick(View v)
			{
				if (selectedProfile.equals("<none>"))
				{
					Toast.makeText(ShortcutActivity.this, "Error: You must select a profile first.", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Intent shortcutIntent = new Intent(ShortcutActivity.this, ShortcutCleanActivity.class);
					shortcutIntent.putExtra(SHORTCUT_PROFILE_NAME, selectedProfile);
					shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

					ShortcutIconResource iconResource = Intent.ShortcutIconResource.fromContext(ShortcutActivity.this, R.drawable.clean);
					Intent intent = new Intent();
					intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
					intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
					intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, selectedProfile);
					setResult(RESULT_OK, intent);

					dialog.cancel();
					ShortcutActivity.this.finish();

					Toast.makeText(ShortcutActivity.this, "Shortcut Created", Toast.LENGTH_SHORT).show();
				}
			}
		});

		String[] profileNames = getProfileNames();
		ListView profileList = (ListView)dialog.findViewById(R.id.shortcut_create_lstProfiles);
		profileList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, profileNames));
		profileList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				selectedProfile = ((TextView)v).getText().toString();
				txtSelected.setText("Selected Profile: " + selectedProfile);
			}
		});
		dialog.show();
	}
}