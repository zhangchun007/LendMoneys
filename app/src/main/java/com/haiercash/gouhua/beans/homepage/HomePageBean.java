package com.haiercash.gouhua.beans.homepage;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 够花4.0新版首页返回，为list<HomePageBean>
 */
public class HomePageBean implements MultiItemEntity {
    public static final int DEFAULT = -1;
    public static final int THEME = 1;
    public static final int NOTICE = 2;
    public static final int BANNER = 3;
    public static final int VIP_BANNER = 4;
    public static final int GOODS = 5;
    public static final int LOAN_PRODUCT = 6;
    public static final int BOTTOM = 7;

    private String type;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HomePageBean(String type, String title, Object data) {
        this.type = type;
        this.title = title;
        this.data = data;
    }

    private Object data;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    @Override
    public int getItemType() {
        int currentType;
        if ("theme".equals(type)) {
            currentType = THEME;
        } else if ("banner".equals(type)) {
            currentType = BANNER;
        } else if ("midRes".equals(type)) {//vip资源位
            currentType = VIP_BANNER;
        } else if ("loan_product".equals(type)) {
            currentType = LOAN_PRODUCT;
        } else if ("bottom".equals(type)) {
            currentType = BOTTOM;
        } else if ("notice".equals(type)) {
            currentType = NOTICE;
        } else if ("goods".equals(type)) {
            currentType = GOODS;
        } else {
            currentType = DEFAULT;
        }
        return currentType;
    }
}
