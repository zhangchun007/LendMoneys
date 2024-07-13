package com.haiercash.gouhua.activity.edu;

import android.widget.FrameLayout;
import android.widget.Toast;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.interfaces.OnPopClickListener;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Sun
 * @Date :    2019/5/21
 * @FileName: KeyBoardConfirmPresenter
 * @Description: 用于键盘的Presenter处理
 */
public class KeyBoardConfirmPresenter implements INetResult, NameAuthConfirmPopupWindow.NameAuthCallBack {

    private NameAuthConfirmPopupWindow mPopupWindow;

    private NetHelper netHelper;
    private BaseActivity mActivity;
    private String phoneNo, bizCode;

    private OnPopClickListener onPopClickListener;

    public KeyBoardConfirmPresenter(BaseActivity activity, String phone, String bizCode) {
        if (mPopupWindow == null) {
            Map<String, String> map = new HashMap<>();
            map.put("cardMobile", phone);
            mPopupWindow = new NameAuthConfirmPopupWindow(activity, map);
        }
        phoneNo = phone;
        this.bizCode = bizCode;
        mActivity = activity;
        netHelper = new NetHelper(this);
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public void showPopupWindows(OnPopClickListener onPopClickListener) {
        //如果验证码窗体已经显示 则开始倒计时
        if (!mPopupWindow.isShowing()) {
            FrameLayout content = mActivity.findViewById(android.R.id.content);
            mPopupWindow.showAtLocation(content.getChildAt(0));
            mPopupWindow.setNameAuthCallBack(this);
        }
        this.onPopClickListener = onPopClickListener;
        mPopupWindow.needClickSmsCodeAndSend();
    }


    private void sendSms() {
        mActivity.showProgress(true);
        Map<String, String> maps = new HashMap<>();
        maps.put("phone", EncryptUtil.simpleEncrypt(phoneNo));
        //4.1.1新加入参 deviceId不能传空的加密，否则后台不会走正常成功流程
        String deviceId = SystemUtils.getDeviceID(mActivity);
        if (!CheckUtil.isEmpty(deviceId)) {
            maps.put("deviceId", RSAUtils.encryptByRSA(deviceId));
        }
        if (!CheckUtil.isEmpty(bizCode)) {
            maps.put("bizCode", bizCode);//场景ID（用于映射短信模板）
        }
        netHelper.getService(ApiUrl.url_yanzhengma_get, maps);
    }

    @Override
    public void retryRequestSign() {
        sendSms();
    }

    @Override
    public void updateSmsCode(String code) {
        checkSmsVerify(code);
    }

    /**
     * 校验 验证码
     */
    private void checkSmsVerify(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", RSAUtils.encryptByRSA(phoneNo));//手机号
        map.put("verifyNo", RSAUtils.encryptByRSA(code));//验证码
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.url_yanzhengma_xiaoyan, map);
        mActivity.showProgress(true);
    }


    @Override
    public <T> void onSuccess(T t, String url) {
        mActivity.showProgress(false);
        if (ApiUrl.url_yanzhengma_get.equals(url)) {
            Map map = (Map) t;
            if(map.get("message") instanceof String){
                String message = (String) map.get("message");
                UiUtil.toast(message);
            }
            mPopupWindow.startTimer();
        } else if (ApiUrl.url_yanzhengma_xiaoyan.equals(url)) {
            mPopupWindow.dismiss();
            if (onPopClickListener != null) {
                onPopClickListener.onViewClick(null, 1, null);
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        String errorMsg = error == null || error.getHead() == null ? NetConfig.DATA_PARSER_ERROR : error.getHead().getRetMsg();
        if (ApiUrl.url_yanzhengma_xiaoyan.equals(url)) {
            mPopupWindow.onErrorCallBack(errorMsg);
            mActivity.showProgress(false);
        } else if (ApiUrl.url_yanzhengma_get.equals(url)) {
            mActivity.showProgress(false);
            UiUtil.toast(errorMsg);
        } else {
            mActivity.onError(errorMsg);
        }
    }

    public void onDestroy() {
        if (mPopupWindow != null) {
            mPopupWindow.onDestroy();
            mPopupWindow = null;
        }
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        mActivity = null;
    }
}
