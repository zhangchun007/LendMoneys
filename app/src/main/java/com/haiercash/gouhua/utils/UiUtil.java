package com.haiercash.gouhua.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.BaseApplication;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.view.CustomToast;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.AppApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/21
 * 描    述：UI 工具类
 * 修订历史：
 * ================================================================
 */
public class UiUtil {

    /**
     * 弹出 Toast,纯文本
     */
    public static void toast(Context context, String msg) {
        CustomToast.makeText(context, msg);
    }

    /**
     * 弹出 Toast
     */
    public static void toast(String msg) {
        toast(BaseApplication.CONTEXT, msg);
    }

    /**
     * 弹出 Toast
     */
    public static void toastDeBug(String msg) {
        if (!BuildConfig.IS_RELEASE) {
            toast(BaseApplication.CONTEXT, "debug:" + msg);
        }
    }

    /**
     * 弹出 Toast,纯文本
     */
    public static void toastLongTime(String msg) {
        toastLongTime(BaseApplication.CONTEXT, msg);
    }

    /**
     * 弹出 Toast,纯文本
     */
    public static void toastLongTime(Context context, String msg) {
        CustomToast.makeLongText(context, msg);
    }

    /**
     * 弹出 Toast,带对号图标
     */
    public static void toastImage(String msg) {
        CustomToast.makeText(BaseApplication.CONTEXT, msg);
    }

    /**
     * 获取资源文件String
     */
    public static String getString(@StringRes int resId) {
        return BaseApplication.CONTEXT.getResources().getString(resId);
    }

    /**
     * 获取资源文件Color
     */
    @ColorInt
    public static int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(BaseApplication.CONTEXT, resId);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转换dip
     */
    public static int px2dip(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }


