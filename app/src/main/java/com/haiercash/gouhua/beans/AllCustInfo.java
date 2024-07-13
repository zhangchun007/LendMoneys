package com.haiercash.gouhua.beans;

/**
 * Created by use on 2017/2/13.
 */
public class AllCustInfo {

    public String custNo;

    //单位信息
    public String officeName;
    public String position;
    private String mthInc;//月收入
    public String officeTyp;//职业
    public String officeTel;
    public String officeProvince;
    public String officeCity;
    public String officeArea;
    public String officeAddr;
    //行业性质
    public String custIndtry;
    //年收入
    public String yearInc;

    //个人信息
    public String maritalStatus;
    public String liveProvince;
    public String liveCity;
    public String liveArea;
    public String liveAddr;

    public String dataFrom;
    private String email;
    private String education;//	01	00-硕士及以上， 10-本科， 20-大专， 30-高中， 40-初中及以下;

    public String getOfficeTyp() {
        return officeTyp;
    }

    public void setOfficeTyp(String officeTyp) {
        this.officeTyp = officeTyp;
    }

    @Override
    public String toString() {
        return "AllCustInfo{" +
                "custNo='" + custNo + '\'' +
                ", officeName='" + officeName + '\'' +
                ", position='" + position + '\'' +
                ", mthInc='" + mthInc + '\'' +
                ", officeTel='" + officeTel + '\'' +
                ", officeProvince='" + officeProvince + '\'' +
                ", officeTyp='" + officeTyp + '\'' +
                ", officeCity='" + officeCity + '\'' +
                ", officeArea='" + officeArea + '\'' +
                ", officeAddr='" + officeAddr + '\'' +
                ", custIndtry='" + custIndtry + '\'' +
                ", yearInc='" + yearInc + '\'' +
                ", maritalStatus='" + maritalStatus + '\'' +
                ", liveProvince='" + liveProvince + '\'' +
                ", liveCity='" + liveCity + '\'' +
                ", liveArea='" + liveArea + '\'' +
                ", liveAddr='" + liveAddr + '\'' +
                ", dataFrom='" + dataFrom + '\'' +
                ", email='" + email + '\'' +
                ", education='" + education + '\'' +
                '}';
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public AllCustInfo() {
    }

    public String getYearInc() {
        return yearInc;
    }

    public void setYearInc(String yearInc) {
        this.yearInc = yearInc;
    }

    public String getCustIndtry() {
        return custIndtry;
    }

    public void setCustIndtry(String custIndtry) {
        this.custIndtry = custIndtry;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMthInc() {
        return mthInc;
    }

    public void setMthInc(String mthInc) {
        this.mthInc = mthInc;
    }

    public String getOfficeTel() {
        return officeTel;
    }

    public void setOfficeTel(String officeTel) {
        this.officeTel = officeTel;
    }

    public String getOfficeProvince() {
        return officeProvince;
    }

    public void setOfficeProvince(String officeProvince) {
        this.officeProvince = officeProvince;
    }

    public String getOfficeCity() {
        return officeCity;
    }

    public void setOfficeCity(String officeCity) {
        this.officeCity = officeCity;
    }

    public String getOfficeArea() {
        return officeArea;
    }

    public void setOfficeArea(String officeArea) {
        this.officeArea = officeArea;
    }

    public String getOfficeAddr() {
        return officeAddr;
    }

    public void setOfficeAddr(String officeAddr) {
        this.officeAddr = officeAddr;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getLiveProvince() {
        return liveProvince;
    }

    public void setLiveProvince(String liveProvince) {
        this.liveProvince = liveProvince;
    }

    public String getLiveCity() {
        return liveCity;
    }

    public void setLiveCity(String liveCity) {
        this.liveCity = liveCity;
    }

    public String getLiveArea() {
        return liveArea;
    }

    public void setLiveArea(String liveArea) {
        this.liveArea = liveArea;
    }

    public String getLiveAddr() {
        return liveAddr;
    }

    public void setLiveAddr(String liveAddr) {
        this.liveAddr = liveAddr;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
