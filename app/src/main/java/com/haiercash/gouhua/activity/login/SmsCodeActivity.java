package com.haiercash.gouhua.activity.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.edu.PerfectInfoHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseDialogActivity;
import com.haiercash.gouhua.beans.login.UserInfoBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.sms.SmsTimeView;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.YiDunUtils;
import com.netease.nis.captcha.Captcha;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * @Description:
 * @Author: zhangchun
 * <p>
 * 验证码git地址
 * https://github.com/Chen-keeplearn/SplitEditTextView
 * @CreateDate: 11/16/22
 * @Version: 1.0
 */
public class SmsCodeActivity extends BaseDialogActivity implements YiDunUtils.MyYiDunCaptchaListener, OnInputListener, LoginNetHelper.INetHelperCallBack, View.OnFocusChangeListener {


    @BindView(R.id.fl_back)
    FrameLayout fl_back;
    @BindView(R.id.img_close)
    ImageView imgClose;


    @BindView(R.id.splitEdit)
    SplitEditTextView splitEdit;

    @BindView(R.id.tv_tips)
    TextView tvTips;

    @BindView(R.id.tv_countdown)
    SmsTimeView tvCountdown;    //倒计时

    @BindView(R.id.tv_error_message)
    TextView tvErrorMessage;

    private boolean isCheckError = false;
    private LoginNetHelper loginNetHelper;
    private String mobileNo, captchaId, seqNo, message;
    private String changePhone;

    @Override
    protected int getLayout() {
        return R.layout.activity_sms_code;
    }


    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        loginNetHelper = new LoginNetHelper(this, LoginNetHelper.SMS_LOGIN, null, getPageCode());
        loginNetHelper.setCallBack(this);
        Intent intent = getIntent();
        if (!CheckUtil.isEmpty(intent.getStringExtra("mobileNo"))) {
            mobileNo = intent.getStringExtra("mobileNo");
            tvTips.setText(getResources().getString(R.string.set_login_pwd_verify_mobile_detail, CheckUtil.getPhone(mobileNo)));
        } else {
            showDialog("数据异常，请稍后重试");
        }
        changePhone = intent.getStringExtra("changePhone");
        //说明来源h5，需要隐藏返回键同时登录成功要给H5回调
        boolean needHide = intent.getBooleanExtra("needHide", false);
        if (needHide) {
            fl_back.setVisibility(View.GONE);
        } else {
            fl_back.setVisibility(View.VISIBLE);
        }

        splitEdit.setOnInputListener(this);
        softAdapter(SmsCodeActivity.this, findViewById(R.id.rootView), splitEdit, UiUtil.dip2px(SmsCodeActivity.this, 21), null);
        //加载验证码模块
        registerSmsTime(R.id.tv_countdown).setPhoneNum(mobileNo).setAutoSendSms(false).setOnClick(() -> {
            //8月份优化需求,发送验证码时,清空已输入验证码
            splitEdit.setText("");
            UMengUtil.commonClickEvent("MMipGetCode_Click", "重新获取", getPageNameCn(), getPageCode());
            if (YiDunUtils.isSliderOpen()) {
                doYiDun();
            } else {
                onSendSms(mobileNo, null, null, null);
            }
        });

        boolean isSendOtpSuccess = intent.getBooleanExtra("isSendOtpSuccess", false);

        if (isSendOtpSuccess){
            captchaId = intent.getStringExtra("captchaId");
            seqNo = intent.getStringExtra("seqNo");
            message = intent.getStringExtra("message");
            if(!"null".equals(message)){
                UiUtil.toast(message);
            }
            tvCountdown.startTime();
            KeyBordUntil.showKeyBord(this, splitEdit);
        }else{
            tvCountdown.callOnClick();
        }

        fl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgClose.setOnClickListener(v -> {
            animTypes = ANIM_TYPE_BOTTOM;
            if ("Y".equals(changePhone) || "y".equals(changePhone)) {
                ActivityUntil.finishOthersActivity(MainActivity.class);
            } else {
                ActivityUntil.finishActivity(LoginSelectHelper.getLoginWayActivityPages());
            }
        });

