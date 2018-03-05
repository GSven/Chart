package com.sxt.chart.activity;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sxt.banner.library.CycleViewPager;
import com.sxt.banner.library.adapter.BaseCyclePagerAdapter;
import com.sxt.chart.R;
import com.sxt.chart.adapter.BannerAdapter;
import com.sxt.chart.adapter.MainAdapter;

import java.util.Arrays;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends BaseActivity {

    private CycleViewPager viewPager;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewPager();//轮播图
        initRecyclerView();
        initBottomSheet();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new MainAdapter(this));
    }

    private void initBottomSheet() {
        final BottomSheetBehavior<View> sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        findViewById(R.id.action_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    private void initViewPager() {
        String[] array = getResources().getStringArray(R.array.banner_urls);
        List<String> urls = Arrays.asList(array);
        viewPager = (CycleViewPager) findViewById(R.id.viewpager);
        viewPager
                .setAdapter(new BannerAdapter(this, urls))
                .addOnPageSelecedListener(new CycleViewPager.OnPageSelectedListener() {
                    @Override
                    public void onPageSelected(ViewPager viewPager, BaseCyclePagerAdapter baseCyclePagerAdapter, int i, int i1, int i2) {
                    }
                }).setScrollSelfState(true);

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
