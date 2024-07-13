package com.haiercash.gouhua.adaptor;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.bumptech.glide.Glide;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.MsgList;
import com.haiercash.gouhua.view.OvalImageView;

/**
 * 消息中心列表还款提醒和申请进度消息适配器
 */

public class MessageAdapter extends BaseAdapter<MsgList, MessageAdapter.MessageHolder> {


    /**
     * 保存系统时间
     */
    private String sysTime;

    public void setSysTime(String time) {
        sysTime = time;
    }

    public MessageAdapter() {
        super(R.layout.item_message_new);
    }

    @Override
    protected void convert(MessageHolder messageHolder, MsgList msgList) {

        String isTip = msgList.getIsTip();
        //设置时间
        int start = msgList.insertTime.indexOf(sysTime);
        if (-1 != start) {
            //今天 16:16
            start = start + start + sysTime.length();
            //去掉秒
            int end = msgList.insertTime.length() - 3;
            messageHolder.tvTime.setText(String.format(getContext().getString(R.string.today), msgList.insertTime.substring(start, end)));
        } else if (sysTime.length() >= 10) {
            //非当天仅显示年月日 2020-01-01
            messageHolder.tvTime.setText(msgList.insertTime.substring(0, 10));
        } else {
            messageHolder.tvTime.setText(msgList.insertTime);
        }
        //设置图片
        if (!CheckUtil.isEmpty(msgList.picPath) && "02".equals(msgList.activityTypeTag)) {
            messageHolder.ivBack.setVisibility(View.VISIBLE);
            //设置图片圆角
            messageHolder.ivBack.setRoundingRadius(5, 5, 0, 0);
            Glide.with(getContext())
                    .load(BuildConfig.API_SERVER_URL + msgList.picPath)
                    .placeholder(R.drawable.ic_gouhua_login)
                    .error(R.drawable.ic_gouhua_login)
                    .into(messageHolder.ivBack);
        } else {
            messageHolder.ivBack.setVisibility(View.GONE);
        }
        //设置标题
        messageHolder.tvTitle.setText(msgList.getTitle());
        //设置内容
        messageHolder.tvContent.setText(msgList.getMessage());
        //消息已读状态
        String flag = msgList.getIsRead();

        messageHolder.tvTipShade.setVisibility(View.GONE);
        if (!"0".equals(flag)) {
            //已读 整个卡片透明度 50%（除图片）
            messageHolder.llMessageItem.setAlpha(0.5f);
            if ("Y".equals(isTip)) {
                messageHolder.tvTipShade.setVisibility(View.VISIBLE);
            }
        } else {
            //未读
            messageHolder.llMessageItem.setAlpha(1);
        }

        //是否显示角标
        if ("Y".equals(isTip)) {
            messageHolder.hsaTip.setVisibility(View.VISIBLE);
        } else {
            messageHolder.hsaTip.setVisibility(View.INVISIBLE);
        }

        //是否显示更多
        if (!"NOJUMP".equals(msgList.jumpType)) {
            messageHolder.hasMore.setVisibility(View.VISIBLE);
        } else {
            messageHolder.hasMore.setVisibility(View.GONE);
        }
    }

    public static final class MessageHolder extends ViewHolder {

        public TextView tvTime;
        public TextView tvTitle;
        public TextView tvContent;
        public OvalImageView ivBack;
        public LinearLayout hasMore;
        public TextView hsaTip;
        public LinearLayout llMessageItem;
        public TextView tvTipShade;

        public MessageHolder(View v) {
            super(v);
            tvTime = v.findViewById(R.id.tv_message_time);
            tvContent = v.findViewById(R.id.tv_message_content);
            ivBack = v.findViewById(R.id.iv_message_introduce);
            tvTitle = v.findViewById(R.id.tv_message_title);
            hsaTip = v.findViewById(R.id.tv_message_tip);
            hasMore = v.findViewById(R.id.ll_has_more);
            llMessageItem = v.findViewById(R.id.ll_message_item);
            tvTipShade = v.findViewById(R.id.tv_tips_shade);
        }
    }

}
