package com.app.haiercash.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com<br/>
 * 版    本：1.0<br/>
 * 创建日期：2021/2/19<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class AreaListBean implements Parcelable {
    private List<ArrayBean> areaCodeList;    //省市区编码列表
    private String maxVersion;              //最新数据版本号

    protected AreaListBean(Parcel in) {
        areaCodeList = in.createTypedArrayList(ArrayBean.CREATOR);
        maxVersion = in.readString();
    }

    public static final Creator<AreaListBean> CREATOR = new Creator<AreaListBean>() {
        @Override
        public AreaListBean createFromParcel(Parcel in) {
            return new AreaListBean(in);
        }

        @Override
        public AreaListBean[] newArray(int size) {
            return new AreaListBean[size];
        }
    };

    public List<ArrayBean> getAreaCodeList() {
        return areaCodeList;
    }

    public void setAreaCodeList(List<ArrayBean> areaCodeList) {
        this.areaCodeList = areaCodeList;
    }

    public String getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(String maxVersion) {
        this.maxVersion = maxVersion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(areaCodeList);
        dest.writeString(maxVersion);
    }
}
