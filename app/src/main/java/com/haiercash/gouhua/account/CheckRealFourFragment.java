package com.haiercash.gouhua.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.utils.TextUtils;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.accountsettings.AppealActivity;
import com.haiercash.gouhua.activity.accountsettings.ChangeNewLoginPasswordActivity;
import com.haiercash.gouhua.activity.bankcard.BankCardListActivity;
import com.haiercash.gouhua.activity.edu.NameAuthConfirmPopupWindow;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.BankInfoBean;
import com.haiercash.gouhua.beans.login.UserInfoBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.uihelper.CallPhoneNumberHelper;
import com.haiercash.gouhua.utils.LoginUserHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.YiDunUtils;
import com.haiercash.gouhua.widget.DelEditText;
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
 * 创建日期：2018/10/10<br/>
 * 描    述：账户体系：验证实名<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class CheckRealFourFragment extends BaseFragment implements YiDunUtils.MyYiDunCaptchaListener {
    @BindView(R.id.et_name)
    DelEditText etName;
    @BindView(R.id.et_id_number)
    DelEditText etIdNumber;
    @BindView(R.id.et_card_number)
    DelEditText etCardNumber;
    @BindView(R.id.tv_bank_station)
    TextView tvBankStation;
    @BindView(R.id.et_phone)
    DelEditText etPhone;
    @BindView(R.id.bt_next)
    Button btnNext;
    public static int ID = CheckRealFourFragment.class.hashCode();

    private NameAuthConfirmPopupWindow confirmPopupWindow;

    private UserInfoBean userInfoBean;
    /**
     * Appeal:账号申诉<br/>
     * PwdChangPhone1:原手机号不再使用->修改手机号<br/>
     * DeviceChangPhone:修改手机号
     * ValidateUser：只验证四要素
     */
    private String doType;
    private String mobile;
    private String smsToken;
    private String fourKeysToken;
    private String changeCustDoType;
    private String bizCode;

    private int mCheckErrorCount = 0;//新设备四要素验证失败次数，两次以上需要弹窗


    public static CheckRealFourFragment newInstance(Bundle bd) {
        CheckRealFourFragment fragment = new CheckRealFourFragment();
        if (bd != null) {
            fragment.setArguments(bd);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_check_real_four;
    }

    @Override
    protected void initEventAndData() {
        SystemUtils.setWindowSecure(mActivity);
        mActivity.setTitle("验证信息");
        if (getArguments() == null) {
            UiUtil.toast("账号异常，请退出重试");
            mActivity.finish();
            return;
        }
        doType = getArguments().getString("doType");
        bizCode = getArguments().getString("bizCode");
        if (getArguments().containsKey("mobile")) {
            mobile = getArguments().getString("mobile");
        } else {
            mobile = SpHp.getLogin(SpKey.LOGIN_MOBILE);
        }
        smsToken = getArguments().getString("smsToken");
        etCardNumber.addFocusChangeListener((v, hasFocus) -> {
            if (mActivity.isFinishing()) {
                return;
            }
            if (!hasFocus && etCardNumber != null && etCardNumber.getInputTextReplace().length() > 0) {
                Map<String, String> map = new HashMap<>();
                map.put("cardNo", RSAUtils.encryptByRSA(etCardNumber.getInputTextReplace()));
                netHelper.getService(ApiUrl.urlBankInfo, map, BankInfoBean.class, true);
            }
        });
        CheckUtil.formatBankCard44x(etCardNumber, new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 12) {
                    tvBankStation.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                CheckRealFourFragment.this.afterTextChange();
            }
        });
        confirmPopupWindow = new NameAuthConfirmPopupWindow(mActivity, null);
        confirmPopupWindow.setNameAuthCallBack(callBack);
    }


    @OnClick({R.id.tv_support_card, R.id.iv_tips, R.id.bt_next})
    public void viewOnClick(View view) {
        if (view.getId() == R.id.tv_support_card) {
            startActivity(new Intent(mActivity, BankCardListActivity.class));
        } else if (view.getId() == R.id.iv_tips) {
            showDialog("手机号说明", getResources().getString(R.string.bankcard_tips), "我知道了", null, null).setButtonTextColor(1, R.color.colorPrimary).show();
        } else if (view.getId() == R.id.bt_next) {
            if (etIdNumber.getInputTextReplace().length() != 18) {
                showDialog("请输入正确的身份证号");
            } else if (CheckUtil.checkPhone(etPhone.getInputTextReplace())) {
                callBack.retryRequestSign();
            } else {
                showDialog("请输入正确的手机号码");
            }
        }
    }

    @Override
    public void onSuccess(Object t, String url) {
        if (ApiUrl.POST_SMS_SENDVERIFY_BY_POST.equals(url)) {
            Map paramsMap = (Map) t;
            String message="";
            if(paramsMap.get("message") instanceof String){
                message = (String) paramsMap.get("message");
            }
            showProgress(false);
            Map<String, String> map = new HashMap<>();
            map.put("cardMobile", etPhone.getInputText());
            map.put("message", message);

            if (!confirmPopupWindow.isShowing()) {
                confirmPopupWindow.showAtLocation(btnNext);
            }
            confirmPopupWindow.updateView(map);
            confirmPopupWindow.startTimer();
        } else if (ApiUrl.urlBankInfo.equals(url)) {
            BankInfoBean bean = (BankInfoBean) t;
            tvBankStation.setVisibility(View.VISIBLE);
            tvBankStation.setCompoundDrawablesWithIntrinsicBounds(UiUtil.getBankCardImageRes(bean.bankName), 0, 0, 0);
            tvBankStation.setText(bean.bankName);
            showProgress(false);
        } else if (ApiUrl.POST_FOUR_INFO.equals(url)) {
            showProgress(false);
            if ("DeviceChangPhone".equals(doType)) {//更换设备-->修改手机号
                userInfoBean = getUserInfoBean((Map) t);
                Map map = (Map) t;
                changeCustMobileByRealInfo(map, "DeviceChangPhone", "N");
            } else if ("PwdChangPhone1".equals(doType)) {//原手机号不再使用->修改手机号
                Map map = (Map) t;
                changeCustMobileByRealInfo(map, "ChangPhoneAndPwd", "N");
            } else if ("Appeal".equals(doType)) {//账号申诉
                if (CheckUtil.isEmpty(smsToken) || CheckUtil.isEmpty(mobile)) {
                    UiUtil.toast("服务器开小差了，请稍后再试");
                    return;
                }
                Intent intent = new Intent(mActivity, ChangeNewLoginPasswordActivity.class);
                intent.putExtra(ChangeNewLoginPasswordActivity.PWD_TAG, "WJDLMM");
                intent.putExtra("mobile", mobile);
                intent.putExtra("smsToken", smsToken);
                startActivity(intent);
            } else if ("ValidateUser".equals(doType)) {
                UserInfoBean bean = getUserInfoBean((Map) t);
                if (bean != null && !TextUtils.isEmpty(bean.getUserId())) {
                    Intent intent = new Intent();
                    intent.putExtra("userInfoBean", bean);
                    mActivity.setResult(1, intent);
                    if (confirmPopupWindow != null) {
                        confirmPopupWindow.dismiss();
                    }
                    mActivity.finish();
                } else {
                    showDialog("数据异常，请稍后重试");
                }
            }
        } else if (ApiUrl.POST_RELEASE_MOBILE.equals(url)) {
            showProgress(false);
            UiUtil.toast("申诉成功 请重新登录");
            LoginSelectHelper.closeExceptMainAndToLogin(mActivity);
        } else if (ApiUrl.POST_ChANGE_MOBILE_REALINFO.equals(url)) {
            showProgress(false);
            postEvent("true", "");
            if ("DeviceChangPhone".equals(changeCustDoType)) {
//                UiUtil.toast("更换成功");
                LoginSelectHelper.closeExceptMainAndToLogin(mActivity);
            } else if ("ChangPhoneAndPwd".equals(changeCustDoType)) {//忘记密码时传入的是登录手机号
                //修改
                Intent intent = new Intent(mActivity, ChangeNewLoginPasswordActivity.class);
                intent.putExtra(ChangeNewLoginPasswordActivity.PWD_TAG, "WJDLMM");
                intent.putExtra("mobile", mobile);//登录手机号
                intent.putExtra("fourKeysToken", fourKeysToken);
                startActivity(intent);
            }
        } else if (ApiUrl.URL_ACCOUNT_APPEAL_VERIFY.equals(url)) {
            //检验能否进行账户申诉
            Map<String, String> map = JsonUtils.getRequest(t);
            if (map.containsKey("errorMsg") && !CheckUtil.isEmpty(map.get("errorMsg"))) {
                showProgress(false);
                CallPhoneNumberHelper.callServiceNumber(mActivity,
                        getString(R.string.safe_setting_hint),
                        map.get("errorMsg"),
                        getString(R.string.safe_setting_call),
                        getString(R.string.safe_setting_i_know), 3);
            } else {
                Intent intent = new Intent(mActivity, AppealActivity.class);
                intent.putExtra(AppealActivity.FROM, AppealActivity.NEW_DEVICE_ERROR);
                startActivity(intent);
                showProgress(false);
            }
        }
    }

    private UserInfoBean getUserInfoBean(Map map) {
        UserInfoBean bean = new UserInfoBean();
        if (map != null && map.containsKey("loginInfo")) {
            bean = JsonUtils.fromJson(map.get("loginInfo"), UserInfoBean.class);
        }

        if (bean != null && !CheckUtil.isEmpty(bean.getUserId())) {
            return LoginUserHelper.getDecrypt(bean);
        }
        return null;

    }

    private void changeCustMobileByRealInfo(Map map, String type, String needChangeMobile) {
        changeCustDoType = type;
        if (!map.containsKey("fourKeysToken") || CheckUtil.isEmpty(map.get("fourKeysToken"))) {
            UiUtil.toast("服务器开小差了，请稍后再试");
            return;
        }
        fourKeysToken = String.valueOf(map.get("fourKeysToken"));
        showProgress(true);
        Map<String, String> mapRealInfo = new HashMap<>();
      /*  if (!"ChangPhoneAndPwd".equals(changeCustDoType)) {
            mapRealInfo.put("mobile", EncryptUtil.simpleEncrypt(mobile));//登录手机号
        }*/
        mapRealInfo.put("mobile", RSAUtils.encryptByRSA(mobile));//登录手机号
        mapRealInfo.put("needChangeMobile", needChangeMobile);//是否要更改手机号   Y或 N   N  不改手机号
        mapRealInfo.put("fourKeysToken", fourKeysToken);//实名四要素验证成功的凭证信息
        mapRealInfo.put("newMobile", RSAUtils.encryptByRSA(etPhone.getInputText()));//新手机号
        mapRealInfo.put("deviceType", "AND");//设备类型
        mapRealInfo.put("deviceId", EncryptUtil.simpleEncrypt(SystemUtils.getDeviceID(mActivity)));//设备号
        mapRealInfo.put("iccid", RSAUtils.encryptByRSA(SystemUtils.getDeviceICCID(mActivity)));//设备iccid
        mapRealInfo.put("isRsa", "Y");
        netHelper.postService(ApiUrl.POST_ChANGE_MOBILE_REALINFO, mapRealInfo);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.POST_SMS_SENDVERIFY_BY_POST.equals(url)) {
            if (confirmPopupWindow != null) {
                showProgress(false);
                confirmPopupWindow.onErrorCallBack(error.getHead().getRetMsg());
            } else {
                super.onError(error, url);
            }
        } else if (ApiUrl.urlBankInfo.equals(url)) {
            UiUtil.toast(error.getHead().getRetMsg());
            etCardNumber.setFocusable(true);
            etCardNumber.setFocusableInTouchMode(true);
            etCardNumber.requestFocus();
        } else if (ApiUrl.POST_FOUR_INFO.equals(url)) {
            //A182520 请求参数无效
            //A182521 不是存量用户
            //A182522 手机号与实名信息不匹配
            showProgress(false);
            if ("Appeal".equals(doType)) {
                if ("A182522".equals(error.getHead().getRetFlag())) {
                    Map map1 = (Map) error.getBody();
                    if (!map1.containsKey("fourKeysToken") || CheckUtil.isEmpty(map1.get("fourKeysToken"))) {
                        UiUtil.toast("服务器开小差了，请稍后再试");
                        return;
                    }
                    showProgress(true);
                    Map<String, String> map = new HashMap<>();
                    map.put("mobile", EncryptUtil.simpleEncrypt(mobile));//登录手机号
                    map.put("smsToken", smsToken);//登录手机号短信码验证成功的凭证信息
                    map.put("fourKeysToken", String.valueOf(map1.get("fourKeysToken")));//实名四要素验证成功的凭证信息
                    netHelper.postService(ApiUrl.POST_RELEASE_MOBILE, map);
                } else {
                    super.onError(error, url);
                }
            } else if ("PwdChangPhone1".equals(doType)) {
                if ("A182524".equals(error.getHead().getRetFlag())) {
                    UiUtil.toast(error.getHead().getRetMsg());
                } else {
                    if (confirmPopupWindow != null && confirmPopupWindow.isShowing()) {
                        confirmPopupWindow.clearKeyNumber();
                        confirmPopupWindow.dismiss();
                    }
                    if ("A182521".equals(error.getHead().getRetFlag())) {//不是存量用户
                        showDialog("此功能仅对实名用户开放，您的实名信息校验未通过，请检查输入信息是否正确或使用新的手机号登录", "去登录", "确定", (dialog, which) -> {
                            if (which == 1) {
                                LoginSelectHelper.closeExceptMainAndToLogin(mActivity);
                            }
                        });
                    } else {
                        super.onError(error, url);
                    }
                }
            } else if ("DeviceChangPhone".equals(doType)) {
                UiUtil.toast("抱歉，信息填写有误或实名信息不匹配，暂不能更换");
            } else if ("ValidateUser".equals(doType)) {
                if ("3200".equals(error.getHead().getRetFlag())) {
                    //四要素验证失败2次以上才弹出申诉弹窗，其他错误不记录
                    if (confirmPopupWindow != null && confirmPopupWindow.isShowing()) {
                        confirmPopupWindow.clearKeyNumber();
                        confirmPopupWindow.dismiss();
                    }
                    mCheckErrorCount++;
                    if (mCheckErrorCount > 2) {
                        showAppealDialog();
                    } else {
                        super.onError(error, url);
                    }
                } else {
                    UiUtil.toast(error.getHead().getRetMsg());
                }
            } else {
                super.onError(error, url);
            }
        } else if (ApiUrl.POST_RELEASE_MOBILE.equals(url)) {
            showProgress(false);
            if (confirmPopupWindow != null && confirmPopupWindow.isShowing()) {
                confirmPopupWindow.clearKeyNumber();
                confirmPopupWindow.dismiss();
            }
            showDialog("申诉失败\n请检查填写的信息是否正确", "去注册", "确定", (dialog, which) -> {
                if (which == 1) {
                    LoginSelectHelper.closeExceptMainAndToLogin(mActivity);
                }
            });
        } else if (ApiUrl.POST_ChANGE_MOBILE_REALINFO.equals(url)) {
            // A182540 请求参数无效
            //A182541 新手机号验证码无效
            //A182542 fourKeysToken超时（目前限制15分钟有效）
            //A182543 新手机号不是实名本人手机号（运营商校验不通过)
            if ("DeviceChangPhone".equals(changeCustDoType) || "ChangPhoneAndPwd".equals(changeCustDoType)) {
                if ("A182543".equals(error.getHead().getRetFlag())) {
                    showDialog("请使用本人手机号");
                    postEvent("false", "请使用本人手机号");
                } else {
                    UiUtil.toast(error.getHead().getRetMsg());
                    postEvent("false", error.getHead().getRetMsg());
                }
                showProgress(false);
            } else {
                super.onError(error, url);
            }
        } else {
            super.onError(error, url);
        }
    }

    /**
     * 新设备验证失败两次以上弹窗申诉
     */
    private void showAppealDialog() {
        showDialog(getString(R.string.notice), getString(R.string.appeal_from_new_device_error),
                getString(R.string.i_know), getString(R.string.appeal_from_new_device_error_appeal),
                (dialog, which) -> {
                    if (which == 2) {
                        //账号申诉前置校验流程，校验是否有在途业务和未结清借据
                        showProgress(true);
                        Map<String, String> map = new HashMap<>();
                        map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
                        netHelper.postService(ApiUrl.URL_ACCOUNT_APPEAL_VERIFY, map);
                    }
                }).setStandardStyle(3);
    }

    @OnTextChanged(value = {R.id.et_name, R.id.et_id_number, R.id.et_phone}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChange() {
        btnNext.setEnabled(!CheckUtil.isEmpty(etName.getInputText()) && !CheckUtil.isEmpty(etIdNumber.getInputText()) && !CheckUtil.isEmpty(etCardNumber.getInputTextReplace()) && !CheckUtil.isEmpty(etPhone.getInputText()));
    }

    private NameAuthConfirmPopupWindow.NameAuthCallBack callBack = new NameAuthConfirmPopupWindow.NameAuthCallBack() {
        @Override
        public void retryRequestSign() {
//            long last = NameAuthConfirmPopupWindow.getLastTime();
//            if (last == -1) {
                showProgress(true);
                if (YiDunUtils.isSliderOpen()) {
                    doYiDun();
                } else {
                    getSmsCode(etPhone.getInputText(), bizCode, null, null, null);
                }

//            } else {
//                UiUtil.toast("验证码发送频繁，请" + last / 1000 + "s后重试");
//            }
        }

        @Override
        public void updateSmsCode(String code) {
            showProgress(true);
            String existsUserOnly = "0";
            if ("DeviceChangPhone".equals(doType)) {
                existsUserOnly = "1";
            } else if ("PwdChangPhone1".equals(doType)) {
                existsUserOnly = "1";
            } else if ("Appeal".equals(doType)) {
                existsUserOnly = "0";
            }
            Map<String, String> map = new HashMap<>();
          /*  if (!"PwdChangPhone1".equals(doType)) {
                map.put("mobile", EncryptUtil.simpleEncrypt(mobile));//登录手机号
            }*/
            map.put("mobile", EncryptUtil.simpleEncrypt(mobile));//登录手机号
            map.put("custName", EncryptUtil.simpleEncrypt(etName.getInputText()));//姓名
            map.put("certNo", EncryptUtil.simpleEncrypt(etIdNumber.getInputText()));//身份证号
            map.put("cardNo", EncryptUtil.simpleEncrypt(etCardNumber.getInputTextReplace()));//银行卡号
            map.put("cardMobile", EncryptUtil.simpleEncrypt(etPhone.getInputText()));//银行预留手机号
            map.put("cardMobileVerifyNo", code);//银行预留手机号验证码
            map.put("existsUserOnly", existsUserOnly);//是否限制存量用户
            map.put("needToken", "1");//返回实名验证成功token
            if ("ValidateUser".equals(doType)) {
                map.put("needBind", "Y");//返回实名验证成功token
                map.put("deviceId", SystemUtils.getDeviceID(mActivity));
            }
            netHelper.postService(ApiUrl.POST_FOUR_INFO, map);
        }
    };

    //上传埋点事件
    private void postEvent(String success, String failReason) {
        UMengUtil.commonClickCompleteEvent("FourValidation_Confirmed_Click", "确定", success, failReason, getPageCode());
    }

    @Override
    protected String getPageCode() {
        return "FourValidationPage";
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
        if ("true".equals(result)) {
            getSmsCode(etPhone.getInputText(), bizCode, validate, null, null);
        }
    }

    @Override
    public void onError(int i, String s) {
        getSmsCode(etPhone.getInputText(), bizCode, null, String.valueOf(i), s);

    }

    /**
     * 网易易盾执行
     */
    private void doYiDun() {
        YiDunUtils.initAndValidate(getActivity(), this);
    }


    @Override
    public void onClose(Captcha.CloseType closeType) {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        if (!CheckUtil.isEmpty(bizCode)) {
            map.put("bizCode", bizCode);
        } else {
            map.put("bizCode", "TMP_012");//场景ID（用于映射短信模板）
        }
        map.put("validate", validate);
        map.put("wy_error_code", wy_error_code);
        map.put("wyErrorMsg", wyErrorMsg);
        netHelper.postService(ApiUrl.POST_SMS_SENDVERIFY_BY_POST, map);

    }
}

