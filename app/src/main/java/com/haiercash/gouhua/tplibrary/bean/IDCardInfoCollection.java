package com.haiercash.gouhua.tplibrary.bean;

import java.io.Serializable;

public class IDCardInfoCollection implements Serializable {

    /**
     * 图片路径
     */
    public String path;
    /**
     * 正反面
     */
    public String side;

    /**
     * 签发机关
     */
    public String issuedBy;

    /**
     * 有效日期的起始时间
     */
    public String validDateStart;

    /**
     * 有效日期的结束时间
     */
    public String validDateEnd;

    /**
     * 姓名
     */
    public String name;
    /**
     * 性别
     */
    public String gender;

    /**
     * 民族
     */
    public String nationality;

    /**
     * 出身年份
     */
    public String birthYear;

    /**
     * 出生月数
     */
    public String birthMonth;

    /**
     * 出生日
     */
    public String birthDay;
    /**
     * 身份证号
     */
    public String idcardNumber;

    /**
     * 住址
     */
    public String address;

    //是否可能为学生
    public String maybeStudent;  //Y 是  N 否  正面必返

    //需要签的协议
    private String contName;  //协议名
    private String contUrl;  //协议地址

    public IDCardInfoCollection(String path, String side, String issuedBy, String validDateStart, String validDateEnd, String name, String gender, String nationality, String birthYear, String birthMonth, String birthDay, String idcardNumber, String address, String maybeStudent, String contName, String contUrl, String signAgreementStatus) {
        this.path = path;
        this.side = side;
        this.issuedBy = issuedBy;
        this.validDateStart = validDateStart;
        this.validDateEnd = validDateEnd;
        this.name = name;
        this.gender = gender;
        this.nationality = nationality;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.idcardNumber = idcardNumber;
        this.address = address;
        this.maybeStudent = maybeStudent;
        this.contName = contName;
        this.contUrl = contUrl;
        this.signAgreementStatus = signAgreementStatus;
    }

    public String getContName() {
        return contName;
    }

    public void setContName(String contName) {
        this.contName = contName;
    }

    public String getContUrl() {
        return contUrl;
    }

    public void setContUrl(String contUrl) {
        this.contUrl = contUrl;
    }

    public String getMaybeStudent() {
        return maybeStudent;
    }

    public void setMaybeStudent(String maybeStudent) {
        this.maybeStudent = maybeStudent;
    }

    public String getSignAgreementStatus() {
        return signAgreementStatus;
    }

    public void setSignAgreementStatus(String signAgreementStatus) {
        this.signAgreementStatus = signAgreementStatus;
    }

    //非学生承诺函签署状态
    public String signAgreementStatus; //Y 是  N 否  正面必返

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getValidDateStart() {
        return validDateStart;
    }

    public void setValidDateStart(String validDateStart) {
        this.validDateStart = validDateStart;
    }

    public String getValidDateEnd() {
        return validDateEnd;
    }

    public void setValidDateEnd(String validDateEnd) {
        this.validDateEnd = validDateEnd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(String birthMonth) {
        this.birthMonth = birthMonth;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getIdcardNumber() {
        return idcardNumber;
    }

    public void setIdcardNumber(String idcardNumber) {
        this.idcardNumber = idcardNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
