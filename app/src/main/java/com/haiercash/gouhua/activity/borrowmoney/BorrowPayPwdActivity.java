package com.haiercash.gouhua.activity.borrowmoney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.AntiHijackingUtil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.accountsettings.SetTransactionPwdActivity;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.SaveOrderBean;
import com.haiercash.gouhua.beans.getpayss.LoanCoupon;
import com.haiercash.gouhua.fragments.mine.CheckCertNoFragment;
import com.haiercash.gouhua.tplibrary.livedetect.FaceCheckActivity;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.CustomCodeView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/6/22.
 * 借款-输入交易密码
 * tag:EDJH/XJD 标识，这两种value不能更改，如果需要新增value要告诉我
 * EDJH:额度激活
 * XJD ：现金贷
 */
public class BorrowPayPwdActivity extends BaseActivity implements BorrowPayDataImpl.IBorrowPayView {
    @BindView(R.id.view_pay)
    CustomCodeView payPassWordView;
    private final AtomicBoolean mRefreshing = new AtomicBoolean(false);
    /**
     * tag标识<br/>
     * XGJYMM:修改交易密码<br/>
     * EDJH:额度激活<br/>
     * XJD ：现金贷<br/>
     */
    private String getTag;
    private String orderNo;//订单号
    private LoanCoupon coupon;//免息券
    private String applSeq;//流水号
    private SaveOrderBean.UniteLoanInfoBean uniteLoanInfo;
    private BorrowPayDataImpl payDataImpl;

    @Override
    protected void onStop() {
        super.onStop();
        if (null != payPassWordView) {
            payPassWordView.clearnData();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_repayment;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("请输入交易密码");
        SystemUtils.setWindowSecure(this);
        getTag = getIntent().getStringExtra("tag");
        //联合放款信息
        uniteLoanInfo = (SaveOrderBean.UniteLoanInfoBean) getIntent().getSerializableExtra("uniteLoanInfoDate");
        initData();
        getEnterKey();
        Intent intent = getIntent();
        payDataImpl = new BorrowPayDataImpl(this, orderNo,
                intent.getStringExtra("borrowMoney"),
                intent.getStringExtra("borrowTnr"),
                intent.getStringExtra("typCde"),
                coupon, applSeq, uniteLoanInfo, this,
                getPageCode());
    }

    @Override
    protected void onPause() {
        AntiHijackingUtil.activityPageOnPause(this);
        super.onPause();
    }

    private void initData() {
        if ("EDJH".equals(getTag)) {
            applSeq = getIntent().getStringExtra("applSeq");
        } else if ("XJD".equals(getTag)) {
            Intent intent = getIntent();
            orderNo = intent.getStringExtra("orderNo");
            coupon = (LoanCoupon) intent.getSerializableExtra("coupon");
            applSeq = intent.getStringExtra("applSeq");
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_forget_password})
    public void viewOnClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                KeyBordUntil.hideKeyBord(this);
                finish();
                break;
            case R.id.tv_forget_password:
                Bundle bundle = new Bundle();
                bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
                bundle.putSerializable(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                bundle.putString(SetTransactionPwdActivity.TAG, "WJJYMM");
                ContainerActivity.to(this, CheckCertNoFragment.class.getSimpleName(), bundle);
                break;
            default:
                break;
        }
    }

    /**
     * 确定键
     */
    private void getEnterKey() {
        payPassWordView.setOnInputFinishedListener(password -> {
            if (CheckUtil.isEmpty(password)) {
                postCommitEvent("false", "请输入交易密码");
                UiUtil.toast("请输入交易密码");
                return;
            }
            if (password.length() != 6) {
                postCommitEvent("false", "请输入完整的交易密码");
                UiUtil.toast("请输入完整的交易密码");
                return;
            }
            //为了防止重复点击
            if (!mRefreshing.compareAndSet(false, true)) {
                return;
            }
            postCommitEvent("true", "");
            payDataImpl.payPwd(payPassWordView.getCurrentWord());
        });
    }

    @Override
    public void payPwdCallBack(boolean payResult, int failSum) {
        mRefreshing.set(false);
        if (payResult) {
            if ("XJD".equals(getTag)) {
                payDataImpl.signingContract();
            }
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

    }

    @Override
    public void commitApplyRisk() {
        Intent intent = new Intent();
        intent.setClass(mContext, BorrowingResultActivity.class);
        intent.putExtra("orderNo", orderNo);
        if (coupon != null && !CheckUtil.isEmpty(coupon.getCouponNo())) {
            intent.putExtra("couponNo", coupon.getCouponNo());
        }
        intent.putExtra("applSeq", applSeq);
        startActivity(intent);
        Activity activity = ActivityUntil.findActivity(GoBorrowMoneyActivity.class);
        if (activity != null) {
            activity.finish();
        }
        Activity borrowAgreementActivity = ActivityUntil.findActivity(BorrowAgreementActivity.class);
        if (borrowAgreementActivity != null) {
            borrowAgreementActivity.finish();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        payDataImpl.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected String getPageCode() {
        return "XJD".equals(getTag) ? "InputTransactionPasswordPage" : "";
    }

    //点击提交上传事件
    private void postCommitEvent(String success, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("button_name", "提交");
        UMengUtil.commonCompleteEvent("InputTransactionPassword", map, success, failReason, getPageCode());
    }
}
