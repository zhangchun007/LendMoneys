package com.haiercash.gouhua.activity.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.HmacSHA256Utils;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.geetest.onelogin.OneLoginHelper;
import com.google.gson.reflect.TypeToken;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.DownStageUserReminderActivity;
import com.haiercash.gouhua.activity.checkdeviceid.CheckDeviceIdActivity;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.activity.gesture.GesturesSettingActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.beans.login.QueryAgreementListBean;
import com.haiercash.gouhua.beans.login.UserInfoBean;
import com.haiercash.gouhua.beans.register.IsPasswordExistBean;
import com.haiercash.gouhua.beans.register.SaveUauthUsersBeanRtn;
import com.haiercash.gouhua.biometriclib.BiometricUntil;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.AppLockUntil;
import com.haiercash.gouhua.utils.BrAgentUtils;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.LoginUserHelper;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.WyDeviceIdUtils;
import com.haiercash.gouhua.wxapi.WxUntil;
import com.networkbench.agent.impl.NBSAppAgent;
import com.megvii.idcardlib.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/6/21<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class LoginNetHelper implements INetResult, BiometricUntil.BiometricUntilCallBack {
    /**
     * 设备验证请求Code
     */
    public static final int REQUEST_CODE = 10;

    /**
     * 四要素验证请求Code
     */
    public static final int FOUR_REQUEST_CODE = 11;
    /**
     * 用户账号密码登录
     */
    public static final int USER_LOGIN = 1;
    /**
     * 用户注册登录
     */
    public static final int REGISTER_LOGIN = 2;
    /**
     * 用户微信授权登录
     */
    public static final int WX_LOGIN = 3;
    /**
     * 短信验证码登录
     */
    public static final int SMS_LOGIN = 4;

    /**
     * 一键登录
     */
    public static final int ONE_KEY_LOGIN = 5;

    private BaseActivity mActivity;
    private final NetHelper netHelper;
    /**
     * loginType == REGISTER_LOGIN 是需要用户的密码用于注册后自动登录过程
     */
    private String userPwd;
    /**
     * 微信锁屏的tag
     */
    private String wxLockTag;
    /**
     * 风险信息上送的类型
     */
    private final String riskDataType;
    /**
     * 绑定微信时，当前输入的手机号是否注册过<br/>
     * true 已注册的账号执行绑定操作；false 未注册过，执行账号注册且微信绑定
     */
    private boolean isRegister;
    /**
     * USER_LOGIN       用户账号密码登录<br/>
     * REGISTER_LOGIN   用户注册登录<br/>
     * WX_LOGIN         用户微信授权登录<br/>
     */
    private final int loginType;
    /**
     * 部分功能需要回调：短信验证码登录（验证失败的时候需要清空验证码）
     */
    private INetHelperCallBack callBack;
    /**
     * H5与APP交互时：需要先登录而后进入网页
     */
    private String webUrl, webDoType;

    String mobileNumber;  //记录当前的手机号

    String weChatOpenId = "";  //记录当前微信openID

    private final String pageCode;
    public static List<QueryAgreementListBean> agreementListBeanList;
    private TextView tvAgreement;
    private String infoTv;
    private String wxAuthCode;

    public LoginNetHelper(BaseActivity mActivity, int loginType, String riskDataType, String pageCode) {
        this.mActivity = mActivity;
        this.loginType = loginType;
        this.riskDataType = riskDataType;
        this.pageCode = pageCode != null ? pageCode : "LoginPage";
        webUrl = mActivity.getIntent().getStringExtra("webUrl");
        webDoType = mActivity.getIntent().getStringExtra("webDoType");
        netHelper = new NetHelper(mActivity, this);
    }

    public LoginNetHelper setWebUrl(String webUrl) {
        this.webUrl = webUrl;
        return this;
    }

    public LoginNetHelper setWebDoType(String webDoType) {
        this.webDoType = webDoType;
        return this;
    }

    String getPageCode() {
        return pageCode;
    }

    /**
     * 用户注册
     *
     * @param mobileNo 手机号
     * @param verifyNo 验证码
     * @param password 密码
     * @param deviceId 设备ID
     */
    void userRegister(String mobileNo, String verifyNo, String password, String deviceId) {
        mActivity.showProgress(true);
        mobileNumber = mobileNo;
        userPwd = password;
        Map<String, String> map = new HashMap<>();
        map.put("verifyNo", RSAUtils.encryptByRSA(verifyNo));
        map.put("password", RSAUtils.encryptByRSA(password));
        map.put("mobile", RSAUtils.encryptByRSA(mobileNo));
        map.put("deviceId", RSAUtils.encryptByRSA(CheckUtil.isEmpty(deviceId) ? "aaaaa" : deviceId));
        map.put("promotionChannel", SystemUtils.metaDataValueForTDChannelId(mActivity));
        map.put("iccId", RSAUtils.encryptByRSA(SystemUtils.getDeviceICCID(mActivity)));//设备iccid
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.url_zhuceyonghu_post, map, SaveUauthUsersBeanRtn.class, true);
    }

    /**
     * 用户登录
     *
     * @param mobileNo        手机号
     * @param password        密码
     * @param deviceId        设备ID
     * @param needBind        需要server绑定设备
     * @param isFromLoginPage 是否从登录页面调用(后端需要验证可能的滑块)
     * @param wyErrorCode     网易滑块错误code
     * @param wyErrorMsg      网易滑块错误msg
     */
    public void userLogin(String mobileNo, String password, String deviceId, String needBind, String validate, boolean isFromLoginPage, String wyErrorCode, String wyErrorMsg) {
        mActivity.showProgress(true);
        mobileNumber = mobileNo;
        Map<String, String> map = new HashMap<>();
        map.put("type", isFromLoginPage ? "login" : "set");
        map.put("deviceType", "AND");
        map.put("mobile", RSAUtils.encryptByRSA(mobileNo));
        map.put("password", password);
        map.put("deviceId", RSAUtils.encryptByRSA("AND-" + deviceId + "-" + mobileNo));
        if (!CheckUtil.isEmpty(validate)) {
            map.put("validate", validate);
        }
        if (!CheckUtil.isEmpty(wyErrorCode)) {
            map.put("wy_error_code", wyErrorCode);
        }
        if (!CheckUtil.isEmpty(wyErrorMsg)) {
            map.put("wy_error_msg", wyErrorMsg);
        }
        map.put("timeStamp", System.currentTimeMillis() + "");
        if (!TextUtils.isEmpty(needBind)) {
            map.put("needBind", needBind);
            SpHelper.getInstance().deleteAllMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE);//携带该参数表面server会走绑定设备流程
        }
        //必须放在map最后一行，是对整个map参数进行签名对
        map.put("sign", HmacSHA256Utils.buildNeedSignValue(map));
        netHelper.putService(ApiUrl.loginUrl, map, UserInfoBean.class, true);
    }

    /**
     * 用户登录
     *
     * @param mobileNo  手机号
     * @param captcha   验证码
     * @param captchaId 验证码id
     * @param seqNo     发送序号
     */
    public void smsLogin(String mobileNo, String captcha, String captchaId, String seqNo) {
        mActivity.showProgress(true);
        mobileNumber = mobileNo;
        Map<String, String> map = new HashMap<>();
        map.put("mobile", RSAUtils.encryptByRSA(mobileNo));
        map.put("captcha", RSAUtils.encryptByRSA(captcha));
        map.put("captchaId", captchaId);
        map.put("seqNo", seqNo);
        map.put("bizCode", "102101");
        map.put("deviceId", RSAUtils.encryptByRSA(SystemUtils.getDeviceID(mActivity)));
        map.put("iccId", RSAUtils.encryptByRSA(SystemUtils.getDeviceICCID(mActivity)));
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.POST_SMS_LOGIN, map, UserInfoBean.class, true);
    }


    /**
     * 一键登录接口
     *
     * @param process_id
     * @param token
     * @param authcode
     */
    public void onKeyLogin(String process_id, String token, String authcode) {
//        mActivity.showProgress(true);
        mActivity.showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("process_id", process_id);
        map.put("token", token);
        map.put("authcode", authcode);
        map.put("deviceId", SystemUtils.getDeviceID(mActivity));
        map.put("deviceType", "AND");
        map.put("iccId", SystemUtils.getDeviceICCID(mActivity));
        netHelper.postService(ApiUrl.ONE_KEY_LOGIN_URL, map, UserInfoBean.class, true);

    }

    /**
     * 获取协议列表----登录+注册
     */
    public void getAgreementList(TextView tvAgreement, String infoTv) {
        this.tvAgreement = tvAgreement;
        this.infoTv = infoTv;
        HashMap<String, String> map = new HashMap<>();
        map.put("sceneType", "register");
        netHelper.postService(ApiUrl.URL_GET_QUERY_AGREEMENT_LIST, map);
    }


    /**
     * 判断是否有H5调用APP的标记，如果有则跳转到相应的H5
     *
     * @return true 有标记并且跳转到相应的h5页面
     */
    private boolean checkH5OrCheckGesturePwd() {
        if (!CheckUtil.isEmpty(webUrl)) {
            //有登录后H5跳转，则需要置空登录结果回调
            AppApplication.setLoginCallback(null);
            //此时前后业务线如果一样才走跳转url，否则toast提示并回首页（matchUserBusinessByLocal()内部处理了）
            if (matchUserBusinessByLocal()) {
                Bundle bundle = new Bundle();
                bundle.putString("webDoType", webDoType);
                WebHelper.startActivityForUrl(mActivity, webUrl, bundle);
                //登录成功后关掉登录相关页
                ActivityUntil.finishOthersActivityByPageKey(PagePath.ACTIVITY_MAIN);
            }
            return true;
        }
        return false;
    }

    /**
     * 检测是否有手势密码
     */
    public void checkGesturePwd() {
        if (LoginSelectHelper.hasSetGesture()) {
            onSuccess(null, ApiUrl.urlExistPassword);
        } else {
            mActivity.showProgress(true);
            Map<String, String> map = new HashMap<>();
            map.put("userId", EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_USERID)));
            netHelper.postService(ApiUrl.urlExistPassword, map, IsPasswordExistBean.class);
        }
    }

    /**
     * 微信授权之后绑定手机
     *
     * @param isRegister 是否已经注册的账号
     * @param mobileNo   注册或绑定的手机号
     * @param verifyNo   手机号对应的验证码
     * @param openId     手机号需要绑定的微信openid
     */
    public void socialBind(boolean isRegister, String mobileNo, String verifyNo, String openId) {
        this.isRegister = isRegister;
        weChatOpenId = openId;
        mActivity.showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("isRegister", isRegister ? "Y" : "N");
        map.put("deviceId", RSAUtils.encryptByRSA(SystemUtils.getDeviceID(mActivity)));
        map.put("openId", RSAUtils.encryptByRSA(openId));
        map.put("mobileNo", RSAUtils.encryptByRSA(mobileNo));
        map.put("verifyNo", RSAUtils.encryptByRSA(verifyNo));
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.URL_POST_LOGIN_REGISTER, map, UserInfoBean.class, true);
    }

    /**
     * WXEntryActivity 授权登录或者解锁
     *
     * @param code 微信授权之后拿到的code
     */
    public void authSuccessIn(String code, String openId) {
        this.wxAuthCode = code;
        if (WxUntil.mBundle != null) {
            wxLockTag = WxUntil.mBundle.getString("tag");
            webUrl = WxUntil.mBundle.getString("webUrl");
            webDoType = WxUntil.mBundle.getString("webDoType");
        }
        mActivity.showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("openId", openId);
        if (CommomUtils.hasApplyPhoneStatePermission()) {
            map.put("deviceId", RSAUtils.encryptByRSA(SystemUtils.getDeviceID(mActivity)));
            map.put("hasAuth", "Y"); //是否开启了设备权限弹框
        } else {
            map.put("hasAuth", "N");
        }
        netHelper.postService(ApiUrl.URL_POST_IS_BANDING, map, UserInfoBean.class, true);
    }

    private BiometricUntil biometricUntil;

    /**
     * 新用户注册成功后逻辑
     */
    private void registerSuccessAction() {
        //新用户注册后，支持指纹，就引导设置指纹，不支持指纹就引导设置手势密码
        if (biometricUntil != null) {
            biometricUntil.cancelAuthenticate();
            biometricUntil = null;
        }
        biometricUntil = new BiometricUntil(mActivity, this);
        if (biometricUntil.isBiometricPromptEnable()) {
            mActivity.showDialog("温馨提示", "当前设备支持快捷登录，您可以开启指纹识别登录，操作更便捷哦～", "跳过", "去设置", (dialog, which) -> {
                if (which == 2) {
                    UMengUtil.commonClickEvent("SfppGoSet_Click", "去设置", "设置指纹页", "SetFingerprintPage");
                    biometricUntil.showBiometricLibPop();
                } else {
                    UMengUtil.commonClickEvent("SfppSkip_Click", "跳过按钮", "设置指纹页", "SetFingerprintPage");
                    UMengUtil.pageEnd("SetFingerprintPage");
                    afterQuickLoginSet();
                }
            }).setStandardStyle(2);
            UMengUtil.pageStart("SetFingerprintPage");
            postUmEventWithExposureFingerprintDialog();
        } else {
            if (!checkH5OrCheckGesturePwd()) {
                Intent intent = new Intent(mActivity, GesturesSettingActivity.class);
                intent.putExtra("pageType", "setGestures");
                mActivity.startActivity(intent);
            }
        }
    }

    private void postUmEventWithExposureFingerprintDialog() {
        Map<String, Object> map = new HashMap<>();
        map.put("pop_name", "指纹设置提示弹窗");
        map.put("page_name_cn", "设置指纹页");
        UMengUtil.onEventObject("SetFingerprintPop_Exposure", map, "SetFingerprintPage");
    }

    @Override
    public void onSuccess(Object t, String url) {
        mActivity.showProgress(false);
        if (ApiUrl.urlExistPassword.equals(url)) {
            IsPasswordExistBean bean = (IsPasswordExistBean) t;
            if (bean != null) {
                SpHelper spHelper = SpHelper.getInstance();
                spHelper.saveMsgToSp(SpKey.STATE, SpKey.STATE_HASPAYPAS, "1".equals(bean.getPayPasswdFlag()) ? "Y" : "N");
                if ("1".equals(bean.getGestureFlag())) {
                    LoginSelectHelper.saveGestureOpenState();
                }
            }
            if (loginType == USER_LOGIN || loginType == SMS_LOGIN) {
                if (loginType == USER_LOGIN) {
                    newPostLoginEvent("true", "无");
                } else {
                    UMengUtil.commonCompleteEvent("MMipCodeCheck_Result", getSmsPageNameCn(), "true", "无", getPageCode());
                }
                if (checkH5OrCheckGesturePwd()) {
                    UiUtil.toast("登录成功");
                    return;
                }
                AppApplication.loginSuccessToDo();
                UiUtil.toast("登录成功");
                return;
            }
            RiskInfoUtils.updateRiskInfoByNode(riskDataType, "YES");
            if (!isRegister) {
                RiskInfoUtils.updateRiskInfoByNode("BR01", "YES");
            } else {
                //百融风险采集登录
                BrAgentUtils.logInBrAgent(mActivity);
            }
            if (loginType == WX_LOGIN) {
                UMengUtil.pageStart("LoginPage");
                postLoginEvent(weChatOpenId, "WebCatLogin", "", "true", "");
                if (checkH5OrCheckGesturePwd()) {
                    UiUtil.toast("登录成功");
                    return;
                }
            }
            UiUtil.toast("登录成功");
            AppApplication.loginSuccessToDo();
        } else if (ApiUrl.url_zhuceyonghu_post.equals(url)) {
            SaveUauthUsersBeanRtn usersBeanRtn = (SaveUauthUsersBeanRtn) t;
            String userId = usersBeanRtn.getUserId();
            if (CheckUtil.isEmpty(userId)) {
                UiUtil.toast("服务器开小差了，请稍后再试");
                mActivity.finish();
                return;
            }
            userLogin(usersBeanRtn.getMobile(), userPwd, SystemUtils.getDeviceID(mActivity), "", null, false, null, null);
        } else if (ApiUrl.ONE_KEY_LOGIN_URL.equals(url)) {//一键登录回调
            AppLockUntil.resetTimes();
            OneKeyLoginUtils.getInstance().closeLoading();
            UserInfoBean bean = LoginUserHelper.getDecrypt((UserInfoBean) t);
            if (!"00000".equals(bean.getRetFlag())) {
                UiUtil.toast("网络异常");
                return;
            }
            if ("Y".equals(bean.getIsNewDevice()) || "Y".equals(bean.getLongTimeNotLogin())) {
                OneLoginHelper.with().dismissAuthActivity();
                UMengUtil.pageEnd("OnetouchloginPop");
                errorByNewDeviceLogin(bean, false, true);
                return;
            }
            if (!TextUtils.isEmpty(mobileNumber) && mobileNumber.equals(SpHelper.getInstance().readMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE_PHONE)) && "Y".equals(SpHelper.getInstance().readMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE))) {
                OneLoginHelper.with().dismissAuthActivity();
                UMengUtil.pageEnd("OnetouchloginPop");
                errorByNewDeviceLogin(bean, false, true);
                return;
            }
            LoginUserHelper.saveLoginInfo(bean);
            OneKeyLoginUtils.getInstance().loginEvent(true, "");
            //风险数据上送
            RiskNetServer.startRiskServer1(mActivity, "register_login_oauth_login", "", 2);
            if ("Y".equals(bean.getIsNewUser())) {//新户
                registerSuccessAction();
                afterOneKeyLoginSuccess();
            } else {
                checkGesturePwd();
            }

        } else if (ApiUrl.loginUrl.equals(url)) {
            UserInfoBean bean = LoginUserHelper.getDecrypt((UserInfoBean) t);
            if (!"00000".equals(bean.getRetFlag())) {
                UiUtil.toast("网络异常");
                return;
            }
            if ("Y".equals(bean.getIsNewDevice()) || "Y".equals(bean.getLongTimeNotLogin())) {
                errorByNewDeviceLogin(bean);
                return;
            }
            if (!TextUtils.isEmpty(mobileNumber) && mobileNumber.equals(SpHelper.getInstance().readMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE_PHONE)) && "Y".equals(SpHelper.getInstance().readMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE))) {
                errorByNewDeviceLogin(bean);
                return;
            }
            LoginUserHelper.saveLoginInfo(bean);
            if (loginType == USER_LOGIN) {
                RiskNetServer.startRiskServer1(mActivity, "register_login_password_login", "", 2);
                RiskInfoUtils.updateRiskInfoByNode(riskDataType, "YES");
                //百融风险采集登录
                BrAgentUtils.logInBrAgent(mActivity);
                if ("Y".equals(bean.getIsNewUser())) {//新户
                    registerSuccessAction();

                } else {
                    checkGesturePwd();
                }
                return;
            } else if (loginType == REGISTER_LOGIN) {
                //百融注册
                BrAgentUtils.registerBrAgent(mActivity, (afSwiftNumber, brObject) -> {
                    RiskInfoUtils.postBrOrBigData(mActivity, "register", "", brObject);
                    RiskInfoUtils.requestRiskInfoBrAgentInfo(afSwiftNumber, "antifraud_register", "");
                });
                RiskNetServer.startRiskServer1(mActivity, "register_set_login_password", "", 1);
                RiskInfoUtils.updateRiskInfoByNode(riskDataType, "YES");
                registerSuccessAction();
            }
            UiUtil.toast("登录成功");
        } else if (ApiUrl.POST_SMS_LOGIN.equals(url)) {//短信验证码登录
            UserInfoBean bean = LoginUserHelper.getDecrypt((UserInfoBean) t);
            if (!"00000".equals(bean.getRetFlag())) {
                mActivity.showDialog("网络异常");
                UMengUtil.commonCompleteEvent("MMipCodeCheck_Result", getSmsPageNameCn(), "false", "网络异常", getPageCode());
                if (callBack != null) {
                    callBack.onCallBack("网络异常");
                }
                return;
            }
            if ("Y".equals(bean.getIsNewDevice()) || "Y".equals(bean.getLongTimeNotLogin())) {
                if ("Y".equals(bean.getIsRealInfo()) || "Y".equals(bean.getPwdStatus())) {
                    errorByNewDeviceLogin(bean, true, false);
                    return;
                }

            }
            if (!TextUtils.isEmpty(mobileNumber) && mobileNumber.equals(SpHelper.getInstance().readMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE_PHONE)) && "Y".equals(SpHelper.getInstance().readMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE))) {
                errorByNewDeviceLogin(bean);
                return;
            }
            smsLoginSuss(bean);
        } else if (ApiUrl.URL_POST_LOGIN_REGISTER.equals(url)) {
            postLoginEvent("", "WebCatBindTel", mobileNumber, "true", "");
            if (!TextUtils.isEmpty(weChatOpenId)) {
                UMengUtil.pageStart("LoginPage");
                postLoginEvent(weChatOpenId, "WebCatLogin", "", "true", "");
            }
            if (!isRegister) { //百融注册
                BrAgentUtils.registerBrAgent(mActivity, (afSwiftNumber, brObject) -> {
                    RiskInfoUtils.postBrOrBigData(mActivity, "register", "", brObject);
                    RiskInfoUtils.requestRiskInfoBrAgentInfo(afSwiftNumber, "antifraud_register", "");
                });
            }
            UserInfoBean auth = LoginUserHelper.getDecrypt((UserInfoBean) t);
            if ("Y".equals(auth.getIsNewDevice()) || "Y".equals(auth.getLongTimeNotLogin())) {
                otherErrorByNewDeviceLogin(auth);
            } else if (!TextUtils.isEmpty(mobileNumber) && mobileNumber.equals(SpHelper.getInstance().readMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE_PHONE)) && "Y".equals(SpHelper.getInstance().readMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE))) {
                otherErrorByNewDeviceLogin(auth);
            } else {
                LoginUserHelper.saveLoginInfo(auth);
                checkGesturePwd();
            }
            RiskNetServer.startRiskServer1(mActivity, "wechat_login_bind_phone_number", "", 1);
        } else if (ApiUrl.URL_POST_IS_BANDING.equals(url)) {
            UserInfoBean auth = LoginUserHelper.getDecrypt((UserInfoBean) t);
            UMengUtil.pageStart("LoginPage");
            weChatOpenId = auth.getOpenId();
            postLoginEvent(weChatOpenId, "WebCatLogin", "", "true", "");
            RxBus.getInstance().post(new ActionEvent(ActionEvent.REFRESHUSERINFO));
            String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
            //微信锁屏页进来的 && 并且是同一个用户
            if ("lock".equals(wxLockTag) && auth.getUserId().equals(userId)) {
                RiskNetServer.startRiskServer1(mActivity, "lock_screen_wechat_login", "", 2);
                LoginUserHelper.saveLoginInfo(auth);
                if (!CheckUtil.isEmpty(webDoType) && !CheckUtil.isEmpty(webUrl)) {
                    Intent intent = new Intent(mActivity, JsWebBaseActivity.class);
                    intent.putExtra("jumpKey", webUrl);
                    intent.putExtra("webDoType", webDoType);
                    mActivity.startActivity(intent);
                }
                ActivityUntil.finishActivityByPageKey(PagePath.ACTIVITY_VERIFY_WX);
                if (!(mActivity instanceof MainActivity)) {
                    mActivity.finish();
                }
                return;
            }
            RiskNetServer.startRiskServer1(mActivity, "register_login_wechat_login", "", 2);
            if ("Y".equals(auth.getIsBanding())) {//已经绑定微信
                //百融风险采集登录
                BrAgentUtils.logInBrAgent(mActivity);
                if ("Y".equals(auth.getHasAuth())) { //弹出过设备授权
                    if ("Y".equals(auth.getIsNewDevice()) || "Y".equals(auth.getLongTimeNotLogin())) {
                        otherErrorByNewDeviceLogin(auth);
                    } else if (!TextUtils.isEmpty(mobileNumber) && mobileNumber.equals(SpHelper.getInstance().readMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE_PHONE)) && "Y".equals(SpHelper.getInstance().readMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE))) {
                        otherErrorByNewDeviceLogin(auth);
                    } else {
                        LoginUserHelper.saveLoginInfo(auth);
                        checkGesturePwd();
                    }
                } else { //没有开启过设备授权
                    mActivity.requestPermission(aBoolean -> {
                        //保存存储设备权限状态
                        SpHelper.getInstance().saveMsgToSp(SpKey.PERMISSION_STATE, Manifest.permission.READ_PHONE_STATE, "Y");
                        Logger.e("PERMISSION_STATE--0");
                        //保存申请存储设备权的时间
                        SpHelper.getInstance().saveMsgToSp(SpKey.READ_PHONE_STATE, SpKey.LAST_SHOW_DIALOG_TIME, TimeUtil.calendarToString());
                        //调用bing接口
                        authSuccessIn(wxAuthCode, weChatOpenId);
                    }, R.string.permission_phone_statue, Manifest.permission.READ_PHONE_STATE);
                }

            } else {
                ARouterUntil router = ARouterUntil.getInstance(PagePath.ACTIVITY_SOCIAL_BIND);
                if (!CheckUtil.isEmpty(webUrl) && !CheckUtil.isEmpty(webDoType)) {
                    router.put("webUrl", webUrl).put("webDoType", webDoType);
                }
                router.put("openId", auth.getOpenId()).navigation();
                if (!(mActivity instanceof MainActivity)) {
                    mActivity.finish();
                }
            }
        } else if (ApiUrl.URL_GET_QUERY_AGREEMENT_LIST.equals(url)) {
            agreementListBeanList = JsonUtils.fromJsonArray(t, QueryAgreementListBean.class);
            if (!CheckUtil.isEmpty(tvAgreement) && !CheckUtil.isEmpty(infoTv)) {
                SpHelper.getInstance().saveMsgToSp(SpKey.LATEST_AGREEMENT, SpKey.LATEST_AGREEMENT_LIST, JsonUtils.toJson(agreementListBeanList));
                setAgreement(infoTv, agreementListBeanList);
            }

        }

    }

    /**
     * 一键登录成功后关闭页面的操作
     */
    private void afterOneKeyLoginSuccess() {
        //关闭一键登录页面
        OneLoginHelper.with().dismissAuthActivity();
        UMengUtil.pageEnd("OnetouchloginPop");
        RxBus.getInstance().post(new ActionEvent(ActionEvent.REFRESHUSERINFO));
    }

    /**
     * H5中调用app登录后需要判断登录业务线是否跟未登录时一样，
     * 如果一样，则登录逻辑走完后继续跳转；
     * 如果不一样，则登录逻辑走完后不跳转web而直接回到首页
     * 在save用户信息之后调用
     * 携带webUrl进入登录界面才需要此判断
     *
     * @return 前后业务线一样则true
     */
    private boolean matchUserBusinessByLocal() {
        String business = TokenHelper.getInstance().getSmyParameter("business");
        if (CheckUtil.isEmpty(business) && CheckUtil.isEmpty(NetConfig.TD_BUSINESS_SMY)) {
            return true;
        }
        if (business != null && business.equals(NetConfig.TD_BUSINESS_SMY)) {
            return true;
        }
        UiUtil.toast("抱歉，您暂不符合该活动，如有问题联系客服400-018–7777");
        MainHelper.backToMain();
        return false;
    }

    /**
     * ApiUrl.loginUrl和ApiUrl.url_ValidateGestureCount 接口请求后报错
     * 返回错误码为 U01999 代表降期用户
     *
     * @param mActivity BaseActivity对象
     * @param error     接口请求的错误实体
     * @return true:代表为降期用户并且已成功跳转到降期界面；false：代表参数获取失败等因素，继续运行其他流程
     */
    public static boolean isDownStageUser(BaseActivity mActivity, BasicResponse error) {
        if ("U01999".equals(error.getHead().getRetFlag())) {
            if (!CheckUtil.isEmpty(error.getBody())) {
                HashMap map = JsonUtils.fromJson(error.getBody(), HashMap.class);
                if (!CheckUtil.isEmpty(map) && map.containsKey("custNo")) {
                    String custNo = String.valueOf(map.get("custNo"));
                    if (!CheckUtil.isEmpty(custNo)) {
                        Intent intent = new Intent(mActivity, DownStageUserReminderActivity.class);
                        intent.putExtra("custNo", custNo);
                        mActivity.startActivity(intent);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onError(BasicResponse error, String url) {
        mActivity.showProgress(false);
        if (ApiUrl.URL_POST_IS_BANDING.equals(url)) {
            UMengUtil.pageStart("LoginPage");
            RiskNetServer.startRiskServer1(mActivity, "register_login_wechat_login", "", 2);
            postLoginEvent(weChatOpenId, "WebCatLogin", "", "false", error.getHead().getRetMsg());
            UiUtil.toast(error.getHead().getRetMsg());
            mActivity.finish();
        } else if (ApiUrl.ONE_KEY_LOGIN_URL.equals(url)) {//一键登录回调
            OneLoginHelper.with().dismissAuthActivity();
            OneKeyLoginUtils.getInstance().closeLoading();
            UMengUtil.pageEnd("OnetouchloginPop");
            Map<String, Object> map = new HashMap<>();
            map.put("page", "一键登录页面");
            map.put("message", error.head.retMsg);
            NBSAppAgent.reportError("发生异常", map);
            OneLoginHelper.with().register(CommonConfig.ONE_KEY_SECRET);
            onError(error);
            OneKeyLoginUtils.getInstance().loginEvent(false, error.getHead().getRetMsg());
        } else if (ApiUrl.loginUrl.equals(url)) {
            if (isDownStageUser(mActivity, error)) {
                return;
            }
            if (loginType == REGISTER_LOGIN) {
                RiskNetServer.startRiskServer1(mActivity, "register_set_login_password", "", 1);
                //后台登录失败跳到登录页
                MainHelper.backToMain();
                LoginSelectHelper.staticToGeneralLogin();
            } else if (loginType == USER_LOGIN) {
                RiskNetServer.startRiskServer1(mActivity, "register_login_password_login", "", 2);
                newPostLoginEvent("false", error.getHead().getRetMsg());
                onError(error);
            }
        } else if (ApiUrl.POST_SMS_LOGIN.equals(url)) {
            UMengUtil.commonCompleteEvent("MMipCodeCheck_Result", getSmsPageNameCn(), "false", error.getHead().getRetMsg(), getPageCode());
            if (callBack != null) {
                callBack.onCallBack(error.getHead().getRetMsg());
            }
            onError(error);
        } else if (ApiUrl.urlExistPassword.equals(url)) {
            if (loginType == USER_LOGIN) {
                AppApplication.loginSuccessToDo();
                UiUtil.toast("登录成功");
            } else {
                onSuccess(null, ApiUrl.urlExistPassword);
            }
        } else if (ApiUrl.url_zhuceyonghu_post.equals(url)) {
            onError(error);
        } else if (ApiUrl.URL_GET_QUERY_AGREEMENT_LIST.equals(url)) {
            if (!CheckUtil.isEmpty(tvAgreement) && !CheckUtil.isEmpty(infoTv)) {
                setAgreement(infoTv, getDefaultAgreementList());
            }

        } else {
            if (ApiUrl.URL_POST_LOGIN_REGISTER.equals(url)) {
                RiskNetServer.startRiskServer1(mActivity, "wechat_login_bind_phone_number", "", 1);
            }
            onError(error);
        }
    }

    private void onError(BasicResponse error) {
        if (callBack == null) {
            if (error == null || error.getHead() == null) {
                UiUtil.toast("服务器开小差了，请稍后再试");
            } else {
                UiUtil.toast(error.getHead().getRetMsg());
            }
        }
    }

    private void errorByNewDeviceLogin(UserInfoBean body) {
        errorByNewDeviceLogin(body, false, false);
    }

    /**
     * ApiUrl.loginUrl和 ApiUrl.POST_SMS_LOGIN在新设备登录的错误信息处理
     */
    private void errorByNewDeviceLogin(UserInfoBean body, boolean fromSms, boolean isOneKeyLogin) {
        SpHelper.getInstance().saveMsgToSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE, "Y");
        SpHelper.getInstance().saveMsgToSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE_PHONE, mobileNumber);
        isRegister = true;
        LoginUserHelper.loginSuccessButDeviceId(body);
        Intent intent_checkDevice = new Intent(mActivity, CheckDeviceIdActivity.class);
        intent_checkDevice.putExtra("userInfoBean", body);
        intent_checkDevice.putExtra("fromSms", fromSms);
        intent_checkDevice.putExtra("isOneKeyLogin", isOneKeyLogin);
        if (fromSms) {
            mActivity.startActivityForResult(intent_checkDevice, FOUR_REQUEST_CODE);
        } else {
            mActivity.startActivityForResult(intent_checkDevice, FOUR_REQUEST_CODE);

        }
        RiskInfoUtils.updateRiskInfoByNode(riskDataType, "YES");
    }

    //除上述情况外的设备认证
    private void otherErrorByNewDeviceLogin(UserInfoBean body) {
        SpHelper.getInstance().saveMsgToSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE, "Y");
        SpHelper.getInstance().saveMsgToSp(SpKey.NEED_CHANGE_DEVICE_STATE, SpKey.NEED_VALIDATE_PHONE, mobileNumber);
        LoginUserHelper.loginSuccessButDeviceId(body);
        Intent intent_checkDevice = new Intent(mActivity, CheckDeviceIdActivity.class);
        intent_checkDevice.putExtra("userInfoBean", body);
        mActivity.startActivityForResult(intent_checkDevice, REQUEST_CODE);
    }

    /**
     * 手动断开连接
     */
    public void onDestroy() {
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        mActivity = null;
    }

    public void setCallBack(INetHelperCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 快捷登录设置或者取消之后
     */
    private void afterQuickLoginSet() {
        if (!checkH5OrCheckGesturePwd()) {
            AppApplication.loginSuccessToDo();
        }
    }

    @Override
    public void biometricSwitch(boolean isOpening) {
        //正在开启指纹过程进行中
    }

    @Override
    public void onSuccess(boolean isOpenFingerprint) {
        //指纹开启/关闭成功
        postUmEventWithFingerprintResult(true, "");
        UMengUtil.pageEnd("SetFingerprintPage");
        afterQuickLoginSet();
    }

    @Override
    public void onFailed(Integer errorCode, String errorReason) {
        //指纹开启失败,包括其所有原因导致的失败
        postUmEventWithFingerprintResult(false, errorReason);
    }

    @Override
    public void onErrorForMoreFailed() {
        //5次及以上指纹验证失败
        UMengUtil.pageEnd("SetFingerprintPage");
        mActivity.showDialog("指纹验证失败", "指纹识别登录开启失败。您可稍后在我的-设置-安全中心 尝试开启～", (dialog, which) -> {
            afterQuickLoginSet();
        });
    }

    @Override
    public void onCancel() {
        //取消指纹识别
        UMengUtil.pageEnd("SetFingerprintPage");
        afterQuickLoginSet();
    }

    /*指纹设置成功/失败埋点*/
    private void postUmEventWithFingerprintResult(boolean isSuccess, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("is_success", isSuccess ? "true" : "false");
        map.put("fail_reason", !CheckUtil.isEmpty(failReason) ? failReason : "无");
        map.put("page_name_cn", "设置指纹页");
        UMengUtil.onEventObject("SfrpSet_Result", map, "SetFingerprintPage");
    }

    public interface INetHelperCallBack {
        void onCallBack(String errMsg);
    }

    private String[] mAgreementName = {"用户注册协议", "个人信息使用授权", "消费信贷服务协议", "用户隐私权政策"};

    private String[] mAgreementUrl = {ApiUrl.URL_REGISTER_AGREEMENT, ApiUrl.URL_AGREEMENT_URL_1, ApiUrl.URL_AGREEMENT_URL_2, ApiUrl.URL_AGREEMENT_URL_3};

    public static List<QueryAgreementListBean> getDefaultAgreementList() {
        String savedData = SpHelper.getInstance().readMsgFromSp(SpKey.LATEST_AGREEMENT, SpKey.LATEST_AGREEMENT_LIST);
        if (!CheckUtil.isEmpty(savedData)) {
            return JsonUtils.fromJson(savedData, new TypeToken<List<QueryAgreementListBean>>() {
            }.getType());
        }
        List<QueryAgreementListBean> list = new ArrayList<>();
        QueryAgreementListBean bean1 = new QueryAgreementListBean();
        bean1.setContCode("ConsumeServiceUserUsage-BZ-2022-V1.0");
        bean1.setContName("《消费信贷服务用户使用协议》");
        bean1.setContType("ConsumeServiceUserUsage");
        bean1.setContUrl("/static/agreement/ConsumeServiceUserUsage-BZ-2022-V1.0.html?channelNo=42");

        QueryAgreementListBean bean2 = new QueryAgreementListBean();
        bean2.setContCode("PrivacyPolicy-2022-V1.0");
        bean2.setContName("《海尔消费金融隐私政策》");
        bean2.setContType("PrivacyAgreement");
        bean2.setContUrl("/static/agreement/PrivacyPolicy-2022-V1.0.html?channelNo=42");

        list.add(bean1);
        list.add(bean2);
        return list;
    }

    //兼容部分弹窗场景
    public SpannableStringBuilder setAgreement(String text) {
        return setAgreement(text, getDefaultAgreementList());
    }

    //兼容部分弹窗场景
    public SpannableStringBuilder setAgreement(String text, boolean isFindTv) {
        return setAgreement(text, getDefaultAgreementList(), isFindTv);
    }

    /**
     * 设置注册协议样式
     */
    public SpannableStringBuilder setAgreement(String text, List<QueryAgreementListBean> list) {
        return setAgreement(text, list, true);
    }

    /**
     * 设置注册协议样式,最后一个参数的目的是：在activity使用时候如果弹窗了，会把当前activity协议内容变更
     */
    public SpannableStringBuilder setAgreement(String text, List<QueryAgreementListBean> list, boolean isFindTv) {
        SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(mActivity, text);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (i != 0) {
                    builder.append(" ").setForegroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                }
                int finalI = i;
                builder.append(list.get(i).getContName()).setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        KeyBordUntil.hideKeyBord2(mActivity);
                        WebSimpleFragment.WebService(mActivity, ApiUrl.API_SERVER_URL + list.get(finalI).getContUrl(), list.get(finalI).getContName());
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                        ds.setUnderlineText(false);
                        ds.clearShadowLayer();
                    }
                });
            }
        }

        if (!CheckUtil.isEmpty(tvAgreement) && isFindTv) {
            tvAgreement.setText(builder.create());
        }
        return builder.create();
    }

    public void smsLoginSuss(UserInfoBean bean) {
        LoginUserHelper.saveLoginInfo(bean);
        SpHelper.getInstance().deleteAllMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE);//登录成功，表明需要删除该参数
        RiskNetServer.startRiskServer1(mActivity, "loyal_cust_sms_login_success", "", 2);
        RiskInfoUtils.updateRiskInfoByNode(riskDataType, "YES");
        //百融风险采集登录
        BrAgentUtils.logInBrAgent(mActivity);
        if ("Y".equals(bean.getIsNewUser())) {//新户
            registerSuccessAction();
        } else {
            checkGesturePwd();
        }
    }

    private void postLoginEvent(String webcat_account, String eventId, String tel, String success, String failReason) {
        Map<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(webcat_account)) {
            map.put("webcat_account", webcat_account);
        }
        if (!TextUtils.isEmpty(tel)) {
            map.put("tel", EncryptUtil.base64(tel));
        }
        map.put("is_first", isRegister ? "true" : "false");
        UMengUtil.commonCompleteEvent(eventId, map, success, failReason, this.pageCode);
    }

    private void newPostLoginEvent(String success, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "账号密码登录弹窗");
        map.put("button_name", "登录");
        UMengUtil.commonCompleteEvent("PspLogin_Result", map, success, failReason, this.pageCode);
    }

    private Map<String, Object> getSmsPageNameCn() {
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "验证码登录-输入验证码弹窗");
        return map;
    }
}
