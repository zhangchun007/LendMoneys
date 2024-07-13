package com.haiercash.gouhua.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.image.DrawableUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.CalculationAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.CalculationBean;
import com.haiercash.gouhua.databinding.FgmTmpBinding;
import com.haiercash.gouhua.databinding.FragmentLoanCalculation2Binding;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.CalculationHeadView;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/3/27<br/>
 * 描    述：贷款计算页面2<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_CALCULATION2)
public class LoanCalculationFragment2 extends BaseFragment {
    private FragmentLoanCalculation2Binding getBinding() {
        return (FragmentLoanCalculation2Binding) _binding;
    }
    private CalculationHeadView headView;
    private CalculationAdapter mAdapter;

//    public LoanCalculationFragment2() {
//        super(R.layout.fragment_loan_calculation2);
//    }

    @Override
    protected FragmentLoanCalculation2Binding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentLoanCalculation2Binding.inflate(inflater, container, false);
    }

    @Override
    protected void initEventAndData() {
        if (!calculationSubmit()) {
            UiUtil.toast("计算错误，请稍后重试");
            mActivity.finish();
            return;
        }
        mActivity.setTitle("借款试算");
        getBinding().barHeader.setTitle("借款试算");
        getBinding().barHeader.setTitleBackgroundColor(getResources().getColor(R.color.white));
        setonClickByViewId(R.id.headLeft, R.id.tvBackHome);
        headView = new CalculationHeadView(this);
        getBinding().rvList.setLayoutManager(new LinearLayoutManager(mActivity));
        getBinding().rvList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private float maxY = 0, blankHeight = 0, headHeight = 0;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] location = new int[2];
                headView.getHeadView().findViewById(R.id.tvRepayAmount).getLocationInWindow(location);
                int locationY = location[1];
                if (headHeight == 0) {
                    headHeight = getBinding().barHeader.getHeight();
                }
                if (maxY < locationY) {
                    maxY = locationY;
                    blankHeight = maxY - headHeight;
                }
                float alpha = 1.00F - ((locationY - headHeight) / blankHeight);
                //System.out.println("onScrollChange-->A: " + alpha + " ;L: " + locationY + " ;H: " + headHeight + " ;B: " + blankHeight);
                //setStatusBarTextColor(alpha > 0);
                //setBackColorBarView(getResources().getColor(R.color.transparent));
                getBinding().barHeader.setAlpha(alpha);
                getBinding().ivBg.setAlpha(1.00F - alpha);
                //getTitleBarView().setImageAlpha(alpha > 0 ? 255 : 0);
            }
        });

        mAdapter = new CalculationAdapter(mActivity, getArguments().getString("repayWay"));
        getBinding().rvList.setAdapter(mAdapter);
        View view = getLayoutInflater().inflate(R.layout.foot_calculation, null);
        view.findViewById(R.id.tvRem).setBackground(DrawableUtils.shapeColorRadiusTopBottom(Color.WHITE, 0, UiUtil.dip2px(mActivity, 8)));
        mAdapter.removeAllFooterView();
        mAdapter.addFooterView(view);
        mAdapter.removeAllHeaderView();
        mAdapter.addHeaderView(headView.getHeadView());
    }

    /**
     * 贷款计算
     */
    private boolean calculationSubmit() {
        if (getArguments() == null) {
            return false;
        }
        String applyAmt = getArguments().getString("applyAmt");//输入的金额
        String perNo = getArguments().getString("perNo");//选择的期数
        String repayWay = getArguments().getString("repayWay");//还款方式  1、等额本息<br/>2、等额还本付息
        String rate = getArguments().getString("rate");//年利率
        Map<String, String> map = new HashMap<>();
        map.put("principal", applyAmt);
        map.put("rate", rate);
        map.put("months", perNo);
        map.put("repayWay", repayWay);
        netHelper.postService(ApiUrl.POST_URL_LOAN_CALC, map, CalculationBean.class);
        //if ("1".equals(repayWay)) {//等额本息
        //    calculateEqualPrincipalAndInterest(Double.parseDouble(applyAmt), Integer.parseInt(perNo), Float.parseFloat(rate));
        //} else {//等本等息
        //    calculate(Double.parseDouble(applyAmt), Integer.parseInt(perNo), Float.parseFloat(rate));
        //}
        return true;
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        super.onSuccess(t, url);
        CalculationBean calBean = (CalculationBean) t;
        headView.initViewData(calBean);
        mAdapter.setNewData(calBean.getMapList());
    }

    @Override
    public void onError(String error) {
        super.onError(error);
    }
    //    /**
