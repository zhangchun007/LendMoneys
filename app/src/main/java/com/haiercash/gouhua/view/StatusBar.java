package com.haiercash.gouhua.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.haiercash.gouhua.utils.UiUtil;

/**
 * 自定义状态栏占位视图，用于沉浸式状态栏占位
 */
public class StatusBar extends View {
    public StatusBar(Context context) {
        super(context);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(UiUtil.getStatusBarHeight(getContext()), MeasureSpec.EXACTLY));
    }
}
