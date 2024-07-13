package com.haiercash.gouhua.uihelper;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.app.haiercash.base.utils.image.DrawableUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.databinding.PopRepayWayBinding;
import com.haiercash.gouhua.interfaces.OnPopClickListener;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * R.layout.pop_repay_way
 */
public class RepayWayPop extends BasePopupWindow {
    private final OnPopClickListener popClickListener;

    private PopRepayWayBinding getBinding() {
        return (PopRepayWayBinding) _binding;
    }


    public RepayWayPop(BaseActivity context, Object data, OnPopClickListener onPopClickListener) {
        super(context, data);
        popClickListener = onPopClickListener;
    }

    @Override
    protected PopRepayWayBinding initBinding(LayoutInflater inflater) {
        return PopRepayWayBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(Object data) {
        getBinding().ivClose.setOnClickListener(this);
        getBinding().tvWay1.setOnClickListener(this);
        getBinding().tvWay2.setOnClickListener(this);
        getBinding().rlContent.setBackground(DrawableUtils.shapeColorRadius(mActivity.getResources().getColor(R.color.white),
                UiUtil.dip2px(mActivity, 20), UiUtil.dip2px(mActivity, 20), 0, 0));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == getBinding().ivClose) {
            dismiss();
        } else if (v == getBinding().tvWay1) {
            if (popClickListener != null) {
                popClickListener.onViewClick(v, 2, null);
            }
            dismiss();
        } else if (v == getBinding().tvWay2) {
            if (popClickListener != null) {
                popClickListener.onViewClick(v, 1, null);
            }
            dismiss();
        }
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }
}
