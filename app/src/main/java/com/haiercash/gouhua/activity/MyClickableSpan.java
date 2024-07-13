package com.haiercash.gouhua.activity;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.ColorInt;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 9/21/22
 * @Version: 1.0
 */
public class MyClickableSpan extends ClickableSpan {
    private View.OnClickListener listener;
    private int color;

    public MyClickableSpan(@ColorInt int color, View.OnClickListener listener) {
        this.listener = listener;
        this.color = color;
    }

    @Override
    public void onClick(View widget) {
        listener.onClick(widget);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.setColor(color);
    }
}
