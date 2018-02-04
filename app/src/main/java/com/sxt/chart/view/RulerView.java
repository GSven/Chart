package com.sxt.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.Scroller;

import com.sxt.chart.R;
import com.sxt.chart.utils.Px2DpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SXT on 2018/1/26.
 */

public class RulerView extends View {

    public RulerView(Context context) {
        super(context);
        init();
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint basePaint;
    private Paint textPaint;
    private Paint scalePaint;
    private Paint indicatorPaint;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float basePadding = 50;
    private float indicatorWidth = basePadding / 3;
    private float indicatorHigth = basePadding / 2;
    private Path indicatorPath;


    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private int touchSlop;
    private float leftBorder, rightBorder;
    private int rulerWidth;
    private float mLastMoveY; // 触摸的坐标y
    private float mLastMoveX; // 触摸的坐标X
    private boolean mIsMovingCenter; // 是否正在滑向中间
    // 可以把scroller看做模拟的触屏滑动操作，mLastScrollY为上次触屏滑动的坐标
    private int mLastScrollY = 0; // Scroller的坐标y
    private int mLastScrollX = 0; // Scroller的坐标x

    private boolean mDisallowTouch = false; // 不允许触摸


    private int page;
    private List<String> data;
    private float minValue = 0, maxValue = 101;
    private float mItemWidth = Px2DpUtil.dip2px(getContext(), 6);
    private List<float[]> scaleLines = new ArrayList<>();
    private List<String[]> scaleTexts = new ArrayList<>();
    private OnSelectChangeListenrer onSelectChangeListener;

    public void init() {
        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        basePaint.setStrokeWidth(Px2DpUtil.dip2px(getContext(), 1));
        basePaint.setTextAlign(Paint.Align.CENTER);
        basePaint.setTextSize(Px2DpUtil.dip2px(getContext(), 15));
        basePaint.setColor(ContextCompat.getColor(getContext(), R.color.text_color_gray_3));

        textPaint = new Paint(basePaint);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_color_1));
        scalePaint = new Paint(basePaint);
        scalePaint.setStrokeWidth(Px2DpUtil.dip2px(getContext(), 1));

        indicatorPaint = new Paint(basePaint);
        indicatorPaint.setStrokeWidth(Px2DpUtil.dip2px(getContext(), 2));
        indicatorPaint.setColor(ContextCompat.getColor(getContext(), R.color.main_blue));

        indicatorPath = new Path();

        mScroller = new Scroller(getContext());
        mGestureDetector = new GestureDetector(getContext(), new FlingOnGestureListener());
        touchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        rulerWidth = (int) ((maxValue + 1 - minValue) * mItemWidth + basePadding);
        if (rulerWidth < widthMeasureSpec) {
            rulerWidth = widthMeasureSpec;
        }
        setMeasuredDimension(rulerWidth, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            startX = getPaddingLeft() + basePadding;
            startY = getMeasuredHeight() - getPaddingBottom();
            endX = getMeasuredWidth() - getPaddingRight();
            endY = getPaddingTop() + basePadding;
            page = (int) (rulerWidth / (endX - startX));

            leftBorder = startX - (maxValue - minValue - 1) * mItemWidth / 2;
            rightBorder = endX + rulerWidth;
        }
    }

