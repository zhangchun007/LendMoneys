package com.haiercash.gouhua.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ================================================================
 * 作    者：stone<p/>
 * 邮    箱：shixiangfei@haiercash.com<p/>
 * 版    本：1.0<p/>
 * 创建日期：2021/8/11<p/>
 * 描    述：<p/>
 * 修订历史：<br/>
 * ================================================================
 */
public class JudgeRecyclerView extends RecyclerView {
    private static final int MODE_IDLE = 0;
    private static final int MODE_HORIZONTAL = 1;
    private static final int MODE_VERTICAL = 2;
    private int scrollMode;

    private boolean isNeedScroll = true;
    private int mTouchSlop = 20; // 判定为滑动的阈值，单位是像素
    private float xDistance, yDistance, xLast, yLast;
    LinearLayoutManager layoutManager = null;

    public JudgeRecyclerView(@NonNull Context context) {
        super(context);
    }

    public JudgeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JudgeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                scrollMode = MODE_IDLE;
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (scrollMode == MODE_IDLE) {
                    float xDistance = Math.abs(xLast - ev.getX());
                    float yDistance = Math.abs(yLast - ev.getY());
                    if (xDistance > yDistance && xDistance > mTouchSlop) {
                        scrollMode = MODE_HORIZONTAL;
                    } else if (yDistance > xDistance && yDistance > mTouchSlop) {
                        scrollMode = MODE_VERTICAL;
                        if (yLast > ev.getY() && isVisBottom()) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                        }
                    }


                    final float curX = ev.getX();
                    final float curY = ev.getY();
                    xDistance += Math.abs(curX - xLast);
                    yDistance += Math.abs(curY - yLast);
                    xLast = curX;
                    yLast = curY;
                    if (xDistance < 5 && yDistance < 5) {
                        //如果横纵坐标的偏移量都小于五个像素，那么就把它当做点击事件触发
                        //防止isNeedScroll为true时，消费了子空间的点击事件
                        break;
                    } else if (xDistance > yDistance) {
                        return false;
                    } else {
                        return isNeedScroll;
                    }
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /*
     *改方法用来处理NestedScrollView是否拦截滑动事件
     */
    public void setNeedScroll(boolean isNeedScroll) {
        this.isNeedScroll = isNeedScroll;
    }

    public boolean isVisBottom() {
        if (layoutManager == null) {
            layoutManager = (LinearLayoutManager) getLayoutManager();
        }
        if (layoutManager == null) {
            return false;
        }
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == this.SCROLL_STATE_IDLE) {
            return true;
        } else {
            return false;
        }
        //判断recyclerview是否滑动到了最底下
//        if (layoutManager.findViewByPosition(layoutManager.findFirstVisibleItemPosition()).getTop() == 0
//                && layoutManager.findFirstVisibleItemPosition() == 0) {
//            return true;
//        } else {
//            return false;
//        }
    }
}
