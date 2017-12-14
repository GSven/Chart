package com.sxt.chart.fragment.wifi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sxt.chart.R;
import com.sxt.chart.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by archer on 2016/4/20.
 */
public class WifiSearchFragment extends Fragment {

    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private final int WIFI_SCAN_PERMISSION_CODE = 100;

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
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);//开启WIFI
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 获取wifi连接需要定位权限,没有获取权限
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                }, WIFI_SCAN_PERMISSION_CODE);
            }
        } else {
            wifiManager.startScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WIFI_SCAN_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 允许
                    wifiManager.startScan();

                } else {
                    // 不允许
                    Toast.makeText(getActivity(), "权限取消", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
            LogUtil.i("wifi", "" + scanResults.size());
            WifiStationFragment fragment = new WifiStationFragment();
            Bundle arguments = new Bundle();
            arguments.putSerializable("ScanResult", scanResults);
            fragment.setArguments(arguments);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.wifi_setting_content, fragment)
                    .commit();
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
