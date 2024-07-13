package com.app.haiercash.base.utils.image;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/6/14<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class DrawableUtils {
    /**
     * 圆角背景+圆角
     */
    public static GradientDrawable shapeColorRadius(int color, float radius) {
        return shapeColorRadius(color, radius, radius, radius, radius);
    }

    /**
     * 圆角背景+圆角
     */
    public static GradientDrawable shapeColorRadiusTopBottom(int color, float topRadius, float bottomRadius) {
        return shapeColorRadius(color, topRadius, topRadius, bottomRadius, bottomRadius);
    }

    /**
     * 圆角背景+圆角
     */
    public static GradientDrawable shapeColorRadiusLeftRight(int color, float leftRadius, float rightRadius) {
        return shapeColorRadius(color, leftRadius, rightRadius, leftRadius, rightRadius);
    }

    /**
     * 圆角背景+圆角
     *
     * @param color             背景色
     * @param topLeftRadius     左上角
     * @param topRightRadius    右上角
     * @param bottomLeftRadius  右下角
     * @param bottomRightRadius 左下角
     */
    public static GradientDrawable shapeColorRadius(int color, float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        GradientDrawable drawable = new GradientDrawable();
        //drawable.setCornerRadius(UiUtil.dip2px(AppApplication.CONTEXT, 5));//设置4个角的弧度
        //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
        drawable.setCornerRadii(new float[]{topLeftRadius,
                topLeftRadius, topRightRadius, topRightRadius,
                bottomRightRadius, bottomRightRadius, bottomLeftRadius,
                bottomLeftRadius});
        drawable.setColor(color);// 设置颜色
        return drawable;
    }
}
