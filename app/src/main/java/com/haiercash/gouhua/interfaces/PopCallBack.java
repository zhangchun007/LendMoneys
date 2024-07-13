package com.haiercash.gouhua.interfaces;

import com.haiercash.gouhua.beans.PopDialogBean;

/**
 * 弹窗接口回调接口
 */
public interface PopCallBack {
    void clickNode(PopDialogBean popDialogBean, boolean isClose);

    void postRecord(String popupId);

    void clickJump(String routeType, String jumpUrl, String popupId);
}
