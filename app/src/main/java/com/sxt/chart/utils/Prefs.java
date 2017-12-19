package com.sxt.chart.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by izhaohu on 2017/11/27.
 */

public class Prefs {

    public static final String IS_FRIST_ENTER = "IS_FRIST_ENTER";
    public static final String EXIST_TIME = "EXIST_TIME";

    private static Prefs prefs = new Prefs();
    private static SharedPreferences sp;
    private static final String SPNAME = "SP_NAME";
    public static String KEY_WIFI_SSID = "KEY_WIFI_SSID";
    public static String KEY_WIFI_PWD="KEY_WIFI_PWD";

    public synchronized static Prefs getInstance(Context context) {
        if (prefs == null) {
            prefs = new Prefs();
        }
        if (sp == null) {
            sp = context.getSharedPreferences(SPNAME, MODE_PRIVATE);
        }
        return prefs;
    }

    public void putString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public Boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }
}