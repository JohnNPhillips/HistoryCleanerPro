package com.ayros.historycleaner.cleaning;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayros.historycleaner.Globals;
import com.ayros.historycleaner.R;
import com.ayros.historycleaner.helpers.Helper;

public abstract class CleanItem
{
	protected final Category parentCat;
	protected ViewGroup itemView = null;
	protected CheckBox itemEnabled = null;

	public CleanItem(Category parent)
	{
		parentCat = parent;
	}

	// Override
	public boolean clean()
	{
		return false;
	}

	public String getDataPath()
	{
		return "/data/data/" + getPackageName();
	}

	// Override
	public abstract String getDisplayName();

	public Drawable getIcon()
	{
		return Helper.getPackageIcon(getPackageName());
	}

	// Override
	public String getPackageName()
	{
		return "";
	}

	public int getUniqueId()
	{
		return getUniqueName().hashCode();
	}

	public String getUniqueName()
	{
		return parentCat.getName() + ":" + getDisplayName();
	}

	/**
	 * A warning message to be displayed when the item is selected. Should be
	 * used for cleaning volatile items such as SMS history, saved passwords,
	 * etc.
	 * 
	 * @return Warning message or null
	 */
	public String getWarningMessage()
	{
		return null;
	}

	public ViewGroup getView()
	{
		return itemView;
	}

	// Override
	public List<String[]> getSavedData()
	{
		return null;
	}

	public boolean isApplicable()
	{
		return Helper.isPackageInstalled(getPackageName());
	}

	public boolean isChecked()
	{
		return itemEnabled.isChecked();
	}

	public boolean isRootRequired()
	{
		return true;
	}

	public boolean killProcess()
	{
		if (getPackageName() == null || getPackageName().length() == 0)
		{
			return false;
		}

		ActivityManager manager = (ActivityManager)Globals.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		manager.killBackgroundProcesses(getPackageName());

		return true;
	}

	/**
	 * Whether the process should be killed after the item is cleaned. By default it returns true.
	 * @return
	 */
	public boolean killProcessAfterCleaning()
	{
		return true;
	}

	/**
	 * Should be called after cleaning the item. By default it kills the
	 * application's process
	 */
	public void postClean()
	{
		if (killProcessAfterCleaning())
		{
			killProcess();
		}
	}

	public boolean runOnUIThread()
	{
		return false;
	}

	public void setChecked(boolean checked)
	{
		// setEnabled(checked) not working?
		if (checked != itemEnabled.isChecked())
		{
			itemEnabled.toggle();
		}
	}

	public void toggleChecked()
	{
		itemEnabled.toggle();
	}

	public View getItemView(final Context c, boolean showDivider)
	{
		itemView = (ViewGroup)View.inflate(c, R.layout.category_item, null);

		itemEnabled = (CheckBox)itemView.findViewById(R.id.enabled);

		itemView.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				itemEnabled.toggle();
			}
		});

		ImageView itemIcon = (ImageView)itemView.findViewById(R.id.item_icon);
		itemIcon.setImageDrawable(getIcon());
		itemIcon.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				itemEnabled.toggle();
			}
		});

		TextView itemName = (TextView)itemView.findViewById(R.id.item_name);
		itemName.setText(getDisplayName());
		itemName.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				itemEnabled.toggle();
			}
		});

		itemEnabled.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked && getWarningMessage() != null)
				{
					Toast.makeText(c, "WARNING: " + getWarningMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});

		if (!showDivider)
		{
			View divider = (View)itemView.findViewById(R.id.dividerLine);
			divider.setVisibility(View.GONE);
		}

		return itemView;
	}
}
