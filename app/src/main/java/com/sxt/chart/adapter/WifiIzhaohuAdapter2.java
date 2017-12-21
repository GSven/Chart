package com.sxt.chart.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by izhaohu on 2017/8/4.
 */

public class WifiIzhaohuAdapter2 extends RecyclerView.Adapter {

    private Context context;
    private List<String> scanResults;
    private OnItemClickListener onItemClickListener;

    public WifiIzhaohuAdapter2(Context context, List<String> scanResults) {
        this.context = context;
        this.scanResults = scanResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select_wifi, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;

//        if (position == 0) {
//            holder.line.setVisibility(View.GONE);
//        } else {
//            holder.line.setVisibility(View.VISIBLE);
//        }
//        holder.tvWifiName.setText(scanResults.get(position));
//        holder.root.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onItemClickListener != null) {
//                    onItemClickListener.onItemClick(position, holder, scanResults);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return scanResults == null ? 0 : scanResults.size();
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
