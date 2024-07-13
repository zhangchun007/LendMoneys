package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.RepayAdapter;
import com.haiercash.gouhua.base.BaseDialogActivity;
import com.haiercash.gouhua.beans.homepage.HomeRepayBean;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;

import java.util.List;

import butterknife.BindView;

public class RepayListDialogActivity extends BaseDialogActivity {
    public static final String REPAY_DATA = "repayData";
    @BindView(R.id.iv_close)
    AppCompatImageView ivClose;

    @BindView(R.id.rv_repay_list)
    RecyclerView rvRepayList;

    @Override
    protected int getLayout() {
        return R.layout.activity_repay_list_dialog;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        initView();
        initListener();
    }

    private void initListener() {
        ivClose.setOnClickListener(v -> finish());
    }

    private void initView() {
        List<HomeRepayBean> repayList = (List<HomeRepayBean>) getIntent().getSerializableExtra(REPAY_DATA);
        RepayAdapter adapter = new RepayAdapter();
        adapter.setNewData(repayList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvRepayList.setLayoutManager(manager);
        rvRepayList.setAdapter(adapter);
        adapter.addChildClickViewIds(R.id.tv_repay, R.id.cl_repay_item);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (CheckUtil.isEmpty(repayList) || CheckUtil.isEmpty(repayList.get(position)) ||
                        CheckUtil.isEmpty(repayList.get(position).getJumpH5Url())) {
                    showDialog("网络错误，请稍后重试~");
                    return;
                }
                goPay(repayList.get(position).getJumpH5Url());
            }
        });
    }

    //跳转到还款页
    private void goPay(String jumpH5Url) {
        Intent intent = new Intent(this, JsWebBaseActivity.class);
        intent.putExtra("jumpKey", jumpH5Url);
        intent.putExtra("isHideCloseIcon", true);
        startActivity(intent);
    }
}