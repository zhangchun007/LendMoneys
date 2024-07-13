package com.haiercash.gouhua.beans.risk;

import java.io.Serializable;

public class GeoBean implements Serializable {
    public String latitude;   //	纬度	纬度
    public String longitude;  //	经度	经度
    public String city_name;  //	市名称	市名称
    public String province_name;  //	省名称	省名称
    public String adcode; //
    public String address;    //	解析地址	解析地址
    public String country;    //	国家	国家
    public String district;   //	区	区
    public String street; //	街道	街道
    public String street_number;  //	街道号	街道号
    public String town;   //	县	县
    public String provinceId;  //省的id  提供给H5
    public String cityId;
    public String areaId;
    public String errMsg; //提供给H5的错误信息
    public String permissionState; //权限状态  0：无权限  1：有权限


}
