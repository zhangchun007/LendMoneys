package com.haiercash.gouhua.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.SystemUtils;

/**
 * 自定义底部系统导航栏占位视图，用于popWindow设置全屏幕边框setClippingEnabled(false)底部导航栏占位
 */
public class NavigationBar extends View {
    public NavigationBar(Context context) {
        super(context);
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(SystemUtils.getNavigationBarHeight(getContext()), MeasureSpec.EXACTLY));
    }
}
