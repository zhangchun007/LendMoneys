package com.haiercash.gouhua.bill;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.RepaymentAmountInfoActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.base.BaseListFragment;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.beans.InterestFreeBean;
import com.haiercash.gouhua.beans.repayment.BillEventBean;
import com.haiercash.gouhua.beans.repayment.BillEventDetail;
import com.haiercash.gouhua.beans.repayment.CashierInfo;
import com.haiercash.gouhua.beans.repayment.LoanBean;
import com.haiercash.gouhua.beans.repayment.PartialBean;
import com.haiercash.gouhua.beans.repayment.Repayment;
import com.haiercash.gouhua.databinding.FragmentPeriodBillsBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.repayment.InThePaymentFragment;
import com.haiercash.gouhua.repayment.RepaymentConfirmActivity;
import com.haiercash.gouhua.uihelper.CallPhoneNumberHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Sun
 * Date :    2018/6/14
 * FileName: PeriodBillsFragment
 * Description: 7日待还和当月待还
 */
public class PeriodBillsFragment extends BaseListFragment {
    private FragmentPeriodBillsBinding getBinding() {
        return (FragmentPeriodBillsBinding) _binding;
    }

    public static final int ID = PeriodBillsFragment.class.hashCode();

    private PartialBean beanData;
    //逾期90天还款
    private List<LoanBean> m3List = new ArrayList<>();
    //逾期
    private List<LoanBean> overdueList = new ArrayList<>();
    //当日还款
    private List<LoanBean> todayList = new ArrayList<>();
    //正常还款
    private List<LoanBean> normalList = new ArrayList<>();
    //勾选还款
    private List<LoanBean> selectList = new ArrayList<>();
    private BigDecimal payMoney = new BigDecimal("0.00");
    private boolean multiSelectSwitch = false;
    private boolean needRefreshData = false;
    private List<Map<String, String>> applSeqLoanNo;
    private InterestFreeBean.RepayCouponsBean chooseInterestFreeCoupon;//传入进来的指定券
    private InterestFreeBean.RepayCouponsBean chooseInterestFreeCoupon_new;//选择的券
    private InterestFreeBean mInterestFreeBean;//券列表组成的对象

    public static BaseFragment newInstance(Bundle extra) {
        final PeriodBillsFragment f = new PeriodBillsFragment();
        if (extra != null) {
            f.setArguments(extra);
        }
        return f;
    }

