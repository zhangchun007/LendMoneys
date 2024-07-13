package com.hunofox.gestures.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 项目名称：账户工具
 * 项目作者：胡玉君
 * 创建日期：2016/4/5 17:14.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class AccountHelper {
    public static final String GESTURES = "Gestures_password_SharedPreferences_file_name";
    public static final String GESTURES_IS_FREEZE = "Gestures_password_SharedPreferences_is_freeze";
    public static final String GESTURES_COUNT = "Gestures_password_SharedPreferences_count";
    private SharedPreferences sp;
    /**
     * 单例+懒加载
     */
    private AccountHelper() {
    }

    private static class SingleTonHolder {
        private static AccountHelper instance = new AccountHelper();
    }

    public static AccountHelper getInstance() {
        return SingleTonHolder.instance;
    }

    public int getCount(Context context) {
        sp = context.getSharedPreferences(GESTURES, Context.MODE_PRIVATE);
        int count = sp.getInt(GESTURES_COUNT, 0);
        sp = null;
        return count;
    }

    public boolean isFreeze(Context context) {
        sp = context.getSharedPreferences(GESTURES, Context.MODE_PRIVATE);
        /* 默认值为 冻结，若用户清理了数据，则默认账户被冻结，若冻结了则请求服务器验证是否被冻结 */
        boolean isFreeze = sp.getBoolean(GESTURES_IS_FREEZE, false);
        sp = null;
        return isFreeze;
    }

    public void setCount(Context context, int count) {
        sp = context.getSharedPreferences(GESTURES, Context.MODE_PRIVATE);
        sp.edit().putInt(GESTURES_COUNT, count).apply();
        sp = null;
    }

    public void setIsFreeze(Context context, boolean isFreeze) {
        sp = context.getSharedPreferences(GESTURES, Context.MODE_PRIVATE);
        sp.edit().putBoolean(GESTURES_IS_FREEZE, isFreeze).apply();
        sp = null;
    }

}
