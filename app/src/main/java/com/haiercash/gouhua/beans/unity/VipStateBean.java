package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/28
 * @Version: 1.0
 */
public class VipStateBean implements Serializable {
    private String defaultShow;
    private VipTipsBean vipTips;
    private VipImageBean vipImage;
    private List<ImageInfoBean> vipBgImage;

    public String getDefaultShow() {
        return defaultShow;
    }

    public void setDefaultShow(String defaultShow) {
        this.defaultShow = defaultShow;
    }


    public VipTipsBean getVipTips() {
        return vipTips;
    }

    public void setVipTips(VipTipsBean vipTips) {
        this.vipTips = vipTips;
    }

    public VipImageBean getVipImage() {
        return vipImage;
    }

    public void setVipImage(VipImageBean vipImage) {
        this.vipImage = vipImage;
    }

    public List<ImageInfoBean> getVipBgImage() {
        return vipBgImage;
    }

    public void setVipBgImage(List<ImageInfoBean> vipBgImage) {
        this.vipBgImage = vipBgImage;
    }
}
