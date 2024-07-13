package com.app.haiercash.base.utils.system;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/4/12<br/>
 * 描    述：<br/>
 * 修订历史：参考资源:https://blog.csdn.net/yegongheng/article/details/38435553<br/>
 * ================================================================
 */
public class AnimatorUntil {
    /**
     * 使用ValueAnimator实现图片缩放动画
     */
    public static ValueAnimator scaleValueAnimator(final View view, float minScale, long duration) {
        //1.设置目标属性名及属性变化的初始值和结束值
        PropertyValuesHolder mPropertyValuesHolderScaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, minScale);
        PropertyValuesHolder mPropertyValuesHolderScaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, minScale);
        ValueAnimator mAnimator = ValueAnimator.ofPropertyValuesHolder(mPropertyValuesHolderScaleX, mPropertyValuesHolderScaleY);
        //2.为目标对象的属性变化设置监听器
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 3.根据属性名获取属性变化的值分别为ImageView目标对象设置X和Y轴的缩放值
                float animatorValueScaleX = (float) animation.getAnimatedValue("scaleX");
                float animatorValueScaleY = (float) animation.getAnimatedValue("scaleY");
                view.setScaleX(animatorValueScaleX);
                view.setScaleY(animatorValueScaleY);
            }
        });
        //4.为ValueAnimator设置自定义的Interpolator
        mAnimator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                input *= 0.8f;
                return input * input;
            }
        });
        //5.设置动画的持续时间、是否重复及重复次数等属性
        mAnimator.setDuration(duration);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        //6.为ValueAnimator设置目标对象并开始执行动画
        mAnimator.setTarget(view);
        mAnimator.start();
        return mAnimator;
    }

    /**
     * 透明度明暗切换
     *
     * @param view     需要变换的View
     * @param duration 动画的长度设置
     */
    public static void startAlphaAnimator(View view, long duration) {
        ObjectAnimator mAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 1f);
        //5.设置动画的持续时间、是否重复及重复次数等属性
        mAnimator.setDuration(duration);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.start();
    }

    /**
     * 批量结束Animator
     *
     * @param animators 需要结束的Animator
     */
    public static void cancelEndAnimator(Animator... animators) {
        if (animators != null) {
            for (Animator animator : animators) {
                try {
                    if (animator != null) {
                        animator.cancel();
                        animator.end();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