    @Override
    protected FragmentPeriodBillsBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentPeriodBillsBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle("7日内待还");
        super.initEventAndData();
        setRecyclerViewNestedScroll();
        if (getArguments() != null) {
            chooseInterestFreeCoupon = (InterestFreeBean.RepayCouponsBean) getArguments().getSerializable("interestFreeCoupon");
        }
        getBinding().tvTotalAmountTitle.setTypeface(FontCustom.getMediumFont(mActivity));
        setonClickByViewId(R.id.btRepayment);
        mAdapter.addChildClickViewIds(R.id.cb_select, R.id.tv_last_time);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            LoanBean bean = (LoanBean) mAdapter.getData().get(position);
            if (view.getId() == R.id.cb_select) {
                updateAmount(bean, position);
            } else if (view.getId() == R.id.tv_last_time) {
                Intent intent = new Intent(getActivity(), BillDetailsActivity.class);
                intent.putExtra("applSeq", bean.getApplSeq());
                startActivity(intent);
            }
        });
        showProgress(true);
        startRefresh(true, false);
    }

    @Override
    public BaseAdapter getAdapter() {
        return new PeriodBillsAdapter();
    }

    private void updateData(List<LoanBean> listData) {
        try {
            getBinding().llEmpty.setVisibility(CheckUtil.isEmpty(listData) ? View.VISIBLE : View.GONE);
            if (mRefreshHelper != null) {
                mRefreshHelper.updateData(listData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadSourceData(int page, int pageSize) {
        String idNo = SpHp.getUser(SpKey.USER_CERTNO);
        String custNo = SpHp.getUser(SpKey.USER_CUSTNO);
        if (CheckUtil.isEmpty(idNo) && TextUtils.isEmpty(custNo)) {
            showProgress(false);
            updateData(null);
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("idNo", RSAUtils.encryptByRSA(idNo));
        map.put("custNo", RSAUtils.encryptByRSA(custNo));
        map.put("userId", RSAUtils.encryptByRSA(AppApplication.userid));
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.URL_PERIODBILLS_OD, map, PartialBean.class);
    }


    @Override
    public void onClick(View view) {
        if (m3List.size() > 0) {
            CallPhoneNumberHelper.callServiceNumber(mActivity, "逾期90天及以上不支持线上还款，请联系客服帮助还款", "联系客服", "放弃");
            return;
        }
        // 逾期  或者  选择的是当日还款的
        if (overdueList.size() > 0 || "0".equals(selectList.get(0).getRemainDays())) {
            repaymentTrial(getTrialType(), getIsRepayDay());
        } else {
            getRepayMentList(String.valueOf(payMoney));
        }
    }

    private String getTrialType() {
        //逾期传"overdue"
        return overdueList.size() > 0 ? "overdue" : "period";
    }

    private String getIsRepayDay() {
        String isRepayDay;
        // 逾期  或者  选择的是当日还款的
        if (overdueList.size() > 0 || "0".equals(selectList.get(0).getRemainDays())) {
            isRepayDay = overdueList.size() > 0 ? "N" : "Y";
        } else {
            //selectList只有一条记录；即不允许多选，只能单期还款的情况：走还款试算NF模式
            if (selectList != null && selectList.size() == 1) {
                isRepayDay = "0".equals(selectList.get(0).getRemainDays()) ? "Y" : "N";
            } else {
                isRepayDay = "N";
            }
        }
        return isRepayDay;
    }

    /**
     * 进入收银台 进行还款
     */
    private void getRepayMentList(String payMoney) {
        if (multiSelectSwitch) { //允许多选  走现有流程----目前不允许多选
            rePaymentNormal(payMoney);
        } else {
            repaymentTrial(getTrialType(), getIsRepayDay());
        }
    }

    /**
     * 走现有流程
     */
    private void rePaymentNormal(String payMoney) {
        List<Repayment> list = new ArrayList<>();
        CashierInfo cashierInfo = new CashierInfo();
        cashierInfo.setStayAmount(payMoney);
        String cName = SpHp.getUser(SpKey.USER_CUSTNAME);
        String cNo = SpHp.getUser(SpKey.USER_CUSTNO);
        for (LoanBean bean : selectList) {
            if (bean.isChecked()) {
                Repayment repayment = new Repayment();
                repayment.setApplSeq(bean.getApplSeq());
                repayment.setLoanNo(bean.getLoanNo());
                repayment.setSetlTyp("01");
                repayment.setUseCoup("N");
                repayment.setPsPerdNo(bean.getPsPerdNo());
                repayment.setSetlValDt(bean.getPsDueDt());
                repayment.setAcName(cName);
                repayment.setCustNo(cNo);
                repayment.setRepayAmt(bean.getAmount());
                if (!"normal".equals(bean.getLoanTyp())) {
                    repayment.setSetlMode("NM");
                } else {
                    repayment.setSetlMode("NF");
                }
                list.add(repayment);
            }
        }
        //单选且有匹配券使用，加入参:券相关
        if (list.size() == 1 && chooseInterestFreeCoupon_new != null) {
            list.get(0).setUseCoup("Y");
            list.get(0).setCoupNo(chooseInterestFreeCoupon_new.getCouponNo());
            list.get(0).setCoupUseAmt(chooseInterestFreeCoupon_new.getDiscValue());
        }
        cashierInfo.setList(list);
        RepaymentConfirmActivity.balanceCashier(mActivity, cashierInfo, chooseInterestFreeCoupon_new, false);
    }

    /**
     * 还款试算
     */
    private void repaymentTrial(String trialType, String isRepayDay) {
        applSeqLoanNo = new ArrayList<>();
        BillEventBean bean = new BillEventBean();
        List<BillEventDetail> details = new ArrayList<>();
        for (LoanBean loanBean : selectList) {
            BillEventDetail detail = new BillEventDetail();
            Map<String, String> map = new HashMap<>();
            map.put("applSeq", loanBean.getApplSeq());
            map.put("loanNo", loanBean.getLoanNo());
            map.put("psPerdNo", loanBean.getPsPerdNo());
            map.put("setlValDt", loanBean.getPsDueDt());
            map.put("loanTyp", loanBean.getLoanTyp());
            //map.put("loanTypeCode", loanBean.getLoanTypeCode());
            //map.put("apprvAmt", loanBean.getApprvAmt());
            applSeqLoanNo.add(map);

            double sum;
            double periods;
            try {
                sum = CheckUtil.isEmpty(loanBean.getApprvAmt()) ?
                        0 : Double.parseDouble(loanBean.getApprvAmt());

                periods = CheckUtil.isEmpty(loanBean.getApprvTnr()) ?
                        0 : Double.parseDouble(loanBean.getApprvTnr());

            } catch (Exception e) {
                sum = 0;
                periods = 0;
            }
            detail.setLoan_id(loanBean.getLoanNo());
            detail.setLoannotrepay_sum(sum);
            detail.setLoannotrepay_periods(periods);
            details.add(detail);
        }
        bean.setNotrepaydetail(details);
        postEvent(JsonUtils.toJson(bean));
        Map<String, Object> map = new HashMap<>();
        map.put("type", trialType);
        map.put("isRepayDay", isRepayDay);
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
        map.put("needCalculatedLoans", applSeqLoanNo);
        map.put("isRsa", "Y");
        showProgress(true);
        netHelper.postService(ApiUrl.URL_CALCULATE_REPAYMENT_AMOUNT, map, CashierInfo.class, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needRefreshData && InThePaymentFragment.TAG_REPAYMENT_RESULT) {
            showProgress(true);
            loadSourceData(1, 1);
            needRefreshData = false;
        } else if (!needRefreshData) {
            needRefreshData = true;
        }
    }

    /**
     * 更新数据
     */
    private void updateAmount(LoanBean loanBean, int position) {
        String message = null;
        //第一种情况：如果存在逾期则不可选择非逾期
        if (!"normal".equals(loanBean.getLoanTyp())) {
            mAdapter.notifyItemChanged(position);
            showDialog("请您先归还逾期部分的欠款");
            return;
        }
        //第二种情况：当前记录是否允许还款：eg 系统日结；其他特殊情况
        if ("02".equals(loanBean.getCanActiveRepayStatus())) {
            message = beanData.getDailySettleNotAllowRepayErrorMsg();
        } else if ("03".equals(loanBean.getCanActiveRepayStatus())) {
            message = "服务器开小差了，请稍后再试";
        }
        //第三种情况: 是联合放款 且非当天还款日的 不允许还当期
        if (!"0".equals(loanBean.getRemainDays()) && "Y".equals(loanBean.getLoanMode())) {
            message = "抱歉，该笔暂不支持当期还款，您可通过全部待还入口结清全部借款。";
        }
        if (!TextUtils.isEmpty(message)) {
            mAdapter.notifyItemChanged(position);
            showDialog(message);
            return;
        }
        //第四种情况：可多选 且 还款日当天的账单和非当天的不能同时选择
        if (multiSelectSwitch && todayList.size() > 0) {
            boolean isContains = todayList.contains(loanBean);
            if (selectList.size() > 0) {
                LoanBean bean = selectList.get(0);
                //说明selectList全部是当期还款的
                if ("0".equals(bean.getRemainDays())) {
                    if (!isContains) {//说明当前选的LoanBean不是当期的
                        message = "还款日当天与其他日期的不支持同时结清，请重新选择";
                    }
                } else {
                    if (isContains) {
                        message = "还款日当天与其他日期的不支持同时结清，请重新选择";
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(message)) {
            mAdapter.notifyItemChanged(position);
            showDialog(message);
            return;
        }
        boolean check = !loanBean.isChecked();
        loanBean.setChecked(check);
        if (check) {
            if (multiSelectSwitch) {
                //允许多选时，多选时不能使用券
                for (LoanBean bean : selectList) {
                    bean.setUseCoupon(false);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                //不允许多选时，需要相减
                subTractOtherBill();
            }
            selectList.add(loanBean);
        } else {
            loanBean.setUseCoupon(false);
            loanBean.setDiscValue("");
            loanBean.setDiscRepayAmt("");
            mAdapter.notifyItemChanged(position);
            selectList.remove(loanBean);
        }
        calculationMoney(loanBean.getAmount(), check);
        //取消或者选中后都要初始化，然后重新获取匹配的券
        chooseInterestFreeCoupon_new = null;
        if (mInterestFreeBean != null && mInterestFreeBean.getCoupons() != null) {
            mInterestFreeBean.getCoupons().clear();
            mInterestFreeBean.setCoupons(null);
        }
        mInterestFreeBean = null;

        setButtonEnable();
        requestAllRepayCouponsForOrder();
    }

    private void requestAllRepayCouponsForOrder() {
        //构成单选时才能用券，逾期不能用券
        if (selectList.size() == 1 && !m3List.contains(selectList.get(0)) && !overdueList.contains(selectList.get(0))) {
            //单选时设置显示券
            selectList.get(0).setUseCoupon(true);
            BillHelper.getAllRepayCouponsForOrder(null, this, netHelper, selectList.get(0), "NF", "N");
        } else if (selectList.size() >= 1) {
            BillHelper.getAllCouponsForOverdueOrder(netHelper, selectList);

        }
    }

    /**
     * 设置按钮状态
     */
    private void setButtonEnable() {
        String money = payMoney.toPlainString();
        getBinding().btRepayment.setEnabled(!CheckUtil.isZero(money));
        getBinding().btRepayment.setText(UiUtil.getStr("立即还款(", selectList.size(), ")"));
        //单选才能用免息券
        if (selectList.size() == 1 && chooseInterestFreeCoupon_new != null &&
                !CheckUtil.isEmpty(chooseInterestFreeCoupon_new.getDiscRepayAmt()) &&
                !CheckUtil.isEmpty(chooseInterestFreeCoupon_new.getDiscValue())) {
            getBinding().tvTotalAmount.setText(UiUtil.getStr("￥", CheckUtil.formattedAmount1(chooseInterestFreeCoupon_new.getDiscRepayAmt())));
            getBinding().tvHasDiscount.setText(SpannableStringUtils.getBuilder(mActivity, "已优惠")
                    .append(SpannableStringUtils.getBuilder(mActivity,
                            UiUtil.getStr(CheckUtil.formattedAmount(chooseInterestFreeCoupon_new.getCoupUseAmt()), "元"))
                            .setBold().setForegroundColor(0xFFFF5151).create()).create());
            getBinding().tvHasDiscount.setVisibility(View.VISIBLE);
        } else {
            getBinding().tvTotalAmount.setText(UiUtil.getStr("￥", CheckUtil.formattedAmount1(money)));
            getBinding().tvHasDiscount.setVisibility(View.GONE);
        }

        //数据最少两条并且没有逾期，说明为还款日选择了两条，要toast提醒
        if (selectList.size() > 1 && overdueList.size() == 0 && "0".equals(selectList.get(0).getRemainDays())) {
            UiUtil.toast("多笔借款合并还，不能使用免息券哦～");
        }
    }

    /**
     * 当选控制只控制非逾期，非当日的。<br/>
     * 当单选开启时，减掉已经选择的订单
     */
    private void subTractOtherBill() {
        for (LoanBean bean : selectList) {
            int index = beanData.getOrders().indexOf(bean);
            beanData.getOrders().get(index).setChecked(false);
            beanData.getOrders().get(index).setUseCoupon(false);
            calculationMoney(bean.getAmount(), false);
        }
        mAdapter.notifyDataSetChanged();
        selectList.clear();
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (success == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        showProgress(false);
        if (ApiUrl.URL_PERIODBILLS_OD.equals(url)) {
            resetInitVariable();
            beanData = (PartialBean) success;
            //multiSelectSwitch = "ON".equals(beanData.getMultiSelectSwitch());
            if (beanData.getOrders() != null) {
                processListData();
            }
            getBinding().rlBottom.setVisibility((beanData.getOrders() != null && beanData.getOrders().size() > 0) ? View.VISIBLE : View.GONE);
            getBinding().vShadow.setVisibility((beanData.getOrders() != null && beanData.getOrders().size() > 0) ? View.VISIBLE : View.GONE);
            getBinding().tvBillRemark.setVisibility((beanData.getOrders() != null && beanData.getOrders().size() > 0) ? View.VISIBLE : View.GONE);
            getBinding().tvOverdueRem.setVisibility((m3List.size() > 0 || overdueList.size() > 0) ? View.VISIBLE : View.GONE);
            List<LoanBean> listData = new ArrayList<>(m3List);
            listData.addAll(overdueList);
            listData.addAll(todayList);
            listData.addAll(normalList);
            updateData(listData);
            setButtonEnable();
            //正常待还账单，且有传进来的券，进行匹配，如果有匹配则自动勾选使用券，如果没有，则不显示券
            if (selectList.size() == 0 && chooseInterestFreeCoupon != null) {
                for (LoanBean bean : listData) {
                    if (bean == null) {
                        continue;
                    }
                    //寻找指定券（券包列表传过来的）已绑定的借据(借据号和期数都要匹配)自动勾选
                    if (!CheckUtil.isEmpty(chooseInterestFreeCoupon.getBindApplSeq()) &&
                            chooseInterestFreeCoupon.getBindApplSeq().equals(bean.getApplSeq()) &&
                            (chooseInterestFreeCoupon.getBindPerdNo() == null ||
                                    chooseInterestFreeCoupon.getBindPerdNo().equals(bean.getPsPerdNo()))) {
                        updateAmount(bean, listData.indexOf(bean));
                        break;
                    }
                }
            } else {
                requestAllRepayCouponsForOrder();
            }
        } else if (ApiUrl.URL_ALL_REPAY_COUPONS_FOR_ORDER.equals(url)) {
            HashMap map = JsonUtils.fromJson(success, HashMap.class);
            String hasDiscountMsg = map.get("hasDiscountMsg") + "";
            if (!CheckUtil.isEmpty(hasDiscountMsg)) {
                UiUtil.toast(hasDiscountMsg);
            }
            List<InterestFreeBean.RepayCouponsBean> repayCouponsBeanList = JsonUtils.fromJsonArray(success, "repayCoupons", InterestFreeBean.RepayCouponsBean.class);
            if (!CheckUtil.isEmpty(repayCouponsBeanList)) {
                mInterestFreeBean = new InterestFreeBean();
                mInterestFreeBean.setCoupons(repayCouponsBeanList);
                InterestFreeBean.RepayCouponsBean bestCoupon = null;
                for (InterestFreeBean.RepayCouponsBean repayCouponsBean : repayCouponsBeanList) {
                    //优先判断有没有已绑定券，有则直接用该已绑定券，无则用最优券
                    if (repayCouponsBean != null && repayCouponsBean.hasBind()) {
                        chooseInterestFreeCoupon_new = repayCouponsBean;
                        break;
                    }
                    if (bestCoupon == null && repayCouponsBean != null && repayCouponsBean.isBest()) {
                        bestCoupon = repayCouponsBean;
                    }
                }
                if (chooseInterestFreeCoupon_new == null) {
                    chooseInterestFreeCoupon_new = bestCoupon;
                }
                if (chooseInterestFreeCoupon_new != null) {
                    selectList.get(0).setUseCoupon(true);
                    selectList.get(0).setDiscValue(chooseInterestFreeCoupon_new.getDiscValue());
                    selectList.get(0).setDiscRepayAmt(chooseInterestFreeCoupon_new.getDiscRepayAmt());
                    mAdapter.notifyDataSetChanged();
                    setButtonEnable();
                }
            }
        } else if (ApiUrl.URL_COUPONS_FOR_OVERDUE_ORDER.equals(url)) {
            HashMap map = JsonUtils.fromJson(success, HashMap.class);
            String hasDiscountMsg = map.get("hasDiscountMsg") + "";
            if (!CheckUtil.isEmpty(hasDiscountMsg)) {
                UiUtil.toast(hasDiscountMsg);
            }
            List<InterestFreeBean.RepayCouponsBean> repayCouponsBeanList = JsonUtils.fromJsonArray(success, "repayCoupons", InterestFreeBean.RepayCouponsBean.class);
            if (!CheckUtil.isEmpty(repayCouponsBeanList)) {
                mInterestFreeBean = new InterestFreeBean();
                mInterestFreeBean.setCoupons(repayCouponsBeanList);
            }
        } else if (ApiUrl.URL_CALCULATE_REPAYMENT_AMOUNT.equals(url)) {
            CashierInfo mCashierInfo = (CashierInfo) success;
            Intent intent = new Intent(mActivity, RepaymentAmountInfoActivity.class);
            Bundle bundle = new Bundle();
            if (!CheckUtil.isEmpty(overdueList)) {
                mCashierInfo.setStayAmount(mCashierInfo.getOdAmount());
                mCashierInfo.setMutilOverdueNum(true);
            }
            if (selectList.size() == 1 && CheckUtil.isEmpty(overdueList) && mInterestFreeBean != null && !CheckUtil.isEmpty(mInterestFreeBean.getCoupons())) {
                //如果选择的是单期且为非逾期数据，则允许查询是否有优惠券
                bundle.putSerializable("chooseList", mInterestFreeBean);
                if (chooseInterestFreeCoupon_new != null) {
                    bundle.putSerializable("interestFreeCoupon", chooseInterestFreeCoupon_new);
                }
            } else if (selectList.size() >= 1 && !CheckUtil.isEmpty(overdueList) && mInterestFreeBean != null && !CheckUtil.isEmpty(mInterestFreeBean.getCoupons())) {
                bundle.putSerializable("chooseList", mInterestFreeBean);
                bundle.putBoolean("isOverdue", true);
            }
            bundle.putString("repaymentType", getTrialType());
            bundle.putString("isRepayDay", getIsRepayDay());
            bundle.putSerializable("repayInfo", (Serializable) applSeqLoanNo);
            bundle.putSerializable("cashInfo", mCashierInfo);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        if (ApiUrl.URL_PERIODBILLS_OD.equals(url)) {
            resetInitVariable();
            getBinding().tvBillRemark.setVisibility(View.GONE);
            updateData(null);
            mRefreshHelper.errorData();
        }
    }

    /**
     * 重置相关变量
     */
    private void resetInitVariable() {
        payMoney = new BigDecimal("0.00");
        m3List.clear();
        overdueList.clear();
        todayList.clear();
        normalList.clear();
        selectList.clear();
    }

    private void processListData() {
        for (LoanBean item : beanData.getOrders()) {
            String loanTyp = item.getLoanTyp();
            if (!"normal".equals(loanTyp)) {
                if ("bad".equals(loanTyp)) {
                    m3List.add(item);
                } else {
                    overdueList.add(item);
                }
            } else if ("0".equals(item.getRemainDays())) {
                todayList.add(item);
            } else {
                normalList.add(item);
            }
        }
        for (LoanBean item : m3List) {
            if ("01".equals(item.getRepayStatus())) {//还款处理中
                continue;
            }
            item.setEnable(true);
            item.setChecked(true);
            selectList.add(item);
            calculationMoney(item.getAmount(), true);
        }
        for (LoanBean item : overdueList) {
            if ("01".equals(item.getRepayStatus())) {//还款处理中
                continue;
            }
            item.setEnable(true);
            item.setChecked(true);
            selectList.add(item);
            calculationMoney(item.getAmount(), true);
        }
        for (LoanBean item : todayList) {
            if ("01".equals(item.getRepayStatus())) {//还款处理中
                continue;
            }
            //无逾期时  还款日当天的期数可以自由选择还款,有逾期时不可选
            item.setEnable(m3List.size() == 0 && overdueList.size() == 0);
            if (m3List.size() <= 0 && overdueList.size() <= 0) {//&& "03".equals(item.getRepayStatus())
                item.setChecked(true);
                selectList.add(item);
                calculationMoney(item.getAmount(), true);
            }
        }
        for (LoanBean item : normalList) {
            item.setEnable(m3List.size() == 0 && overdueList.size() == 0);
        }
    }

    /**
     * 重置当前数据源
     * <p>
     * private void checkUpdateData() {
     * for (LoanBean item : beanData.getOrders()) {
     * String loanTyp = item.getLoanTyp();
     * if (!"normal".equals(loanTyp)) {
     * item.setChecked(true);
     * overdue = true;
     * if ("bad".equals(loanTyp)) {
     * hasM3 = true;
     * }
     * }
     * //设置如果存在逾期的记录，则正常的记录数据是不可选中的
     * if ("normal".equals(loanTyp) && (hasM3 || overdue)) {
     * item.setEnable(false);
     * } else {
     * item.setEnable(true);
     * }
     * //联合放款  不可选中，不可还款  点击选择时候弹窗提示：抱歉，该笔暂不支持当期还款，您可通过全部待还入口结清全部借款。我知道了
     * if ("Y".equals(item.getLoanMode()) && "normal".equals(loanTyp)) {
     * item.setChecked(false);
     * }
     * //对于选择的订单进行金额汇总
     * if (item.isChecked()) {
     * calculationMoney(item.getAmount(), true);
     * }
     * }
     * }
     */
    private void calculationMoney(String amount, boolean isAdd) {
        if (isAdd) {
            payMoney = payMoney.add(new BigDecimal(amount));
        } else {
            payMoney = payMoney.subtract(new BigDecimal(amount));
        }
    }

    @Override
    protected String getPageCode() {
        return "Gh_NotRepayListPage7Days";
    }

    //点击去还款埋点

    /**
     * 待还总额	notrepay_sum	所有待还款总额加起来
     * 选择笔数	select_count	未选是0，单选是1，多选是计算n
     * 是否逾期	overdue_flag	true、false
     * 待还明细 notrepaydetail	还款借据 loan_id	"动态传，这三个字段KV拼接JSON串，单选1条，多选多条数据   { ""notrepaydetail"": [
     * 按钮名称	button_name	去还款
     */
    private void postEvent(String detail) {
        Map<String, Object> map = new HashMap<>();
        double notrepay_sum;
        try {
            notrepay_sum = CheckUtil.isEmpty(beanData.getStayAmount()) ?
                    0 : Double.parseDouble(beanData.getStayAmount());

        } catch (Exception e) {
            notrepay_sum = 0;
        }
        map.put("notrepay_sum", notrepay_sum);
        map.put("select_count", selectList.size());
        map.put("overdue_flag", overdueList.size() > 0 ? "true" : "false");
        map.put("notrepaydetail", detail);
        map.put("button_name", "去还款");
        UMengUtil.onEventObject("RepayLoanList7Days", map, getPageCode());
    }
}
