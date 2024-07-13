package com.haiercash.gouhua.sms;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author: Sun<br/>
 * Date :    2018/1/26<br/>
 * FileName: SmsTimePresenter<br/>
 * Description: 发送验证码处理类。<br/>
 */

public class SmsTimePresenter implements View.OnClickListener, INetResult {

    private SmsTimeView smsView;

    private EditText phoneEdit;

    private OnSmsClickListener onClickListener;
    private OnSmsPhoneCheckListener onCheckListener;
    private OnSmsSendListener onSendListener;

    private boolean autoSendSms = true;
    private boolean doCheck = true; //是否各自处理，默认流程处理
    private String bizCode;//场景ID（用于映射短信模板）,发送验证码接口区分场景需要
    private String mPhoneNum;
    private AtomicBoolean mRefreshing = new AtomicBoolean(false);

    private NetHelper netHelper;
    private final BaseActivity mActivity;

    /**
     * 发送验证码
     *
     * @param callBack   验证码接口回调
     * @param timeView 控件SmsTimeView
     */
    private SmsTimePresenter(BaseActivity callBack, SmsTimeView timeView) {
        this.mActivity = callBack;
        smsView = timeView;
        if(smsView!=null) {
            smsView.setOnClickListener(this);
        }
    }

    public static SmsTimePresenter getSmsTime(BaseActivity callBack, SmsTimeView timeView) {
        return new SmsTimePresenter(callBack, timeView);
    }

    public SmsTimePresenter setBizCode(String bizCode) {
        this.bizCode = bizCode;
        return this;
    }

    public SmsTimePresenter setSmsView(SmsTimeView view) {
        smsView = view;
        if(smsView!=null) {
            smsView.setOnClickListener(this);
        }
        return this;
    }

    public SmsTimePresenter setPhoneEdit(EditText editText) {
        phoneEdit = editText;
        return this;
    }

    public SmsTimePresenter setAutoSendSms(boolean autoSendSms) {
        this.autoSendSms = autoSendSms;
        return this;
    }

    public SmsTimePresenter setPhoneNum(String phone) {
        mPhoneNum = phone;
        return this;
    }

    public void stopTime() {
        if (smsView != null) {
            smsView.onFinish();
        }
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        onClickListener = null;
        onCheckListener = null;
        onSendListener = null;
    }

    /**
     * 计时走动监听
     */
    public SmsTimePresenter setOnSmsTickListener(SmsTimeView.OnSmsTick onSmsTickListener) {
        if (smsView != null) {
            smsView.setOnSmsTick(onSmsTickListener);
        }
        return this;
    }

    /**
     * 监听短信验证码按钮点击事件
     */
    public SmsTimePresenter setOnClick(OnSmsClickListener listener) {
        onClickListener = listener;
        return this;
    }

    public SmsTimePresenter setOnCheckListener(OnSmsPhoneCheckListener listener) {
        onCheckListener = listener;
        return this;
    }

    /**
     * 监听短信验证码按钮点击事件
     */
    public SmsTimePresenter setOnSend(OnSmsSendListener listener) {
        onSendListener = listener;
        return this;
    }

    private String realPhone;//出场带号码并且中间用*替代的情况

    public void setRealPhone(String realPhone) {
        this.realPhone = realPhone;
    }

    @Override
    public void onClick(View v) {
        //为了防止重复点击
        if (!mRefreshing.compareAndSet(false, true)) {
            return;
        }
        String phone;
        if (phoneEdit == null) {
            phone = mPhoneNum;
        } else {
            phone = phoneEdit.getText().toString().trim().replaceAll(" ", "");
        }
        //是否各自处理，默认流程处理
        if (doCheck) {
            if (CheckUtil.isEmpty(phone)) {
                inpdocError("手机号不能为空");
                return;
            }
            //兼容带*号验证
            if ((!CheckUtil.isEmpty(realPhone) && phone.length() == 11 && !phone.equals(CheckUtil.hidePhoneNumber(realPhone)))
                    || (CheckUtil.isEmpty(realPhone) && !CheckUtil.checkPhone(phone))) {
                inpdocError("请输入正确的手机号码");
                return;
            }
        }
        if (onCheckListener != null && !onCheckListener.onPhoneCheck()) {
            mRefreshing.set(false);
            return;
        }
        if (autoSendSms) {
            sendSms(phone);
        }
        if (onClickListener != null) {
            onClickListener.onClick();
        }
        mRefreshing.set(false);
    }

    public SmsTimePresenter setDoCheck(boolean doCheck) {
        this.doCheck = doCheck;
        return this;
    }

    private void inpdocError(String errorMessage) {
        if (onSendListener != null) {
            onSendListener.onSendResult(errorMessage, false);
        }
        onError(new BasicResponse("", errorMessage), ApiUrl.url_yanzhengma_get);
    }


    //请求验证码
    private void sendSms(String phone) {
        if (mActivity != null && !mActivity.isFinishing()) {
            mActivity.showProgress(true);
        }
        netHelper = new NetHelper(this);
        Map<String, String> maps = new HashMap<>();
        maps.put("phone", EncryptUtil.simpleEncrypt(phone));
        //4.1.1新加入参 deviceId不能传空的加密，否则后台不会走正常成功流程
        String deviceId = SystemUtils.getDeviceID(AppApplication.CONTEXT);
        if (!CheckUtil.isEmpty(deviceId)) {
            maps.put("deviceId", RSAUtils.encryptByRSA(deviceId));
        }
        maps.put("bizCode", bizCode != null ? bizCode : "");//场景ID（用于映射短信模板）
        netHelper.getService(ApiUrl.url_yanzhengma_get, maps);
    }


    public void setSmsFinish(SmsTimeView.OnSmsFinish smsFinish) {
        if (smsView != null) {
            smsView.setOnSmsFinish(smsFinish);
        }
    }

    @Override
    public <T> void onSuccess(T success, String url) {
        if (mActivity != null && !mActivity.isFinishing()) {
            mActivity.showProgress(false);
        }
        if (onSendListener != null) {
            onSendListener.onSendResult(success, true);
        }
        mRefreshing.set(false);
        Map map = (Map) success;
        if(map.get("message") instanceof String){
            String message = (String) map.get("message");
            UiUtil.toast(message);
        }else {
            UiUtil.toast("验证码已发送至您的手机");
        }
        if(smsView!=null) {
            smsView.startTime();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (mActivity != null && !mActivity.isFinishing()) {
            mActivity.showProgress(false);
        }
        mRefreshing.set(false);
        try {
            UiUtil.toast(error.getHead().getRetMsg());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface OnSmsClickListener {
        void onClick();
    }

    public interface OnSmsPhoneCheckListener {
        boolean onPhoneCheck();
    }

    public interface OnSmsSendListener {
        /**
         * 短信发送结果
         *
         * @param success true:发送成功；false:发送失败
         * @param flag    true:自动弹起键盘；
         */
        void onSendResult(Object success, boolean flag);
    }

}
