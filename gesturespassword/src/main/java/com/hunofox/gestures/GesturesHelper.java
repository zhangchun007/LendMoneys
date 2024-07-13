package com.hunofox.gestures;

import android.app.Activity;
import android.content.Intent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hunofox.gestures.interfaces.IAccount;
import com.hunofox.gestures.view.GesturesCheckView;

/**
 * 项目名称：手势密码 一键调用
 * 项目作者：胡玉君
 * 创建日期：2016/4/5 9:27.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * <p>
 * 1.在进入手势密码验证界面(GesturesCheckActivity)之前应做如下操作：
 * <p>
 * ----------------------------------------------------------------------------------------------------
 */
public class GesturesHelper {
    public static final String PASSWORD = "gestures_password";//手势密码
    public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";//手机号码
    public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";//意图

    /**
     * 单例模式+懒加载
     */
    public GesturesHelper() {
    }

//    private static class SingleTonHolder {
//        private static GesturesHelper instance = new GesturesHelper();
//    }
//
//    public static GesturesHelper getInstance() {
//        return SingleTonHolder.instance;
//    }

    private void toActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    /**
     * 获取 超出指定次数，账户立即被冻结
     */
    public static final String TYPE_FREEZE = "freeze";

    public GesturesCheckView getFreezeGesturesView(Activity context, String password, FrameLayout container, IAccount callback, TextView tv, int count, boolean showGestureWay) {
        return new GesturesCheckView(context, password, container, callback, tv, count, 0, 0, 0, true, showGestureWay);
    }

    /**
     * 获取超出指定次数，账户开始读秒，开始时间1分钟，最多5分钟
     */
    public static final String TYPE_SECOND = "freezeBySecond";

    public GesturesCheckView getSecondGesturesView(Activity context, String password, FrameLayout container, IAccount callback, TextView tv, int count, boolean showGestureWay) {
        return new GesturesCheckView(context, password, container, callback, tv, count, 60000, 120000, 300000, false, showGestureWay);
    }

    /**
     * 获取超出5次输入错误，账户立即被冻结
     */
    public static final String TYPE_FREEZE_FIVE = "freezeByFive";

    public GesturesCheckView getFreezeGesturesView(Activity context, String password, FrameLayout container, IAccount callback, TextView tv, boolean showGestureWay) {
        return getFreezeGesturesView(context, password, container, callback, tv, 5, showGestureWay);
    }
}
