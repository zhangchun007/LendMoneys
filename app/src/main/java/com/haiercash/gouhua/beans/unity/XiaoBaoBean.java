package com.haiercash.gouhua.beans.unity;


import com.haiercash.gouhua.beans.homepage.ConfigData;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/12/17
 * @Version: 1.0
 */
public class XiaoBaoBean implements Serializable {
    private List<ConfigData>recode;
    private List<ConfigData>data;
    private String title;
    private String forwardUrl;

    public List<ConfigData> getRecode() {
        return recode;
    }

    public void setRecode(List<ConfigData> recode) {
        this.recode = recode;
    }

    public List<ConfigData> getData() {
        return data;
    }

    public void setData(List<ConfigData> data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getForwardUrl() {
        return forwardUrl;
    }

    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }
}
