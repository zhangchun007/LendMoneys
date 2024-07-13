package com.haiercash.gouhua.utils;


import android.content.Context;
import android.text.TextUtils;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.interfaces.WyTokenCallback;
import com.haiercash.gouhua.network.NetHelper;
import com.netease.mobsec.xs.NTESCSConfig;
import com.netease.mobsec.xs.NTESCSDevice;

import java.util.HashMap;

/**
 * @Description: 获取网易设备指纹ID
 * https://support.dun.163.com/documents/609099986339037184?docId=609101009640161280
 * @Author: zhangchun
 * @CreateDate: 2/2/23
 * @Version: 1.0
 */
public class WyDeviceIdUtils implements INetResult {

    protected NetHelper netHelper;

    /**
     * 初始化结果
     */
    private int intCode;

    //构造函数私有
    private WyDeviceIdUtils() {
    }

    public static WyDeviceIdUtils getInstance() {
        return WyDeviceIdUtils.SingletonHolder.mInstance;
    }


    /**
     * 静态内部类
     */
    private static class SingletonHolder {
        private static final WyDeviceIdUtils mInstance = new WyDeviceIdUtils();
    }

    /**
     * 网易设备指纹初始化，建议在同意隐私权限之后初始化
     *
     * @param context
     */
    public int init(Context context) {
        netHelper = new NetHelper(this);
        //以下配置仅针对私有化场景，使用saas版本时不需要配置
        NTESCSConfig config = new NTESCSConfig();
        String url = BuildConfig.IS_RELEASE ? "https://fgp-upload.haiercash.com/v1/ad/d" : "https://fgp-upload-test.haiercash.com/v1/ad/d";
        config.setUrl(url);
        config.setCollectWifiInfo(true);
        String channel = SystemUtils.metaDataValueForTDChannelId(context);
        return NTESCSDevice.get().initV2(context, CommonConfig.WY_DEVICE_INFO_KEY, channel, config);
    }


    /**
     * 从获取网易设备指纹 token
     */
    public void getWyDeviceIDToken(WyTokenCallback callback) {
        NTESCSDevice.get().getToken(result -> {
            if (result == null) {
                if (callback != null)
                    callback.onResult("", -1, "未知错误");
                return;
            }
            //callback会返回在主线程中
            if (result.getCode() == 200 || result.getCode() == 201) {
                //获取token
                if (!TextUtils.isEmpty(result.getToken())) {
                    if (callback != null)
                        callback.onResult(result.getToken(), result.getCode(), "");
                    //保存当前token跟时间戳
                    SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.WY_DEVICEID_TOKEN, result.getToken());
                    SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.WY_DEVICEID_TOKEN_TIME, TimeUtil.calendarToString());
                }else {
                    if (callback != null)
                        callback.onResult("", result.getCode(), "未知错误");
                }
            } else {
                //上传失败信息
                String msg = getTokenExceptionMsg(result.getCode());
                int code = result.getCode();
                postErrorMsgToServer(code, "网易设备指纹", "getToken()", msg);
                if (callback != null)
                    callback.onResult(result.getToken(), result.getCode(), getTokenExceptionMsg(result.getCode()));

            }
        });
    }

    /**
     * 从本地获取网易设备指纹 token
     */
    public void getWyDeviceIDTokenFromNative(Context context, WyTokenCallback callback) {
        String token = SpHelper.getInstance().readMsgFromSp(SpKey.OTHER, SpKey.WY_DEVICEID_TOKEN, "");
        if (isOver20Min() || TextUtils.isEmpty(token)) {//超过20分钟刷新token & 或者本地token为空时
            if (200 == getIntCode()) {//如果初始化成功
                getWyDeviceIDToken(callback);
            } else {//若启动时初始化失败，则在风险数据上送节点重新初始化
                int initCodeFromServer = init(context);
                if (200 == initCodeFromServer) {
                    getWyDeviceIDToken(callback);
                } else {
                    //初始化失败获取token直接返回null
                    if (callback != null)
                        callback.onResult("", initCodeFromServer, getInitExceptionMsg(initCodeFromServer));

                    //再次上报初始化失败数据
                    WyDeviceIdUtils.getInstance().postErrorMsgToServer(initCodeFromServer, "网易设备指纹", "init()", getInitExceptionMsg(initCodeFromServer));
                }
            }
        } else {//从本地获取
            if (callback != null)
                callback.onResult(token, 200, "");

        }
    }

    /**
     * token本地时间是否未超过20分钟
     *
     * @return
     */
    private boolean isOver20Min() {
        String currentTime = TimeUtil.calendarToString();
        String lastTime = SpHelper.getInstance().readMsgFromSp(SpKey.OTHER, SpKey.WY_DEVICEID_TOKEN_TIME);
        return CheckUtil.isEmpty(lastTime) || TimeUtil.isOver20Min(lastTime, currentTime);
    }

    /**
     * 初始化根据code获取失败的信息
     *
     * @param code
     * @return
     */
    public String getInitExceptionMsg(int code) {
        String msg = "未知错误";
        switch (code) {
            case 1007:
                msg = "SDK初始化参数校验异常，如Context为空或者appId格式异常";
                break;
            case 1008:
                msg = "SDK当前初始化在非主进程内，必须要在主进程内执行初始化";
                break;
            case 1009:
                msg = "SDK私有化URL地址校验失败，必须以http://或者https://开头";
                break;
            case 1010:
                msg = "SDK初始化加载SO文件异常，请确保已正常集成SDK或者ABI过滤正确";
                break;
            case 1011:
                msg = "SDK初始化函数加载异常，请确保配置正确";
                break;
        }
        return msg;
    }

    /**
     * 根据code获取token失败的信息
     *
     * @param code
     * @return
     */
    public String getTokenExceptionMsg(int code) {
        String msg = "未知错误";
        switch (code) {
            case 1000:
                msg = "本地采集数据异常";
                break;
            case 1002:
                msg = "服务端返回数据解析异常";
                break;
            case 1003:
                msg = "SDK未初始化或初始化失败";
                break;
            case 1005:
                msg = "服务端返回数据校验异常";
                break;
            case 1006:
                msg = "同步getToken模式运行在主线程错误码";
                break;
        }
        return msg;
    }


    @Override
    public <T> void onSuccess(T t, String url) {

    }

    @Override
    public void onError(BasicResponse error, String url) {

    }

    /**
     * 上报异常数据
     *
     * @param code        错误码
     * @param type        类型（哪个sdk）
     * @param errorMethod 问题方法
     * @param msg         详细信息
     */
    public void postErrorMsgToServer(int code, String type, String errorMethod, String msg) {
        if (netHelper != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("code", code + "");
            map.put("type", type);
            map.put("errorMethod", errorMethod);
            map.put("msg", code + ":" + msg);
            netHelper.postService(ApiUrl.POST_APP_ACTION_LOG, map);
        }

    }

    /**
     * 设置初始化结果
     *
     * @param intCode
     */
    public void setIntCode(int intCode) {
        this.intCode = intCode;
    }

    /**
     * 获取初始化结果
     *
     * @return
     */
    public int getIntCode() {
        return intCode;
    }
}


