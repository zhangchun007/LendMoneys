package com.haiercash.gouhua.fragments.mine;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SoftKeyBoardListenerUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.accountsettings.AppealActivity;
import com.haiercash.gouhua.activity.accountsettings.AppealResultActivity;
import com.haiercash.gouhua.activity.bankcard.BankCardListActivity;
import com.haiercash.gouhua.activity.edu.NameAuthConfirmPopupWindow;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.login.UserInfoBean;
import com.haiercash.gouhua.databinding.FragmentAppealBankCardBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.repayment.SignBankCardHelper;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.LoginUserHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 账号申诉-实名认证
 */
public class AppealBankCardFragment extends BaseFragment implements View.OnFocusChangeListener, NameAuthConfirmPopupWindow.NameAuthCallBack {
    private FragmentAppealBankCardBinding appealBankCardBinding;
    private SignBankCardHelper signBankCardHelper;
    private boolean mCardError;//输入框下方是否显示错误文案
    private NameAuthConfirmPopupWindow mPopupWindow;

    @Override
    protected ViewBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return appealBankCardBinding = FragmentAppealBankCardBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initEventAndData() {
        appealBankCardBinding.tvRealName.setTypeface(FontCustom.getMediumFont(mActivity));
        appealBankCardBinding.tvBankCard.setTypeface(FontCustom.getMediumFont(mActivity));
        String phone = SpHp.getLogin(SpKey.LOGIN_MOBILE);
        String showPhone = CheckUtil.getPhone344(phone);
        appealBankCardBinding.tvPhone.setText(showPhone);
        appealBankCardBinding.etBankCard.setOnFocusChangeListener(this);
        CheckUtil.formatBankCard44x(appealBankCardBinding.etBankCard, null);
        appealBankCardBinding.etBankCard.addTextChangedListener(this);
        appealBankCardBinding.tvBankCardSupport.setOnClickListener(this);
        appealBankCardBinding.ivPhoneTip.setOnClickListener(this);
        appealBankCardBinding.ivBankNumDel.setOnClickListener(this);
        appealBankCardBinding.tvNext.setOnClickListener(this);
        if (mActivity instanceof AppealActivity) {
            ((AppealActivity) mActivity).addTouchOutOfViewList(appealBankCardBinding.etBankCard);
        }
        SoftKeyBoardListenerUtil.setListener(mActivity, new SoftKeyBoardListenerUtil.MySoftKeyboardListener(appealBankCardBinding.etBankCard));

        Map<String, String> map = new HashMap<>();
        map.put("cardMobile", phone);
        mPopupWindow = new NameAuthConfirmPopupWindow(mActivity, map);
    }

    private String getFrom() {
        if (mActivity instanceof AppealActivity) {
            return ((AppealActivity) mActivity).getFrom();
        } else {
            return "";
        }
    }

    private String getCertName() {
        if (mActivity instanceof AppealActivity) {
            return ((AppealActivity) mActivity).getCertName();
        } else {
            return "";
        }
    }

    private String getCertNo() {
        if (mActivity instanceof AppealActivity) {
            return ((AppealActivity) mActivity).getCertNo();
        } else {
            return "";
        }
    }

    @Override
    public void onClick(View v) {
        if (v == appealBankCardBinding.tvNext) {
            requestBankCardInfo();
        } else if (v == appealBankCardBinding.tvBankCardSupport) {
            //查看支持银行
            startActivity(new Intent(mActivity, BankCardListActivity.class));
        } else if (v == appealBankCardBinding.ivPhoneTip) {
            //手机号说明
            mActivity.showDialog(getString(R.string.appeal_bank_card_tip_dialog_title),
                    getString(R.string.appeal_bank_card_tip_dialog_content), null);
        }
        else if (v == appealBankCardBinding.ivBankNumDel) {
            //清空银行卡号
            appealBankCardBinding.etBankCard.setText("");
        }else {
            super.onClick(v);
        }
    }

    private void requestBankCardInfo() {
        showProgress(true);
        String bankCard = getCardNo();
        if (signBankCardHelper == null) {
            signBankCardHelper = new SignBankCardHelper(this);
        }
        signBankCardHelper.requestCardInfo(bankCard, getCertName());
    }

    private String getCardNo() {
        return (appealBankCardBinding.etBankCard.getText() != null ? appealBankCardBinding.etBankCard.getText().toString() : "").replace(" ", "");
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {
            if (v == appealBankCardBinding.etBankCard) {
                if (hasFocus) {
                    appealBankCardBinding.lineBankCard.setBackgroundResource(R.color.color_606166);
                    appealBankCardBinding.tvBankCardError.setVisibility(View.INVISIBLE);
                } else {
                    setErrorUi(mCardError);
                }
            }
        } catch (Exception e) {
            //
        }
    }

