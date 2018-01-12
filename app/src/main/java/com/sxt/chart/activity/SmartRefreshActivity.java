package com.sxt.chart.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.sxt.chart.App;
import com.sxt.chart.R;
import com.sxt.chart.adapter.WifiIzhaohuAdapter2;
import com.sxt.chart.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by izhaohu on 2018/1/12.
 */

public class SmartRefreshActivity extends AppCompatActivity {
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smart_refresh_layout);

        final SmartRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        List<String> data = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            data.add(String.valueOf(i));
        }
        recyclerView.setAdapter(new WifiIzhaohuAdapter2(this, data));

        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefresh();
                    }
                }, 2000);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishLoadmore();
                    }
                }, 2000);
            }
        });
    }
}
