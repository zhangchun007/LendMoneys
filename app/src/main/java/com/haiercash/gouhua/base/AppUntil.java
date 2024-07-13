package com.haiercash.gouhua.base;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.app.haiercash.base.interfaces.EventResultListener;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.retrofit.RetrofitFactory;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.appsafekb.safekeyboard.NKeyBoardTextField;
import com.geetest.onelogin.OneLoginHelper;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.SplashActivity;
import com.haiercash.gouhua.activity.login.LoginNetHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.receiver.XGUntil;
import com.haiercash.gouhua.uihelper.PrivacyProtocolPopupWindow;
import com.haiercash.gouhua.utils.BrAgentUtils;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.WyDeviceIdUtils;
import com.megvii.idcardlib.utils.CommonUtils;
import com.networkbench.agent.impl.NBSAppAgent;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.app.haiercash.base.net.config.NetConfig.application;

public class AppUntil {

    /**
     * 初始化APP 三方sdk
     */
    public static void initAPP(Application application, EventResultListener listener) {
        //用户同意了隐私协议政策
        SystemUtils.isAllow = isAllow();
        //logger
        Logger.initLogUntil(application, !BuildConfig.IS_RELEASE);
        registerOthers(application, listener);
    }

    /**
     * 第三方库注册
     */
    private static void registerOthers(Application application, EventResultListener listener) {
        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
        executorService.submit(() -> {
            try {
                ARouterUntil.initARouter(application);
                //设置线程的优先级；不与主线程抢资源
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                if (SystemUtils.isAllow) {
                    //听云
                    NBSAppAgent.setLicenseKey(CommonConfig.TY_APP_KEY)
                            .setRedirectHost(CommonConfig.TY_APP_HOST)
                            .setStartOption(16383)
                            .start(application.getApplicationContext());
                    NBSAppAgent.setUserIdentifier(EncryptUtil.simpleEncrypt(AppApplication.userid));

                    NBSAppAgent.setLogEnable(!BuildConfig.IS_RELEASE);//设置成 true，输出日志
                    NBSAppAgent.setCellCollectEnabled(false);//关闭基站数据采集

                    // 百融初始化
                    BrAgentUtils.initBrAgent((afSwiftNumber, brObject) -> RiskInfoUtils.postBrToBigData(brObject));
                    //QbSdk.initX5Environment(getApplicationContext(), null);
                    registerBugly(application);
                    //registerTalkingdata(application);
                    registerLeakCanary();
                    XGUntil.initPush(application, !BuildConfig.IS_RELEASE);
                    //友盟埋点初始化
                    UMengUtil.init();
                    //一键登录
                    initOneKeyLogin(application);

                    //网易设备指纹初始化
                    int initCode = WyDeviceIdUtils.getInstance().init(application);
                    Logger.e("WyDeviceIdUtils", "initcodeapp==" + initCode);
                    WyDeviceIdUtils.getInstance().setIntCode(initCode);
                    if (200 == initCode) {
                        WyDeviceIdUtils.getInstance().getWyDeviceIDToken((token, code, msg) -> Logger.e("WyDeviceIdUtils", "tokencode==" + "---code==" + code + "---" + token));
                    } else {
                        String errorMsg = WyDeviceIdUtils.getInstance().getInitExceptionMsg(initCode);
                        WyDeviceIdUtils.getInstance().postErrorMsgToServer(initCode, "网易设备指纹", "init()", errorMsg);
                    }

                }
                //爱加密键盘授权
                NKeyBoardTextField.setNlicenseKey(application.getString(R.string.authorize_code));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (listener != null) {
                listener.eventResult(null);
            }
        });
    }

    /**
     * 一键登录
     */
    private static void initOneKeyLogin(Application application) {
        /**
         * OneLogin 初始化
         */
        OneLoginHelper.with()
                //开启日志打印功能，Release 版本可以关闭
                .setLogEnable(false)
                /**
                 * 新的 init 接口第二个参数支持配置 APP_ID（将会把请求运营商配置提前到初始化阶段），register 接口无需重复配置，可传空字符串
                 * OneLogin 与 OnePass 属于不同的产品，注意产品 APPID 不可混用，请在后台分别申请对用的 APPID
                 */
                .init(application, CommonConfig.ONE_KEY_SECRET)
                .setRequestTimeout(8000, 8000)
                /**
                 * register 方法建议在应用启动或进入登录页的前一个页面、用户登出时时调用
                 * 1. 提前注册用于提前预取号，便于快速拉起授权页
                 * 2. 用户退出登录后，再次调用重新预取号，方便下次登录能迅速进入授权页
                 */
                .register(CommonConfig.ONE_KEY_SECRET);
    }

    /**
     * Bugly注册
     */
    private static void registerBugly(Application application) {
        CrashReport.initCrashReport(application, BuildConfig.IS_RELEASE ? CommonConfig.CRASHREPORT_RELEASE : CommonConfig.CRASHREPORT_DEBUG, true);
        //发版时放开日志屏蔽
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (!BuildConfig.IS_RELEASE) {
                Logger.e("程序发送崩溃，异常了开始输出异常信息");
                Logger.setStackDeep(application, 10);
                Logger.e(Log.getStackTraceString(e));
                Logger.file("ErrorMessage:" + e.getMessage() + "\n");
                Logger.setStackDeep(application, 1);
                Logger.e("---------------------------------------------------------------------------------");
            }
            String deviceId = "";
            try {
                deviceId = SystemUtils.getDeviceID(application);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            postError(Log.getStackTraceString(e), deviceId);
            CrashReport.postCatchedException(e);
            Intent intent = new Intent(application, SplashActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(intent);
        });
    }

    /**
     * 错误信息上送指定服务器
     * <p>
     * AppUntil.postErrorStr(Log.getStackTraceString(e), SystemUtils.getDeviceID(mActivity));
     */
    public static void postErrorStr(String errorMsg, String deviceId) {
        String url = "https://sysmonitor.haiercash.com/sysmonitor/printForAllStr";
        Map<String, Object> map1 = new HashMap<>();
        map1.put("logType", "AndCrashError");
        map1.put("userId", AppApplication.userid);
        map1.put("deviceMsg", NetConfig.initRequestHeader().toString() + "\nBRAND:" + Build.BRAND + "\nDeviceId:" + deviceId);
        map1.put("errorMsg", errorMsg);

        Map<String, Object> map = new HashMap<>();
        map.put("data", JsonUtils.toJson(map1));
        Logger.i(JsonUtils.toJson(map));
        RetrofitFactory.getInstance().getOthers(url, map, new INetResult() {
            @Override
            public <T> void onSuccess(T t, String url) {
                Logger.i("printForAllStr | crash 日志报送成功");
            }

            @Override
            public void onError(BasicResponse error, String url) {
                Logger.i("printForAllStr | crash 日志报送失败");
            }
        });
    }

    /**
     * 崩溃日志
     */
    public static void postError(String errorMsg, String deviceId) {
        try {
            String url = "https://sysmonitor.haiercash.com/sysmonitor/printForH5Obj";
            Map<String, Object> map = new HashMap<>();
            map.put("time", System.currentTimeMillis());
            map.put("logLevel", "Error");
            List<Object> logCenter = new ArrayList<>();
            Map<String, Object> map2 = new HashMap<>();
            map2.put("id", AppApplication.userid);
            map2.put("logType", "AndCrashError");
            map2.put("apiUrl", NetConfig.API_SERVER_URL);
            //map2.put("data", new HashMap<>());
            Map<String, Object> map3 = new HashMap<>();
            map3.put("retCode", "-1");
            map3.put("retMsg", errorMsg);
            map3.put("success", false);
            map2.put("message", map3);
            map2.put("errUrl", NetConfig.API_SERVER_URL);
            Map<String, Object> map4 = new HashMap<>();
            map4.put("isRelease", BuildConfig.IS_RELEASE);
            map4.put("channel", TokenHelper.getInstance().getSmyParameter("channelNo"));
            map4.put("userAgent", NetConfig.initRequestHeader().toString() + "\nBRAND:" + Build.BRAND + "\nDeviceId:" + deviceId);
            map2.put("props", map4);
            logCenter.add(map2);
            map.put("logCenter", logCenter);
            Logger.i(JsonUtils.toJson(map));
            RetrofitFactory.getInstance().getOthers(url, JsonUtils.toJson(map), new INetResult() {
                @Override
                public <T> void onSuccess(T t, String url) {
                    Logger.i("printForH5Obj | crash 日志报送成功");
                }

                @Override
                public void onError(BasicResponse error, String url) {
                    Logger.i("printForH5Obj | crash 日志报送失败");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求ip
     */
    public static void IpRequest() {
        try {
            Map<String, String> map = new HashMap<>();
            RetrofitFactory.getInstance().getOthers("https://www.taobao.com/help/getip.php", JsonUtils.toJson(map), new INetResult() {
                @Override
                public <T> void onSuccess(T t, String url) {
                    String ipCallback = t.toString();
                    if (!TextUtils.isEmpty(ipCallback)) {
                    }
                }

                @Override
                public void onError(BasicResponse error, String url) {
                }
            });

        } catch (Exception e) {

        }
    }

//    /**
//     * talkingData
//     */
//    public static void registerTalkingdata(Application application) {
//        String tdAppId = BuildConfig.IS_RELEASE ? CommonConfig.TDAPPID_RELEASE : CommonConfig.TDAPPID_DEBUG;
//        String tdChannelId = SystemUtils.metaDataValueForTDChannelId(application);
//    }

    /**
     * 检测内存泄漏问题
     */
    private static void registerLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
    }

    /**
     * 是否获得用户授权
     */
    public static boolean isAllow() {
        return "Y".equals(SpHp.getOther(SpKey.OTHER_PRIVACY, "N"));
    }

    public static boolean checkIsFirstInstall(){
        PackageManager packageManager = application.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(AppApplication.CONTEXT.getPackageName(), 0);
            int currentCode = packageInfo.versionCode;
            String lastCode = SpHp.getOther(SpKey.OTHER_FIRST_ENTER, "0");
            if (currentCode > Integer.parseInt(lastCode)) {
                SpHp.saveSpOther(SpKey.OTHER_FIRST_ENTER, String.valueOf(currentCode));
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 是否为游客模式
     */
    public static boolean isTourist() {
        return "Y".equals(SpHp.getOther(SpKey.OTHER_TOURIST_MODE, "N"));
    }

    public static boolean touristIntercept(View viewRoot,BaseActivity activity){
        if (!isTourist()) {
            return false;
        }

        PrivacyProtocolPopupWindow popupWindow = new PrivacyProtocolPopupWindow(activity, LoginNetHelper.getDefaultAgreementList());
        popupWindow.setRetainContent("同意","继续浏览");
        popupWindow.showAtLocation(viewRoot, (view, flagTag, obj) -> {
            if(PrivacyProtocolPopupWindow.BUTTON_AGREE_FLAG == flagTag){
                AppUntil.initAPP(activity.getApplication(), null);
                SpHp.saveSpOther(SpKey.OTHER_PRIVACY, "Y");
                SpHp.saveSpOther(SpKey.OTHER_TOURIST_MODE, "N");
                Intent intent = new Intent(application, SplashActivity.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                application.startActivity(intent);
            } else if(PrivacyProtocolPopupWindow.BUTTON_DISAGREE_FLAG == flagTag){
                SpHp.saveSpOther(SpKey.OTHER_TOURIST_MODE, "Y");
            }
        });
        return true;
    }
}
