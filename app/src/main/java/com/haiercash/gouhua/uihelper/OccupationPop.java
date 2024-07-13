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
import com.haiercash.gouhua.adaptor.OccupationAdapter;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.ModelBean;
import com.haiercash.gouhua.databinding.PopOccupationBinding;

import java.util.List;

/**
 * 职业选择弹窗
 */
public class OccupationPop extends BasePopupWindow {

    private OnOccupationSelectListener onOccupationSelectListener;

    public OccupationPop(BaseActivity context, Object data, OnOccupationSelectListener onOccupationSelectListener) {
        super(context, data);
        this.onOccupationSelectListener = onOccupationSelectListener;
    }

    public void setOnOccupationSelectListener(OnOccupationSelectListener onOccupationSelectListener) {
        this.onOccupationSelectListener = onOccupationSelectListener;
    }

    @Override
    protected PopOccupationBinding initBinding(LayoutInflater inflater) {
        return PopOccupationBinding.inflate(inflater, null, false);
    }

    private PopOccupationBinding getBinding() {
        return (PopOccupationBinding) _binding;
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
        @SuppressWarnings("unchecked")
        OccupationAdapter occupationAdapter = new OccupationAdapter((List<ModelBean>) data);
        occupationAdapter.addChildClickViewIds(R.id.tvOccupation);
        occupationAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.tvOccupation) {
                    dismiss();
                    if (onOccupationSelectListener != null) {
                        onOccupationSelectListener.OnOccupationSelect(occupationAdapter.getItem(position));
                    }
                }
            }
        });
        getBinding().rvOccupation.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        getBinding().rvOccupation.setAdapter(occupationAdapter);

    }

    @Override
    public void showAtLocation(View view) {
        if (view != null) {
            showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }

    public void showOccupationPop(View view, String selectName) {
        OccupationAdapter adapter = (OccupationAdapter) getBinding().rvOccupation.getAdapter();
        if (adapter != null) {
            adapter.setSelectName(selectName);
            adapter.notifyDataSetChanged();
        }
        showAtLocation(view);
    }

    public interface OnOccupationSelectListener {
        void OnOccupationSelect(ModelBean bean);
    }
}
