package com.haiercash.gouhua.uihelper;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.adaptor.AgreementsPopAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.agreement.SmyAgreementBean;
import com.haiercash.gouhua.databinding.PopAgreementsBinding;

/**
 * 协议组弹窗
 */
public class AgreementsPop extends BasePopupWindow {
    public AgreementsPop(BaseActivity context, Object data) {
        super(context, data);
    }

    @Override
    protected PopAgreementsBinding initBinding(LayoutInflater inflater) {
        return PopAgreementsBinding.inflate(inflater, null, false);
    }

    private PopAgreementsBinding getBinding() {
        return (PopAgreementsBinding) _binding;
    }

    @Override
    protected void onViewCreated(Object data) {
        setPopupOutsideTouchable(true);
        getBinding().tvTitle.setTypeface(FontCustom.getMediumFont(mContext));
        getBinding().ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        getBinding().rvAgreement.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        AgreementsPopAdapter agreementsPopAdapter = new AgreementsPopAdapter(data);
        agreementsPopAdapter.addChildClickViewIds(R.id.tvAgreement);
        agreementsPopAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                dismiss();
                SmyAgreementBean.AgreementBean bean = agreementsPopAdapter.getItem(position);
                if (bean != null && !CheckUtil.isEmpty(bean.getLink())) {
                    WebSimpleFragment.WebService(mActivity, ApiUrl.API_SERVER_URL + bean.getLink(), bean.getName(), WebSimpleFragment.STYLE_OTHERS);
                }
            }
        });
        getBinding().rvAgreement.setAdapter(agreementsPopAdapter);
    }

    @Override
    public void showAtLocation(View view) {
        if (view != null) {
            showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }
}
