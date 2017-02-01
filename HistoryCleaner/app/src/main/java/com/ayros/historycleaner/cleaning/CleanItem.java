package com.ayros.historycleaner.cleaning;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface CleanItem
{
    void clean() throws IOException;

    String getDataPath();

    String getDisplayName();

    Drawable getIcon();

    String getPackageName();

    int getUniqueId();

    String getUniqueName();

    Optional<String> getWarningMessage();

    ViewGroup getView();

    List<String[]> getSavedData() throws IOException, UnsupportedOperationException;

    Set<String> getRequiredPermissions();

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