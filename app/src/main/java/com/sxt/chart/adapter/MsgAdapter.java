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
 * Created by sxt on 2018/1/7.
 */

public class MsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> data;
    private Context context;
    private OnItemClickListener onItemClickLister;
    private OnTextViewClickListener onTextViewClickListener;

    public MsgAdapter(Context context, List<String> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        if (position == getItemCount() - 1) {
            holder.line.setVisibility(View.INVISIBLE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }

        if (getItemViewType(position) == 0) {
            holder.title.setText("积分消息");
            holder.content.setText("立夏今天康乐活动积极给予10积分奖励…");
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText("11:23");
            holder.state.setVisibility(View.INVISIBLE);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickLister != null) {
                        onItemClickLister.onItemClick(position, holder);
                    }
                }
            });
        } else {
            holder.title.setText("林允");
            holder.content.setText("冯绍峰巴拉巴的朋友请求关注他…");
            holder.state.setVisibility(View.VISIBLE);
            holder.state.setText("已同意");
            holder.date.setVisibility(View.INVISIBLE);
            holder.state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onTextViewClickListener != null) {
                        onTextViewClickListener.onClick(position, holder);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).startsWith("TYPE_1")) {
            return 0;
        } else {
            return 1;
        }
    }

    public void setOnItemClickLister(OnItemClickListener onItemClickLister) {
        this.onItemClickLister = onItemClickLister;
    }


    public void setOnTextViewClickListener(OnTextViewClickListener onTextViewClickListener) {
        this.onTextViewClickListener = onTextViewClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Object object);
    }

    public interface OnTextViewClickListener {
        void onClick(int position, Object object);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final View root;
        private final ImageView img;
        private final TextView title;
        private final TextView content;
        private final View line;
        private final TextView date;
        private final TextView state;

        public ViewHolder(View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.root);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
            line = itemView.findViewById(R.id.line);
            content = itemView.findViewById(R.id.content);
            date = itemView.findViewById(R.id.date);
            state = itemView.findViewById(R.id.state);
        }
    }
}
