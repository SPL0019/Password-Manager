package com.samcore.passwordmanager.components;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSession {
    public static final String PREF_NAME="SessionPref";
    public static final String KEY_IS_LOGGED_IN="isLoggedIn";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static  final String KEY_USERNAME="username";
    private static  final String KEY_UID="uid";

    public AppSession(Context context) {
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

    public void setKeyUsername(String username){
        editor.putString(KEY_USERNAME,username);
        editor.apply();
    }
    public String getKeyUsername(){
        return sharedPreferences.getString(KEY_USERNAME,"");
    }
    public void setKeyUid(String uid){
        editor.putString(KEY_UID,uid);
        editor.apply();
    }
    public String getKeyUid(){
        return sharedPreferences.getString(KEY_UID,"");
    }
}
