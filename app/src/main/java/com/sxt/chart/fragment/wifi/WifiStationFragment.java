package com.sxt.chart.fragment.wifi;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sxt.chart.R;
import com.sxt.chart.adapter.WifiAdapter;

import java.util.ArrayList;

/**
 * Created by archer on 2016/4/25.
 */
public class WifiStationFragment extends Fragment {

    private ArrayList<ScanResult> scanResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        scanResult = (ArrayList<ScanResult>) bundle.getSerializable("ScanResult");
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
                Bundle bundle = new Bundle();
                bundle.putString(WifiSelectedFragment.KEY_WIFI_SELECTED_SSID, scanResult.SSID);
                bundle.putInt(WifiSelectedFragment.KEY_WIFI_SELECTED_LEVEL, scanResult.level);
                WifiSelectedFragment fragment = new WifiSelectedFragment();
                fragment.setArguments(bundle);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(WifiSelectedFragment.class.getName())
                        .replace(R.id.wifi_setting_content, fragment)
                        .commit();
            }
        });
    }
}
