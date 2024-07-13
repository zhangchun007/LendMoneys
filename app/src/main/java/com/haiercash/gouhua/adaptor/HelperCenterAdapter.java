package com.haiercash.gouhua.adaptor;

import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.beans.help.HelpCenterBean;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/1/16<br/>
 * 描    述：帮助中心<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class HelperCenterAdapter extends BaseAdapter<HelpCenterBean, ViewHolder> {

    public HelperCenterAdapter() {
        super(R.layout.a_helpcenter_item);
    }

    @Override
    protected void convert(ViewHolder holder, HelpCenterBean item) {
       // String index = item.getRownum() <= 9 ? "0" + item.getRownum() : item.getRownum() + "";
       // holder.setText(R.id.tv_index, index);
        holder.setText(R.id.tv_title, item.getProblemTitle());
        //holder.setText(R.id.text1, String.format("%s. %s", item.rownum, item.getHelpTitle()));
    }
}