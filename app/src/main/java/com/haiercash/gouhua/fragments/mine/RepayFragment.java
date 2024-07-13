package com.haiercash.gouhua.fragments.mine;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.MsgList;
import com.haiercash.gouhua.hybrid.H5LinkJumpHelper;

/**
 * @author zhangjie
 * @date 2017/7/18
 * 消息中心--还款提醒列表
 */

public class RepayFragment extends BaseMessageFragment {

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

        H5LinkJumpHelper.INSTANCE().goH5RepayPage(mActivity);

        //跳转7日待还

    }

    @Override
    public String getTypeCode() {
        return "03";
    }
}
