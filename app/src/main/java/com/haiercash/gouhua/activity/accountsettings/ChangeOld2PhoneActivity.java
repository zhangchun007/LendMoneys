package com.haiercash.gouhua.activity.accountsettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.LoginUserHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/8/29<br/>
 * 描    述：更改手机号<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ChangeOld2PhoneActivity extends BaseActivity {
    @BindView(R.id.tvSetOK)
    TextView mSet;
    @BindView(R.id.edt_phone)
    EditText mPhone;//手机号
    @BindView(R.id.edt_getcode)
    EditText mGetCode;//验证码
    private String mobile;

    @Override
    protected int getLayout() {
        return R.layout.activity_change_old_phone2;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.update_phone_title);
        mSet.setTypeface(FontCustom.getMediumFont(this));
        mobile = getIntent().getStringExtra("mobile");
        if (CheckUtil.isEmpty(mobile)) {
            mobile = SpHp.getLogin(SpKey.LOGIN_MOBILE);
        }
        CheckUtil.formatPhone344(mPhone);
        mSet.setEnabled(false);
        //加载发送验证码模块
        registerSmsTime(R.id.ttv_get_code).setPhoneEdit(mPhone).setBizCode("TMP_004");
    }


    @Override
    @OnClick({R.id.tvSetOK})
    public void onClick(View v) {
        if (v.getId() == R.id.tvSetOK) {
            requestChangePhone();
        }
    }

    /**
     * 修改绑定手机号
     */
    private void requestChangePhone() {
        String id = SpHp.getLogin(SpKey.LOGIN_USERID);
        if (CheckUtil.isEmpty(id)) {
            UiUtil.toast("账号异常，请退出重试");
            return;
        }
        if (CheckUtil.isEmpty(mobile)) {
            UiUtil.toast("账号异常，请退出重试");
            return;
        }
        if (!CheckUtil.checkPhone(getTextValue())) {
            UiUtil.toast("手机号格式错误 请重新输入");
            return;
        }
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("userId", RSAUtils.encryptByRSA(id));
        map.put("oldMobile", RSAUtils.encryptByRSA(mobile));
        map.put("newMobile", RSAUtils.encryptByRSA(getTextValue()));
        map.put("verifyMobile", RSAUtils.encryptByRSA(getTextValue()));
        map.put("verifyNo", RSAUtils.encryptByRSA(mGetCode.getText().toString().trim()));
        map.put("isRsa", "Y");
        netHelper.putService(ApiUrl.urlChangePhone, map);
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.urlChangePhone.equals(url)) {
            String mobile = getTextValue();
            LoginUserHelper.updateLoginMobileSuccessAndSave(mobile);
            startActivity(new Intent(this, ChangePhoneResultActivity.class));
            showProgress(false);
            finish();
            return;
        }
        showProgress(false);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.urlChangePhone.equals(url)) {
            if (error != null && error.getHead() != null && "0001".equals(error.getHead().getRetFlag())) {
                //新手机号被其他userID占用，且占用此手机号的原账号有在途或未结清
                showProgress(false);
                showDialog(getString(R.string.notice), error.getHead().getRetMsg(), (dialog, which) -> {
                    mPhone.setText(null);
                    mGetCode.setText(null);
                });
                return;
            }
        }
        super.onError(error, url);
    }

    @OnTextChanged(value = {R.id.edt_phone, R.id.edt_getcode}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        mSet.setEnabled(mPhone.length() > 0 && mGetCode.length() > 0);
    }

    private String getTextValue() {
        CharSequence sequence = mPhone.getText().toString().trim();
        return String.valueOf(sequence).replaceAll(" ", "");
    }
}
