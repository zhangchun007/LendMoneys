package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/28
 * @Version: 1.0
 */
public class VipTipsBean implements Serializable {
    private String tips;
    private ImageInfoBean vipTipsImage;
    private String defaultShow;

    public String getDefaultShow() {
        return defaultShow;
    }

    public void setDefaultShow(String defaultShow) {
        this.defaultShow = defaultShow;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public ImageInfoBean getVipTipsImage() {
        return vipTipsImage;
    }

    public void setVipTipsImage(ImageInfoBean vipTipsImage) {
        this.vipTipsImage = vipTipsImage;
    }
}
