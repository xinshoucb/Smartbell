package com.smartbell.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cb on 17-5-11.
 */

public class SPUtil {
    private static final String PREFERENCES_NAME = "com.smartbell.preferences";
    public static Context sContext;

    public static void savePreference(String key, int value) {
        SharedPreferences s = sContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        s.edit().putInt(key, value).apply();
    }

    public static int getPreference(String key, int defValue) {
        SharedPreferences s = sContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return s.getInt(key, defValue);
    }
}
