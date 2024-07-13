package com.haiercash.gouhua.choosebank;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.haiercash.base.bui.BaseHeadView;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.bankcard.AddBankCardInformaticaActivity;
import com.haiercash.gouhua.activity.bankcard.BankCardListActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.databinding.ViewAddbankFootviewBinding;
import com.haiercash.gouhua.utils.UiUtil;


/**
 * Author: Sun<br/>
 * Date :    2018/5/16<br/>
 * FileName: AddBankFooterView<br/>
 * Description:<br/>
 */
public class AddBankFooterView extends BaseHeadView<ViewAddbankFootviewBinding> {
    private boolean canAddBankCard = true;

    public AddBankFooterView(@SuppressWarnings("rawtypes") BaseFragment fragment) {
        super(fragment);
    }

    public TextView getTvAddBankcard() {
        return binding.tvAddBankcard;
    }

    public void setCanAddBankCard(boolean canAddBankCard) {
        this.canAddBankCard = canAddBankCard;
    }

    /**
     * R.layout.view_addbank_footview
     */
    @Override
    protected ViewAddbankFootviewBinding initBinding(LayoutInflater inflater) {
        return ViewAddbankFootviewBinding.inflate(inflater);
    }

    @Override
    public void initViewData(Object objData) {
        Drawable drawableLeft = ContextCompat.getDrawable(mContext, R.drawable.icon_tips);
        if (drawableLeft != null) {
            drawableLeft.setBounds(0, 0, UiUtil.dip2px(mContext, 15), UiUtil.dip2px(mContext, 15));
        }
        binding.tvLimitBankcard.setCompoundDrawables(drawableLeft, null, null, null);
        binding.tvAddBankcard.setOnClickListener(this::onClick);
        binding.tvLimitBankcard.setOnClickListener(this::onClick);
    }

    public void onClick(View view) {
        if (view == binding.tvAddBankcard) {
            if (canAddBankCard) {
                Intent intent = new Intent(mActivity, AddBankCardInformaticaActivity.class);
                mActivity.startActivityForResult(intent, AddBankCardInformaticaActivity.ADD_BANK_REQUEST_CODE);
            }
        } else if (view == binding.tvLimitBankcard) {
            mActivity.startActivity(new Intent(mActivity, BankCardListActivity.class));
        }
    }
}
