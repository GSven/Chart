package com.sxt.chart.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by izhaohu on 2017/11/16.
 */

public class WatchDogReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent in = new Intent(context, MainActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
        }
    }
}
