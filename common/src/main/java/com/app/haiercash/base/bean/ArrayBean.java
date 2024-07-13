package com.app.haiercash.base.bean;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.contrarywind.interfaces.IPickerViewData;

/**
 * *@Author:    Sun
 * *@Date  :    2020/7/22
 * *@FileName: AddressInfo
 * *@Description:
 */
@Entity(tableName = "s_area")
public class ArrayBean implements Parcelable, IPickerViewData {
//    private String name;                 //区域名称
//    private String code;                 //区域编码
//    private String parent;               //父区域编码
//    private String type;                 //区域类型
//    private String firstShortSpell;      //首字母缩写
//    private String firstIndexChar;       //检索首字母
//    private String indexChars;           //检索范围
//    private String version;              //版本号


    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int id;

    @ColumnInfo(name = "AREA_CODE")
    public String code;

    @ColumnInfo(name = "AREA_NAME")
    public String name;

    @ColumnInfo(name = "AREA_PARENT_CODE")
    public String parent;

    @ColumnInfo(name = "AREA_TYPE")
    public String type;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayBean() {

    }

    @Ignore
    public ArrayBean(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Ignore
    public ArrayBean(String code, String name, String type) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

    protected ArrayBean(Parcel in) {
        id = in.readInt();
        code = in.readString();
        name = in.readString();
        parent = in.readString();
        type = in.readString();
    }

    public static final Creator<ArrayBean> CREATOR = new Creator<ArrayBean>() {
        @Override
        public ArrayBean createFromParcel(Parcel in) {
            return new ArrayBean(in);
        }

        @Override
        public ArrayBean[] newArray(int size) {
            return new ArrayBean[size];
        }
    };

    @Override
    public String toString() {
        return "AddressInfo{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", parent='" + parent + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(parent);
        dest.writeString(type);
    }

    @Override
    public String getPickerViewText() {
        return name;
    }
}
