package com.android.crop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;

/**
 * Created by Sun on 2017/8/7.
 * 状态栏工具类
 */

public class StatusView {

    /**
     * 设置状态栏颜色
     *
     * @param activity 要设置状态栏的activity
     * @param color    状态栏的颜色
     * @param alpha    透明度
     */
    public static void setTransparentBar(Activity activity, @ColorInt int color, int alpha) {

        //6.0以上设置为透明 并默认状态栏颜色字体为黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.setStatusBarColor(Color.TRANSPARENT);
            decorView.setSystemUiVisibility(option);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0--6.0 设置为半透明
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            int finalColor = alpha == 0 ? Color.TRANSPARENT :
                    Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
            window.setStatusBarColor(finalColor);
            decorView.setSystemUiVisibility(option);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4--5.0 添加半透明View
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int finalColor = alpha == 0 ? Color.TRANSPARENT :
                    Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
            decorView.addView(createStatusBarView(activity, finalColor));
        }
    }

    /**
     * 设置当前页面的状态栏颜色是否为黑色
     *
     * @param activity
     * @param isDark
     */
    public static void setStatusBarTextColor(Activity activity, boolean isDark) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        View decor = activity.getWindow().getDecorView();
        if (isDark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    // 4.4---5.1 之间自定义状态栏
    private static View createStatusBarView(Context context, @ColorInt int color) {
        View mStatusBarTintView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(context));
        params.gravity = Gravity.TOP;
        mStatusBarTintView.setLayoutParams(params);
        mStatusBarTintView.setBackgroundColor(color);
        return mStatusBarTintView;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            @SuppressLint("InternalInsetResource") int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }
}
