package com.sxt.chart.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by izhaohu on 2017/11/27.
 */

public abstract class ListViewAdapter<T> extends BaseAdapter {

    protected Context context;
    protected List<T> dates;

    public ListViewAdapter(Context context, List<T> dates) {
        this.context = context;
        this.dates = dates;
    }

    @Override
    public int getCount() {
        return dates == null ? 0 : dates.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
