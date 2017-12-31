package com.sxt.chart.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.sxt.banner.library.CycleViewPager;
import com.sxt.banner.library.adapter.BaseCyclePagerAdapter;
import com.sxt.chart.R;
import com.sxt.chart.adapter.BannerAdapter;
import com.sxt.chart.chart.BeizerCurveLine;
import com.sxt.chart.chart.ChartBar;
import com.sxt.chart.chart.ChartBean;
import com.sxt.chart.chart.ChartPie;
import com.sxt.chart.chart.ChartPieBean;
import com.sxt.chart.chart.CircleProgressView;
import com.sxt.chart.chart.LineOnScrollChangeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends BaseActivity {

    private CycleViewPager viewPager;
    String[] lineName;
    String[] lineUnit;
    int[] lineColor;
    int[] shaderColor;
    private LinearLayout bottomListRoot;
    private List<ChartBean> chartBeanList0;
    private List<ChartBean> chartBeanList;
    private NestedScrollView scrollView;
    private LineOnScrollChangeListener onScrollChangeListener;
    private List<ChartPieBean> pieBeanList;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowStatusBarColor(this, R.color.colorPrimary);
        setContentView(R.layout.activity_main);

        KeyguardManager keyguardManager
                = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

        viewPager = (CycleViewPager) findViewById(R.id.viewpager);
        scrollView = findViewById(R.id.scrollview);
        bottomListRoot = (LinearLayout) findViewById(R.id.ll_bottom_list);
        onScrollChangeListener = new LineOnScrollChangeListener();
        scrollView.setOnScrollChangeListener(onScrollChangeListener);

//        Intent intent = new Intent(Settings.ACTION_SETTINGS);
//        startActivity(intent);

        initViewPager();//轮播图
        initData();
        for (int i = 0; i < 100; i++) {
            if (i < 2) {
                drawPie();
            } else if (i < 3) {
                drawBar();
            } else if (i < 5) {
                drawLine();
            } else if (i < 9) {
                drawPie();
            } else if (i % 2 == 0) {
                drawLine();//曲线
            } else {
                drawPie();
            }
        }

        drawCircleProgress();//圆形进度
    }

    public void wifi(View view) {
        startActivity(new Intent(getApplicationContext(), WifiSettingActivity.class));
//        startActivity(new Intent(getApplicationContext(), DragerActivity.class));
    }


    private void initViewPager() {
        String[] array = getResources().getStringArray(R.array.banner_urls);
        List<String> urls = Arrays.asList(array);
        viewPager
                .setAdapter(new BannerAdapter(this, urls))
                .addOnPageSelecedListener(new CycleViewPager.OnPageSelectedListener() {
                    @Override
                    public void onPageSelected(ViewPager viewPager, BaseCyclePagerAdapter baseCyclePagerAdapter, int i, int i1, int i2) {
                    }
                })
                .setScrollSelfState(true);
    }

    @SuppressLint("CutPasteId")
    private void drawCircleProgress() {
        View view = View.inflate(this, R.layout.item_circle_progress, null);
        bottomListRoot.addView(view);
        CircleProgressView itemView = (CircleProgressView) view.findViewById(R.id.chart_circle_progress);
        itemView
                .setDuration(2000)
                .setLabels(
                        new String[]{"运动详情"},
                        new int[]{R.color.colorPrimaryDark}).setProgress(new Random().nextInt(361),
                new Random().nextInt(10001),
                "今日步数");
    }


    private void drawBar() {
        //柱状图-------------------------------------------------------------------------------------
        View barView = View.inflate(this, R.layout.item_chart_bar, null);
        bottomListRoot.addView(barView);
        barView.setTag(bottomListRoot.getChildCount() - 1);

        final ChartBar chartBar = (ChartBar) barView.findViewById(R.id.chartbar);
        //设置柱状图的数据源
        chartBar
                .setRectData(chartBeanList0)
                .setLabels(
                        new String[]{getString(R.string.string_label_smzl), getString(R.string.string_label_smzl_bad), getString(R.string.string_label_smzl_good)},
                        new int[]{lineColor[0], lineColor[1], lineColor[3]})
                .start();
        onScrollChangeListener.addLine(chartBar);
    }

    private void initData() {
        lineName = new String[]{getString(R.string.string_label_press), getString(R.string.string_label_xt), getString(R.string.string_label_hb), getString(R.string.string_label_bt)};
        lineColor = new int[]{R.color.violet_rgb_185_101_255, R.color.red_rgb_255_127_87, R.color.red, R.color.blue_rgba_24_261_255, R.color.green_rgb_40_220_162};
        shaderColor = new int[]{R.color.violet_sharder, R.color.red_sharder, R.color.red_sharder, R.color.blue_sharder, R.color.green_sharder};
        lineUnit = new String[]{getString(R.string.string_unit_xt), getString(R.string.string_unit_hb), getString(R.string.string_unit_press), getString(R.string.string_unit_bt)};

        chartBeanList = new ArrayList<>();
        chartBeanList.add(new ChartBean("9月", 20));
        chartBeanList.add(new ChartBean("1", 80));
        chartBeanList.add(new ChartBean("2", 58));
        chartBeanList.add(new ChartBean("3", 100));
        chartBeanList.add(new ChartBean("4", 20));
        chartBeanList.add(new ChartBean("5", 70));
        chartBeanList.add(new ChartBean("6", 10));


        chartBeanList0 = new ArrayList<>();
        chartBeanList0.add(new ChartBean("9月", 20));
        chartBeanList0.add(new ChartBean("1", 80));
        chartBeanList0.add(new ChartBean("2", 58));
        chartBeanList0.add(new ChartBean("3", 100));
        chartBeanList0.add(new ChartBean("4", 20));
        chartBeanList0.add(new ChartBean("5", 70));
        chartBeanList0.add(new ChartBean("6", 10));
        chartBeanList0.add(new ChartBean("7", 30));
        chartBeanList0.add(new ChartBean("8", 5));

        pieBeanList = new ArrayList<>();
        pieBeanList.add(new ChartPieBean(3090, "押金使用", R.color.green));
        pieBeanList.add(new ChartPieBean(501f, "天猫购物", R.color.blue_rgba_24_261_255));
        pieBeanList.add(new ChartPieBean(800, "话费充值", R.color.orange));
        pieBeanList.add(new ChartPieBean(1000, "生活缴费", R.color.red_2));
        pieBeanList.add(new ChartPieBean(2300, "早餐", R.color.progress_color_default));
    }

    private void drawPie() {
//        //底部的曲线图
//        View child = View.inflate(this, R.layout.item_chart_num, null);
//        RoundNumberView view = child.findViewById(R.id.chart_number);
//        view.setText("1");
//        bottomListRoot.addView(child);
        //底部的曲线图
        View childAt = View.inflate(this, R.layout.item_chart_pie, null);
        bottomListRoot.addView(childAt);
        ChartPie chartPie = childAt.findViewById(R.id.chart_pie);
        chartPie.setData(pieBeanList).start();

        //将当前曲线添加到ScrollView的滑动监听中
        onScrollChangeListener.addLine(chartPie);
    }

    private void drawLine() {
        //底部的曲线图
        View childAt = View.inflate(this, R.layout.item_chart_line, null);
        bottomListRoot.addView(childAt);
        BeizerCurveLine chartLine = (BeizerCurveLine) childAt.findViewById(R.id.chart_line);
        BeizerCurveLine.CurveLineBuilder builder = new BeizerCurveLine.CurveLineBuilder();
        List<ChartBean> chartBeans = new ArrayList<>();

        for (int y = 0; y < chartBeanList.size(); y++) {
            ChartBean chartBean = chartBeanList.get(y);
            chartBeans.add(new ChartBean(chartBean.x, chartLine.parseFloat(String.valueOf(chartBean.y))));
        }
        chartLine
                .setMaxXNum(6)

                .setXYColor(R.color.text_color_3)
                .setHintLineColor(R.color.text_color_3)
                .setUnit(lineUnit[0]);

        chartLine.setLabels(new String[]{lineName[0], getString(R.string.string_label_press_hight), getString(R.string.string_label_press_lower)}, new int[]{lineColor[0], lineColor[2], lineColor[1]});
        builder.builder(chartBeans, lineColor[0], shaderColor[0]);

        builder.build(chartLine);
        //将当前曲线添加到ScrollView的滑动监听中
        onScrollChangeListener.addLine(chartLine);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @SuppressWarnings("deprecation")
    public void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
