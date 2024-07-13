package com.haiercash.gouhua.adaptor.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class LoanMarketBean implements Parcelable {
    public String organizationName;
    public String creditAmount;
    public String organizationIcon;
    public String yearInterest;
    public String applyUrl;
    public String specialParam;
    public SpecialData specialData;

    protected LoanMarketBean(Parcel in) {
        organizationName = in.readString();
        creditAmount = in.readString();
        organizationIcon = in.readString();
        yearInterest = in.readString();
        applyUrl = in.readString();
        specialParam = in.readString();
        specialData = in.readParcelable(SpecialData.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(organizationName);
        dest.writeString(creditAmount);
        dest.writeString(organizationIcon);
        dest.writeString(yearInterest);
        dest.writeString(applyUrl);
        dest.writeString(specialParam);
        dest.writeParcelable(specialData, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LoanMarketBean> CREATOR = new Creator<LoanMarketBean>() {
        @Override
        public LoanMarketBean createFromParcel(Parcel in) {
            return new LoanMarketBean(in);
        }

        @Override
        public LoanMarketBean[] newArray(int size) {
            return new LoanMarketBean[size];
        }
    };
}
