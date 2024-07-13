package com.haiercash.gouhua.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/7/31<br/>
 * 描    述：仿照ViewPage没有adapter，直系子空间的数目就是页数<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class UnLoadViewPage extends ViewGroup {
    private final Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mCurScreen;
    private final int mDefaultScreen = 0;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private static final int SNAP_VELOCITY = 600;
    private int mTouchState = TOUCH_STATE_REST;
    private final int mTouchSlop;
    private float mLastMotionX;
    private float mLastMotionY;
    private OnViewChangeListener mOnViewChangeListener;

    private int height = 0;

    /**
     * 设置是否可左右滑动
     */
    private boolean isScroll = true;


    public UnLoadViewPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnLoadViewPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
        mCurScreen = mDefaultScreen;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 对该控件里面的子控件定位
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                final int childWidth = childView.getMeasuredWidth();
                childView.layout(childLeft, 0, childLeft + childWidth,
                        childView.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();
        View child = this.getChildAt(getCurScreen());
        if (child.getVisibility() == GONE) {
            for (int i = 0; i < count; i++) {
                if (this.getChildAt(i).getVisibility() == VISIBLE) {
                    child = this.getChildAt(i);
                }
            }
        }
        child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        height = child.getMeasuredHeight();


        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        // final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // if (widthMode != MeasureSpec.EXACTLY) {
        // throw new IllegalStateException(
        // "ScrollLayout only canmCurScreen run at EXACTLY mode!");
        // }
        // final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        // if (heightMode != MeasureSpec.EXACTLY) {
        // throw new IllegalStateException(
        // "ScrollLayout only can run at EXACTLY mode!");
        // }

        // The children are given the same width and height as the scrollLayout

        for (int i = 0; i < count; i++) {

            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        // Log.e(TAG, "moving to screen "+mCurScreen);
        scrollTo(mCurScreen * width, 0);
    }

    /**
     * According to the position of current layout scroll to the destination
     * page.
     */
    public void snapToDestination() {
        final int screenWidth = getWidth();
        final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
        snapToScreen(destScreen);
    }

    public void snapToScreen(int whichScreen) {
        // 是否可滑动
        if (!isScroll) {
            setToScreen(whichScreen);
        } else {
            scrollToScreen(whichScreen);
        }
    }

    public void scrollToScreen(int whichScreen) {
        // get the valid layout page
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (getScrollX() != (whichScreen * getWidth())) {
            final int delta = whichScreen * getWidth() - getScrollX();
            mScroller.startScroll(getScrollX(), 0, delta, 0,
                    Math.abs(delta));// 持续滚动时间 以毫秒为单位
            mCurScreen = whichScreen;
            // invalidate(); // Redraw the layout

            if (mOnViewChangeListener != null) {
                mOnViewChangeListener.OnViewChange(mCurScreen);
            }
        }

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        } else {
            layoutParams.height = height;
        }
        setLayoutParams(layoutParams);
    }

    public void setToScreen(int whichScreen) {
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        mCurScreen = whichScreen;
        scrollTo(whichScreen * getWidth(), 0);

        if (mOnViewChangeListener != null) {
            mOnViewChangeListener.OnViewChange(mCurScreen);
        }
    }

    public int getCurScreen() {
        return mCurScreen;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isScroll) {
            return false;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (mLastMotionX - x);
                int deltaY = (int) (mLastMotionY - y);
                if (Math.abs(deltaX) < 200 && Math.abs(deltaY) > 10) {
                    break;
                }
                mLastMotionY = y;
                mLastMotionX = x;
                scrollBy(deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                // if (mTouchState == TOUCH_STATE_SCROLLING) {
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();
                if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
                    // Fling enough to move left
                    snapToScreen(mCurScreen - 1);
                } else if (velocityX < -SNAP_VELOCITY
                        && mCurScreen < getChildCount() - 1) {
                    // Fling enough to move right
                    snapToScreen(mCurScreen + 1);
                } else {
                    snapToDestination();
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isScroll) {
            return false;
        }
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE)
                && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }
        final float x = ev.getX();
        final float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(mLastMotionX - x);
                if (xDiff > mTouchSlop) {
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return mTouchState != TOUCH_STATE_REST;

    }

    /**
     * 设置屏幕切换监听器
     */
    public void setOnViewChangeListener(OnViewChangeListener listener) {
        mOnViewChangeListener = listener;
    }

    /**
     * 屏幕切换监听器
     */
    public interface OnViewChangeListener {
        public void OnViewChange(int view);
    }

    /**
     * 设置控件是否可以左右滑动
     */
    public void setCanScroll(boolean scroll) {
        this.isScroll = scroll;
    }
}
