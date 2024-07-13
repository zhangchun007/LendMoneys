package com.haiercash.gouhua.activity.bankcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.edu.ApplyWaiting;
import com.haiercash.gouhua.activity.edu.EduCommon;
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
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

/**
 * Created by Administrator on 2017/6/26.
 * 添加银行卡
 */

public class AddBankCardInformaticaActivity extends BaseActivity {
    public static int ADD_BANK_REQUEST_CODE = 11;
    @BindView(R.id.tvSetOK)
    TextView mBtn;
    @BindView(R.id.et_phone)
    DelEditText mPhone;//手机号
    @BindView(R.id.bankcardview)
    BankCardView bankcardview;
    @BindView(R.id.et_bankcard_code)
    DelEditText etBankcardCode;
    @BindView(R.id.cb_greement)
    CheckBox cb_greement;
    @BindView(R.id.ll_greement)
    LinearLayout llGreement;
    @BindView(R.id.tv_add_name_bank)
    TextView tv_add_name_bank;
    private String mUserCertNo;//身份证号

    private BankInfoBean bankInfo;
    private String requestNo;

    private SignBankCardHelper signBankCardHelper;
    private NameAuthConfirmPopupWindow confirmPopupWindow;
    private String signCardNum;
    private String tag;

    @Override
    protected int getLayout() {
        return R.layout.activity_add_bankcard_information;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        SystemUtils.setWindowSecure(this);
        mBtn.setEnabled(false);
        signCardNum = getIntent().getStringExtra("signCardNum");
        tag = getIntent().getStringExtra("tag");
        if (isEdApply()) {
            setTitle("额度申请");
            etBankcardCode.setText(CheckUtil.farmatCard(signCardNum));
            etBankcardCode.requestFocus();
            etBankcardCode.setSelection(etBankcardCode.getInputText().length());
            setBarLeftImage(0, v -> EduCommon.onBackPressed(this, "要验证银行卡", getPageCode(), "银行卡绑定页面"));
        } else {
            setTitle("添加银行卡");
        }
        setBarRightText("支持银行卡", 0, view -> {
            Intent intent = new Intent(this, BankCardListActivity.class);
            startActivity(intent);
        });
        //姓名
        String mUserName = SpHp.getUser(SpKey.USER_CUSTNAME);
        tv_add_name_bank.setText(UiUtil.getStr("请添加", CheckUtil.getName(mUserName), "的银行卡"));
        signBankCardHelper = new SignBankCardHelper(this);
        CheckUtil.formatPhone344(mPhone);
        CheckUtil.formatBankCard44x(etBankcardCode, new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 12) {
                    bankcardview.initView();
                    llGreement.setVisibility(View.GONE);
                    if (cb_greement.isChecked()) {
                        cb_greement.setChecked(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPhone.setSelection(mPhone.getInputText().length());
                checkComplete();
            }
        });
    }

    private void initData() {
        mUserCertNo = SpHp.getUser(SpKey.USER_CERTNO);//身份证号
        bankcardview.updateView(bankInfo.getBankName(), bankInfo.getCardNo(), bankInfo.getCardTypeName());
        if (TextUtils.isEmpty(bankInfo.getSignAgreementUrl())) {
            llGreement.setVisibility(View.GONE);
        } else {
            llGreement.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @return 额度申请流程
     */
    private boolean isEdApply() {
        return !CheckUtil.isEmpty(tag) && !CheckUtil.isEmpty(signCardNum);
    }

    @OnFocusChange({R.id.et_bankcard_code})
    public void onViewFocusChange(boolean hasFocus) {
        try {//fragment销毁时会出现异常（也会走这里，并且linePhone为null）
            if (!hasFocus) {//当失去焦点的时候
                if (!CheckUtil.isEmpty(etBankcardCode.getInputTextReplace())) {
                    etBankcardCode.removeIcon();
                    requestCardInfo();
                } else {
                    UiUtil.toast("银行卡号不能为空");
                }
            } else {
                etBankcardCode.addIcon();
            }
        } catch (Exception e) {
            Logger.e("fragment销毁而使设置了onFocusChange的View失去焦点");
        }
    }

    @OnTextChanged(value = {R.id.et_phone}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        checkComplete();
    }

    @OnCheckedChanged(R.id.cb_greement)
    public void onCheckedChanged() {
        checkComplete();
    }

    @Override
    @OnClick({R.id.tvSetOK, R.id.tv_greement1, R.id.iv_tips})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSetOK:
                if (CheckUtil.isEmpty(bankInfo)) {
                    return;
                }
                if (!CheckUtil.checkPhone(mPhone.getInputTextReplace())) {
                    showDialog("请输入正确的手机号码");
                } else if (CheckUtil.isEmpty(etBankcardCode.getInputTextReplace())) {
                    showDialog("请输入正确的银行卡号");
                } else {
                    getLastTime();
                }
                break;
            case R.id.tv_greement1:
                if (CheckUtil.isEmpty(bankInfo)) {
                    return;
                }
                if (CheckUtil.isEmpty(mPhone.getInputTextReplace())) {
                    showDialog("请输入正确的手机号码");
                    return;
                }
                if (CheckUtil.isEmpty(bankInfo.getSignAgreementUrl())) {
                    return;
                }
                SignBankCardHelper.gotoSignAgreement(this, bankInfo.getSignAgreementUrl(),
                        mPhone.getInputTextReplace(), mUserCertNo);
                break;
            case R.id.iv_tips:
                BaseDialog.getDialog(this, "手机号说明", getResources().getString(R.string.bankcard_tips),
                                "我知道了", null)
                        .setButtonTextColor(1, R.color.colorPrimary).show();
                break;
            default:
                break;
        }
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
        if (bankInfo == null || !bankInfo.getCardNo().equals(etBankcardCode.getInputTextReplace())) {
            requestCardInfo();
        } else {
            bankInfo.setCardMobile(mPhone.getInputTextReplace());
            signBankCardHelper.requestSign(bankInfo);
            showProgress(true);
        }
    }

