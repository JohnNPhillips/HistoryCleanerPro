package com.ayros.historycleaner;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ayros.historycleaner.cleaning.Profile;

public class ProfileAdapter extends ArrayAdapter<Profile>
{
	Context context;
	int layoutResourceId;
	Profile data[] = null;
	OnClickListener buttonCallback = null;

	public ProfileAdapter(Context context, int layoutResourceId, Profile[] data, OnClickListener buttonCallback)
	{
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		this.buttonCallback = buttonCallback;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		ProfileItemUI holder = null;

		if (row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new ProfileItemUI();
			holder.txtName = (TextView)row.findViewById(R.id.profile_list_txtName);
			holder.txtInfo = (TextView)row.findViewById(R.id.profile_list_txtInfo);
			holder.txtSchedule = (TextView)row.findViewById(R.id.profile_list_txtSchedule);
			holder.btnLoad = (Button)row.findViewById(R.id.profile_list_btnLoad);
			holder.btnSettings = (Button)row.findViewById(R.id.profile_list_btnSettings);

			row.setTag(holder);
		}
		else
		{
			holder = (ProfileItemUI)row.getTag();
		}

		Profile profile = data[position];
		holder.txtName.setText(profile.getName());
		//holder.txtName.setText("Clear Widget Test");
		holder.txtInfo.setText(profile.getNumItems() + " selected items");
		holder.txtSchedule.setText("Not scheduled");

		holder.btnLoad.setTag(profile);
		holder.btnLoad.setOnClickListener(buttonCallback);
		
		holder.btnSettings.setTag(profile);
		holder.btnSettings.setOnClickListener(buttonCallback);
		
		// holder.txtSchedule.setText("Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday at 3:00AM");

		return row;
	}

	static class ProfileItemUI
	{
		TextView txtName;
		TextView txtInfo;
		TextView txtSchedule;
		Button btnLoad;
		Button btnSettings;
	}
}
