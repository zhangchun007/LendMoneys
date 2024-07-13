package com.haiercash.gouhua.base;

import com.haiercash.gouhua.BuildConfig;

/**
 * Created by Administrator on 2017/8/11.
 */

public class CommonConfig {

    private static String BAIRONGCID_RELEASE = "3003616";
    private static String BAIRONGCID_DEBUG = "3000425";
    public static String CRASHREPORT_RELEASE = "d18d9d3bda";
    public static String CRASHREPORT_DEBUG = "d18d9d3bda";
    //public static String TDAPPID_RELEASE = "76E94FBDDD2A46ECAB76FDD4ED4AA45B";
    //public static String TDAPPID_DEBUG = "87DED556698341EFBEB34E7B1404DF81";
    public static String TDCHANNELID_DEBUG = "haiercash";
    public static String XGV2ACCESSID_RELEASE = "2100262041";
    public static String XGV2ACCESSID_DEBUG = "2100262041";

    public static final int LOCK_SCREEN_TIME = 1000 * 60 * 5;
    //public static final int READ_PHONE_STATE = 101;//本机状态权限
    public static final String DEFAULT_DEVICEID = "000000000000000";//本机状态权限

    public static String bairongCid = BAIRONGCID_RELEASE;
    //个人中心模版如参
    public static String PERSON_MODEL_UNITY = "personalCenterGH";

    /**
     * 微信APP ID
     */
    public static String WX_APP_ID = "wxac3aa90b3baeffd5";
    public static String WX_APP_SECRET = "ca84ba15457babece897134e949b8a47";

    //一键登录
    public static String ONE_KEY_SECRET = "9d1522ac1ed635a9c60845dfbb2c8d06";

    //网易设备指纹sdk key
    public static String WY_DEVICE_INFO_KEY = BuildConfig.IS_RELEASE ? "662b400a8a3c43b5ad48ae5ae3f2ad46" : "f51155359d0648ffa425815f2c33c8ce";

    /**
     * 听云--appkey
     */
    public static String TY_APP_KEY =  BuildConfig.IS_RELEASE?"4c0dd617c1a741f8b34cf45112158e84":"8a07d708ff69472ea596989aeb34eb36";
    /**
     * 听云--apphost
     */
    public static String TY_APP_HOST =BuildConfig.IS_RELEASE?"saserver.haiercash.com:8000/app": "saserver-stg.haiercash.com/app";
}
