package com.sxt.chart.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sxt.chart.R;
import com.sxt.chart.fragment.wifi.WifiDescriptionFragment;

import java.util.List;

/**
 * Created by izhaohu on 2017/12/14.
 */

public class WifiSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifi_setting);
        createdDescriptionFragment();
    }

    private void createdDescriptionFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.wifi_setting_content, new WifiDescriptionFragment(), WifiDescriptionFragment.class.getName())
//                .addToBackStack(WifiDescriptionFragment.class.getName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            super.onBackPressed();
        } else {
            //一个参数为null时，第二个参数为0时： 会弹出回退栈中最上层的那一个fragment。
            getSupportFragmentManager().popBackStack(null, 0);
        }
    }
}
