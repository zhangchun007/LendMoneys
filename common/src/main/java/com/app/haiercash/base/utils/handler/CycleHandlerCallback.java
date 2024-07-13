package com.app.haiercash.base.utils.handler;


import android.app.Activity;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * @Author: Sun
 * @Date :    2018/1/17
 * @FileName: CycleHandler
 * @Description:
 */

public abstract class CycleHandlerCallback implements Callback {
    protected WeakReference<Activity> activityWeakReference;

    public CycleHandlerCallback(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (activityWeakReference == null || activityWeakReference.get() == null
                || activityWeakReference.get().isFinishing()
                || activityWeakReference.get().isDestroyed()) {
            Log.e("CycleHandler", "activity gone");
            return false;
        } else {
            dispatchMessage(msg);
        }
        return true;
    }

    /**
     * 抽象方法用户实现,用来处理具体的业务逻辑
     *
     * @param msg
     */
    public abstract void dispatchMessage(Message msg);

}
