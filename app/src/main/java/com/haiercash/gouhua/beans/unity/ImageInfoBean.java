package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/22
 * @Version: 1.0
 */
public class ImageInfoBean implements Serializable {
    private String imageUrl;
    private String normalImageUrl;
    private String imgWidth;
    private String imgHeight;
    private float imgRatio;
    private String tagType;
    private ShowConditionBean showCondition;

    public ShowConditionBean getShowCondition() {
        return showCondition;
    }

    public void setShowCondition(ShowConditionBean showCondition) {
        this.showCondition = showCondition;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getNormalImageUrl() {
        return normalImageUrl;
    }

    public void setNormalImageUrl(String normalImageUrl) {
        this.normalImageUrl = normalImageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(String imgWidth) {
        this.imgWidth = imgWidth;
    }

    public String getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(String imgHeight) {
        this.imgHeight = imgHeight;
    }

    public float getImgRatio() {
        return imgRatio;
    }

    public void setImgRatio(float imgRatio) {
        this.imgRatio = imgRatio;
    }
}
