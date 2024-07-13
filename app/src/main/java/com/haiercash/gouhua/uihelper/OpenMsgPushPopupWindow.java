package com.haiercash.gouhua.uihelper;

import android.view.Gravity;
import android.view.View;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.utils.NotificationsUtils;

import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/8/23<br/>
 * 描    述：开启通知权限<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class OpenMsgPushPopupWindow extends BasePopupWindow {

    public OpenMsgPushPopupWindow(BaseActivity context, Object data) {
        super(context, data);
    }

    @Override
    protected int getLayout() {
        return R.layout.open_msg_push_dialog;
    }

    @Override
    protected void onViewCreated(Object data) {
        setPopupOutsideTouchable(false);
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    @OnClick({R.id.tv_open, R.id.tv_cancle})
    public void onViewClicked(View view) {
        dismiss();
        if (view.getId() == R.id.tv_cancle) {
            NotificationsUtils.saveTime();
        } else if (view.getId() == R.id.tv_open) {
            NotificationsUtils.toSetting(mActivity);
            NotificationsUtils.saveTime();
        }
    }

    /**
     * 通知权限
     */
    public static void showNoticePermission(BaseActivity activity, View view) {
        if (!NotificationsUtils.isNotificationsEnabled(activity) && NotificationsUtils.isTimeRemind()) {
            OpenMsgPushPopupWindow openMsgPushDialog = new OpenMsgPushPopupWindow(activity, null);
            openMsgPushDialog.showAtLocation(view);
        }
    }
}
