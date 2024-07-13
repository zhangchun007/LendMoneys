package com.haiercash.gouhua.uihelper;

import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.adaptor.ProtacalPopAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.login.QueryAgreementListBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/5/21<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class RegisterAgreementPopupWindow extends BasePopupWindow {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    ProtacalPopAdapter mAdapter;


    public RegisterAgreementPopupWindow(BaseActivity context, Object data) {
        super(context, data);
    }

    @Override
    protected int getLayout() {
        return R.layout.pop_register_agreement;
    }

    @Override
    protected void onViewCreated(Object data) {
        List<QueryAgreementListBean> mList=new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        if (data != null) {
            mList = (List<QueryAgreementListBean>) data;
        }
        mAdapter = new ProtacalPopAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);

        List<QueryAgreementListBean> finalMList = mList;
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                WebSimpleFragment.WebService(mActivity, ApiUrl.API_SERVER_URL + finalMList.get(position).getContUrl(), finalMList.get(position).getContName());
            }
        });
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    @OnClick({R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

}
