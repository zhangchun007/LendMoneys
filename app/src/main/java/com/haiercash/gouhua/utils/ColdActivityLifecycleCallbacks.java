package com.haiercash.gouhua.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.app.haiercash.base.utils.system.SystemUtils;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com<br/>
 * 版    本：1.0<br/>
 * 创建日期：2021/3/18<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ColdActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private static boolean isLive = false;
    private int index = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (index == 0) {
            if (isLive) {
                if (SystemUtils.isAllow) {
                    UMengUtil.appStartEvent("热启动");
                }
                //Logger.e("ColdActivityLifecycleCallbacks", "热启动");
            } else {
                if (SystemUtils.isAllow) {
                    UMengUtil.appStartEvent("冷启动");
                }
                //Logger.e("ColdActivityLifecycleCallbacks", "冷启动");
            }
        }
        if (!isLive) {
            isLive = true;
        }
        index++;
        //Logger.e("ColdActivityLifecycleCallbacks", "index = " + index);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        index--;
//        if (!CheckUtil.isOnForeignGround()) {
//            //app 进入后台
//            isLive = false;
//            //全局变量isActive = false 记录当前已经进入后台
//        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
