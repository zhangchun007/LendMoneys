package com.haiercash.gouhua.activity.personalinfor;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.edu.PerfectInfoHelper;
import com.haiercash.gouhua.activity.login.LoginNetHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.register.IsRegisterBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.sms.SmsTimeView;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.RegisterAgreementPopupWindow;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.widget.DelEditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/6/10<br/>
 * 描    述：老用户绑定微信或新用户绑定微信<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.ACTIVITY_SOCIAL_BIND)
public class SocialBindActivity extends BaseActivity {
    @BindView(R.id.ll_root)
    View llRoot;
    @BindView(R.id.ll_parent)
    View llParent;
    @BindView(R.id.et_input_mobile_no)
    DelEditText etMobileNo;
    @BindView(R.id.det_input_verify)
    DelEditText etVerify;
    @BindView(R.id.ll_agreement)
    LinearLayout llAgreement;
    @BindView(R.id.tv_agreement)
    TextView tvGreen;
    @BindView(R.id.cb_agree)
    CheckBox cbAgree;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.stv_sms)
    SmsTimeView smsTimeView;

    private RegisterAgreementPopupWindow mPopWind;
    private String openId;
    //true 已注册的账号执行绑定操作；false 未注册过，执行账号注册且微信绑定
    private boolean isRegister = false;
    private LoginNetHelper loginNetHelper;
    private boolean isSend;  //是否发送过验证码
    private boolean canChangeCodeView = true;


    @Override
    protected int getLayout() {
        return R.layout.activity_social_bind;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("微信登录");
        openId = getIntent().getStringExtra("openId");
        if (CheckUtil.isEmpty(openId)) {
            UiUtil.toast("微信授权失败，请重新授权登录");
            finish();
            return;
        }
        if (loginNetHelper == null) {
            loginNetHelper = new LoginNetHelper(this, LoginNetHelper.WX_LOGIN, "BR015", getPageCode());
        }
        CheckUtil.formatPhone344(etMobileNo);
        if (!CheckUtil.isEmpty(LoginNetHelper.agreementListBeanList)) {
            tvGreen.setText(loginNetHelper.setAgreement("我已阅读并同意够花APP", LoginNetHelper.agreementListBeanList));
        } else {
            loginNetHelper.getAgreementList(tvGreen, "我已阅读并同意够花APP");
        }
        tvGreen.setMovementMethod(LinkMovementMethod.getInstance());
        tvGreen.setHighlightColor(Color.TRANSPARENT);

        etMobileNo.addFocusChangeListener(onFocusChangeListener);
        etVerify.addFocusChangeListener(onFocusChangeListener);
        setSmsTimeView(false);
        registerSmsTime(R.id.stv_sms).setPhoneEdit(etMobileNo).setBizCode("TMP_009").setOnCheckListener(this::checkMobile).setOnClick(() -> {
            KeyBordUntil.hideKeyBord2(mContext);
            if (!CheckUtil.checkPhone(PerfectInfoHelper.phone_pattern, getTextValue())) {
                UiUtil.toast("手机号格式错误，请重新输入");
                return;
            }
            try {
                showProgress(true);
                isSend = true;
                Map<String, String> map = new HashMap<>();
                map.put("mobile", URLEncoder.encode(RSAUtils.encryptByRSA(getTextValue()), "UTF-8"));
                netHelper.getService(ApiUrl.url_zhanghaochaxun_get, map, IsRegisterBean.class, true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }).setOnSend((obj, flag) -> {
            if (flag) {
                etVerify.setFocusable(true);
                etVerify.setFocusableInTouchMode(true);
                etVerify.requestFocus();
                KeyBordUntil.openKeyBord(mContext);
                canChangeCodeView = false;
            } else {
                canChangeCodeView = true;
            }
        }).setSmsFinish(() -> canChangeCodeView = true);
    }

    private void setSmsTimeView(boolean enable) {
        smsTimeView.setEnabled(enable);
        if (enable) {
            smsTimeView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {
            smsTimeView.setTextColor(ContextCompat.getColor(this, R.color.text_hint));
        }
    }


    @OnTextChanged(value = {R.id.det_input_verify, R.id.et_input_mobile_no}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        setSmsTimeView(canChangeCodeView && etMobileNo.getInputText().replace(" ", "").length() >= 11);

        btnNext.setEnabled(!CheckUtil.isEmpty(etMobileNo) && etMobileNo.getInputText().length() >= 11 && !CheckUtil.isEmpty(etVerify) && etVerify.getInputText().length() >= 1);

    }

    @OnCheckedChanged({R.id.cb_agree})
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        if (view.getId() == R.id.cb_agree) {
            afterTextChanged();
            KeyBordUntil.hideKeyBord2(this);
        }
    }

    @OnClick({R.id.btn_next})
    public void viewOnClick(View v) {
        KeyBordUntil.hideKeyBord2(this);
        String verifyNo = etVerify.getInputText();
        if (!checkMobile()) {
            return;
        }
        if (!isSend) {
            UiUtil.toast("请先获取验证码");
            return;
        }
        if (CheckUtil.isEmpty(verifyNo)) {
            UiUtil.toast("请输入验证码");
            return;
        }
        if (!isRegister && cbAgree.getVisibility() == View.VISIBLE) {
            if (!cbAgree.isChecked()) {
                dHelper.showDialog("服务协议及隐私保护", loginNetHelper.setAgreement("为了更好的给您提供服务，并保障您的合法权益，请您阅读并同意以下协议"), "不同意", "同意", (dialog, which) -> {
                    if (which == 2) {
                        cbAgree.setChecked(true);
                        viewOnClick(btnNext);
                    }
                    dialog.dismiss();
                });
                return;
            }
        }
        //需要申请设备权限弹框
        if (!CommomUtils.hasApplyPhoneStatePermission() || !CommomUtils.noNeedPhoneStateDialog()) {
            applyPhoneStatePermission();
            return;
        }
        if (loginNetHelper == null) {
            loginNetHelper = new LoginNetHelper(this, LoginNetHelper.WX_LOGIN, "BR015", getPageCode());
        }
        loginNetHelper.socialBind(isRegister, getTextValue(), verifyNo, openId);
    }

    private boolean checkMobile() {
        if (CheckUtil.isEmpty(getTextValue())) {
            UiUtil.toast("手机号不能为空");
            return false;
        }
        if (!CheckUtil.checkPhone(PerfectInfoHelper.phone_pattern, getTextValue())) {
            UiUtil.toast("手机号格式错误，请重新输入");
            return false;
        }
        return true;
    }


    private String getTextValue() {
        CharSequence sequence = etMobileNo.getText();
        return String.valueOf(sequence).replaceAll(" ", "");
    }

    @Override
    public void onSuccess(Object success, String url) {
        showProgress(false);
        if (ApiUrl.url_zhanghaochaxun_get.equals(url)) {
            IsRegisterBean bean = (IsRegisterBean) success;
            if (bean != null) {
                String isRegister = bean.isRegister;
                if (isRegister.contains("-")) {
                    int index = isRegister.indexOf("-") + 1;
                    bean.isRegister = isRegister.substring(index, index + 1);
                }
                this.isRegister = !"N".equals(bean.getIsRegister());
                if (this.isRegister) {
                    llAgreement.setVisibility(View.GONE);
                } else {
                    llAgreement.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        showProgress(false);
        if (ApiUrl.url_zhanghaochaxun_get.equals(url)) {
            showDialog("服务器开小差了，请稍后再试", "取消", "确定", (dialog, which) -> {
                if (which == 2) {
                    showProgress(true);
                    Map<String, String> map = new HashMap<>();
                    map.put("mobile", RSAUtils.encryptByRSA(getTextValue()));
                    netHelper.getService(ApiUrl.url_zhanghaochaxun_get, map, IsRegisterBean.class, true);
                } else {
                    finish();
                }
            });
        } else {
            super.onError(error, url);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginNetHelper.REQUEST_CODE) {
            if (resultCode == 1) {
                //判断是否设置过手势密码
                AppApplication.userid = SpHp.getLogin(SpKey.LOGIN_USERID);
                loginNetHelper.checkGesturePwd();
            } else {//设备验证失败或者取消 默认用户没登录成功  清除所有缓存数据
                CommomUtils.clearSp();
            }
        }

    }


    private View.OnFocusChangeListener onFocusChangeListener = (v, hasFocus) -> {
        if (v == etMobileNo && !hasFocus) {
            if (!CheckUtil.isEmpty(getTextValue()) && getTextValue().length() == 11) {
                Map<String, Object> map = new HashMap<>();
                map.put("lose_focus_tel", EncryptUtil.base64(getTextValue()));
                UMengUtil.onEventObject("TelInputLoseFocus", map, getPageCode());
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (loginNetHelper != null) {
            loginNetHelper.onDestroy();
        }
        super.onDestroy();
        AppApplication.doLoginCallback();
    }

    @Override
    protected String getPageCode() {
        return "WebCatBindPage";
    }


    /**
     * 申请设备权限
     */
    private void applyPhoneStatePermission() {
        requestPermission(aBoolean -> {
            //保存存储设备权限状态
            SpHelper.getInstance().saveMsgToSp(SpKey.PERMISSION_STATE, Manifest.permission.READ_PHONE_STATE, "Y");
            Logger.e("PERMISSION_STATE--3");
            //保存申请存储设备权的时间
            SpHelper.getInstance().saveMsgToSp(SpKey.READ_PHONE_STATE, SpKey.LAST_SHOW_DIALOG_TIME, TimeUtil.calendarToString());
            viewOnClick(btnNext);
        }, R.string.permission_phone_statue, Manifest.permission.READ_PHONE_STATE);
    }
}
