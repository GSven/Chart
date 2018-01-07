package com.sxt.chart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxt.chart.R;

import java.util.List;

/**
 * Created by sxt on 2018/1/7.
 */

public class ScoreMsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> data;
    private Context context;

    public ScoreMsgAdapter(Context context, List<String> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_msg_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;


        if (position > 0) {
            if (data.get(position).endsWith(data.get(position - 1))) {
                holder.date.setVisibility(View.INVISIBLE);
                holder.date.setText("");
            } else {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(data.get(position));
            }
            holder.content.setText("假如你避免不了,就得去忍受不能假如你避免不了,就得去忍受不能忍受生命中注定 要忍受的事情,就是软弱和愚蠢的表现。即使整个 世界恨你");

        } else {
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(data.get(position));
            holder.content.setText("假如你避免不了,就得去忍受不能忍受生命中注定 要忍受的事情,就是软弱和愚蠢的表现。即使整个 世界恨你");
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final View root;

        private final TextView content;
        private final TextView date;

        public ViewHolder(View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.root);
            content = itemView.findViewById(R.id.content);
            date = itemView.findViewById(R.id.date);
        }
    }
}
