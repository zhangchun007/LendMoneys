package com.haiercash.gouhua.utils;

import android.text.TextUtils;

import com.app.haiercash.base.utils.UserPrivacyInfo;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.interfaces.SpKey;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/12/26<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class SpHp {


    /* ***************************************************************************************** */

    /**
     * 保存 SpKey.LOGIN
     */
    public static void saveSpLogin(String key, String value) {
        SpHelper.getInstance().saveMsgToSp(SpKey.LOGIN, key, value);
    }

    /**
     * 获取 SpKey.LOGIN
     */
    public static String getLogin(String key) {
        return getLogin(key, "");
    }

    /**
     * 获取 SpKey.LOGIN
     */
    public static String getLogin(String key, String defaultValue) {
        return SpHelper.getInstance().readMsgFromSp(SpKey.LOGIN, key, defaultValue);
    }

    /**
     * 删除 SpKey.LOGIN
     */
    public static void deleteLogin(String key) {
        SpHelper.getInstance().deleteMsgFromSp(SpKey.LOGIN, key);
    }

    /* ***************************************************************************************** */

    /**
     * 保存 SpKey.STATE
     */
    public static void saveSpState(String key, String value) {
        SpHelper.getInstance().saveMsgToSp(SpKey.STATE, key, value);
    }

    /**
     * 获取 SpKey.STATE
     */
    public static String getState(String key) {
        return SpHelper.getInstance().readMsgFromSp(SpKey.STATE, key);
    }

    /**
     * 删除 SpKey.STATE
     */
    public static void deleteState(String key) {
        SpHelper.getInstance().deleteMsgFromSp(SpKey.STATE, key);
    }

    /* ***************************************************************************************** */

    /**
     * 保存 SpKey.USER
     */
    public static void saveUser(String key, String value) {
        SpHelper.getInstance().saveMsgToSp(SpKey.USER, key, value);
    }

    /**
     * 获取 SpKey.USER
     */
    public static String getUser(String key) {
        return getUser(key, "");
    }

    /**
     * 获取 SpKey.USER
     */
    public static String getUser(String key, String defaultValue) {
        return SpHelper.getInstance().readMsgFromSp(SpKey.USER, key, defaultValue);
    }

    /* ***************************************************************************************** */

    /**
     * 保存SpKey.OTHER
     */
    public static void saveSpOther(String key, String value) {
        SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, key, value);
    }

    /**
     * 获取SpKey.OTHER
     */
    public static String getOther(String key) {
        return getOther(key, "");
    }

    /**
     * 获取SpKey.OTHER
     */
    public static String getOther(String key, String defaultValue) {
        return SpHelper.getInstance().readMsgFromSp(SpKey.OTHER, key, defaultValue);
    }

    /**
     * 获取SpKey.OTHER中的Boole值<p/>
     * 判断依据为key对应的值是Y或者N
     */
    public static boolean getOtherBoole(String key, boolean defaultValue) {
        String value = getOther(key);
        if (CheckUtil.isEmpty(value)) {
            return defaultValue;
        } else {
            return "Y".equals(value.toUpperCase()) || "TRUE".equals(value.toUpperCase());
        }
    }

    /**
     * 获取SpKey.OTHER中的Boole值,default为true<p/>
     * 判断依据为key对应的值是Y或者N
     */
    public static boolean getOtherBoole(String key) {
        String value = getOther(key);
        if (CheckUtil.isEmpty(value)) {
            return true;
        } else {
            return "Y".equals(value.toUpperCase()) || "TRUE".equals(value.toUpperCase());
        }
    }

    /**
     * 删除 SpKey.OTHER
     */
    public static void deleteOther(String... keys) {
        for (String key : keys) {
            SpHelper.getInstance().deleteMsgFromSp(SpKey.OTHER, key);
        }
    }

    /* ***************************************************************************************** */
}
