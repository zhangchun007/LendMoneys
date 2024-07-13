package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/28
 * @Version: 1.0
 */
public class UserTagBean implements Serializable {
    private String tagType;
    private String defaultShow;
    private List<VipImageListBean> imageList;

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getDefaultShow() {
        return defaultShow;
    }

    public void setDefaultShow(String defaultShow) {
        this.defaultShow = defaultShow;
    }

    public List<VipImageListBean> getImageList() {
        return imageList;
    }

    public void setImageList(List<VipImageListBean> imageList) {
        this.imageList = imageList;
    }
}
