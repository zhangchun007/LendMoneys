package com.haiercash.gouhua.uihelper;

import android.view.LayoutInflater;

import com.app.haiercash.base.bui.BaseGHFragment;
import com.app.haiercash.base.bui.BaseHeadView;
import com.app.haiercash.base.utils.image.DrawableUtils;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.CalculationBean;
import com.haiercash.gouhua.databinding.HeadCalculationBinding;
import com.haiercash.gouhua.utils.UiUtil;

public class CalculationHeadView extends BaseHeadView<HeadCalculationBinding> {

    public CalculationHeadView(@SuppressWarnings("rawtypes") BaseGHFragment mFragment) {
        super(mFragment);
    }

    /**
     * R.layout.head_calculation
     */
    @Override
    protected HeadCalculationBinding initBinding(LayoutInflater inflater) {
        return HeadCalculationBinding.inflate(inflater);
    }


    @Override
    public void initViewData(Object objData) {
        CalculationBean calBean = (CalculationBean) objData;
        binding.tvRepayAmount.setTypeface(FontCustom.getDinFont(mActivity));
        binding.tvRepayAmount.setText(calBean.getTotalMoney());
        binding.tvApplyAmt.setText(calBean.getApplyAmt());
        binding.tvSumInterest.setText(calBean.getTotalInterest());
        binding.tvSumPerNO.setText(calBean.getPerNo());
        binding.tvRepayWay.setText(calBean.getRepayWay());
        binding.tvDetail.setBackground(DrawableUtils.shapeColorRadius(mActivity.getResources().getColor(R.color.white),
                UiUtil.dip2px(mActivity, 8), UiUtil.dip2px(mActivity, 8), 0, 0));
    }
}
