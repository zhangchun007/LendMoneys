package com.haiercash.gouhua.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * @Author: Sun
 * @Date :    2018/12/20
 * @FileName: SlowSwipeRefreshLayout
 * @Description:
 */
public class SlowSwipeRefreshLayout extends SwipeRefreshLayout {
    private float mInitialDownY;
    private float mInitialDownX;
    private int mTouchSlop;

    public SlowSwipeRefreshLayout(Context context) {
        super(context, null);
    }

    public SlowSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isInEditMode()) {
            return super.onInterceptTouchEvent(ev);
        }
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = ev.getY();
                mInitialDownX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                final float yDiff = ev.getY() - mInitialDownY;
                float xDiff = ev.getX() - mInitialDownX;
                //如果y方向移动小于 mTouchSlop或者 y的移动小于x方向的位移，则交由子view进行处理。
                if (yDiff < mTouchSlop || Math.abs(yDiff) < Math.abs(xDiff)) {
                    return false;
                }
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);

    }


}
