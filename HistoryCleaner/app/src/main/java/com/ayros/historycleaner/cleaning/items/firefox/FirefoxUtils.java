package com.ayros.historycleaner.cleaning.items.firefox;

import com.ayros.historycleaner.helpers.RootHelper;

import java.io.IOException;

public class FirefoxUtils {
    public static final String FIREFOX_PACKAGE = "org.mozilla.firefox";
    public static final String FIREFOX_BETA_PACKAGE = "org.mozilla.firefox_beta";
    public static final String FIREFOX_NIGHTLY_PACKAGE = "org.mozilla.fennec";

    public static String getFirefoxDataPath(String packageName) throws IOException
    {
        String profiles = RootHelper.getFileContents("/data/data/" + packageName + "/files/mozilla/profiles.ini");
        if (profiles == null)
        {
            throw new IOException("Could not read the FireFox profiles.ini - app hasn't been opened yet");
        }

        String[] lines = profiles.split("\n");
        for (String line : lines)
        {
            if (line.contains("Path="))
            {
                String folder = "/data/data/" + packageName + "/files/mozilla/" + line.replace("Path=", "");
                if (RootHelper.fileOrFolderExists(folder))
                {
                    return folder;
                }
                else
                {
                    throw new IOException("Found FireFox data path, but it doesn't seem to exist: " + folder);
                }
            }
        }

        throw new IOException("Could not find path variable in FireFox profiles.ini");
    }
}
