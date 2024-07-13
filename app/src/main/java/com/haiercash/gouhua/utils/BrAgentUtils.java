package com.haiercash.gouhua.utils;

import android.content.Context;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.bairong.mobile.BrAgent;
import com.bairong.mobile.BrEventType;
import com.bairong.mobile.BrResponse;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.beans.br.BrBean;
import com.haiercash.gouhua.beans.br.BrEventParam;
import com.haiercash.gouhua.beans.br.BrInitDataBean;
import com.haiercash.gouhua.beans.br.BrInitParam;
import com.haiercash.gouhua.beans.br.InitParamBean;
import com.haiercash.gouhua.interfaces.SpKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Author: Sun
 * Date :    2017/12/7
 * FileName: BrAgentUtils
 * Description: 百融工具类
 * 注册事件（BrEventTypeRegister）
 * 登录事件（BrEventTypeLogin）
 * 借款事件（BrEventTypeLend）
 * 提现事件（BrEventTypeCash）
 * 还款事件（BrEventTypeRepay）
 * 营销-优惠券事件（BrEventTypeMkCoupon）
 * 营销-积分事件（BrEventTypeMkPoint）
 * 营销-里程事件（BrEventTypeMkMileage）
 * 营销-保险事件（BrEventTypeMkInsure）
 * 营销-实物事件（BrEventTypeMkArticle）
 */

public class BrAgentUtils {
    public static boolean IS_DEBUG_BR = false;
    /**
     * {
     * "api_code": "3003616",
     * "swift_number": "f3c4c33c817bf10150c97166ce8092b1",
     * "sign": "8e5a9b19ef22164b7b0781410d064a22",
     * "param": {"plat_type":"android","br_version":"4.0.1","battery_plug":"2","battery_status":"3","device_id":"868035039173189","bssid_ip":"192.168.59.112","gid":"30004250030015774168295323845512","brand":"HUAWEI","MAC":"6c:b7:49:c2:76:53","UUID":"adbc55d4-99b2-4cf2-903f-d407525a2f29","statistic_id":"3003616_4bbe6c63-075a-4bb8-abe8-1ad968704b79","android_id":"00fef3657100dec9","os_version":"10","app_name":"够花封58","app_version":"3.6.0","package_name":"com.haiercash.gouhua","model":"ALP-AL00","resolution":"2412x1440","network_speed":"468","network_signal":"-56","client_status":"COMPLETED","board":"ALP","bootloader":"unknown","cpu_abi":"armeabi-v7a","driver":"HWALP","device_version":"HUAWEIALP-AL00","fingerpring":"e40780438f7a4156886b8a704fa1752b","hardware":"kirin970","device_host":"cn-west-3b-28f81ae491595390406091-865c478c76-qpxxc","manufacturer":"HUAWEI","product":"ALP-AL00","tags":"release-keys","type":"user","incremental":"10.0.0.175C00","cpuInfo":"92a07e46e5edfef2c9fa9d2bc32e9fd3","is_root":"0","is_simulator":"0","is_vpn_proxy":"0","is_wifi_proxy":"0","disk_total":"52.71G","disk_free":"37.10G","is_dev":"1","is_siml":"0","is_ect":"0","is_uct":"0","is_rv":"0","is_monkey":"0","boot_time":"1630064420325","boot_duration":"944785769","battery_level":"57","battery_scale":"100","battery_vol":"3.7","battery_tem":"29.0","battery_health":"2","volume_call":"14","volume_alarm":"13","volume_media":"1","volume_ring":"5","volume_system":"5","brightness":"0.33","time_zone":"GMT+08:00","memory_total":"3637.52M","languages":["zh"],"network_type":"2","network_ip":"192.168.59.112","is_wlan_open":"1","is_multirun":"0","application_list":[{"package_name":"com.tencent.mm","app_name":"微信","app_version":"8.0.11"},{"package_name":"com.haiercash.gouhua","app_name":"够花封58","app_version":"3.6.0"},{"package_name":"haiermerchant.com.haiermerchantapp","app_name":"商户封测版","app_version":"2.0.7"},{"package_name":"com.autonavi.minimap","app_name":"高德地图","app_version":"11.01.2.2856"},{"package_name":"com.alibaba.android.rimet","app_name":"钉钉","app_version":"6.0.27"}]}
     * }
     */
    public static String objInitBean; // objInitBean为原始数据
    private static String br_token_id = "00000000";
    private static final String GET_GID = "https://das.bairong.cn/queenbee/gid/get";
    private static final String EVENT_PUT = "https://das.bairong.cn/queenbee/event_data/put";

