package com.haiercash.gouhua.beans.homepage;

import java.util.HashMap;

/**
 * 首页获取的h5跳转地址
 * checkNodeStep目前用不到
 */
public class HomeH5Data {
    private String h5token;  //h5的token
    private String nodeJumpUrl;  //申额跳转的url
    private String loanJumpUrl; // 支用url
    private String repayJumpUrlD;//七日待还链接
    private String repayJumpUrlM;//本月待还链接

    private String repayJumpUrl; //还款入口url

    private String repayJumpUrlY;//逾期待还款url

    @Override
    public String toString() {
        return "HomeH5Data{" +
                "h5token='" + h5token + '\'' +
                ", nodeJumpUrl='" + nodeJumpUrl + '\'' +
                ", loanJumpUrl='" + loanJumpUrl + '\'' +
                ", repayJumpUrlD='" + repayJumpUrlD + '\'' +
                ", repayJumpUrlM='" + repayJumpUrlM + '\'' +
                ", repayJumpUrl='" + repayJumpUrl + '\'' +
                ", repayJumpUrlY='" + repayJumpUrlY + '\'' +
                ", repayJumpUrlA='" + repayJumpUrlA + '\'' +
                ", processId='" + processId + '\'' +
                ", checkNodeStep=" + checkNodeStep +
                ", h5CustLoginInfo=" + h5CustLoginInfo +
                '}';
    }

    public String getRepayJumpUrlY() {
        return repayJumpUrlY;
    }

    public void setRepayJumpUrlY(String repayJumpUrlY) {
        this.repayJumpUrlY = repayJumpUrlY;
    }

    public HomeH5Data(String h5token, String nodeJumpUrl, String loanJumpUrl, String repayJumpUrlD, String repayJumpUrlM, String repayJumpUrl, String repayJumpUrlY, String repayJumpUrlA, String processId, HashMap<String, String> checkNodeStep, HashMap<String, Object> h5CustLoginInfo) {
        this.h5token = h5token;
        this.nodeJumpUrl = nodeJumpUrl;
        this.loanJumpUrl = loanJumpUrl;
        this.repayJumpUrlD = repayJumpUrlD;
        this.repayJumpUrlM = repayJumpUrlM;
        this.repayJumpUrl = repayJumpUrl;
        this.repayJumpUrlY = repayJumpUrlY;
        this.repayJumpUrlA = repayJumpUrlA;
        this.processId = processId;
        this.checkNodeStep = checkNodeStep;
        this.h5CustLoginInfo = h5CustLoginInfo;
    }

    public String getRepayJumpUrlD() {
        return repayJumpUrlD;
    }

    public void setRepayJumpUrlD(String repayJumpUrlD) {
        this.repayJumpUrlD = repayJumpUrlD;
    }

    public String getRepayJumpUrlM() {
        return repayJumpUrlM;
    }

    public void setRepayJumpUrlM(String repayJumpUrlM) {
        this.repayJumpUrlM = repayJumpUrlM;
    }

    public String getRepayJumpUrlA() {
        return repayJumpUrlA;
    }

    public void setRepayJumpUrlA(String repayJumpUrlA) {
        this.repayJumpUrlA = repayJumpUrlA;
    }

    public HomeH5Data(String h5token, String nodeJumpUrl, String loanJumpUrl, String repayJumpUrl, String repayJumpUrlD, String repayJumpUrlM, String repayJumpUrlA, String processId, HashMap<String, String> checkNodeStep, HashMap<String, Object> h5CustLoginInfo) {
        this.h5token = h5token;
        this.nodeJumpUrl = nodeJumpUrl;
        this.loanJumpUrl = loanJumpUrl;
        this.repayJumpUrl = repayJumpUrl;
        this.repayJumpUrlD = repayJumpUrlD;
        this.repayJumpUrlM = repayJumpUrlM;
        this.repayJumpUrlA = repayJumpUrlA;
        this.processId = processId;
        this.checkNodeStep = checkNodeStep;
        this.h5CustLoginInfo = h5CustLoginInfo;
    }

    private String repayJumpUrlA;//全部待还链接


    public String getLoanJumpUrl() {
        return loanJumpUrl;
    }

    public void setLoanJumpUrl(String loanJumpUrl) {
        this.loanJumpUrl = loanJumpUrl;
    }

    public String getRepayJumpUrl() {
        return repayJumpUrl;
    }

    public void setRepayJumpUrl(String repayJumpUrl) {
        this.repayJumpUrl = repayJumpUrl;
    }

    public HomeH5Data(String h5token, String nodeJumpUrl, String loanJumpUrl, String repayJumpUrl, String processId, HashMap<String, String> checkNodeStep, HashMap<String, Object> h5CustLoginInfo) {
        this.h5token = h5token;
        this.nodeJumpUrl = nodeJumpUrl;
        this.loanJumpUrl = loanJumpUrl;
        this.repayJumpUrl = repayJumpUrl;
        this.processId = processId;
        this.checkNodeStep = checkNodeStep;
        this.h5CustLoginInfo = h5CustLoginInfo;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    private String processId;   //流程id
    private HashMap<String, String> checkNodeStep;  //没用上

    public HashMap<String, Object> getH5CustLoginInfo() {
        return h5CustLoginInfo;
    }

    public void setH5CustLoginInfo(HashMap<String, Object> h5CustLoginInfo) {
        this.h5CustLoginInfo = h5CustLoginInfo;
    }

    private HashMap<String, Object> h5CustLoginInfo;//h5的信息

    public void setH5token(String h5token) {
        this.h5token = h5token;
    }

    public String getH5token() {
        return h5token;
    }

    public void setNodeJumpUrl(String nodeJumpUrl) {
        this.nodeJumpUrl = nodeJumpUrl;
    }

    public String getNodeJumpUrl() {
        return nodeJumpUrl;
    }

    public void setCheckNodeStep(HashMap<String, String> checkNodeStep) {
        this.checkNodeStep = checkNodeStep;
    }

    public HashMap<String, String> getCheckNodeStep() {
        return checkNodeStep;
    }

}
