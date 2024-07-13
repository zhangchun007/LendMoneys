package com.haiercash.gouhua.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.activity.edu.PerfectInfoHelper;
import com.haiercash.gouhua.tplibrary.PagePath;

@Route(path = PagePath.ACTIVITY_SMS_LOGIN)
public class SmsWayLoginActivity extends BaseLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loginWay = LoginNetHelper.SMS_LOGIN;
        loginNetHelper = new LoginNetHelper(this, LoginNetHelper.SMS_LOGIN, null, getPageCode());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        initRxBus();
    }

    private void initRxBus() {
        RxBus.getInstance().addSubscription(this, RxBus.getInstance().register(ActionEvent.class, (actionEvent -> {
            if (ActionEvent.GO_SMS_LOGIN_WAY == actionEvent.getActionType()) {
                Intent intent1 = actionEvent.getParams();
                mobile = intent1.getStringExtra("mobile");
                changePhone = intent1.getStringExtra("changePhone");
                isFromSms = intent1.getBooleanExtra("fromSms", false);
                boolean needHide1 = intent1.getBooleanExtra("needHide", true);

                boolean needShowBack1 = intent1.getBooleanExtra("needShowBack", false);
                if (needShowBack1) {
                    ivBack.setVisibility(View.VISIBLE);
                } else {
                    ivBack.setVisibility(View.GONE);
                }
                if (needHide1 && !CheckUtil.isEmpty(mobile) && mobile.length() == 11
                        && CheckUtil.checkPhone(PerfectInfoHelper.phone_pattern, mobile)) {
                    etMobile.setText(CheckUtil.hidePhoneNumber(mobile));
                } else if (!CheckUtil.isEmpty(mobile)) {
                    etMobile.setText(mobile);
                } else if (!CheckUtil.isEmpty(LoginSelectHelper.getLastLoginSuccessMobile()) && CheckUtil.isEmpty(changePhone)) {
                    if (LoginSelectHelper.getLastLoginSuccessMobile().length() == 11) {
                        mobile = LoginSelectHelper.getLastLoginSuccessMobile();
                        etMobile.setText(CheckUtil.hidePhoneNumber(mobile));
                    }
                }
            }
        })));
    }

}