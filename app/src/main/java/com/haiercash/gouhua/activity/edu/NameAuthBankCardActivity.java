package com.haiercash.gouhua.activity.edu;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SoftHideKeyBoardUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.bankcard.BankCardListActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseDialog;
import com.haiercash.gouhua.beans.BankInfoBean;
import com.haiercash.gouhua.beans.ShiMingRenZheng_Fanhuixinxi_post;
import com.haiercash.gouhua.beans.bankcard.RequestSignBean;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.repayment.SignBankCardHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.tplibrary.bean.IdCardInfo;
import com.haiercash.gouhua.tplibrary.livedetect.FaceRecognitionActivity;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.BankCardView;
import com.haiercash.gouhua.widget.DelEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

/**
 * 绑定银行卡+ 实名认证
 */
public class NameAuthBankCardActivity extends BaseActivity {
    @BindView(R.id.bankcardview)
    BankCardView bankcardview;
    @BindView(R.id.et_bankcard_code)
    DelEditText etBankcardCode;
    @BindView(R.id.et_phone)
    DelEditText etPhone;
    @BindView(R.id.cb_sesame_agreement)
    CheckBox cbSesameAgreement;
    @BindView(R.id.tv_payment_agreement)
    TextView tvPaymentAgreement;
    @BindView(R.id.bt_next)
    Button btNext;
    private IdCardInfo idInfo;

    private String requestNo; //签约请求号

    private NameAuthConfirmPopupWindow confirmPopupWindow;

    private SignBankCardHelper signBankCardHelper;
    private BankInfoBean bankCardInfo;

    //是否继续请求验证码
    private boolean isNextGetCode = false;
    private boolean isChangeMobile = false;

    @Override
    protected int getLayout() {
        return R.layout.activity_name_auth_bank_card;
    }

    /**
     * 额度申请流程中一部分
     */
    public static void gotoBankCard(BaseActivity activity, IdCardInfo cardInfo) {
        gotoBankCard(activity, cardInfo, false);
    }

    /**
     * 修改实名认证的手机号
     *
     * @param isChangeMobile true:修改实名认证的手机号</p>
     *                       false:额度申请流程中一部分
     */
    public static void gotoBankCard(BaseActivity activity, IdCardInfo cardInfo, boolean isChangeMobile) {
        Intent intent = activity.getIntent();
        intent.setClass(activity, NameAuthBankCardActivity.class);
        intent.putExtra("info", cardInfo);
        intent.putExtra("isChangeMobile", isChangeMobile);
        activity.startActivity(intent);
        if (!isChangeMobile) {
            activity.finish();
        }
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        SystemUtils.setWindowSecure(this);
        isChangeMobile = getIntent().getBooleanExtra("isChangeMobile", false);
        if (isChangeMobile) {
            setTitle("更换手机号");
            findViewById(R.id.tv_add_bankcard_title).setVisibility(View.GONE);
            findViewById(R.id.tv_add_remark).setVisibility(View.GONE);
            findViewById(R.id.tv_supportbank).setVisibility(View.GONE);
            bankcardview.setVisibility(View.GONE);
            findViewById(R.id.tv_back_idcard).setVisibility(View.GONE);
            findViewById(R.id.edupbb_view).setVisibility(View.GONE);
        } else {
            setTitle("额度申请");
            findViewById(R.id.tv_1).setVisibility(View.GONE);
            setRightImage(R.drawable.iv_blue_details, v -> ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation());
        }
        idInfo = (IdCardInfo) getIntent().getSerializableExtra("info");
        btNext.setEnabled(false);
        signBankCardHelper = new SignBankCardHelper(this);
        //处理键盘
        SoftHideKeyBoardUtil.assistActivity(this).setSoftKeyBordListener(softKeyBordListener);
        CheckUtil.formatBankCard44x(etBankcardCode, new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 12) {
                    bankcardview.initView();
                    setAgreementVisibility(View.GONE);
                    if (cbSesameAgreement.isChecked()) {
                        cbSesameAgreement.setChecked(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                btNext.setEnabled(checkComplete());
            }
        });
    }

    @OnFocusChange({R.id.et_bankcard_code})
    public void onViewFocusChange(boolean hasFocus) {
        try {//fragment销毁时会出现异常（也会走这里，并且linePhone为null）
            if (!hasFocus) {//当失去焦点的时候
                etBankcardCode.removeIcon();
                getBankCardInfo();
            } else {
                etBankcardCode.addIcon();
            }
        } catch (Exception e) {
            Logger.e("fragment销毁而使设置了onFocusChange的View失去焦点");
        }
    }

