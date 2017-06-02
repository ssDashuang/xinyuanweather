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
}
