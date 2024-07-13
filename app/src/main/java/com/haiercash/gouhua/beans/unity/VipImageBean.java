package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/12/5
 * @Version: 1.0
 */
public class VipImageBean implements Serializable {
    private String defaultShow;
    private List<VipImageListBean> imageList;

    public String getDefaultShow() {
        return defaultShow;
    }

    public void setDefaultShow(String defaultShow) {
        this.defaultShow = defaultShow;
    }

    public List<VipImageListBean> getVipImageList() {
        return imageList;
    }

    public void setVipImageList(List<VipImageListBean> vipImageList) {
        this.imageList = vipImageList;
    }
}
