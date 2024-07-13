package com.haiercash.gouhua.activity.accountsettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.fingerprint.FingerprintPasswordActivity;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.biometriclib.BiometricUntil;
import com.haiercash.gouhua.databinding.ActivitySafetySettingBinding;
import com.haiercash.gouhua.fragments.mine.CheckCertNoFragment;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.tplibrary.livedetect.FaceCheckActivity;
import com.haiercash.gouhua.uihelper.CallPhoneNumberHelper;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/8/29<br/>
 * 描    述：账号安全设置<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.ACTIVITY_SAFETY_SETTING)
public class SafetySettingActivity extends BaseActivity implements View.OnClickListener {
    private ActivitySafetySettingBinding mSafeSettingBinding;
    private boolean hasSetLoginPwd;//有密码
    private boolean hasSetPayPwd;//有交易密码
    private BiometricUntil biometricUntil;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return mSafeSettingBinding = ActivitySafetySettingBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.safe_setting_title);
        biometricUntil = new BiometricUntil(this, null);
        updateUiData();
        mSafeSettingBinding.llLoginPhone.setOnClickListener(this);
        mSafeSettingBinding.llRealName.setOnClickListener(this);
        mSafeSettingBinding.llOtherBind.setOnClickListener(this);
        mSafeSettingBinding.llLoginPwd.setOnClickListener(this);
        mSafeSettingBinding.llPayPwd.setOnClickListener(this);
        mSafeSettingBinding.llLoginGesture.setOnClickListener(this);
        mSafeSettingBinding.llLoginFingerprint.setOnClickListener(this);
        mSafeSettingBinding.llAccountAppeal.setOnClickListener(this);
        mSafeSettingBinding.llCancellation.setOnClickListener(this);
    }

    private void updateUiData() {
        boolean cancellationSwitchOpen = "Y".equals(SpHp.getOther(SpKey.OTHER_CANCELLATION_SWITCH, "N"));
        mSafeSettingBinding.llCancellation.setVisibility(cancellationSwitchOpen ? View.VISIBLE : View.GONE);
        mSafeSettingBinding.tvLoginPhone.setText(CheckUtil.hidePhoneNumber(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
        hasSetLoginPwd = "Y".equals(SpHp.getLogin(SpKey.LOGIN_PASSWORD_STATUS));
        mSafeSettingBinding.tvLoginPwd.setText(hasSetLoginPwd ? R.string.safe_setting_update_login_pwd : R.string.safe_setting_set_login_pwd);
        if (CommomUtils.isRealName()) {
            mSafeSettingBinding.lineRealName.lineRoot.setVisibility(View.VISIBLE);
            mSafeSettingBinding.llRealName.setVisibility(View.VISIBLE);
            mSafeSettingBinding.tvPayPwdTitle.setVisibility(View.VISIBLE);
            mSafeSettingBinding.llPayPwd.setVisibility(View.VISIBLE);
            mSafeSettingBinding.llAccountAppeal.setVisibility(View.VISIBLE);
            mSafeSettingBinding.tvOtherTitle.setVisibility(View.VISIBLE);
            mSafeSettingBinding.lineCancellation.lineRoot.setVisibility(cancellationSwitchOpen ? View.VISIBLE : View.GONE);
        } else {
            mSafeSettingBinding.lineRealName.lineRoot.setVisibility(View.GONE);
            mSafeSettingBinding.llRealName.setVisibility(View.GONE);
            mSafeSettingBinding.tvPayPwdTitle.setVisibility(View.GONE);
            mSafeSettingBinding.llPayPwd.setVisibility(View.GONE);
            mSafeSettingBinding.llAccountAppeal.setVisibility(View.GONE);
            mSafeSettingBinding.tvOtherTitle.setVisibility(cancellationSwitchOpen ? View.VISIBLE : View.GONE);
            mSafeSettingBinding.lineCancellation.lineRoot.setVisibility(View.GONE);
        }
        mSafeSettingBinding.tvGestureStatus.setText(LoginSelectHelper.hasSetGesture() ? R.string.safe_setting_login_quick_status_yes : R.string.safe_setting_login_quick_status_no);
        boolean fingerprintOpen = biometricUntil.isBiometricPromptEnableAndSwitchOpen();
        mSafeSettingBinding.tvFingerprintStatus.setText(fingerprintOpen ? R.string.safe_setting_login_quick_status_yes : R.string.safe_setting_login_quick_status_no);
        boolean fingerprintIsHardwareDetected = biometricUntil.isHardwareDetected();
        mSafeSettingBinding.lineLoginFingerprint.lineRoot.setVisibility(fingerprintIsHardwareDetected ? View.VISIBLE : View.GONE);
        mSafeSettingBinding.llLoginFingerprint.setVisibility(fingerprintIsHardwareDetected ? View.VISIBLE : View.GONE);
        hasSetPayPwd = "Y".equals(SpHelper.getInstance().readMsgFromSp(SpKey.STATE, SpKey.STATE_HASPAYPAS));
        mSafeSettingBinding.tvPayPwd.setText(hasSetPayPwd ? R.string.safe_setting_pay_pwd_update : R.string.safe_setting_pay_pwd_set);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUiData();
    }

    @Override
    public void onClick(View v) {
        if (v == mSafeSettingBinding.llLoginPhone) {//修改手机号
            CommomUtils.clickToUpdateBindMobile(this);
        } else if (v == mSafeSettingBinding.llRealName) {//实名信息
            startActivity(new Intent(this, RealNameInfoActivity.class));
        } else if (v == mSafeSettingBinding.llOtherBind) {//第三方账号绑定
            startActivity(new Intent(this, OtherBindActivity.class));
        } else if (v == mSafeSettingBinding.llLoginPwd) {//修改/设置登录密码
            Intent intent = new Intent();
            if (hasSetLoginPwd) {
                intent.setClass(this, VerifyPwdActivity.class);
                intent.putExtra("title", "修改密码");
                intent.putExtra(VerifyPwdActivity.PAGE_KEY, ChangeNewLoginPasswordActivity.class);
                intent.putExtra(ChangeNewLoginPasswordActivity.PWD_TAG, "XGDLMM");
                startActivity(intent);
            } else {
                intent.setClass(this, SetLoginPwdVerifyMobileActivity.class);
                startActivity(intent);
            }
            startInTransition();
        } else if (v == mSafeSettingBinding.llLoginGesture) {//手势密码
            startActivity(new Intent(this, GestureManagerActivity.class));
        } else if (v == mSafeSettingBinding.llLoginFingerprint) {//指纹密码
            startActivity(new Intent(this, FingerprintPasswordActivity.class));
        } else if (v == mSafeSettingBinding.llPayPwd) {//设置/修改交易密码
            if (hasSetPayPwd) {
                Intent intent = new Intent();
                intent.setClass(this, VerifyPwdActivity.class);
                intent.putExtra("title", "验证交易密码");
                intent.putExtra(VerifyPwdActivity.PAGE_KEY, SetTransactionPwdActivity.class);
                intent.putExtra(SetTransactionPwdActivity.TAG, "XGJYMM");
                startActivity(intent);
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
                bundle.putSerializable(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                bundle.putString(SetTransactionPwdActivity.TAG, "SZJYMM");
                bundle.putString(FaceCheckActivity.FROM, "SZJYMM");
                ContainerActivity.to(this, CheckCertNoFragment.class.getSimpleName(), bundle);
            }
        } else if (v == mSafeSettingBinding.llAccountAppeal) {//账户申诉
            //账号申诉前置校验流程，校验是否有在途业务和未结清借据
            showProgress(true);
            Map<String, String> map = new HashMap<>();
            map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
            netHelper.postService(ApiUrl.URL_ACCOUNT_APPEAL_VERIFY, map);
        } else if (v == mSafeSettingBinding.llCancellation) {//注销账户
            String cNo = SpHp.getUser(SpKey.USER_CUSTNO);
            if (!CheckUtil.isEmpty(cNo)) {
                showProgress(true);
                Map<String, String> map = new HashMap<>();
                map.put("custNo", EncryptUtil.simpleEncrypt(cNo));
                netHelper.getService(ApiUrl.URL_LOGIN_OUT_VERIFY, map);
            } else {
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_CANCELLATION).navigation();
            }
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.URL_LOGIN_OUT_VERIFY.equals(url)) {
            HashMap map = JsonUtils.fromJson(success, HashMap.class);
            if (map != null && map.containsKey("status") && "Y".equals(map.get("status"))) {
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_CANCELLATION).navigation();
                showProgress(false);
            } else {
                showProgress(false);
                showDialog("您存在未结清借据，暂不能办理账号注销");
            }
        } else if (ApiUrl.URL_ACCOUNT_APPEAL_VERIFY.equals(url)) {
            //检验能否进行账户申诉
            Map<String,String> map = JsonUtils.getRequest(success);
            if (map.containsKey("errorMsg") && !CheckUtil.isEmpty(map.get("errorMsg"))) {
                showProgress(false);
                CallPhoneNumberHelper.callServiceNumber(this,
                        getString(R.string.safe_setting_hint),
                        map.get("errorMsg"),
                        getString(R.string.safe_setting_call),
                        getString(R.string.safe_setting_i_know), 3);
            } else {
                startActivity(new Intent(this, AppealActivity.class));
                showProgress(false);
            }
        } else {
            showProgress(false);
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
    }

    @Override
    protected void onDestroy() {
        if (biometricUntil != null) {
            biometricUntil.cancelAuthenticate();
            biometricUntil = null;
        }
        super.onDestroy();
    }
}
