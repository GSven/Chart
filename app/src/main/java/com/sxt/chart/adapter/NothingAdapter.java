package com.sxt.chart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chart.R;

import java.util.List;

/**
 * Created by sxt on 2018/1/3.
 */

public class NothingAdapter extends ListViewAdapter<String> {

    public NothingAdapter(Context context, List<String> dates) {
        super(context, dates);
    }

    @Override
    public View getView(int position, View itemView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_select_wifi, viewGroup, false);
            holder = new ViewHolder();
            holder.root = itemView.findViewById(R.id.item_select_wifi_root);
            holder.line = itemView.findViewById(R.id.item_wifi_line);
            holder.tvWifiName = (TextView) itemView.findViewById(R.id.item_wifi_name);
            holder.tvWifiType = (TextView) itemView.findViewById(R.id.item_wifi_type);
            holder.imgWifiLevel = (ImageView) itemView.findViewById(R.id.item_wifi_level);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        return itemView;
    }

    public class ViewHolder {

        public View root;
        public View line;
        public TextView tvWifiName;
        public TextView tvWifiType;
        public ImageView imgWifiLevel;
    }
}
