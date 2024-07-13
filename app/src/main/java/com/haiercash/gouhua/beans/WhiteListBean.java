package com.haiercash.gouhua.beans;

/**
 * Created by StarFall on 2016/6/1.
 * 白名单过滤
 */
public class WhiteListBean {
    /**
     * storeNo : 602016000301
     * merchName : liu520
     * merchNo : 222016000615
     * invitTagid : 74b30e0eadac4d33a694e464ee5772a5
     * storeName : 门店1
     * tagName : 大学生
     * userName : 5-17测试商户代表
     * userId : 180398
     */
    public String custName;
    public String idTyp;
    public String idNo;
    public String isPass;
    public String level;
    public String phonenumber;
    public String isExits;
    public String storeNo;
    public String merchName;
    public String merchNo;
    public String invitTagid;
    public String storeName;
    public String tagName;
    public String userName;
    public String userId;
    public String haierCredit;//预授信额度
    private boolean grayListTypeFlag;

    public String getHaierCredit() {
        return haierCredit;
    }

    public void setHaierCredit(String haierCredit) {
        this.haierCredit = haierCredit;
    }

    public String getIsExits() {
        return isExits;
    }

    public void setIsExits(String isExits) {
        this.isExits = isExits;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getIdTyp() {
        return idTyp;
    }

    public void setIdTyp(String idTyp) {
        this.idTyp = idTyp;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getIsPass() {
        return isPass;
    }

    public void setIsPass(String isPass) {
        this.isPass = isPass;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStoreNo() {
        return storeNo;
    }

    public void setStoreNo(String storeNo) {
        this.storeNo = storeNo;
    }

    public String getMerchName() {
        return merchName;
    }

    public void setMerchName(String merchName) {
        this.merchName = merchName;
    }

    public String getMerchNo() {
        return merchNo;
    }

    public void setMerchNo(String merchNo) {
        this.merchNo = merchNo;
    }

    public String getInvitTagid() {
        return invitTagid;
    }

    public void setInvitTagid(String invitTagid) {
        this.invitTagid = invitTagid;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isGrayListTypeFlag() {
        return grayListTypeFlag;
    }

    public void setGrayListTypeFlag(boolean grayListTypeFlag) {
        this.grayListTypeFlag = grayListTypeFlag;
    }
}
