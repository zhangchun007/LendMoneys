package com.haiercash.gouhua.bill;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.bean.ResultHead;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.RepaymentAmountInfoActivity;
import com.haiercash.gouhua.adaptor.bill.AllBillAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.base.BaseListFragment;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.beans.InterestFreeBean;
import com.haiercash.gouhua.beans.repayment.AllRePay;
import com.haiercash.gouhua.beans.repayment.BillEventBean;
import com.haiercash.gouhua.beans.repayment.BillEventDetail;
import com.haiercash.gouhua.beans.repayment.CashierInfo;
import com.haiercash.gouhua.databinding.FragmentAllBillsBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.repayment.InThePaymentFragment;
import com.haiercash.gouhua.uihelper.CallPhoneNumberHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Sun
 * Date :    2018/6/15
 * FileName: AllBillsFragment
 * Description: 全部待还 按笔展示借据
 */
public class AllBillsFragment extends BaseListFragment {
    public static final int ID = AllBillsFragment.class.hashCode();

    private FragmentAllBillsBinding getBinding() {
        return (FragmentAllBillsBinding) _binding;
    }

    private boolean needRefreshData = false;
    //已经选择的订单数,方便后期多选
    private int mCount = 0;
    //已经选择的订单，目前全部待还只能单选
    private AllRePay mAllRePay;
    private List<Map<String, String>> applSeqLoanNo;
    private InterestFreeBean.RepayCouponsBean chooseInterestFreeCoupon_new;//选择的券，目前全部待还列表不能用券
    private InterestFreeBean mInterestFreeBean;//券列表组成的对象，目前全部待还列表不能用券

