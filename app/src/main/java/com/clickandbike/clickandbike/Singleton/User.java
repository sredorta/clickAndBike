package com.clickandbike.clickandbike.Singleton;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.Location;
import android.util.Log;

import com.clickandbike.clickandbike.DAO.QueryPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sredorta on 1/12/2017.
 */
public class User {
    private static Boolean DEBUG_MODE = true;
    private final String TAG = this.getClass().getSimpleName() + "::";

    private Context mContext;
    public static String uFirstName;            // FirstName of the User
    public static String uLastName;             // FirstName of the User
    public static String uEmail;                // Email of the User
    public static String uToken;                // Last User token
    public static Location uLocation;           // Last location of the User


    //Unique instance of this class (Singleton)
    private static User mUser = new User();

    //Private constructor to avoid external calls
    private User() {}

    //Method to get the only instance of Locker
    public static User getUser() {
        return mUser;
    }

    //Inits the singleton (to be called only once in the app !)
    public void init(Context context) {
        mContext = context;
        User me = User.getUser();
        me.getFromPreferences();
    }

    public void saveToPreferences() {
        QueryPreferences.setPreference(mContext,QueryPreferences.PREFERENCE_USER_FIRST_NAME, User.uFirstName);
        QueryPreferences.setPreference(mContext,QueryPreferences.PREFERENCE_USER_LAST_NAME, User.uLastName);
        QueryPreferences.setPreference(mContext,QueryPreferences.PREFERENCE_USER_EMAIL, User.uEmail);
    }

    public void getFromPreferences() {
        User.uFirstName = QueryPreferences.getPreference(mContext,QueryPreferences.PREFERENCE_USER_FIRST_NAME);
        User.uLastName = QueryPreferences.getPreference(mContext,QueryPreferences.PREFERENCE_USER_LAST_NAME);
        User.uEmail = QueryPreferences.getPreference(mContext,QueryPreferences.PREFERENCE_USER_EMAIL);
    }


}
