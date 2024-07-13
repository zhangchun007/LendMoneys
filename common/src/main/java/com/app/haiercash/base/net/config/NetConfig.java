package com.app.haiercash.base.net.config;

import android.content.Context;

import com.app.haiercash.base.CommonManager;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;

/**
 * Author: Sun
 * Date :    2017/12/21
 * FileName: NetConfig
 * Description: 1，配置网络请求的基础地址；
 * 2，配置网络请求的超时时间和成功标识；
 * 3，配置网络请求的请求头；
 */

public class NetConfig {

    //baseUrl
    public static String API_SERVER_URL;
//            "http://10.164.194.121:9000/";//测试环境
//            "http://10.164.194.121/";//封测=-
//            "http://10.164.200.110/";//封测B
//            "https://shop.haiercash.com/";//生产
//            "http://58.56.183.206:8888/"; //封测-外网
//            "http://10.164.17.68:8180/"; //临时

    /**
     * APP获取TOKEN
     */
    public static final String API_REQUEST_TOKEN = "app/appserver/refresh_token";


    public static final  String POST_URL_PSERSON_CENTER_NEW = "app/appserver/multi/smartH5UnEncode";
    public static final  String POST_URL_PSERSON_CENTER_NEW2 = "app/appserver/multi/smartH5";
    //获取模版数据
    public static final  String POST_MODEL_DATA = "app/appserver/multi/getModelConfig";
    //timeOut
    public static final long DEFAULT_TIMEOUT = 10;

    //请求成功标识
    public static final String FLAG_NET_SUCCESS = "00000";

    //token失效标志
    public static final String FLAG_TOKEN_INVALID = "invalid_token";

    public static final String NET_CODE_CONNECT = "400";
    public static final String NET_CODE_UNKNOWN_HOST = "401";
    public static final String NET_CODE_SOCKET_TIMEOUT = "402";
    public static final String NET_CODE_TOKEN_INVALID = "999";
    public static final String NET_CODE_LOGIN_INVALID = "2001";
    public static final String NET_CODE_PARSER_ERROR = "601";

    public static final String SOCKET_TIMEOUT_EXCEPTION = "网络连接超时，请检查您的网络状态，稍后重试";
    public static final String DATA_PARSER_ERROR = "服务器开小差了，请稍后再试";
    public static final String TOKEN_INVALID = "登录失效，请重新登录...";

    //http 固定请求头
    private static Headers commonHeaders = null;
    public static String TD_BUSINESS_SMY = "", TD_IDEA_SMY = "", TD_REGIS_VECTOR_SMY = "", TD_APP_DOWN_FROM_SMY = "";//运营业务线/创意/注册载体/下载来源（投放渠道）
    public static HashMap<String, String> TD_HTTP_HEADERS = new HashMap<>();
    public static String TD_BUSINESS_EMPTY = "X0";//检测到空业务线时设置为此值（兼容老客户端注册的用户），因为考虑到定制包的情况所以不能直接用TD_BUSINESS_SMY
    public static Context application;
    public static boolean IS_DEBUG_NET = true;


    public static Headers initRequestHeader() {
        if (commonHeaders == null) {
            initHttpHeaderValue();
            Map<String, String> headMap = new HashMap<>();
            headMap.put("Connection", "close");
            headMap.put("APPVersion", "AND-P-" + SystemUtils.getAppVersion(application));
            headMap.put("DeviceModel", "AND-P-" + SystemUtils.getDeviceModel());
            headMap.put("DeviceResolution", "AND-P-" + SystemUtils.getDeviceWidth(application) + "," + SystemUtils.getDeviceHeight(application));
            headMap.put("SysVersion", "AND-P-" + android.os.Build.VERSION.RELEASE);
            headMap.put("promotionChannel", SystemUtils.metaDataValueForTDChannelId(application));
            headMap.put("channel", "18");
            headMap.put("channel_no", CommonManager.CHANNEL_ID);
            headMap.put("processId", TokenHelper.getInstance().getH5ProcessId());
            headMap.putAll(TD_HTTP_HEADERS);
            commonHeaders = Headers.of(headMap);
        }
        commonHeaders = commonHeaders.newBuilder().set("processId", TokenHelper.getInstance().getH5ProcessId()).build();
        initSmyDefaultValue();
        Logger.e("============", commonHeaders);
        return commonHeaders;
    }

    //获取manifest写入的数据
    private static HashMap<String, String> getHttpHeader() {
        String headerString = SystemUtils.metaDataValue(application, "TD_HTTP_HEADERS");
        if (!CheckUtil.isEmpty(headerString)) {
            try {
                return JsonUtils.fromJson(headerString, HashMap.class);
            } catch (Exception e) {
                Logger.e(e);
                return new HashMap<>();
            }
        }
        return new HashMap<>();
    }

    //配置为 qd_id等于alibaba以及idea等于aaaa以及other等于otherali等于version等于1
    private static void initHttpHeaderValue() {
        try {
            String headerValue = SystemUtils.metaDataValue(application, "TD_HTTP_HEADERS");
            if (CheckUtil.isEmpty(headerValue)) {
                return;
            }
            if (TD_HTTP_HEADERS == null) {
                TD_HTTP_HEADERS = new HashMap<>();
            }
            String[] keyValues = headerValue.split("以及");
            for (String value : keyValues) {
                if (!CheckUtil.isEmpty(value)) {
                    String[] realValue = value.split("等于");
                    if (!CheckUtil.isEmpty(realValue) && realValue.length == 2) {
                        TD_HTTP_HEADERS.put(realValue[0], realValue[1]);
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    private static void initSmyDefaultValue() {
        TD_BUSINESS_SMY = SystemUtils.metaDataValue(application, "TD_BUSINESS_SMY");
        TD_IDEA_SMY = SystemUtils.metaDataValue(application, "TD_IDEA_SMY");
        TD_REGIS_VECTOR_SMY = SystemUtils.metaDataValue(application, "TD_REGIS_VECTOR_SMY");
        TD_APP_DOWN_FROM_SMY = SystemUtils.metaDataValue(application, "TD_APP_DOWN_FROM_SMY");
    }

    public static void registerNet(Context context, boolean isDebug) {
        application = context.getApplicationContext();
        IS_DEBUG_NET = IS_DEBUG_NET && isDebug;
        initSmyDefaultValue();
    }


}
