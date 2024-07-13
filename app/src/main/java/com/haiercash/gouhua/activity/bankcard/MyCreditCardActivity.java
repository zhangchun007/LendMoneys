package com.haiercash.gouhua.activity.bankcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.BankCardAdapter;
import com.haiercash.gouhua.adaptor.ListRefreshHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.bankcard.QueryCardBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 项目名称：HaiercashPayAPP
 * 项目作者：刘明戈
 * 创建日期：2016/5/5
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：我的银行卡列表
 * ----------------------------------------------------------------------------------------------------
 */
public class MyCreditCardActivity extends BaseActivity implements ListRefreshHelper.ListRefreshListener, View.OnClickListener {
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout mScrollRefreshLayout;
    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_add_bank_cart)
    LinearLayout ll_add_bank_cart;

    private ListRefreshHelper mRefreshHelper;
    private BankCardAdapter mAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_my_creditcard;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("银行卡管理");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BankCardAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRefreshHelper = new ListRefreshHelper(this, mScrollRefreshLayout, mRecyclerView, this);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            /*接收到带有标识的跳转后的item点击事件*/
            QueryCardBean bean = mAdapter.getItem(position);
            if (bean == null) {
                return;
            }
            Intent reDataIntent = new Intent(mContext, BankCardDetailsActivity.class);
            bean.setCustName(SpHp.getUser(SpKey.USER_CUSTNAME));
            Bundle bundle = new Bundle();
            bundle.putSerializable("bankCard", bean);
            reDataIntent.putExtras(bundle);
            reDataIntent.putExtra("count", mAdapter.getData().size());
            startActivity(reDataIntent);
        });
        mRefreshHelper.build(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRefreshHelper != null) {
            mRefreshHelper.refreshActive();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("wrong", "nothing");
            setResult(10, intent);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*查询银行卡列表*/
    @Override
    public void loadSourceData(int page, int pageSize) {
        showProgress(true);
        String custNo = SpHp.getUser(SpKey.USER_CUSTNO);
        if (CheckUtil.isEmpty(custNo)) {
            showProgress(false);
            mRefreshHelper.setEmptyData(R.drawable.img_empty_normal, "您还没有银行卡哦~");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("custNo", RSAUtils.encryptByRSA(custNo));
        map.put("acctName", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNAME)));
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.URL_GETBANCARDLIST, map, null, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 4) {
            //刷新银行卡列表
            mRefreshHelper.refreshActive();
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        if (success == null) {
            onError(ApiUrl.DATA_PARSER_ERROR);
            return;
        }
        showProgress(false);
        mAdapter.removeAllHeaderView();
        mAdapter.removeAllFooterView();
        List<QueryCardBean> quCardList = JsonUtils.fromJsonArray(success, "info", QueryCardBean.class);
        if (quCardList.size() > 0) {
            mScrollRefreshLayout.setVisibility(View.VISIBLE);
            mAdapter.addFooterView(getFootView());
            mRefreshHelper.updateData(quCardList);
            ll_add_bank_cart.setVisibility(View.GONE);
        } else {
            ll_add_bank_cart.setVisibility(View.VISIBLE);
            ll_add_bank_cart.removeAllViews();
            mScrollRefreshLayout.setVisibility(View.GONE);
            ll_add_bank_cart.addView(getFootView());
        }
    }


    @Override
    public void onError(BasicResponse response, String url) {
        super.onError(response, url);
        mRefreshHelper.setEmptyData(R.drawable.img_empty_normal, "让网络再飞一会儿~");
    }

    private View getFootView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UiUtil.dip2px(mContext, 50));
        params.setMargins(0, 35, 0, 35);
        params.gravity = Gravity.CENTER;
        TextView tv = new TextView(this);
        tv.setBackgroundResource(R.drawable.icon_add_card);
        tv.setLayoutParams(params);
        tv.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, AddBankCardInformaticaActivity.class);
            intent.putExtra("tag", "addBank");
            startActivity(intent);
        });
        return tv;
    }
}