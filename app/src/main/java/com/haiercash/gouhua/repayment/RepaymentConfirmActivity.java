package com.haiercash.gouhua.repayment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.HmacSHA256Utils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.InterestFreeBean;
import com.haiercash.gouhua.beans.bankcard.QueryCardBean;
import com.haiercash.gouhua.beans.repayment.CashierInfo;
import com.haiercash.gouhua.beans.repayment.PaymentResult;
import com.haiercash.gouhua.beans.repayment.Repayment;
import com.haiercash.gouhua.beans.repayment.SignBankCardNeed;
import com.haiercash.gouhua.choosebank.ChooseBankCardFragment;
import com.haiercash.gouhua.databinding.ActivityCashierRepaymentBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.uihelper.CallPhoneNumberHelper;
import com.haiercash.gouhua.uihelper.RepaymentDetailPopupWindow;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.wxapi.WxUntil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Sun
 * Date :    2018/5/12
 * FileName: RepaymentConfirmActivity
 * Description: 收银台：还款订单确认
 */
public class RepaymentConfirmActivity extends BaseActivity {
    private ActivityCashierRepaymentBinding binding;
    private CashierInfo mCashierInfo;
    private InterestFreeBean.RepayCouponsBean chooseCoupon;
    private static final String TAG_BALANCE = "info";
    private static final String TAG_COUPON = "interestFreeCoupon";
    private static final String TAG_TRIALED = "isTrialed";
    private ChooseBankCardFragment chooseBankCardFragment;
    /**
     * 是否选择了还款支付方式
     */
    private boolean isChoosePayWay = false;
    private SignBankCardHelper signBankCardHelper;
    //选择的银行
    private String mBankCardNo;
    private QueryCardBean mCardBean;
    private RepaymentDetailPopupWindow repPopupWindow;

