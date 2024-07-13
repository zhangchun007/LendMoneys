package com.haiercash.gouhua.fragments.mine;


import android.content.Intent;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.beans.MsgList;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;

/**
 * *@Author:    Sun
 * *@Date  :    2020/9/1
 * *@FileName: ProgressFragment
 * *@Description: 消息中心--申请进度列表
 */
public class ProgressFragment extends BaseMessageFragment {

    @Override
    protected void initEventAndData() {
        super.initEventAndData();
        startRefresh(true, R.drawable.img_empty_message, getString(R.string.empty_message));
    }

    @Override
    public void toActivePage() {
        MsgList msg = msgList.get(currentPosition);
        //NOJUMP 不做处理
        if ("NOJUMP".equals(msg.jumpKey)) {
            return;
        }
        //额度
        if ("02".equals(msg.msgTyp)) {
            limitProgress(msg);
        } else {
            //贷款
            loanProgress(msg);
        }
    }


    /**
     * 贷款申请处理
     *
     * @param msg msg
     */
    private void loanProgress(MsgList msg) {
        String url = msg.getDirectUrl();
        if (!CheckUtil.isEmpty(url)) {
            Intent intent = new Intent(mActivity, JsWebBaseActivity.class);
            intent.putExtra("jumpKey", url);
            mActivity.startActivity(intent);

        }
    }

    /**
     * 额度申请点击处理
     *
     * @param msg msg
     */
    private void limitProgress(MsgList msg) {

        //额度已通过，不做处理
        if ("27".equals(msg.outSts)) {
            return;
        }

        //额度被拒绝，跳转H5
        if ("25".equals(msg.outSts) && !CheckUtil.isEmpty(directUrl)) {
            Intent intent = new Intent(mActivity, JsWebBaseActivity.class);
            intent.putExtra("jumpKey", directUrl);
            intent.putExtra("jumpType", msg.jumpType);
            mActivity.startActivity(intent);
        } else {
            //回到首页
            mActivity.finish();
        }
    }

    @Override
    public String getTypeCode() {
        return "04";
    }


//    @Override
//    public <T> void onSuccess(T t, String url) {
//        showProgress(false);
//        if (ApiUrl.URL_getMsgCenterList.equals(url)) {
//            super.onSuccess(t, url);
//        }
//    }

}
