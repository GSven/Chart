package com.sxt.chart.utils;

import android.util.Log;

/**
 * Created by izhaohu on 2017/8/23.
 */

public class LogUtil {

    private static boolean isDebug = true;

    public static void v(String TAG, String log) {
        if (isDebug) {
            Log.v(TAG, log);
        }
    }

    public static void i(String TAG, String log) {
        if (isDebug) {
            Log.i(TAG, log);
        }
    }

    public static void warn(String TAG, String log) {
        if (isDebug) {
            Log.w(TAG, log);
        }
    }

    public static void e(String TAG, String log) {
        if (isDebug) {
            Log.e(TAG, log);
        }
    }
}
