package com.sxt.chart.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.sxt.chart.utils.Px2DpUtil;

/**
 * Created by SXT on 2018/1/26.
 */

public class ScrollerLayout extends ViewGroup {


    public ScrollerLayout(Context context) {
        super(context);
        init();
    }

    public ScrollerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int leftBorder;
    private int rightBorder;
    private Scroller mScroller;
    private int touchSlop;
    private int baseMargin = Px2DpUtil.dip2px(getContext(), 16);

    private void init() {
        mScroller = new Scroller(getContext());
        touchSlop = ViewConfiguration.get(getContext()).getScaledPagingTouchSlop();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);//测量子View的大小
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                childAt.layout(i * childAt.getMeasuredWidth(), 0, (i + 1) * childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
            //计算左右滑动的边界
            if (getChildCount() > 0) {
                leftBorder = getChildAt(0).getLeft();
                rightBorder = getChildAt(getChildCount() - 1).getRight();
            }
        }
    }

    @Override
    protected ScrollerLayout.LayoutParams generateLayoutParams(LayoutParams p) {
        return super.generateLayoutParams(p);
    }

    private float downX;
    private float moveX;
    private float upX;
    private float lastMove;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                lastMove = downX;
                break;

            case MotionEvent.ACTION_MOVE:
                moveX = ev.getX();
                lastMove = moveX;
                float abs = Math.abs(moveX - downX);
                if (abs > touchSlop) {
                    return true;//触发滑动事件，拦截子类的事件传递
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                lastMove = downX;
                return true;

            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                float dis = lastMove - moveX;
                if (getScrollX() + dis < leftBorder) {
                    scrollTo(leftBorder, 0);
                    return true;
                } else if (getScrollX() + dis + getWidth() > rightBorder) {
                    scrollTo(rightBorder - getWidth(), 0);
                    return true;
                }
                scrollBy((int) dis, 0);
                lastMove = moveX;

               /* int index = (getScrollX() + getWidth() / 2) / getWidth();
                if (index >= 1 && index < getChildCount() - 1) {
                    getChildAt(index-1).setScaleY(0.875f);
                    getChildAt(index+1).setScaleY(0.875f);
                }*/

                 break;

            case MotionEvent.ACTION_UP:
                upX = event.getX();
                moveX = upX;
                int disIndex = (getScrollX() + getWidth() / 2) / getWidth();
                int targetX = getWidth() * disIndex - getScrollX();
                mScroller.startScroll(getScrollX(), 0, targetX, 0);
                invalidate();

                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
