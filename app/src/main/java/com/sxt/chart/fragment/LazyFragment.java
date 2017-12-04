package com.sxt.chart.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sxt on 2016/5/17.
 */

public abstract class LazyFragment extends Fragment {
    public View contentView;
    public boolean isInit = false;
    public boolean isFirst = true;
    protected Activity activity;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(getDisplayView(), container, false);
            initView();
            isInit = true;
        }
        readyLoadData();
        return contentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        checkUserVisibleHint(isVisibleToUser);
        readyLoadData();
    }

    private void readyLoadData() {
        if (!isInit) {//setUserVisibleHint( ) 方法是系统自动回调 在fragment生命周期调用之前
            return;
        }
        if (isFirst && getUserVisibleHint()) {
            loadData();
            isFirst = false;//为防止重复加载数据 將标记置为false
        }
    }

    protected void checkUserVisibleHint(boolean isVisibleToUser) {
    }

    /**
     * fragment创建后 主动加载数据 一次
     */
    protected void loadData() {

    }

    /**
     * 后续想要更新数据 须调用此方法 进行刷新
     */
    protected void refreshData() {
        if (contentView != null && isInit) {//说明已经初始化完成了
            //TODO 在这里进行数据刷新
        }
    }

    protected abstract int getDisplayView();

    protected abstract void initView();

}
