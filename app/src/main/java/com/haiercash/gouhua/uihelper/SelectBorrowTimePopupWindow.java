package com.haiercash.gouhua.uihelper;

import android.view.Gravity;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.bean.ArrayBean;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.SelectBorrowTimeAdapter;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.interfaces.OnPopClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectBorrowTimePopupWindow extends BasePopupWindow {
    @BindView(R.id.rl_time)
    RecyclerView tv_confirm;
    private List<ArrayBean> dataList;
    private OnPopClickListener listener;

    public SelectBorrowTimePopupWindow(BaseActivity context, Object data, OnPopClickListener listener) {
        super(context, data);
        this.dataList = (List<ArrayBean>) data;
        this.listener = listener;
    }

    @Override
    protected int getLayout() {
        return R.layout.pop_borrow_time_select;
    }

    @Override
    protected void onViewCreated(Object data) {
        if (data == null) {
            return;
        }
        dataList = (List<ArrayBean>) data;
        SelectBorrowTimeAdapter wheelAdapter = new SelectBorrowTimeAdapter(dataList, tv_confirm);
        tv_confirm.setLayoutManager(new LinearLayoutManager(mActivity));
        //设置分隔线
        CustomDecoration dividerItemDecoration = new CustomDecoration(mActivity, CustomDecoration.VERTICAL_LIST, R.drawable.shap_line, 0);
        tv_confirm.addItemDecoration(dividerItemDecoration);
        tv_confirm.setAdapter(wheelAdapter);
        wheelAdapter.setOnItemSingleSelectListener((itemPosition, isSelected) -> {
            listener.onViewClick(null, 1, dataList.get(itemPosition));
            dismiss();
        });
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }


    @OnClick({R.id.tv_cancle})
    public void viewOnClick(View view) {
        if (view.getId() == R.id.tv_cancle) {
            dismiss();
        }
    }

    public int getSize() {
        return dataList == null ? 0 : dataList.size();
    }
}
