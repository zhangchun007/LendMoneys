package com.haiercash.gouhua.activity.fingerprint;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.biometriclib.BiometricUntil;
import com.haiercash.gouhua.utils.UiUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 账户安全中心--指纹密码页面
 */
public class FingerprintPasswordActivity extends BaseActivity {

    @BindView(R.id.sc_open)
    ImageView scOpen;
    private boolean isOpen;
    private BiometricUntil biometricUntil;

    @Override
    protected int getLayout() {
        return R.layout.activity_fingerprint_password;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.safe_setting_login_fingerprint);
        initBiometric();
        isOpen = biometricUntil.isBiometricPromptEnableAndSwitchOpen();
        setChecked();
    }

    private void setChecked() {
        scOpen.setImageResource(isOpen ? R.drawable.togglebutton_on : R.drawable.togglebutton_off);
    }

    private void initBiometric() {
        //每次都要初始化，不然再次调起是无效的
        if (biometricUntil != null) {
            biometricUntil.cancelAuthenticate();
            biometricUntil = null;
        }
        biometricUntil = new BiometricUntil(this, BiometricUntil.BIOMETRIC_SWITCH, new BiometricUntil.BiometricUntilCallBack() {
            @Override
            public void biometricSwitch(boolean isOpening) {

            }

            @Override
            public void onSuccess(boolean isOpenFingerprint) {
                isOpen = isOpenFingerprint;
                setChecked();
                if (!isOpen) {
                    UiUtil.toast("指纹登录已关闭");
                }
            }

            @Override
            public void onFailed(Integer errorCode, String errorReason) {

            }

            @Override
            public void onErrorForMoreFailed() {
                //5次及以上指纹验证失败
                showDialog("指纹验证失败", "验证失败次数过多，请稍后重试～", null);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @OnClick({R.id.tv_content_h5, R.id.sc_open})
    public void viewOnClick(View v) {
        switch (v.getId()) {
            case R.id.tv_content_h5:
                WebSimpleFragment.WebService(mContext, ApiUrl.API_SERVER_URL + "static/gh/serviceAgreementTouchAndFace.html", "指纹与面容ID相关协议", WebSimpleFragment.STYLE_OTHERS);
                break;
            case R.id.sc_open:
                if (isOpen) {
                    showDialog("确认关闭指纹登录吗？", "取消", "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 2) {
                                initBiometric();
                                biometricUntil.openOrCloseFingerprint();
                            }
                        }
                    }).setStandardStyle(3);
                } else {
                    initBiometric();
                    biometricUntil.openOrCloseFingerprint();
                }
                break;
            default:
                break;
        }
    }
}
