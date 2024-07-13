package com.haiercash.gouhua.beans;

import java.util.List;
import java.util.Map;

public class CalculationBean {
    private String totalMoney;//还款总金额
    private String applyAmt;//本金
    private String totalInterest;//总利息
    private String perNo;//期数
    private String repayWay;//还款方式
    private List<Map<String, String>> mapList;//详情

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getApplyAmt() {
        return applyAmt;
    }

    public void setApplyAmt(String applyAmt) {
        this.applyAmt = applyAmt;
    }

    public String getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(String totalInterest) {
        this.totalInterest = totalInterest;
    }

    public String getPerNo() {
        return perNo;
    }

    public void setPerNo(String perNo) {
        this.perNo = perNo;
    }

    public String getRepayWay() {
        return repayWay;
    }

    public void setRepayWay(String repayWay) {
        this.repayWay = repayWay;
    }

    public List<Map<String, String>> getMapList() {
        return mapList;
    }

    public void setMapList(List<Map<String, String>> mapList) {
        this.mapList = mapList;
    }
}
