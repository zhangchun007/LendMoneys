package com.haiercash.gouhua.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by StarFall on 2016/5/26.
 * 现金贷订单保存
 * 商品代码	goodsCode
 * 商品品牌	goodsBrand
 * 商品类型	goodsKind
 * 商品名称	goodsName
 * 商品型号	goodsModel
 * 数量	goodsNum
 * 单价	goodsPrice
 * deliverAddr
 * <p>
 * monthRepay期供
 * <p>
 * //        送货地址省代码	deliverProvince
 * //        送货地址市代码	deliverCity
 * //        送货地址区代码	deliverArea
 */
public class SaveOrderBean implements Serializable {
    private String orderNo;
    private String merchNo;
    private String cooprCde;
    private String crtUsr;
    private String typGrp;
    private String purpose;
    private String custNo;
    private String typCde;
    private String applyAmt;
    private String applyTnr;
    private String applyTnrTyp;
    private String totalnormint;
    private String totalfeeamt;
    private String mtdCde;
    private String accAcBchCde;
    private String accAcBchName;
    private String repayApplCardNo;
    private String applCardNo;
    private String source;
    private String applSeq;
    private String applCde;
    private String apply_dt;
    private String deliverAddr;//送货地址
    private String monthRepay;//期供
    private String whiteType;//白名单类型
    private String proPurAmt;//商品总额
    private String deliverAddrTyp;//门店code
    private String fstPay;//首付金额
    private String deliverProvince;
    private String deliverCity;
    private String deliverArea;
    private String indivMobile;
    private String version;
    private String userId;//2016-8-29 添加
    private String expectCredit;
    /**
     * accBankName :
     * idTyp : 20
     * cooprName : 宁夏平克电子商贸有限公司恒泰祥门店
     * accBankCde :
     * idNo : 372926199009295116
     * backReason : 资料不齐,资料上传错误
     * repayAccBankName : 中国农业银行
     * state :
     * countCommonRepaymentPerson : 1
     * pLoanTypFstPct :
     * xfze : 36.0
     * applyDt : 2016-09-24
     * typDesc : 家电分期
     * applseq : 1143603
     * pLoanTypMaxAmt :
     * isCustInfoCompleted : N
     * repayAccBankCde : 103
     * appInAdvice : 各具特色吗
     * pLoanTypMinAmt :
     * pLoanTypTnrOpt :
     * payMtdDesc :
     * repayAccBchCde :
     * pLoanTypGoodMaxNum :
     * custName : 贺庆信
     * repayAccBchName :
     * payMtd :
     * isConfirmContract : 1
     * isConfirmAgreement : 1
     * expectCredit额度期望值
     * formType 订单类型  商品贷必输
     */

    private String accBankName;
    private String idTyp;
    private String cooprName;
    private String accBankCde;
    private String idNo;
    private String backReason;
    private String repayAccBankName;
    private String state;
    private int countCommonRepaymentPerson;
    private String pLoanTypFstPct;
    private String xfze;
    private String applyDt;
    private String typDesc;
    private String applseq;
    private String pLoanTypMaxAmt;
    private String isCustInfoCompleted;
    private String repayAccBankCde;
    private String appInAdvice;
    private String pLoanTypMinAmt;
    private String pLoanTypTnrOpt;
    private String payMtdDesc;
    private String repayAccBchCde;
    private String pLoanTypGoodMaxNum;
    private String custName;
    private String repayAccBchName;
    private String payMtd;
    private String isConfirmContract;
    private String isConfirmAgreement;
    private String formType;
    private String needResignCredit;//是否需要重签征信授权
    private UniteLoanInfoBean uniteLoanInfo;//联合放款信息

    public LinksBean getForcePreviewInfo() {
        return forcePreviewInfo;
    }

    public void setForcePreviewInfo(LinksBean forcePreviewInfo) {
        this.forcePreviewInfo = forcePreviewInfo;
    }

    private LinksBean forcePreviewInfo;//需要强制展示的合同

    public String getNeedResignCredit() {
        return needResignCredit;
    }

    public void setNeedResignCredit(String needResignCredit) {
        this.needResignCredit = needResignCredit;
    }

    public UniteLoanInfoBean getUniteLoanInfo() {
        return uniteLoanInfo;
    }

    public void setUniteLoanInfo(UniteLoanInfoBean uniteLoanInfo) {
        this.uniteLoanInfo = uniteLoanInfo;
    }

    public String getpLoanTypFstPct() {
        return pLoanTypFstPct;
    }

