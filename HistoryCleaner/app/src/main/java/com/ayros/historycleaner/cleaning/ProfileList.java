package com.ayros.historycleaner.cleaning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ayros.historycleaner.Globals;

public class ProfileList
{
	public static final String LAST_VIEW = "com.ayros.historycleaner._LAST_VIEW";
	
	public static ArrayList<Profile> profileList = null;
	
	public static Profile create(String name)
	{
		Profile p = get(name);
		if (p != null)
		{
			p.selectedItems = new ArrayList<String>();
			p.saveChanges();
			return p;
		}
		
		p = new Profile(name, new ArrayList<String>());
		profileList.add(p);
		p.saveChanges();
		
		return p;
	}
	
	public static boolean delete(Profile p)
	{
		return delete(p.getName());
	}
	
	public static boolean delete(String name)
	{
		int index = indexOf(name);
		if (index == -1)
		{
			return false;
		}
		
		SharedPreferences prefs = Globals.getContext().getSharedPreferences("Profiles", Activity.MODE_PRIVATE);  
		Editor prefEdit = prefs.edit();
		prefEdit.remove(name);
		profileList.remove(index);
		
		return prefEdit.commit();
	}
	
	public static Profile get(String name)
	{
		if (name == null)
		{
			name = LAST_VIEW;
		}
		
		int index = indexOf(name);
		if (index == -1)
		{
			return null;
		}
		
		return profileList.get(index);
	}
	
	public static List<Profile> getClonedList(boolean includeLastView)
	{
		List<Profile> clonedList = new ArrayList<Profile>();
		
		for (Profile p : profileList)
		{
			if (p.getName().equals(LAST_VIEW) && !includeLastView)
			{
				continue;
			}
			
			clonedList.add(p);
		}
		
		return clonedList;
	}
	
	public static List<String> getNamesList(boolean includeLastView)
	{
		List<Profile> profiles = getClonedList(includeLastView);
		List<String> names = new ArrayList<String>();
		
		for (Profile p : profiles)
		{
			names.add(p.getName());
		}
		
		return names;
	}
	
	public static int indexOf(String findName)
	{
		for (int i = 0; i < profileList.size(); i++)
		{
			if (profileList.get(i).profileName.equals(findName))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public static boolean isLoaded()
	{
		return profileList != null;
	}
	
	public static void load()
	{
		profileList = new ArrayList<Profile>();
		SharedPreferences prefs = Globals.getContext().getSharedPreferences("Profiles", Activity.MODE_PRIVATE);  
		
		if (!prefs.contains(LAST_VIEW))
		{
			Profile lastView = new Profile(LAST_VIEW, new ArrayList<String>());
			lastView.saveChanges();
		}
		
		Map<String, ?> profiles = prefs.getAll();
		for (String key : profiles.keySet())
		{
			Profile p = Profile.fromSerialized((String)profiles.get(key));
			
			profileList.add(p);
		}
	}
}
