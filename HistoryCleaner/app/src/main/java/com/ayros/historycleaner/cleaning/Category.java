package com.ayros.historycleaner.cleaning;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ayros.historycleaner.R;

public class Category
{
	private String name;
	private List<CleanItem> items = new ArrayList<CleanItem>();
	private ViewGroup titleView = null;
	
	public Category(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<CleanItem> getItems()
	{
		return items;
	}
	
	public void addItem(CleanItem item)
	{
		items.add(item);
	}
	
	public View getCategoryView(Context c)
	{
		titleView = (ViewGroup)View.inflate(c, R.layout.category_heading, null);

		TextView tv = (TextView)titleView.findViewById(R.id.category_name);
		tv.setText(getName() + " (" + getItems().size() + ")");
		
		for (int i = 0; i < items.size(); i++)
		{
			titleView.addView(items.get(i).getItemView(c, i != items.size() - 1));
		}
		
		return titleView;
	}
}
