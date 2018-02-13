package com.sxt.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
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

    private float basePadding = Px2DpUtil.dip2px(getContext(), 30);
    private float offsetY = basePadding / 3;
    private float indicatorWidth = basePadding / 3;
    private float indicatorHigth = basePadding / 2;
    private Path indicatorPath;

    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private float mLastMoveY; // 触摸的坐标y
    private float mLastMoveX; // 触摸的坐标X
    private boolean isHorizontal = true;

    private int centerPosition;
    private int prePosition;
    private List<String> data;
    private float minValue = 0, maxValue = 220, selectValue;
    private float mItemWidth = Px2DpUtil.dip2px(getContext(), 6);
    private List<float[]> scaleLines = new ArrayList<>();
    private List<String[]> scaleTexts = new ArrayList<>();
    private OnSelectChangeListenrer onSelectChangeListener;
    private Handler handler = new Handler();

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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        rulerWidth = (int) ((maxValue - minValue - 1) * mItemWidth);
//        if (rulerWidth < widthMeasureSpec) {
//            rulerWidth = widthMeasureSpec;
//        }
//        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            startX = getPaddingLeft() + basePadding;
            startY = getMeasuredHeight() - getPaddingBottom();
            endX = getMeasuredWidth() - getPaddingRight();
            endY = getPaddingTop();
        }
    }

    public void setData(float minValue, float maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawRect(canvas);
        drawScals(canvas);//画刻度
        drawIndicator(canvas);//画指示器
    }

    private void drawRect(Canvas canvas) {
        if (scaleLines.size() > 0) {
            Paint paint = new Paint(basePaint);
            paint.setColor(ContextCompat.getColor(getContext(), R.color.main_body));
            canvas.drawRect(scaleLines.get(0)[0], scaleLines.get(0)[1] - 2 * mItemWidth + getScrollY(), endX, scaleLines.get(scaleLines.size() - 1)[1] + 2 * mItemWidth + getScrollY(), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Px2DpUtil.dip2px(getContext(), 2));
            paint.setColor(ContextCompat.getColor(getContext(), R.color.dividing_line));
            canvas.drawRect(scaleLines.get(0)[0], scaleLines.get(0)[1] - 2 * mItemWidth + getScrollY(), endX, scaleLines.get(scaleLines.size() - 1)[1] + 2 * mItemWidth + getScrollY(), paint);
        }
    }

    private void drawIndicator(Canvas canvas) {
        //初始化时，将指示器绘制在中间位置
        if (scaleLines.size() > 0) {
            //查找触摸位置
            float[] xy = getOnTouch();
            canvas.drawLine(xy[0], xy[1], xy[0] + basePadding, xy[3], indicatorPaint);
            indicatorPath.reset();
            indicatorPath.moveTo(xy[0], xy[1]);
            indicatorPath.lineTo(xy[0] - indicatorWidth, xy[1] - mItemWidth);
            indicatorPath.lineTo(xy[0] - indicatorWidth, xy[1] + mItemWidth);
            indicatorPath.close();
            canvas.drawPath(indicatorPath, indicatorPaint);
            if (onSelectChangeListener != null) {
                onSelectChangeListener.onSelectChange(this, minValue + (xy[1] - offsetY) / mItemWidth);
            }
        }
    }

    private void drawScals(Canvas canvas) {
        if (scaleLines.size() == 0) {
            for (int i = 0; i <= maxValue - minValue; i++) {
                float[] pts = new float[4];
                float start = endY + offsetY + i * mItemWidth;
                if (i % 10 == 0) {
                    pts[2] = startX + basePadding;
                } else if (i % 5 == 0) {
                    pts[2] = (float) (startX + 0.5 * basePadding);
                } else {
                    pts[2] = (float) (startX + 0.2 * basePadding);
                }
                pts[0] = startX;
                pts[1] = start;
                pts[3] = start;

                canvas.drawLine(pts[0], pts[1], pts[2], pts[3], scalePaint);
                if (i % 10 == 0) {
                    Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
                    float height = fontMetrics.bottom - fontMetrics.top;//计算行高

                    float result = minValue + (pts[1] - offsetY) / mItemWidth;
                    canvas.drawText(String.valueOf((int) result), pts[2] + basePadding / 2, pts[3] + height / 3, textPaint);
                    scaleTexts.add(new String[]{String.valueOf((int) result), String.valueOf(pts[2]), String.valueOf(pts[3])});
                }
                scaleLines.add(pts);//记录刻度坐标
            }

        } else {
            for (int i = 0; i < scaleLines.size(); i++) {
                canvas.drawLine(scaleLines.get(i)[0], scaleLines.get(i)[1], scaleLines.get(i)[2], scaleLines.get(i)[3], scalePaint);
            }
            for (int i = 0; i < scaleTexts.size(); i++) {
                Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
                float height = fontMetrics.bottom - fontMetrics.top;//计算行高
                canvas.drawText(scaleTexts.get(i)[0], Float.parseFloat(scaleTexts.get(i)[1]) + basePadding / 2, Float.parseFloat(scaleTexts.get(i)[2]) + height / 3, textPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getX() > endX || event.getX() < startX || event.getY() > startY || event.getY() < endY) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    int disY = (int) (event.getY() - mLastMoveY);
                    if (disY > 0) {//向下滑动
                        if (centerPosition != scaleLines.size() - 1) {
                            scrollBy(0, disY);
                            mLastMoveY = event.getY();
                            mLastMoveX = event.getX();
                            invalidate();
                        } else {
                            mScroller.startScroll(mScroller.getCurrX(), 0, 0, 0);
                            mScroller.setFinalY((int) (getScrollY() + scaleLines.get(centerPosition)[1]));
                            invalidate();
                        }
                    } else {//向上滑动
                        if (centerPosition != 0) {
                            scrollBy(0, disY);
                            mLastMoveY = event.getY();
                            mLastMoveX = event.getX();
                            invalidate();
                        } else {
                            mScroller.startScroll(mScroller.getCurrX(), 0, 0, 0);
                            mScroller.setFinalY((int) (getScrollY() + scaleLines.get(centerPosition)[1]));
                            invalidate();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastMoveY = event.getY();
                mLastMoveX = event.getX();



                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {//平滑移动
            if (0 <= centerPosition && centerPosition <= scaleLines.size() - 1) {
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
                invalidate();
            } else {
               /* scrollTo(mScroller.getCurrX(), 0);
                mScroller.setFinalY((int) (getScrollY() + scaleLines.get(centerPosition)[1]));
                invalidate();*/
            }
        }
    }

    private class FlingOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        public boolean onDown(MotionEvent event) {
            if (event.getX() > endX || event.getX() < startX || event.getY() > startY || event.getY() < endY) {
                getParent().requestDisallowInterceptTouchEvent(false);
            } else {
                getParent().requestDisallowInterceptTouchEvent(true);
                // 点击时取消所有滚动效果
                cancelScroll();
                mLastMoveY = event.getY();
                mLastMoveX = event.getX();
                return true;
            }
            return super.onDown(event);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (0 == centerPosition || centerPosition == scaleLines.size() - 1) {
                return super.onFling(e1, e2, velocityX, velocityY);
            }
            cancelScroll();
            // 惯性滑动
            fling(mScroller.getFinalY(), velocityY);
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mLastMoveY = e.getY();
            mLastMoveX = e.getX();
            performClick();
            return true;
        }
    }

    public void cancelScroll() {
        mScroller.abortAnimation();
    }

    // 平滑滚动
    private void scroll(float from, int to) {
        mScroller.startScroll((int) from, 0, 0, 0);
        mScroller.setFinalX(to);
        invalidate();
    }

    // 惯性滑动，
    private void fling(float from, float vel) {

        mScroller.fling(mScroller.getCurrX(), (int) from, 0, (int) vel, 0, 0,
                (int) (-scaleLines.size() * mItemWidth), (int) (scaleLines.size() * mItemWidth));
//        mScroller.startScroll(mScroller.getCurrX(), (int) from, 0, (int) (10 * mItemWidth),2000);
        invalidate();
    }

    public float[] getOnTouch() {
//        float centerX = startX + (endX - startX) / 2 + getScrollX();
        float centerY = endY + (startY - endY) / 2 + getScrollY();
        if (scaleLines.size() > 0) {
            for (int i = 0; i < scaleLines.size(); i++) {
                float y = scaleLines.get(i)[1];
                if (Math.abs(centerY - y) < mItemWidth) {
                    this.centerPosition = i;
                    this.prePosition = i;
                    return scaleLines.get(i);
                } else {
                    continue;
                }
            }
            this.centerPosition = prePosition;
            return scaleLines.get(prePosition);
        }
        return new float[4];
    }

    public RulerView setSelectValue(final float selectValue) {
        if (selectValue >= minValue && selectValue <= maxValue) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (scaleLines.size() > 0) {
                        float disIndex = selectValue - minValue - prePosition;
                        float disValue = (disIndex - 6) * mItemWidth;//TODO
                        mScroller.startScroll(mScroller.getCurrX(), (int) scaleLines.get(prePosition)[2], 0, (int) disValue);
                        invalidate();
                    }
                }
            }, 800);
        }
        return this;
    }

    private int getCenterPosition() {
        return this.centerPosition;
    }

    public void setOnSelectChangeListener(OnSelectChangeListenrer onSelectChangeListener) {
        this.onSelectChangeListener = onSelectChangeListener;
    }

    public interface OnSelectChangeListenrer {
        void onSelectChange(RulerView rulerView, float selectValue);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}
