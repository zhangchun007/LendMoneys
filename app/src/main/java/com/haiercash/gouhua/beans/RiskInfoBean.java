package com.haiercash.gouhua.beans;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;

import java.util.List;

/**
 * Created by Limige on 2016/6/22.
 * 6.53.(POST) 外部风险信息采集
 */
public class RiskInfoBean {

    /**
     * reserved3 :
     * reserved2 :
     * reserved1 :
     * idNo : 511322198911283000
     * reserved5 :
     * reserved4 :
     */
    private String userId;
    private List<Object> reserved3;
    private List<Object> reserved2;
    private String reserved1;
    private String idNo;
    private String reserved5;
    private String reserved6;
    private String reserved7;
    private List<Object> reserved4;
    private String name;
    private String mobile;
    private String mobileNo;
    private String event;
    private String applSeq;
    /**
     * dataTyp : 01
     * source : 2
     * content : ["1","2","3","4","5","6"]
     */
    private String dataTyp;
    private String source;
    private List<Object> content;
    private String remark1;
    private String remark2;//采集节点占用
    private String dataDt;//数据采集时间
    private String sysid;//数据来源系统
    private String remark4;//手机操作系统
    private String remark5;//APP版本
    private String custName;
    private List<Object> riskList;//风险数据列表

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String getDataDt() {
        return dataDt;
    }

    public void setDataDt(String dataDt) {
        this.dataDt = dataDt;
    }

    public String getSysid() {
        return sysid;
    }

    public void setSysid(String sysid) {
        this.sysid = sysid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Object> getReserved3() {
        return reserved3;
    }

    public void setReserved3(List<Object> reserved3) {
        this.reserved3 = reserved3;
    }

    public List<Object> getReserved2() {
        return reserved2;
    }

    public void setReserved2(List<Object> reserved2) {
        this.reserved2 = reserved2;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = RSAUtils.encryptByRSA(reserved1);
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getReserved5() {
        return reserved5;
    }

    public void setReserved5(String reserved5) {
        this.reserved5 = reserved5;
    }

    public String getReserved6() {
        return reserved6;
    }

    public void setReserved6(String reserved6) {
        this.reserved6 = reserved6;
    }

    public String getReserved7() {
        return reserved7;
    }

    public void setReserved7(String reserved7) {
        this.reserved7 = reserved7;
    }

    public List<Object> getReserved4() {
        return reserved4;
    }

    public void setReserved4(List<Object> reserved4) {
        this.reserved4 = reserved4;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getApplSeq() {
        return applSeq;
    }

    public void setApplSeq(String applSeq) {
        this.applSeq = applSeq;
    }

    public String getDataTyp() {
        return dataTyp;
    }

    public void setDataTyp(String dataTyp) {
        this.dataTyp = dataTyp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Object> getContent() {
        return content;
    }

    public void setContent(List<Object> content) {
        this.content = content;
    }

    public String getRemark1() {
        return remark1;
    }

    public String getRemark4() {
        return remark4;
    }

    public void setRemark4(String remark4) {
        this.remark4 = remark4;
    }

    public String getRemark5() {
        return remark5;
    }

    public void setRemark5(String remark5) {
        this.remark5 = remark5;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }


    public List<Object> getRiskList() {
        return riskList;
    }
    public void setRiskList(List<Object> riskList) {
        this.riskList = riskList;
    }
}
