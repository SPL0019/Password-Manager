package com.samcore.passwordmanager;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSession {
    public static final String PREF_NAME="SessionPref";
    public static final String KEY_IS_LOGGED_IN="isLoggedIn";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final Context context;
    public AppSession(Context context) {
        this.context=context;
        sharedPreferences=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor= sharedPreferences.edit();
    }
    public void setKeyIsLoggedIn(boolean isLoggedIn){
        editor.putBoolean(KEY_IS_LOGGED_IN,isLoggedIn);
        editor.apply();
    }
    public boolean isLoggedIN(){
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN,false);
    }
}
