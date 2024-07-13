package com.haiercash.gouhua.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/15
 * 描    述：
 * 修订历史：
 * ================================================================
 */
public class UserCertPhotoInfo implements Parcelable {
//    private String reversePhotoUploadStatus;            //反面身份证图片是否存在  String Y 已存在 N 不存在
//    private String frontPhotoUploadStatus;              //正面身份证图片是否存在  StringY 已存在 N 不存在
    private String frontCertPhotoPath;                  //正面身份证图片路径  String
    private String reverseCertPhotoPath;                //反面身份证图片路径String

    public String getFrontCertPhotoPath() {
        return frontCertPhotoPath;
    }

    public void setFrontCertPhotoPath(String frontCertPhotoPath) {
        this.frontCertPhotoPath = frontCertPhotoPath;
    }

    public String getReverseCertPhotoPath() {
        return reverseCertPhotoPath;
    }

    public void setReverseCertPhotoPath(String reverseCertPhotoPath) {
        this.reverseCertPhotoPath = reverseCertPhotoPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(frontCertPhotoPath);
        dest.writeString(reverseCertPhotoPath);
    }

    public UserCertPhotoInfo() {
    }

    protected UserCertPhotoInfo(Parcel in) {
        frontCertPhotoPath = in.readString();
        reverseCertPhotoPath = in.readString();
    }

    public static final Creator<UserCertPhotoInfo> CREATOR = new Creator<UserCertPhotoInfo>() {
        @Override
        public UserCertPhotoInfo createFromParcel(Parcel in) {
            return new UserCertPhotoInfo(in);
        }

        @Override
        public UserCertPhotoInfo[] newArray(int size) {
            return new UserCertPhotoInfo[size];
        }
    };
}
