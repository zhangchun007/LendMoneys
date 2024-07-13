package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.RepaymentInfoAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.getpayss.GetPaySsAndCoupons;
import com.haiercash.gouhua.beans.getpayss.GetPaySsBeanRtn;
import com.haiercash.gouhua.beans.getpayss.LoanCoupon;
import com.haiercash.gouhua.databinding.ActivityRepaymentDetailsNewBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 7/26/22
 * @Version: 1.0
 */
public class RepaymentDetailsNewActivity extends BaseActivity implements View.OnClickListener {

    private String inputAmount;
    private String typCde;
    private String applyTnrTyp;
    private String applyTnr;
    private String mtdCde;
    private LoanCoupon currLoanCoupon;
    private ActivityRepaymentDetailsNewBinding binding;
    private String totalFeeAmt;
    private RepaymentInfoAdapter mAdapter;

    @Override
    protected ActivityRepaymentDetailsNewBinding initBinding(LayoutInflater inflater) {
        return binding = ActivityRepaymentDetailsNewBinding.inflate(inflater);
    }


    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {

        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        showProgress(true);
        setonClickByViewId(R.id.layout_close, R.id.layout_certain, R.id.ivInterestClose, R.id.img_tips, R.id.layout_dialog_tips);
        binding.layoutClose.setOnClickListener(v -> finish());
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
        mAdapter = new RepaymentInfoAdapter(currLoanCoupon);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(layoutManager);
        layoutManager.setSmoothScrollbarEnabled(true);
        binding.recycler.setHasFixedSize(true);
        binding.recycler.setNestedScrollingEnabled(false);
        binding.recycler.setAdapter(mAdapter);
        binding.layoutRoot.setVisibility(View.GONE);
        getPaySs();
    }

    /*还款试算*/
    private void getPaySs() {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("typCde", typCde);//贷款品种代码
        map.put("apprvAmt", inputAmount);//贷款金额
        map.put("applyTnrTyp", applyTnrTyp);//期限类型
        map.put("applyTnr", applyTnr);//期限
        map.put("mtdCde", mtdCde);//还款方式     无则传空、新版APP标准化产品则必传
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
        map.put("speSeq", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_EDU_SPE_SEQ)));
        netHelper.postService(ApiUrl.url_huankuanshisuan_useCoupon, map, GetPaySsAndCoupons.class);

    }

    private String getFormatted(String amount) {
        return CheckUtil.formattedAmount1(amount);
    }

    @Override
    public void onSuccess(Object response, String url) {
        if (response == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        showProgress(false);
        binding.layoutRoot.setVisibility(View.VISIBLE);
        GetPaySsAndCoupons paySsAndCoupons = (GetPaySsAndCoupons) response;
        GetPaySsBeanRtn getPaySsBeanRtn = paySsAndCoupons.getPaySsResult();
        String repaymentTotalAmt = getPaySsBeanRtn.getRepaymentTotalAmt();//总还款额
        String totalNormInt = getPaySsBeanRtn.getTotalNormInt();//利息总额
        totalFeeAmt = getPaySsBeanRtn.getTotalFeeAmt();//费用总额


        binding.title.setText("应还总额" + CheckUtil.showThound(getFormatted(repaymentTotalAmt)) + "元");
        //券相关展示
        if (currLoanCoupon != null) {
            if (!CheckUtil.isZero(currLoanCoupon.getDiscRepayAmt()))
                binding.title.setText("应还总额" + CheckUtil.showThound(getFormatted(repaymentTotalAmt)) + "元，" + "优惠后" + CheckUtil.showThound(currLoanCoupon.getDiscRepayAmt()) + "元");
            if (isTheBestCouponCanUse() && "N".equals(currLoanCoupon.getBatchDeduction())) {
                binding.tvTitleTips.setVisibility(View.VISIBLE);
            } else {
                binding.tvTitleTips.setVisibility(View.GONE);
            }
        }
        binding.tvLoanMoneyNum.setText("¥" + getFormatted(inputAmount));
        binding.tvInterestNum.setText("¥" + getFormatted(totalNormInt));

        if (isTheBestCouponCanUse() && !TextUtils.isEmpty(currLoanCoupon.getDiscTotalNormInt())) {
            binding.tvCouponInterestNum.setVisibility(View.VISIBLE);
            binding.tvCouponInterestNum.setText("（优惠后" + currLoanCoupon.getDiscTotalNormInt() + "）");
        } else {
            binding.tvCouponInterestNum.setVisibility(View.GONE);
        }

        //
        String dayRepay = "";
        if (getPaySsBeanRtn.getMx().get(0).getData() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(getPaySsBeanRtn.getMx().get(0).getData());
            dayRepay = cal.get(Calendar.DAY_OF_MONTH) + "";
        }
        binding.tvQishu.setText("共" + getPaySsBeanRtn.getMx().size() + "期·每月" + dayRepay + "日还款");
        mAdapter.setNewData(getPaySsBeanRtn.getMx());
    }

    @Override
    public void onError(String error) {
        super.onError(error);
        binding.layoutRoot.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivInterestClose:
            case R.id.layout_close:
            case R.id.layout_certain:
                finish();
                break;
            case R.id.img_tips:
            case R.id.layout_dialog_tips:
                showDialog("此还款日仅为试算，具体还款日以实际借款成功日期计算，具体可在借款详情页查看");
                break;

        }
    }

    @Override
    protected String getPageCode() {
        return "PlanRepayPage";
    }

    /**
     * 当前最优优惠券能否可用
     *
     * @return
     */
    private boolean isTheBestCouponCanUse() {
        if (currLoanCoupon != null && "Y".equals(currLoanCoupon.getCanUseState()))
            return true;
        return false;
    }
}