    /**
     * 将px转为sp值，并保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp转为px值，并保证文字大小不变
     */
    public static float sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    /**
     * 复杂的单位切换
     */
    public static float toDimension(Context context, float dip, int complexUnit) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(complexUnit, dip, displayMetrics);
    }
    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getDeviceWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager) AppApplication.CONTEXT.getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getDeviceHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager) AppApplication.CONTEXT.getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        return SystemUtils.getStatusBarHeight(context);
    }


    /**
     * 禁止EditText输入空格
     */
    public static void setEditTextInhibitInputSpace(EditText editText) {
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            if (" ".equals(source)) {
                return "";
            } else {
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

//    /**
//     * 禁止EditText输入特殊字符
//     */
//    public static void setEditTextInhibitInputSpeChat(EditText editText) {
//        InputFilter filter = new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
// int dstart, int dend) {
//                String speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\]
// .<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
//                Pattern pattern = Pattern.compile(speChat);
//                Matcher matcher = pattern.matcher(source.toString());
//                if (matcher.find()) return "";
//                else return null;
//            }
//        };
//        editText.setFilters(new InputFilter[]{filter});
//    }


    /**
     * 获取屏幕密度
     */
    public static float density(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 改变同一个TextView中的字体大小和颜色
     *
     * @param textview 要改变的布局
     * @param style    style文件
     * @param i        从第几个开始改变
     * @param j        结束改变
     */
    public static void setTextColorSize(Context context, TextView textview, int style, int i, int j) {
        //更改字体大小和颜色
        SpannableStringBuilder builder = new SpannableStringBuilder(textview.getText().toString());
        TextAppearanceSpan textAppearanceSpan1 = new TextAppearanceSpan(context, style);
        builder.setSpan(textAppearanceSpan1, i, j, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textview.setText(builder);
    }

    /**
     * 将字体切换成指定颜色
     */
    public static SpannableString getSpannableStr(String name, int start, int end, int color) {
        SpannableString spannableString = new SpannableString(name);
        spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * @param color   关键字颜色
     * @param text    文本
     * @param keyword 关键字
     */
    public static SpannableString getHighLightKeyWord(int color, String text, String keyword) {
        SpannableString s = new SpannableString(text);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

    /**
     * @param color   关键字颜色
     * @param text    文本
     * @param keyword 多个关键字
     */
    public static SpannableString getHighLightKeyWord(int color, String text, String[] keyword) {
        SpannableString s = new SpannableString(text);
        for (String value : keyword) {
            Pattern p = Pattern.compile(value);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;

    }

    /**
     * 获取设备分辨率，当前仅打印Log用，其他地方需要使用再修改
     */
    public static void getDeviceResolution() {
        Logger.e("获取手机设备信息>>>>>>>>", "手机分辨率：" + SystemUtils.getDeviceWidth(BaseApplication.CONTEXT) + "," + SystemUtils.getDeviceHeight(BaseApplication.CONTEXT));
    }

    /**
     * 获取手机分辨率的倍数
     */
    public static String getSizeType(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        if (CheckUtil.isEqual(1.5, density)) {
            return "AND480";
        } else if (CheckUtil.isEqual(2.0, density)) {
            return "AND720";
        } else if (CheckUtil.isEqual(3.0, density)) {
            return "AND1080";
        } else {
            return "AND1080";
        }
    }

    /**
     * 获取银行卡对应的logo
     */
    public static int getBankCardImageRes(String bankCardName) {
        switch (bankCardName) {
            case "中国工商银行":
                return R.drawable.logo_circle_icbc;
            case "中国农业银行":
                return R.drawable.logo_circle_abc;
            case "中国银行":
                return R.drawable.logo_circle_boc;
            case "中国建设银行":
                return R.drawable.logo_circle_ccb;
            case "广发银行":
                return R.drawable.logo_circle_gdb;
            case "招商银行":
                return R.drawable.logo_circle_cmb;
            case "兴业银行":
                return R.drawable.logo_circle_cib;
            case "中国邮政储蓄银行":
                return R.drawable.logo_circle_psbc;
            case "交通银行"://交通银行
                return R.drawable.logo_circle_bocom;
            case "中信银行"://中信银行
                return R.drawable.logo_circle_cncb;
            case "中国光大银行"://光大银行
                return R.drawable.logo_circle_ceb;
            case "华夏银行"://华夏银行
                return R.drawable.logo_circle_hxb;
            case "中国民生银行"://民生银行
                return R.drawable.logo_circle_cmbc;
            case "平安银行"://平安银行
                return R.drawable.logo_circle_pab;
            case "上海浦东发展银行"://浦发银行
                return R.drawable.logo_circle_spdb;
            case "北京银行"://北京银行
                return R.drawable.logo_circle_bccb;
            case "上海银行"://上海银行
                return R.drawable.logo_circle_bos;
            case "青岛银行"://青岛银行
                return R.drawable.logo_circle_qdccb;
            default:
                return R.drawable.backgdefaultx;
        }
    }

    /**
     * 获取银行卡对应的logo背景资源
     */
    public static int getBankCardBgRes(String bankName) {
        switch (bankName) {
            //红色系
            case "中国工商银行":
            case "招商银行":
            case "中国银行":
            case "广发银行":
            case "中信银行":
            case "华夏银行":
            case "北京银行":
            case "青岛银行":
                return R.drawable.bank_red;
            //绿色系
            case "中国农业银行":
            case "中国邮政储蓄银行":
            case "中国民生银行"://民生银行
                return R.drawable.bank_green;
            //蓝色系
            case "中国建设银行":
            case "兴业银行":
            case "交通银行":
            case "上海浦东发展银行":
            case "上海银行":
                return R.drawable.bank_blue;
            //紫色系
            case "中国光大银行":
                return R.drawable.bank_purple;
            //橙色系
            case "平安银行":
                return R.drawable.bank_orange;
            default:
                return R.drawable.bank_blue;
        }
    }

    /**
     * 将内容复制到剪贴板
     */
    public static void copyValueToShearPlate(Context context, String valueText) {
        try {
            //获取剪贴版
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            //创建ClipData对象
            //第一个参数只是一个标记，随便传入。
            //第二个参数是要复制到剪贴版的内容
            ClipData clip = ClipData.newPlainText("simple text", valueText);
            if (clipboard != null) {
                //传入clipdata对象.
                clipboard.setPrimaryClip(clip);
                toast("复制成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解决：Do not concatenate text displayed with setText的问题
     *
     * @param objects 需要拼接的字符串（目前支持的为String、int类型）
     * @return 将所有字符串拼接成一个完整的字符串
     */
    public static String getStr(Object... objects) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objects) {
            sb.append(obj);
        }
        return sb.toString();
    }

    /**
     * @return null字符串变“”
     */
    public static String getEmptyStr(String s) {
        return s == null ? "" : s;
    }

    public static String getEmptyStr(String s, String defVal) {
        return s == null || s.length() == 0 ? defVal : s;
    }

    /**
     * @return 空金额字符串变0.00, 其他变数值
     */
    public static double getEmptyMoneyStr(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.00d;
        }
    }

    public static void hideKeyBord(Activity activity) {//适合Activity
        if (activity != null && activity.getCurrentFocus() != null) {
            ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 是否touch在v之外
     */
    public static boolean isTouchOutOfV(View v, MotionEvent event) {
        if (v == null || v.getVisibility() != View.VISIBLE) {
            return true;
        }
        int[] leftTop = {0, 0};
        //获取v当前的location位置
        v.getLocationInWindow(leftTop);
        int left = leftTop[0];
        int top = leftTop[1];
        int bottom = top + v.getHeight();
        int right = left + v.getWidth();
        return !(event.getX() > left && event.getX() < right
                && event.getY() > top && event.getY() < bottom);
    }
}
