package com.sxt.chart.utils;

import android.content.Context;

public abstract class AbsUtil {
    
    protected static Context mCtx;
    
    public static void init(Context ctx){
        mCtx = ctx;
    }
    
}
