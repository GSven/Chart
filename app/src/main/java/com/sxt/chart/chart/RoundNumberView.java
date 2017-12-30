package com.sxt.chart.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sxt.chart.utils.Px2DpUtil;

/**
 * Created by izhaohu on 2017/12/28.
 */

public class RoundNumberView extends View {

    private Paint basePaint;
    private float basePadding = 8;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float centerX;
    private float centerY;
    private float R;
    private String text = "2";
    private int resId;

    public RoundNumberView(Context context) {
        super(context);
        init(context);
    }

    public RoundNumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundNumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setStyle(Paint.Style.STROKE);
        basePaint.setColor(Color.RED);
        basePaint.setTextAlign(Paint.Align.CENTER);
        basePaint.setTextSize(Px2DpUtil.dip2px(context, 11));
        basePaint.setStrokeWidth(Px2DpUtil.dip2px(context, 2));
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            startX = getPaddingLeft() + basePadding;
            endX = getMeasuredWidth() - getPaddingRight() - basePadding;
            startY = getMeasuredHeight() - getPaddingBottom() - basePadding;
            endY = getPaddingTop() + basePadding;
            centerX = startX + (endX - startX) / 2;
            centerY = endY + (startY - endY) / 2;
            R = (endX - startX) / 2;
        }
    }

    public void setText(String text) {
        this.text = text;
        this.resId = 0;
        invalidate();
    }

    public void setText(int resId) {
        this.text = null;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        if (text != null) {
        Paint paint = new Paint(basePaint);
        canvas.drawCircle(centerX, centerY, R, paint);
        canvas.drawText(text, centerX, centerY, paint);
//        } else if (resId != 0) {
//            Paint paint = new Paint(basePaint);
//            canvas.drawCircle(centerX, centerY, R, paint);
//            canvas.drawText(getContext().getString(resId), centerX, centerY, paint);
//        }
    }


}
