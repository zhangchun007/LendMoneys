package com.haiercash.gouhua.uihelper;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.utils.system.FontCustom;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.ContractPopAdapter;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.databinding.PopAgreementsBinding;

/**
 * 协议组弹窗
 */
public class ContractPop extends BasePopupWindow {
    public ContractPop(BaseActivity context, Object data) {
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
        getBinding().tvTitle.setText("相关合同");
        getBinding().ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        getBinding().rvAgreement.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        ContractPopAdapter contractPopAdapter = new ContractPopAdapter(data);
        contractPopAdapter.addChildClickViewIds(R.id.tvAgreement);
        contractPopAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                dismiss();
                listener.clickPos(position);
            }
        });
        getBinding().rvAgreement.setAdapter(contractPopAdapter);
    }

    @Override
    public void showAtLocation(View view) {
        if (view != null) {
            showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }

    private IClickListener listener;

    public void setOnPosClickListener(IClickListener listener) {
        this.listener = listener;
    }

    public interface IClickListener {
        void clickPos(int pos);
    }
}
