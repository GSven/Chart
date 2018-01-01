package com.sxt.chart.chart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.sxt.chart.utils.Px2DpUtil;

/**
 * Created by izhaohu on 2017/12/28.
 */

public class RoundNumberView extends View {

    private Paint basePaint;
    private float basePadding = dip2px(2);
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float centerX;
    private float centerY;
    private float R;
    private String text = "2";

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
        basePaint.setTextSize(Px2DpUtil.dip2px(context, 10));
        basePaint.setStrokeWidth(Px2DpUtil.dip2px(context, 1F));
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            startX = getPaddingLeft() + basePadding;
            endX = getMeasuredWidth() - getPaddingRight() - basePadding;
            startY = getMeasuredHeight() - getPaddingBottom() - basePadding;
            endY = getPaddingTop() + basePadding;

            float R1 = startY - endY;
            float R2 = endX - -startX;
            centerX = startX + R2 / 2;
            centerY = endY + R1 / 2;

            //宽度>高度  选择高度 , 宽度<高度 选择宽度  , 能避免在大屏下坐标越界
            if (R1 >= R2) {
                if (R2 >= dip2px(25)) {
                    R = dip2px(5);
                } else {
                    R = R2 / 2;
                }
            } else {
                if (R1 >= dip2px(25)) {
                    R = dip2px(5);
                } else {
                    R = R1 / 2;
                }
            }
        }
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (text != null && !TextUtils.isEmpty(text)) {
            Paint paint = new Paint(basePaint);
            Paint paint1 = new Paint(paint);
            paint1.setColor(Color.WHITE);
           paint1.setStrokeWidth(R);
//            RectF rect = new RectF(centerX - R, centerY - R, centerX + R, centerY + R);
//            canvas.drawArc(rect, 0, 360, true, paint1);

            canvas.drawPoint(centerX,centerY,paint1);
            canvas.drawCircle(centerX, centerY, R, paint);
            paint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float height = fontMetrics.bottom - fontMetrics.top;
            paint.setStrokeWidth(dip2px(0.5f));
            canvas.drawText(text, centerX, centerY + height / 4, paint);
        }
    }

    int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
