package com.haiercash.gouhua.activity.gesture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.HmacSHA256Utils;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.inenter.BaseVerifyActivity;
import com.haiercash.gouhua.activity.login.LoginNetHelper;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.gesture.ValidateUserBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.BrAgentUtils;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.LoginUserHelper;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.hunofox.gestures.GesturesHelper;
import com.hunofox.gestures.interfaces.IAccount;
import com.hunofox.gestures.view.GesturesCheckView;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：手势密码界面
 * 项目作者：胡玉君
 * 创建日期：2016/4/13 19:45.
 * ----------------------------------------------------------------------------------------------------
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 * 修改：刘明戈
 */
@Route(path = PagePath.ACTIVITY_GESTURES_SECRET)
public class GesturesPasswordActivity extends BaseVerifyActivity {

    private GesturesCheckView gesturesView;//手势密码View
    private String userGesture;//用户输入的手势密码

    @Override
    protected int getContentResId() {
        return R.layout.activity_gesture_verify;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        mTextTip.setText("");
        FrameLayout mGestureContainer = findViewById(R.id.gesture_container);
        gesturesView = new GesturesHelper().getFreezeGesturesView(this, "1236", mGestureContainer, new Account(), mTextTip, 5, LoginSelectHelper.showGestureWay());
    }

    @Override
    protected void versionCancel() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            userGesture = EncryptUtil.string2MD5(inputCode);
            requestGesture();
        }
    }

    @Override
    protected void onDestroy() {
        removePostValidateGestureTimeRunnable();
        if (gesturesView != null) {
            gesturesView.freeHandler();
        }
        super.onDestroy();
        AppApplication.doLoginCallback();
    }

    /*验证手势密码*/
    private void requestGesture() {
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
        map.put("gesture", EncryptUtil.simpleEncrypt(userGesture));
        map.put("deviceId", RSAUtils.encryptByRSA(deviceId));
        //必须放在map最后一行，是对整个map参数进行签名对
        map.put("sign", HmacSHA256Utils.buildNeedSignValue(map));
        //接口请求如果超过1.5秒则上送
        mPostValidateGestureTimeHandler = new Handler(Looper.getMainLooper());
        mPostValidateGestureTimeRunnable = new PostValidateGestureTimeRunnable(System.currentTimeMillis());
        mPostValidateGestureTimeHandler.postDelayed(mPostValidateGestureTimeRunnable, 1500);
        netHelper.getService(ApiUrl.url_ValidateGestureCount, map, ValidateUserBean.class, true);
    }

    private Handler mPostValidateGestureTimeHandler;
    private PostValidateGestureTimeRunnable mPostValidateGestureTimeRunnable;

    private void removePostValidateGestureTimeRunnable() {//接口返回后移除掉
        if (mPostValidateGestureTimeHandler != null && mPostValidateGestureTimeRunnable != null) {
            mPostValidateGestureTimeHandler.removeCallbacks(mPostValidateGestureTimeRunnable);
        }
    }

    private static class PostValidateGestureTimeRunnable implements Runnable {
        private final long time;

        public PostValidateGestureTimeRunnable(long time) {
            this.time = time;
        }

        @Override
        public void run() {
            HashMap<String, String> map = new HashMap<>();
            map.put("code", "-1");
            map.put("type", "timeout");
            map.put("errorMethod", "app/appserver/uauth/validateGesture");
            map.put("msg", "接口时长超过2秒，发起请求时间点" + TimeUtil.longToString(time));
            new NetHelper().postService(ApiUrl.POST_APP_ACTION_LOG, map);
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.url_ValidateGestureCount.equals(url)) {
            removePostValidateGestureTimeRunnable();
            if (success == null) {
                onError("服务器开小差了，请稍后再试");
                return;
            }
            showProgress(false);
            //手势密码验证成功
            LoginUserHelper.saveLoginInfo((ValidateUserBean) success);
            mTextTip.setText("手势密码验证成功");
            gesturesView.setGestureCheckSuccess("手势密码验证成功");
            RiskNetServer.startRiskServer1(this, "lock_screen_gesture_login", "", 2);
            BrAgentUtils.logInBrAgent(this);//百融风险采集登录
            if ("lock".equals(pageTag)) {
                gesturesView.setGestureCheckSuccess("手势密码验证成功");
                String webUrl = getIntent().getStringExtra("webUrl");
                String webDoType = getIntent().getStringExtra("webDoType");
                if (!CheckUtil.isEmpty(webDoType) && !CheckUtil.isEmpty(webUrl)) {
                    Intent intent = new Intent(this, JsWebBaseActivity.class);
                    intent.putExtra("jumpKey", webUrl);
                    intent.putExtra("webDoType", webDoType);
                    startActivity(intent);
                }
                String contentValue = SpHelper.getInstance().readMsgFromSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_VALUE);
                if (!CheckUtil.isEmpty(contentValue) && "HomePage".equals(contentValue)) {
                    CommomUtils.clearPushSp();
                    CommomUtils.goHomePage();
                } else if (!CheckUtil.isEmpty(contentValue)) {
                    RxBus.getInstance().post(new ActionEvent(ActionEvent.DEAL_WITH_PUSH_INFO));
                }

            } else {
                UiUtil.toast("登录成功");
                postGestureLoginEvent(true, "成功");
                RiskInfoUtils.updateRiskInfoByNode("BR011", "YES");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        //基类里版本更新接口失败也会调这个回调
        if (ApiUrl.url_ValidateGestureCount.equals(url)) {
            removePostValidateGestureTimeRunnable();
            if (LoginNetHelper.isDownStageUser(this, error)) {
                return;
            }
            RiskNetServer.startRiskServer1(this, "lock_screen_gesture_login", "", 2);
            postGestureLoginEvent(false, error.getHead().getRetMsg());
            RiskInfoUtils.updateRiskInfoByNode("BR011", "NO");
            showProgress(false);
            gesturesView.setGestureCheckFailed(error.getHead().getRetMsg());
        }
    }

    private void postGestureLoginEvent(boolean isSuccess, String failReason) {
        Map map = new HashMap();
        if (isSuccess) {
            map.put("is_success", "true");
            map.put("fail_reason", "成功");
        } else {
            map.put("fail_reason", failReason);
            map.put("is_success", "false");
        }
        map.put("page_name_cn", "手势密码登录页");
        UMengUtil.onEventObject("GesturePasswordLogin_Result", map, getPageCode());
    }

    @Override
    protected String getPageName() {
        return "手势密码登录页";
    }

    @Override
    protected String getPageCode() {
        return "GesturePasswordPage";
    }
}
