package com.haiercash.gouhua.beans;

/**
 * Created by use on 2016/5/16.
 * 客户编号	custNo
 * 客户名称	custName
 * 证件类型	certType
 * 证件号	certNo
 * 银行卡号	cardNo
 * 手机号	mobile
 * 账号	acctNo
 * 账户名	acctName
 * 银行号	acctBankNo
 * 开户省	acctProvince
 * 开户市	acctCity
 * 开户区	acctArea
 * 分支行代码	accBchCde
 * 分支行名称	accBchName
 */
public class ShiMingRenZheng_Fanhuixinxi_post {
    public String code;
    public String custNo;
    public String custName;
    public String certType;
    public String certNo;
    public String cardNo;
    public String mobile;
    public String acctNo;
    public String acctName;
    public String photoUploadStatus; //图片上传状态
    public String identityVerifyStatus; //实名认证状态
    public String isUpdateMobileSuccess;//修改绑定手机号是否成功
    public String creditExist;//在该接口中增加返回字段当实名认证成功后，使用客户号到信贷查询用户是否存在额度（额度编号：50120001，信贷接口为：110200），如存在额度则creditExist返回为Y，不存在则返回为N。前端针对这个字段做不同的逻辑处理。

    public String getCreditReject() {
        return creditReject;
    }

    public void setCreditReject(String creditReject) {
        this.creditReject = creditReject;
    }

    public String creditReject;//额度被拒

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public String getPhotoUploadStatus() {
        return photoUploadStatus;
    }

    public void setPhotoUploadStatus(String photoUploadStatus) {
        this.photoUploadStatus = photoUploadStatus;
    }

    public String getIdentityVerifyStatus() {
        return identityVerifyStatus;
    }

    public void setIdentityVerifyStatus(String identityVerifyStatus) {
        this.identityVerifyStatus = identityVerifyStatus;
    }

    public String getIsUpdateMobileSuccess() {
        return isUpdateMobileSuccess;
    }

    public void setIsUpdateMobileSuccess(String isUpdateMobileSuccess) {
        this.isUpdateMobileSuccess = isUpdateMobileSuccess;
    }

    public String getCreditExist() {
        return creditExist;
    }

    public void setCreditExist(String creditExist) {
        this.creditExist = creditExist;
    }
}
