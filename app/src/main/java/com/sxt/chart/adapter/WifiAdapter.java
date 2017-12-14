package com.sxt.chart.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chart.R;

import java.util.List;

/**
 * Created by izhaohu on 2017/12/14.
 */

public class WifiAdapter extends BaseAdapter {

    private List<ScanResult> data;
    private Context context;

    public WifiAdapter(Context context, List<ScanResult> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_wifi, viewGroup,false);
            holder = new ViewHolder();
            holder.wifiRoot = view.findViewById(R.id.wifi_root);
            holder.wifiLine = view.findViewById(R.id.wifi_line);
            holder.wifiLevel = (ImageView) view.findViewById(R.id.wifi_level);
            holder.wifiSsid = (TextView) view.findViewById(R.id.wifi_ssid);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.wifiSsid.setText(data.get(i).BSSID);
        if (i == data.size() - 1) {
            holder.wifiLine.setVisibility(View.GONE);
        } else {
            holder.wifiLine.setVisibility(View.VISIBLE);
        }


        return view;
    }

    class ViewHolder {
        private TextView wifiSsid;
        private ImageView wifiLevel;
        private View wifiRoot;
        private View wifiLine;
    }
}
