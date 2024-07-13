package com.haiercash.gouhua.repayment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.BankInfoBean;
import com.haiercash.gouhua.beans.bankcard.AddBankCardBean;
import com.haiercash.gouhua.beans.bankcard.QueryCardBean;
import com.haiercash.gouhua.beans.bankcard.RequestSignBean;
import com.haiercash.gouhua.beans.repayment.SignBankCardNeed;
import com.haiercash.gouhua.sms.SmsTimePresenter;
import com.haiercash.gouhua.sms.SmsTimeView;
import com.haiercash.gouhua.utils.LoginUserHelper;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.NumBerKeyBoard;
import com.haiercash.gouhua.widget.DelEditText;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * Author: Sun
 * Date :    2018/5/12
 * FileName: SignBankCardActivity
 * Description: 老用户进行签约
 * 1，检测是否需要签约
 * 2，对于需要签约的用户进行签约操作
 */

public class SignBankCardActivity extends BaseActivity {
    private static final String TAG_BANK = "bankcard";
    private static final String TAG_SIGN_CARD_NEED = "SignBankCardNeed";

    public static final int REQUEST_CODE = 0x01;
    @BindView(R.id.clRoot)
    View clRoot;
    @BindView(R.id.et_phone)
    DelEditText etPhone;
    @BindView(R.id.edt_getcode)
    DelEditText edtGetcode;
    @BindView(R.id.tvNotCode)
    TextView tvNotCode;
    @BindView(R.id.ivBankCard)
    ImageView ivBankCard;
    @BindView(R.id.tvSignBankCard)
    TextView tvSignBankCard;
    @BindView(R.id.tvPhoneError)
    TextView tvPhoneError;
    @BindView(R.id.tvCodeError)
    TextView tvCodeError;
    private NumBerKeyBoard numBerKeyBoard;
    private String requestNo;
    private SignBankCardHelper signBankCardHelper;
    private QueryCardBean mCardBean;
    private SignBankCardNeed mSignBankCardNeed;
    private SmsTimePresenter smsTimePresenter;

    public static void goSignBankCard(BaseActivity context, QueryCardBean cardBean, SignBankCardNeed signBankCardNeed) {
        Intent intent = new Intent(context, SignBankCardActivity.class);
        intent.putExtra(TAG_BANK, cardBean);
        intent.putExtra(TAG_SIGN_CARD_NEED, signBankCardNeed);
        context.startActivityForResult(intent, REQUEST_CODE);
        context.overridePendingTransition(0, 0);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }


    @OnFocusChange({R.id.et_phone, R.id.edt_getcode})
    public void onFocusChange(View view, boolean hasFocus) {
        try {//fragment销毁时会出现异常（也会走这里，并且linePhone为null）
            if (hasFocus) {
                numBerKeyBoard.attachTo((EditText) view);
                tvPhoneError.setText("");
                tvCodeError.setText("");
            }
        } catch (Exception e) {
            Logger.e("fragment销毁而使设置了onFocusChange的View失去焦点");
        }
    }

