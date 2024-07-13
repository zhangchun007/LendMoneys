package com.haiercash.gouhua.activity.login;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.permission.PermissionUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.geetest.onelogin.activity.OneLoginActivity;
import com.geetest.onelogin.activity.OneLoginWebActivity;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.activity.edu.PerfectInfoHelper;
import com.haiercash.gouhua.activity.personalinfor.SocialBindActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseDialog;
import com.haiercash.gouhua.beans.login.LastLoginTypeBean;
import com.haiercash.gouhua.interfaces.ICallBack;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.wxapi.WXEntryActivity;
import com.haiercash.gouhua.wxapi.WxUntil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录选择逻辑帮助类
 */
public class LoginSelectHelper implements INetResult, OneKeyLoginListener {

    private final Bundle mExtras;//传给登录页面的

    private LoginSelectHelper(Bundle mExtras) {
        this.mExtras = mExtras;
    }

    public static void staticToGeneralLogin() {
        staticToGeneralLogin(null, null);
    }

    public static void staticToGeneralLogin(Bundle argc) {
        staticToGeneralLogin(argc, null);
    }

    public static void staticToGeneralLogin(Bundle argc, String loginWay) {
        staticToGeneralLogin(null, argc, loginWay);
    }

    /**
     * 调起常规登录流程，由RegisterLoginActivity内调起
     *
     * @param activity 调起时activity
     * @param argc     登录页面用的参数
     * @param loginWay 携带登录方式 sms password oneKey
     */
    public static void staticToGeneralLogin(BaseActivity activity, Bundle argc, String loginWay) {
        //多个登录方式，从上一个登录activity获取参数
        LoginSelectHelper loginSelectHelper = new LoginSelectHelper(argc == null && activity != null && activity.getIntent() != null ? activity.getIntent().getExtras() : argc);
        if (!CheckUtil.isEmpty(loginWay)) {
            switch (loginWay) {
                case "sms":
                    loginSelectHelper.smsLogin("", false);
                    break;
                case "password":
                    loginSelectHelper.passwordLogin("");
                    break;
                case "oneKey":
                    loginSelectHelper.goOneLogin();
                    break;
            }
            return;
        }
        loginSelectHelper.toGeneralLogin();
    }

    public static void staticToSwitchLogin(BaseActivity activity, Object bean) {
        //多个登录方式，从上一个登录activity获取参数
        LoginSelectHelper loginSelectHelper = new LoginSelectHelper(activity != null && activity.getIntent() != null ? activity.getIntent().getExtras() : null);
        if (bean instanceof LastLoginTypeBean) {
            if ("password".equals(((LastLoginTypeBean) bean).getLoginMethod())) {
                loginSelectHelper.passwordLogin(LoginSelectHelper.getLastLoginSuccessMobile());
            } else {
                loginSelectHelper.goOneLogin();
            }
        } else {
            loginSelectHelper.goOneLogin();
        }
    }

    /**
     * 关闭除了MainActivity的其他所有activity，并重新启动RegisterLoginActivity（登录）<br/>
     * 如果没有首页则先创建首页
     *
     * @param activity 当前Activity
     */
    public static void closeExceptMainAndToLogin(BaseActivity activity) {
        ActivityUntil.finishOthersActivity(MainActivity.class);
        if (ActivityUntil.findActivity(MainActivity.class) == null) {
            activity.startActivity(new Intent(activity, MainActivity.class));
        }
        LoginSelectHelper.staticToGeneralLogin();
    }

    private static Activity getActivity() {
        return ActivityUntil.getTopActivity();
    }

    private static BaseActivity getBaseActivity() {
        //触发登录时必定有BaseActivity
        for (int i = ActivityUntil.getActivitySize() - 1; i >= 0; i--) {
            Activity activity = ActivityUntil.getActivityList().get(i);
            if (activity instanceof BaseActivity && !activity.isFinishing()) {
                return (BaseActivity) activity;
            }
        }
        return ActivityUntil.findActivity(MainActivity.class);
    }

    /**
     * 与登录有关的页面
     */
    public static List<Class> getLoginActivityPages() {
        return Arrays.asList(WXEntryActivity.class, SocialBindActivity.class,
                OneLoginActivity.class, OneLoginWebActivity.class,
                SmsWayLoginActivity.class, PasswordWayLoginActivity.class, SmsCodeActivity.class);
    }

