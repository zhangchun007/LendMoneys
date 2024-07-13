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

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.InterestFreeBean;
import com.haiercash.gouhua.beans.PopupWindowBean;
import com.haiercash.gouhua.beans.repayment.CashierInfo;
import com.haiercash.gouhua.databinding.ActivityRepaymentAmountInfoBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.repayment.RepaymentConfirmActivity;
import com.haiercash.gouhua.uihelper.NormalNewPopWindow;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 非逾期并且非联合放款选择《免息券》还款金额确认页
 */
public class RepaymentAmountInfoActivity extends BaseActivity {
    private ActivityRepaymentAmountInfoBinding binding;
    public static final int REQUEST_CODE = 10001;
    private List<Map<String, String>> applSeqLoanNo;
    private CashierInfo mCashierInfo;
    private InterestFreeBean interestFreeBean;
    private InterestFreeBean.RepayCouponsBean lastChooseCoupon;//上次选择的免息券，用于记录和失败回退到上一波UI
    private InterestFreeBean.RepayCouponsBean chooseCoupon;//选择的免息券

    @Override
    protected ActivityRepaymentAmountInfoBinding initBinding(LayoutInflater inflater) {
        return binding = ActivityRepaymentAmountInfoBinding.inflate(inflater);
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

        setTitle("还款金额信息");
        if (!CheckUtil.isEmpty(applSeqLoanNo)) {
            applSeqLoanNo.clear();
        }
        setonClickByView(binding.tvInterestFreeAmountClick, binding.btnToPay, binding.ivInterestClose, binding.tvInterestFreeDes);
        mCashierInfo = (CashierInfo) getIntent().getSerializableExtra("cashInfo");
        interestFreeBean = (InterestFreeBean) getIntent().getSerializableExtra("chooseList");
        //noinspection unchecked
        applSeqLoanNo = (List<Map<String, String>>) getIntent().getSerializableExtra("repayInfo");
        binding.tvTotalAmountTitle.setTypeface(FontCustom.getMediumFont(this));
        binding.tvToPayTotal.setTypeface(FontCustom.getMediumFont(this));
        binding.tvInterestFreeAmount.setTypeface(FontCustom.getMediumFont(this));
        binding.tvCouponUseDesc.setTypeface(FontCustom.getMediumFont(this));
        resetViewData(mCashierInfo, false);
        //放在设置UI后面，因为前一页面传过来的mCashierInfo是不传券号得来的(防止取消选中券等时接口报错，从而可以用默认值)
        chooseCoupon = (InterestFreeBean.RepayCouponsBean) getIntent().getSerializableExtra("interestFreeCoupon");
        if (isUseCoupon()) {
            getRepayCalculate();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvInterestFreeAmountClick) {
            //跳转免息券选择列表
            Intent intent = new Intent(this, InterestFreeChooseListActivity.class);
            if (chooseCoupon != null) {
                intent.putExtra("choosedCouponNo", chooseCoupon.getCouponNo());
            }
            intent.putExtra("chooseList", interestFreeBean);
            startActivityForResult(intent, REQUEST_CODE);
        } else if (view.getId() == R.id.btnToPay) {
            postEvent();
            if (chooseCoupon != null && !"Y".equals(chooseCoupon.getCanUseState())) {
                RepaymentConfirmActivity.balanceCashier(this, mCashierInfo, null, true);
            } else {
                RepaymentConfirmActivity.balanceCashier(this, mCashierInfo, chooseCoupon, true);
            }
            finish();
        } else if (view.getId() == R.id.ivInterestClose) {
            finish();
        } else if (view.getId() == R.id.tvInterestFreeDes) {
            PopupWindowBean bean = new PopupWindowBean();
            bean.setTitle("免息券通用说明");
            bean.setContent(this.getResources().getText(R.string.interest_free_des).toString());
            bean.setButtonTv("我知道了");
            bean.setButtonBack(R.drawable.bg_btn_commit);
            new NormalNewPopWindow(this, bean).showAtLocation(binding.tvInterestFreeDes);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == InterestFreeChooseListActivity.RESULT_CODE && requestCode == REQUEST_CODE) {
            lastChooseCoupon = chooseCoupon;
            chooseCoupon = data != null ? (InterestFreeBean.RepayCouponsBean) data.getSerializableExtra("interestFreeCoupon") : null;
            getRepayCalculate();
        }
    }

