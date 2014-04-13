package com.ayros.historycleaner.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ayros.historycleaner.ProfileAdapter;
import com.ayros.historycleaner.R;
import com.ayros.historycleaner.cleaning.Profile;
import com.ayros.historycleaner.cleaning.ProfileList;

public class ProfileFragment extends Fragment implements OnClickListener, OnProfileUpdated
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
			sendProfileUpdatedMessage();
		}
	}

	public static ProfileFragment newInstance()
	{
		ProfileFragment fragment = new ProfileFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.frag_profile, container, false);

		return rootView;
	}

	@Override
	public void onClick(View v)
	{
		final Profile p = (Profile)v.getTag();
		Button btn = (Button)v;

		if (btn.getText().equals(getResources().getString(R.string.profile_list_row_load)))
		{
			AlertDialog.Builder confirmLoad = new AlertDialog.Builder(ProfileFragment.this.getActivity());
			confirmLoad.setTitle("Load Profile");
			confirmLoad.setMessage("Do you want to load this profile? (It will replace the currently selected items)");

			confirmLoad.setPositiveButton(android.R.string.yes, new ConfirmLoadListener(p));
			confirmLoad.setNegativeButton(android.R.string.no, null);

			confirmLoad.show();
		}
		else if (btn.getText().equals(getResources().getString(R.string.profile_list_row_settings)))
		{
			final Dialog profileSettings = new Dialog(ProfileFragment.this.getActivity());
			profileSettings.setTitle("Profile Settings");

			profileSettings.setContentView(R.layout.profile_item_settings);

			Button saveButton = (Button)profileSettings.findViewById(R.id.profile_item_settings_btnSave);
			saveButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Toast.makeText(ProfileFragment.this.getActivity(), "Changes saved successfully!",
						Toast.LENGTH_SHORT).show();
					// p.saveChanges();
					sendProfileUpdatedMessage();
					profileSettings.dismiss();
				}
			});

			Button cancelButton = (Button)profileSettings.findViewById(R.id.profile_item_settings_btnCancel);
			cancelButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					profileSettings.dismiss();
				}
			});

			Button deleteButton = (Button)profileSettings.findViewById(R.id.profile_item_settings_btnDelete);
			deleteButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					ProfileList.delete(p);
					profileSettings.dismiss();
					sendProfileUpdatedMessage();
				}
			});

			profileSettings.show();
		}
	}

	@Override
	public void onProfileUpdated()
	{
		refreshProfileList();
	}

	@Override
	public void onResume()
	{
		super.onResume();

		onProfileUpdated();
	}

	public void refreshProfileList()
	{
		ListView lv = (ListView)this.getView().findViewById(R.id.profile_list);

		List<Profile> profList = ProfileList.getClonedList(false);

		Profile[] profData = profList.toArray(new Profile[profList.size()]);

		ProfileAdapter adapter = new ProfileAdapter(this.getActivity(), R.layout.profile_list_item, profData, this);

		lv.setAdapter(adapter);
	}

	public void sendProfileUpdatedMessage()
	{
		((OnProfileUpdated)getActivity()).onProfileUpdated();
	}
}
