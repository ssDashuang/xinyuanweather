package com.dashuang.xinyuan.xinyuanweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by chx on 2017/5/31.
 */

public class PrefUtil {

    private static SharedPreferences pref = null;

    public static boolean putString(Context ctx,String key,String data){
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(key, data);
            editor.apply();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public static void putSetString(Context ctx,String key,Set<String> set){
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = pref.edit();
            editor.putStringSet(key,set);
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Set<String> getSetString(Context ctx,String key){
        pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        Set<String> set = pref.getStringSet(key,null);
        return  set;
    }
    public static String getString(Context ctx,String key){
        pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String weather = pref.getString(key,null);
        return  weather;
    }

    public static boolean putBoolean(Context ctx,String key,boolean b){
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(key, b);
            editor.apply();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public static boolean getBoolean(Context ctx,String key){
        pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean b = pref.getBoolean(key,false);
        return  b;
    }
    public static boolean putInt(Context ctx,String key,int data){
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(key, data);
            editor.apply();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public static int getInt(Context ctx,String key){
        pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        int data = pref.getInt(key,0);
        return  data;
    }
}
