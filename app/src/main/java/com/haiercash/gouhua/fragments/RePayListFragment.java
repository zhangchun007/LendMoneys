package com.haiercash.gouhua.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.FilterCriteriaActivity;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.adaptor.RePayListAdapter;
import com.haiercash.gouhua.adaptor.bean.RepayListRecordSection;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseListFragment;
import com.haiercash.gouhua.beans.borrowmoney.RepayBean;
import com.haiercash.gouhua.beans.borrowmoney.RepayRecordBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.SpHp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/1/10<br/>
 * 描    述：还款记录<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.ORDER_REPAY_LIST)
public class RePayListFragment extends BaseListFragment {
    @BindView(R.id.tv_filter_criteria)
    TextView tvFilterCriteria;
    private String startTime;
    private String endTime;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_repay_list;
    }

    @Override
    protected void initEventAndData() {
        super.initEventAndData();
        startTime = TimeUtil.getDataBefore(0, 0, -89) + " 00:00:00";
        endTime = TimeUtil.calendarToString2() + " 23:59:59";
        tvFilterCriteria.setText("近三月");
        showProgress(true);
        mRefreshHelper.build(true, false).setEmptyData(R.drawable.img_empty_repay_list, "");
        mRefreshHelper.setEmptyViewBgResource(R.color.color_EFF2F5);
        mRefreshHelper.setEmptyViewLineGone(true);
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new RePayListAdapter();
    }

    @OnClick(R.id.tv_filter_criteria)
    public void viewOnClick() {
//        ARouterUntil.startActivityForResult(this, PagePath.FRAGMENT_FILTER_CRITERIA, 5);
        Intent intent = new Intent(mActivity, FilterCriteriaActivity.class);
        startActivityForResult(intent, 5);
    }

    @Override
    public void onItemClick(Object item) {
        if (item != null) {
            RepayListRecordSection o = (RepayListRecordSection) item;
            if (o.isHeader) {
                return;
            }
            Bundle extra = new Bundle();
            extra.putInt("PageType", BorrowRepayDetailFragment.REPAY_DETAIL);
            extra.putSerializable("Record", o.t);
            ContainerActivity.to(getActivity(), BorrowRepayDetailFragment.ID, extra);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == 5) {
            if (data.getExtras() != null) {
                startTime = data.getExtras().getString("startTime");
                endTime = data.getExtras().getString("endTime");
                String dealSelectTips=data.getExtras().getString("dealSelectTips");
                if (!TextUtils.isEmpty(dealSelectTips)){
                    tvFilterCriteria.setText(dealSelectTips);
                }else {
                    tvFilterCriteria.setText(String.format("%s至%s", TimeUtil.stringToString(startTime), TimeUtil.stringToString(endTime)));
                }
                showProgress(true);
                mRefreshHelper.refreshActive();
            }
        }
    }

    @Override
    public void loadSourceData(int page, int pageSize) {
        String idNo = SpHp.getUser(SpKey.USER_CERTNO);
        if (CheckUtil.isEmpty(idNo)) {
            mRefreshHelper.updateData(null);
            showProgress(false);
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("certNo", RSAUtils.encryptByRSA(idNo));
        map.put("dateType", "customize");//每页显示条数
        map.put("startRequestTime", startTime);//开始时间
        map.put("endRequestTime", endTime);//结束时间
        map.put("isRsa", "Y");
        //map.put("endRequestTime", TimeUtil.calendarToString());//结束时间
        netHelper.postService(ApiUrl.URL_PWG_REPAY_RECORD, map, null, true);
    }

    @Override
    public void onSuccess(Object response, String flag) {
        showProgress(false);
        mAdapter.getData().clear();
        mAdapter.notifyDataSetChanged();
        List<RepayRecordBean> list = JsonUtils.fromJsonArray(response, RepayRecordBean.class);
        if (CheckUtil.isEmpty(list) && mAdapter.getData().size() > 0) {
            mRefreshHelper.setLoadMoreEnd(true);
            return;
        }
        List<RepayListRecordSection> mData = new ArrayList<>();
        for (RepayRecordBean ben : list) {
            mData.add(new RepayListRecordSection(true, ben.getRepayRecordTime()));
            for (RepayBean repayBean : ben.getRepayRecordList()) {
               /* if("01".equals(repayBean.getBizType())&&("FAIL".equals(repayBean.getStatus())||"PROCESSING".equals(repayBean.getStatus()))){
                    continue;
                }*/
                mData.add(new RepayListRecordSection(repayBean));
            }
        }
        //mRefreshHelper.updateData(mData, list.size());
        mRefreshHelper.updateData(mData);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        mRefreshHelper.errorData();
        showProgress(false);
        if (mAdapter.getData().isEmpty()) {
            super.onError(error, url);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SpHelper.getInstance().deleteAllMsgFromSp(SpKey.FILTER_CRITERIA_KEY);
    }
}
