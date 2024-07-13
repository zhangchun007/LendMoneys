package com.haiercash.gouhua.fragments.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.fragment.app.Fragment;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.MessageCenterAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseListFragment;
import com.haiercash.gouhua.beans.msg.MessageBeanNew;
import com.haiercash.gouhua.beans.msg.MessageInfoNew;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/6/5<br/>
 * 描    述：我的消息列表：入参：msgTitle->标题；msgType->消息类型<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class MessageListFragment extends BaseListFragment {
    private String msgType;

    // 两次点击间隔不能少于800ms
    private static final int FAST_CLICK_DELAY_TIME = 800;
    private static long lastClickTime;

    public static MessageListFragment newInstance(Bundle extra) {
        final MessageListFragment f = new MessageListFragment();
        if (extra != null) {
            f.setArguments(extra);
        }
        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_comm_list;
    }

    @Override
    protected void initEventAndData() {
        if (getArguments() == null) {
            UiUtil.toast("未找到您要的界面");
            mActivity.finish();
            return;
        }
        msgType = getArguments().getString("msgType");
        super.initEventAndData();
        mActivity.setTitle(getArguments().getString("msgTitle"));
        startRefresh(true, true);

        //一键已读设置红点全部消失
        RxBus.getInstance().addSubscription(this, RxBus.getInstance().register(ActionEvent.class, (actionEvent -> {
            if (actionEvent.getActionType() == ActionEvent.ONE_KEY_CLEAR_MESSAGE) {
                if (mAdapter != null) {
                    ((MessageCenterAdapter) mAdapter).setOneKeyClear(true);
                    mAdapter.notifyDataSetChanged();
                }
            }
        })));
    }

    /**
     * 防止重复点击
     * @return
     */
    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    @Override
    public MessageCenterAdapter getAdapter() {
        return new MessageCenterAdapter();
    }

    @Override
    public void onItemClick(Object item) {
        if (isFastClick()) return;
        MessageInfoNew info = (MessageInfoNew) item;
        if (info == null) return;
        postClickEvent(info);
        Fragment fragment = getParentFragment();
        if (fragment != null && fragment instanceof MessageFragment) {
            //消息已读取
            ((MessageFragment) fragment).makeMessageRead(info.getId(), "N");
            info.setReadStatus("Y");
            if (mAdapter != null) {
                mAdapter.notifyItemChanged(mAdapter.getItemPosition(info));
            }
        }
        if ("CustomContent".equals(info.getPushContentType())) {//自定义内容
            getMessageDetail(info.getId(), TokenHelper.getInstance().getH5ProcessId());
        } else if ("H5Link".equals(info.getPushContentType())) {//H5链接跳转
            if (CheckUtil.isEmpty(info.getH5Url())) {
                return;
            }
            Intent intent = new Intent();
            intent.setClass(mActivity, JsWebBaseActivity.class);
            intent.putExtra("jumpKey", info.getH5Url());
            mActivity.startActivity(intent);
        } else if ("EduApply".equals(info.getPushContentType()) || "LoanApply".equals(info.getPushContentType()) || "RepayBill".equals(info.getPushContentType())) {//自定义内容
            getMessageDetail(info.getId(), TokenHelper.getInstance().getH5ProcessId());
        } else if ("HomePage".equals(info.getPushContentType())) {//回到首页
            getActivity().finish();
            RxBus.getInstance().post(new ActionEvent(ActionEvent.SELECT_MAIN_TAB));
        }
    }

    private void postClickEvent(MessageInfoNew info) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "消息中心");
        if ("NOTICE".equals(msgType)) {//通知
            map.put("page_tab", "通知");
        } else if ("ACTIVITY".equals(msgType)) {//活动
            map.put("page_tab", "活动");
        }
        map.put("msg_ids", info.getId());
        map.put("msg_title", info.getPushTitle());
        UMengUtil.onEventObject("GH_Message_Center_Item_Click", map, getPageCode());
    }

    /**
     * 获取消息详情
     */
    private void getMessageDetail(String inmailId, String processId) {
        mActivity.showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("inmailId", inmailId);
        map.put("processId", processId);
        map.put("deviceId", SystemUtils.getDeviceID(mActivity));
        netHelper.postService(ApiUrl.MESSAGE_DETAIL_INFO, map, MessageInfoNew.class);

    }

    @Override
    public void loadSourceData(int page, int pageSize) {
        ((MessageCenterAdapter) mAdapter).setOneKeyClear(false);
        //StaticMsg 更改为SpKey
        Map<String, Object> map = new HashMap<>();
        if ("NOTICE".equals(msgType)) {//通知
            map.put("inmailType", "NOTICE");
        } else if ("ACTIVITY".equals(msgType)) {//活动
             map.put("inmailType", "ACTIVITY");
        }
        map.put("page", String.valueOf(page));//页数
        map.put("pageSize", "10");//每页显示条S数
        netHelper.postService(ApiUrl.HOME_NOTICE_LIST_NEW, map, MessageBeanNew.class);

    }

    @Override
    public void onSuccess(Object response, String flag) {
        mActivity.showProgress(false);
        if (ApiUrl.HOME_NOTICE_LIST_NEW.equals(flag)) { //消息列表
            MessageBeanNew data = (MessageBeanNew) response;
            if ("NOTICE".equals(msgType) && data.getRecords().size() == 0) {//通知
                mRefreshHelper.setEmptyData(R.drawable.img_empty_message, "暂无通知消息");
            } else if ("ACTIVITY".equals(msgType) && data.getRecords().size() == 0) {//活动
                mRefreshHelper.setEmptyData(R.drawable.img_empty_message, "暂无活动消息");
            }
            mRefreshHelper.setPAGE_SIZE(10);
            mRefreshHelper.updateData(data.getRecords());
            HashMap<String, Object> map = new HashMap<>();
            map.put("page_name_cn", "消息中心");
            if ("NOTICE".equals(msgType)) {//通知
                map.put("page_tab", "通知");
            } else if ("ACTIVITY".equals(msgType)) {//活动
                map.put("page_tab", "活动");
            }
            if (data != null && data.getRecords() != null && data.getRecords().size() > 0) {
                String ids = "";
                for (MessageInfoNew infoNew : data.getRecords()) {
                    if (CheckUtil.isEmpty(ids)) {
                        ids += infoNew.getId();
                    } else {
                        ids = ids + "," + infoNew.getId();
                    }
                    map.put("msg_ids", ids);
                }
            }
            UMengUtil.onEventObject("GH_Message_Center_Exposure", map, getPageCode());
        } else if (ApiUrl.MESSAGE_DETAIL_INFO.equals(flag)) { //消息详情
            MessageInfoNew data = (MessageInfoNew) response;
            if ("CustomContent".equals(data.getPushContentType())) {
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_NOTICE)
                        .put("noticeTitle", data.getPushTitle())
                        .put("pushSubTitle", data.getPushSubTitle())
                        .put("noticeTime", data.getCreateTimeStr())
                        .put("noticeContent", data.getContent())
                        .navigation();
            } else if ("HomePage".equals(data.getPushContentType())) {
                getActivity().finish();
                RxBus.getInstance().post(new ActionEvent(ActionEvent.SELECT_MAIN_TAB));
            } else {
                String url = data.getJumpUrl();
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                if (url.contains("gouhua://")) {
                    url = url.substring(url.indexOf("gouhua://"));
                    ActivityUntil.startOtherApp(mActivity, url);
                } else if (URLUtil.isNetworkUrl(url)) {
                    if (CheckUtil.isEmpty(url)) {
                        return;
                    }

                    Intent intent = new Intent();
                    intent.setClass(mActivity, JsWebBaseActivity.class);
                    intent.putExtra("jumpKey", url);
                    mActivity.startActivity(intent);
                } else {
                    ActivityUntil.startOtherApp(mActivity, url);
                }
            }

        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        if (ApiUrl.HOME_NOTICE_LIST_NEW.equals(url)) {
            mRefreshHelper.errorData();
            if (mAdapter.getData().isEmpty()) {
                mRefreshHelper.setEmptyData(R.drawable.img_empty_normal, "让网络再飞一会儿~");
            }
        }

    }

    @Override
    protected String getPageCode() {
        return "Gh_Message_Center";
    }

    @Override
    public void onDestroyView() {
        RxBus.getInstance().unSubscribe(this);
        super.onDestroyView();
    }
}
