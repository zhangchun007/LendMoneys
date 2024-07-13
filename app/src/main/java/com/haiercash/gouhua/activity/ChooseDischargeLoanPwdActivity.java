package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.accountsettings.SetTransactionPwdActivity;
import com.haiercash.gouhua.activity.accountsettings.VerifyPwdActivity;
import com.haiercash.gouhua.activity.borrowmoney.BorrowPayDataImpl;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.fragments.mine.CheckCertNoFragment;
import com.haiercash.gouhua.tplibrary.livedetect.FaceCheckActivity;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.CustomCodeView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/6/8<br/>
 * 描    述：结清证明交易密码<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ChooseDischargeLoanPwdActivity extends BaseActivity implements BorrowPayDataImpl.IBorrowPayView {
    private CustomCodeView payPassWordView;
    private final AtomicBoolean mRefreshing = new AtomicBoolean(false);
    private String applseq;
    private BorrowPayDataImpl payDataImpl;

    @Override
    protected int getLayout() {
        return R.layout.activity_choose_discharge_loan_pwd;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        payPassWordView = findViewById(R.id.password_input_transaction);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_forget_password).setOnClickListener(this);
        findViewById(R.id.tv_no_six_password).setOnClickListener(this);
        applseq = getIntent() != null ? getIntent().getStringExtra("applseq") : "";
        payPassWordView.setOnInputFinishedListener(password -> {
            if (TextUtils.isEmpty(password) || password.length() != 6) {
                UiUtil.toast("请输入正确的交易密码");
            } else {
                //为了防止重复点击
                if (!mRefreshing.compareAndSet(false, true)) {
                    return;
                }
                payDataImpl.payPwd(payPassWordView.getCurrentWord());
            }
        });
        payDataImpl = new BorrowPayDataImpl(this, null, null, null, null, null, null, null, this, "");
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.tv_forget_password) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
            bundle.putSerializable(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
            bundle.putString(SetTransactionPwdActivity.TAG, "WJJYMM");
            ContainerActivity.to(this, CheckCertNoFragment.class.getSimpleName(), bundle);
        } else if (v.getId() == R.id.tv_no_six_password) {
            Intent intent = new Intent(this, VerifyPwdActivity.class);
            intent.putExtra("title", "验证交易密码");
            intent.putExtra(VerifyPwdActivity.PAGE_KEY, SetTransactionPwdActivity.class);
            intent.putExtra(SetTransactionPwdActivity.TAG, "XGJYMM");
            startActivity(intent);
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void payPwdCallBack(boolean payResult, int failSum) {
        mRefreshing.set(false);
        if (payResult) {
            payDataImpl.postCertificate(applseq);
        } else {
            if (failSum == 1) {
                showBtn2Dialog("交易密码输入错误", "重新输入", (dialog, which) -> {
                    //清空并重新输入
                    payPassWordView.clearnData();
                }).setButtonTextColor(2, R.color.colorPrimary);
            } else if (failSum == 3) {
                showDialog("交易密码输入错误次数过多,您\n的账号已被锁定,请点击忘记密\n码进行找回或明日重试", "忘记密码", "我知道了", (dialog, which) -> {
                    if (which == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
                        bundle.putSerializable(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                        bundle.putString(SetTransactionPwdActivity.TAG, "WJJYMM");
                        ContainerActivity.to(mContext, CheckCertNoFragment.class.getSimpleName(), bundle);
                        payPassWordView.clearnData();
                    } else {
                        payPassWordView.clearnData();
                        dialog.dismiss();
                    }
                }).setStandardStyle(4);
            } else {
                showDialog("交易密码输入错误,\n   还可以输入" + failSum + "次", "忘记密码", "重新输入", (dialog, which) -> {
                    if (which == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
                        bundle.putSerializable(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                        bundle.putString(SetTransactionPwdActivity.TAG, "WJJYMM");
                        ContainerActivity.to(mContext, CheckCertNoFragment.class.getSimpleName(), bundle);
                        payPassWordView.clearnData();
                    } else {
                        //清空并重新输入
                        payPassWordView.clearnData();
                    }
                }).setStandardStyle(4);
            }
        }
    }

    @Override
    public void certificateCallBack(boolean isSuccess, String email, String error) {
        if (isSuccess) {
            Intent intent = new Intent(mContext, ApplySubmitSuccessOrFailActivity.class);
            if (email != null) {
                intent.putExtra("email", email);
            }
            startActivity(intent);
            setResult(RESULT_OK);
        } else {
            //关闭页面然后弹失败弹窗
            Intent data = new Intent();
            //原因文案规范
            data.putExtra("reason", error);
            setResult(RESULT_CANCELED, data);
        }
        finish();
    }

    @Override
    public void commitApplyRisk() {

    }

    @Override
    protected void onDestroy() {
        payDataImpl.onDestroy();
        super.onDestroy();
    }
}
