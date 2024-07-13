package com.app.haiercash.base.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 用户敏感信息存储
 * @Author: zhangchun
 * @CreateDate: 8/19/22
 * @Version: 1.0
 */
public class UserPrivacyInfo {

    public static Map<String, String> map = new LinkedHashMap<>();

    /**
     * 读取数据
     *
     * @param key
     * @param defaultVal
     * @return
     */
    public static String readMsgInfo(String key, String defaultVal) {
        String value = map.get(key);
        return TextUtils.isEmpty(value) ? defaultVal : value;
    }

    /**
     * 保存数据
     *
     * @param key
     * @param value
     * @return
     */
    public static void saveMsgInfo(String key, String value) {
        map.put(key, value);
    }

    /**
     * 删除用户信息
     */
    public static void clearUserInfo() {
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (isUserPrivacyInfo(key)) {
                iterator.remove();        //添加该行代码
            }
        }
    }

    /**
     * 当前用户信息为敏感数据
     *
     * @param key
     * @return
     */
    public static boolean isUserPrivacyInfo(String key) {
        if ("custName".equals(key) || "certNo".equals(key)) {
            return true;
        }
        return false;
    }

    /**
     * 删除地理位置信息
     */
    public static void clearUserLocalInfo() {
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (isLocationPrivacyInfo(key)) {
                iterator.remove();        //添加该行代码
            }
        }
    }

    /**
     * 当前地理信息为敏感数据
     *
     * @param key
     * @return
     */
    public static boolean isLocationPrivacyInfo(String key) {
        if ("longitude".equals(key) ||
                "latitude".equals(key) ||
                "provinceName".equals(key) ||
                "provinceCode".equals(key) ||
                "cityName".equals(key) ||
                "cityCode".equals(key) ||
                "areaName".equals(key) ||
                "areaName".equals(key)) {
            return true;
        }
        return false;
    }
}
