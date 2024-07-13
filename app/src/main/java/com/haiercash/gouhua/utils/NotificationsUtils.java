package com.haiercash.gouhua.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationManagerCompat;

import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.interfaces.SpKey;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/6/11<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class NotificationsUtils {

    /**
     * 判断是否开启了通知栏权限：是否允许接收通知Notifications
     */
    public static boolean isNotificationsEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * 去设置开启通知权限
     */
    public static void toSetting(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

    /**
     * 保存当前的时间
     */
    public static void saveTime() {
        SpHelper.getInstance().saveMsgToSp(SpKey.LOCK, SpKey.NOTICE_REMIND, String.valueOf(System.currentTimeMillis()));
    }

    /**
     * 判断是否超过一个月的提醒
     */
    public static boolean isTimeRemind() {
        try {
            String saveTime = SpHelper.getInstance().readMsgFromSp(SpKey.LOCK, SpKey.NOTICE_REMIND);
            long sTime = Long.parseLong(CheckUtil.isEmpty(saveTime) ? "0" : saveTime);
            long cTime = System.currentTimeMillis();
            long intervalTime = 30L * 24L * 60L * 60L * 1000L;
            return cTime - sTime > intervalTime;
        } catch (NumberFormatException numberFormatException) {
            saveTime();
            return false;
        }
    }
}
