package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/9
 * @Version: 1.0
 */
public class SourceDataBean implements Serializable {
    private UserComponentInfoBean userInfo; //用户信息
    private RepayCardBean repayCard;//还款卡片
    private List<MenusBean> menus;
    private FootCardBean footInfo;

    public FootCardBean getFootInfo() {
        return footInfo;
    }

    public void setFootInfo(FootCardBean footInfo) {
        this.footInfo = footInfo;
    }

    public List<MenusBean> getMenus() {
        return menus;
    }

    public void setMenus(List<MenusBean> menus) {
        this.menus = menus;
    }

    public UserComponentInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserComponentInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public RepayCardBean getRepayCard() {
        return repayCard;
    }

    public void setRepayCard(RepayCardBean repayCard) {
        this.repayCard = repayCard;
    }
}
