package com.haiercash.gouhua.activity.borrowmoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.bill.RepaymentDetailsAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseDialog;
import com.haiercash.gouhua.beans.getpayss.GetPaySsAndCoupons;
import com.haiercash.gouhua.beans.getpayss.GetPaySsBeanRtn;
import com.haiercash.gouhua.beans.getpayss.LoanCoupon;
import com.haiercash.gouhua.databinding.ActivityRepaymentDetailsBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Limige on 2017-07-01.
 * 还款计划（计划）
 */

public class RepaymentDetailsActivity extends BaseActivity {
    private ActivityRepaymentDetailsBinding binding;
    private String inputAmount;
    private String typCde;
    private String applyTnrTyp;
    private String applyTnr;
    private String mtdCde;
    private String totalFeeAmt;
    private LoanCoupon currLoanCoupon;
    private RepaymentDetailsAdapter mAdapter;
    //private String ;feeRateByPeriods

//    public RepaymentDetailsActivity() {
//        super(R.layout.activity_repayment_details);
//    }

    @Override
    protected ActivityRepaymentDetailsBinding initBinding(LayoutInflater inflater) {
        binding = ActivityRepaymentDetailsBinding.inflate(inflater);
        binding.barHeader.setTitleBackgroundColor(0);
        return binding;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("还款计划", "#FFFFFFFF");
        showProgress(true);
        setonClickByViewId(R.id.iv_details);
        Intent intent = getIntent();
        inputAmount = intent.getStringExtra("inputAmount");
        typCde = intent.getStringExtra("typCde");
        applyTnrTyp = intent.getStringExtra("applyTnrTyp");
        applyTnr = intent.getStringExtra("applyTnr");
        mtdCde = intent.getStringExtra("mtdCde");
        Serializable serializableExtra = intent.getSerializableExtra(LoanCoupon.class.getSimpleName());
        if (serializableExtra instanceof LoanCoupon) {
            currLoanCoupon = (LoanCoupon) serializableExtra;
        }
        //binding.tvRate.setBackground(DrawableUtils.shapeColorRadius(Color.TRANSPARENT, UiUtil.dip2px(this, 15)));
        binding.tvRepaymentAll.setTypeface(FontCustom.getMediumFont(this));
        mAdapter = new RepaymentDetailsAdapter(currLoanCoupon);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.lvRepaymentDetails.setLayoutManager(layoutManager);
        layoutManager.setSmoothScrollbarEnabled(true);
        binding.lvRepaymentDetails.setHasFixedSize(true);
        binding.lvRepaymentDetails.setNestedScrollingEnabled(false);
        binding.lvRepaymentDetails.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            View item = view.findViewById(R.id.ll_cost);
            if (item.getVisibility() == View.GONE) {
                item.setVisibility(View.VISIBLE);
            } else {
                item.setVisibility(View.GONE);
            }
            //关闭之前展开的
            if (position != mAdapter.getCurrentOpenPosition() && mAdapter.getCurrentOpenPosition() >= 0
                    && mAdapter.getCurrentOpenPosition() < mAdapter.getItemCount()) {
                mAdapter.getData().get(mAdapter.getCurrentOpenPosition()).isOpened = false;
                mAdapter.notifyItemChanged(mAdapter.getCurrentOpenPosition());
            }
            mAdapter.getData().get(position).isOpened = !mAdapter.getData().get(position).isOpened;
            mAdapter.setCurrentOpenPosition(position);
        });
        getPaySs();
        setBarLeftImage(R.drawable.iv_back_white, v -> {
            finish();
        });
    }

    /*还款试算*/
    private void getPaySs() {
        Map<String, String> map = new HashMap<>();
        map.put("typCde", typCde);//贷款品种代码
        map.put("apprvAmt", inputAmount);//贷款金额
        map.put("applyTnrTyp", applyTnrTyp);//期限类型
        map.put("applyTnr", applyTnr);//期限
        map.put("mtdCde", mtdCde);//还款方式     无则传空、新版APP标准化产品则必传
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
        map.put("speSeq", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_EDU_SPE_SEQ)));
        netHelper.postService(ApiUrl.url_huankuanshisuan_useCoupon, map, GetPaySsAndCoupons.class);

        /*       测试数据代码  start
        GetPaySsAndCoupons success = new GetPaySsAndCoupons();
        success.setPaySsResult(new GetPaySsBeanRtn().setTestBean());
        List<LoanCoupon> couponList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            couponList.add(new LoanCoupon().setTestBean(i + 1));
        }
        success.setLoanCoupons(couponList);
        onSuccess(success, ApiUrl.url_huankuanshisuan_useCoupon);
        end*/


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(Object response, String flag) {
        if (response == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        showProgress(false);
        GetPaySsAndCoupons paySsAndCoupons = (GetPaySsAndCoupons) response;
        GetPaySsBeanRtn getPaySsBeanRtn = paySsAndCoupons.getPaySsResult();
        String repaymentTotalAmt = getPaySsBeanRtn.getRepaymentTotalAmt();//总还款额
        String totalNormInt = getPaySsBeanRtn.getTotalNormInt();//利息总额
        totalFeeAmt = getPaySsBeanRtn.getTotalFeeAmt();//费用总额
        //feeRateByPeriods = getPaySsBeanRtn.feeRateByPeriods;
        binding.tvRepaymentAll.setText(CheckUtil.showThound(getFormatted(repaymentTotalAmt)));
        //券相关展示
        if (currLoanCoupon != null) {
            binding.tvCouponTip.setText("优惠后" + CheckUtil.showThound(currLoanCoupon.getDiscRepayAmt()) + "元");
            binding.llRepaymentCouponTip.setVisibility(View.VISIBLE);
            binding.tvRepaymentCouponTip.setText("预计可优惠" + CheckUtil.formattedAmount(currLoanCoupon.getDiscValue()));
            binding.tvRepaymentCouponTip.setVisibility(View.VISIBLE);
        } else {
            binding.llRepaymentCouponTip.setVisibility(View.GONE);
            binding.tvRepaymentCouponTip.setVisibility(View.GONE);
        }
        //券的使用详情
        if (currLoanCoupon != null && !CheckUtil.isEmpty(currLoanCoupon.getBatchDetailDesc())) {
            binding.tvRepaymentBottomTipTitle.setVisibility(View.VISIBLE);
            binding.tvRepaymentBottomTip.setText(getString(R.string.repayment_bottom_interest_desc,
                    CheckUtil.showThound(getFormatted(repaymentTotalAmt)),
                    CheckUtil.formattedAmount(currLoanCoupon.getDiscValue()),
                    CheckUtil.showThound(currLoanCoupon.getDiscRepayAmt()),
                    CheckUtil.isEmpty(currLoanCoupon.getCalVol()) ? "任意一" : UiUtil.getStr("第", currLoanCoupon.getCalVol())));
            binding.tvRepaymentBottomTip.setVisibility(View.VISIBLE);
        } else {
            binding.tvRepaymentBottomTipTitle.setVisibility(View.GONE);
            binding.tvRepaymentBottomTip.setVisibility(View.GONE);
        }
        //binding.tvApplyTnr.setText(applyTnr);
        getRepaymentMessage(totalNormInt, totalFeeAmt);
        //默认展开第一个
        mAdapter.setCurrentOpenPosition(0);
        mAdapter.setNewData(getPaySsBeanRtn.getMx());
    }

    private void getRepaymentMessage(String totalNormInt, String totalFeeAmt) {//, String totalFeeAmt
        binding.tvCapital.setText(getFormatted(inputAmount));
        //message += "利息" + getFormatted(totalNormInt) + "元，";
        binding.llInterest.setVisibility(View.VISIBLE);
        binding.tvInterest.setText(getFormatted(totalNormInt));
        // message += "手续费" + getFormatted(totalFeeAmt) + "元";
        binding.tvApplyTnr.setText(getFormatted(totalFeeAmt));
        binding.llFeeAmt.setVisibility(CheckUtil.isZero(totalFeeAmt) ? View.GONE : View.VISIBLE);
       /* if (message.endsWith("，")) {
            message = message.substring(0, message.length() - 1);
        }
        return message;*/
    }


    private String getFormatted(String amount) {
        return CheckUtil.formattedAmount1(amount);
    }

    private BaseDialog dialog;

    @Override
    public void onClick(View view) {
        if (dialog == null) {
            dialog = BaseDialog.getDialog(this, "提示", "", "知道了", null)
                    .setButtonTextColor(1, R.color.colorPrimary);
            //String message = "应还款=本金+利息+手续费 ";
            //if (!CheckUtil.isEmpty(totalFeeAmt) && Double.parseDouble(totalFeeAmt) > 0) {
            //    message += "（本金 *" + feeRateByPeriods + "%）\nⓘ 手续费仅第1期收取";
            //}
            //Spannable WordtoSpan = new SpannableString(message);
            //if (message.contains("ⓘ")) {
            //    WordtoSpan.setSpan(new AbsoluteSizeSpan((int) UiUtil.sp2px(this, 11)), 0, message.indexOf("ⓘ"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //    WordtoSpan.setSpan(new AbsoluteSizeSpan((int) UiUtil.sp2px(this, 10)), message.indexOf("ⓘ"), message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //}
            String message = "应还款=本金+利息" + (CheckUtil.isZero(totalFeeAmt) ? "" : "+手续费 ");
            dialog.getmHtvMessage().setText(message);
//            dialog.getmHtvMessage().setText("应还款=本金+利息 ");
        }
        dialog.show();
    }

}
