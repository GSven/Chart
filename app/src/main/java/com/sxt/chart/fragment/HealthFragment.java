package com.sxt.chart.fragment;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxt.chart.R;
import com.sxt.chart.chart.BeizerCurveLine;
import com.sxt.chart.chart.ChartBean;
import com.sxt.chart.utils.ArithTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by izhaohu on 2017/11/21.
 */
public class HealthFragment extends LazyFragment {

    private LinearLayout topListRoot;
    private LinearLayout historyRoot;
    private LinearLayout bottomListRoot;
    String[] lineName;
    String[] lineUnit;
    int[] lineColor;
    int[] shaderColor;
    int[] res = {R.mipmap.heart, R.mipmap.xueyang, R.mipmap.shousuoya, R.mipmap.shuzhangya,
            R.mipmap.xuetang, R.mipmap.tiwen, R.mipmap.xiaotuiwei, R.mipmap.tizhong};

    @Override
    protected int getDisplayView() {
        return R.layout.fragment_health_layout;
    }

    @Override
    protected void initView() {
        topListRoot = (LinearLayout) contentView.findViewById(R.id.ll_body_state_top_list);
        historyRoot = (LinearLayout) contentView.findViewById(R.id.ll_body_state_history_title);
        bottomListRoot = (LinearLayout) contentView.findViewById(R.id.ll_body_state_bottom_list);
        lineName = new String[]{getString(R.string.string_label_xt), getString(R.string.string_label_hb), getString(R.string.string_label_press), getString(R.string.string_label_bt)};
        lineColor = new int[]{R.color.violet_rgb_185_101_255, R.color.red_rgb_255_127_87, R.color.red, R.color.blue_rgba_24_261_255, R.color.green_rgb_40_220_162};
        shaderColor = new int[]{R.color.violet_sharder, R.color.red_sharder, R.color.red_sharder, R.color.blue_sharder, R.color.green_sharder};
        lineUnit = new String[]{getString(R.string.string_unit_xt), getString(R.string.string_unit_hb), getString(R.string.string_unit_press), getString(R.string.string_unit_bt)};
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void loadData() {//fragment创建后 ke==可是状态下 主动加载数据 一次
        super.loadData();
        fillTopListData();
        fillBottomLinesData();
    }

    int num = 88;

    private void fillTopListData() {
        topListRoot.removeAllViews();
        //设置顶部列表的数据
        String[] titles = getResources().getStringArray(R.array.health_title_strings);
        String[] units = getResources().getStringArray(R.array.health_unit_strings);
        //顶部的状态列表
        for (int i = 0; i < titles.length; i++) {
            View view = View.inflate(activity, R.layout.item_health_detail_top, null);
            if (i == 0) {
                view.findViewById(R.id.body_state_top_line).setVisibility(View.GONE);
            }
            ((TextView) view.findViewById(R.id.title)).setText(titles[i]);
            ((TextView) view.findViewById(R.id.result)).setText(String.valueOf(num -= 2));
            ((TextView) view.findViewById(R.id.unit)).setText(units[i]);
            ((ImageView) view.findViewById(R.id.img)).setImageResource(res[i]);
            topListRoot.addView(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fillBottomLinesData() {
        View titleHistory = LayoutInflater.from(activity).inflate(R.layout.item_histroy_title, historyRoot);
        TextView tv = (TextView) titleHistory.findViewById(R.id.tv_title);
        tv.setText(R.string.chart_zhihu_data);
        //底部的曲线图
        for (int i = 0; i < 3; i++) {
            View view = View.inflate(activity, R.layout.item_chart_line, null);
            bottomListRoot.addView(view);
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.item_bottom_no_data, null);
        bottomListRoot.addView(view);
        for (int x = 0; x < bottomListRoot.getChildCount() - 1; x++) {
            View childAt = bottomListRoot.getChildAt(x);
            BeizerCurveLine chartLine = (BeizerCurveLine) childAt.findViewById(R.id.chart_line);
            BeizerCurveLine.CurveLineBuilder builder = new BeizerCurveLine.CurveLineBuilder();
            List<ChartBean> chartBeans = new ArrayList<>();

            if (x == 0) {//v-endTime : "2017-11-22 12:30:00+0800"
                chartBeans.add(new ChartBean(chartLine.parseDate("2017-11-12 13:33:00+0800"), 10));
                chartBeans.add(new ChartBean(chartLine.parseDate("2017-11-13 13:33:00+0800"), 1));
                chartBeans.add(new ChartBean(chartLine.parseDate("2017-11-14 13:33:00+0800"), 10));
            } else {
                chartBeans.add(new ChartBean(chartLine.parseDate("2017-11-11 13:33:00+0800"), 255));
                chartBeans.add(new ChartBean(chartLine.parseDate("2017-11-12 13:33:00+0800"), 20));
                chartBeans.add(new ChartBean(chartLine.parseDate("2017-11-13 13:33:00+0800"), 120));
            }

            chartLine
                    .setXYColor(R.color.text_color_3)
                    .setHintLineColor(R.color.text_color_3)
                    .setUnit(lineUnit[x])
                    .setLabels(new String[]{lineName[x], lineName[x], lineName[x]}, new int[]{lineColor[x], lineColor[1], lineColor[2]});

            builder.builder(chartBeans, lineColor[x], shaderColor[x]);
            builder.build(chartLine);
        }
    }

    private String getPressResult(String spResult, String dpResult) {
        try {
            if (spResult != null && dpResult != null) {
                if (spResult.length() == 0 && dpResult.length() == 0) return "";
                //if (spResult.length() == 1 && dpResult.length() == 1) return "0/0";

                return spResult + "/" + dpResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    private String getBtResult(String result) {
        if (result != null && result.length() > 0) {
            try {
                if (result.startsWith("+")) {
                    String s = result.substring(1, result.length());
                    return "+" + ArithTool.div(Float.parseFloat(s), 10, 1);
                } else if (result.startsWith("-")) {
                    String s = result.substring(1, result.length());
                    return "-" + ArithTool.div(Float.parseFloat(s), 10, 1);
                } else {
                    return String.valueOf(ArithTool.div(Float.parseFloat(result), 10, 1));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
        }
        return "";
    }
}
