package com.haiercash.gouhua.widget;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/8/1<br/>
 * 描    述：NestedScrollView依然消费事件，所以我们还需要对NestedScrollView事件进行处理，判断如果是左右滑动的时候，我们不让NestedScrollView处理，而是交给子View处理<br/>
 * 修订历史：https://blog.csdn.net/xiaoshuxgh/article/details/84935714<br/>
 * ================================================================
 */
public class JudgeNestedScrollView extends NestedScrollView {
    private boolean isNeedScroll = true;
    private float xDistance, yDistance, xLast, yLast;

    public JudgeNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public JudgeNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JudgeNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
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
        return super.onInterceptTouchEvent(ev);
    }

    /*
     *改方法用来处理NestedScrollView是否拦截滑动事件
     */
    public void setNeedScroll(boolean isNeedScroll) {
        this.isNeedScroll = isNeedScroll;
    }

}
