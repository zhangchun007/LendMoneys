package com.haiercash.gouhua.tplibrary.bean;

import com.app.haiercash.base.utils.system.CheckUtil;

import java.io.Serializable;

/**
 * 项目名称：goHuaAND
 * 项目作者：胡玉君
 * 创建日期：2017/6/21 14:46.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class IdCardInfo implements Serializable, Cloneable {

    public String certNo;   //身份证号
    public String custName; //客户姓名
    public String gender;   //性别
    public String ethnic;   //民族
    public String birthDt; //出生日期
    public String regAddr; //详细住址

    public String certStrDt;// 身份证有效期限开始日期
    public String certEndDt;// 身份证有效期限结束日期

    public String certDt; //身份证有效期


    public String certOrga; //签发机关

    //身份证正面照片
    public byte[] ivFrontPath;

    //身份证反面照片
    public byte[] ivBackPath;

    //头像照片
    public byte[] ivPhoto;

    public String isRsa;

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEthnic() {
        return ethnic;
    }

    public void setEthnic(String ethnic) {
        this.ethnic = ethnic;
    }

    public String getBirthDt() {
        return birthDt;
    }

    public void setBirthDt(String birthDt) {
        this.birthDt = birthDt;
    }

    public String getRegAddr() {
        return regAddr;
    }

    public void setRegAddr(String regAddr) {
        this.regAddr = regAddr;
    }

    public String getCertStrDt() {
        return certStrDt;
    }

    public void setCertStrDt(String certStrDt) {
        this.certStrDt = certStrDt;
    }

    public String getCertEndDt() {
        return certEndDt;
    }

    public void setCertEndDt(String certEndDt) {
        this.certEndDt = certEndDt;
    }

    public String getCertDt() {
        return certDt;
    }

    public void setCertDt(String certDt) {
        this.certDt = certDt;
    }

    public String getCertOrga() {
        return certOrga;
    }

    public void setCertOrga(String certOrga) {
        this.certOrga = certOrga;
    }

    public byte[] getIvFrontPath() {
        return ivFrontPath;
    }

    public void setIvFrontPath(byte[] ivFrontPath) {
        this.ivFrontPath = ivFrontPath;
    }

    public byte[] getIvBackPath() {
        return ivBackPath;
    }

    public void setIvBackPath(byte[] ivBackPath) {
        this.ivBackPath = ivBackPath;
    }

    public byte[] getIvPhoto() {
        return ivPhoto;
    }

    public void setIvPhoto(byte[] ivPhoto) {
        this.ivPhoto = ivPhoto;
    }

    public String getIsRsa() {
        return isRsa;
    }

    public void setIsRsa(String isRsa) {
        this.isRsa = isRsa;
    }

    public static String isInfoPerfect(IdCardInfo info) {
        if (CheckUtil.isEmpty(info.certNo)
                || CheckUtil.isEmpty(info.custName)
                || CheckUtil.isEmpty(info.gender)
                || CheckUtil.isEmpty(info.ethnic)
                || CheckUtil.isEmpty(info.birthDt)
                || CheckUtil.isEmpty(info.regAddr)) {
            return "请先扫描身份证正面信息";
        } else if (CheckUtil.isEmpty(info.certStrDt)
                || CheckUtil.isEmpty(info.certEndDt)) {
            return "请先扫描身份证反面照片";
        } else if (CheckUtil.isEmpty(info.ivFrontPath)) {
            return "没有获取到身份证正面照片";
        } else if (CheckUtil.isEmpty(info.ivBackPath)) {
            return "没有获取到身份证反面照片";
        }
        return null;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