        splitEdit.setOnFocusChangeListener(this);
    }

    private String getPageNameCn() {
        return "验证码登录-输入验证码弹窗";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if ("Y".equals(changePhone) || "y".equals(changePhone)) {
            ActivityUntil.finishOthersActivity(MainActivity.class);
        }
    }

    @Override
    protected String getPageCode() {
        return "MslpMessageInputPop";
    }

    @Override
    public void onCaptchaShow() {

    }

    @Override
    public void onCaptchaVisible() {//滑块弹窗肉眼可见
        showProgress(false);
    }

    @Override
    public void onValidate(String result, String validate, String msg) {
        if (TextUtils.isEmpty(validate)){
            postErrorMsgToServer("-1","短信登录网易滑块validate为空了","onValidate()","短信登录网易滑块validate为空了");
        }
        onSendSms(mobileNo, validate, null, null);
    }

    @Override
    public void onError(int i, String s) {
        postErrorMsgToServer(i+"","短信登录网易滑块报错了","onError()",s);
        onSendSms(mobileNo, null, String.valueOf(i), s);
    }

    @Override
    public void onClose(Captcha.CloseType closeType) {

    }

    private void doYiDun() {
        showProgress(true);
        YiDunUtils.initAndValidate(this, this);
    }

    /**
     * 发送验证码
     */
    public void onSendSms(String mobileNo, String validate, String wyErrorCode, String wyErrorMsg) {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("mobile", RSAUtils.encryptByRSA(mobileNo));
        map.put("bizCode", "102101");
        if (!CheckUtil.isEmpty(validate)) {
            map.put("validate", validate);
        }
        if (!CheckUtil.isEmpty(wyErrorCode)) {
            map.put("wy_error_code", wyErrorCode);
        }
        if (!CheckUtil.isEmpty(wyErrorMsg)) {
            map.put("wy_error_msg", wyErrorMsg);
        }
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.POST_LOGIN_SMS_SEND, map);
    }

    /**
     * 上报异常数据
     *
     * @param code        错误码
     * @param type        类型（哪个sdk）
     * @param errorMethod 问题方法
     * @param msg         详细信息
     */
    public void postErrorMsgToServer(String code, String type, String errorMethod, String msg) {
        if (netHelper != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("code", code);
            map.put("type", type);
            map.put("errorMethod", errorMethod);
            map.put("msg", code + ":" + msg);
            netHelper.postService(ApiUrl.POST_APP_ACTION_LOG, map);
        }

    }

    @Override
    public void onSuccess(Object t, String url) {
        showProgress(false);
        @SuppressWarnings("rawtypes") Map map = (Map) t;
        if (map != null) {
            captchaId = String.valueOf(map.get("captchaId"));
            seqNo = String.valueOf(map.get("seqNo"));
        }
        tvCountdown.startTime();
        KeyBordUntil.showKeyBord(this, splitEdit);
    }

    @Override
    public void onCallBack(String errMsg) {
        isCheckError = true;
        splitEdit.setUnderlineErrorColor(Color.parseColor("#FF5151"), Color.parseColor("#FF5151"));
        tvErrorMessage.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(CheckUtil.isEmpty(errMsg) ? "验证码错误" : errMsg);
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
        } else if (requestCode == LoginNetHelper.FOUR_REQUEST_CODE && resultCode == 1 && loginNetHelper != null) {
            UserInfoBean bean = (UserInfoBean) data.getSerializableExtra("userInfoBean");
            if (bean != null && !CheckUtil.isEmpty(bean.getUserId())) {
                loginNetHelper.smsLoginSuss(bean);
            } else {
                showDialog("数据异常，请稍后重试");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppApplication.doLoginCallback();
    }

    @Override
    public void onInputFinished(String content) {
        if (!CheckUtil.checkPhone(PerfectInfoHelper.phone_pattern, mobileNo)) {
            UiUtil.toast("手机号格式错误，请重新输入");
            return;
        }
        if (CheckUtil.isEmpty(captchaId) || CheckUtil.isEmpty(seqNo)) {
            UiUtil.toast("请重新发送验证码");
            return;
        }
        loginNetHelper.smsLogin(mobileNo, content, captchaId, seqNo);
    }

    @Override
    public void onInputChanged(String text) {
        if (isCheckError) {
            isCheckError = false;
            splitEdit.setText("");
            splitEdit.setUnderlineErrorColor(Color.parseColor("#E8EAEF"), Color.parseColor("#606166"));
            tvErrorMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {
            if (!hasFocus) {
                UMengUtil.postEvent("MMipLoseFoucs");
            }

        } catch (Exception e) {
            Logger.e("SmsCodeActivity销毁而使设置了onFocusChange的View失去焦点");
        }
    }
}
