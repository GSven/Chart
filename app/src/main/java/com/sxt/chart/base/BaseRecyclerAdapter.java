package com.sxt.chart.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by izhaohu on 2018/1/19.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> data;
    protected Context context;
    private LayoutInflater mInflater;
    protected OnClickListener onClickListener;
    private ContentObserVer contentObserVer;

    public BaseRecyclerAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public BaseRecyclerAdapter(Context context, List<T> data) {
        this(context);
        this.data = data;
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    @Override
    public abstract android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewHolder, int position);

    @Override
    public int getItemCount() {
        int count = data == null ? 0 : data.size();
        if (contentObserVer != null) {
            contentObserVer.notify(count, this.data);
        }
        return count;
    }

    public T getItem(int position) {
        if (data != null && data.size() > position) {
            return data.get(position);
        }
        return null;
    }

    public void notifyDataSetChanged(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, ViewHolder holder, Object object);
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setContentObserver(ContentObserVer contentObserVer) {
        this.contentObserVer = contentObserVer;
    }

    public interface ContentObserVer {
        void notify(int count, Object object);
    }
}
