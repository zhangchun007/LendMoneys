package com.haiercash.gouhua.activity.login;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.geetest.onelogin.activity.OneLoginActivity;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.account.CheckPhoneFragment;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.edu.PerfectInfoHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseDialogActivity;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.WyDeviceIdUtils;
import com.haiercash.gouhua.utils.YiDunUtils;
import com.haiercash.gouhua.widget.DelEditText;
import com.haiercash.gouhua.widget.NewNKeyBoardTextField;
import com.haiercash.gouhua.wxapi.WxUntil;
import com.netease.nis.captcha.Captcha;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class BaseLoginActivity extends BaseDialogActivity implements View.OnFocusChangeListener, YiDunUtils.MyYiDunCaptchaListener {
    protected int loginWay; //登录方式
    protected LoginNetHelper loginNetHelper;

    @BindView(R.id.root_view)
    RelativeLayout rootView;
    @BindView(R.id.iv_back)
    AppCompatImageView ivBack;
    @BindView(R.id.iv_close)
    AppCompatImageView ivClose;

    @BindView(R.id.et_mobile)
    DelEditText etMobile;
    @BindView(R.id.tv_password_login)
    TextView tvPasswordLogin;
    @BindView(R.id.et_password)
    NewNKeyBoardTextField etPassword;
    @BindView(R.id.line_psw)
    View linePsw;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.tv_sms_login)
    TextView tvSmsLogin;

    @BindView(R.id.cb_agree)
    CheckBox cbAgree;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R.id.btn_next)
    TextView tvNext;

    @BindView(R.id.iv_phone)
    ImageView ivPhone;
    @BindView(R.id.iv_wechat)
    ImageView ivWeChat;
    protected String mobile;  //外部携带的手机号
    protected String mobileNo;
    private String passWord;
    private String deviceId;
    protected boolean isFromSms; //判断当前账号密页面进入
    protected String changePhone;//是否需要关闭所有页面，为Y则需要关闭，且回到首页

    private String captchaId, seqNo,message;
    @Override
    protected int getLayout() {
        return R.layout.activity_base_login;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        initView();
        //如果是因为单点登录被下线，需要展示原因
        LoginSelectHelper.logoutReasonDialog(this, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mobile = intent.getStringExtra("mobile");
        changePhone = intent.getStringExtra("changePhone");
        setPhone(intent);
    }

    private void setPhone(Intent intent) {
        mobile = intent.getStringExtra("mobile");
        changePhone = intent.getStringExtra("changePhone");
        isFromSms = intent.getBooleanExtra("fromSms", false);
        boolean needHide = intent.getBooleanExtra("needHide", true);

        boolean needShowBack = intent.getBooleanExtra("needShowBack", false);
        if (needShowBack) {
            ivBack.setVisibility(View.VISIBLE);
        } else {
            ivBack.setVisibility(View.GONE);
        }
        if (needHide && !CheckUtil.isEmpty(mobile) && mobile.length() == 11
                && CheckUtil.checkPhone(PerfectInfoHelper.phone_pattern, mobile)) {
            etMobile.setText(CheckUtil.hidePhoneNumber(mobile));
        } else if (!CheckUtil.isEmpty(mobile)) {
            etMobile.setText(mobile);
        } else if (!CheckUtil.isEmpty(LoginSelectHelper.getLastLoginSuccessMobile()) && CheckUtil.isEmpty(changePhone)) {
            if (LoginSelectHelper.getLastLoginSuccessMobile().length() == 11) {
                mobile = LoginSelectHelper.getLastLoginSuccessMobile();
                etMobile.setText(CheckUtil.hidePhoneNumber(mobile));
            }
        }
    }

    //根据loginType确定哪些显示哪些不显示
    private void initView() {
        tvNext.setTypeface(FontCustom.getMediumFont(this));
        CheckUtil.formatPhone344(etMobile);
        etMobile.addFocusChangeListener(this);
        etPassword.addOnFocusChangeLis(this);
        etPassword.setNewNKeyTextSize(20, 13);
        setPhone(getIntent());
        if (isPasswordLogin()) {  //账号密码登录
            tvPasswordLogin.setVisibility(View.GONE);
            tvNext.setText(R.string.login);

        } else {  //短信验证码
            etPassword.setVisibility(View.GONE);
            linePsw.setVisibility(View.GONE);
            tvForgetPassword.setVisibility(View.GONE);
            tvSmsLogin.setVisibility(View.GONE);
            tvNext.setText(R.string.get_code);
        }

        if (!CheckUtil.isEmpty(LoginNetHelper.agreementListBeanList)) {
            tvAgreement.setText(loginNetHelper.setAgreement("我已阅读并同意够花APP", LoginNetHelper.agreementListBeanList));
        } else {
            loginNetHelper.getAgreementList(tvAgreement, "我已阅读并同意够花APP");
        }
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        tvAgreement.setHighlightColor(Color.TRANSPARENT);
        cbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String, Object> map = new HashMap<>();
                if (isChecked) {
                    map.put("agreement", tvAgreement.getText().toString().replace("我已阅读并同意够花APP", ""));
                    if (isPasswordLogin()) {
                        UMengUtil.commonClickEvent("PspAgreementCheck_Click", "勾选协议", getPageNameCn(), map, getPageCode());
                    } else {
                        UMengUtil.commonClickEvent("MsgpAgreementCheck_Click", "勾选协议", getPageNameCn(), map, getPageCode());
                    }
                } else {
                    if (isPasswordLogin()) {
                        UMengUtil.commonClickEvent("PspAgreementUncheck_Click", "取消勾选协议", getPageNameCn(), map, getPageCode());
                    } else {
                        UMengUtil.commonClickEvent("MsgpAgreementUncheck_Click", "取消勾选协议", getPageNameCn(), map, getPageCode());
                    }
                }
            }
        });
        softAdapter(BaseLoginActivity.this, findViewById(R.id.root_view), isPasswordLogin() ? tvSmsLogin : tvPasswordLogin, UiUtil.dip2px(BaseLoginActivity.this, 20), isPasswordLogin() ? findViewById(R.id.et_password) : null);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {
            if (v.getId() == R.id.et_password) {
                linePsw.setSelected(hasFocus);
                if (!hasFocus) {
                    UMengUtil.postEvent("PspInputLoseFoucs");
                }
            } else if (v.getId() == R.id.et_mobile) {
                if (hasFocus && !CheckUtil.isEmpty(etMobile) && etMobile.getInputText().contains("*")) {
                    etMobile.setText("");
                    etMobile.requestFocus();
                    KeyBordUntil.openKeyBord(this);
                }
                if (!hasFocus) {
                    if (isPasswordLogin()) {
                        UMengUtil.postEvent("PspTelInputLoseFoucs");
                    } else {
                        UMengUtil.postEvent("MsgpTelInputLoseFoucs");
                    }
                }
            }
        } catch (Exception e) {
            Logger.e("fragment销毁而使设置了onFocusChange的View失去焦点");
        }
    }

    /**
     * 根据输入框内容判断 按钮状态
     */
    @OnTextChanged(value = {R.id.et_mobile, R.id.et_password}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        if (etMobile.getInputText().length() == 0) {
            // No entered text so will show hint
            etMobile.setTypeface(null);
            etMobile.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            etMobile.setTypeface(FontCustom.getDinFont(this));
            etMobile.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        }
        //输入手机号等于11位时，收起软件盘
        if (getRealMobile().length() == 11) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                KeyBordUntil.hideKeyBord(BaseLoginActivity.this);
                if (etMobile != null)
                    etMobile.clearFocus();
            }, 200);

        }
        if (isPasswordLogin()) {
            tvNext.setEnabled(etMobile.getInputText().length() >= 11 && etPassword.getNKeyboardText().length() >= 6);
        } else {
            tvNext.setEnabled(etMobile.getInputText().length() >= 11);
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_close, R.id.tv_password_login, R.id.tv_forget_password, R.id.tv_sms_login, R.id.btn_next, R.id.iv_phone, R.id.iv_wechat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (isPasswordLogin()) {
                    goSmsLogin();
                } else {
                    goPassWordLogin();
                }
                break;
            case R.id.iv_close:
                animTypes = ANIM_TYPE_BOTTOM;
                if (isPasswordLogin()) {
                    UMengUtil.commonClickEvent("PspClose_Click", "弹窗关闭按钮", getPageNameCn(), getPageCode());
                } else {
                    UMengUtil.commonClickEvent("MsgpClose_Click", "弹窗关闭按钮", getPageNameCn(), getPageCode());
                }
                if (needCloseAll()) {
                    ActivityUntil.finishOthersActivity(MainActivity.class);
                } else {
                    ActivityUntil.finishActivity(LoginSelectHelper.getLoginWayActivityPages());
                }
                break;
            case R.id.tv_password_login:
                goPassWordLogin();
                UMengUtil.commonClickEvent("MsgpPasswordLogin_Click", "账号密码登录", getPageNameCn(), getPageCode());
                break;
            case R.id.tv_forget_password:
                forgetPsw();
                break;
            case R.id.tv_sms_login:
                UMengUtil.commonClickEvent("PspMessageLogin_Click", "验证码登录", getPageNameCn(), getPageCode());
                goSmsLogin();
                break;
            case R.id.btn_next:
                if (isPasswordLogin()) {
                    UMengUtil.commonClickEvent("PspLogin_Click", "登录", getPageNameCn(), getPageCode());
                    passwordWay();
                } else {
                    UMengUtil.commonClickEvent("MsgpAd_CodeInputBox_Click", "获取验证码", getPageNameCn(), getPageCode());
                    smsWay();
                }
                break;
            case R.id.iv_phone:
                if (isPasswordLogin()) {
                    UMengUtil.commonClickEvent("PspOnetouchlogin_Click", "一键登录", getPageNameCn(), getPageCode());
                } else {
                    UMengUtil.commonClickEvent("MsgpOnetouchlogin_Click", "一键登录", getPageNameCn(), getPageCode());
                }
                animTypes = ANIM_TYPE_BOTTOM;
                //防止多次调用一键登录页面出现的bug，因为OneLoginActivity不是singleTask的
                OneLoginActivity oneLoginActivity = ActivityUntil.findActivity(OneLoginActivity.class);
                if (oneLoginActivity != null) {
                    ActivityUntil.finishActivity(LoginSelectHelper.getLoginWayActivityPages());
                } else {
                    LoginSelectHelper.staticToGeneralLogin(this, null, "oneKey");
                }
                //}
                break;
            case R.id.iv_wechat:
                if (isPasswordLogin()) {
                    UMengUtil.commonClickEvent("PspWechatLogin_Click", "微信登录", getPageNameCn(), getPageCode());
                } else {
                    UMengUtil.commonClickEvent("MsgpWechatLogin_Click", "微信登录", getPageNameCn(), getPageCode());
                }
                weChatLogin();
                break;
        }
    }

    //跳转到账号密码登录
    private void goPassWordLogin() {
        Intent intent = new Intent();
        intent.putExtra("mobile", getRealMobile());
        intent.putExtra("fromSms", true);
        intent.putExtra("needHide", needHide());
        intent.putExtra("changePhone", needCloseAll() ? "Y" : "N");
        if (null != ActivityUntil.findActivity(PasswordWayLoginActivity.class)) {
            RxBus.getInstance().post(new ActionEvent(ActionEvent.GO_PASSWORD_LOGIN_WAY, intent));
            animTypes = ANIM_TYPE_RIGHT;
            finish();
        } else {
            intent.putExtra("needShowBack", true);
            PasswordWayLoginActivity.startDialogActivity(BaseLoginActivity.this, PasswordWayLoginActivity.class, ANIM_TYPE_RIGHT, intent);
            RxBus.getInstance().post(new ActionEvent(ActionEvent.GO_PASSWORD_LOGIN_WAY, intent));
        }

    }

    //跳转到短信登录
    private void goSmsLogin() {
        Intent intent = new Intent();
        intent.putExtra("mobile", getRealMobile());
        intent.putExtra("needHide", needHide());
        intent.putExtra("changePhone", needCloseAll() ? "Y" : "N");
        if (null != ActivityUntil.findActivity(SmsWayLoginActivity.class)) {
            RxBus.getInstance().post(new ActionEvent(ActionEvent.GO_SMS_LOGIN_WAY, intent));
            animTypes = ANIM_TYPE_RIGHT;
            finish();
        } else {
            intent.putExtra("needShowBack", true);
            SmsWayLoginActivity.startDialogActivity(BaseLoginActivity.this, SmsWayLoginActivity.class, ANIM_TYPE_RIGHT, intent);
            RxBus.getInstance().post(new ActionEvent(ActionEvent.GO_SMS_LOGIN_WAY, intent));

        }
    }

    //验证码方式
    private void smsWay() {
        mobileNo = getRealMobile();
        if (!CheckUtil.checkPhone(PerfectInfoHelper.phone_pattern, mobileNo)) {
            UiUtil.toast("手机号格式错误，请重新输入");
            return;
        }
        if (!cbAgree.isChecked()) {
            showContractDialog();
        } else {
            if (CommomUtils.noNeedPhoneStateDialog()) {
                sendOtp();
            } else {
                requestPermission(aBoolean -> {
                    sendOtp();
                }, R.string.permission_phone_statue, Manifest.permission.READ_PHONE_STATE);

            }
        }
    }

    private void goSmsCodePage(String captchaId, String seqNo,String message) {
        Intent intent = new Intent();
        intent.putExtra("mobileNo", mobileNo);
        intent.putExtra("changePhone", needCloseAll() ? "Y" : "N");
        intent.putExtra("isSendOtpSuccess", true);
        intent.putExtra("captchaId", captchaId);
        intent.putExtra("seqNo", seqNo);
        intent.putExtra("message", message);
        startDialogActivity(this, SmsCodeActivity.class, ANIM_BOTTOM_IN_RIGHT_OUT, intent);
    }

    private void sendOtp(){
        if (YiDunUtils.isSliderOpen()) {
            doYiDun();
        } else {
            onSendSms(mobileNo, null, null, null);
        }
    }

    /**
     * 发送验证码
     */
    public void onSendSms(String mobileNo, String validate, String wyErrorCode, String wyErrorMsg) {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("mobile", RSAUtils.encryptByRSA(mobileNo));
        map.put("bizCode", "102101");
        if (!CheckUtil.isEmpty(validate)) {
            map.put("validate", validate);
        }
        if (!CheckUtil.isEmpty(wyErrorCode)) {
            map.put("wy_error_code", wyErrorCode);
        }
        if (!CheckUtil.isEmpty(wyErrorMsg)) {
            map.put("wy_error_msg", wyErrorMsg);
        }
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.POST_LOGIN_SMS_SEND, map);
    }


    //密码登录
    private void passwordWay() {
        mobileNo = getRealMobile();
        passWord = etPassword.getNKeyboardText();
        if (!CheckUtil.checkPhone(PerfectInfoHelper.phone_pattern, mobileNo)) {
            UiUtil.toast("手机号格式错误，请重新输入");
            return;
        }
        if (!cbAgree.isChecked()) {
            showContractDialog();
        } else {
            deviceId = SystemUtils.getDeviceID(this);
            if (CommomUtils.noNeedPhoneStateDialog()) {
                passWordLogin();
            } else {
                requestPermission(aBoolean -> {
                    passWordLogin();
                    //设备权限申请完成后，需要更新下网易设备指纹token
                    WyDeviceIdUtils.getInstance().getWyDeviceIDToken(null);
                }, R.string.permission_phone_statue, Manifest.permission.READ_PHONE_STATE);
            }
        }
    }

    private void showContractDialog() {
        dHelper.showDialog("服务协议及隐私保护", loginNetHelper.setAgreement("为了更好的给您提供服务，并保障您的合法权益，请您阅读并同意以下协议", false), "不同意", "同意", (dialog, which) -> {
            if (which == 2) {
                cbAgree.setChecked(true);
                onViewClicked(tvNext);
            }
            dialog.dismiss();
        });
    }

    /**
     * 密码登录
     */
    private void passWordLogin() {
        if (!CheckUtil.checkPhone(PerfectInfoHelper.phone_pattern, mobileNo)) {
            UiUtil.toast("手机号格式错误，请重新输入");
            return;
        }
        if (YiDunUtils.isSliderOpen()) {
            doYiDun();
        } else {
            requestLogin(null, null, null);
        }
    }

    private void requestLogin(String validate, String wyErrorCode, String wyErrorMsg) {
        if (loginNetHelper == null) {
            loginNetHelper = new LoginNetHelper(this, LoginNetHelper.USER_LOGIN, "BR010", null);
        }
        loginNetHelper.userLogin(mobileNo, passWord, deviceId, "", validate, true, wyErrorCode, wyErrorMsg);
    }

    //忘记密码
    private void forgetPsw() {
        //UMengUtil.commonClickEvent("ForgetPasswordPageClick", "忘记密码", getPageCode());
        // 账户体系忘记密码
        String mobileNo = getRealMobile();
        if (!CheckUtil.checkPhone(PerfectInfoHelper.phone_pattern, mobileNo)) {
            UiUtil.toast("请输入有效的手机号");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("mobileNo", mobileNo);
        ContainerActivity.to(this, CheckPhoneFragment.class.getSimpleName(), bundle);
    }

    //微信登录
    private void weChatLogin() {
        //微信登录
        if (!WxUntil.isReady(this)) {
            UiUtil.toast("请先安装微信应用");
            return;
        }
        if (CheckUtil.isEmpty(SpHelper.getInstance().readMsgFromSp(SpKey.WECHAT_LOGIN_DIALOG_STATE, SpKey.HAS_SHOW_WECHAT_LOGIN_DIALOG))) {
            Map<String, String> map = new HashMap<>();
            String deviceId = SystemUtils.getDeviceID(this);
            if (!CheckUtil.isEmpty(deviceId)) {
                map.put("deviceId", RSAUtils.encryptByRSA(deviceId));
            }
            netHelper.postService(ApiUrl.POST_CHECK_HAS_WECHAT_LOGIN, map);
        } else {
            WxUntil.sendAuthForLogin(this, getIntent().getExtras());
//            if (CommomUtils.noNeedPhoneStateDialog()) {
//                WxUntil.sendAuthForLogin(this, getIntent().getExtras());
//            } else {
//                requestPermission(aBoolean -> {
//                    WxUntil.sendAuthForLogin(this, getIntent().getExtras());
//                    //设备权限申请完成后，需要更新下网易设备指纹token
//                    WyDeviceIdUtils.getInstance().getWyDeviceIDToken(null);
//                }, R.string.permission_phone_statue, Manifest.permission.READ_PHONE_STATE);
//            }
        }
    }

    private boolean isPasswordLogin() {
        return loginWay == LoginNetHelper.USER_LOGIN;
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (url.equals(ApiUrl.POST_CHECK_HAS_WECHAT_LOGIN)) {
            Map<String, String> map = (Map<String, String>) success;
            if (map != null && !TextUtils.isEmpty(map.get("existState"))) {
                if ("Y".equals(map.get("existState"))) {
                    WxUntil.sendAuthForLogin(this, getIntent().getExtras());
//                    if (CommomUtils.noNeedPhoneStateDialog()) {
//                        WxUntil.sendAuthForLogin(this, getIntent().getExtras());
//                    } else {
//                        requestPermission(aBoolean -> {
//                            WxUntil.sendAuthForLogin(this, getIntent().getExtras());
//                            //设备权限申请完成后，需要更新下网易设备指纹token
//                            WyDeviceIdUtils.getInstance().getWyDeviceIDToken(null);
//                        }, R.string.permission_phone_statue, Manifest.permission.READ_PHONE_STATE);
//                    }
                } else {
                    showConfirmDialog();
                    SpHelper.getInstance().saveMsgToSp(SpKey.WECHAT_LOGIN_DIALOG_STATE, SpKey.HAS_SHOW_WECHAT_LOGIN_DIALOG, "Y");
                }
            } else {
                showConfirmDialog();
            }

        }
        if(ApiUrl.POST_LOGIN_SMS_SEND.equals(url)){
            showProgress(false);
            @SuppressWarnings("rawtypes") Map map = (Map) success;
            if (map != null) {
                 captchaId = String.valueOf(map.get("captchaId"));
                 seqNo = String.valueOf(map.get("seqNo"));
                 message = String.valueOf(map.get("message"));

            }
            goSmsCodePage(captchaId,seqNo,message);
        }
    }

    private void showConfirmDialog() {
        showDialog("提示", getString(R.string.wechat_info), "取消", "同意并继续", (dialog, which) -> {
            //如果无权限且 暂不授权，直接返回false
            dialog.dismiss();
            if (which == 2) {
//                if (CommomUtils.noNeedPhoneStateDialog()) {
//                    WxUntil.sendAuthForLogin(this, getIntent().getExtras());
//                } else {
//                    requestPermission(aBoolean -> {
//                        WxUntil.sendAuthForLogin(this, getIntent().getExtras());
//                        //设备权限申请完成后，需要更新下网易设备指纹token
//                        WyDeviceIdUtils.getInstance().getWyDeviceIDToken(null);
//                    }, R.string.permission_phone_statue, Manifest.permission.READ_PHONE_STATE);
//                }
                WxUntil.sendAuthForLogin(this, getIntent().getExtras());
            }
        }).setStandardStyle(2);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onStop() {
        super.onStop();
        if (etPassword != null) {
            etPassword.clearNkeyboard();
        }
    }

    @Override
    public void onCaptchaShow() {

    }

    @Override
    public void onCaptchaVisible() {//滑块弹窗肉眼可见
        showProgress(false);
    }

    @Override
    public void onValidate(String result, String validate, String msg) {
        if (TextUtils.isEmpty(validate)) {
            postErrorMsgToServer("-1", "账号密码登录网易滑块validate为空了", "onValidate()", "短信登录网易滑块validate为空了");
        }
        if(isPasswordLogin()){
            requestLogin(validate, null, null);
        }else if(loginWay == LoginNetHelper.SMS_LOGIN){
            onSendSms(mobileNo, validate, null, null);
        }
    }

    @Override
    public void onError(int i, String s) {
        if(isPasswordLogin()){
            requestLogin(null, String.valueOf(i), s);
        }else if(loginWay == LoginNetHelper.SMS_LOGIN){
            onSendSms(mobileNo, null, String.valueOf(i), s);
        }
        postErrorMsgToServer(i + "", "账号密码登录网易滑块报错了", "onError()", s);
    }

    @Override
    public void onClose(Captcha.CloseType closeType) {

    }

    private void doYiDun() {
        showProgress(true);
        //需要每次都初始化，不然如果连续两个页面都有初始化滑块sdk，再回到第一个页面不重新初始化而执行validate就会阻塞
        YiDunUtils.initAndValidate(this, this);
    }

    private String getRealMobile() {
        CharSequence sequence = "";
        if (!CheckUtil.isEmpty(etMobile) && !CheckUtil.isEmpty(etMobile.getText())) {
            sequence = etMobile.getText().toString().trim();
        }
        String phoneNo = String.valueOf(sequence).replaceAll(" ", "");
        if (phoneNo.contains("*") && !CheckUtil.isEmpty(mobile)) {
            phoneNo = mobile;
        }
        return phoneNo;
    }

    private boolean needHide() {
        CharSequence sequence = "";
        if (!CheckUtil.isEmpty(etMobile) && !CheckUtil.isEmpty(etMobile.getText())) {
            sequence = etMobile.getText().toString().trim();
        }
        String phoneNo = String.valueOf(sequence).replaceAll(" ", "");
        return phoneNo.contains("*");
    }

    //h5携带了        changePhone = Y 则点击X需要关闭所有页面回首页
    private boolean needCloseAll() {
        return "Y".equals(changePhone) || "y".equals(changePhone);
    }

    @Override
    protected String getPageCode() {
        return isPasswordLogin() ? "PasswordLoginPop" : "MessageLoginPop";
    }

    private String getPageNameCn() {
        return isPasswordLogin() ? "账号密码登录弹窗" : "验证码登录弹窗";
    }

    @Override
    protected void onDestroy() {
        RxBus.getInstance().unSubscribe(this);
        super.onDestroy();
        AppApplication.doLoginCallback();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (needCloseAll()) {
            ActivityUntil.finishOthersActivity(MainActivity.class);
        }
    }


    /**
     * 上报异常数据
     *
     * @param code        错误码
     * @param type        类型（哪个sdk）
     * @param errorMethod 问题方法
     * @param msg         详细信息
     */
    public void postErrorMsgToServer(String code, String type, String errorMethod, String msg) {
        if (netHelper != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("code", code);
            map.put("type", type);
            map.put("errorMethod", errorMethod);
            map.put("msg", code + ":" + msg);
            netHelper.postService(ApiUrl.POST_APP_ACTION_LOG, map);
        }

    }
}