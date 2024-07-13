package com.haiercash.gouhua.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.SoftHideKeyBoardUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.databinding.FragmentLoanCalculationBinding;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.RepayWayPop;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.NumBerKeyBoard;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/3/27<br/>
 * 描    述：贷款计算<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_CALCULATION)
public class LoanCalculationFragment extends BaseFragment {
    private FragmentLoanCalculationBinding getBinding() {
        return (FragmentLoanCalculationBinding) _binding;
    }

    private NumBerKeyBoard numBerKeyBoard;
    private String inputAmount;//用户输入金额
    private RepayWayPop repayWayPop;
    /**
     * 1、等本等息<br/>
     * 2、等额本息
     */
    private String repayWay;

//    public LoanCalculationFragment() {
//        super(R.layout.fragment_loan_calculation);
//    }

    @Override
    protected FragmentLoanCalculationBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentLoanCalculationBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initEventAndData() {
        //mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mActivity.setTitle("借款试算");
        getBinding().barHeader.setTitle("借款试算", getResources().getColor(R.color.white), null);
        getBinding().barHeader.setLeftImage(R.drawable.icon_page_close, v -> mActivity.onBackPressed());
        getBinding().barHeader.setTitleBackgroundColor(getResources().getColor(R.color.transparent));
        getBinding().tvConfirm.setTypeface(FontCustom.getMediumFont(mActivity));
        setEdtHintValue();
        numBerKeyBoard = new NumBerKeyBoard(mActivity, 1);
        numBerKeyBoard.showSoftKeyboard();
        numBerKeyBoard.hideKeyboard();
        numBerKeyBoard.setOnOkClick(() -> {
            if (CheckUtil.isEmpty(inputAmount)) {
                return;
            }
            if (Double.parseDouble(inputAmount) > 200000) {
                getBinding().etLoanAmount.setText("");
                UiUtil.toast("最大可借金额200000元");
                return;
            }
            getBinding().etLoanAmount.clearFocus();
            numBerKeyBoard.hideKeyboard();
            calculation(false);
        });
        getBinding().etLoanAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getBinding().etLoanAmount.getInputText().startsWith("0")) {
                    getBinding().etLoanAmount.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                inputAmount = getBinding().etLoanAmount.getInputText().replaceAll(",", "");
                if (CheckUtil.isEmpty(inputAmount)) {
                    inputAmount = "0.00";
                }
                calculation(false);
            }
        });
        getBinding().etLoanAmount.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (getBinding() != null && getBinding().etLoanAmount != null)
                    getBinding().etLoanAmount.addIcon();
                numBerKeyBoard.attachTo((EditText) v);
            } else {
                if (getBinding() != null && getBinding().etLoanAmount != null)
                    getBinding().etLoanAmount.removeIcon();
            }
        });
        if (getBinding() != null && getBinding().etLoanAmount != null)
            CheckUtil.formatMoneyThousandth(getBinding().etLoanAmount);
        setonClickByViewId(R.id.tvRepayWay, R.id.tvConfirm, R.id.rlRoot);
        SoftHideKeyBoardUtil.assistActivity(mActivity).setSoftKeyBordListener(new SoftHideKeyBoardUtil.SoftKeyBordListener() {
            @Override
            public void softShowing() {
                try {
                    numBerKeyBoard.hideKeyboard();
                    getBinding().tvConfirm.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void softHide() {
                try {
                    getBinding().tvConfirm.setVisibility(View.VISIBLE);
                    //getBinding().etLoanAmount.clearFocus();
                    //getBinding().detRate.clearFocus();
                    calculation(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        getBinding().rgRateList.setOnCheckedChangeListener((group, checkedId) -> calculation(false));
    }

    @Override
    public void onClick(View view) {
        KeyBordUntil.hideKeyBord2(mActivity);
        getBinding().detRate.clearFocus();
        if (view.getId() == R.id.tvRepayWay) {
            if (repayWayPop == null) {
                repayWayPop = new RepayWayPop(mActivity, null, (view1, flagTag, obj) -> {
                    repayWay = flagTag == 1 ? "1" : "2";
                    getBinding().tvRepayWay.setText(flagTag == 1 ? "等本等息" : "等额本息");
                    calculation(false);
                });
            }
            repayWayPop.showAtLocation(view);
        } else if (view.getId() == R.id.tvConfirm) {
            //sumMoney();
            calculation(true);
        }
    }

    /**
     * 执行计算器
     */
    private void calculation(boolean isGoPage) {
        KeyBordUntil.hideKeyBord2(mActivity);
        getBinding().detRate.clearFocus();
        getBinding().detRate.setTextColor(Color.parseColor("#303133"));
        getBinding().tvConfirm.setBackgroundResource(R.drawable.bg_blue2_radius25);
        if (CheckUtil.isEmpty(inputAmount) || Double.parseDouble(inputAmount) > 200000 || Double.parseDouble(inputAmount) < 500) {
            toastMsg(isGoPage, "请输入500-20万之间的金额");
            return;
        }
        String perNo = getBinding().rb12.isChecked() ? "12" : (
                getBinding().rb9.isChecked() ? "9" : (
                        getBinding().rb6.isChecked() ? "6" : "3"
                )
        );
        if (CheckUtil.isEmpty(perNo)) {
            toastMsg(isGoPage, "请您选择期数");
            return;
        }
        if (CheckUtil.isEmpty(repayWay)) {
            toastMsg(isGoPage, "请您的还款方式");
            return;
        }
        String rate = getBinding().detRate.getText().toString();
        if (CheckUtil.isEmpty(rate)) {
            toastMsg(isGoPage, "请输入您的年利率");
            return;
        }
        if (rate.split("\\.").length == 2 && rate.split("\\.")[1].length() > 2) {
            toastMsg(isGoPage, "年利率将保留两位小数");
            rate = CheckUtil.round(rate);
            //return;
        }
        if (Double.parseDouble(rate) > 36) {
            getBinding().detRate.setTextColor(Color.parseColor("#ff5151"));
            toastMsg(isGoPage, "请输入小于36%的年化利率");
            return;
        }
        getBinding().tvConfirm.setBackgroundResource(R.drawable.bg_blue_radius25);
        if (isGoPage) {
            Bundle bundle = new Bundle();
            bundle.putString("applyAmt", inputAmount);
            bundle.putString("perNo", perNo);
            bundle.putString("repayWay", repayWay);
            bundle.putString("rate", rate);
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_CALCULATION2).put("isShowTitle", false).put(bundle).navigation();
        }
    }

    private void toastMsg(boolean isGoPage, String msg) {
        if (isGoPage) {
            UiUtil.toast(msg);
        }
    }

    /**
     * 设置 输入框的Hint值
     */
    private void setEdtHintValue() {
        SpannableString ss = new SpannableString("输入借款金额");//定义hint的值
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(25, true);//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getBinding().etLoanAmount.setHint(new SpannedString(ss));
    }
}
