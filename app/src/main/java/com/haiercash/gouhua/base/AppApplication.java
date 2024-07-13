package com.haiercash.gouhua.base;

import android.content.Context;
import android.text.TextUtils;

import com.app.haiercash.base.BaseApplication;
import com.app.haiercash.base.CommonManager;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.activity.login.OneKeyLoginUtils;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.interfaces.LoginCallback;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.receiver.XGUntil;
import com.haiercash.gouhua.utils.BrAgentUtils;
import com.haiercash.gouhua.utils.ColdActivityLifecycleCallbacks;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.RiskKfaUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.x5webview.WebDataImpl;
import com.tencent.android.tpush.XGPushConfig;

/**
 * 项目名称：
 * 项目作者：胡玉君
 * 创建日期：2017/4/11 18:27.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class AppApplication extends BaseApplication {
    public static String userid;
    public static boolean enableCredit;  //申额开关
    public static boolean enableLoan;  //支用
    public static boolean enableRepay = true;  //还款

    private static volatile LoginCallback loginCallback;//用于登录结果回调逻辑

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //配置 信鸽推送SDK 的 Provider 组件功能是否自动初始化，默认开启
        XGPushConfig.setAutoInit(AppUntil.isAllow());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //其他进程启动的过程不做任何操作
        if (SystemUtils.getPackageName(this).equals(SystemUtils.getCurrentProcessNameByFile())) {
            CONTEXT = getApplicationContext();
            //registerReceiver(new BatteryReceiver(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            CommonManager.registerCommon(this, !BuildConfig.IS_RELEASE);
            //友盟埋点预初始化
            UMengUtil.preInit(!BuildConfig.IS_RELEASE);
            if (!BuildConfig.IS_RELEASE) {
                String saveUrl = SpHp.getOther(SpKey.OTHER_CURRENT_URL, "");
                NetConfig.API_SERVER_URL = TextUtils.isEmpty(saveUrl) ? BuildConfig.API_SERVER_URL : saveUrl;
            } else {
                NetConfig.API_SERVER_URL = BuildConfig.API_SERVER_URL;
            }

            if (TextUtils.isEmpty(SpHelper.getInstance().readMsgFromSp(SpKey.TEMP, SpKey.TEMP_KEY))) {
                CommomUtils.clearSp();
                SpHelper.getInstance().saveMsgToSp(SpKey.TEMP, SpKey.TEMP_KEY, "Y");
            }
            userid = SpHp.getLogin(SpKey.LOGIN_USERID);
            AppUntil.initAPP(this, null);
            registerActivityLifecycleCallbacks(new ColdActivityLifecycleCallbacks());
            Logger.e("AppApplication：API_URL-> " + NetConfig.API_SERVER_URL + "\n网络接口日志：" + NetConfig.IS_DEBUG_NET + "\n风控KFARISK：" + RiskKfaUtils.IS_DEBUG_RISK + "\n百融DEBUG：" + BrAgentUtils.IS_DEBUG_BR + "\n友盟DEBUG：" + UMengUtil.IS_BURY_DEBUG + "\n信鸽DEBUG：" + XGUntil.IS_DEBUG_XG + "\n网页相关DEBUG：" + WebDataImpl.IS_DEBUG_WEB);
        }
    }

    /**
     * 是否登录
     *
     * @return true 登录 false 未登录
     */
    public static boolean isLogIn() {
        String logState = SpHelper.getInstance().readMsgFromSp(SpKey.STATE, SpKey.STATE_LOGINSTATE);
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        String loginMobile = SpHp.getLogin(SpKey.LOGIN_MOBILE);
        return "Y".equals(logState) && !CheckUtil.isEmpty(userId) && !CheckUtil.isEmpty(loginMobile);
    }

    /**
     * 灵活处理登录回调
     */
    public static synchronized void doLoginCallback() {
        if (AppApplication.loginCallback != null) {
            if (AppApplication.isLogIn()) {
                AppApplication.loginCallback.onLoginSuccess();
                AppApplication.loginCallback = null;
            } else if (!LoginSelectHelper.hasActiveLoginPage()) {//最后一个登录页面销毁后才执行
                AppApplication.loginCallback.onLoginCancel();
                AppApplication.loginCallback = null;
            }
        }
    }

    /**
     * 设置登录回调
     *
     * @param loginCallback action
     */
    public static void setLoginCallback(LoginCallback loginCallback) {
        AppApplication.loginCallback = loginCallback;
    }

    /**
     * 设置登录条件处理
     * RequestCode
     *
     * @param loginCallback action
     */
    public static void setLoginCallbackTodo(LoginCallback loginCallback) {
        if (isLogIn()) {
            if (loginCallback != null) {
                loginCallback.onLoginSuccess();
            }
        } else {
            AppApplication.loginCallback = loginCallback;
            LoginSelectHelper.staticToGeneralLogin();
        }
    }

    /**
     * 设置登录条件处理
     * RequestCode
     *
     * @param loginCallback action
     */
    public static void setLoginCallbackTodo(boolean needLogin, LoginCallback loginCallback) {
        if (needLogin) {
            if (isLogIn()) {
                if (loginCallback != null) {
                    loginCallback.onLoginSuccess();
                }
            } else {
                AppApplication.loginCallback = loginCallback;
                LoginSelectHelper.staticToGeneralLogin();
            }
        } else {
            loginCallback.onLoginSuccess();
        }
    }

    /**
     * 登陆成功后关闭相关页面
     */
    public static void loginSuccessToDo() {
        //先判断，因为AppApplication.loginCallback在操作后可能变为空
        boolean backToMain = AppApplication.loginCallback == null;
        //需要关掉所有与登录有关的页面，此时不一定只有首页和登录页----start
        //关闭一键登录页面
        OneKeyLoginUtils.dismissOneKeyLoginPage();
        ActivityUntil.finishActivity(LoginSelectHelper.getLoginActivityPages());
        //需要关掉所有与登录有关的页面，此时不一定只有首页和登录页----end

        if (backToMain) {
            MainHelper.backToMain();
        }
        RxBus.getInstance().post(new ActionEvent(ActionEvent.REFRESHUSERINFO));
    }
}