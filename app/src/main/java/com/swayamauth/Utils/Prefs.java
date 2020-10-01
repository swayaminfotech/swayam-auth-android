package com.swayamauth.Utils;

/**
 * Created by swayam infotech
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


/*
*  Store Value in Preferences
* */

public class Prefs
{

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        return preferences.getString(key, defValue);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        return preferences.getBoolean(key, defValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Integer getInt(Context context, String key, int defValue) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        return preferences.getInt(key, defValue);
    }

    public static void putInt(Context context, String key, Integer value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putInt(key, value).commit();
    }

    public static void putFloat(Context context, String key, float value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putFloat(key, value).commit();
    }

    public static float getFloat(Context context, String key, float defValue) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        return preferences.getFloat(key, defValue);
    }

    public static void putLong(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putLong(key, value).commit();
    }

    public static long getLong(Context context, String key, long defValue) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        return preferences.getLong(key, defValue);
    }

    public static void remove(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.remove(key);
    }

    public static void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        editor.commit();
    }

}
