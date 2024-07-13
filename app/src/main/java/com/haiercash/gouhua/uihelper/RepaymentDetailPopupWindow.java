package com.haiercash.gouhua.uihelper;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.repayment.CashierInfo;
import com.haiercash.gouhua.databinding.ViewDialogRepaymentDetailBinding;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * 还款详情弹窗
 */
public class RepaymentDetailPopupWindow extends BasePopupWindow {

    private ViewDialogRepaymentDetailBinding getBinding() {
        return (ViewDialogRepaymentDetailBinding) _binding;
    }

    public RepaymentDetailPopupWindow(BaseActivity context, Object data) {
        super(context, data);
    }

    /**
     * R.layout.view_dialog_repayment_detail
     */
    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return ViewDialogRepaymentDetailBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(Object data) {
        CashierInfo mCashierInfo = (CashierInfo) data;
        if (CheckUtil.isEmpty(mCashierInfo)) {
            return;
        }
        getBinding().tv1.setText(UiUtil.getStr(mCashierInfo.getPrcpAmt(), "元"));
        getBinding().tv2.setText(UiUtil.getStr(mCashierInfo.getNormInt(), "元"));
        //手续费（贷款）	新加字段：psFeeAmtNew	若为空或为0则不展示否则展示
        if (CheckUtil.isZero(mCashierInfo.psFeeAmtNew)) {
            getBinding().rl3.setVisibility(View.GONE);
        } else {
            getBinding().tv3.setText(UiUtil.getStr(mCashierInfo.psFeeAmtNew, "元"));
        }
        //提前还款手续费	新字段：earlyRepayAmt	若为空或为0则不展示否则展示
        if (CheckUtil.isZero(mCashierInfo.earlyRepayAmt)) {
            getBinding().rl4.setVisibility(View.GONE);
        } else {
            getBinding().tv4.setText(UiUtil.getStr(mCashierInfo.earlyRepayAmt, "元"));
        }
        //逾期罚息	odIntAmt	若为空或为0则不展示否则展示
        if (CheckUtil.isZero(mCashierInfo.getOdIntAmt())) {
            getBinding().rl5.setVisibility(View.GONE);
        } else {
            getBinding().tv5.setText(UiUtil.getStr(mCashierInfo.getOdIntAmt(), "元"));
        }
        //逾期手续费	新加字段：odFeeAmt	若为空或为0则不展示否则展示
        if (CheckUtil.isZero(mCashierInfo.odFeeAmt)) {
            getBinding().rl6.setVisibility(View.GONE);
        } else {
            getBinding().tv6.setText(UiUtil.getStr(mCashierInfo.odFeeAmt, "元"));
        }
        goneParentViewOrSetText(mCashierInfo.getSetlTotalAmtCr(), getBinding().tv7);
        getBinding().ivClose.setOnClickListener(this);
        getBinding().rlPop.setOnClickListener(this);
        setPopupOutsideTouchable(true);
    }

    private void goneParentViewOrSetText(String amount, TextView view) {
        if (CheckUtil.isEmpty(amount) || CheckUtil.isZero(amount)) {
            View parentView = (View) view.getParent();
            parentView.setVisibility(View.GONE);
        } else {
            view.setText(UiUtil.getStr(amount, "元"));
        }
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_close || view.getId() == R.id.rl_pop) {
            dismiss();
        }
    }

    public void updateData(CashierInfo mCashierInfo) {
        onViewCreated(mCashierInfo);
    }
}
