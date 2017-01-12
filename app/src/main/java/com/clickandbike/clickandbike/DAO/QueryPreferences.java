package com.clickandbike.clickandbike.DAO;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by sredorta on 1/12/2017.
 */
public class QueryPreferences {
    private static Boolean DEBUG_MODE = true;
    private static final String TAG = "QueryPreferences::";

    public static final String PREFERENCE_USER_NAME = "name";
    public static final String PREFERENCE_USER_PASSWORD = "password";

    public static void setPreference(Context context, String preference, String value) {
        if (DEBUG_MODE) Log.i(TAG, "Stored into preferences: " + preference + " : " + value);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(preference, value)
                .apply();
    }

    public static String getPreference(Context context, String preference) {
        if (DEBUG_MODE) Log.i(TAG, "Preference queried : " + preference + " : " +  PreferenceManager.getDefaultSharedPreferences(context).getString(preference, null));
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(preference, null);
    }

}