//    public void setData(float minValue, float maxValue) {
//        this.minValue = minValue;
//        this.maxValue = maxValue;
//        invalidate();
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawNoTouch(canvas);
        drawIndicator(canvas);
    }

    private void drawNoTouch(Canvas canvas) {
        drawScals(canvas);//画刻度
        drawLines(canvas);//画线
    }

    private void drawIndicator(Canvas canvas) {
        //初始化时，将指示器绘制在中间位置
        if (scaleLines.size() > 0) {
//            if (mLastMoveX == 0.0) {
//                int index = scaleLines.size() / 2;
//                float[] floats = scaleLines.get(index);
//                canvas.drawLine(floats[0], floats[1], floats[2], floats[3], indicatorPaint);
//                indicatorPath.reset();
//                indicatorPath.moveTo(floats[0], floats[1]);
//                indicatorPath.lineTo(floats[0] + indicatorWidth, floats[1] - indicatorHigth);
//                indicatorPath.lineTo(floats[0] - indicatorWidth, floats[1] - indicatorHigth);
//                indicatorPath.close();
//                canvas.drawPath(indicatorPath, indicatorPaint);
//                if (onSelectChangeListener != null) {
//                    onSelectChangeListener.onSelectChange(this, (floats[0] - leftBorder) / mItemWidth);
//                }
//            } else {
            //查找触摸位置
            float[] xy = getOnTouch();
            canvas.drawLine(xy[0], xy[1], xy[2], xy[3], indicatorPaint);
            indicatorPath.reset();
            indicatorPath.moveTo(xy[0], xy[1]);
            indicatorPath.lineTo(xy[0] + indicatorWidth, xy[1] - indicatorHigth);
            indicatorPath.lineTo(xy[0] - indicatorWidth, xy[1] - indicatorHigth);
            indicatorPath.close();
            canvas.drawPath(indicatorPath, indicatorPaint);
            if (onSelectChangeListener != null) {
                onSelectChangeListener.onSelectChange(this, (xy[0] - leftBorder) / mItemWidth);
            }
//            }
        }
    }

    private void drawScals(Canvas canvas) {
        if (scaleLines.size() == 0) {
            for (int i = 0; i < maxValue - minValue; i++) {
                float[] pts = new float[4];
                float start = startX + i * mItemWidth;
                pts[0] = start;
                pts[1] = endY;
                pts[2] = start;
                if (i % 10 == 0) {
                    pts[3] = endY + 2 * basePadding;
                } else if (i % 5 == 0) {
                    pts[3] = (float) (endY + basePadding * 1.5);
                } else {
                    pts[3] = (float) (endY + basePadding * 1);
                }
                canvas.drawLine(pts[0], pts[1], pts[2], pts[3], scalePaint);
                if (i % 10 == 0) {
                    canvas.drawText(String.valueOf(i), pts[2], pts[3] + basePadding, textPaint);
                    scaleTexts.add(new String[]{String.valueOf(i), String.valueOf(pts[2]), String.valueOf(pts[3])});
                }
                scaleLines.add(pts);//记录刻度坐标
            }
        } else {
            for (int i = 0; i < scaleLines.size(); i++) {
                canvas.drawLine(scaleLines.get(i)[0], scaleLines.get(i)[1], scaleLines.get(i)[2], scaleLines.get(i)[3], scalePaint);
            }
            for (int i = 0; i < scaleTexts.size(); i++) {
                canvas.drawText(scaleTexts.get(i)[0], Float.parseFloat(scaleTexts.get(i)[1]), Float.parseFloat(scaleTexts.get(i)[2]) + basePadding, textPaint);
            }
        }
    }

    private void drawLines(Canvas canvas) {

//        RectF oval = new RectF(startX, endY - basePadding, endX, endY);
//        canvas.drawArc(oval, -180, 180, false, new Paint(basePaint));
//
//        RectF ova2 = new RectF(startX, startY, endX, startY);
//        canvas.drawArc(ova2, -180, 180, false, new Paint(basePaint));
        canvas.drawLine(startX, endY, startX + rulerWidth, endY, basePaint);
        canvas.drawLine(startX, startY - basePadding, startX + rulerWidth, startY - basePadding, basePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float offsetX = event.getX() - mLastMoveX;
                mLastMoveX = event.getX();
                mLastMoveY = event.getY();

                if (Math.abs(offsetX) > touchSlop) {
                    if (getScrollX() + offsetX < leftBorder) {
                        scrollTo((int) leftBorder, 0);
                        return true;
                    }
                    if (getScrollX() + offsetX > rightBorder) {
                        scrollTo((int) rightBorder, 0);
                        return true;
                    }

                    scrollBy((int) offsetX, 0);
                    invalidate();

                } else {
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                mLastMoveX = event.getX();
                mLastMoveY = event.getY();
//                mScroller.startScroll((int) leftBorder, 0, (int) rightBorder, 0);
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {//平滑移动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    private class FlingOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        public boolean onDown(MotionEvent e) {
            // 不允许父组件拦截事件
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
            // 点击时取消所有滚动效果
            cancelScroll();
            mLastMoveY = e.getY();
            mLastMoveX = e.getX();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, final float velocityY) {
            // 惯性滑动
            cancelScroll();
            fling(getScrollX(), velocityX);
            return true;
        }
    }

    public void cancelScroll() {
        mLastScrollY = 0;
        mLastScrollX = 0;
        mScroller.abortAnimation();
    }

    // 平滑滚动
    private void scroll(float from, int to) {
        mLastScrollX = (int) from;
        mIsMovingCenter = true;
        mScroller.startScroll((int) from, 0, 0, 0);
        mScroller.setFinalX(to);
        invalidate();
    }

    // 惯性滑动，
    private void fling(float from, float vel) {
        mLastScrollX = (int) from;
        // 最多可以惯性滑动20个item
        mScroller.fling((int) from, 0, (int) vel, 0, -20 * (int) mItemWidth,
                20 * (int) mItemWidth, 0, 0);
        invalidate();
    }

    public float[] getOnTouch() {
        float centerX = startX + (endX - startX) / 2 + getScrollX();
        if (scaleLines.size() > 0) {
            for (int i = 0; i < scaleLines.size(); i++) {
                float x = scaleLines.get(i)[0];
                if (centerX == x) {
                    return scaleLines.get(i);
                } else {
                    if (Math.abs(centerX - x) < mItemWidth) {
                        return scaleLines.get(i);
                    } else {
                        continue;
                    }
                }
            }
        }
        return scaleLines.get(scaleLines.size() / 2);
    }

    public void setOnSelectChangeListener(OnSelectChangeListenrer onSelectChangeListener) {
        this.onSelectChangeListener = onSelectChangeListener;
    }

    public interface OnSelectChangeListenrer {
        void onSelectChange(RulerView rulerView, float selectValue);
    }
}
