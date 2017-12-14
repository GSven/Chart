package com.sxt.chart.fragment.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sxt.chart.R;

import java.util.ArrayList;

/**
 * Created by archer on 2016/4/20.
 */
public class WifiSearchFragment extends Fragment {

    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWifiSettings();
    }

    private void initWifiSettings() {
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.setPriority(1000);
        getActivity().registerReceiver(wifiReceiver, filter);

        wifiManager.startScan();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi_search, null);
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<ScanResult> scanResults = (ArrayList<ScanResult>) wifiManager.getScanResults();
            Log.i("wifi", String.valueOf(scanResults.size()));
        }
    }

    @Override
    public void onDestroy() {
        if (wifiReceiver != null) {
            getActivity().unregisterReceiver(wifiReceiver);
        }
        super.onDestroy();
    }
}
