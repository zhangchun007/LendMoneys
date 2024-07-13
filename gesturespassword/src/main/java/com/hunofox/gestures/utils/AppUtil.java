package com.hunofox.gestures.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * 项目名称：获取屏幕分辨率
 * 项目作者：胡玉君
 * 创建日期：2016/4/5 10:03.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class AppUtil {

    /**
     * 获取屏幕分辨率
     *
     * @param context
     * @return
     */
    public static int[] getScreenDispaly(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// 手机屏幕的宽度
        int height = dm.heightPixels;// 手机屏幕的高度
        return new int[]{width, height};
    }

}
