package com.haiercash.gouhua.beans;

import com.app.haiercash.base.utils.system.CheckUtil;

import java.io.File;
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
    public String ivFrontPath;

    //身份证反面照片
    public String ivBackPath;

    //头像照片
    public String ivPhoto;

    private String isRsa;

    public String getCert() {
        return certDt;
    }

    public void setCert(String cert) {
        this.certDt = cert;
    }

    public String getIvFrontPath() {
        return ivFrontPath;
    }

    public void setIvFrontPath(String ivFrontPath) {
        this.ivFrontPath = ivFrontPath;
    }

    public String getIvBackPath() {
        return ivBackPath;
    }

    public void setIvBackPath(String ivBackPath) {
        this.ivBackPath = ivBackPath;
    }

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

    public String getIssue() {
        return certOrga;
    }

    public void setIssue(String issue) {
        this.certOrga = issue;
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
        } else if (CheckUtil.isEmpty(info.ivFrontPath) || !new File(info.ivFrontPath).exists()) {
            return "没有获取到身份证正面照片";
        } else if (CheckUtil.isEmpty(info.ivBackPath) || !new File(info.ivBackPath).exists()) {
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
