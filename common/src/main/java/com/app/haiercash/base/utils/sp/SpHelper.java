package com.app.haiercash.base.utils.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.app.haiercash.base.utils.UserPrivacyInfo;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.InfoEncryptUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangjie on 2017/6/5.
 * SP存值帮助类，统一管理sp存值
 * 这个类中只有存取String数据的一些公共操作方法，需要存取其他类型的可以通过方法获取实例，然后进行存取
 * 存取时为保证存值与取值key一致，相对应的key值存在接口文件 SpKey中
 * 需要新增值类型存进sp中时，key值也要写到 SpKey中，方便管理与查阅
 */

public class SpHelper {

    private Context application;

    public void registerSharedPreferences(Context context) {
        this.application = context.getApplicationContext();
    }


    /**
     * 获取sp实例公共方法，需要传入sp的key值
     */
    public synchronized SharedPreferences getSpInstance(String spKey) {
        if (application == null) {
            Log.e(this.getClass().getSimpleName(), "请先进行注册");
            return null;
        }
        return application.getSharedPreferences(spKey, Context.MODE_PRIVATE);
    }

    /**
     * 获取editor实例的公共方法，需要传入sp的key值
     */
    public synchronized SharedPreferences.Editor getSpEditorInstance(String spKey) {
        return getSpInstance(spKey).edit();
    }

    /**
     * 保存sp信息的公共方法
     * spKey：sp的key值
     * valueKey：键值对的key值
     */
    public void saveMsgToSp(String spKey, String valueKey, String value) {
        //存取用户隐私数据
        if ("USER".equals(spKey) && UserPrivacyInfo.isUserPrivacyInfo(valueKey)) {
            UserPrivacyInfo.saveMsgInfo(valueKey, value);
            return;
        }
        if ("LOCATION".equals(spKey) && UserPrivacyInfo.isLocationPrivacyInfo(valueKey)) {
            UserPrivacyInfo.saveMsgInfo(valueKey, value);
            return;
        }
        SharedPreferences.Editor editor = getSpEditorInstance(spKey);
        if (!TextUtils.isEmpty(value)) {
            value = EncryptUtil.simpleEncrypt(value);
        }
        editor.putString(valueKey, value).commit();
    }

    /**
     * 读取sp信息的公共方法
     * spKey：sp的key值
     * valueKey：键值对的key值
     */
    public String readMsgFromSp(String spKey, String valueKey) {
        return readMsgFromSp(spKey, valueKey, "");
    }

    /**
     * 读取sp信息的公共方法
     * spKey：sp的key值
     * valueKey：键值对的key值
     */
    public String readMsgFromSp(String spKey, String valueKey, String defaultValue) {
        try {
            //获取用户隐私数据
            if ("USER".equals(spKey) && UserPrivacyInfo.isUserPrivacyInfo(valueKey)) {
                return UserPrivacyInfo.readMsgInfo(valueKey, defaultValue);
            }
            if ("LOCATION".equals(spKey) && UserPrivacyInfo.isLocationPrivacyInfo(valueKey)) {
                return UserPrivacyInfo.readMsgInfo(valueKey, defaultValue);
            }
            String value = getSpInstance(spKey).getString(valueKey, defaultValue);
            if (TextUtils.isEmpty(value) || value.equals(defaultValue)) {
                return defaultValue;
            }
            return EncryptUtil.simpleDecrypt(value);
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 删除某个sp文件下的所有值的公共方法
     * spKey：sp的key值
     */
    public void deleteAllMsgFromSp(String spKey) {
        if ("USER".equals(spKey)) {
            UserPrivacyInfo.clearUserInfo();
        }
        if ("LOCATION".equals(spKey)) {
            UserPrivacyInfo.clearUserLocalInfo();
        }
        SharedPreferences.Editor editor = getSpEditorInstance(spKey);
        editor.clear().commit();
    }

    /**
     * 删除sp中某一条信息的公共方法
     * spKey：sp的key值
     * valueKey：键值对的key值
     */
    public void deleteMsgFromSp(String spKey, String valueKey) {
        SharedPreferences.Editor editor = getSpEditorInstance(spKey);
        editor.remove(valueKey).commit();
    }

    /**
     * 需要删除多个sp中的数据时使用
     * 将初始化sp的key值封装成list传进来即可
     *
     * @param list
     */
    public void deleteAllMsgFromSpList(List<String> list) {
        for (String str : list) {
            deleteAllMsgFromSp(str);
        }
    }

    /**
     * 需要删除多个sp中的数据时使用
     * 将初始化sp的key值封装成list传进来即可
     */
    public void deleteAllMsgFromSpList(String... sps) {
        deleteAllMsgFromSpList(Arrays.asList(sps));
    }

    //单例
    private static final class SingleTonHolder {
        private static final SpHelper instance = new SpHelper();
    }

    public static SpHelper getInstance() {
        return SingleTonHolder.instance;
    }

    private SpHelper() {
    }
}
