package com.haiercash.gouhua.beans;

/**
 * Created by use on 2016/11/22.
 * 实名认证信息	SMRZ
 * 人脸识别信息	RLSB
 * 个人基本信息	GRJBXX
 * 单位信息	DWXX
 * 居住信息	JZXX
 * 联系人信息	LXRXX
 * 银行流水	YHLS
 * 工资证明	GZZM
 * 公积金	GJJ
 * 资产证明	ZCZM
 * 影像信息	YXXX
 * 身份证照片是否存在  CERTFLAG
 * 人脸图像是否存在 facePhotoFlag
 * 是否已上传通讯录风险信息 contactRiskFlag
 * 身份证是否有效 certInfoExpired
 * 身份证是否过期certInfoExpiredStatus
 */

public class CheckIfMsgCompleteBean {
    public String SMRZ;
    public Object RLSB;
    public String GRJBXX;
    public String DWXX;
    public String JZXX;
    public String LXRXX;
    public String CERTFLAG;
    public String facePhotoFlag;
    public String contactRiskFlag;
    public String certInfoExpired;//Y是 N否
    public String certInfoExpiredStatus;//Y过期 N未过期 E 即将过期
    public DefaultCard defaultCardInfo;//默认卡信息
    private String emailExist;//Y 存在 N 不存在
    private String hasMthInc;
    private String personalInfoComplate;  //个人信息是否完整
    private String msgCompleteState;    //信息是否完整

    public String getPersonalInfoComplate() {
        return personalInfoComplate;
    }

    public void setPersonalInfoComplate(String personalInfoComplate) {
        this.personalInfoComplate = personalInfoComplate;
    }

    public String getMsgCompleteState() {
        return msgCompleteState;
    }

    public void setMsgCompleteState(String msgCompleteState) {
        this.msgCompleteState = msgCompleteState;
    }

    public DefaultCard getDefaultCardInfo() {
        return defaultCardInfo;
    }

    public void setDefaultCardInfo(DefaultCard defaultCardInfo) {
        this.defaultCardInfo = defaultCardInfo;
    }

    public String getCertInfoExpiredStatus() {
        return certInfoExpiredStatus;
    }

    public void setCertInfoExpiredStatus(String certInfoExpiredStatus) {
        this.certInfoExpiredStatus = certInfoExpiredStatus;
    }

    public String getCertInfoExpired() {
        return certInfoExpired;
    }

    public void setCertInfoExpired(String certInfoExpired) {
        this.certInfoExpired = certInfoExpired;
    }

    public String getSMRZ() {
        return SMRZ;
    }

    public void setSMRZ(String SMRZ) {
        this.SMRZ = SMRZ;
    }

    public Object getRLSB() {
        return RLSB;
    }

    public void setRLSB(Object RLSB) {
        this.RLSB = RLSB;
    }

    public String getGRJBXX() {
        return GRJBXX;
    }

    public void setGRJBXX(String GRJBXX) {
        this.GRJBXX = GRJBXX;
    }

    public String getDWXX() {
        return DWXX;
    }

    public void setDWXX(String DWXX) {
        this.DWXX = DWXX;
    }

    public String getJZXX() {
        return JZXX;
    }

    public void setJZXX(String JZXX) {
        this.JZXX = JZXX;
    }

    public String getLXRXX() {
        return LXRXX;
    }

    public void setLXRXX(String LXRXX) {
        this.LXRXX = LXRXX;
    }

    public String getCERTFLAG() {
        return CERTFLAG;
    }

    public void setCERTFLAG(String CERTFLAG) {
        this.CERTFLAG = CERTFLAG;
    }

    public String getFacePhotoFlag() {
        return facePhotoFlag;
    }

    public void setFacePhotoFlag(String facePhotoFlag) {
        this.facePhotoFlag = facePhotoFlag;
    }

    public String getContactRiskFlag() {
        return contactRiskFlag;
    }

    public void setContactRiskFlag(String contactRiskFlag) {
        this.contactRiskFlag = contactRiskFlag;
    }

    public String getEmailExist() {
        return emailExist;
    }

    public void setEmailExist(String emailExist) {
        this.emailExist = emailExist;
    }

    public String getHasMthInc() {
        return hasMthInc;
    }

    public void setHasMthInc(String hasMthInc) {
        this.hasMthInc = hasMthInc;
    }

    public class DefaultCard {
        //默认卡卡号
        public String cardNo;
        public String custName;
        //SIGN_DEALING: 处理中
        //SIGN_SUCCESS:成功
        //SIGN_FAIL:失败
        //UN_SIGN:未签约
        //SIGN_EXPIRE:签约过期
        public String signStatus;

        public String getCustName() {
            return custName;
        }

        public void setCustName(String custName) {
            this.custName = custName;
        }

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }

        public String getSignStatus() {
            return signStatus;
        }

        public void setSignStatus(String signStatus) {
            this.signStatus = signStatus;
        }
    }
}