//     * 等本等息
//     *
//     * @param principal 贷款总额
//     * @param months    贷款期限
//     * @param rate      贷款利率
//     */
//    public void calculate(double principal, int months, double rate) {
//        calBean = new CalculationBean();
//        double monthRate = rate / (100 * 12);//月利率
//        double preLoan = principal / months + principal * monthRate;//每月还款金额
//        double totalMoney = preLoan * months;//还款总额
//        double interest = totalMoney - principal;//还款总利息
//        calBean.setTotalMoney(CheckUtil.round(String.valueOf(totalMoney)));//还款总额
//        calBean.setApplyAmt(CheckUtil.round(String.valueOf(principal)));//本金
//        calBean.setTotalInterest(CheckUtil.round(String.valueOf(interest)));//还款总利息
//        calBean.setRepayWay("等本等息");
//        calBean.setPerNo(String.valueOf(months));//还款期限
//        List<Map<String, String>> mapList = new ArrayList<>();
//        for (int i = 0; i < months; i++) {
//            Map<String, String> map = new HashMap<>();
//            map.put("perNo", String.valueOf(i + 1));
//            map.put("preLoan", CheckUtil.round(String.valueOf(preLoan)));//每月还款金额
//            map.put("principal", CheckUtil.round(String.valueOf(principal / months)));//月还款本金
//            map.put("interest", CheckUtil.round(String.valueOf(principal * monthRate)));//月还款利息  Arith.format()
//            mapList.add(map);
//        }
//        calBean.setMapList(mapList);
//    }

//    /**
//     * 计算等额本息还款
//     *
//     * @param principal 贷款总额
//     * @param months    贷款期限
//     * @param rate      贷款利率
//     */
//    public void calculateEqualPrincipalAndInterest(double principal, int months, double rate) {
//        calBean = new CalculationBean();
//        double monthRate = rate / (100 * 12);//月利率
//        double preLoan = (principal * monthRate * Math.pow((1 + monthRate), months)) / (Math.pow((1 + monthRate), months) - 1);//每月还款金额
//        double totalMoney = preLoan * months;//还款总额
//        double interest = totalMoney - principal;//还款总利息
//        calBean.setTotalMoney(CheckUtil.round(String.valueOf(totalMoney)));//还款总额
//        calBean.setApplyAmt(CheckUtil.round(String.valueOf(principal)));//本金
//        //calBean.set(FORMAT.format(principal));//贷款总额
//        calBean.setTotalInterest(CheckUtil.round(String.valueOf(interest)));//还款总利息
//        calBean.setRepayWay("等额本息");
//        calBean.setPerNo(String.valueOf(months));//还款期限
//        List<Map<String, String>> mapList = new ArrayList<>();
//        double payMoney = 0;//已归还的本金
//        for (int i = 0; i < months; i++) {
//            Map<String, String> map = new HashMap<>();
//            map.put("perNo", String.valueOf(i + 1));
//            map.put("preLoan", CheckUtil.round(String.valueOf(preLoan)));//每月还款金额
//            double mothInterest = (principal - payMoney) * monthRate;//利息
//            double mothPrincipal = preLoan - interest;//月还款本金
//            payMoney += mothPrincipal;
//            map.put("principal", CheckUtil.round(String.valueOf(mothPrincipal)));//月还款本金
//            map.put("interest", CheckUtil.round(String.valueOf(mothInterest)));//月还款利息  Arith.format()
//            mapList.add(map);
//        }
//        calBean.setMapList(mapList);
//    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.headLeft) {
            mActivity.onBackPressed();
        } else if (v.getId() == R.id.tvBackHome) {
            MainActivity activity = ActivityUntil.findActivity(PagePath.ACTIVITY_MAIN);
            if (activity != null) {
                activity.setFragment();
            } else {
                ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).navigation();
            }
            ActivityUntil.finishOthersActivityByPageKey(PagePath.ACTIVITY_MAIN);
        }
    }
}
