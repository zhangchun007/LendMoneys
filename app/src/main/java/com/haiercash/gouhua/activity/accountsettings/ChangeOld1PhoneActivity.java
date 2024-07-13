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
 * 描    述：原手机号仍然使用<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ChangeOld1PhoneActivity extends BaseActivity {
    @BindView(R.id.tvSetOK)
    TextView mSet;
    @BindView(R.id.tv_zhanshi)
    TextView tvZhanshi;

    private String numPhone;//手机号
    @BindView(R.id.edt_getcode)
    EditText edtGetCode;

    @Override
    protected int getLayout() {
        return R.layout.activity_change_old_phone1;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("验证手机号");
        mSet.setTypeface(FontCustom.getMediumFont(this));
        mSet.setEnabled(false);
        numPhone = SpHp.getLogin(SpKey.LOGIN_MOBILE);//用户手机号
        tvZhanshi.setText(UiUtil.getStr("验证码将发送至", CheckUtil.getPhone(numPhone)));
        //加载发送验证码模块
        registerSmsTime(R.id.ttv_get_code).setPhoneNum(numPhone).setBizCode("TMP_004");
    }

    @Override
    @OnClick({R.id.tv_remark, R.id.tvSetOK})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSetOK:
                showProgress(true);
                Map<String, String> map = new HashMap<>();
                map.put("phone", RSAUtils.encryptByRSA(numPhone));
                map.put("verifyNo", RSAUtils.encryptByRSA(edtGetCode.getText().toString().trim()));
                map.put("isRsa", "Y");
                netHelper.postService(ApiUrl.url_yanzhengma_xiaoyan, map);
                break;
            case R.id.tv_remark:
                showDialog(null, "1. 请确认手机是否欠费或停机\n2. 可能网络繁忙，请耐心等待\n3. 手机号已不再使用，请修改手机号\n4. 请检查是否被安全管家拦截", "知道了", null, null).setButtonTextColor(1, R.color.colorPrimary);
                break;
        }
    }

    @OnTextChanged(value = R.id.edt_getcode, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        mSet.setEnabled(edtGetCode.length() > 0);
    }

    @Override
    public void onSuccess(Object success, String url) {
        showProgress(false);
        startActivity(new Intent(this, ChangeOld2PhoneActivity.class));
        presenter.stopTime();
        finish();
    }

    @Override
    public void onError(BasicResponse error, String url) {
        try {
            showProgress(false);
            UiUtil.toast(error.getHead().getRetMsg());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
