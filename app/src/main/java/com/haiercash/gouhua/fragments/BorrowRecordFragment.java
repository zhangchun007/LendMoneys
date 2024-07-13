package com.haiercash.gouhua.fragments;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.adaptor.RecordAdapter;
import com.haiercash.gouhua.adaptor.bean.BorrowRecordSection;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseListFragment;
import com.haiercash.gouhua.beans.borrowmoney.BorrowRecordBen;
import com.haiercash.gouhua.beans.borrowmoney.RecordBean;
import com.haiercash.gouhua.bill.BillDetailsActivity;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.SpHp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/1/10<br/>
 * 描    述：借款记录<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.ORDER_RECORD_LIST)
public class BorrowRecordFragment extends BaseListFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_comm_list;
    }

    @Override
    protected void initEventAndData() {
        super.initEventAndData();
        //mSwipeRefreshLayout.setBackgroundResource(R.color.windowBackground);
        showProgress(true);
        mRefreshHelper.build(true, false).setEmptyData(R.drawable.img_empty_bill, getString(R.string.empty_record));
        mRefreshHelper.setEmptyViewBgResource(R.color.windowBackground);
        setRefreshViewBg(R.color.windowBackground);
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new RecordAdapter();
    }

    @Override
    public void onItemClick(Object item) {
        BorrowRecordSection section = (BorrowRecordSection) item;
        if (section.isHeader) {
            return;
        }
        if ("0".equals(section.t.getBorrowingApplStatus())) {
            Intent intent = new Intent(getActivity(), BillDetailsActivity.class);
            intent.putExtra("applSeq", section.t.getApplSeq());
            startActivity(intent);
        } else {
            Bundle extra = new Bundle();
            extra.putInt("PageType", BorrowRepayDetailFragment.BORROW_DETAIL);
            extra.putString("applySeq", CheckUtil.formattedAmount(section.t.getApplSeq()));
            ContainerActivity.to(getActivity(), BorrowRepayDetailFragment.ID, extra);
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
        map.put("idNo", EncryptUtil.simpleEncrypt(idNo));
        map.put("page", String.valueOf(page));//页数
        map.put("pageNum", String.valueOf(pageSize));//每页显示条数
        netHelper.getService(ApiUrl.URL_ALL_BORROWING_RECORD_GROUP, map);
    }

    @Override
    public void onSuccess(Object response, String flag) {
        showProgress(false);
        List<BorrowRecordBen> list = JsonUtils.fromJsonArray(response, BorrowRecordBen.class);
        List<BorrowRecordSection> mData = new ArrayList<>();
        for (BorrowRecordBen ben : list) {
            mData.add(new BorrowRecordSection(true, ben.getApplyMonth()));
            for (RecordBean recordBean : ben.getApplyList()) {
                mData.add(new BorrowRecordSection(recordBean));
            }
        }
        mRefreshHelper.updateData(mData, list.size());
    }

    @Override
    public void onError(BasicResponse error, String url) {
        mRefreshHelper.errorData();
        super.onError(error, url);
    }
}
