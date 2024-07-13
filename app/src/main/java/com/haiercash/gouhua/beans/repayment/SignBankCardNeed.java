package com.haiercash.gouhua.beans.repayment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 查询银行卡是否需要签约的接口返回类
 */
public class SignBankCardNeed implements Parcelable {
    private String needSign;//是否需要进行验证,Y需要,N不需要
    private String interId;//签约渠道
    private String signSuccessNumber;//签约渠道数，小于等于0则需要强制签约

    public SignBankCardNeed() {
    }

    protected SignBankCardNeed(Parcel in) {
        needSign = in.readString();
        interId = in.readString();
        signSuccessNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(needSign);
        dest.writeString(interId);
        dest.writeString(signSuccessNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SignBankCardNeed> CREATOR = new Creator<SignBankCardNeed>() {
        @Override
        public SignBankCardNeed createFromParcel(Parcel in) {
            return new SignBankCardNeed(in);
        }

        @Override
        public SignBankCardNeed[] newArray(int size) {
            return new SignBankCardNeed[size];
        }
    };

    public String getSignSuccessNumber() {
        return signSuccessNumber;
    }

    public void setSignSuccessNumber(String signSuccessNumber) {
        this.signSuccessNumber = signSuccessNumber;
    }

    public boolean needSign() {
        return "Y".equals(needSign);
    }

    public String getNeedSign() {
        return needSign;
    }

    public void setNeedSign(String needSign) {
        this.needSign = needSign;
    }

    public String getInterId() {
        return interId;
    }

    public void setInterId(String interId) {
        this.interId = interId;
    }
}
