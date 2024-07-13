package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/12/17
 * @Version: 1.0
 */
public class CmsBean implements Serializable {
    private List<CmsInfo> data;
    private String title;
    private String isShow;


    public List<CmsInfo> getData() {
        return data;
    }

    public void setData(List<CmsInfo> data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }
}
