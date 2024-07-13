package com.app.haiercash.base.utils.system;

import android.content.Context;
import android.graphics.Typeface;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/10/18<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class FontCustom {
    // fongUrl是自定义字体分类的名称
    public static String MEDIUM = "Roboto-Medium.ttf";//中等
    public static String DIN = "din-medium_0.ttf";//din
    public static String DIN_ALTERNATE_BOLD = "DIN-Alternate-Bold.ttf";//DIN-Alternate-Bold.ttf
    //Typeface是字体，这里我们创建一个对象
    private static Typeface tfMedium;
    private static Typeface tfDin;
    private static Typeface tfDinAlternateBold;

    /**
     * 设置字体
     */
    public static Typeface getMediumFont(Context context) {
        if (tfMedium == null) {//给它设置你传入的自定义字体文件，再返回回来
            tfMedium = getFont(context, MEDIUM);
        }
        return tfMedium;
    }

    /**
     * 设置字体  isDin--是否是din字体
     */
    public static Typeface getDinFont(Context context) {
        if (tfDin == null) {//给它设置你传入的自定义字体文件，再返回回来
            tfDin = getFont(context, DIN);
        }
        return tfDin;
    }

    /**
     * 设置字体  是否是DIN-Alternate-Bold字体
     * 不能设置为数字，因为会使数字上下不齐，所以一般配合getDinFont显示数字getDinAlternateBoldFont显示其他文字来使用
     */
    public static Typeface getDinAlternateBoldFont(Context context) {
        if (tfDinAlternateBold == null) {//给它设置你传入的自定义字体文件，再返回回来
            tfDinAlternateBold = getFont(context, DIN_ALTERNATE_BOLD);
        }
        return tfDinAlternateBold;
    }

    /**
     * 设置字体
     */
    public static Typeface getFont(Context context, String fongUrl) {
        return Typeface.createFromAsset(context.getAssets(), fongUrl);
    }

}
