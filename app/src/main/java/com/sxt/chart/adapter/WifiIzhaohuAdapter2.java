package com.sxt.chart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chart.R;

import java.util.List;

/**
 * Created by izhaohu on 2017/8/4.
 */

public class WifiIzhaohuAdapter2 extends BaseRecyclerAdapter<String> {

    private Context context;
    private List<String> scanResults;
    private OnItemClickListener onItemClickListener;

    public WifiIzhaohuAdapter2(Context context, List<String> scanResults) {
        super(context, scanResults);
        this.context = context;
        this.scanResults = scanResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select_wifi, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ViewHolder holder, Object object);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public View line;
        public TextView tvWifiName;
        public TextView tvWifiType;
        public ImageView imgWifiLevel;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.item_select_wifi_root);
            line = itemView.findViewById(R.id.item_wifi_line);
            tvWifiName = (TextView) itemView.findViewById(R.id.item_wifi_name);
            tvWifiType = (TextView) itemView.findViewById(R.id.item_wifi_type);
            imgWifiLevel = (ImageView) itemView.findViewById(R.id.item_wifi_level);
        }
    }

}