    @Override
    protected ActivityCashierRepaymentBinding initBinding(LayoutInflater inflater) {
        return binding = ActivityCashierRepaymentBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("还款");
        signBankCardHelper = new SignBankCardHelper(this);
        setBarRightText("其他问题?", ContextCompat.getColor(mContext, R.color.colorPrimary), v -> {
            CallPhoneNumberHelper.callServiceNumber(RepaymentConfirmActivity.this, "如遇到无法还款情况，请您及时联系客服帮助还款，避免产生逾期。",
                    "拨打客服热线", "取消");
        });
        setonClickByViewId(R.id.btRepayment, R.id.tvDetail, R.id.rlWxPay);
        chooseBankCardFragment = new ChooseBankCardFragment();
        changeFragment(R.id.content, chooseBankCardFragment);
        mCashierInfo = (CashierInfo) getIntent().getSerializableExtra(TAG_BALANCE);
        chooseCoupon = (InterestFreeBean.RepayCouponsBean) getIntent().getSerializableExtra(TAG_COUPON);
        if (mCashierInfo == null) {
            UiUtil.toast("获取还款参数失败，请稍后重试");
            finish();
            return;
        }
        binding.tvAmount.setText(CheckUtil.formattedAmount1(mCashierInfo.getStayAmount()));
        binding.btRepayment.setText(UiUtil.getStr("确认还款¥", CheckUtil.formattedAmount1(mCashierInfo.getStayAmount())));
        binding.tvMoneyDetail.setVisibility(getIntent().getBooleanExtra(TAG_TRIALED, false) ? View.GONE : View.VISIBLE);
        binding.tvDetail.setVisibility(getIntent().getBooleanExtra(TAG_TRIALED, false) ? View.VISIBLE : View.GONE);
        //获取资源图片
        Drawable leftDrawable = ContextCompat.getDrawable(mContext, R.drawable.iv_gray_details);
        if (leftDrawable != null) {
            //设置图片的尺寸，奇数位置后减前得到宽度，偶数位置后减前得到高度。
            leftDrawable.setBounds(0, 0, 42, 42);
        }
        //设置图片在TextView中的位置
        binding.tvDetail.setCompoundDrawables(null, null, leftDrawable, null);
        if (mCashierInfo.getList().size() == 1) {
            mCashierInfo.getList().get(0).setIsNeedPayNo("Y");
            binding.tvWxTitle.setTextColor(ContextCompat.getColor(mContext, R.color.text_black));
            binding.tvWxTip.setVisibility(View.GONE);
            binding.tvWxDesc.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray_light));
        } else {
            binding.tvWxTitle.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
            binding.tvWxTip.setVisibility(View.VISIBLE);
            binding.tvWxDesc.setTextColor(ContextCompat.getColor(mContext, R.color.text_gray));
        }
        if (mCashierInfo.getList().size() > 1 && mCashierInfo.getMutilOverdueNum()) {
            binding.rlWxPay.setVisibility(View.GONE);
            binding.viewDividerRepay.setVisibility(View.GONE);
        }
    }

    /**
     * 主动还款
     */
    private void repaymentSubmit() {
        Map<String, Object> map = new HashMap<>();
        List<Repayment> list = mCashierInfo.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map mapBean = JsonUtils.fromJson(JsonUtils.toJson(list.get(i)), Map.class);
                //必须放在map最后一行，是对整个map参数进行签名对
                String signValue = HmacSHA256Utils.buildNeedSignValue(mapBean);
                Repayment repayment = list.get(i);
                repayment.setSign(signValue);
            }
        }
        map.put("list", list);
        netHelper.postService(ApiUrl.URL_Active_Repayment, map);
    }

    /**
     * 跳转数据 结算
     * 调用前先判断是否使用券(单选一期账单且有选择匹配券)，如果需要，则加入CashierInfo中的list还款参数Repayment中
     */
    public static void balanceCashier(Context context, CashierInfo allRePay, InterestFreeBean.RepayCouponsBean chooseCoupon, boolean isTrialed) {
        Intent intent = new Intent(context, RepaymentConfirmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG_BALANCE, allRePay);
        bundle.putSerializable(TAG_COUPON, chooseCoupon);
        bundle.putBoolean(TAG_TRIALED, isTrialed);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void updateRepaymentConfirmUi(QueryCardBean cardBean) {
        binding.ivChoose.setImageResource(R.drawable.cb_single_enable72);//icon_select_cashier_false
        if (cardBean == null || CheckUtil.isEmpty(cardBean.getCardNo())) {
            UiUtil.toast("获取银行卡信息失败，请重试");
            return;
        }
        binding.btRepayment.setEnabled(true);
        chooseBankCardFragment.updateView(cardBean.getCardNo());
        setBankCardNum(cardBean);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignBankCardActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                repaymentSubmit();
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getBooleanExtra("toPwdCheck", false)) {
                    RepaymentActivity.gotoRepaymentActivity(this, mCashierInfo, chooseCoupon);
                }
            }
        }
    }

    /**
     * 更新借据信息
     */
    private void setBankCardNum(QueryCardBean cardBean) {
        mBankCardNo = cardBean != null ? cardBean.getCardNo() : null;
        mCardBean = cardBean;
        isChoosePayWay = true;
        List<Repayment> repaymentList = mCashierInfo.getList();
        for (Repayment repayment : repaymentList) {
            boolean noCard = !hasSelectCard();
            repayment.setAcCardNo(noCard ? "" : mBankCardNo);
            repayment.setAcBch(noCard ? "" : mCardBean.getBankCode());
            repayment.setAcName(noCard ? "" : SpHp.getUser(SpKey.USER_CUSTNAME));
            repayment.setRepayWay(noCard ? "02" : "");
            repayment.setExpRecCde(noCard ? "2322" : "");
        }
    }

    private boolean hasSelectCard() {
        return !CheckUtil.isEmpty(mBankCardNo);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btRepayment) {
            if (!isChoosePayWay) {
                showDialog("请先选择还款银行");
            } else if (hasSelectCard()) {//用银行卡需要走签约流程
                postClickEvent();
                signBankCardHelper.requestNeedSign(mBankCardNo, 3);
            } else {
                postClickEvent();
                RepaymentActivity.gotoRepaymentActivity(this, mCashierInfo, chooseCoupon);
            }
        } else if (view.getId() == R.id.tvDetail) {
            if (!CheckUtil.isEmpty(mCashierInfo)) {
                if (getIntent().getBooleanExtra(TAG_TRIALED, false)) {
                    if (repPopupWindow == null) {
                        repPopupWindow = new RepaymentDetailPopupWindow(this, mCashierInfo);
                    } else {
                        repPopupWindow.updateData(mCashierInfo);
                    }
                    repPopupWindow.showAtLocation(binding.btRepayment);
                }
            }
        } else if (view.getId() == R.id.rlWxPay) {
            if (mCashierInfo.getList().size() == 1) {
                if (!WxUntil.isReady(this)) {
                    UiUtil.toast("未安装微信，请安装后再进行支付");
                    return;
                }
                binding.ivChoose.setImageResource(R.drawable.icon_cb_standard_checked);
                binding.btRepayment.setEnabled(true);
                chooseBankCardFragment.updateView("NULL");
                setBankCardNum(null);
            }
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        if (success == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        showProgress(false);
        switch (url) {
            case ApiUrl.URL_QUERY_NEED_SIGN:
                SignBankCardNeed bean = (SignBankCardNeed) success;
                if (bean.needSign()) {
                    SignBankCardActivity.goSignBankCard(this, mCardBean, bean);
                } else {
                    RepaymentActivity.gotoRepaymentActivity(this, mCashierInfo, chooseCoupon);
                }
                break;
            case ApiUrl.URL_Active_Repayment:
                showPaymentResult((ArrayList<PaymentResult>) JsonUtils.fromJsonArray(success, "list", PaymentResult.class));
                break;
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
    }

    /**
     * 跳转至结果页
     */
    private void showPaymentResult(ArrayList<PaymentResult> list) {
        InThePaymentFragment.toRePayMentResult(mContext, mCashierInfo.getStayAmount(), list);
        finish();
    }

    //点击还款埋点
    private void postClickEvent() {
        Map<String, Object> map = new HashMap<>();
        map.put("repay_way", mCardBean == null || CheckUtil.isEmpty(mCardBean.getBankName()) ? "微信支付" : mCardBean.getBankName());
        UMengUtil.commonClickEvent("ConfirmedRepay_Click", "确认还款", map, getPageCode());
    }

    //免息券使用成功与否还款友盟埋点
    private void postCouponUmEvent(String isSuccess, String failReason) {
        try {
            RepaymentUmHelper.postUmClickEvent((InterestFreeBean.RepayCouponsBean) getIntent().getSerializableExtra(RepaymentConfirmActivity.TAG_COUPON),
                    mCashierInfo.getStayAmount(), mCashierInfo.getList().get(0).getLoanNo(), isSuccess, failReason, getPageCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getPageCode() {
        return "CheckstandPage";
    }

    @Override
    protected void onDestroy() {
        if (repPopupWindow != null) {
            repPopupWindow.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        showDialog("温馨提示", "您确认要放弃本笔还款吗？按时还款有助于维持良好信用哦~", "放弃", "继续还款", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    RepaymentConfirmActivity.super.onBackPressed();
                }
            }
        }).setStandardStyle(2);
    }
}
