package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;

/**
 * @Description: Menu组件
 * @Author: zhangchun
 * @CreateDate: 2023/11/9
 * @Version: 1.0
 */
public class MenusBean implements Serializable {
    private String icon; //图片地址
    private String content;//文本
    private String label;//文本
    private PopBean pop; //气泡
    private ActionBean action; //跳转动作

    private ImageInfoBean image;
    private String cid; // 埋点使用
    private String customerGroupId;//客群ID
    private String needLogin;// 是否需要登录

    public ImageInfoBean getImage() {
        return image;
    }

    public void setImage(ImageInfoBean image) {
        this.image = image;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCustomerGroupId() {
        return customerGroupId;
    }

    public void setCustomerGroupId(String customerGroupId) {
        this.customerGroupId = customerGroupId;
    }

    public String getNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(String needLogin) {
        this.needLogin = needLogin;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public PopBean getPop() {
        return pop;
    }

    public void setPop(PopBean pop) {
        this.pop = pop;
    }

    public ActionBean getAction() {
        return action;
    }

    public void setAction(ActionBean action) {
        this.action = action;
    }
}
