package com.example.music8027;

import android.content.Context;
import android.content.SharedPreferences;

public class DataManager {
    private static final String PREF_NAME = "userDetail";
    private static final String KEY_USER_STRING = null;

    private static DataManager instance;
    private SharedPreferences sharedPreferences;

    private DataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    public String getUserID() {
        return sharedPreferences.getString(KEY_USER_STRING, null);
    }

    public void setUserID(String userString) {
        sharedPreferences.edit().putString(KEY_USER_STRING, userString).apply();
    }
}