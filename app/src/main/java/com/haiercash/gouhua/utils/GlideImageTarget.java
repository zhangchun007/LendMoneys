package com.haiercash.gouhua.utils;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/4/11<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public abstract class GlideImageTarget extends CustomViewTarget<View, Drawable> {

    public GlideImageTarget(View view) {
        super(view);
    }

    @Override
    protected void onResourceCleared(@Nullable Drawable placeholder) {
        if (placeholder != null) {
            onResourceReady(placeholder);
        }
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        if (errorDrawable != null) {
            onResourceReady(errorDrawable);
        }
    }

    @Override
    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        onResourceReady(resource);
    }


    protected abstract void onResourceReady(@NonNull Drawable resource);
}
