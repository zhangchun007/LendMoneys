package com.haiercash.gouhua.activity.bankcard;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.BankCardListAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.bankcard.BankcardBean;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/6/20.
 * 银行卡列表-支持银行
 */
public class BankCardListActivity extends BaseActivity /*implements ListRefreshHelper.ListRefreshListener*/ {
    // @BindView(R.id.srl_refresh)
    //  SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;
    private BankCardListAdapter mAdapter;

    // private ListRefreshHelper mRefreshHelper;

    @Override
    protected int getLayout() {
        return R.layout.activity_bankcard_list;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("银行卡还款限额");//setTitle("支持银行");
        // mSwipeRefreshLayout.setEnabled(false);
        //mSwipeRefreshLayout.setRefreshing(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BankCardListAdapter();

        mAdapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.bankcard_head, mRecyclerView, false));
        //mRefreshHelper = new ListRefreshHelper(this, mSwipeRefreshLayout, mRecyclerView, this);
        // mRefreshHelper.build(true, false);

    }
  /*  @Override
    public void loadSourceData(int page, int pageSize) {

    }*/

    @Override
    protected void onResume() {
        super.onResume();
        showProgress(true);
        netHelper.getService(ApiUrl.url_yinghangmingcheng_get, null);
    }

    @Override
    public void onSuccess(Object response, String url) {
        showProgress(false);
        if (ApiUrl.url_yinghangmingcheng_get.equals(url)) {
            //mRefreshHelper.updateData(JsonUtils.fromJsonArray(response, BankcardBean.class));
            List<BankcardBean> list = JsonUtils.fromJsonArray(response, BankcardBean.class);
            if (CheckUtil.isEmpty(list)) {
                return;
            }
            mAdapter.setNewData(list);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        //mRefreshHelper.updateData(null);
    }
}
