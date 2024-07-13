package com.haiercash.gouhua.beans.location;


import java.io.Serializable;
import java.util.List;

/**
 * 定位后调接口获取省市区code的bean
 */
public class LocationCodeBean implements Serializable {
    private List<AreaCode> areaCodeList;

    public void setAreaCodeList(List<AreaCode> areaCodeList) {
        this.areaCodeList = areaCodeList;
    }

    public List<AreaCode> getAreaCodeList() {
        return areaCodeList;
    }
}
