package com.clickandbike.clickandbike.Singleton;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.Location;
import android.util.Log;

import com.clickandbike.clickandbike.DAO.QueryPreferences;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sredorta on 1/12/2017.
 */
public class User {
    private static Boolean DEBUG_MODE = true;
    private final static String TAG =  "User ::";

    private Context mContext;
    public static Integer uId;                  // Id as in the database
    public static String uFirstName;            // FirstName of the User
    public static String uLastName;             // FirstName of the User
    public static String uAccountName;          // Contains the account name during creation email or password
    public static String uPhone;                // Phone of the User
    public static String uEmail;                // Email of the User
    public static String uPassword;
    public static String uToken;                // Token is handled by account manager !!!!!!!!
    public static Location uLocation;           // Last location of the User


    //Unique instance of this class (Singleton)
    private static User mUser = new User();

    //Private constructor to avoid external calls
    private User() {}

    //Method to get the only instance of User
    public static User getUser() {
        return mUser;
    }

    //Inits the singleton (to be called only once in the app !)
    public void init(Context context) {
        mContext = context;
        User me = User.getUser();
        me.getFromPreferences();
    }

    //Sets the correct field depending on the entry
    public static void setEmailOrPhone(String email_or_phone) {
        Pattern r;
        Matcher m;
        r = Pattern.compile("^.*@.*");
        m = r.matcher(email_or_phone);
        if (m.find()) {
            if (DEBUG_MODE) Log.i(TAG, "Found @ so setting email");
            User.uEmail = email_or_phone;
        } else {
            if (DEBUG_MODE) Log.i(TAG, "Not found @ so setting phone");
            User.uPhone = email_or_phone;
        }
    }
    //Encrypts the password
    public static String sha1Password(String password) {
        // Needs to be done !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
       // String myHash = Hashing.sha1.hashString(password, Charsets.UTF_8).toString();
        return password;
    }

    //Check that password meets the required format
    //  8 chars min
    //  2 numbers min
    //  2 lowercase chars min
    //  2 upercase chars min
    public static boolean checkPasswordInput(String password) {
        //Check the length
        if (password.length() < 8) {
            return false;
        }

        //Check that contains at least 2 numbers
        Pattern r;
        Matcher m;
        r = Pattern.compile("[0-9]");
        m = r.matcher(password);
        int count = 0;
        while (m.find()) count++;
        if (count < 2) return false;

        //Check that at least 2 lowercase characters
        r = Pattern.compile("[a-z]");
        m = r.matcher(password);
        count = 0;
        while (m.find()) count++;
        if (count < 2) return false;

        //Check that at least 2 uppercase characters
        r = Pattern.compile("[A-Z]");
        m = r.matcher(password);
        count = 0;
        while (m.find()) count++;
        if (count < 2) return false;
        return true;
    }

    //Check that email meets the required format
    //  @ must exist
    //  . must exist
    //  8 chars min
    public static boolean checkEmailInput(String email) {
        //Check the length
        if (email.length() < 8) {
            return false;
        }

        //Check that contains at least 2 numbers
        Pattern r;
        Matcher m;
        r = Pattern.compile("@");
        m = r.matcher(email);
        int count = 0;
        while (m.find()) count++;
        if (count != 1) return false;

        //Check that at least 2 lowercase characters
        r = Pattern.compile("\\.[a-z]+$");
        m = r.matcher(email);
        if (!m.find()) {
            Log.i(TAG, "Could not found .com !");
            return false;
        }
        return true;
    }

    //Check that phone meets the required format
    //  8 numbers min
    //  only numbers
    public static boolean checkPhoneInput(String number) {
        //Check the length
        if (number.length() < 8) {
            return false;
        }

        //Check that contains at least 2 numbers
        Pattern r;
        Matcher m;
        r = Pattern.compile("[0-9]");
        m = r.matcher(number);
        int count = 0;
        while (m.find()) count++;
        if (count != number.length()) return false;

        return true;
    }




    public void saveToPreferences() {
        if (User.uFirstName != null)
            QueryPreferences.setPreference(mContext,QueryPreferences.PREFERENCE_USER_FIRST_NAME, User.uFirstName);
        if (User.uLastName != null)
            QueryPreferences.setPreference(mContext,QueryPreferences.PREFERENCE_USER_LAST_NAME, User.uLastName);
        if (User.uAccountName != null)
            QueryPreferences.setPreference(mContext,QueryPreferences.PREFERENCE_USER_ACCOUNT_NAME, User.uAccountName);
        if (User.uEmail != null)
            QueryPreferences.setPreference(mContext,QueryPreferences.PREFERENCE_USER_EMAIL, User.uEmail);
        if (User.uPhone != null)
            QueryPreferences.setPreference(mContext,QueryPreferences.PREFERENCE_USER_PHONE, User.uPhone);
        //We don't save password but Token !
    }

    public void getFromPreferences() {
        User.uFirstName = QueryPreferences.getPreference(mContext,QueryPreferences.PREFERENCE_USER_FIRST_NAME);
        User.uLastName = QueryPreferences.getPreference(mContext,QueryPreferences.PREFERENCE_USER_LAST_NAME);
        User.uAccountName = QueryPreferences.getPreference(mContext,QueryPreferences.PREFERENCE_USER_ACCOUNT_NAME);
        User.uEmail = QueryPreferences.getPreference(mContext,QueryPreferences.PREFERENCE_USER_EMAIL);
        User.uPhone = QueryPreferences.getPreference(mContext,QueryPreferences.PREFERENCE_USER_PHONE);

    }


}
