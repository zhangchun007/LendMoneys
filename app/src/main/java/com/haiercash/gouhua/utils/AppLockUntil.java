package com.haiercash.gouhua.utils;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.haiercash.gouhua.activity.inenter.BaseVerifyActivity;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;

import java.lang.ref.WeakReference;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/17
 * 描    述：
 * 修订历史：
 * ================================================================
 */
public class AppLockUntil {
    private static final int SHOW_ANOTHER_ACTIVITY = 0;
    /**
     * 锁屏计时
     */
    private static MyHandler mHandler = null;

    /**
     * 初始化/第一次进入界面（包括隔天等情况）
     */
    public static void initTimes(BaseActivity activity) {
        SharedPreferences lockSp = SpHelper.getInstance().getSpInstance(SpKey.LOCK);
        final long launchTime = lockSp.getLong(SpKey.LOCK_CURRENT_TIME, 0);
        final long currentTime = System.currentTimeMillis();
        if (launchTime == 0) {
            lockSp.edit().putLong(SpKey.LOCK_CURRENT_TIME, currentTime).apply();
        } else {
            long diff = currentTime - launchTime;
            if (CommonConfig.LOCK_SCREEN_TIME <= diff) {
                startLockActivity();
            }
        }
        resetTime(activity);
    }

    /**
     * 判断当前页面是否需要锁屏
     *
     * @param activity 当前activity
     * @return true 是即需要，false 否即不需要
     */
    public static boolean isNeedLock(BaseActivity activity) {
        return !(activity instanceof BaseVerifyActivity) &&
                !activity.getClass().getSimpleName().contains("SplashActivity") &&
                !LoginSelectHelper.getLoginActivityPages().contains(activity.getClass());
    }

    /**
     * 重置计时器
     */
    public static void resetTime(BaseActivity activity) {
        if (mHandler == null) {
            mHandler = new MyHandler();
        }
        mHandler.resetWeak(activity);
        Log.e("----------------->", "开始倒计时");
        mHandler.removeMessages(SHOW_ANOTHER_ACTIVITY);
        Message msg = mHandler.obtainMessage(SHOW_ANOTHER_ACTIVITY);
        mHandler.sendMessageDelayed(msg, CommonConfig.LOCK_SCREEN_TIME);
    }

    /**
     * 释放Handler+重置时间
     */
    public static void resetTimes() {
        if (mHandler != null) {
            mHandler.removeMessages(SHOW_ANOTHER_ACTIVITY);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        final long currentTime = System.currentTimeMillis();
        SharedPreferences.Editor lockEditor = SpHelper.getInstance().getSpEditorInstance(SpKey.LOCK);
        lockEditor.putLong(SpKey.LOCK_CURRENT_TIME, currentTime).commit();
    }

    /**
     * 锁定屏幕
     */
    private static void startLockActivity() {
        if (!AppApplication.isLogIn()) {
            return;
        }
        if (LoginSelectHelper.hasSetBiometric()) {
            //UiUtil.toast("进入指纹验证过程");
            ARouterUntil.getInstance(PagePath.ACTIVITY_VERIFY_BIOMETRIC).put("tag", "lock").navigation();
        } else if (LoginSelectHelper.hasSetGesture()) {
            //UiUtil.toast("进入手势验证过程");
            ARouterUntil.getInstance(PagePath.ACTIVITY_GESTURES_SECRET).put("tag", "lock").navigation();
        }//没有设置快捷登录方式，就保持登录态不做处理
    }

    private static class MyHandler extends Handler {
        private WeakReference<BaseActivity> mActivity;

        void resetWeak(BaseActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                System.out.println(msg);
                if (mActivity == null || mActivity.get() == null) {
                    return;
                }
                super.handleMessage(msg);
                if (msg.what == SHOW_ANOTHER_ACTIVITY) {
                    Log.e("--------------->", "计时锁屏！");
                    startLockActivity();
                }
            } catch (Exception e) {
                Log.e("handleMessage", e.toString());
            }
        }
    }
}
