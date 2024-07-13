package com.haiercash.gouhua.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.AntiHijackingUtil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.activity.edu.PerfectInfoHelper;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.login.UserInfoBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.SpHp;

public class PasswordWayLoginActivity extends BaseLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loginWay = LoginNetHelper.USER_LOGIN;
        loginNetHelper = new LoginNetHelper(this, LoginNetHelper.USER_LOGIN, "BR010", getPageCode());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        SystemUtils.setWindowSecure(this);
        initRxBus();
    }

    private void initRxBus() {
        RxBus.getInstance().addSubscription(this, RxBus.getInstance().register(ActionEvent.class, (actionEvent -> {
            if (ActionEvent.GO_PASSWORD_LOGIN_WAY == actionEvent.getActionType()) {
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

    @Override
    protected void onPause() {
        AntiHijackingUtil.activityPageOnPause(this);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginNetHelper.REQUEST_CODE) {
            if (resultCode == 1 && loginNetHelper != null) {
                //判断是否设置过手势密码
                AppApplication.userid = SpHp.getLogin(SpKey.LOGIN_USERID);
                loginNetHelper.checkGesturePwd();
            } else {//设备验证失败或者取消 默认用户没登录成功  清除所有缓存数据
                CommomUtils.clearSp();
            }
        }else if (requestCode == LoginNetHelper.FOUR_REQUEST_CODE && resultCode == 1 && loginNetHelper != null) {
            UserInfoBean bean = (UserInfoBean) data.getSerializableExtra("userInfoBean");
            if (bean != null && !CheckUtil.isEmpty(bean.getUserId())) {
                loginNetHelper.smsLoginSuss(bean);
            } else {
                showDialog("数据异常，请稍后重试");
            }
        }
    }
}