package com.haiercash.gouhua.beans;

import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 3/20/23
 * @Version: 1.0
 */
public class PersonVipInfo {
    private String title;
    //待展示数据
    private List<PersonVipListInfo> data;
    //01 轮播
    //02 左1右上2右下3
    private String typesettingType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PersonVipListInfo> getData() {
        return data;
    }

    public void setData(List<PersonVipListInfo> data) {
        this.data = data;
    }

    public String getTypesettingType() {
        return typesettingType;
    }

    public void setTypesettingType(String typesettingType) {
        this.typesettingType = typesettingType;
    }
}
