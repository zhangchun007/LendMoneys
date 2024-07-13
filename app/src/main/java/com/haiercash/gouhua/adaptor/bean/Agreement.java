package com.haiercash.gouhua.adaptor.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Agreement implements Parcelable {
    public String contCode;
    public String contName;
    public String contType;
    public String contUrl;

    protected Agreement(Parcel in) {
        contCode = in.readString();
        contName = in.readString();
        contType = in.readString();
        contUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contCode);
        dest.writeString(contName);
        dest.writeString(contType);
        dest.writeString(contUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Agreement> CREATOR = new Creator<Agreement>() {
        @Override
        public Agreement createFromParcel(Parcel in) {
            return new Agreement(in);
        }

        @Override
        public Agreement[] newArray(int size) {
            return new Agreement[size];
        }
    };
}
