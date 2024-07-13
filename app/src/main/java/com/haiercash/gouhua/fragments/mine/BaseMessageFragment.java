package com.haiercash.gouhua.fragments.mine;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.MessageAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseListFragment;
import com.haiercash.gouhua.beans.MessageCenter;
import com.haiercash.gouhua.beans.MsgList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 还款提醒和申请进度消息父类
 */
public abstract class BaseMessageFragment extends BaseListFragment {
    protected List<MsgList> msgList = new ArrayList<>();
    protected int currentPosition = -1;
    protected String directUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_comm_list;
    }


    @Override
    protected void initEventAndData() {
        super.initEventAndData();
    }


    @Override
    public void loadSourceData(int page, int pageSize) {
        mRefreshHelper.setEmptyData(R.drawable.img_empty_message, getString(R.string.empty_message));
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", AppApplication.userid);
        map.put("tagCode", getTypeCode());
        map.put("page", page + "");
        map.put("pageSize", pageSize + "");
        netHelper.postService(ApiUrl.URL_getMsgCenterList, map, MessageCenter.class);
    }

    @Override
    public MessageAdapter getAdapter() {
        return new MessageAdapter();
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        currentPosition = position;
        MsgList msg = msgList.get(position);
        String flag = msg.getIsRead();
        String messageId = msg.getId();
        if ("0".equals(flag)) {
            //未读 则先标记已读
            showProgress(true);
            HashMap<String, String> map = new HashMap<>();
            map.put("msgId", messageId);
            map.put("userId", AppApplication.userid);
            map.put("tagCode", getTypeCode());
            netHelper.postService(ApiUrl.URL_UPDATE_MSG_STATUS, map);

        } else {
            //已读，点击后跳转
            toActivePage();
        }
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        if (ApiUrl.URL_getMsgCenterList.equals(url)) {
            MessageCenter messageCenter = (MessageCenter) t;
            //额度被拒倒流H5 url
            directUrl = messageCenter.directUrlForCreditRejection;

            //更新系统时间
            if (getAdapter() != null) {
                ((MessageAdapter) mAdapter).setSysTime(messageCenter.sysTime);
            }
            msgList.clear();
            msgList.addAll(messageCenter.getMsgList());
            mRefreshHelper.updateData(msgList);
        } else if (ApiUrl.URL_UPDATE_MSG_STATUS.equals(url)) {
            updateStatus();
        }
    }

    //点击之后更新状态
    private void updateStatus() {
        showProgress(false);
        //更新当前点击位置状态
        msgList.get(currentPosition).setIsRead("1");
        mRefreshHelper.updateData(msgList);
        //标记更新成功后跳转
        toActivePage();

    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.URL_getMsgCenterList.equals(url)) {
            mRefreshHelper.errorData();
            if (mAdapter.getData().isEmpty()) {
                mRefreshHelper.setEmptyData(R.drawable.img_empty_normal, "让网络再飞一会儿~");
            }
        } else {
            super.onError(error, url);
        }
    }

    /**
     * 点击列表跳转
     */
    public abstract void toActivePage();


    /**
     * 当前页面的 typecode
     *
     * @return typecode
     */
    public abstract String getTypeCode();

}
