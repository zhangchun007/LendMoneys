package com.haiercash.gouhua.activity.gesture;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.HmacSHA256Utils;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.gesture.ValidateUserBean;
import com.haiercash.gouhua.databinding.ActivityVerifyGestureBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;
import com.hunofox.gestures.GesturesHelper;
import com.hunofox.gestures.interfaces.IAccount;
import com.hunofox.gestures.view.GesturesCheckView;

import java.util.HashMap;
import java.util.Map;

/**
 * 手势密码验证
 */
public class GestureVerifyActivity extends BaseActivity {
    public static final String KEY_TAG = "GestureVerifyActivity_TAG";
    public static final String KEY_TAG_CLOSE_GESTURE = "GestureVerifyActivity_TAG_CLOSE_GESTURE";
    private ActivityVerifyGestureBinding mVerifyGestureBinding;
    private GesturesCheckView gesturesView;//手势密码View
    private boolean mVerifySuccess;//验证成功标志，用于区分流程逻辑(验证手势成功后、点击忘记手势密码)
    private String mTag;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return mVerifyGestureBinding = ActivityVerifyGestureBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.gesture_verify_title);
        Intent intent = getIntent();
        if (intent != null) {
            mTag = intent.getStringExtra(KEY_TAG);
        }
        gesturesView = new GesturesHelper().getFreezeGesturesView(this, "1236", findViewById(R.id.gesture_container), new Account(), mVerifyGestureBinding.textTip, 5, LoginSelectHelper.showGestureWay());
        mVerifyGestureBinding.tvForgetGesture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mVerifyGestureBinding.tvForgetGesture) {
            showDialog(getString(R.string.gesture_verify_forget), getString(R.string.gesture_verify_forget_dialog_content),
                    getString(R.string.cancel), getString(R.string.gesture_verify_re_login),
                    (dialog, which) -> {
                        if (which == 2) {
                            showProgress(true);
                            requestDeleteGesture(false);
                        }
                    }).setStandardStyle(3);
        } else {
            super.onClick(v);
        }
    }

    private void requestDeleteGesture(boolean verifySuccess) {
        this.mVerifySuccess = verifySuccess;
        netHelper.postService(ApiUrl.URL_DELETE_GESTURE, null);
    }

    /*验证手势密码*/
    private void requestGesture(String md5Gesture) {
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        if (CheckUtil.isEmpty(userId)) {
            showProgress(false);
            showDialog("账号异常，请退出重试");
            return;
        }
        String deviceId = SystemUtils.getDeviceID(this);
        if (CheckUtil.isEmpty(deviceId)) {
            showProgress(false);
            showDialog("deviceId获取失败");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", EncryptUtil.simpleEncrypt(userId));
        map.put("gesture", EncryptUtil.simpleEncrypt(md5Gesture));
        map.put("deviceId", RSAUtils.encryptByRSA(deviceId));
        //必须放在map最后一行，是对整个map参数进行签名对
        map.put("sign", HmacSHA256Utils.buildNeedSignValue(map));
        netHelper.getService(ApiUrl.url_ValidateGestureCount, map, ValidateUserBean.class, true);
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.url_ValidateGestureCount.equals(url)) {
            //手势密码验证成功
            if (KEY_TAG_CLOSE_GESTURE.equals(mTag)) {
                requestDeleteGesture(true);
            } else {
                setTipUi(true, getString(R.string.gesture_verify_success));
                Intent intent = new Intent(this, GesturesSettingActivity.class);
                intent.putExtra("pageType", "changeGestures");
                startActivity(intent);
                showProgress(false);
                finish();
            }
        } else if (ApiUrl.URL_DELETE_GESTURE.equals(url)) {
            LoginSelectHelper.saveGestureOpenState(false);
            setTipUi(true, getString(R.string.gesture_verify_success));
            showProgress(false);
            if (this.mVerifySuccess) {
                finish();
            } else {//点击忘记手势密码弹窗按钮调的接口
                LoginSelectHelper.closeExceptMainAndToLogin(this);
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        String errorMsg = error == null || error.getHead() == null ? NetConfig.DATA_PARSER_ERROR : error.getHead().getRetMsg();
        if (ApiUrl.url_ValidateGestureCount.equals(url)) {
            setTipUi(false, errorMsg);
            showProgress(false);
        } else if (ApiUrl.URL_DELETE_GESTURE.equals(url)) {
            if (this.mVerifySuccess) {
                setTipUi(false, errorMsg);
                showProgress(false);
            } else {//点击忘记手势密码弹窗按钮调的接口
                super.onError(error, url);
            }
        } else {
            super.onError(error, url);
        }
    }

    private void setTipUi(boolean success, String msg) {
        try {
            mVerifyGestureBinding.textTip.setTextColor(success ? 0xff303133 : 0xffff5151);
            if (success) {
                gesturesView.setGestureCheckSuccess(msg);
            } else {
                gesturesView.setGestureCheckFailed(msg);
            }
        } catch (Exception e) {
            //
        }
    }

    @Override
    protected void onDestroy() {
        if (gesturesView != null) {
            gesturesView.freeHandler();
        }
        super.onDestroy();
    }

    /* 输入手势密码回调 */
    private class Account implements IAccount {
        @Override
        public boolean isFreeze(boolean isFreeze) {
            return false;
        }

        @Override
        public int getCount(int count) {
            return count;
        }

        @Override
        public void setCount(int count) {
        }

        @Override
        public void freezeCount(boolean isFreeze) {
        }

        @Override
        public void success(String inputCode) {
            showProgress(true);
            requestGesture(EncryptUtil.string2MD5(inputCode));
        }
    }
}
