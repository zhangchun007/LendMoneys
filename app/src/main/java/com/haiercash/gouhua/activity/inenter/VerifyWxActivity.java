package com.haiercash.gouhua.activity.inenter;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.wxapi.WxUntil;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/6/12<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.ACTIVITY_VERIFY_WX)
public class VerifyWxActivity extends BaseVerifyActivity {

    @Override
    protected int getContentResId() {
        return R.layout.activity_verify_wx;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        WxUntil.mBundle = null;
        goneAllView();
        findViewById(R.id.iv_wx_login).setOnClickListener(this);
        findViewById(R.id.iv_wx_phone_login).setOnClickListener(this);
        WxUntil.regToWx(this, false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_wx_phone_login) {
            LoginSelectHelper.staticToGeneralLogin();
        } else if (v.getId() == R.id.iv_wx_login) {
            if (!WxUntil.isReady(this)) {
                UiUtil.toast("请先安装微信应用");
                return;
            }
            if (CommomUtils.noNeedPhoneStateDialog()) {
                WxUntil.sendAuthForLogin(mContext, getIntent().getExtras());
            } else {
                requestPermission(aBoolean -> WxUntil.sendAuthForLogin(mContext, getIntent().getExtras()), R.string.permission_phone_statue, Manifest.permission.READ_PHONE_STATE);
            }

        }
    }

    @Override
    protected void versionCancel() {

    }
}
