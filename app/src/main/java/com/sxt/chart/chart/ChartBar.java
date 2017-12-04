package com.sxt.chart.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ScrollView;

import com.sxt.chart.R;
import com.sxt.chart.utils.DateFormatUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by sxt on 2017/7/13.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class ChartBar extends View {


    public ChartBar(Context context) {
        super(context);
        init();
    }

    public ChartBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint basePaint;
    private Paint baseLabelPaint;
    private Paint xyPaint;
    private Paint hintPaint;
    private Paint rectPaint;
    private List<ChartBean> datas;
    private float basePadding = 30;
    private float startX;
    private float endX;
    private float startY;
    private float endY;
    /**
     * 顶部的Label 文字
     */
    private String[] labelStrs;
    /**
     * 顶部的Label 颜色
     */
    private int[] labelColors;
    private long duration = 800;
    private float animatedValue;
    private ValueAnimator valueAnimator;

    private void init() {
        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setColor(Color.GRAY);
        basePaint.setStrokeWidth(dip2px(0.5f));
        basePaint.setTextSize(dip2px(10));
        basePaint.setTextAlign(Paint.Align.CENTER);
        basePaint.setStrokeCap(Paint.Cap.ROUND);
        basePaint.setDither(true);

        baseLabelPaint = new Paint();
        baseLabelPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_color_1));
        baseLabelPaint.setTextSize(dip2px(14));
        baseLabelPaint.setTextAlign(Paint.Align.LEFT);
        Typeface font0 = Typeface.create(Typeface.SANS_SERIF, Typeface.DEFAULT_BOLD.getStyle());
        baseLabelPaint.setTypeface(font0);

        xyPaint = new Paint(basePaint);
        xyPaint.setColor(Color.GRAY);
        xyPaint.setStrokeWidth(dip2px(1));

        hintPaint = new Paint(basePaint);
        hintPaint.setStrokeWidth(0.5f);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            startX = getPaddingLeft() + basePadding;
            endX = getMeasuredWidth() - getPaddingRight();
            startY = getMeasuredHeight() - getPaddingBottom() - basePadding * 3;
            endY = getPaddingTop() + basePadding * 4;
        }
    }

    public ChartBar setRectData(List<ChartBean> datas) {
        this.datas = datas;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLabels(canvas);//画顶部Label
        drawLine(canvas);//画横线
        if (datas == null || datas.size() == 0 || xyPaint == null) return;
        drawX(canvas); //画X轴
        //drawY(canvas); //画Y轴
        darwRect(canvas);//画矩形
    }

    private void drawLine(Canvas canvas) {
        //X轴
        canvas.drawLine(startX, startY, endX, startY, hintPaint);
        //顶部的横线
        canvas.drawLine(startX, endY, endX, endY, hintPaint);
    }

    /**
     * 画顶部的Label
     */
    private void drawLabels(Canvas canvas) {
        if (labelStrs == null || labelStrs.length == 0) return;
        if (labelColors == null || labelColors.length == 0) return;

        //在坐标系左上角 画单位
        float labelCenterY = endY - basePadding * 1.5f;

        Paint leftLabelPaint = new Paint(baseLabelPaint);
        leftLabelPaint.setTextSize(size2sp(15, getContext()));
        leftLabelPaint.setTextAlign(Paint.Align.LEFT);
        Typeface font0 = Typeface.create(Typeface.SANS_SERIF, Typeface.DEFAULT_BOLD.getStyle());
        leftLabelPaint.setTypeface(font0);
        canvas.drawText(labelStrs[0], startX + basePadding / 2, labelCenterY, leftLabelPaint);

        float top0 = leftLabelPaint.getFontMetrics().top;
        float descent0 = leftLabelPaint.getFontMetrics().descent;

        //左上角的标题label
        Paint rectPaint = new Paint(basePaint);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(ContextCompat.getColor(getContext(), labelColors[0]));
        canvas.drawRect(startX, labelCenterY + top0 * 0.8f, startX + basePadding / 3, labelCenterY + descent0 / 2, rectPaint);

        if (labelStrs.length != 3 || labelColors.length != 3) return;
        //右上角的label
        float left = endX - basePadding * 8;
        float baseY = endY - basePadding;
        float right = left + 4.5F * basePadding;
        float DX = basePadding / 2;

        Paint paint = new Paint(basePaint);
        paint.setTextSize(dip2px(10));
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText(labelStrs[1], left, baseY, paint);//低压
        float top1 = paint.getFontMetrics().top;
        float descent1 = paint.getFontMetrics().descent;

        canvas.drawText(labelStrs[2], right, baseY, paint);//高压
        float top2 = paint.getFontMetrics().top;
        float descent2 = paint.getFontMetrics().descent;

        rectPaint.setColor(ContextCompat.getColor(getContext(), labelColors[1]));
        float top11 = top1 * 0.8f;
        float descent11 = descent1 * 0.6f;
        canvas.drawRect(
                left - DX + top11 - descent11,
                baseY + top11,
                left - DX,
                baseY + descent11,
                rectPaint);

        float top22 = top2 * 0.8f;
        //float descent22 = descent2 * 0.6f;
        rectPaint.setColor(ContextCompat.getColor(getContext(), labelColors[2]));
        canvas.drawRect(
                right - DX + top11 - descent11,
                baseY + top22,
                right - DX,
                baseY + descent11,
                rectPaint);
    }

    private void darwRect(Canvas canvas) {
        float dx = getDx();
        float dy = (startY - endY - basePadding) / 100;
        for (int i = 0; i < datas.size(); i++) {
            RectF rectf = new RectF(startX + dx * i, startY - datas.get(i).y * dy * animatedValue, startX + dx * (i + 1), startY);
            rectPaint = new Paint(basePaint);
            if (datas.get(i).y > 90) {
                rectPaint.setColor(ContextCompat.getColor(getContext(), labelColors[2]));
            } else if (datas.get(i).y >= 60) {
                rectPaint.setColor(ContextCompat.getColor(getContext(), labelColors[1]));
            } else {
                rectPaint.setColor(ContextCompat.getColor(getContext(), labelColors[0]));
            }
            canvas.drawRect(rectf, rectPaint);
        }
    }

    private void drawX(Canvas canvas) {
        float dx = getDx();
        for (int i = 0; i < datas.size(); i++) {
            canvas.drawText(datas.get(i).x, (float) (startX + dx * (i + 0.5)), startY + 2 * basePadding, xyPaint);
        }
    }

    private void drawY(Canvas canvas) {
        canvas.drawLine(startX, startY, startX, endY, basePaint);
        //canvas.drawText(String.valueOf(0), startX - basePadding, startY - basePadding / 3, xyPaint);
        //canvas.drawText(String.valueOf(100), startX - basePadding, endY, xyPaint);
    }

    public ChartBar setLabels(String[] labelStrs, int[] labelColors) {
        this.labelStrs = labelStrs;
        this.labelColors = labelColors;
        return this;
    }

    public ChartBar setXYColor(int colorRes) {
        xyPaint.setColor(ContextCompat.getColor(getContext(), colorRes));
        return this;
    }

    public ChartBar setHintLineColor(int colorId) {
        hintPaint.setColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public ChartBar setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    float size2sp(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, context.getResources().getDisplayMetrics());
    }

    int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @NonNull
    public String parseDate(String currentDate) {
        if (currentDate != null && currentDate.length() > 0) {
            try {
                String currentStr = String.valueOf(DateFormatUtil.getSecondsFromDate(currentDate));//转换成毫秒值
                String mm = DateFormatUtil.getDateFromSeconds(currentStr, "MM");
                String dd = DateFormatUtil.getDateFromSeconds(currentStr, "dd");
                if (mm.startsWith("0")) {
                    mm = mm.substring(1, mm.length());
                }
                if (dd.startsWith("0")) {
                    dd = dd.substring(1, dd.length());
                }

                return dd + "/" + mm;

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }

    /**
     * 数据可能是 1/5  4/5
     *
     * @param floatStr
     * @return
     */
    public float parseFloat(String floatStr) {
        if (floatStr != null && floatStr.length() > 0) {
            try {
                if (floatStr.length() > 2 && floatStr.contains("/") && floatStr.indexOf("/") == 1) {
                    String[] split = floatStr.split("/");
                    return new BigDecimal(split[0]).divide(new BigDecimal(split[1])).floatValue();
                } else {
                    return Float.parseFloat(floatStr) <= 0 ? 0 : Float.parseFloat(floatStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    /**
     * 计算 x 轴 平均值
     *
     * @return
     */
    private float getDx() {
        return (endX - startX) / 9;
    }


    public ChartBar setScrollView(ScrollView scrollView) {

        scrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                Log.i("sxt", "onScrollChange");
                if (isCover(ChartBar.this)) {
                    Log.i("sxt", "start");
                    start();
                }
            }
        });

        return this;
    }

    public void start() {
        initAnimator();
    }

    private void initAnimator() {
        valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animatedValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }


    /**
     * 检测制定View是否被遮住显示不全
     *
     * @return
     */
    protected boolean isCover(View view) {
        Rect rect = new Rect();
        if (view.getGlobalVisibleRect(rect)) {
            if (rect.width() >= view.getMeasuredWidth() && rect.height() >= view.getMeasuredHeight()) {
                return true;
            }
        }
        return false;
    }

}