    @Override
    @OnClick({R.id.tvNotCode, R.id.tvCommit})
    public void onClick(View view) {
        if (view.getId() == R.id.tvNotCode) {
            showDialog("提示", "收不到银行预留手机号验证码，可换张银行卡支付，您也可以联系当前银行卡的银行办理更换预留手机号。",
                    "更换银行卡", "交易密码验证", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 1://去更换银行卡再来
                                    setResult(false, true, false);
                                    break;
                                case 2://跳转到交易密码验证
                                    setResult(false, false, true);
                                    break;
                            }
                        }
                    }).setButtonTextColor(1, R.color.gray_333333)
                    .setButtonTextColor(2, R.color.colorPrimary);
        } else if (view.getId() == R.id.tvCommit) {
            commit();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_sign_bankcard;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        initView();
        getBankCardInfo();
    }

    private void initView() {
        mCardBean = (QueryCardBean) getIntent().getSerializableExtra(TAG_BANK);
        if (mCardBean == null) {
            mCardBean = new QueryCardBean();
        }
        mSignBankCardNeed = getIntent().getParcelableExtra(TAG_SIGN_CARD_NEED);
        if (mSignBankCardNeed == null) {
            mSignBankCardNeed = new SignBankCardNeed();
        }
        signBankCardHelper = new SignBankCardHelper(this);
        numBerKeyBoard = new NumBerKeyBoard(this, 1);
        //加载发送验证码模块
        smsTimePresenter = registerSmsTime(R.id.ttv_get_code)
                .setPhoneEdit(etPhone)
                .setAutoSendSms(false)
                .setDoCheck(false)
                .setOnClick(() -> {
                    String phoneN = getRealPhone();
                    if (CheckUtil.isEmpty(phoneN)) {
                        tvPhoneError.setText("手机号不能为空!");
                        return;
                    }
                    if (!CheckUtil.checkPhone(phoneN)) {
                        tvPhoneError.setText("请输入正确的手机号!");
                        return;
                    }
                    requestNo = "";
                    edtGetcode.requestFocus();
                    edtGetcode.setFocusableInTouchMode(true);
                    requestSign();
                });
        /*
         * 签约渠道小于2个就需要弹窗，要求用户签约，用户可以跳过("收不到验证码"入口)；如果签约渠道小于等于0，则要求用户必须签约，不允许跳过
         */
        if (mSignBankCardNeed != null && CheckUtil.mIntegerParseInt(mSignBankCardNeed.getSignSuccessNumber()) > 0) {
            smsTimePresenter.setOnSmsTickListener(new SmsTimeView.OnSmsTick() {
                @Override
                public void onTick(long millisUntilFinished) {
                    try {//当剩余70秒时开始显示“收不到验证码”
                        tvNotCode.setVisibility(millisUntilFinished <= 71 * 1000 ? View.VISIBLE : View.INVISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        getEnterKey();
    }

    /**
     * 确定键
     */
    private void getEnterKey() {
        numBerKeyBoard.setOnOkClick(this::commit);
    }

    private void commit() {
        if (!CheckUtil.checkPhone(getRealPhone())) {
            tvPhoneError.setText("请输入正确的手机号!");
        } else if (TextUtils.isEmpty(requestNo)) {
            tvCodeError.setText("请先发送验证码!");
        } else if (edtGetcode.getInputText().length() != 6) {
            tvCodeError.setText("请输入正确的验证码!");
        } else {
            signBankCard();
        }
    }

    /*银行卡签约*/
    private void signBankCard() {
        showProgress(true);
        AddBankCardBean bean = new AddBankCardBean();
        bean.setCardNo(mCardBean != null ? RSAUtils.encryptByRSA(mCardBean.getCardNo()) : "");//银行卡号
        bean.setPhonenumber(RSAUtils.encryptByRSA(getRealPhone()));//绑定手机号
        bean.setCheckCode(RSAUtils.encryptByRSA(edtGetcode.getInputText()));//验证码
        bean.setInterId(getInterId());
        bean.setRequestNo(requestNo);
        signBankCardHelper.reSignBankCard(bean);
    }


    /**
     * 请求签约
     */
    private void requestSign() {
        BankInfoBean bankCardInfo = new BankInfoBean();
        bankCardInfo.setCardMobile(getRealPhone());
        if (mCardBean != null) {
            bankCardInfo.setCardNo(mCardBean.getCardNo());
            bankCardInfo.setBankCode(mCardBean.getBankCode());
            bankCardInfo.setBankName(mCardBean.getBankName());
        }
        bankCardInfo.setInterId(getInterId());
        signBankCardHelper.requestSign(bankCardInfo);
        showProgress(true);
    }

    private String getInterId() {
        return mSignBankCardNeed != null ? mSignBankCardNeed.getInterId() : "";
    }

    /**
     * 获取银行卡信息
     */
    private void getBankCardInfo() {
        ivBankCard.setImageResource(UiUtil.getBankCardImageRes(mCardBean.getBankName()));
        try {
            tvSignBankCard.setText(UiUtil.getStr(mCardBean.getBankName(), "(", mCardBean.getCardNo().substring(mCardBean.getCardNo().length() - 4), ")"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCardBean != null && !CheckUtil.isEmpty(mCardBean.getMobile())) {
            smsTimePresenter.setRealPhone(mCardBean.getMobile());
            etPhone.setText(CheckUtil.hidePhoneNumber(mCardBean.getMobile()));
            numBerKeyBoard.attachTo(etPhone, false);
            doX();
        } else {
            numBerKeyBoard.attachTo(etPhone, true);
        }
        if (etPhone.getText() != null) {
            etPhone.setSelection(etPhone.getText().length());
        }
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (response == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        showProgress(false);
        if (ApiUrl.URL_SIGNING.equals(flag)) {
            RequestSignBean requestSignBean = (RequestSignBean) response;
            ((SmsTimeView) findViewById(R.id.ttv_get_code)).startTime();
            requestNo = requestSignBean.getRequestNo();
        } else if (flag.equals(ApiUrl.URL_RESIGN)) {
            LoginUserHelper.saveRealNameMsg(response);
            setResult(true);
        }
    }

    private void doX() {
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //只要改变了并且有*则直接清空
                if (etPhone.getInputText().contains("*")) {
                    etPhone.setText("");
                }
            }
        });
    }

    @Override
    public void onError(BasicResponse error, String url) {
        showProgress(false);
        UiUtil.toast(error.getHead().getRetMsg());
    }

    /**
     * 去掉*号，得到真正的手机号
     */
    private String getRealPhone() {
        String mobileNo;
        try {
            mobileNo = etPhone.getInputText();
            if (mobileNo.length() == 11 && mobileNo.equals(CheckUtil.hidePhoneNumber(mCardBean.getMobile()))) {
                mobileNo = mCardBean.getMobile();
            }
        } catch (Exception e) {
            mobileNo = "";
        }
        return mobileNo.replace(" ", "");
    }

    /**
     * 返回相应结果
     * 签约成功result才是true,resultCode才是RESULT_OK
     */
    private void setResult(boolean result, boolean toUpdateBankCard, boolean toPwdCheck) {
        int resultCode = result ? RESULT_OK : RESULT_CANCELED;
        Intent intent = new Intent();
        //是否是收不到验证码弹窗选择“更换银行卡”按钮导致的操作
        intent.putExtra("toUpdateBankCard", toUpdateBankCard);
        //是否是收不到验证码弹窗选择“交易密码验证”按钮导致的操作
        intent.putExtra("toPwdCheck", toPwdCheck);
        setResult(resultCode, intent);
        finish();
    }

    private void setResult(boolean result) {
        setResult(result, false, false);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        setResult(false);
    }
}