    /**
     * 检查当前页面信息完整程度
     */
    private void checkComplete() {
        boolean enable = (etBankcardCode.getInputTextReplace().length() >= 12) && (mPhone.getInputTextReplace().length() == 11);
        if (llGreement.getVisibility() == View.VISIBLE) {
            enable = enable && cb_greement.isChecked();
        }
        mBtn.setEnabled(enable);
    }


    /*网络请求，添加银行卡*/
    private void requestSaveCard(String code) {
        showProgress(true);
        AddBankCardBean bean = new AddBankCardBean();
        bean.setCardNo(RSAUtils.encryptByRSA(bankInfo.getCardNo()));//银行卡号
        bean.setPhonenumber(RSAUtils.encryptByRSA(mPhone.getInputTextReplace()));//绑定手机号
        bean.setVerifyMobile(RSAUtils.encryptByRSA(mPhone.getInputTextReplace()));//手机号
        bean.setCheckCode(RSAUtils.encryptByRSA(code));//验证码
        bean.setInterId(bankInfo.getInterId());
        bean.setRequestNo(requestNo);
        signBankCardHelper.signBankCard(bean);
    }

    @Override
    public void onError(BasicResponse response, String flag) {
        if (ApiUrl.URL_SIGNING.equals(flag)) {
            if (confirmPopupWindow != null) {
                confirmPopupWindow.onErrorCallBack(response.getHead().getRetMsg());
                showProgress(false);
            } else {
                showProgress(false);
                UiUtil.toast(response.getHead().getRetMsg());
            }
        } else {
            showProgress(false);
            UiUtil.toast(response.getHead().getRetMsg());
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.urlSaveBankCard.equals(url)) {
            showProgress(false);
            LoginUserHelper.saveRealNameMsg(success);
            UiUtil.toast("添加成功");
            if (confirmPopupWindow != null) {
                confirmPopupWindow.dismiss();
            }
            //2.9.0额度申请提交校验是否需求重新签约
            if ("EDJH".equals(tag)) {
                startActivity(new Intent(this, ApplyWaiting.class));
            } else if ("addBank".equals(tag)) {
                //2023/8/29 9月份优化需求,添加银行卡后,需跳转银行卡列表页
                startActivity(new Intent(this, MyCreditCardActivity.class));
//                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_LOAN_ASSOCIATION)
//                        .put("bankName", bankInfo.getBankName())
//                        .put("cardNo", bankInfo.getCardNo())
//                        .put("cardTypeName", bankInfo.getCardTypeName())
//                        .put("bankCode", bankInfo.getBankCode())
//                        .navigation();
            }
            if (bankInfo != null && !TextUtils.isEmpty(bankInfo.getCardNo())) {
                SpHelper.getInstance().saveMsgToSp(SpKey.CHOOSE_SHOW_CONTROL, SpKey.CHECK_POSITION, bankInfo.getCardNo());
                Intent intent = new Intent();
                QueryCardBean cardBean = new QueryCardBean();
                cardBean.setBankCode(bankInfo.getBankCode());
                cardBean.setCardNo(bankInfo.getCardNo());
                cardBean.setBankName(bankInfo.getBankName());
                intent.putExtra("card", cardBean);
                intent.putExtra("isChooseBank", true);
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        } else if (ApiUrl.URL_SIGNING.equals(url)) {
            showProgress(false);
            RequestSignBean requestSignBean = (RequestSignBean) success;
            requestNo = requestSignBean.getRequestNo();
            Map<String, String> map = new HashMap<>();
            map.put("cardMobile", bankInfo.getCardMobile());
            map.put("cardInterName", bankInfo.getInterName());
            map.put("message", requestSignBean.getMessage());
            if (confirmPopupWindow == null) {
                confirmPopupWindow = new NameAuthConfirmPopupWindow(this, map);
            } else {
                confirmPopupWindow.updateView(map);
            }
            //如果验证码窗体已经显示 则开始倒计时
            if (!confirmPopupWindow.isShowing()) {
                confirmPopupWindow.showAtLocation(mBtn);
                confirmPopupWindow.setNameAuthCallBack(nameAuthCallBack);
            }
            confirmPopupWindow.startTimer();
        } else if (ApiUrl.URL_BUSINESS.equals(url)) {
            showProgress(false);
            bankInfo = (BankInfoBean) success;
            KeyBordUntil.hideKeyBord(this);
            initData();
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
            if (!CheckUtil.isEmpty(bankInfo) && !mPhone.getInputTextReplace().equals(bankInfo.getCardMobile())) {
                showDialog("请重新发送验证码");
            } else if (CheckUtil.isEmpty(requestNo)) {
                showDialog("请先发送验证码");
            } else {
                requestSaveCard(code);
            }
        }
    };

    /**
     * 请求卡号信息
     */
    private void requestCardInfo() {
        showProgress(true);
        SignBankCardHelper signBankCardHelper = new SignBankCardHelper(this);
        signBankCardHelper.requestCardInfo(etBankcardCode.getInputTextReplace());
    }

    @Override
    protected boolean useBaseToUmPage() {
        return false;
    }

    @Override
    protected String getPageCode() {
        return isEdApply() ? "RealnameAuthBankPage_gouhua" : super.getPageCode();
    }

    @Override
    public void onBackPressed() {
        if (isEdApply()) {
            EduCommon.onBackPressed(this, "要验证银行卡", getPageCode(), "银行卡绑定页面");
        } else {
            super.onBackPressed();
        }
    }
}
