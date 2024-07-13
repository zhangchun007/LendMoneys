package com.haiercash.gouhua.fragments.mine;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.accountsettings.ChangeNewLoginPasswordActivity;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.FindLoginPwd2UpdateBean;
import com.haiercash.gouhua.sms.SmsTimeView;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.widget.NewNKeyBoardTextField;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/8/31<br/>
 * 描    述：通过短信修改登录密码<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ResetPasswordForSms extends BaseFragment {
    @BindView(R.id.tvSetOK)
    TextView tvSetOK;
    @BindView(R.id.tvMobile)
    TextView tvMobile;
    @BindView(R.id.smscode)
    EditText mEditTextSmscode;
    @BindView(R.id.resend)
    SmsTimeView vResend;
    @BindView(R.id.password)
    NewNKeyBoardTextField mEditTextPassword;
    @BindView(R.id.password2)
    NewNKeyBoardTextField mEditTextPassword2;

    private String mobileNo;
    private String userCardName;
    private String userIdCard;
    private String custNo;
    private Bundle mArguments;

    public static ResetPasswordForSms newInstance(Bundle bd) {
        final ResetPasswordForSms f = new ResetPasswordForSms();
        if (bd != null) {
            f.setArguments(bd);
        }
        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.a_resetpasswordforsms;
    }

    @Override
    protected void initEventAndData() {
        SystemUtils.setWindowSecure(mActivity);
        mActivity.setTitle("设置登录密码");
        mArguments = getArguments();
        if (mArguments == null) {
            UiUtil.toast("账号异常，请退出重试");
            return;
        }
        mobileNo = mArguments.getString("mobileNo");
        userCardName = mArguments.getString("userCardName");
        custNo = mArguments.getString("custNo");
        userIdCard = mArguments.getString("userIdCard");
        tvMobile.setText(UiUtil.getStr("验证码将发送至", CheckUtil.getPhone(mobileNo)));
        mEditTextPassword.setEditClearIcon(true);
        mEditTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mEditTextPassword2.setEditClearIcon(true);
        mEditTextPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
        tvSetOK.setTypeface(FontCustom.getMediumFont(mActivity));
        //加载发送验证码模块
        registerSmsTime(vResend).setPhoneNum(mobileNo).setBizCode("TMP_005");
    }

    @OnCheckedChanged(R.id.passwordCheckBox)
    public void onCheckedChanged(boolean isChecked) {
        if (isChecked) {
            mEditTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEditTextPassword2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            mEditTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEditTextPassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        mEditTextPassword2.setSelection(mEditTextPassword2.getNKeyboardText().length());
    }

    public void submit() {
        String smscode = mEditTextSmscode.getText().toString();
        String password = mEditTextPassword.getNKeyboardText();
        String password2 = mEditTextPassword2.getNKeyboardText();
        if (CheckUtil.isEmpty(smscode)) {
            UiUtil.toast("验证码不能为空");
            return;
        }
        if (CheckUtil.isEmpty(password)) {
            UiUtil.toast("密码不能为空");
            return;
        }
        if (CheckUtil.isEmpty(password2)) {
            UiUtil.toast("确认密码不能为空");
            return;
        }
        if (!CheckUtil.checkPassword(password) || !CheckUtil.checkPassword(password2)) {
            UiUtil.toast("请输入8-20位字母和数字的组合");
            return;
        }
        if (!password.equals(password2)) {
            UiUtil.toast("两次设置密码不一致，请重新输入");
            return;
        }
        FindLoginPwd2UpdateBean map = new FindLoginPwd2UpdateBean();
        if (!CheckUtil.isEmpty(userCardName) && !CheckUtil.isEmpty(userIdCard) && !CheckUtil.isEmpty(custNo)) { //已实名重置新密码
            map.setCustNo(RSAUtils.encryptByRSA(custNo));
            map.setCustName(RSAUtils.encryptByRSA(userCardName));
            map.setIdNo(RSAUtils.encryptByRSA(userIdCard));
        }
        map.setUserId(RSAUtils.encryptByRSA(mobileNo));
        map.setNewPassword(password);
        map.setVerifyMobile(RSAUtils.encryptByRSA(mobileNo));
        map.setVerifyNo(RSAUtils.encryptByRSA(smscode));
        map.setDeviceId(RSAUtils.encryptByRSA(SystemUtils.getDeviceID(mActivity)));
        map.setIsRsa("Y");
        showProgress(true);
        netHelper.postService(ApiUrl.url_findLoginPwd2Update, map);
    }

    @OnClick({R.id.tvSetOK, R.id.tv_no_get_sms})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvSetOK:
                submit();
                break;
            case R.id.tv_no_get_sms:
                showDialog(null, "1. 请确认手机是否欠费或停机\n2. 可能网络繁忙，请耐心等待\n3. 手机号已不再使用，请修改手机号\n4. 请检查是否被安全管家拦截", "知道了", null, null).setButtonTextColor(1, R.color.colorPrimary);
                break;
        }
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (ApiUrl.url_findLoginPwd2Update.equals(flag)) {
            showProgress(false);
            UiUtil.toast("设置成功");
            RiskInfoUtils.updateRiskBro09Or06(true, mArguments.getString(ChangeNewLoginPasswordActivity.PWD_TAG));
            CommomUtils.clearSp();
            LoginSelectHelper.closeExceptMainAndToLogin(mActivity);
        }
    }

    @Override
    public void onError(final BasicResponse error, String url) {
        if (ApiUrl.url_findLoginPwd2Update.equals(url)) {
            RiskInfoUtils.updateRiskBro09Or06(false, mArguments.getString(ChangeNewLoginPasswordActivity.PWD_TAG));
            showProgress(false);
            try {
                UiUtil.toast(error.getHead().getRetMsg());
                if ("A0101".equals(error.getHead().getRetFlag())) {
                    mActivity.finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @OnTextChanged(value = {R.id.password, R.id.password2, R.id.smscode}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        tvSetOK.setEnabled(mEditTextSmscode.length() > 0 && mEditTextPassword.getNKeyboardText().length() > 0 && mEditTextPassword2.getNKeyboardText().length() > 0);
    }

    @Override
    public void onDestroyView() {
        if (mEditTextPassword != null) {
            mEditTextPassword.destoryNKeyboard();
        }
        if (mEditTextPassword2 != null) {
            mEditTextPassword2.destoryNKeyboard();
        }
        super.onDestroyView();
    }
}
