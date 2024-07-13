package com.haiercash.gouhua.activity.bankcard;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.edu.NameAuthConfirmPopupWindow;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseDialog;
import com.haiercash.gouhua.beans.BankInfoBean;
import com.haiercash.gouhua.beans.bankcard.AddBankCardBean;
import com.haiercash.gouhua.beans.bankcard.QueryCardBean;
import com.haiercash.gouhua.beans.bankcard.RequestSignBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.repayment.SignBankCardHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.LoginUserHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.BankCardView;
import com.haiercash.gouhua.widget.DelEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Administrator on 2017/6/22.
 * 银行卡详情
 */

public class BankCardDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.bankcardview)
    BankCardView bankcardview;
    @BindView(R.id.ll_btn_content)
    View llBtnContent;
    @BindView(R.id.tv_goToSign)
    TextView tvGoToSign;
    @BindView(R.id.tv_loan_association)
    TextView tvLoanAssociation;
    @BindView(R.id.cl_check_sign)
    ConstraintLayout clCheckSign;
    @BindView(R.id.et_bancard_phone)
    DelEditText et_bancard_phone;
    @BindView(R.id.iv_bankcard_tips)
    ImageView iv_bankcard_tips;
    @BindView(R.id.cb_bankcard_agreement)
    CheckBox cb_bankcard_agreement;
    @BindView(R.id.tv_quick_payment_bank_agreement)
    TextView tv_quick_payment_bank_agreement;
    @BindView(R.id.tv_bank_cancle)
    TextView tv_bank_cancle;
    @BindView(R.id.tv_bank_confirm)
    TextView tv_bank_confirm;
    @BindView(R.id.ll_loan_association_rem)
    View llLoanAssociationRem;
    private QueryCardBean cardBean;
    private SignBankCardHelper signBankCardHelper;
    private BankInfoBean bankCardInfo;
    private String requestNo; //签约请求号
    private NameAuthConfirmPopupWindow confirmPopupWindow;
    private String signAgreementUrl;
    private boolean isRefreshCurrentView = false;//是否刷新当前页面

    @Override
    protected int getLayout() {
        return R.layout.activity_bankcard_details;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("银行卡详情");
        CheckUtil.formatPhone344(et_bancard_phone);
        setBarRightText("删除", Color.RED, view -> showDialogNew());
        signBankCardHelper = new SignBankCardHelper(this);
        cardBean = (QueryCardBean) getIntent().getSerializableExtra("bankCard");
        signAgreementUrl = cardBean.getSignAgreementUrl();
        bankCardState(cardBean.getBankName(), cardBean.getCardNo(), cardBean.getCardTypeName(), cardBean.getBankCardSupport(), cardBean.getSignStatus());
        if ("N".equals(cardBean.getBankCardSupport())) {
            tvGoToSign.setEnabled(false);
            tvLoanAssociation.setVisibility(View.GONE);
            llLoanAssociationRem.setVisibility(View.GONE);
            tvGoToSign.setText("暂不支持签约");
        } else {
            tvGoToSign.setEnabled(true);
            tvLoanAssociation.setVisibility(View.VISIBLE);
            llLoanAssociationRem.setVisibility(View.VISIBLE);
            tvGoToSign.setText("验证银行卡/签约");
        }
        getBankCardInfo();
    }

    /**
     * 初始化卡的状态
     */
    private void bankCardState(String bankName, String cardNo, String cardTypeName, String bankCardSupport, String signStatus) {
        bankcardview.updateView(bankName, cardNo, cardTypeName);
        bankcardview.initStation(bankCardSupport, signStatus, true);
    }

    @OnTextChanged(value = {R.id.et_bancard_phone}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        tv_bank_confirm.setEnabled(checkComplete());
    }

    @OnCheckedChanged(R.id.cb_bankcard_agreement)
    public void onCheckedChanged(boolean isChecked) {
        if (isChecked) {
            if (!cb_bankcard_agreement.isChecked()) {
                cb_bankcard_agreement.setChecked(true);
            }
            tv_bank_confirm.setEnabled(checkComplete());
        } else {
            tv_bank_confirm.setEnabled(false);
        }
    }

    /**
     * 检查当前页面信息完整程度
     */
    private boolean checkComplete() {
        if (et_bancard_phone.getInputText().replace(" ", "").length() != 11) {
            return false;
        }
        if (cb_bankcard_agreement.getVisibility() == View.VISIBLE && tv_quick_payment_bank_agreement.getVisibility() == View.VISIBLE) {
            return cb_bankcard_agreement.isChecked();
        }
        return true;
    }

    private void showDialogNew() {
        showDialog("提示", "您确定要删除此卡吗？", "取消", "确定", (dialog, which) -> {
            if (which == 2) {
                requestUnBind();
            }
        }).setButtonTextColor(2,R.color.colorPrimary);
    }

    @OnClick({R.id.tv_quick_payment_bank_agreement, R.id.tv_loan_association, R.id.tv_goToSign,
            R.id.iv_bankcard_tips, R.id.tv_bank_cancle, R.id.tv_bank_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_goToSign:
                clCheckSign.setVisibility(View.VISIBLE);
                llBtnContent.setVisibility(View.GONE);
                break;
            case R.id.tv_loan_association:
                if ("SIGN_SUCCESS".equals(cardBean.getSignStatus()) || (bankCardInfo != null && "SIGN_SUCCESS".equals(bankCardInfo.getSignStatus()))) {
                    ARouterUntil.getContainerInstance(PagePath.FRAGMENT_LOAN_ASSOCIATION)
                            .put("bankName", cardBean.getBankName())
                            .put("cardNo", cardBean.getCardNo())
                            .put("cardTypeName", cardBean.getCardTypeName())
                            .put("bankCode", cardBean.getBankCode())
                            .navigation();
                } else {
                    showDialog("请先进行银行卡签约");
                }
                break;
            case R.id.tv_quick_payment_bank_agreement:
                if (CheckUtil.isEmpty(et_bancard_phone.getInputText().replace(" ", ""))) {
                    showDialog("请输入正确的手机号码");
                    return;
                }
                SignBankCardHelper.gotoSignAgreement(this, signAgreementUrl,
                        et_bancard_phone.getInputText().replace(" ", ""), SpHp.getUser(SpKey.USER_CERTNO));
                break;
            case R.id.iv_bankcard_tips:
                BaseDialog.getDialog(this, "手机号说明", getResources().getString(R.string.bankcard_tips),
                                "我知道了", null)
                        .setButtonTextColor(1, R.color.colorPrimary).show();
                break;
            case R.id.tv_bank_cancle:
                clCheckSign.setVisibility(View.GONE);
                llBtnContent.setVisibility(View.VISIBLE);
                et_bancard_phone.setText("");
                cb_bankcard_agreement.setChecked(false);
                break;
            case R.id.tv_bank_confirm:
                String phoneNo = et_bancard_phone.getInputText().replaceAll(" ", "");
                if (!CheckUtil.checkPhone(phoneNo)) {
                    showDialog("请输入正确的手机号码");
                } else if (cb_bankcard_agreement.getVisibility() == View.VISIBLE && tv_quick_payment_bank_agreement.getVisibility() == View.VISIBLE && !cb_bankcard_agreement.isChecked()) {
                    showDialog("请先阅读并同意协议");
                } else {
                    // SignBankCardActivity.goSignBankCard(this, item.getCardNo(),true);
                    getLastTime();
                }
                break;
            default:
                break;
        }
    }

    /*解除绑定*/
    private void requestUnBind() {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("custNo", SpHp.getUser(SpKey.USER_CUSTNO));
        map.put("cardNo", RSAUtils.encryptByRSA(cardBean.getCardNo()));
        netHelper.getService(ApiUrl.urlDeleteBankCard, map, null, true);
    }

    /**
     * 获取银行卡信息
     */
    private void getBankCardInfo() {
        signBankCardHelper.requestCardInfo(cardBean.getCardNo());
        showProgress(true);
    }

    private void getLastTime() {
//        long last = NameAuthConfirmPopupWindow.getLastTime();
//        if (last == -1) {
            showProgress(true);
            requestSign();//申请签约
//        } else {
//            UiUtil.toast("验证码发送频繁，请" + last / 1000 + "s后重试");
//        }
    }

    /**
     * 请求签约
     */
    private void requestSign() {
        if (bankCardInfo == null || !bankCardInfo.getCardNo().equals(cardBean.getCardNo())) {
            getBankCardInfo();
        } else {
            bankCardInfo.setCardMobile(et_bancard_phone.getInputText().replace(" ", ""));
            signBankCardHelper.requestSign(bankCardInfo);
            showProgress(true);
        }
    }

    /*银行卡签约*/
    private void signBankCard(String code) {
        showProgress(true);
        AddBankCardBean bean = new AddBankCardBean();
        bean.setCardNo(RSAUtils.encryptByRSA(cardBean.getCardNo()));//银行卡号
        bean.setPhonenumber(RSAUtils.encryptByRSA(et_bancard_phone.getInputText().replace(" ", "")));//绑定手机号
        bean.setCheckCode(RSAUtils.encryptByRSA(code));//验证码
        bean.setInterId(bankCardInfo.getInterId());
        bean.setRequestNo(requestNo);
        signBankCardHelper.reSignBankCard(bean);
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (success == null && !url.equals(ApiUrl.urlDeleteBankCard)) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        showProgress(false);
        if (ApiUrl.URL_BUSINESS.equals(url)) {
            bankCardInfo = (BankInfoBean) success;
            if (!TextUtils.isEmpty(bankCardInfo.getSignAgreementUrl())) {
                cb_bankcard_agreement.setVisibility(View.VISIBLE);
                tv_quick_payment_bank_agreement.setVisibility(View.VISIBLE);
            }
            if (isRefreshCurrentView) {
                signAgreementUrl = bankCardInfo.getSignAgreementUrl();
                bankCardState(bankCardInfo.getBankName(), bankCardInfo.getCardNo(), bankCardInfo.getCardTypeName(), "Y", bankCardInfo.getSignStatus());
                if (confirmPopupWindow != null) {
                    confirmPopupWindow.dismiss();
                }
                clCheckSign.setVisibility(View.GONE);
                llBtnContent.setVisibility(View.VISIBLE);
                et_bancard_phone.setText("");
                cb_bankcard_agreement.setChecked(false);
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
                confirmPopupWindow.showAtLocation(tv_bank_confirm);
                confirmPopupWindow.setNameAuthCallBack(nameAuthCallBack);
            }
            confirmPopupWindow.startTimer();
        } else if (url.equals(ApiUrl.URL_RESIGN)) {
            UiUtil.toast("验证签约成功");
            LoginUserHelper.saveRealNameMsg(success);
            isRefreshCurrentView = true;
            getBankCardInfo();
        } else {
            if (url.equals(ApiUrl.urlDeleteBankCard)) {
                UiUtil.toast("银行卡已删除");
            }
            finish();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.URL_SIGNING.equals(url)) {
            if (confirmPopupWindow != null) {
                confirmPopupWindow.onErrorCallBack(error.getHead().getRetMsg());
            } else {
                showProgress(false);
                UiUtil.toast(error.getHead().getRetMsg());
            }
        } else {
            showProgress(false);
            UiUtil.toast(error.getHead().getRetMsg());
        }
    }

    private NameAuthConfirmPopupWindow.NameAuthCallBack nameAuthCallBack = new NameAuthConfirmPopupWindow.NameAuthCallBack() {
        @Override
        public void retryRequestSign() {
            showProgress(true);
            requestSign();
        }

        @Override
        public void updateSmsCode(String code) {
            signBankCard(code);
        }
    };

}
