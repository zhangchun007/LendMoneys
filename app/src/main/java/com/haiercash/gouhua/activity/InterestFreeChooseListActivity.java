package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.InterestAdapter;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.InterestFreeBean;
import com.haiercash.gouhua.databinding.PopLoanCouponBinding;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 免息券选择列表
 */
public class InterestFreeChooseListActivity extends BaseActivity {
    private PopLoanCouponBinding binding;
    private InterestAdapter adapter;
    private String couponNo;
    public static final int RESULT_CODE = 10002;
    private InterestFreeBean interestFreeBean;
    private InterestFreeBean.RepayCouponsBean mRepayCouponsBean;

    @Override
    protected PopLoanCouponBinding initBinding(LayoutInflater inflater) {
        return binding = PopLoanCouponBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("选择免息券");
        interestFreeBean = (InterestFreeBean) getIntent().getSerializableExtra("chooseList");
        String choosedCouponNo = getIntent().getStringExtra("choosedCouponNo");
        setonClickByView(binding.ivCouponClose, binding.btnLoanCoupon);
        initRecyclerviewAdapter();
        if (!CheckUtil.isEmpty(choosedCouponNo) && interestFreeBean != null && !CheckUtil.isEmpty(interestFreeBean.getCoupons())) {
            for (int i = 0; i < interestFreeBean.getCoupons().size(); i++) {
                //指定
                if (choosedCouponNo.equals(interestFreeBean.getCoupons().get(i).getCouponNo())) {
                    update(interestFreeBean.getCoupons().get(i), true);
                    break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 50 && data != null && data.getBooleanExtra("closeThis", false)) {
            finish();
        }
    }

    private void initRecyclerviewAdapter() {
        binding.rvPopLoanCoupon.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InterestAdapter(true);
        adapter.setNewData(interestFreeBean != null ? interestFreeBean.getCoupons() : null);
        binding.rvPopLoanCoupon.setAdapter(adapter);
        adapter.addChildClickViewIds(R.id.vBg, R.id.tv_rule);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            InterestFreeBean.RepayCouponsBean repayCouponsBean = (InterestFreeBean.RepayCouponsBean) adapter.getData().get(position);
            if (view.getId() == R.id.vBg) {
                if ("N".equals(repayCouponsBean.getCanUseState())) {
                    return;
                }
                if (repayCouponsBean.hasBind() && "Y".equals(repayCouponsBean.getCanUseState()) && repayCouponsBean.isCheck()) {
                    UiUtil.toast(getString(R.string.coupon_has_bind));
                    return;
                }
                update(repayCouponsBean, false);
            } else if (view.getId() == R.id.tv_rule) {
                postUmClickEvent("FreeCardDirection_Click", this.adapter.getItem(position), view);
                binding.clRoot.post(() -> {
                    int height = binding.clRoot.getHeight();
                    List<InterestFreeBean.RepayCouponsBean> coupons = interestFreeBean.getCoupons();
                    InterestFreeBean.RepayCouponsBean item = coupons.get(position);
                    CouponRuleActivity.startCouponRuleActivity(InterestFreeChooseListActivity.this, height, item.getBatchDetailDesc(), 50);
                });
            }
        });
    }

    private void update(InterestFreeBean.RepayCouponsBean repayCouponsBean, boolean init) {
        //已绑定的券不能取消勾选
        if (mRepayCouponsBean != null && mRepayCouponsBean.hasBind() && "Y".equals(mRepayCouponsBean.getCanUseState())) {
            return;
        }
        boolean check = !repayCouponsBean.isCheck();
        repayCouponsBean.setCheck(check);
        if (check) {
            if (mRepayCouponsBean != null && !repayCouponsBean.getCouponNo().equals(mRepayCouponsBean.getCouponNo())) {
                mRepayCouponsBean.setCheck(false);
            }
            if (!init) {
                postUmClickEvent("FreeCard_Click", repayCouponsBean, null);
            }
            couponNo = repayCouponsBean.getCouponNo();
            mRepayCouponsBean = repayCouponsBean;
        } else {
            couponNo = "";
            mRepayCouponsBean = null;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivCouponClose) {
            finish();
        } else if (view.getId() == R.id.btnLoanCoupon) {
            Intent intent = new Intent();
            intent.putExtra("couponNo", couponNo);
            intent.putExtra("interestFreeCoupon", mRepayCouponsBean);
            postEvent();
            setResult(RESULT_CODE, intent);
            finish();
        }

    }

    //点击确认上报事件
    private void postEvent() {
        if (mRepayCouponsBean != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("freeinterestticket_id", UiUtil.getEmptyStr(mRepayCouponsBean.getCouponNo()));
            map.put("freeinterest_money", UiUtil.getEmptyMoneyStr(mRepayCouponsBean.getDiscValue()));
            map.put("freeinterest_desc", UiUtil.getEmptyStr(mRepayCouponsBean.getDescribe1()));
            UMengUtil.commonClickEvent("FreeInterestTicketConfirmed_Click", "确定", map, getPageCode());
        }
    }

    @Override
    protected String getPageCode() {
        return "FreeInterestTicketPage";
    }

    private void postUmClickEvent(String clickEventId, InterestFreeBean.RepayCouponsBean bean, View view) {
        if (bean == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "还款环节");
        map.put("free_card_No", UiUtil.getEmptyStr(bean.getBatchNo()));
        map.put("free_card_id", UiUtil.getEmptyStr(bean.getCouponNo()));
        map.put("free_card_type", UiUtil.getEmptyStr(bean.getKindName()));
        //具体可在哪一期使用
        map.put("free_card_certain_period", UiUtil.getEmptyStr(bean.getBindPerdNo()));
        //可使用在最低总期数为多少的产品上使用（期数类型3期、6期等）
//        map.put("free_card_loan_periods", bean.getBindPerdNo());
        String date = "";
        if (!CheckUtil.isEmpty(bean.getBindStartDt())) {
            date += TimeUtil.getNeedDate(bean.getBindStartDt());
        }
        date += "-";
        if (!CheckUtil.isEmpty(bean.getBindEndDt())) {
            date += TimeUtil.getNeedDate(bean.getBindEndDt());
        }
        map.put("free_card_timelimit", date);
        map.put("free_card_name", bean.getCouponTypeName());
        UMengUtil.commonClickEvent(clickEventId, view instanceof TextView ? ((TextView) view).getText().toString() : "", map, getPageCode());
    }
}