    @Override
    protected ViewBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentAllBillsBinding.inflate(inflater, container, false);
    }

    public static BaseFragment newInstance(Bundle extra) {
        final AllBillsFragment f = new AllBillsFragment();
        if (extra != null) {
            f.setArguments(extra);
        }
        return f;
    }

    @Override
    protected void initEventAndData() {
        setStatusBarTextColor(true);
        setonClickByView(getBinding().btRepayment);
        getBinding().barHeader.setTitle("全部待还");
        mActivity.setTitleVisibility(false);
        super.initEventAndData();
        setRecyclerViewNestedScroll();
        getBinding().tvBillDiscount.setTypeface(FontCustom.getMediumFont(mActivity));
        mAdapter.addChildClickViewIds(R.id.cb_select, R.id.tv_last_time);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            AllRePay item = (AllRePay) adapter.getData().get(position);
            if (view.getId() == R.id.cb_select) {
                updateAmount(position, item);
            } else if (view.getId() == R.id.tv_last_time) {
                Intent intent = new Intent(getActivity(), BillDetailsActivity.class);
                intent.putExtra("applSeq", item.getApplSeq());
                startActivity(intent);
            }
        });
        showProgress(true);
        startRefresh(true, false);
    }

    @Override
    public BaseAdapter getAdapter() {
        return new AllBillAdapter();
    }

    private void updateData(List<AllRePay> listData) {
        try {
            getBinding().llEmpty.setVisibility(CheckUtil.isEmpty(listData) ? View.VISIBLE : View.GONE);
            if (mRefreshHelper != null) {
                mRefreshHelper.updateData(listData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新累计金额
     */
    private void updateAmount(int position, AllRePay allRePay) {
        if ("bad".equals(allRePay.getLoanTyp())) {
            allRePay.setSelected(false);
            allRePay.setUseCoupon(false);
            mAdapter.notifyItemChanged(position);
            CallPhoneNumberHelper.callServiceNumber(mActivity, "逾期90天及以上不支持线上还款，请联系客服帮助还款",
                    "联系客服", "放弃");
            return;
        }
        boolean check = !allRePay.isSelected();
        allRePay.setSelected(check);
        if (check) {
            mCount++;
            subTractOtherBill(allRePay);
            mAllRePay = allRePay;
        } else {
            allRePay.setUseCoupon(false);
            allRePay.setDiscValue("");
            allRePay.setDiscRepayAmt("");
            mAllRePay = null;
            mCount--;
        }
//        //取消或者选中后都要初始化，然后重新获取匹配的券
        chooseInterestFreeCoupon_new = null;
        setButtonEnable();
        //全部待还列表目前不能用券
        //构成单选时才能用券
        if (mCount == 1) {
            BillHelper.getAllRepayCouponsForOrder(null, this, netHelper, mAllRePay, "FS", "Y");
        }
    }

    /**
     * 按钮是否可以点击
     */
    private void setButtonEnable() {
        getBinding().btRepayment.setEnabled(mCount != 0);
        //单选才能用免息券
        if (mCount == 1 && mAllRePay != null && chooseInterestFreeCoupon_new != null && "Y".equals(chooseInterestFreeCoupon_new.getCanUseState())) {
            getBinding().tvRepayment.setText(UiUtil.getStr("￥", CheckUtil.formattedAmount1(chooseInterestFreeCoupon_new.getDiscRepayAmt())));
            getBinding().tvBillDiscount.setText(UiUtil.getStr("已优惠", CheckUtil.formattedAmount(chooseInterestFreeCoupon_new.getDiscValue()), "元"));
            getBinding().tvBillDiscount.setVisibility(View.VISIBLE);
        } else {
            if (mCount == 1 && mAllRePay != null) {
                getBinding().tvRepayment.setText(UiUtil.getStr(CheckUtil.formattedAmount1(mAllRePay.getStayAmount())));
            }
            getBinding().tvBillDiscount.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 当单选开启时，减掉已经选择的订单
     */
    private void subTractOtherBill(AllRePay allRePay) {
        if (mAllRePay != null && !allRePay.getLoanNo().equals(mAllRePay.getLoanNo())) {
            mAllRePay.setSelected(false);
            mAllRePay.setUseCoupon(false);
            mAdapter.notifyDataSetChanged();
            mCount--;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needRefreshData || InThePaymentFragment.TAG_REPAYMENT_RESULT) {
            showProgress(true);
            loadSourceData(1, 0);
            //确保回到首页的时候将TAG_REPAYMENT_RESULT变成false，保证首页的数据刷新
            if (mActivity.getTitleBarView() == null) {
                InThePaymentFragment.TAG_REPAYMENT_RESULT = false;
            }
            needRefreshData = false;
        }
    }

    @Override
    public void loadSourceData(int page, int pageSize) {
        if (page != 1) {
            updateData(null);
            showProgress(false);
            return;
        }
        String certNo = SpHp.getUser(SpKey.USER_CERTNO);
        String custNo = SpHp.getUser(SpKey.USER_CUSTNO);
        if (TextUtils.isEmpty(certNo) && TextUtils.isEmpty(custNo)) {
            updateData(null);
            showProgress(false);
            return;
        }
        Map<String, String> map = new HashMap<>(1);
        map.put("idNo", EncryptUtil.simpleEncrypt(certNo));
        map.put("custNo", EncryptUtil.simpleEncrypt(custNo));
        netHelper.getService(ApiUrl.URL_GETALLBILLS, map, HashMap.class, true);
    }


    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        if (success == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        showProgress(false);
        if (url.equals(ApiUrl.URL_GETALLBILLS)) {
            List<AllRePay> list = JsonUtils.fromJsonArray(success, "bills", AllRePay.class);
            updateView(list);
        } else if (url.equals(ApiUrl.URL_CALCULATE_REPAYMENT_AMOUNT)) {
            CashierInfo mCashierInfo = (CashierInfo) success;
            //如果非逾期并且非联合放款并且能够主动还款跳转还款确认页
            Intent intent = new Intent(mActivity, RepaymentAmountInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("cashInfo", mCashierInfo);
            bundle.putSerializable("repayInfo", (Serializable) applSeqLoanNo);
            bundle.putString("repaymentType", "all");
            bundle.putSerializable("chooseList", mInterestFreeBean);
            bundle.putSerializable("interestFreeCoupon", chooseInterestFreeCoupon_new);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (ApiUrl.URL_ALL_REPAY_COUPONS_FOR_ORDER.equals(url)) {
            HashMap map = JsonUtils.fromJson(success, HashMap.class);
            String hasDiscountMsg = map.get("hasDiscountMsg") + "";
            if (!CheckUtil.isEmpty(hasDiscountMsg)) {
                UiUtil.toast(hasDiscountMsg);
            }
            List<InterestFreeBean.RepayCouponsBean> repayCouponsBeanList = JsonUtils.fromJsonArray(success, "repayCoupons", InterestFreeBean.RepayCouponsBean.class);
            if (mInterestFreeBean != null && mInterestFreeBean.getCoupons() != null) {
                mInterestFreeBean.getCoupons().clear();
                mInterestFreeBean.setCoupons(null);
            }
            mInterestFreeBean = null;
            mInterestFreeBean = new InterestFreeBean();
            mInterestFreeBean.setCoupons(repayCouponsBeanList);
            for (InterestFreeBean.RepayCouponsBean repayCouponsBean : repayCouponsBeanList) {
                if (repayCouponsBean != null && (repayCouponsBean.hasBind() || repayCouponsBean.isBest())) {
                    chooseInterestFreeCoupon_new = repayCouponsBean;
                    break;
                }
            }
            if (chooseInterestFreeCoupon_new != null) {
                int index = mAdapter.getData().indexOf(mAllRePay);
                mAllRePay.setUseCoupon(true);
                mAllRePay.setDiscValue(chooseInterestFreeCoupon_new.getDiscValue());
                mAllRePay.setDiscRepayAmt(chooseInterestFreeCoupon_new.getDiscRepayAmt());
                mAdapter.notifyItemChanged(index);
                setButtonEnable();
            }
        }
    }

    @Override
    public void onClick(View v) {
        List<AllRePay> allRePays = new ArrayList<>();
        @SuppressWarnings("unchecked") List<AllRePay> list = mAdapter.getData();
        for (AllRePay allRePay : list) {
            if (allRePay.isSelected()) {
                allRePays.add(allRePay);
            }
        }
        getRepayCalculate(allRePays);
    }

    private void updateView(List<AllRePay> list) {
        //重置计数器
        if (getBinding().llRepayment == null) {
            return;
        }
        mCount = 0;
        mAllRePay = null;
        getBinding().tvBillRemark.setVisibility(list != null && list.size() > 0 ? View.VISIBLE : View.GONE);
        getBinding().llRepayment.setVisibility(list != null && list.size() > 0 ? View.VISIBLE : View.GONE);
        getBinding().vShadow.setVisibility(list != null && list.size() > 0 ? View.VISIBLE : View.GONE);

        if (list != null
                && getArguments() != null
                && getArguments().containsKey("applSeq")
                && !CheckUtil.isEmpty(getArguments().get("applSeq"))) {
            for (int i = 0; i < list.size(); i++) {
                {
                    if (getArguments().get("applSeq").equals(list.get(i).getApplSeq())) {
                        updateAmount(i, list.get(i));
                        break;
                    }
                }

            }
        }

        updateData(list);
        setButtonEnable();
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        if (url.equals(ApiUrl.URL_CALCULATE_REPAYMENT_AMOUNT)) {
            ResultHead resultHead = error.getHead();
            if ("A182435".equals(resultHead.getRetFlag()) && mAllRePay != null) {
                String message = resultHead.getRetMsg();
                message = message.replace("{repayDate}", mAllRePay.getRepayDate());
                showDialog(message);
            }
        } else {
            updateData(null);
            mRefreshHelper.errorData();
        }
    }

    /**
     * 还款试算
     */
    private void getRepayCalculate(List<AllRePay> allRePays) {
        showProgress(true);
        Map<String, Object> map = new HashMap<>(4);
        //map.put("idNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CERTNO)));
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
        //map.put("userId", RSAUtils.encryptByRSA(SpHp.getLogin(SpKey.LOGIN_USERID)));
        map.put("type", "all");
        applSeqLoanNo = new ArrayList<>();
        BillEventBean bean = new BillEventBean();
        List<BillEventDetail> details = new ArrayList<>();
        String isOverdue = "N";
        for (AllRePay rePay : allRePays) {
            Map<String, String> data = new HashMap<>(2);
            data.put("applSeq", rePay.getApplSeq());
            data.put("loanNo", rePay.getLoanNo());
            data.put("loanTyp", rePay.getLoanTyp());
            applSeqLoanNo.add(data);

            if ("Y".equals(rePay.getIsOnlyOd())) {
                isOverdue = "Y";
            }
            BillEventDetail detail = new BillEventDetail();
            detail.setLoan_id(rePay.getLoanNo());
            double sum;
            double periods;
            try {
                sum = CheckUtil.isEmpty(rePay.getApprvAmt()) ?
                        0 : Double.parseDouble(rePay.getApprvAmt());

                periods = CheckUtil.isEmpty(rePay.getApprvTnr()) ?
                        0 : Double.parseDouble(rePay.getApprvTnr());

            } catch (Exception e) {
                sum = 0;
                periods = 0;
            }

            detail.setLoannotrepay_sum(sum);
            detail.setLoannotrepay_periods(periods);
            details.add(detail);
        }
        bean.setNotrepaydetail(details);
        postEvent(isOverdue, JsonUtils.toJson(bean));
        map.put("isRsa", "Y");
        map.put("needCalculatedLoans", applSeqLoanNo);
        netHelper.postService(ApiUrl.URL_CALCULATE_REPAYMENT_AMOUNT, map, CashierInfo.class, true);
    }

    @Override
    protected String getPageCode() {
        return "Gh_NotRepayListPage";
    }

    /**
     * 点击去还款埋点
     * 待还总额	notrepay_sum	所有待还款总额加起来
     * 选择笔数	select_count	未选是0，单选是1，多选是计算n
     * 是否逾期	overdue_flag	true、false
     * 待还明细 notrepaydetail	还款借据 loan_id	"动态传，这三个字段KV拼接JSON串，单选1条，多选多条数据   { ""notrepaydetail"": [
     * 按钮名称	button_name	去还款
     */
    private void postEvent(String isOverdue, String detail) {
        Map<String, Object> map = new HashMap<>();
        map.put("notrepay_sum", "");
        map.put("select_count", 1);
        map.put("overdue_flag", isOverdue);
        map.put("notrepaydetail", detail);
        map.put("button_name", "去还款");
        UMengUtil.onEventObject("RepayLoanList", map, getPageCode());
    }
}
