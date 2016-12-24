package com.ayros.historycleaner.cleaning;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

public interface CleanItem
{
    void clean() throws IOException;

    String getDataPath();

    String getDisplayName();

    Drawable getIcon();

    String getPackageName();

    int getUniqueId();

    String getUniqueName();

    String getWarningMessage();

    ViewGroup getView();

    List<String[]> getSavedData() throws IOException;

    boolean isApplicable();

    boolean isChecked();

    boolean isRootRequired();

    boolean killProcess();

    boolean killProcessAfterCleaning();

    void postClean();

    boolean runOnUIThread();

    void setChecked(boolean checked);

    View getItemView(final Context c, boolean showDivider);
}