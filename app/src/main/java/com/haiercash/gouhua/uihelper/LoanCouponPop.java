package com.haiercash.gouhua.uihelper;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haiercash.gouhua.activity.CouponRuleActivity;
import com.haiercash.gouhua.adaptor.PopLoanCouponAdapter;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.getpayss.LoanCoupon;
import com.haiercash.gouhua.databinding.PopLoanCouponBinding;
import com.haiercash.gouhua.interfaces.OnPopClickListener;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 借款页面选择免息券弹窗
 */
public class LoanCouponPop extends BasePopupWindow {
    private final OnPopClickListener popClickListener;
    private volatile String mCurrentCouponNo;//传入的选中券


    public LoanCouponPop(BaseActivity context, Object data, String currentCouponNo, OnPopClickListener popClickListener) {
        super(context, data);
        this.popClickListener = popClickListener;
        updateData(data, currentCouponNo);
    }

    private PopLoanCouponBinding getBinding() {
        return (PopLoanCouponBinding) _binding;
    }

    @Override
    protected PopLoanCouponBinding initBinding(LayoutInflater inflater) {
        return PopLoanCouponBinding.inflate(inflater, null, false);
    }

    @SuppressWarnings("unchecked")
    public void updateData(Object data, String currentCouponNo) {
        this.mCurrentCouponNo = currentCouponNo;
        LoanCoupon selectLoanCoupon = null;
        List<LoanCoupon> couponList = null;
        if (data instanceof List) {
            couponList = (List<LoanCoupon>) data;
            for (LoanCoupon loanCoupon : couponList) {
                if (loanCoupon != null && loanCoupon.getCouponNo() != null && loanCoupon.getCouponNo().equals(mCurrentCouponNo)) {
                    selectLoanCoupon = loanCoupon;
                }
            }
        }
        PopLoanCouponAdapter popLoanCouponAdapter = new PopLoanCouponAdapter(selectLoanCoupon, getPageCode(), new PopLoanCouponAdapter.OnCouponClickListener() {
            @Override
            public void onRuleClick(View view, String rule) {
                getBinding().clRoot.post(() -> {
                    int height = getBinding().clRoot.getHeight();
                    CouponRuleActivity.startCouponRuleActivity(mActivity, height, rule, 50);
                });
            }
        });
        getBinding().rvPopLoanCoupon.setAdapter(popLoanCouponAdapter);
        if (couponList != null) {
            popLoanCouponAdapter.replaceData(couponList);
        }
    }

    @Override
    protected void onViewCreated(Object data) {
        setPopupOutsideTouchable(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        getBinding().rvPopLoanCoupon.setLayoutManager(layoutManager);
        layoutManager.setSmoothScrollbarEnabled(true);
        getBinding().ivCouponClose.setOnClickListener(this);
        getBinding().btnLoanCoupon.setOnClickListener(this);
    }

    private View parentV;

    @Override
    public void showAtLocation(View view) {
        parentV = view;
        if (!isShowing()) {
            UMengUtil.pageStart(getPageCode());
        }
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    private String getPageCode() {
        return "BorrowFreeInterestTicketResultPage";
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            UMengUtil.pageEnd(getPageCode());
        }
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        dismiss();
        if (popClickListener != null) {
            if (v == getBinding().ivCouponClose) {
                popClickListener.onViewClick(v, 1, null);
            } else if (v == getBinding().btnLoanCoupon) {
                RecyclerView.Adapter<?> adapter = getBinding().rvPopLoanCoupon.getAdapter();
                if (adapter instanceof PopLoanCouponAdapter) {
                    postEvent(((PopLoanCouponAdapter) adapter).getCurrSelectCoupon());
                    popClickListener.onViewClick(v, 2, ((PopLoanCouponAdapter) adapter).getCurrSelectCoupon());
                }
            }
        }
    }

    //点击确认上报事件
    private void postEvent(LoanCoupon bean) {
        if (bean != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("freeinterestticket_id", UiUtil.getEmptyStr(bean.getCouponNo()));
            map.put("freeinterest_money", UiUtil.getEmptyMoneyStr(bean.getDiscValue()));
            map.put("freeinterest_desc", UiUtil.getEmptyStr(bean.getBatchDesc()));
            UMengUtil.commonClickEvent("FreeInterestTicketConfirmed_Click", "确定", map, getPageCode());
        }
    }
}
