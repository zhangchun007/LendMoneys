package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/10
 * @Version: 1.0
 */
public class TitleBean implements Serializable {
    private String content; //文本内容
    private String defaultShow;//是否展示

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDefaultShow() {
        return defaultShow;
    }

    public void setDefaultShow(String defaultShow) {
        this.defaultShow = defaultShow;
    }
}