    /**
     * 获取银行卡信息
     */
    private void getBankCardInfo() {
        if (TextUtils.isEmpty(etBankcardCode.getInputTextReplace()) || TextUtils.isEmpty(idInfo.getCustName())) {
            return;
        }
        signBankCardHelper.requestCardInfo(etBankcardCode.getInputTextReplace(), idInfo.getCustName());
        showProgress(true);
    }

    @Override
    public void onBackPressed() {
        if (isChangeMobile) {
            super.onBackPressed();
        } else {
            EduCommon.onBackPressed(this, "要验证银行卡", getPageCode(), "银行卡绑定页面");
        }
    }

    /**
     * 请求签约
     */
    private void requestSign() {
        String phoneNo = etPhone.getInputText();
        String bank = etBankcardCode.getInputTextReplace();
        if (bankCardInfo == null || !bankCardInfo.getCardNo().equals(bank)) {
            isNextGetCode = true;
            getBankCardInfo();
        } else {
            isNextGetCode = false;
            bankCardInfo.setCardMobile(phoneNo);
            signBankCardHelper.requestSign(bankCardInfo, idInfo.certNo, idInfo.custName);
            showProgress(true);
        }
    }


    @Override
    @OnClick({R.id.bt_next, R.id.tv_supportbank, R.id.tv_back_idcard,
            R.id.tv_payment_agreement, R.id.iv_tips})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_next:
                String cardNo = etBankcardCode.getInputTextReplace();
                String phoneNo = etPhone.getInputTextReplace();
                if (CheckUtil.isEmpty(cardNo)) {
                    postData("false", "请输入正确的银行卡号");
                    showDialog("请输入正确的银行卡号");
                } else if (!CheckUtil.checkPhone(phoneNo)) {
                    postData("false", "请输入正确的手机号码");
                    showDialog("请输入正确的手机号码");
                } else if (cbSesameAgreement.getVisibility() == View.VISIBLE && !cbSesameAgreement.isChecked()) {
                    postData("false", "请先阅读并同意协议");
                    showDialog("请先阅读并同意协议");
                } else {
                    getLastTime();
                }
                break;
            case R.id.tv_payment_agreement:
                SignBankCardHelper.gotoSignAgreement(this, bankCardInfo.getSignAgreementUrl(),
                        etPhone.getInputText(), idInfo.certNo);
                break;

            case R.id.iv_tips:
                BaseDialog.getDialog(this, "手机号说明", getResources().getString(R.string.bankcard_tips),
                                "我知道了", null)
                        .setButtonTextColor(1, R.color.colorPrimary).show();
                break;
            case R.id.tv_supportbank:
                startActivity(new Intent(NameAuthBankCardActivity.this, BankCardListActivity.class));
                break;
            case R.id.tv_back_idcard:
                Intent intent = getIntent();
                intent.setClass(this, NameAuthIdCardActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void getLastTime() {
//        long last = NameAuthConfirmPopupWindow.getLastTime();
//        if (last == -1) {
            showProgress(true);
            postData("true", "");
            requestSign();//申请签约
//        } else {
//            postData("false", "验证码发送频繁");
//            UiUtil.toast("验证码发送频繁，请" + last / 1000 + "s后重试");
//        }
    }

    //点击下一步上报事件
    private void postData(String isSuccess, String failReason) {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("button_name", "下一步");
        hashMap.put("bank_card_id", EncryptUtil.base64(etBankcardCode.getInputTextReplace()));

        UMengUtil.commonCompleteEvent("RealnameAuthBank", hashMap, isSuccess, failReason, getPageCode());

    }

    @OnCheckedChanged(R.id.cb_sesame_agreement)
    public void onCheckedChanged(boolean isChecked) {
        if (isChecked) {
            if (!cbSesameAgreement.isChecked()) {
                cbSesameAgreement.setChecked(true);
            }
            btNext.setEnabled(checkComplete());
        } else {
            btNext.setEnabled(false);
        }
    }

    @OnTextChanged(value = {R.id.et_phone}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        btNext.setEnabled(checkComplete());
    }

    /**
     * 检查当前页面信息完整程度
     */
    private boolean checkComplete() {
        if (etBankcardCode.length() == 0) {
            return false;
        }
        if (etPhone.length() != 11) {
            return false;
        }
        if (cbSesameAgreement.getVisibility() == View.VISIBLE) {
            return cbSesameAgreement.isChecked();
        }
        return true;
    }

    private void setAgreementVisibility(int visibility) {
        cbSesameAgreement.setVisibility(visibility);
        tvPaymentAgreement.setVisibility(visibility);
    }

    /**
     * 上送 实名认证的相关信息
     */
    private void uploadNameAuth(String code) {
        String cardNo = etBankcardCode.getInputTextReplace();
        String phoneNo = etPhone.getInputTextReplace();
        Map<String, String> map = new HashMap<>();
        map.put("userId", RSAUtils.encryptByRSA(AppApplication.userid));
        map.put("custName", RSAUtils.encryptByRSA(idInfo.custName));
        map.put("certNo", RSAUtils.encryptByRSA(idInfo.certNo));
        map.put("cardNo", RSAUtils.encryptByRSA(cardNo));
        map.put("mobile", RSAUtils.encryptByRSA(phoneNo));
        map.put("dataFrom", "42");
        map.put("checkCode", RSAUtils.encryptByRSA(code));
        map.put("requestNo", requestNo);
        map.put("interId", bankCardInfo.getInterId());
        if (isChangeMobile) {
            map.put("reIdentityVerify", "Y");
        }
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.URL_NAME_AUTH_NO_PHOTO, map, ShiMingRenZheng_Fanhuixinxi_post.class, true);
    }

