package com.haiercash.gouhua.unity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.haiercash.base.CommonManager;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;


import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/28
 * @Version: 1.0
 */
public class FlattenJsonUtils {
    private static Map<String, Object> map = new HashMap<>();

    /**
     * 首页个人数据
     */
    private static Map<String, Object> homeMap = new HashMap<>();

    //个人中心模版数据
    private static String person_model_data_json = "";

    /**
     * 首页模版数据
     */
    private static String home_model_data_json = "";

    public static Map<String, Object> flattenJson(JSONObject json, String prefix) {
        for (String key : json.keySet()) {
            Object value = json.get(key);

            if (value instanceof JSONObject) {
                flattenJson((JSONObject) value, prefix + key + ".");
            } else if (value instanceof JSONArray) {
                flattenArray((JSONArray) value, prefix + key + ".");
            } else {
                Logger.e("flatMap--" + prefix + key + ": " + value);
                map.put(prefix + key, value);
            }
        }
        return map;
    }
    public static Map<String, Object> getHomeMap() {
        return homeMap;
    }
    public static Map<String, Object> flattenHomeJson(JSONObject json, String prefix) {
        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (key.equals("banner") || key.equals("general") || key.equals("notice") || key.equals("dynamic")) {
                homeMap.put(prefix + key, value);
            }else {
                if (value instanceof JSONObject) {
                    flattenJson((JSONObject) value, prefix + key + ".");
                } else if (value instanceof JSONArray) {
                    flattenArray((JSONArray) value, prefix + key + ".");
                } else {
//                Logger.e("flatMap--" + prefix + key + ": " + value);
                    homeMap.put(prefix + key, value);
                }
            }
        }
        return homeMap;
    }

    public static Map<String, Object> getMap() {
        return map;
    }

    /**
     * 保存数据
     */
    public static void saveUserInfo() {
        if (UserStateUtils.isLogIn()) {
            String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
            map.put("userId", EncryptUtil.simpleEncrypt(userId));
            homeMap.put("userId", EncryptUtil.simpleEncrypt(userId));
            String custNo = SpHelper.getInstance().readMsgFromSp(SpKey.USER, SpKey.USER_CUSTNO);
            map.put("custNo", custNo);
            homeMap.put("custNo", custNo);
            String token = SpHelper.getInstance().readMsgFromSp(SpKey.LOGIN, SpKey.LOGIN_ACCESSTOKEN);
            map.put("token", token);
            homeMap.put("token", token);

            String processId = TokenHelper.getInstance().getH5ProcessId();
            map.put("processId", processId);
            homeMap.put("processId", processId);
            String h5token = TokenHelper.getInstance().getH5Token();
            map.put("h5token", h5token);
            homeMap.put("h5token", h5token);
        }
        map.put("channelNo", CommonManager.CHANNEL_ID);
        homeMap.put("channelNo", CommonManager.CHANNEL_ID);
        String app_version = SystemUtils.getAppVersion(AppApplication.CONTEXT);
        map.put("appVersion", app_version);
        homeMap.put("appVersion", app_version);
        String imgSourceApi = BuildConfig.IS_RELEASE ? "https://oss-s3.haiercash.com" : "https://test-oss-s3.haiercash.com";
        map.put("imgSourceApi", imgSourceApi);
        homeMap.put("imgSourceApi", imgSourceApi);
        String apiHost = NetConfig.API_SERVER_URL;
        map.put("apiHost", apiHost);
        homeMap.put("apiHost", apiHost);
    }

    public static void setMap(Map<String, Object> map) {
        FlattenJsonUtils.map = map;
    }

    public static String getPersonModelDataJson() {
        return person_model_data_json;
    }

    public static void setPersonModelDataJson(String modelDataJson) {
        person_model_data_json = modelDataJson;
    }

    public static void flattenArray(JSONArray array, String prefix) {
        for (int i = 0; i < array.size(); i++) {
            Object element = array.get(i);

            if (element instanceof JSONObject) {
                flattenJson((JSONObject) element, prefix + "[" + i + "].");
            } else if (element instanceof JSONArray) {
                flattenArray((JSONArray) element, prefix + "[" + i + "].");
            } else {
                System.out.println(prefix + "[" + i + "]: " + element);
            }
        }
    }
}