    /**
     * 初始化百融
     */
    public static void initBrAgent(final BrAgentSucc callback) {
        //配置代理地址
        BrAgent.setDataEncrypt(true);
        //百融初始化
        BrAgent.brGetInitParams(AppApplication.CONTEXT, CommonConfig.bairongCid, brResponse -> {
            if (IS_DEBUG_BR) {
                Logger.d("BrAgentUtils : 初始化：" + brResponse.getCode());
            }
            if (brResponse.getCode() != 0) {
                if (callback != null) {
                    callback.onSucced("", null);
                }
                return;
            }
            objInitBean = brResponse.getData();
            BrInitParam initParam = new BrInitParam();
            try {
                BrInitDataBean dataBean = JsonUtils.fromJson(brResponse.getData(), BrInitDataBean.class);
                if (dataBean != null) {
                    InitParamBean bean1 = JsonUtils.fromJson(dataBean.getParam(), InitParamBean.class);
                    dataBean.setParam(bean1);
                }
                initParam.setBrData(dataBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (IS_DEBUG_BR) {
                Logger.d("BrAgentUtils : 请求接口获取gid：" + brResponse.getData());
            }
            checkSuccess(brResponse.getData(), initParam, callback);
        });
    }

    /**
     * 保存百融gid
     */
    private static void checkSuccess(String data, final BrInitParam initParam, final BrAgentSucc callback) {
        BrResponse response = BrAgent.brTransformJson(data, true, true);
        if (response != null && response.getCode() == 0) {
            // 加密压缩成功
            sendToServer(GET_GID, response.getData(), new NetResult("gid") {
                @Override
                public <T> void onSuccess(T t, String url) {
                    //UiUtil.toastLongTime("GET_GID：" + JsonUtils.toJson(t));
                    if (IS_DEBUG_BR) {
                        Logger.d("BrAgentUtils : 接口请求成功：" + JsonUtils.toJson(t));
                    }
                    if (t != null) {
                        BrBean bean = JsonUtils.fromJson(t, BrBean.class);
                        if (0 == bean.getCode()) {
                            if (bean.getData() != null && bean.getData().containsKey("gid")) {
                                //Logger.d("BrAgentUtils : data中包含gid");
                                String gid = bean.getData().get("gid");
                                String tokenId = bean.getData().get("token_id");
                                if (CheckUtil.isEmpty(gid)) {
                                    return;
                                }
                                BrAgent.brSaveGidAndTokenId(AppApplication.CONTEXT, gid, tokenId);
                                br_token_id = CheckUtil.isEmpty(tokenId) ? "00000000" : tokenId;
                                //Logger.d("BrAgentUtils : 解析数据完成保存gid成功");
                            }
                        }
                        //Logger.d("BrAgentUtils : 数据处理结束");
                    }
                    initParam.setGid(getSpHelperGid());
                    initParam.setToken_id(CheckUtil.isEmpty(br_token_id) ? "00000000" : br_token_id);
                    if (callback != null) {
                        callback.onSucced("", initParam);
                    }
                    if (IS_DEBUG_BR) {
                        Logger.d("BrAgentUtils : 初始化结束" + JsonUtils.toJson(initParam));
                    }
                }
            });
        } else {
            initParam.setGid(getSpHelperGid());
            initParam.setToken_id(br_token_id);
            if (callback != null) {
                callback.onSucced("", initParam);
            }
        }
    }

    /**
     * 获取百融gid
     */
    public static void getBrAgentGid(final BrAgentSucc agentSucc) {
        String gid = getSpHelperGid(true);
        //如果gid为空或为00099则重新调用初始化
        if (CheckUtil.isEmpty(gid) || "00099".equals(gid)) {
            initBrAgent(agentSucc);
        } else {
            //如果gid正常则直接返回缓存中的gid
            agentSucc.onSucced(gid, null);
        }
    }

    public static String getSpHelperGid() {
        return getSpHelperGid(false);
    }

    static Object getTokenId() {
        return br_token_id;
    }

    /**
     * 获取百融流水号
     */
    private static String getSwiftNumber(Object data) {
        if (CheckUtil.isEmpty(data)) {
            return "";
        }
        BrBean bean = JsonUtils.fromJson(data, BrBean.class);
        if (bean.getCode() == 0 && bean.getData() != null) {
            if (bean.getData() != null && bean.getData().containsKey("af_swift_number")) {
                return bean.getData().get("af_swift_number");
            }
        }
        return "";
    }

    /**
     * 获取缓存中的gid
     */
    private static String getSpHelperGid(boolean upLoad) {
        String gidObject = BrAgent.brGetDeviceGid(AppApplication.CONTEXT);
        if (gidObject == null) {
            return "00099";
        }
        if (CheckUtil.isEmpty(gidObject) && upLoad) {
            return "00099";
        }
        return gidObject;
    }

    /**
     * 百融额度申请
     */
    public static void lendInfoBrAgent(Context context, final BrAgentSucc callback) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_USERID)));
        try {
            onFraudBrAgent(context, BrEventType.BrEventTypeLend, map, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 百融借款
     */
    public static void cashInfoBrAgent(Context context, final BrAgentSucc callback) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_USERID)));
        try {
            onFraudBrAgent(context, BrEventType.BrEventTypeCash, map, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 缺省类型
     */
    public static void defaultBrAgent(Context context, int brType, final BrAgentSucc callback) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_USERID)));
        try {
            BrEventType type;
            if (1 == brType) {
                type = BrEventType.BrEventTypeRegister;
            } else if (2 == brType) {
                type = BrEventType.BrEventTypeLogin;
            } else if (3 == brType) {
                type = BrEventType.BrEventTypeLend;
            } else if (4 == brType) {
                type = BrEventType.BrEventTypeCash;
            } else {
                getBrAgentGid(callback);
                return;
            }
            onFraudBrAgent(context, type, map, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 百融注册
     */
    public static void registerBrAgent(Context context, final BrAgentSucc callback) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_USERID)));
        try {
            onFraudBrAgent(context, BrEventType.BrEventTypeRegister, map, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 百融业务处理<br/>
     * 注册事件（BrEventType.BrEventTypeRegister）<br/>
     * 登录事件（BrEventType.BrEventTypeLogin）<br/>
     * 借款事件（BrEventType.BrEventTypeLend）<br/>
     * 提现事件（BrEventType.BrEventTypeCash）<br/>
     * 还款事件（BrEventType.BrEventTypeRepay）<br/>
     * 营销-优惠券事件（BrEventType.BrEventTypeMkCoupon）<br/>
     * 营销-积分事件（BrEventType.BrEventTypeMkPoint）<br/>
     * 营销-里程事件（BrEventType.BrEventTypeMkMileage）<br/>
     * 营销-保险事件（BrEventType.BrEventTypeMkInsure）<br/>
     * 营销-实物事件（BrEventType.BrEventTypeMkArticle）<br/>
     */
    private static void onFraudBrAgent(final Context context, final BrEventType eventType, final Map<String, String> map, final BrAgentSucc callback) {
        getBrAgentGid((afSwiftNumber, brObject) -> {
            JSONObject pInfo = new JSONObject();
            for (String key : map.keySet()) {
                try {
                    pInfo.put(key, map.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (IS_DEBUG_BR) {
                Logger.d("onFraudBrAgent :" + eventType);
            }
            BrAgent.brGetEventParams(context, eventType, pInfo, brResponse -> {
                BrEventParam eventParam = new BrEventParam();
                eventParam.setUserId(SpHp.getLogin(SpKey.LOGIN_USERID));
                eventParam.setGid(getSpHelperGid());
                eventParam.setToken_id(br_token_id);
                if (IS_DEBUG_BR) {
                    Logger.d("onFraudBrAgent :BrAgent.brGetEventParams：" + brResponse.getCode() + "\n" + brResponse.getData());
                }
                if (0 == brResponse.getCode()) {
                    BrResponse response1 = BrAgent.brTransformJson(brResponse.getData(), true, true);
                    BrEventParam.BrDataBean dataBean = JsonUtils.fromJson(brResponse.getData(), BrEventParam.BrDataBean.class);
                    if (dataBean != null) {
                        BrEventParam.ParamBean paramBean = JsonUtils.fromJson(dataBean.getParam(), BrEventParam.ParamBean.class);
                        dataBean.setParam(paramBean);
                    }
                    eventParam.setBrData(dataBean);
                    if (response1.getCode() == 0) {
                        getAfSwiftNumber(response1.getData(), eventParam, callback);
                    }
                } else {
                    if (callback != null) {
                        eventParam.setAf_swift_number("APPRCERR000001");
                        callback.onSucced("APPRCERR000001", eventParam);
                    }
                }
            });
        });
    }

    private static void getAfSwiftNumber(String data, final BrEventParam eventParam, final BrAgentSucc callback) {
        sendToServer(EVENT_PUT, data, new NetResult("af_swift_number") {

            @Override
            public <T> void onSuccess(T t, String url) {
                //UiUtil.toastLongTime("EVENT_PUT：" + JsonUtils.toJson(t));
                if (IS_DEBUG_BR) {
                    Logger.d("getAfSwiftNumber :" + JsonUtils.toJson(t));
                }
                String id = getSwiftNumber(t);
                if (CheckUtil.isEmpty(id)) {
                    id = "APPRCERR000001";
                }
                if (eventParam != null) {
                    eventParam.setAf_swift_number(id);
                }
                if (callback != null) {
                    callback.onSucced(id, eventParam);
                }
            }
        });
    }


    /**
     * 登陆百融，进行信息获取
     */
    public static void logInBrAgent(final Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_USERID)));
        onFraudBrAgent(context, BrEventType.BrEventTypeLogin, map, (afSwiftNumber, brObject) -> {
            try {
                SpHp.saveSpOther(SpKey.OTHER_BR_LOGIN, afSwiftNumber);
                RiskInfoUtils.postBrOrBigData(context, "login", "", brObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public interface BrAgentSucc {
        void onSucced(String afSwiftNumber, Object brObject);
    }

    private static abstract class NetResult implements INetResult {
        String failMsg;

        NetResult(String failMsg) {
            this.failMsg = failMsg;
        }

        @Override
        public void onError(BasicResponse error, String url) {
        }
    }

    /**
     * 网络请求方法
     *
     * @param data 调用获取数据方法后 response 返回的 response.data 数据
     */
    private static void sendToServer(String url, String data, final NetResult result) {
        if (!SpHp.getOtherBoole(SpKey.OTHER_BR_SWITCH)) {
            result.onSuccess(null, null);
            return;
        }
        //因百融url会存在 https校验不通过，使用单独请求
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), data);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = new OkHttpClient.Builder()
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .writeTimeout(500, TimeUnit.MILLISECONDS)
                .connectTimeout(2000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .build()
                .newCall(request);
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                if (result != null) {
                    result.onError(new BasicResponse(NetConfig.NET_CODE_SOCKET_TIMEOUT, e.getMessage()), url);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (result != null && response.body() != null) {
                        result.onSuccess(response.body().string(), url);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
