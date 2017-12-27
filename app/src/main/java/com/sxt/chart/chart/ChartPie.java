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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.sxt.chart.R;

/**
 * Created by sxt on 2017/8/5.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class ChartPie extends View {

    private Paint basePaint;
    private Paint piePaint;
    private Paint centerPiePaint;
    private Paint linePaint;
    private Paint textPaint;

    private float basePadding = 30;
    private float startX;
    private float endX;
    private float startY;
    private float endY;
    /**
     * 是否需要执行动画
     */
    private boolean isPlayAnimator = true;
    private float mAnimatorValue;
    private ValueAnimator valueAnimator;
    /**
     * 动画执行的时长
     */
    private long duration = 3000;
    /**
     * 圆心坐标
     */
    private float[] centerXY;
    /**
     * 内部白色圆半径
     */
    private float whiteR;
    /**
     * 外部圆半径
     */
    private float pieR;

    /**
     */
    private RectF areaPie;
    /**
     * 扇形开始的角度
     */
    private int startAngle = -90;

    public ChartPie(Context context) {
        super(context);
        init();
    }

    public ChartPie(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartPie(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setColor(Color.GRAY);
        basePaint.setStrokeWidth(dip2px(0.5f));
        basePaint.setTextSize(dip2px(14));
        basePaint.setTextAlign(Paint.Align.LEFT);
        basePaint.setStrokeCap(Paint.Cap.ROUND);
        basePaint.setDither(true);

        piePaint = new Paint(basePaint);

        centerPiePaint = new Paint(basePaint);
        centerPiePaint.setColor(Color.WHITE);

        linePaint = new Paint(basePaint);

        textPaint = new Paint(basePaint);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_color_1));
        textPaint.setTextAlign(Paint.Align.LEFT);
        Typeface font0 = Typeface.create(Typeface.SANS_SERIF, Typeface.DEFAULT_BOLD.getStyle());
        textPaint.setTypeface(font0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            startX = getPaddingLeft() + basePadding;
            endX = getMeasuredWidth() - getPaddingRight() - basePadding;
            startY = getMeasuredHeight() - getPaddingBottom() - basePadding;
            endY = getPaddingTop() + basePadding;
            centerXY = new float[2];
            centerXY[0] = startX + (endX - startX) / 2;
            centerXY[1] = endY + (startY - endY) / 2;
            whiteR = (endX - startX) / 8;
            pieR = (endX - startX) / 4;
            areaPie = new RectF(centerXY[0] - pieR, centerXY[1] - pieR, centerXY[0] + pieR, centerXY[1] + pieR);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (centerXY == null) return;
        drawPie(canvas);//画饼图
        drawCenter(canvas);//画中心
        drawLines(canvas);//画折线
    }

    private void drawPie(Canvas canvas) {
        for (int i = 0; i < mAnimatorValue; i++) {
            if (mAnimatorValue < 50) {
                piePaint.setColor(Color.GRAY);
            } else if (mAnimatorValue == 50) {
                piePaint.setColor(Color.GRAY);
                startAngle = -40;
            } else if (mAnimatorValue < 180) {
                piePaint.setColor(Color.RED);
            } else if (mAnimatorValue == 180) {
                piePaint.setColor(Color.RED);
                startAngle = 90;
            } else if (mAnimatorValue < 200) {
                piePaint.setColor(Color.BLUE);
            } else if (mAnimatorValue == 200) {
                piePaint.setColor(Color.BLUE);
                startAngle = 110;
            } else if (mAnimatorValue < 300) {
                piePaint.setColor(Color.YELLOW);
            } else if (mAnimatorValue == 300) {
                piePaint.setColor(Color.YELLOW);
                startAngle = 210;
            } else if (mAnimatorValue < 360) {
                piePaint.setColor(Color.GREEN);
            }
            canvas.drawArc(areaPie, startAngle, mAnimatorValue, true, piePaint);
        }
    }

    private void drawCenter(Canvas canvas) {
        canvas.drawCircle(centerXY[0], centerXY[1], whiteR, centerPiePaint);
    }

    private void drawLines(Canvas canvas) {

    }

    public ChartPie setPlayAnimator(boolean isPlayAnimator) {
        this.isPlayAnimator = isPlayAnimator;
        return this;
    }

    public ChartPie setAnimDurationTime(long duration) {
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

    private void startAnimator() {
        valueAnimator = ValueAnimator.ofFloat(0, 360).setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    public void start() {
        startAnimator();
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

    @Override
    protected void onDetachedFromWindow() {
        if (valueAnimator != null && valueAnimator.isRunning()) valueAnimator.cancel();
        super.onDetachedFromWindow();
    }
}
