package com.haiercash.gouhua.activity.personalinfor;

import com.app.haiercash.base.bean.CollectInfo;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;

/**
 * Author: Sun<br/>
 * Date :    2019/4/19<br/>
 * FileName: AnalysisAdapter<br/>
 * Description:<br/>
 */
public class AnalysisAdapter extends BaseAdapter<CollectInfo, ViewHolder> {

    AnalysisAdapter() {
        super(R.layout.activity_analysis_item);
    }

    @Override
    protected void convert(ViewHolder helper, CollectInfo item) {
        helper.setText(R.id.tv_time, TimeUtil.longToString(item.time));
        helper.setText(R.id.tv_event, item.event);
    }
}
