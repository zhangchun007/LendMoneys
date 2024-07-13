package com.haiercash.gouhua.activity.personalinfor;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.app.haiercash.base.db.DbUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseListActivity;

/**
 * Author: Sun<br/>
 * Date :    2019/4/19<br/>
 * FileName: AnalysisActivity<br/>
 * Description:<br/>
 */
public class AnalysisActivity extends BaseListActivity {

    @Override
    public BaseQuickAdapter getAdapter() {
        return new AnalysisAdapter();
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        //noinspection unchecked
        mAdapter.setNewData(DbUtils.getAppDatabase().collectInfoDao().getAllCollect());
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_comm_list;
    }

}
