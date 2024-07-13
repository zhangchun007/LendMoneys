package com.haiercash.gouhua.beans.unity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/9
 * @Version: 1.0
 */
public class ComponentBean implements Serializable, MultiItemEntity {

    public static final int USERINFOCOMPONENT = 0;
    public static final int REPAYMENTCOMPONENT = 1;
    public static final int DATABOARDCOMPONENT = 2;
    public static final int BANNERCOMPONENT = 3;
    public static final int MENUCOMPONENT = 4;
    public static final int FOOTVIEWCOMPONENT = 5;
    public static final int BENEFITCOMPONENT = 6;
    public static final int CONSUMERCOMPONENT = 7;
    public static final int CMSBANNERCOMPONENT = 8; //首页cmsbanner
    public static final int SERVICECOMPONENT = 9;
    public static final int NOTICECOMPONENT = 10;//notice
    public String description;//用户信息
    public String type; //类型
    public String defaultShow; //是否展示
    private ComponentInfoBean data;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultShow() {
        return defaultShow;
    }

    public void setDefaultShow(String defaultShow) {
        this.defaultShow = defaultShow;
    }

    public ComponentInfoBean getData() {
        return data;
    }

    public void setData(ComponentInfoBean data) {
        this.data = data;
    }

    @Override
    public int getItemType() {
        switch (type) {
            case "UserInfoComponent":
                return USERINFOCOMPONENT;
            case "RepaymentComponent":
                return REPAYMENTCOMPONENT;
            case "DataBoardComponent":
                return DATABOARDCOMPONENT;
            case "BannerComponent":
                return BANNERCOMPONENT;
            case "MenuComponent":
                return MENUCOMPONENT;
            case "FooterComponent":
                return FOOTVIEWCOMPONENT;
            case "BenefitComponent":
                return BENEFITCOMPONENT;
            case "ConsumerComponent":
                return CONSUMERCOMPONENT;
            case "CMSBannerComponent":
                return CMSBANNERCOMPONENT;
            case "ServiceComponent":
                return SERVICECOMPONENT;
            case "NoticeComponent":
                return NOTICECOMPONENT;
        }
        return 0;
    }
}
