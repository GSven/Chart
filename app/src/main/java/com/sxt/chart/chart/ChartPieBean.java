package com.sxt.chart.chart;

/**
 * Created by izhaohu on 2017/12/27.
 */

public class ChartPieBean {

    public int colorRes;
    public float value;
    public float rate;
    public float startAngle;
    public float sweepAngle;

    public ChartPieBean() {
    }

    public ChartPieBean(int colorRes, float value, float rate, float startAngle, float sweepAngle) {
        this.colorRes = colorRes;
        this.value = value;
        this.rate = rate;
        this.startAngle = startAngle;
        this.sweepAngle = sweepAngle;
    }
}