    /**
     * 与登录有关的页面
     */
    public static List<Class> getLoginWayActivityPages() {
        return Arrays.asList(SmsWayLoginActivity.class, PasswordWayLoginActivity.class, SmsCodeActivity.class);
    }


    /**
     * 是否还有存活的登录页面，确保最后一个销毁时做操作
     */
    public static boolean hasActiveLoginPage() {
        return ActivityUntil.findActivity(LoginSelectHelper.getLoginActivityPages());
    }

    //如果是因为单点登录被下线，需要展示原因
    public static void logoutReasonDialog(Activity activity, Bundle extras) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        String reason = null;
        String key = "reason";
        if (extras != null && extras.containsKey(key)) {//extras优先级最高
            reason = extras.getString(key);
            extras.remove(key);
        } else if (activity.getIntent() != null && activity.getIntent().hasExtra(key)) {
            reason = activity.getIntent().getStringExtra(key);
            activity.getIntent().removeExtra(key);//只展示一次
        }
        if (!TextUtils.isEmpty(reason)) {
            if (!NetConfig.TOKEN_INVALID.equals(reason)) {
                BaseDialog.getDialog(activity, "提示", reason, "我知道了", null).setButtonTextColor(1, R.color.colorPrimary).show();
            }
            ActivityUntil.finishOthersActivity(LoginSelectHelper.getLoginActivityPages(), MainActivity.class);
        }
    }

    /**
     * @return 登录用户是否已在本机开启指纹识别
     */
    public static boolean hasSetBiometric() {
        return "Y".equals(SpHelper.getInstance().readMsgFromSp(SpHp.getOther(SpKey.LAST_LOGIN_SUCCESS_USERID), SpKey.STATE_HAS_BIOMETRIC));
    }

    /**
     * 保存登录用户已在本机开启指纹识别的状态
     */
    public static void saveBiometricOpenState() {
        saveBiometricOpenState(true);
    }

    public static void saveBiometricOpenState(boolean open) {
        SpHelper.getInstance().saveMsgToSp(SpHp.getOther(SpKey.LAST_LOGIN_SUCCESS_USERID), SpKey.STATE_HAS_BIOMETRIC, open ? "Y" : "N");
    }

    /**
     * @return 是否展示手势轨迹
     */
    public static boolean showGestureWay() {
        //默认展示
        return !"N".equals(SpHelper.getInstance().readMsgFromSp(SpKey.STATE, SpKey.STATE_SHOW_GESTURE_WAY));
    }

    /**
     * 保存手势轨迹开启状态
     */
    public static void saveGestureWayOpenState(boolean open) {
        SpHelper.getInstance().saveMsgToSp(SpKey.STATE, SpKey.STATE_SHOW_GESTURE_WAY, open ? "Y" : "N");
    }

    /**
     * @return 登录用户是否已开启手势密码
     */
    public static boolean hasSetGesture() {
        return "Y".equals(SpHelper.getInstance().readMsgFromSp(SpKey.STATE, SpKey.STATE_HASGESTURESPAS));
    }

    /**
     * 保存登录用户已开启手势密码的状态
     */
    public static void saveGestureOpenState() {
        saveGestureOpenState(true);
    }

    /**
     * 保存登录用户手势密码开启状态
     */
    public static void saveGestureOpenState(boolean open) {
        SpHelper.getInstance().saveMsgToSp(SpKey.STATE, SpKey.STATE_HASGESTURESPAS, open ? "Y" : "N");
        if (!open) {
            SpHelper.getInstance().deleteMsgFromSp(SpKey.STATE, SpKey.STATE_SHOW_GESTURE_WAY);
        }
    }

    /**
     * @return 本机最近一次登录成功的手机号
     */
    public static String getLastLoginSuccessMobile() {
        return SpHp.getOther(SpKey.LAST_LOGIN_SUCCESS_MOBILE);
    }

    /**
     * 跳转常规登录方式
     * 有上次登录成功的手机号，那么去调接口查询最近一次登录的登录方式，
     * 如果是账号密码登录，那么跳转账号密码登录；
     * 如果不是账号密码登录或者无数据，那么判断一键登录预取号是否成功，成功则跳转一键登录，否则跳转验证码登录
     */
    private synchronized void toGeneralLogin() {
        Activity currentAct = getActivity();
        if (currentAct == null) {//防止登录半弹窗页没有背景页面
            ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).navigation();
        } else if (getLoginActivityPages().contains(currentAct.getClass())) {
            return;//是否已经在登录页面
        }
        String lastLoginSuccessUserId = SpHp.getOther(SpKey.LAST_LOGIN_SUCCESS_USERID);
        if (TextUtils.isEmpty(lastLoginSuccessUserId)) {
            goOneLogin();
            return;
        }
        Intent intent = new Intent(AppApplication.CONTEXT, LoginLoadingActivity.class);
        if (mExtras != null) {
            intent.putExtras(mExtras);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppApplication.CONTEXT.startActivity(intent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSuccess(Object t, String url) {
        if (url.equals(ApiUrl.POST_CHECK_HAS_WECHAT_LOGIN)) {
            Map<String, String> map = (Map<String, String>) t;
            if (map != null && !TextUtils.isEmpty(map.get("existState"))) {
                if ("Y".equals(map.get("existState"))) {
                    sendWxAuthForLogin();
                } else {
                    showConfirmDialog();
                    SpHelper.getInstance().saveMsgToSp(SpKey.WECHAT_LOGIN_DIALOG_STATE, SpKey.HAS_SHOW_WECHAT_LOGIN_DIALOG, "Y");
                }
            } else {
                showConfirmDialog();
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
    }

    private void showConfirmDialog() {
        OneKeyLoginUtils.getInstance().showPrivacyDialog(getActivity(), ProtocolActivity.TYPE_WECHAT_PROTOCOL, "提示", getActivity().getString(R.string.wechat_info), getActivity().getIntent().getExtras());
    }

    /**
     * 一键登录流程
     */
    public void goOneLogin() {
        OneKeyLoginUtils.getInstance().login(getActivity(), this);
    }

    @Override
    public void onAuthActivityCreate(Activity activity) {
        LoginSelectHelper.logoutReasonDialog(activity, mExtras);
    }

    @Override
    public void onLoginButtonClick(boolean isChecked, String process_id, String token, String authCode) {
        if (isChecked) {
            //调用一键登录接口
            LoginNetHelper loginNetHelper = new LoginNetHelper(getBaseActivity(), LoginNetHelper.ONE_KEY_LOGIN, null, null);
            if (mExtras != null) {
                loginNetHelper.setWebUrl(mExtras.getString("webUrl")).setWebDoType("webDoType");
            }
            loginNetHelper.onKeyLogin(process_id, token, authCode);
        } else {
            OneKeyLoginUtils.getInstance().showPrivacyDialog(getActivity(), ProtocolActivity.TYPE_PROTOCOL, "服务协议及隐私保护", "", null);
        }
    }

    @Override
    public void onPrivacyClick(String name, String url) {
        if (!CheckUtil.isEmpty(url)) {
            WebSimpleFragment.WebService(getActivity(), url, name);
        }
    }

    @Override
    public void oneKeyLoginError() {
        Logger.e("oneKeyLoginError");
        smsLogin("", false);
    }

    @Override
    public void thirdLogin(String type) {
        if ("wechat".equals(type)) { //微信登录
            weChatLogin();
        } else if ("phone".equals(type)) {//手机号登录
            String mobile = getLastLoginSuccessMobile();
            smsLogin(mobile, true);
        }

    }

    //短信登录
    private void smsLogin(String mobile, boolean isFromOneKeyLogin) {
        Intent intent = new Intent();
        if (mExtras != null) {
            intent.putExtras(mExtras);
        }
        if (!CheckUtil.isEmpty(mobile) && CheckUtil.checkPhone(PerfectInfoHelper.phone_pattern, mobile)) {
            intent.putExtra("mobile", mobile);
        }
        intent.putExtra("isFromOneKeyLogin", isFromOneKeyLogin);
        intent.putExtra("needShowBack", false);

        SmsWayLoginActivity.startDialogActivity(ActivityUntil.getTopActivity(), SmsWayLoginActivity.class, SmsWayLoginActivity.ANIM_BOTTOM_IN_RIGHT_OUT, intent);
        RxBus.getInstance().post(new ActionEvent(ActionEvent.GO_SMS_LOGIN_WAY, intent));
        if (null != ActivityUntil.findActivity(PasswordWayLoginActivity.class)) {
            List<Class> list = new ArrayList<>();
            list.add(PasswordWayLoginActivity.class);
            ActivityUntil.finishActivity(list);
        }
    }

    //短信登录
    private void passwordLogin(String mobile) {
        Intent intent = new Intent();
        if (mExtras != null) {
            intent.putExtras(mExtras);
        }
        if (CheckUtil.checkPhone(PerfectInfoHelper.phone_pattern, mobile)) {
            intent.putExtra("mobile", mobile);
        }
        PasswordWayLoginActivity.startDialogActivity(getActivity(), PasswordWayLoginActivity.class, PasswordWayLoginActivity.ANIM_BOTTOM_IN_RIGHT_OUT, intent);
    }


    private void weChatLogin() {
        //微信登录
        if (!WxUntil.isReady(getActivity())) {
            UiUtil.toast("请先安装微信应用");
            return;
        }
        if (CheckUtil.isEmpty(SpHelper.getInstance().readMsgFromSp(SpKey.WECHAT_LOGIN_DIALOG_STATE, SpKey.HAS_SHOW_WECHAT_LOGIN_DIALOG))) {
            Map<String, String> map = new HashMap<>();
            String deviceId = SystemUtils.getDeviceID(getActivity());
            if (!CheckUtil.isEmpty(deviceId)) {
                map.put("deviceId", RSAUtils.encryptByRSA(deviceId));
            }
            new NetHelper(getActivity(), this).postService(ApiUrl.POST_CHECK_HAS_WECHAT_LOGIN, map);
        } else {
            sendWxAuthForLogin();
        }
    }

    //调起微信授权验证
    private void sendWxAuthForLogin() {
//        if (CommomUtils.noNeedPhoneStateDialog()) {
//            WxUntil.sendAuthForLogin(getActivity(), getActivity().getIntent().getExtras());
//        } else {
//            noticePermission(CALLBACK_WX_LOGIN_PERMISSION, new Callback(), R.string.permission_phone_statue, 2000, Manifest.permission.READ_PHONE_STATE);
//        }
        WxUntil.sendAuthForLogin(getActivity(), getActivity().getIntent().getExtras());
    }

    private static final String CALLBACK_WX_LOGIN_PERMISSION = "wxLoginPermission";

    private static class Callback implements ICallBack {

        @Override
        public void back(Object obj) {
            if (CALLBACK_WX_LOGIN_PERMISSION.equals(obj)) {
                WxUntil.sendAuthForLogin(getActivity(), getActivity().getIntent().getExtras());
            }
        }
    }

    private BaseDialog mDialog;

    private void initDialog(CharSequence title, CharSequence msg, CharSequence btn1, CharSequence btn2, DialogInterface.OnClickListener listener) {
        if (mDialog == null) {
            mDialog = BaseDialog.getDialog(getActivity(), title, msg, btn1, btn2, listener);
        } else {
            mDialog.setTitle(title);
            mDialog.setMessage(msg);
            mDialog.setButton1(btn1, null);
            mDialog.setButton2(btn2, null);
            mDialog.setOnClickListener(listener);
        }
    }

    public BaseDialog showDialog(CharSequence title, CharSequence msg, CharSequence btn1, CharSequence btn2, DialogInterface.OnClickListener listener) {
        initDialog(title, msg, btn1, btn2, listener);
        mDialog.show();
        return mDialog;
    }

    /**
     * 权限申请提示
     */
    public void noticePermission(String callbackObject, ICallBack iCallBack, int notice, int requestCode, String... strings) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !PermissionUtils.getRequestPermission(getActivity(), strings)) {
            if (notice <= 0) {
                getActivity().requestPermissions(strings, requestCode);
                for (String permission : strings) {
                    SpHelper.getInstance().saveMsgToSp(SpKey.PERMISSION_STATE, permission, "Y"); //标识曾经申请过权限
                    Logger.e("PERMISSION_STATE--01");
                }
                return;
            }
            if (notice == R.string.permission_phone_statue) {
                SpHelper.getInstance().saveMsgToSp(SpKey.READ_PHONE_STATE, SpKey.LAST_SHOW_DIALOG_TIME, TimeUtil.calendarToString());
            }
            showDialog("温馨提示", AppApplication.CONTEXT.getString(notice), "拒绝", "开启", (dialog, which) -> {
                dialog.dismiss();
                if (which == 2) {
                    //如果同意
                    getActivity().requestPermissions(strings, requestCode);
                    for (String permission : strings) {
                        SpHelper.getInstance().saveMsgToSp(SpKey.PERMISSION_STATE, permission, "Y"); //标识曾经申请过权限
                        Logger.e("PERMISSION_STATE--2");
                    }
                }
            }).setStandardStyle(2);
        } else {
            if (iCallBack != null) {
                iCallBack.back(callbackObject);
            }
        }
    }
}
