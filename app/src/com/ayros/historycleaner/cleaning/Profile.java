package com.ayros.historycleaner.cleaning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.helpers.Helper;

public class Profile
{
	String profileName;
	ArrayList<String> selectedItems;

	Profile(String name, ArrayList<String> items)
	{
		profileName = name;
		selectedItems = items;
	}

	@SuppressWarnings("unchecked")
	public void copyFrom(Profile p)
	{
		selectedItems = (ArrayList<String>)p.selectedItems.clone();
	}

	public String getName()
	{
		return profileName;
	}

	public int getNumItems()
	{
		return selectedItems.size();
	}

	@SuppressWarnings("unchecked")
	public List<String> getItemNames()
	{
		return (List<String>)selectedItems.clone();
	}

	public String getSerialized()
	{
		String output = Helper.urlEncode(profileName) + "|";

		for (String item : selectedItems)
		{
			output += item + "|";
		}

		if (output.length() > 0)
		{
			return output.substring(0, output.length() - 1);
		}
		else
		{
			return output;
		}
	}

	public static Profile fromSerialized(String rawData)
	{
		List<String> tokens = Arrays.asList(rawData.split("\\|"));
		for (int i = 0; i < tokens.size(); i++)
		{
			tokens.set(i, Helper.urlDecode(tokens.get(i)));
		}

		String pName = tokens.get(0);
		ArrayList<String> pItems = new ArrayList<String>();

		for (int i = 1; i < tokens.size(); i++)
		{
			pItems.add(tokens.get(i));
		}

		return new Profile(pName, pItems);
	}

	public boolean isSelected(CleanItem item)
	{
		return selectedItems.contains(item.getUniqueName());
	}

	public boolean saveChanges()
	{
		SharedPreferences prefs = Globals.getContext().getSharedPreferences("Profiles", Activity.MODE_PRIVATE);
		Editor prefEdit = prefs.edit();

		prefEdit.putString(profileName, getSerialized());

		return prefEdit.commit();
	}

	public void setItems(ArrayList<String> items)
	{
		this.selectedItems = items;
	}
}
