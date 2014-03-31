package com.ayros.historycleaner.cleaning.items;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.ClipboardManager;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.R;
import com.ayros.historycleaner.cleaning.Category;
import com.ayros.historycleaner.cleaning.CleanItem;

@SuppressWarnings("deprecation")
public class _System_Clipboard extends CleanItem
{
	public _System_Clipboard(Category parent)
	{
		super(parent);
	}
	
	@Override
	public String getDisplayName()
	{
		return "Clipboard";
	}
	
	@Override
	public Drawable getIcon()
	{
		return Globals.getContext().getResources().getDrawable(R.drawable.system_clipboard);
	}
	
	@Override
	public boolean isApplicable()
	{
		return true;
	}
	
	@Override
	public boolean runOnUIThread()
	{
		return true;
	}
	
	@Override
	public boolean isRootRequired()
	{
		return false;
	}
	
	@Override
	public List<String []> getSavedData()
	{
		ClipboardManager clipboard = (ClipboardManager)Globals.getContext().getSystemService(Context.CLIPBOARD_SERVICE); 
		CharSequence clip = clipboard.getText();
		if (clip == null)
		{
			List<String []> ret = new ArrayList<String []>();
			ret.add(new String[] { });
			ret.add(new String[] { "No text on clipboard." });
			return ret;
		}
		else
		{
			List<String []> ret = new ArrayList<String []>();
			ret.add(new String[] { });
			ret.add(new String[] { "Clipboard text: " + clip });
			return ret;
		}
	}
	
	@Override
	public boolean clean()
	{
		ClipboardManager clipboard = (ClipboardManager)Globals.getContext().getSystemService(Context.CLIPBOARD_SERVICE); 
		clipboard.setText(null);
		
		return true;
	}
}