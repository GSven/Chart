package com.sxt.chart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.sxt.chart.R;
import com.sxt.chart.base.BaseRecyclerAdapter;

/**
 * Created by izhaohu on 2018/3/1.
 */

public class MainAdapter extends BaseRecyclerAdapter {
    public MainAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 8;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.item_main, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        if (onClickListener != null) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(position, holder, null);
                }
            });
        }
    }

    public class ViewHolder extends BaseRecyclerAdapter.ViewHolder {

        public View root;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
        }
    }

}
