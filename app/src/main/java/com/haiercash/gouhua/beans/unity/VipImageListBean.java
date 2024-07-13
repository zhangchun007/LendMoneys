package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/12/6
 * @Version: 1.0
 */
public class VipImageListBean implements Serializable {
    private ImageInfoBean image;
    private ActionBean action;
    private HashMap<String, Object> exposure;

    public VipImageListBean() {
    }

    public VipImageListBean(ImageInfoBean image, ActionBean action, HashMap<String, Object> exposure) {
        this.image = image;
        this.action = action;
        this.exposure = exposure;
    }

    public HashMap<String, Object> getExposure() {
        return exposure;
    }

    public void setExposure(HashMap<String, Object> exposure) {
        this.exposure = exposure;
    }

    public ImageInfoBean getImage() {
        return image;
    }

    public void setImage(ImageInfoBean image) {
        this.image = image;
    }

    public ActionBean getAction() {
        return action;
    }

    public void setAction(ActionBean action) {
        this.action = action;
    }
}
