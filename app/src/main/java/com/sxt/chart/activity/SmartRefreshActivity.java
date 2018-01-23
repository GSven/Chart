package com.sxt.chart.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.sxt.banner.library.CycleViewPager;
import com.sxt.banner.library.adapter.BaseCyclePagerAdapter;
import com.sxt.chart.R;
import com.sxt.chart.adapter.ImagePagerAdapter;
import com.sxt.chart.utils.Px2DpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by izhaohu on 2018/1/12.
 */

public class SmartRefreshActivity extends AppCompatActivity {
    private SmartRefreshLayout refreshLayout;
    private CycleViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smart_refresh_layout);

        refreshLayout = findViewById(R.id.refreshLayout);
        viewPager = findViewById(R.id.viewPager);
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh();
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore();
            }
        });

        List<Integer> data = new ArrayList<>();
        data.add(R.mipmap.a);
        data.add(R.mipmap.b);
        data.add(R.mipmap.c);
        data.add(R.mipmap.d);
        data.add(R.mipmap.e);
        data.add(R.mipmap.f);
        data.add(R.mipmap.g);
        data.add(R.mipmap.h);

        viewPager.getViewPager().setClipChildren(false);//不裁剪childView
//        viewPager.getViewPager().setPageMargin(Px2DpUtil.dip2px(this, 8));
        ViewGroup.LayoutParams lp = viewPager.getViewPager().getLayoutParams();
        lp.width = getWindowManager().getDefaultDisplay().getWidth() - Px2DpUtil.dip2px(this, 200);
        viewPager.getViewPager().setLayoutParams(lp);
        viewPager
                .setAdapter(new ImagePagerAdapter(this, data))
                .addOnPageSelecedListener(new CycleViewPager.OnPageSelectedListener() {
                    @Override
                    public void onPageSelected(ViewPager viewPager, BaseCyclePagerAdapter baseCyclePagerAdapter, int i, int i1, int i2) {
                    }
                })
                .setPointMarginLeft(Px2DpUtil.dip2px(this, 8))
                .setScrollSelfState(false);
    }
}
