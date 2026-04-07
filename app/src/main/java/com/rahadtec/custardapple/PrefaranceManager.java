package com.rahadtec.custardapple;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefaranceManager {

    private static final String PREF_NAME = "CustardApplePrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PrefaranceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserLogin(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // ইউজারনেম ফিরে পাওয়া (ডেটা না থাকলে null দিবে, Guest নয়)
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    // ইউজার লগইন করা আছে কি না চেক করা
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // লগআউট করার সময় ডেটা মুছে ফেলা
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}