    private void setErrorUi(boolean error) {
        try {
            appealBankCardBinding.lineBankCard.setBackgroundResource(error ? R.color.color_ff5151 : R.color.color_e8eaef);
            appealBankCardBinding.tvBankCardError.setVisibility(error ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        super.afterTextChanged(s);
        String card = getCardNo();
        //为空时不显示错误文案
        boolean isEmpty = TextUtils.isEmpty(card);
        mCardError = !TextUtils.isEmpty(card) && !CheckUtil.checkBankCard(card);
        appealBankCardBinding.tvNext.setEnabled(!isEmpty && !mCardError);
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        if (ApiUrl.URL_BUSINESS.equals(url)) {
            showProgress(false);
            if (mPopupWindow != null) {
                if (!mPopupWindow.isShowing()) {
                    FrameLayout content = mActivity.findViewById(android.R.id.content);
                    mPopupWindow.showAtLocation(content.getChildAt(0));
                }
                mPopupWindow.setNameAuthCallBack(this);
                mPopupWindow.needClickSmsCodeAndSend();
            }
        } else if (ApiUrl.url_yanzhengma_get.equals(url)) {
            Map map = (Map) t;
            if(map.get("message") instanceof String){
                String message = (String) map.get("message");
                UiUtil.toast(message);
            }
            showProgress(false);
            if (mPopupWindow != null) {
                mPopupWindow.startTimer();
            }
        } else if (ApiUrl.POST_APPEAL_COMMIT.equals(url)) {
            String from = getFrom();
            Intent intent = new Intent(mActivity, AppealResultActivity.class);
            if (!CheckUtil.isEmpty(from)) {
                intent.putExtra(AppealActivity.FROM, from);
            }
            try {
                UserInfoBean userInfoBean = LoginUserHelper.getDecrypt((UserInfoBean) t);
                if (!CheckUtil.isEmpty(userInfoBean.getMashMobile())) {
                    intent.putExtra(AppealResultActivity.KEY_MASH_PHONE, userInfoBean.getMashMobile());
                }
                if (!CheckUtil.isEmpty(userInfoBean.getMashCertNo())) {
                    intent.putExtra(AppealResultActivity.KEY_MASH_CERT_NO, userInfoBean.getMashCertNo());
                }
                //代偿用户
                if ("Y".equals(userInfoBean.getCompensationFlag())) {
                    SpHp.deleteOther(SpKey.LAST_LOGIN_SUCCESS_MOBILE);
                    SpHp.deleteOther(SpKey.LAST_LOGIN_SUCCESS_USERID);
                    CommomUtils.clearSp();
                    showProgress(false);
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    UiUtil.toast("账号申诉成功,请重新登录");
                    ActivityUntil.finishOthersActivity(MainActivity.class);
                    LoginSelectHelper.staticToGeneralLogin();
                    return;
                }
                if (AppealActivity.NEW_DEVICE_ERROR.equals(from)) {
                    LoginUserHelper.saveLoginInfo(userInfoBean);
                } else {//清除登录信息和最近登录手机号和userId
                    SpHp.deleteOther(SpKey.LAST_LOGIN_SUCCESS_MOBILE);
                    SpHp.deleteOther(SpKey.LAST_LOGIN_SUCCESS_USERID);
                    CommomUtils.clearSp();
                }
            } catch (Exception e) {
                //
            }
            mActivity.startActivity(intent);
            showProgress(false);
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        String errorMsg = error == null || error.getHead() == null ? NetConfig.DATA_PARSER_ERROR : error.getHead().getRetMsg();
        showProgress(false);
        if (ApiUrl.POST_APPEAL_COMMIT.equals(url)) {
            //特殊错误码"0001"表示申诉失败,需要关掉验证码页面
            if ("0001".equals(error != null && error.getHead() != null ? error.getHead().getRetFlag() : "") &&
                    mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else if (mPopupWindow != null) {
                mPopupWindow.onErrorCallBack(errorMsg);
                return;
            }
        }
        if (!isHidden()) {
            UiUtil.toast(errorMsg);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mActivity != null && !mActivity.isFinishing()) {
            mActivity.onBackPressed();
        }
        return true;
    }

    @Override
    public void retryRequestSign() {
        sendSms();
    }

    @Override
    public void updateSmsCode(String code) {
        checkSmsVerify(code);
    }

    private void sendSms() {
        showProgress(true);
        Map<String, String> maps = new HashMap<>();
        maps.put("phone", EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
        //deviceId不能传空的加密，否则后台不会走正常成功流程
        String deviceId = SystemUtils.getDeviceID(mActivity);
        if (!CheckUtil.isEmpty(deviceId)) {
            maps.put("deviceId", RSAUtils.encryptByRSA(deviceId));
        }
        maps.put("bizCode", "TMP_013");//场景ID（用于映射短信模板）
        netHelper.getService(ApiUrl.url_yanzhengma_get, maps);
    }

    /**
     * 校验验证码且提交申诉
     */
    private void checkSmsVerify(String code) {
        mActivity.showProgress(true);
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
        map.put("mobile", SpHp.getLogin(SpKey.LOGIN_MOBILE));
        map.put("custName", getCertName());
        map.put("certNo", getCertNo());
        if (AppealActivity.NEW_DEVICE_ERROR.equals(getFrom())) {
            map.put("needLogin", "Y");
            map.put("deviceId", SystemUtils.getDeviceID(mActivity));
            map.put("iccId", SystemUtils.getDeviceICCID(mActivity));
        } else {
            map.put("needLogin", "N");
        }
        map.put("cardNo", getCardNo());
        map.put("verifyNo", code);
        netHelper.postService(ApiUrl.POST_APPEAL_COMMIT, map, UserInfoBean.class);
    }

    @Override
    public void onDestroyView() {
        if (mPopupWindow != null) {
            mPopupWindow.onDestroy();
            mPopupWindow = null;
        }
        if (mActivity instanceof AppealActivity) {
            ((AppealActivity) mActivity).addTouchOutOfViewList();
        }
        super.onDestroyView();
    }
}
