package com.haiercash.gouhua.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.accountsettings.ChangeNewLoginPasswordActivity;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.login.SmsWayLoginActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.sms.SmsTimeView;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.YiDunUtils;
import com.netease.nis.captcha.Captcha;

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
 * 创建日期：2018/10/11<br/>
 * 描    述：账户体系->修改密码->验证手机号<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class CheckPhoneFragment extends BaseFragment implements YiDunUtils.MyYiDunCaptchaListener {
    @BindView(R.id.tv_phone)
    TextView tvMobile;
    @BindView(R.id.message_code)
    EditText etCode;
    @BindView(R.id.bt_commit)
    TextView btCommit;

    private String mobileNo;
    private String smsToken;

    private boolean mYiDunShown = false;//易盾滑块弹窗展示中


    @BindView(R.id.get_code)
    SmsTimeView tvCountdown;    //倒计时

    private boolean clicked_sms_btn;//点击获取验证码时true
    private boolean clicked_phone_unused_btn;//点击若手机号停用时为true

    public static CheckPhoneFragment newInstance(Bundle bd) {
        CheckPhoneFragment fragment = new CheckPhoneFragment();
        if (bd != null) {
            fragment.setArguments(bd);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_check_phone;
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle("验证手机号");
        if (getArguments() == null) {
            UiUtil.toast("账号异常，请退出重试");
            mActivity.finish();
            return;
        }
        mobileNo = getArguments().getString("mobileNo");
        tvMobile.setText(UiUtil.getStr("验证码将发送至", CheckUtil.hidePhoneNumber(mobileNo)));
        //加载发送验证码模块
        registerSmsTime(tvCountdown).setPhoneNum(mobileNo).setAutoSendSms(false)
                .setOnClick(() -> {
                    clicked_sms_btn = true;
                    clicked_phone_unused_btn = false;

                    if (YiDunUtils.isSliderOpen()) {
                        doYiDun();
                    } else {
                        getSmsCode(mobileNo, "TMP_005", null, null, null);
                    }
                });
        btCommit.setEnabled(false);
    }

    @Override
    public void onCaptchaShow() {

    }

    @Override
    public void onCaptchaVisible() {//滑块弹窗肉眼可见
        mYiDunShown = true;
        showProgress(false);
    }

    @Override
    public void onValidate(String result, String validate, String msg) {
        if ("true".equals(result)) {
            if (clicked_sms_btn) {
                getSmsCode(mobileNo, "TMP_005", validate, null, null);
            } else if (clicked_phone_unused_btn) {
                getUserRealState(mobileNo, validate, null, null);
            }
        }

    }

    @Override
    public void onError(int i, String s) {
        if (clicked_sms_btn) {
            getSmsCode(mobileNo, "TMP_005", null, String.valueOf(i), s);
        } else if (clicked_phone_unused_btn) {
            getUserRealState(mobileNo, null, String.valueOf(i), s);
        }

    }

    /**
     * 网易易盾执行
     */
    private void doYiDun() {
        showProgress(true);
        YiDunUtils.initAndValidate(getActivity(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClose(Captcha.CloseType closeType) {
        mYiDunShown = false;
    }

    @OnClick({R.id.bt_commit, R.id.tv_modify_phone})
    public void viewOnClick(View view) {
        if (view.getId() == R.id.bt_commit) {
            String verifyNo = etCode.getText().toString();
            if (CheckUtil.isEmpty(verifyNo)) {
                showDialog("请输入验证码");
                return;
            }
            showProgress(true);
            Map<String, String> map = new HashMap<>();
            map.put("phone", RSAUtils.encryptByRSA(mobileNo));//手机号
            map.put("verifyNo", RSAUtils.encryptByRSA(verifyNo));//短信验证码
            map.put("needSMSToken", "1");//验证成功之后返回“短信码验证成功token”
            map.put("isRsa", "Y");
            netHelper.postService(ApiUrl.url_yanzhengma_xiaoyan, map);
        } else if (view.getId() == R.id.tv_modify_phone) {
            clicked_sms_btn = false;
            clicked_phone_unused_btn = true;
            //执行滑块
            try {
                if (YiDunUtils.isSliderOpen()) {
                    doYiDun();
                } else {
                    showProgress(true);
                    getUserRealState(mobileNo, null, null, null);

                }
            } catch (Exception e) {

            }

        }
    }

    /**
     * 获取用户实名状态
     *
     * @param mobile
     * @param validate
     * @param wy_error_code
     */
    private void getUserRealState(String mobile, String validate, String
            wy_error_code, String wyErrorMsg) {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);//手机号
        map.put("validate", validate);
        map.put("wy_error_code", wy_error_code);
        map.put("wyErrorMsg", wyErrorMsg);
        netHelper.postService(ApiUrl.POST_USER_REAL_STATE, map);

    }

    /**
     * 获取短信验证码
     *
     * @param mobile
     * @param bizCode
     * @param validate
     * @param wy_error_code
     */
    private void getSmsCode(String mobile, String bizCode, String validate, String
            wy_error_code, String wyErrorMsg) {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("phone", mobile);//手机号
        map.put("deviceId", SystemUtils.getDeviceID(getActivity()));//手机号
        map.put("bizCode", bizCode);
        map.put("validate", validate);
        map.put("wy_error_code", wy_error_code);
        map.put("wyErrorMsg", wyErrorMsg);
        netHelper.postService(ApiUrl.POST_SMS_SENDVERIFY_BY_POST, map);

    }

    @OnTextChanged(value = R.id.message_code, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        if (etCode.getText().toString().trim().length() > 0) {
            btCommit.setEnabled(true);
        } else {
            btCommit.setEnabled(false);
        }
    }

    @Override
    public void onSuccess(Object t, String url) {
        if (ApiUrl.url_yanzhengma_xiaoyan.equals(url)) {
            Map map1 = (Map) t;
            if (map1 != null && map1.containsKey("smsToken")) {
                Map<String, String> map = new HashMap<>();
                map.put("mobile", EncryptUtil.simpleEncrypt(mobileNo));//手机号
                //map.put("verifyNo", etCode.getText().toString());
                map.put("smsToken", smsToken = String.valueOf(map1.get("smsToken")));//验证成功之后返回“短信码验证成功token”
                netHelper.postService(ApiUrl.POST_USER_STATUS, map);
            } else {
                UiUtil.toast("服务器开小差了，请稍后再试");
                showProgress(false);
            }
        } else if (ApiUrl.POST_USER_STATUS.equals(url)) {
            showProgress(false);
            Map map = (Map) t;
            //if (map.containsKey("isHaierUAC") && "Y".equals(map.get("isHaierUAC"))) {
            //    MemberForgotPassWord.forgotNumber(mActivity, String.valueOf(map.get("alterPwdIn")), String.valueOf(map.get("alterPwdOut")));
            //    return;
            //}
            if (!"Y".equals(map.get("isRegister"))) {
                showDialog("该手机号码未注册，请先注册");
            } else if (map.containsKey("realNameAuth") && "Y".equals(map.get("realNameAuth"))) {//该手机号下有 已经实名信息
                Bundle bundle = new Bundle();
                bundle.putString("mobile", mobileNo);
                bundle.putString("smsToken", smsToken);
                ContainerActivity.to(mActivity, CheckRealThreeFragment.class.getSimpleName(), bundle);
            } else {//该手机号下 没有实名信息->直接修改密码
                Intent intent = new Intent(mActivity, ChangeNewLoginPasswordActivity.class);
                intent.putExtra(ChangeNewLoginPasswordActivity.PWD_TAG, "WJDLMM");
                intent.putExtra("mobile", mobileNo);
                intent.putExtra("smsToken", smsToken);
                startActivity(intent);
            }
        } else if (ApiUrl.POST_USER_REAL_STATE.equals(url)) {
            showProgress(false);
            if (mYiDunShown) return;
            Map map = (Map) t;
            if (!"Y".equals(map.get("realState"))) { //Y 实名   N 未实名
                showDialog("提示", "你可以使用新的手机号登录", "取消", "去登录", (dialog, which) -> {
                    if (which == 2) {
                        Intent intent = new Intent();
                        SmsWayLoginActivity.startDialogActivity(mActivity, SmsWayLoginActivity.class, SmsWayLoginActivity.ANIM_BOTTOM_IN_RIGHT_OUT, intent);
                    }
                });
            } else {
                Bundle extra = new Bundle();
                extra.putString("doType", "PwdChangPhone1");
                extra.putString("mobile", mobileNo);
                ContainerActivity.to(mActivity, CheckRealFourFragment.class.getSimpleName(), extra);
            }
        } else if (ApiUrl.POST_SMS_SENDVERIFY_BY_POST.equals(url)) {
            Map map = (Map) t;
            if(map.get("message") instanceof String){
                String message = (String) map.get("message");
                UiUtil.toast(message);
            }
            showProgress(false);
            if (tvCountdown != null)
                tvCountdown.startTime();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        //UAC 用户判断逻辑
        //A182510 请求参数无效
        //A182511 短信验证码无效
        //A182512 smsToken超时（目前限制15分钟有效）或手机号不匹配，客户端需要重新走流程
        showProgress(false);
        super.onError(error, url);
    }
}

