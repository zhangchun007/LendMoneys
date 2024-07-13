package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/10
 * @Version: 1.0
 */
public class ShowMoreBean implements Serializable {

    private String   content;//文本内容
    private ActionBean   action; //跳转类型
    private String   jumpUrl;//跳转路径
    private String   defaultShow;//是否展示

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ActionBean getAction() {
        return action;
    }

    public void setAction(ActionBean action) {
        this.action = action;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getDefaultShow() {
        return defaultShow;
    }

    public void setDefaultShow(String defaultShow) {
        this.defaultShow = defaultShow;
    }
}
