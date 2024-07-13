package com.haiercash.gouhua.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.ApplyRecordeAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseListFragment;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.beans.CreditLifeBorrowBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.uihelper.CreditLifeHelp;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: liuyaxun
 * Date :    2019/2/25
 * FileName: ApplyRecordeFragment
 * Description: 信用生活--申请记录
 */
public class ApplyRecordeFragment extends BaseListFragment {
    public static final int ID = ApplyRecordeFragment.class.hashCode();
    @BindView(R.id.ll_yellow_expire)
    RelativeLayout llYellowExpire;
    private CreditLifeHelp creditLifeHelp;

    public static ApplyRecordeFragment newInstance(Bundle extra) {
        final ApplyRecordeFragment f = new ApplyRecordeFragment();
        if (extra != null) {
            f.setArguments(extra);
        }
        return f;
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle("申请记录");
        if (mActivity.getTitleBarView() == null) {
            mActivity.setLeftIvVisibility(false);
        } else {
            mActivity.setTitleVisibility(false);
        }
        llYellowExpire.setVisibility(View.VISIBLE);
        super.initEventAndData();
        showProgress(true);
        startRefresh(true, false, true, R.drawable.img_empty_bill, "申请记录空空如也～");
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            @SuppressWarnings("unchecked") List<CreditLifeBorrowBean> records = mAdapter.getData();
            if (CheckUtil.isEmpty(records)) {
                return;
            }
            String channelName = records.get(position).getChannelName();
            creditLifeHelp = new CreditLifeHelp(mActivity, records.get(position));
            creditLifeHelp.getUniteLoginInfo();
        });
    }

    @Override
    public BaseAdapter getAdapter() {
        return new ApplyRecordeAdapter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_apply_recorde;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSourceData(1, 0);
    }

    @Override
    public void loadSourceData(int page, int pageSize) {
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        if (TextUtils.isEmpty(userId)) {
            mRefreshHelper.updateData(null);
            showProgress(false);
            return;
        }
        //查询第三方申请记录
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("userId", EncryptUtil.simpleEncrypt(userId));
        map.put("channelType", "Y");
        netHelper.getService(ApiUrl.QUERY_APPL_RECOCED, map);
    }

    @Override
    public void onSuccess(Object t, String url) {
        super.onSuccess(t, url);
        if (t == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        showProgress(false);
        List<CreditLifeBorrowBean> applyRecordeBeanList = JsonUtils.fromJsonArray(t, "records", CreditLifeBorrowBean.class);
        if (applyRecordeBeanList.size() > 0) {
            mRefreshHelper.updateData(applyRecordeBeanList);
        }
    }

    @OnClick({R.id.iv_close_yellow})
    public void onViewClick(View view) {
        if (view.getId() == R.id.iv_close_yellow) {
            llYellowExpire.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        if (creditLifeHelp != null) {
            creditLifeHelp.destory();
        }
        super.onDestroyView();
    }
}
