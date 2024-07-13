package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.ChooseDischargeLoanAdapter;
import com.haiercash.gouhua.adaptor.ListRefreshHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.OpenDischargeBean;

/**
 * 选择结清贷款页面
 */
public class ChooseDischargeLoanActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rlChooseLoanDischarge;
    private SwipeRefreshLayout srl_refresh;
    private OpenDischargeBean openDischargeBean;
    private ChooseDischargeLoanAdapter adapter;
    private boolean mHasEmail;//是否有邮箱

    @Override
    protected int getLayout() {
        return R.layout.activity_choose_discharge_loan;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("结清证明");
        setRightImage(R.drawable.iv_blue_details, v -> {
            Intent intent = new Intent(mContext, OpenDischargeActivity.class);
            startActivity(intent);
        });
        rlChooseLoanDischarge = findViewById(R.id.rl_choose_loan_discharge);
        srl_refresh = findViewById(R.id.srl_refresh);
        openDischargeBean = (OpenDischargeBean) getIntent().getSerializableExtra("dischargeBean");
        initRecyclerviewAdapter();
        ListRefreshHelper.initSwipeRefresh(this, srl_refresh);
        srl_refresh.setOnRefreshListener(this);
        autoRefresh();
    }

    private void autoRefresh() {
        srl_refresh.post(() -> srl_refresh.setRefreshing(true));
        onRefresh();
    }

    private void initRecyclerviewAdapter() {
        rlChooseLoanDischarge.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.shap_line20);
        if (drawable != null) {
            dividerItemDecoration.setDrawable(drawable);
        }
        rlChooseLoanDischarge.addItemDecoration(dividerItemDecoration);
        adapter = new ChooseDischargeLoanAdapter();
        if (!CheckUtil.isEmpty(openDischargeBean) && !CheckUtil.isEmpty(openDischargeBean.getLoans())) {
            adapter.setNewData(openDischargeBean.getLoans());
            rlChooseLoanDischarge.setAdapter(adapter);
        }
        adapter.setOnItemCheckBoxClictListener(applSeq -> {
            if (openDischargeBean != null && "N".equals(openDischargeBean.getHasEmail())) {
                showDialog(openDischargeBean.getNoEmailMsg());
            } else {
                Intent intent = new Intent(mContext, ChooseDischargeLoanPwdActivity.class);
                intent.putExtra("applseq", applSeq);
                startActivityForResult(intent, 99);
            }
        });
    }

    @Override
    public void onRefresh() {
        netHelper.postService(ApiUrl.POST_DISCHARGE_LIST, null, OpenDischargeBean.class);
    }

    @Override
    public void onSuccess(Object success, String url) {
        showProgress(false);
        if (ApiUrl.POST_DISCHARGE_LIST.equals(url)) {
            srl_refresh.post(() -> srl_refresh.setRefreshing(false));
            openDischargeBean = (OpenDischargeBean) success;
            if (!CheckUtil.isEmpty(openDischargeBean) && !CheckUtil.isEmpty(openDischargeBean.getLoans())) {
                adapter.setNewData(openDischargeBean.getLoans());
                rlChooseLoanDischarge.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        srl_refresh.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String reason;
        if (requestCode == 99 && data != null && !TextUtils.isEmpty(reason = data.getStringExtra("reason"))) {
            //失败原因
            showDialog(reason);
        }
    }
}
