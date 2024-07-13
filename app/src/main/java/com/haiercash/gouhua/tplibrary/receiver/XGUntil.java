package com.haiercash.gouhua.tplibrary.receiver;

import android.content.Context;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/6/7<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class XGUntil {
    public static final boolean IS_DEBUG_XG = false;
    private static final String TAG = "XGUntil";

    public static void initPush(Context context, boolean isDebug) {
        XGPushConfig.enableDebug(context, isDebug);
        if (isDebug) {
            XGPushConfig.setAccessId(context, 1500013847);
            XGPushConfig.setAccessKey(context, "A9IP8YZSMPJP");
        } else {//生产
            XGPushConfig.setAccessId(context, 1500013843);
            XGPushConfig.setAccessKey(context, "AFYXCNM1ZF6K");
        }
        XGPushConfig.setMiPushAppId(context, "2882303761517690474");
        XGPushConfig.setMiPushAppKey(context, "5721769048474");
        XGPushConfig.setMzPushAppId(context, "117186");
        XGPushConfig.setMzPushAppKey(context, "1053b9c7228b4e1798c3b897be54a415");
        // 注意这里填入的是 Oppo 的 AppKey，不是AppId
        XGPushConfig.setOppoPushAppId(context, "b7rY62Suiw8o4GSwSO88gsGkw");
        // 注意这里填入的是 Oppo 的 AppSecret，不是 AppKey
        XGPushConfig.setOppoPushAppKey(context, "431CF48397B8C630d18DacFf382Fc728");
        XGPushConfig.enableOtherPush(context, true);
    }

    /**
     * 注册信鸽+绑定账号 并且上送设备账号
     */
    public static void registerPushAndAccount(Context context, String uid) {
        Logger.e("开始注册信鸽"); //UI要求删除该toast
        try {
            XGPushManager.registerPush(context, callback);

            // 获取token
            NetHelper netHelper = new NetHelper();
            Map<String, String> map = new HashMap<>();
            map.put("userId", RSAUtils.encryptByRSA(uid));
            map.put("mobileNo", RSAUtils.encryptByRSA(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
            map.put("certNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CERTNO)));
            map.put("mobileType", SystemUtils.getDeviceModel());
            map.put("deviceType", "AND");
            map.put("isRsa", "Y");
            netHelper.postService(ApiUrl.URL_MESSAGE_DEVICE, map);
            //UiUtil.toast("XGUntil信鸽注册+上送设备信息");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static XGIOperateCallback callback = new XGIOperateCallback() {
        @Override
        public void onSuccess(Object o, int i) {

            if (!CheckUtil.isEmpty(AppApplication.userid)) {
                XGPushManager.bindAccount(AppApplication.CONTEXT, AppApplication.userid);
            }
            if (IS_DEBUG_XG) {
                Logger.d(TAG, "注册成功，设备token为：" + o);
            }
        }

        @Override
        public void onFail(Object data, int errCode, String msg) {
            if (IS_DEBUG_XG) {
                Logger.d(TAG, "注册失败，错误码：" + errCode + ",错误信息：" + msg + "\n" + data);
            }
        }
    };

    public static void unRegisterPushAndAccount(Context context, XGIOperateCallback callback) {
        XGPushManager.unregisterPush(context, callback);
    }
}
