package com.haiercash.gouhua.beans.agreement;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 萨摩耶协议信息
 */
public class SmyAgreementBean implements Parcelable {
    private ArrayList<AgreementBean> agreementLinkList;

    protected SmyAgreementBean(Parcel in) {
        agreementLinkList = in.createTypedArrayList(AgreementBean.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(agreementLinkList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SmyAgreementBean> CREATOR = new Creator<SmyAgreementBean>() {
        @Override
        public SmyAgreementBean createFromParcel(Parcel in) {
            return new SmyAgreementBean(in);
        }

        @Override
        public SmyAgreementBean[] newArray(int size) {
            return new SmyAgreementBean[size];
        }
    };

    public ArrayList<AgreementBean> getAgreementLinkList() {
        return agreementLinkList;
    }

    public void setAgreementLinkList(ArrayList<AgreementBean> agreementLinkList) {
        this.agreementLinkList = agreementLinkList;
    }

    public static class AgreementBean implements Parcelable {
        private String name;//协议名称
        private String link;//协议路径，需拿到该路径后，拼接够花的对应环境的域名

        public AgreementBean(String name, String link) {
            this.name = name;
            this.link = link;
        }

        protected AgreementBean(Parcel in) {
            name = in.readString();
            link = in.readString();
        }

        public static final Creator<AgreementBean> CREATOR = new Creator<AgreementBean>() {
            @Override
            public AgreementBean createFromParcel(Parcel in) {
                return new AgreementBean(in);
            }

            @Override
            public AgreementBean[] newArray(int size) {
                return new AgreementBean[size];
            }
        };

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(link);
        }
    }
}
