package com.haiercash.gouhua.activity.accountsettings;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.SoftKeyBoardListenerUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.OnInputListener;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.databinding.ActivitySetLoginPwdVerifyMobileBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.sms.SmsTimePresenter;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

import static com.haiercash.gouhua.activity.accountsettings.ChangeNewLoginPasswordActivity.PWD_TAG;

/**
 * 设置密码-验证手机号
 */
public class SetLoginPwdVerifyMobileActivity extends BaseActivity implements OnInputListener, SmsTimePresenter.OnSmsSendListener {

    private ActivitySetLoginPwdVerifyMobileBinding mBinding;
    private String mMobile;
    private boolean isCheckError = false;
    private String tag;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return mBinding = ActivitySetLoginPwdVerifyMobileBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        tag = getIntent().getStringExtra(PWD_TAG);
        if ("WJDLMM".equals(tag)) {
            setTitle("忘记密码");
        } else {
            setTitle(getString(R.string.set_login_pwd_title));
        }
        mBinding.tvVerifyMobile.setTypeface(FontCustom.getMediumFont(this));
        mMobile = SpHp.getLogin(SpKey.LOGIN_MOBILE);
        mBinding.tvVerifyMobileDetail.setText(getResources().getString(R.string.set_login_pwd_verify_mobile_detail, CheckUtil.getPhone(mMobile)));
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.icon_error_set_pwd);
        if (drawable != null) {
            drawable.setBounds(0, 0, UiUtil.dip2px(this, 14), UiUtil.dip2px(this, 15));
        }
        mBinding.tvVerifyMobileError.setCompoundDrawablesRelative(drawable, null, null, null);
        mBinding.splitEdit.setOnInputListener(this);
        SoftKeyBoardListenerUtil.setListener(this, new SoftKeyBoardListenerUtil.MySoftKeyboardListener(mBinding.splitEdit) {
            @Override
            public void keyBoardHide(int height) {
                super.keyBoardHide(height);
                mBinding.splitEdit.setShowCursorAndRedraw(false);
            }

            @Override
            public void keyBoardShow(int height) {
                super.keyBoardShow(height);
                mBinding.splitEdit.setShowCursorAndRedraw(true);
            }
        });
        mBinding.tvRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(null, "1. 请确认手机是否欠费或停机\n2. 可能网络繁忙，请耐心等待\n3. 手机号已不再使用，请修改手机号\n4. 请检查是否被安全管家拦截", "知道了", null, null).setButtonTextColor(1, R.color.colorPrimary);
            }
        });

        //加载发送验证码模块
        registerSmsTime(R.id.tv_verify_mobile_get).setPhoneNum(mMobile).setBizCode("TMP_005").setOnSend(this);
        mBinding.tvVerifyMobileGet.callOnClick();
    }

    private void setSmsEditStyle(boolean isError, String errorText) {
        isCheckError = isError;
        if (isError) {
            mBinding.splitEdit.setUnderlineErrorColor(0xFFFF5151, 0xFFFF5151);
            mBinding.tvVerifyMobileError.setText(errorText);
            mBinding.tvVerifyMobileError.setVisibility(View.VISIBLE);
        } else {
            mBinding.splitEdit.setUnderlineErrorColor(0xFFE8EAEF, 0xFF606166);
            mBinding.tvVerifyMobileError.setVisibility(View.GONE);
            mBinding.splitEdit.setText("");//错误后一键清空
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backOutTransition();
    }

    private void requestVerifyMobile() {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("phone", RSAUtils.encryptByRSA(mMobile));//手机号
        map.put("verifyNo", RSAUtils.encryptByRSA(mBinding.splitEdit.getText().toString()));//短信验证码
        map.put("needSMSToken", "1");//验证成功之后返回“短信码验证成功token”
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.url_yanzhengma_xiaoyan, map);
    }

    @Override
    public void onInputFinished(String content) {
        //输入框6个数字输入完成后调验证接口
        requestVerifyMobile();
    }

    @Override
    public void onInputChanged(String text) {
        if (isCheckError) {
            setSmsEditStyle(false, null);
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        if (ApiUrl.url_yanzhengma_xiaoyan.equals(url)) {
            showProgress(false);
            Map map1 = (Map) success;
            if (map1 != null && map1.containsKey("smsToken")) {
                Intent intent = new Intent(this, ChangeNewLoginPasswordActivity.class);
                if (!CheckUtil.isEmpty(tag)) {
                    intent.putExtra(PWD_TAG, tag);
                } else {
                    intent.putExtra(PWD_TAG, "SZDLMM");
                }
                intent.putExtra("mobile", mMobile);
                intent.putExtra("smsToken", String.valueOf(map1.get("smsToken")));
                startActivity(intent);
                //startInTransition();
                finish();
            } else {
                setSmsEditStyle(true, "服务器开小差了，请稍后再试");
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.url_yanzhengma_xiaoyan.equals(url)) {
            showProgress(false);
            setSmsEditStyle(true, error != null && error.getHead() != null ? error.getHead().getRetMsg() : null);
        } else {
            super.onError(error, url);
        }
    }

    @Override
    public void onSendResult(Object success, boolean flag) {
        if (flag) {//验证码发送成功打开软键盘
            KeyBordUntil.showKeyBord(this, mBinding.splitEdit);
        }
    }
}