    /**
     * 还款试算
     */
    private void getRepayCalculate() {
        showProgress(true);
        Map<String, Object> map = new HashMap<>(4);
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
        map.put("type", getRepaymentType());
        String isRepayDay = getIntent().getStringExtra("isRepayDay");
        if (!CheckUtil.isEmpty(isRepayDay)) {
            map.put("isRepayDay", isRepayDay);
        }
        if (!CheckUtil.isEmpty(applSeqLoanNo) && applSeqLoanNo.size() == 1) {
            if (isUseCoupon() && "Y".equals(chooseCoupon.getCanUseState())) {
                applSeqLoanNo.get(0).put("couponNo", chooseCoupon.getCouponNo());
            } else {
                applSeqLoanNo.get(0).remove("couponNo");
            }
        }
        map.put("isRsa", "Y");
        map.put("needCalculatedLoans", applSeqLoanNo);
        netHelper.postService(ApiUrl.URL_CALCULATE_REPAYMENT_AMOUNT, map, CashierInfo.class, true);
    }

    @Override
    public void onSuccess(Object success, String url) {
        showProgress(false);
        if (ApiUrl.URL_CALCULATE_REPAYMENT_AMOUNT.equals(url)) {
            mCashierInfo = (CashierInfo) success;
            resetViewData(mCashierInfo, true);
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        if (ApiUrl.URL_CALCULATE_REPAYMENT_AMOUNT.equals(url)) {
            //失败则回到上一节点
            chooseCoupon = lastChooseCoupon;
        }
    }

    /**
     * 无可用免息券/请求参数为空
     */
    private void showCoupon(boolean show) {
        binding.tvInterestFreeDes.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.rlInterestFreeAmount.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setCouponUi(CashierInfo mCashierInfo, boolean needShow) {
        if (interestFreeBean != null && !CheckUtil.isEmpty(interestFreeBean.getCoupons())) {
            if (chooseCoupon != null) {
                binding.tvInterestFreeAmount.setText(UiUtil.getStr("-¥", CheckUtil.formattedAmount1(mCashierInfo.getCoupUseAmt())));
                binding.tvInterestFreeAmount.setTextColor(getResources().getColor(R.color.color_ff5151));
                if (chooseCoupon.hasBind() && !"Y".equals(chooseCoupon.getCanUseState())) {
                    binding.tvInterestFreeAmount.setText(TextUtils.isEmpty(chooseCoupon.getUnUseDesc()) ? getString(R.string.can_not_use_overdue) : chooseCoupon.getUnUseDesc());
                    binding.tvInterestFreeAmount.setTextColor(getResources().getColor(R.color.color_909199));
                } else {
                    binding.tvHasDiscount.setText(SpannableStringUtils.getBuilder(this, "已优惠")
                            .append(SpannableStringUtils.getBuilder(this,
                                    UiUtil.getStr(CheckUtil.formattedAmount1(mCashierInfo.getCoupUseAmt()), "元"))
                                    .setBold().setForegroundColor(0xFFFF5151).create()).create());
                    binding.tvHasDiscount.setVisibility(View.VISIBLE);
                    binding.tvCouponUseDesc.setText(chooseCoupon.hasBind() ? "已绑定免息券" : (chooseCoupon.isBest() ? "已匹配当前最优券" : ""));
                    binding.tvCouponUseDesc.setTextColor(getResources().getColor(R.color.color_ff5151));
                }
                binding.tvCouponUseDesc.setVisibility(CheckUtil.isEmpty(binding.tvCouponUseDesc.getText().toString()) ? View.GONE : View.VISIBLE);
            } else {
                if (getIntent().getBooleanExtra("isOverdue", false)) {
                    binding.tvInterestFreeAmount.setText(UiUtil.getStr(getString(R.string.can_not_use_overdue)));
                    binding.tvInterestFreeAmount.setTextColor(getResources().getColor(R.color.color_909199));
                } else {
                    if (needShow) {
                        binding.tvInterestFreeAmount.setText(UiUtil.getStr("可选1张"));
                        binding.tvInterestFreeAmount.setTextColor(getResources().getColor(R.color.color_ff5151));
                    }
                }
                binding.tvHasDiscount.setVisibility(View.GONE);
                binding.tvCouponUseDesc.setVisibility(View.GONE);
            }
            showCoupon(true);
        } else {
            showCoupon(false);
        }
        binding.tvToPayTotal.setText(UiUtil.getStr("￥", CheckUtil.formattedAmount1(mCashierInfo.getStayAmount())));
    }

    /**
     * 重置数据
     */
    private void resetViewData(CashierInfo mCashierInfo, boolean needShow) {
        if (mCashierInfo == null) {
            return;
        }
        if (CheckUtil.isEmpty(mCashierInfo.getSetlTotalAmtCr()) || CheckUtil.isZero(mCashierInfo.getSetlTotalAmtCr())) {
            binding.tvRepayAmountDesc.setVisibility(View.GONE);
            binding.tvRepayAmount.setVisibility(View.GONE);
        } else {
            binding.tvRepayAmountDesc.setVisibility(View.VISIBLE);
            binding.tvRepayAmount.setVisibility(View.VISIBLE);
            binding.tvRepayAmount.setText(UiUtil.getStr("-¥", mCashierInfo.getSetlTotalAmtCr()));
        }
        binding.tvAmount.setText(UiUtil.getStr("¥", mCashierInfo.getPrcpAmt()));
        binding.tvRateAmount.setText(UiUtil.getStr("¥", mCashierInfo.getNormInt()));
        //binding.tvCommissionAmount.setText(UiUtil.getStr("¥", mCashierInfo.getPsFeeAmt()));
        setCouponUi(mCashierInfo, needShow);
        if (CheckUtil.isZero(mCashierInfo.getSettleAwardAmt())) {
            binding.tvSettleAwardAmtTitle.setVisibility(View.GONE);
            binding.tvSettleAwardAmt.setVisibility(View.GONE);
        } else {
            binding.tvSettleAwardAmtTitle.setVisibility(View.VISIBLE);
            binding.tvSettleAwardAmt.setVisibility(View.VISIBLE);
            binding.tvSettleAwardAmt.setText(UiUtil.getStr("-¥", mCashierInfo.getSettleAwardAmt()));
        }

        //逾期罚息	odIntAmt	若为空或为0则不展示否则展示
        binding.tvCommissionAmount.setText(UiUtil.getStr("￥", mCashierInfo.psFeeAmtNew));
        if (CheckUtil.isZero(mCashierInfo.psFeeAmtNew)) {
            binding.llCommissionAmount.setVisibility(View.GONE);
        } else {
            binding.llCommissionAmount.setVisibility(View.VISIBLE);
        }
        //逾期罚息	odIntAmt	若为空或为0则不展示否则展示
        binding.tvOdIntAmt.setText(UiUtil.getStr("￥", mCashierInfo.getOdIntAmt()));
        if (CheckUtil.isZero(mCashierInfo.getOdIntAmt())) {
            binding.llOdIntAmt.setVisibility(View.GONE);
        } else {
            binding.llOdIntAmt.setVisibility(View.VISIBLE);
        }
        //逾期手续费（滞纳金+违约金）	新字段：odFeeAmt	若为空或为0则不展示否则展示
        binding.tvOdFeeAmt.setText(UiUtil.getStr("￥", mCashierInfo.odFeeAmt));
        if (CheckUtil.isZero(mCashierInfo.odFeeAmt)) {
            binding.llOdFeeAmt.setVisibility(View.GONE);
        } else {
            binding.llOdFeeAmt.setVisibility(View.VISIBLE);
        }
        //提前还款手续费	新字段：earlyRepayAmt	若为空或为0则不展示否则展示
        binding.tvEarlyRepayAmt.setText(UiUtil.getStr("￥", mCashierInfo.earlyRepayAmt));
        if (CheckUtil.isZero(mCashierInfo.earlyRepayAmt)) {
            binding.llEarlyRepayAmt.setVisibility(View.GONE);
        } else {
            binding.llEarlyRepayAmt.setVisibility(View.VISIBLE);
        }
    }

    private String getRepaymentType() {
        String repaymentType = getIntent().getStringExtra("repaymentType");
        if (CheckUtil.isEmpty(repaymentType)) {
            repaymentType = "all";
        }
        return repaymentType;
    }

    @Override
    protected String getPageCode() {
        return "RepayMoneyPage";
    }

    //点击去还款埋点

    /**
     * 待还总额	notrepay_sum	所有待还款总额加起来
     * 选择笔数	select_count	未选是0，单选是1，多选是计算n
     * 是否逾期	overdue_flag	true、false
     * 待还明细 notrepaydetail	还款借据 loan_id	"动态传，这三个字段KV拼接JSON串，单选1条，多选多条数据   { ""notrepaydetail"": [
     * 按钮名称	button_name	去还款
     */
    private void postEvent() {
        Map<String, Object> map = new HashMap<>();
        double borrow_memory;
        double interest;
        double service_charge;
        double freeinterest_money;
        try {
            borrow_memory = CheckUtil.isEmpty(getRealMoney(binding.tvAmount.getText().toString())) ?
                    0 : Double.parseDouble(getRealMoney(binding.tvAmount.getText().toString()));
            interest = CheckUtil.isEmpty(getRealMoney(binding.tvRateAmount.getText().toString())) ?
                    0 : Double.parseDouble(getRealMoney(binding.tvRateAmount.getText().toString()));

            service_charge = CheckUtil.isEmpty(getRealMoney(binding.tvCommissionAmount.getText().toString())) ?
                    0 : Double.parseDouble(getRealMoney(binding.tvCommissionAmount.getText().toString()));

            String freeInterest = isUseCoupon() ? mCashierInfo != null ? mCashierInfo.getCoupUseAmt() : "" : "0";
            freeinterest_money = CheckUtil.isEmpty(freeInterest) ? 0 : Double.parseDouble(freeInterest);

        } catch (Exception e) {
            borrow_memory = 0;
            interest = 0;
            service_charge = 0;
            freeinterest_money = 0;
        }

        map.put("button_name", "去还款");
        map.put("borrow_memory", borrow_memory);
        map.put("interest", interest);
        map.put("service_charge", service_charge);
        map.put("freeinterestticket_flag", isUseCoupon() ? "true" : "false");
        map.put("freeinterestticket_id", isUseCoupon() ? chooseCoupon.getCouponNo() : "");
        map.put("freeinterest_money", freeinterest_money);
        map.put("freeinterest_desc", "");
        UMengUtil.onEventObject("Repay_Click", map, getPageCode());
    }

    private String getRealMoney(String money) {
        if (TextUtils.isEmpty(money)) {
            return "";
        }
        if (money.contains("¥")) {
            return money.replace("¥", "").trim();
        } else {
            return money;
        }
    }

    private boolean isUseCoupon() {
        return chooseCoupon != null && !CheckUtil.isEmpty(chooseCoupon.getCouponNo());
    }
}
