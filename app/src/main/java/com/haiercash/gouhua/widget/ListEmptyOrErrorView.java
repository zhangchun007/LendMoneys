package com.haiercash.gouhua.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/1/9<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ListEmptyOrErrorView extends LinearLayout {
    private View vLine;
    private LinearLayout rootView;
    private ImageView iv_pic;
    private TextView tv_msg;

    public ListEmptyOrErrorView(Context context) {
        this(context, null);
    }

    public ListEmptyOrErrorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListEmptyOrErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.list_empty_view, this);
        vLine = findViewById(R.id.v_line);
        rootView = findViewById(R.id.ll_parent);
        iv_pic = findViewById(R.id.iv_pic);
        tv_msg = findViewById(R.id.tv_msg);
    }

    public void setImageCoordinate(int marginTop) {
        LayoutParams layoutParams = (LayoutParams) iv_pic.getLayoutParams();
        layoutParams.topMargin = marginTop;
        iv_pic.setLayoutParams(layoutParams);
    }

    public void setEmptyData(int resId) {
        setEmptyData(resId);
    }

    public void setEmptyData(String msg) {
        setEmptyData(-1, msg);
    }

    public void setEmptyData(int resId, String msg) {
        if (resId != -1) {
            iv_pic.setImageResource(resId);
            iv_pic.setVisibility(VISIBLE);
        } else {
            iv_pic.setVisibility(GONE);
        }
        if (!CheckUtil.isEmpty(msg)) {
            tv_msg.setText(msg);
            tv_msg.setVisibility(VISIBLE);
        } else {
            tv_msg.setVisibility(GONE);
        }
    }

    public void setBgResource(int resId) {
        rootView.setBackgroundResource(resId);
    }

    public void setLineGone(boolean isGone) {
        vLine.setVisibility(isGone ? GONE : VISIBLE);
    }

    public void setBgColor(int color) {
        rootView.setBackgroundColor(color);
    }

    public ListEmptyOrErrorView setTextSize(int spSize) {
        tv_msg.setTextSize(spSize);
        return this;
    }
}
