package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/12/1
 * @Version: 1.0
 */
public class FootCardBean implements Serializable {
    private ImageInfoBean image;
    private ActionBean action;

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
