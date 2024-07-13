package com.haiercash.gouhua.beans.msg;

import java.util.List;

/**
 * ================================================================
 * 作    者：yuelibiao
 * 邮    箱：yuelibiao@haiercash.com
 * 版    本：1.0
 * 创建日期：2017/7/6
 * 描    述：消息中心
 * 修订历史：
 * ================================================================
 */
public class MessageBean {
    private String title;
    private String unreadNm;
    private List<MessageInfo> details;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUnreadNm() {
        return unreadNm;
    }

    public void setUnreadNm(String unreadNm) {
        this.unreadNm = unreadNm;
    }

    public List<MessageInfo> getDetails() {
        return details;
    }

    public void setDetails(List<MessageInfo> details) {
        this.details = details;
    }
}
