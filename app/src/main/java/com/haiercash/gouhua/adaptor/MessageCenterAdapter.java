package com.haiercash.gouhua.adaptor;

import android.widget.ImageView;
import android.widget.TextView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.homepage.HomeNoticeBean;
import com.haiercash.gouhua.beans.msg.MessageInfo;
import com.haiercash.gouhua.beans.msg.MessageInfoNew;
import com.haiercash.gouhua.utils.GlideUtils;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/7/6
 * 描    述：消息中心的Item
 * 修订历史：
 * ================================================================
 */
public class MessageCenterAdapter<T> extends BaseAdapter<T, ViewHolder> implements LoadMoreModule {
    //是否一键已读
    private boolean isOneKeyClearMessage;

    public MessageCenterAdapter() {
        super(R.layout.item_message);
    }

    @Override
    protected void convert(ViewHolder holder, T item) {
        TextView tvTip = holder.getView(R.id.tv_tip);
        tvTip.getPaint().setFakeBoldText(true);
        MessageInfoNew info = (MessageInfoNew) item;
        holder.setText(R.id.tv_tip, info.getPushTitle());
        holder.setGone(R.id.tv_content, CheckUtil.isEmpty(info.getPushSubTitle())).setText(R.id.tv_content, info.getPushSubTitle());
        holder.setGone(R.id.message_read, ((MessageInfoNew) item).getReadStatus().equals("Y"));
        //一键已读
        if (isOneKeyClearMessage) {
            holder.setGone(R.id.message_read, true);
        }
        holder.setText(R.id.tv_time, info.getCreateTimeStr());
    }

    public void setOneKeyClear(boolean flag) {
        this.isOneKeyClearMessage = flag;
    }
}
