package com.sxt.chart.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sxt.chart.App;
import com.sxt.chart.R;
import com.sxt.chart.adapter.NothingAdapter;
import com.sxt.chart.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sxt on 2018/1/3.
 */

public class PullToRefreshActivity extends BaseActivity {

    private PullToRefreshListView listView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_to_refersh_layout);
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        listView = findViewById(R.id.listView);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            data.add(String.valueOf(i));
        }

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout startLabels = listView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");
        startLabels.setRefreshingLabel("正在拉");
        startLabels.setReleaseLabel("放开刷新");
        ILoadingLayout endLabels = listView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉刷新");
        endLabels.setRefreshingLabel("正在载入...");
        endLabels.setReleaseLabel("放开刷新...");

        listView.setAdapter(new NothingAdapter(this, data));

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if (listView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listView.onRefreshComplete();
                            ToastUtil.showToast(App.getCtx(), "下拉刷新");
                        }
                    }, 2000);

                } else if (listView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listView.onRefreshComplete();
                            ToastUtil.showToast(App.getCtx(), "上啦加载更多");
                        }
                    }, 2000);
                }
            }
        });

        refresh();
    }

    private void refresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.setRefreshing(PullToRefreshBase.Mode.PULL_FROM_START,true);
            }
        }, 500);
    }
}
