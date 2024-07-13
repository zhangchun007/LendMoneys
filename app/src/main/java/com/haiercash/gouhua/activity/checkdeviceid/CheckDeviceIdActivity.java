package com.haiercash.gouhua.activity.checkdeviceid;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.account.CheckPhoneFragment;
import com.haiercash.gouhua.account.CheckRealFourFragment;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.login.LoginNetHelper;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.login.UserInfoBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.widget.NewNKeyBoardTextField;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Limige on 2017-03-07.
 * 解锁账户
 */

public class CheckDeviceIdActivity extends BaseActivity {
    @BindView(R.id.tv_secretMobile)
    TextView tvCheckMobile;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.ll_input_password)
    LinearLayout llInputPassword;  //输入密码框
    @BindView(R.id.et_password)
    NewNKeyBoardTextField etPassword;  //密码
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword; //忘记密码
    @BindView(R.id.bt_commit)
    AppCompatTextView btCommit;
    @BindView(R.id.tv_modify_phone)
    TextView tvModifyPhone; //手机号已停用

    @BindView(R.id.line_psw)
    View line_psw; //手机号已停用
    private UserInfoBean bean;
    private boolean fromSms;
    private boolean isOneKeyLogin;//是否是一键登录
    private String smsState;
    private LoginNetHelper loginNetHelper;
    private static final String NEED_CHECK_REAL_FOUR = "needCheckFour";
    private static final String NEED_CHECK_PASSWORD = "needCheckPassword";
    private String bizCode;


    @Override
    protected int getLayout() {
        return R.layout.activity_checkmobile;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("解锁账户");
        SystemUtils.setWindowSecure(this);
        bean = (UserInfoBean) getIntent().getSerializableExtra("userInfoBean");
        fromSms = getIntent().getBooleanExtra("fromSms", false);
        isOneKeyLogin = getIntent().getBooleanExtra("isOneKeyLogin", false);
        String tvInfo = getText(R.string.text_check_mobile_rem).toString();
        tvCheckMobile.setText(CheckUtil.hidePhoneNumber(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
        if (bean != null) {
            if (fromSms) {
                tvModifyPhone.setVisibility(View.GONE);
                //已实名并且新设备
                if ("Y".equals(bean.getIsRealInfo()) && "Y".equals(bean.getIsNewDevice())) {
                    tvInfo = getText(R.string.text_check_mobile_rem).toString();
                    tvCheckMobile.setVisibility(View.INVISIBLE);
                    smsState = NEED_CHECK_REAL_FOUR;
                    bizCode = "TMP011";
                }
                //已实名并且超过三个月没登录
                else if ("Y".equals(bean.getIsRealInfo()) && "Y".equals(bean.getLongTimeNotLogin())) {
                    tvInfo = getText(R.string.text_check_mobile_rem_longTimeNotLogin).toString();
                    tvCheckMobile.setVisibility(View.INVISIBLE);
                    smsState = NEED_CHECK_REAL_FOUR;
                    bizCode = "TMP010";
                }
                //未实名已设置密码并且新设备
                else if (!"Y".equals(bean.getIsRealInfo()) && "Y".equals(bean.getPwdStatus()) && "Y".equals(bean.getIsNewDevice())) {
                    tvInfo = getText(R.string.text_check_mobile_rem).toString();
                    btCommit.setText(getText(R.string.confirm));
                    btCommit.setEnabled(false);
                    llInputPassword.setVisibility(View.VISIBLE);
                    smsState = NEED_CHECK_PASSWORD;
                }
                //未实名已设置密码并且长时间没登录
                else if (!"Y".equals(bean.getIsRealInfo()) && "Y".equals(bean.getPwdStatus()) && "Y".equals(bean.getLongTimeNotLogin())) {
                    tvInfo = getText(R.string.text_check_mobile_rem_longTimeNotLogin).toString();
                    btCommit.setText(getText(R.string.confirm));
                    btCommit.setEnabled(false);
                    llInputPassword.setVisibility(View.VISIBLE);
                    smsState = NEED_CHECK_PASSWORD;
                }
                //新设备
                else if ("Y".equals(bean.getIsNewDevice())) {
                    tvInfo = getText(R.string.text_check_mobile_rem).toString();
                }
                //超过三个月没登录
                else if ("Y".equals(bean.getLongTimeNotLogin())) {
                    tvInfo = getText(R.string.text_check_mobile_rem_longTimeNotLogin).toString();
                }
            } else {
                tvInfo = ("Y".equals(bean.getIsNewDevice())) ?
                        getText(R.string.text_check_mobile_rem).toString() :
                        getText(R.string.text_check_mobile_rem_longTimeNotLogin).toString();
            }
        }

        tvDesc.setText(tvInfo);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btCommit.setEnabled(!CheckUtil.isEmpty(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addOnFocusChangeLis((v, hasFocus) -> line_psw.setSelected(hasFocus));
    }

    @OnClick({R.id.bt_commit, R.id.tv_modify_phone, R.id.tv_forget_password})
    public void viewOnClick(View view) {
        Intent intent = new Intent();
        intent.putExtras(getIntent());
        if (view.getId() == R.id.bt_commit) {
            UMengUtil.commonClickEvent("ToVerify_Click", "开始验证", getPageCode());
            if (NEED_CHECK_PASSWORD.equals(smsState)) {
                String passWord = etPassword.getNKeyboardText();
                if (loginNetHelper == null) {
                    loginNetHelper = new LoginNetHelper(this, LoginNetHelper.USER_LOGIN, "BR010", null);
                }
                loginNetHelper.userLogin(SpHp.getLogin(SpKey.LOGIN_MOBILE), passWord, SystemUtils.getDeviceID(this), "Y", null, false, null, null);
            } else if (NEED_CHECK_REAL_FOUR.equals(smsState)) {
                Bundle extra = new Bundle();
                extra.putString("doType", "ValidateUser");
                extra.putString("bizCode", bizCode);
                ContainerActivity.toForResult(mContext, CheckRealFourFragment.ID, extra, 0);
            } else {
                intent.setClass(this, ChangeDeviceIdActivity.class);
                intent.putExtra("userInfoBean", bean);
                intent.putExtra("isOneKeyLogin", isOneKeyLogin);
                startActivityForResult(intent, 98);
            }
        } else if (view.getId() == R.id.tv_modify_phone) {
            UMengUtil.commonClickEvent("IfTelNotUsePleaseModify_Click", "若手机号已停用，请点此修改", getPageCode());
            if (bean != null && "Y".equals(bean.getIsRealInfo())) {
                Bundle extra = new Bundle();
                extra.putString("doType", "ValidateUser");
                ContainerActivity.toForResult(mContext, CheckRealFourFragment.ID, extra, 98);
            } else {
                showDialog("如手机号不再使用，请使用可用手机号登录", "取消", "去登录", (dialog, which) -> {
                    if (which == 2) {
                        RxBus.getInstance().post(new ActionEvent(ActionEvent.RegisterFragmentClearPhoneNum));
                        LoginSelectHelper.staticToGeneralLogin();
                    }
                });
            }
        } else if (view.getId() == R.id.tv_forget_password) {
            Bundle bundle = new Bundle();
            bundle.putString("mobileNo", SpHp.getLogin(SpKey.LOGIN_MOBILE));
            ContainerActivity.to(this, CheckPhoneFragment.class.getSimpleName(), bundle);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (98 == requestCode && 1 == resultCode) {
            setResult(1,data);
            finish();
        } else if (0 == requestCode && 1 == resultCode) {  //实名验证成功后
            setResult(1, data);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            setResult(10086);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected String getPageCode() {
        return "UnlockAccountPage";
    }
}
