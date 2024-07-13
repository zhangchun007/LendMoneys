package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/28
 * @Version: 1.0
 */
public class UserComponentInfoBean implements Serializable {
    private VipStateBean vipState;
    private List<UserTagBean> userTag;
    private AvatarBean avatar;
    private String name;
    private String mobile;

    public VipStateBean getVipState() {
        return vipState;
    }

    public void setVipState(VipStateBean vipState) {
        this.vipState = vipState;
    }

    public List<UserTagBean> getUserTag() {
        return userTag;
    }

    public void setUserTag(List<UserTagBean> userTag) {
        this.userTag = userTag;
    }

    public AvatarBean getAvatar() {
        return avatar;
    }

    public void setAvatar(AvatarBean avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
