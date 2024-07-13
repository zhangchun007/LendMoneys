package com.haiercash.gouhua.activity.login;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.login.LastLoginTypeBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;

/**
 * 登录页前置的loading页，主要用于接口请求登录方式，用过即毁！
 */
public class LoginLoadingActivity extends BaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.activity_login_loading;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgress(true);
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", SpHp.getOther(SpKey.LAST_LOGIN_SUCCESS_USERID));
        new NetHelper(this, this).postService(ApiUrl.URL_GET_LAST_LOGIN_TYPE, map, LastLoginTypeBean.class);
    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        showProgress(false);
        finish();
        LoginSelectHelper.staticToSwitchLogin(this, success);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        showProgress(false);
        finish();
        LoginSelectHelper.staticToSwitchLogin(this, null);
    }
}