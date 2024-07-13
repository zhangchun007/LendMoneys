package com.haiercash.gouhua.adaptor.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.haiercash.gouhua.beans.login.QueryAgreementListBean;

import java.util.List;

public class SpecialData implements Parcelable {
    public String gzxIcon;
    public String gzxUrl;
    public List<QueryAgreementListBean> agreementList;

    protected SpecialData(Parcel in) {
        gzxIcon = in.readString();
        gzxUrl = in.readString();
        agreementList = in.readParcelable(QueryAgreementListBean.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gzxIcon);
        dest.writeString(gzxUrl);
        dest.writeList(agreementList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SpecialData> CREATOR = new Creator<SpecialData>() {
        @Override
        public SpecialData createFromParcel(Parcel in) {
            return new SpecialData(in);
        }

        @Override
        public SpecialData[] newArray(int size) {
            return new SpecialData[size];
        }
    };
}
