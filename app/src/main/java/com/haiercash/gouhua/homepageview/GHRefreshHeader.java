package com.haiercash.gouhua.homepageview;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haiercash.gouhua.R;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;


public class GHRefreshHeader extends LinearLayout implements RefreshHeader {

    private ImageView mImage;
    private AnimationDrawable mAnimPull;
    private AnimationDrawable mAnimRefresh;

    /**
     * 1，构造方法
     */
    public GHRefreshHeader(Context context) {
        this(context, null, 0);
    }

    public GHRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GHRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.layout_refresh_header, this);
        mImage = view.findViewById(R.id.iv_refresh_header);
    }

    /**
     * 2，获取真实视图（必须返回，不能为null）一般就是返回当前自定义的view
     */
    @NonNull
    @Override
    public View getView() {
        return this;
    }

    /**
     * 3，获取变换方式（必须指定一个：平移、拉伸、固定、全屏）,Translate指平移，大多数都是平移
     */
    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    /**
     * 4，执行下拉的过程
     *
     * @param isDragging
     * @param percent
     * @param offset
     * @param height
     * @param maxDragHeight
     */
    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (percent < 1) {
            mImage.setScaleX(1);
            mImage.setScaleY(1);
        }
    }

    /**
     * 5，一般可以理解为一下case中的三种状态，在达到相应状态时候开始改变
     * 注意：这三种状态都是初始化的状态
     */
    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            //1,下拉刷新的开始状态：下拉可以刷新
            case PullDownToRefresh:
                mImage.setImageResource(R.drawable.icon_home_pull);
                break;
            //2,下拉到最底部的状态：释放立即刷新
            case ReleaseToRefresh:
                mImage.setImageResource(R.drawable.home_refresh);
                mAnimPull = (AnimationDrawable) mImage.getDrawable();
                mAnimPull.start();
                break;
            //3,下拉到最底部后松手的状态：正在刷新
            case Refreshing:
                mImage.setImageResource(R.drawable.home_refresh);
                mAnimRefresh = (AnimationDrawable) mImage.getDrawable();
                mAnimRefresh.start();
                break;
        }
    }

    /**
     * 6，结束下拉刷新的时候需要关闭动画
     *
     * @param refreshLayout
     * @param success
     * @return
     */
    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        if (mAnimRefresh != null && mAnimRefresh.isRunning()) {
            mAnimRefresh.stop();
        }
        if (mAnimPull != null && mAnimPull.isRunning()) {
            mAnimPull.stop();
        }
        return 0;
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
}