    @Override
    public void onSuccess(Object success, String url) {
        showProgress(false);
        if (success == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        if (ApiUrl.URL_BUSINESS.equals(url)) {
            //获取到之前的CardMobile
            String phoneNum = "";
            if (bankCardInfo != null) {
                phoneNum = bankCardInfo.getCardMobile();
            }
            bankCardInfo = (BankInfoBean) success;
            bankCardInfo.setCardMobile(phoneNum);
            if (!TextUtils.isEmpty(bankCardInfo.getSignAgreementUrl())) {
                setAgreementVisibility(View.VISIBLE);
            }
            bankcardview.updateView(bankCardInfo.bankName, bankCardInfo.cardNo, bankCardInfo.cardTypeName);
            if (isNextGetCode) {
                requestSign();
            }
        } else if (ApiUrl.URL_NAME_AUTH_NO_PHOTO.equals(url)) {
            //实名认证成功
            ShiMingRenZheng_Fanhuixinxi_post cust = (ShiMingRenZheng_Fanhuixinxi_post) success;
            saveNameAuth(cust);
            if ("Y".equals(cust.getCreditExist())) {
                showDialog("提示", "您的额度已激活，可返回首页立即借款", "我知道了", null, (dialog, which) -> {
                    MainHelper.backToMainHome();
                }).setButtonTextColor(1, R.color.colorPrimary);
            } else if ("Y".equals(cust.getCreditReject())) {
                showDialog("提示", "额度申请未通过，暂时无法提供借款服务", "我知道了", null, (dialog, which) -> {
                    MainHelper.backToMainHome();
                }).setButtonTextColor(1, R.color.colorPrimary);
            } else {
                if ("Y".equals(cust.getIdentityVerifyStatus())) {
                    if (confirmPopupWindow != null) {
                        confirmPopupWindow.dismiss();
                        confirmPopupWindow = null;
                    }
                    updateRiskInfo();
                    if (isChangeMobile) {
                        //借款流程填写验证码的时候：修改实名认证时候的手机号
                        UiUtil.toast("更换手机号成功");
                        finish();
                    } else {
                        UMengUtil.commonClickCompleteEvent("RealnameAuthSign", "确定", "true", "", getPageCode());
                        toNextActivity();
                    }
                }
            }
        } else if (ApiUrl.URL_SIGNING.equals(url)) {
            RequestSignBean requestSignBean = (RequestSignBean) success;
            requestNo = requestSignBean.getRequestNo();
            Map<String, String> map = new HashMap<>();
            map.put("cardMobile", bankCardInfo.getCardMobile());
            map.put("cardInterName", bankCardInfo.getInterName());
            map.put("message", requestSignBean.getMessage());

            if (confirmPopupWindow == null) {
                confirmPopupWindow = new NameAuthConfirmPopupWindow(this, map);
            } else {
                confirmPopupWindow.updateView(map);
            }
            //如果验证码窗体已经显示 则开始倒计时
            if (!confirmPopupWindow.isShowing()) {
                confirmPopupWindow.showAtLocation(btNext);
                confirmPopupWindow.setNameAuthCallBack(nameAuthCallBack);
            }
            confirmPopupWindow.startTimer();
        }
    }

    /**
     * 保存实名的相关信息
     */
    private void saveNameAuth(ShiMingRenZheng_Fanhuixinxi_post cust) {
        SpHp.saveUser(SpKey.USER_CUSTNAME, cust.getCustName());//客户姓名
        SpHp.saveUser(SpKey.USER_CUSTNO, cust.getCustNo());//客户编号
        SpHp.saveUser(SpKey.USER_CERTNO, cust.getCertNo());//证件号
        SpHp.saveUser(SpKey.USER_MOBILE, cust.getMobile());//实名认证手机号
        // GrowingIOUtils.setCustNo();
    }

    /**
     * 上传风险信息
     */
    private void updateRiskInfo() {
        RiskInfoUtils.requestRiskInfoPhone(this);
    }

    private NameAuthConfirmPopupWindow.NameAuthCallBack nameAuthCallBack = new NameAuthConfirmPopupWindow.NameAuthCallBack() {
        @Override
        public void retryRequestSign() {
            showProgress(true);
            requestSign();
        }

        @Override
        public void updateSmsCode(String code) {
            showProgress(true);
            uploadNameAuth(code);
        }
    };

    @Override
    public void onError(BasicResponse error, String url) {
        isNextGetCode = false;
        showProgress(false);
        UMengUtil.commonClickCompleteEvent("RealnameAuthSign", "确定", "false", error.getHead().getRetMsg(), getPageCode());
        if (ApiUrl.URL_SIGNING.equals(url)) {
            if (confirmPopupWindow != null) {
                confirmPopupWindow.onErrorCallBack(error.getHead().getRetMsg());
                if ("A183482".equals(error.getHead().retFlag)) {
                    confirmPopupWindow.clearKeyNumber();
                }
            } else {
                UiUtil.toast(error.getHead().getRetMsg());
            }
        } else if (ApiUrl.URL_NAME_AUTH_NO_PHOTO.equals(url)
                && ("A184680".equals(error.getHead().retFlag)
                || "A183480".equals(error.getHead().retFlag))) {
            onError(error.getHead().getRetMsg());
        } else {
            UiUtil.toast(error.getHead().getRetMsg());
        }
        if (ApiUrl.URL_BUSINESS.equals(url)) {
            etBankcardCode.setFocusable(true);
            etBankcardCode.setFocusableInTouchMode(true);
            etBankcardCode.requestFocus();
        } else if ("A183482".equals(error.getHead().retFlag)) {
            confirmPopupWindow.clearKeyNumber();
        }
    }

    private SoftHideKeyBoardUtil.SoftKeyBordListener softKeyBordListener = new SoftHideKeyBoardUtil.SoftKeyBordListener() {
        @Override
        public void softShowing() {


        }

        @Override
        public void softHide() {
            if (getCurrentFocus() != null && getCurrentFocus() == etBankcardCode) {
                etBankcardCode.clearFocus();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (confirmPopupWindow != null) {
            confirmPopupWindow.dismiss();
            confirmPopupWindow = null;
        }
    }

    @Override
    protected String getPageCode() {
        return "RealnameAuthBankPage_gouhua";
    }

    //实名认证完成，进行下一步
    private void toNextActivity() {
        UiUtil.toast("实名认证成功");
        //删除图片
        /*if (!TextUtils.isEmpty(idInfo.getIvFrontPath())) {
            FileUtils.deleteFile(new File(idInfo.getIvFrontPath()).getParentFile());
        }*/
        //实名认证成功，则清除保存的相关信息
        SpHp.deleteLogin(SpKey.LOGIN_AUTHMESSAGE);
        Intent intent = new Intent(this, FaceRecognitionActivity.class);
        intent.putExtra("tag", "EDJH");
        startActivity(intent);
        NameAuthIdCardActivity activity = ActivityUntil.findActivity(NameAuthIdCardActivity.class);
        if (activity != null) {
            activity.finish();
        }
        finish();
    }
}