    public void setpLoanTypFstPct(String pLoanTypFstPct) {
        this.pLoanTypFstPct = pLoanTypFstPct;
    }

    public String getpLoanTypMaxAmt() {
        return pLoanTypMaxAmt;
    }

    public void setpLoanTypMaxAmt(String pLoanTypMaxAmt) {
        this.pLoanTypMaxAmt = pLoanTypMaxAmt;
    }

    public String getpLoanTypMinAmt() {
        return pLoanTypMinAmt;
    }

    public void setpLoanTypMinAmt(String pLoanTypMinAmt) {
        this.pLoanTypMinAmt = pLoanTypMinAmt;
    }

    public String getpLoanTypTnrOpt() {
        return pLoanTypTnrOpt;
    }

    public void setpLoanTypTnrOpt(String pLoanTypTnrOpt) {
        this.pLoanTypTnrOpt = pLoanTypTnrOpt;
    }

    public String getpLoanTypGoodMaxNum() {
        return pLoanTypGoodMaxNum;
    }

    public void setpLoanTypGoodMaxNum(String pLoanTypGoodMaxNum) {
        this.pLoanTypGoodMaxNum = pLoanTypGoodMaxNum;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getExpectCredit() {
        return expectCredit;
    }

    public void setExpectCredit(String expectCredit) {
        this.expectCredit = expectCredit;
    }

    public String getIndivMobile() {
        return indivMobile;
    }

    public void setIndivMobile(String indivMobile) {
        this.indivMobile = indivMobile;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeliverProvince() {
        return deliverProvince;
    }

    public void setDeliverProvince(String deliverProvince) {
        this.deliverProvince = deliverProvince;
    }

    public String getDeliverCity() {
        return deliverCity;
    }

    public void setDeliverCity(String deliverCity) {
        this.deliverCity = deliverCity;
    }

    public String getDeliverArea() {
        return deliverArea;
    }

    public void setDeliverArea(String deliverArea) {
        this.deliverArea = deliverArea;
    }

    public String getFstPay() {
        return fstPay;
    }

    public void setFstPay(String fstPay) {
        this.fstPay = fstPay;
    }

    public String getDeliverAddrTyp() {
        return deliverAddrTyp;
    }

    public void setDeliverAddrTyp(String deliverAddrTyp) {
        this.deliverAddrTyp = deliverAddrTyp;
    }

    public String getProPurAmt() {
        return proPurAmt;
    }

    public void setProPurAmt(String proPurAmt) {
        this.proPurAmt = proPurAmt;
    }

    public String getWhiteType() {
        return whiteType;
    }

    public void setWhiteType(String whiteType) {
        this.whiteType = whiteType;
    }

    public String getApplCardNo() {
        return applCardNo;
    }

    public void setApplCardNo(String applCardNo) {
        this.applCardNo = applCardNo;
    }

    public String getMonthRepay() {
        return monthRepay;
    }

    public void setMonthRepay(String monthRepay) {
        this.monthRepay = monthRepay;
    }

    public String getDeliverAddr() {
        return deliverAddr;
    }

    public void setDeliverAddr(String deliverAddr) {
        this.deliverAddr = deliverAddr;
    }

    public String getApply_dt() {
        return apply_dt;
    }

    public void setApply_dt(String apply_dt) {
        this.apply_dt = apply_dt;
    }

    public String getApplSeq() {
        return applSeq;
    }

    public void setApplSeq(String applSeq) {
        this.applSeq = applSeq;
    }

    public String getApplCde() {
        return applCde;
    }

    public void setApplCde(String applCde) {
        this.applCde = applCde;
    }

    private String goodsCode;
    private String goodsBrand;
    private String goodsKind;
    private String goodsName;
    private String goodsModel;
    private String goodsNum;
    private String goodsPrice;

    public String getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(String goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsBrand() {
        return goodsBrand;
    }

    public void setGoodsBrand(String goodsBrand) {
        this.goodsBrand = goodsBrand;
    }

    public String getGoodsKind() {
        return goodsKind;
    }

    public void setGoodsKind(String goodsKind) {
        this.goodsKind = goodsKind;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsModel() {
        return goodsModel;
    }

    public void setGoodsModel(String goodsModel) {
        this.goodsModel = goodsModel;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMerchNo() {
        return merchNo;
    }

    public void setMerchNo(String merchNo) {
        this.merchNo = merchNo;
    }

    public String getCooprCde() {
        return cooprCde;
    }

    public void setCooprCde(String cooprCde) {
        this.cooprCde = cooprCde;
    }

    public String getCrtUsr() {
        return crtUsr;
    }

    public void setCrtUsr(String crtUsr) {
        this.crtUsr = crtUsr;
    }

    public String getTypGrp() {
        return typGrp;
    }

    public void setTypGrp(String typGrp) {
        this.typGrp = typGrp;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getTypCde() {
        return typCde;
    }

    public void setTypCde(String typCde) {
        this.typCde = typCde;
    }

    public String getApplyAmt() {
        return applyAmt;
    }

    public void setApplyAmt(String applyAmt) {
        this.applyAmt = applyAmt;
    }

    public String getApplyTnr() {
        return applyTnr;
    }

    public void setApplyTnr(String applyTnr) {
        this.applyTnr = applyTnr;
    }

    public String getApplyTnrTyp() {
        return applyTnrTyp;
    }

    public void setApplyTnrTyp(String applyTnrTyp) {
        this.applyTnrTyp = applyTnrTyp;
    }

    public String getTotalnormint() {
        return totalnormint;
    }

    public void setTotalnormint(String totalnormint) {
        this.totalnormint = totalnormint;
    }

    public String getTotalfeeamt() {
        return totalfeeamt;
    }

    public void setTotalfeeamt(String totalfeeamt) {
        this.totalfeeamt = totalfeeamt;
    }

    public String getMtdCde() {
        return mtdCde;
    }

    public void setMtdCde(String mtdCde) {
        this.mtdCde = mtdCde;
    }

    public String getAccAcBchCde() {
        return accAcBchCde;
    }

    public void setAccAcBchCde(String accAcBchCde) {
        this.accAcBchCde = accAcBchCde;
    }

    public String getAccAcBchName() {
        return accAcBchName;
    }

    public void setAccAcBchName(String accAcBchName) {
        this.accAcBchName = accAcBchName;
    }

    public String getRepayApplCardNo() {
        return repayApplCardNo;
    }

    public void setRepayApplCardNo(String repayApplCardNo) {
        this.repayApplCardNo = repayApplCardNo;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAccBankName() {
        return accBankName;
    }

    public void setAccBankName(String accBankName) {
        this.accBankName = accBankName;
    }

    public String getIdTyp() {
        return idTyp;
    }

    public void setIdTyp(String idTyp) {
        this.idTyp = idTyp;
    }

    public String getCooprName() {
        return cooprName;
    }

    public void setCooprName(String cooprName) {
        this.cooprName = cooprName;
    }

    public String getAccBankCde() {
        return accBankCde;
    }

    public void setAccBankCde(String accBankCde) {
        this.accBankCde = accBankCde;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getBackReason() {
        return backReason;
    }

    public void setBackReason(String backReason) {
        this.backReason = backReason;
    }

    public String getRepayAccBankName() {
        return repayAccBankName;
    }

    public void setRepayAccBankName(String repayAccBankName) {
        this.repayAccBankName = repayAccBankName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getCountCommonRepaymentPerson() {
        return countCommonRepaymentPerson;
    }

    public void setCountCommonRepaymentPerson(int countCommonRepaymentPerson) {
        this.countCommonRepaymentPerson = countCommonRepaymentPerson;
    }

    public String getPLoanTypFstPct() {
        return pLoanTypFstPct;
    }

    public void setPLoanTypFstPct(String pLoanTypFstPct) {
        this.pLoanTypFstPct = pLoanTypFstPct;
    }

    public String getXfze() {
        return xfze;
    }

    public void setXfze(String xfze) {
        this.xfze = xfze;
    }

    public String getApplyDt() {
        return applyDt;
    }

    public void setApplyDt(String applyDt) {
        this.applyDt = applyDt;
    }

    public String getTypDesc() {
        return typDesc;
    }

    public void setTypDesc(String typDesc) {
        this.typDesc = typDesc;
    }

    public String getApplseq() {
        return applseq;
    }

    public void setApplseq(String applseq) {
        this.applseq = applseq;
    }

    public String getPLoanTypMaxAmt() {
        return pLoanTypMaxAmt;
    }

    public void setPLoanTypMaxAmt(String pLoanTypMaxAmt) {
        this.pLoanTypMaxAmt = pLoanTypMaxAmt;
    }

    public String getIsCustInfoCompleted() {
        return isCustInfoCompleted;
    }

    public void setIsCustInfoCompleted(String isCustInfoCompleted) {
        this.isCustInfoCompleted = isCustInfoCompleted;
    }

    public String getRepayAccBankCde() {
        return repayAccBankCde;
    }

    public void setRepayAccBankCde(String repayAccBankCde) {
        this.repayAccBankCde = repayAccBankCde;
    }

    public String getAppInAdvice() {
        return appInAdvice;
    }

    public void setAppInAdvice(String appInAdvice) {
        this.appInAdvice = appInAdvice;
    }

    public String getPLoanTypMinAmt() {
        return pLoanTypMinAmt;
    }

    public void setPLoanTypMinAmt(String pLoanTypMinAmt) {
        this.pLoanTypMinAmt = pLoanTypMinAmt;
    }

    public String getPLoanTypTnrOpt() {
        return pLoanTypTnrOpt;
    }

    public void setPLoanTypTnrOpt(String pLoanTypTnrOpt) {
        this.pLoanTypTnrOpt = pLoanTypTnrOpt;
    }

    public String getPayMtdDesc() {
        return payMtdDesc;
    }

    public void setPayMtdDesc(String payMtdDesc) {
        this.payMtdDesc = payMtdDesc;
    }

    public String getRepayAccBchCde() {
        return repayAccBchCde;
    }

    public void setRepayAccBchCde(String repayAccBchCde) {
        this.repayAccBchCde = repayAccBchCde;
    }

    public String getPLoanTypGoodMaxNum() {
        return pLoanTypGoodMaxNum;
    }

    public void setPLoanTypGoodMaxNum(String pLoanTypGoodMaxNum) {
        this.pLoanTypGoodMaxNum = pLoanTypGoodMaxNum;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getRepayAccBchName() {
        return repayAccBchName;
    }

    public void setRepayAccBchName(String repayAccBchName) {
        this.repayAccBchName = repayAccBchName;
    }

    public String getPayMtd() {
        return payMtd;
    }

    public void setPayMtd(String payMtd) {
        this.payMtd = payMtd;
    }

    public String getIsConfirmContract() {
        return isConfirmContract;
    }

    public void setIsConfirmContract(String isConfirmContract) {
        this.isConfirmContract = isConfirmContract;
    }

    public String getIsConfirmAgreement() {
        return isConfirmAgreement;
    }

    public void setIsConfirmAgreement(String isConfirmAgreement) {
        this.isConfirmAgreement = isConfirmAgreement;
    }

    public static class UniteLoanInfoBean implements Serializable {

        /**
         * applSeq :
         * agencyId :
         * isUniteLoan : 1
         * agencyIdName :
         * projectId :
         * projectName :
         * creditModleUrl :
         * isCredit : 2
         * contractInfoList : [{"contractName":"","contractLink ":"http:"}]
         */

        private String applSeq;//业务编号
        private String agencyId;//若非联合放款则为空
        private String isUniteLoan;//1-联合放款,0-非联合放款
        private String agencyIdName;//
        private String projectId;//
        private String projectName;//
        private String creditModleUrl;//是否需要征信为2时必输。返回征信模板共享存储路径。
        private String isCredit;//是否需要征信 1-不需要，2-需要
        private List<LinksBean> links;//合同及协议地址集合

        public String getApplSeq() {
            return applSeq;
        }

        public void setApplSeq(String applSeq) {
            this.applSeq = applSeq;
        }

        public String getAgencyId() {
            return agencyId;
        }

        public void setAgencyId(String agencyId) {
            this.agencyId = agencyId;
        }

        public String getIsUniteLoan() {
            return isUniteLoan;
        }

        public void setIsUniteLoan(String isUniteLoan) {
            this.isUniteLoan = isUniteLoan;
        }

        public String getAgencyIdName() {
            return agencyIdName;
        }

        public void setAgencyIdName(String agencyIdName) {
            this.agencyIdName = agencyIdName;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getCreditModleUrl() {
            return creditModleUrl;
        }

        public void setCreditModleUrl(String creditModleUrl) {
            this.creditModleUrl = creditModleUrl;
        }

        public String getIsCredit() {
            return isCredit;
        }

        public void setIsCredit(String isCredit) {
            this.isCredit = isCredit;
        }

        public List<LinksBean> getLinks() {
            return links;
        }

        public void setLinks(List<LinksBean> links) {
            this.links = links;
        }
    }

    public static class LinksBean implements Serializable {
        private String name;//业务编号
        private String link;//是否联合放款
        private String type;//类型: contract 合同 agreement 协议

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
