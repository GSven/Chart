package com.sxt.chart.fragment.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sxt.chart.R;
import com.sxt.chart.adapter.WifiAdapter;
import com.sxt.chart.utils.UtilsWifi;

import java.util.ArrayList;

/**
 * Created by archer on 2016/4/25.
 */
public class WifiStationFragment extends Fragment {

    private ArrayList<ScanResult> scanResult;
    private WifiManager wifiManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        scanResult = (ArrayList<ScanResult>) bundle.getSerializable("ScanResult");
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi_station, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.wifi_station_list);
        listView.setAdapter(new WifiAdapter(getActivity(), scanResult));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ScanResult scanResult = (ScanResult) adapterView.getAdapter().getItem(position);
                Toast.makeText(getContext().getApplicationContext(), "SSID == " + scanResult.SSID, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 添加一个无线网略
     */
    public void addNetwork(WifiConfiguration wifiCon) {
        wifiManager.addNetwork(wifiCon);
        wifiManager.enableNetwork(wifiCon.networkId, true);
    }

    /**
     * 删除一个无线网络
     *
     * @param networkId
     */
    public void disconnectWifi(int networkId) {
        wifiManager.disableNetwork(networkId);
        wifiManager.disconnect();
    }

    /**
     * 一处无线网路
     *
     * @param networkId
     */
    public void removeNetworkWifi(int networkId) {
        disconnectWifi(networkId);
        wifiManager.removeNetwork(networkId);
    }
}
