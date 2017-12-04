package com.sxt.chart.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ViewSwitcher;

import com.sxt.chart.App;
import com.sxt.chart.R;
import com.sxt.chart.adapter.SplashPagerAdapter;
import com.sxt.chart.utils.Prefs;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    private Integer[] res = {R.mipmap.guidepage1, R.mipmap.guidepage2, R.mipmap.guidepage3, R.mipmap.guidepage4};
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private int delayMillis = 2000;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ViewPager viewPager = findViewById(R.id.viewPager);
        final ViewSwitcher viewSwitcher = findViewById(R.id.viewSwitcher);

        if (!Prefs.getInstance(this).getBoolean(Prefs.IS_FRIST_ENTER)) {
            viewSwitcher.setDisplayedChild(0);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewSwitcher.setDisplayedChild(1);//显示ViewPager引导页
                    initViewPager(viewPager);
                    Prefs.getInstance(App.getCtx()).putBoolean(Prefs.IS_FRIST_ENTER, true);
                }
            }, delayMillis);
        } else {
            viewSwitcher.setDisplayedChild(0);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(App.getCtx(), MainActivity.class));
                    finish();
                }
            }, delayMillis);
        }
    }

    private void initViewPager(ViewPager viewPager) {
        viewPager.setAdapter(new SplashPagerAdapter(this, Arrays.asList(res)));
    }
}
