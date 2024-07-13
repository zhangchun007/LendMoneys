package com.haiercash.gouhua.beans.homepage;

import java.io.Serializable;
import java.util.List;

/**
 * 首页额度模块数据
 */
public class Credit implements Serializable {

    private String speSeq;//流水号 String额度编号
    private String status;//额度状态 String真实的额度状态，A1等批注[zw1]:
    private String oldStatus; //老的额度状态 A1-----H3
    private String userStatus;//用户额度状态 String用户额度状态
    private MainCardBean main;//主卡信息 Map
    private String typCde;//贷款品种 String贷款品种
    private String typLvlCde;//贷款品种小类 String贷款品种小类
    private String availLimit;//剩余可支用额度 String总额度
    private String totalLimit;//总额度 String剩余可支用额度
    private String minAmt;//最小可借额度 String最小可用额度
    private String hyOpenState;//会员标识 StringY开通，N未开通
    private String applSeq;//申额流水号String在H1时会返回
    private String creditProcessTitle;//申额进度条标题 String
    private List<CreditProcess> creditProcess;//申额进度条<Map>


    private String thirdTitle;//  助贷新增字段title
    private String thirdLogo;//  助贷新增logo地址

    private String thirdTitleUMeng;//  助贷新增字段title---埋点用

    private List<HomeRepayBean> cardRepayList;//额度卡片还款列表
    private List<HomeRepayBean> kumGangRepayList;//金刚区还款列表

    public String getThirdTitle() {
        return thirdTitle;
    }

    public void setThirdTitle(String thirdTitle) {
        this.thirdTitle = thirdTitle;
    }

    public String getThirdLogo() {
        return thirdLogo;
    }

    public void setThirdLogo(String thirdLogo) {
        this.thirdLogo = thirdLogo;
    }

    public List<HomeRepayBean> getCardRepayList() {
        return cardRepayList;
    }

    public void setCardRepayList(List<HomeRepayBean> cardRepayList) {
        this.cardRepayList = cardRepayList;
    }

    public List<HomeRepayBean> getKumGangRepayList() {
        return kumGangRepayList;
    }

    public void setKumGangRepayList(List<HomeRepayBean> kumGangRepayList) {
        this.kumGangRepayList = kumGangRepayList;
    }

    public Credit(String speSeq, String status, String oldStatus, String userStatus, MainCardBean main, String typCde, String typLvlCde, String availLimit, String totalLimit, String minAmt, String hyOpenState, String applSeq, String creditProcessTitle, List<CreditProcess> creditProcess, String thirdTitle, String thirdLogo, String thirdTitleUMeng, List<HomeRepayBean> cardRepayList, List<HomeRepayBean> kumGangRepayList) {
        this.speSeq = speSeq;
        this.status = status;
        this.oldStatus = oldStatus;
        this.userStatus = userStatus;
        this.main = main;
        this.typCde = typCde;
        this.typLvlCde = typLvlCde;
        this.availLimit = availLimit;
        this.totalLimit = totalLimit;
        this.minAmt = minAmt;
        this.hyOpenState = hyOpenState;
        this.applSeq = applSeq;
        this.creditProcessTitle = creditProcessTitle;
        this.creditProcess = creditProcess;
        this.thirdTitle = thirdTitle;
        this.thirdLogo = thirdLogo;
        this.thirdTitleUMeng = thirdTitleUMeng;
        this.cardRepayList = cardRepayList;
        this.kumGangRepayList = kumGangRepayList;
    }

    public String getSpeSeq() {
        return speSeq;
    }

    public void setSpeSeq(String speSeq) {
        this.speSeq = speSeq;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public MainCardBean getMain() {
        return main;
    }

    public void setMain(MainCardBean main) {
        this.main = main;
    }

    public String getTypCde() {
        return typCde;
    }

    public void setTypCde(String typCde) {
        this.typCde = typCde;
    }

    public String getTypLvlCde() {
        return typLvlCde;
    }

    public void setTypLvlCde(String typLvlCde) {
        this.typLvlCde = typLvlCde;
    }

    public String getAvailLimit() {
        return availLimit;
    }

    public void setAvailLimit(String availLimit) {
        this.availLimit = availLimit;
    }

    public String getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(String totalLimit) {
        this.totalLimit = totalLimit;
    }

    public String getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(String minAmt) {
        this.minAmt = minAmt;
    }

    public String getHyOpenState() {
        return hyOpenState;
    }

    public void setHyOpenState(String hyOpenState) {
        this.hyOpenState = hyOpenState;
    }

    public String getApplSeq() {
        return applSeq;
    }

    public void setApplSeq(String applSeq) {
        this.applSeq = applSeq;
    }

    public String getCreditProcessTitle() {
        return creditProcessTitle;
    }

    public void setCreditProcessTitle(String creditProcessTitle) {
        this.creditProcessTitle = creditProcessTitle;
    }

    public List<CreditProcess> getCreditProcess() {
        return creditProcess;
    }

    public void setCreditProcess(List<CreditProcess> creditProcess) {
        this.creditProcess = creditProcess;
    }

    public Credit(String speSeq, String status, String userStatus, MainCardBean main, String typCde, String typLvlCde, String availLimit, String totalLimit, String minAmt, String hyOpenState, String applSeq, String creditProcessTitle, List<CreditProcess> creditProcess) {
        this.speSeq = speSeq;
        this.status = status;
        this.userStatus = userStatus;
        this.main = main;
        this.typCde = typCde;
        this.typLvlCde = typLvlCde;
        this.availLimit = availLimit;
        this.totalLimit = totalLimit;
        this.minAmt = minAmt;
        this.hyOpenState = hyOpenState;
        this.applSeq = applSeq;
        this.creditProcessTitle = creditProcessTitle;
        this.creditProcess = creditProcess;
    }

    public String getThirdTitleUMeng() {
        return thirdTitleUMeng;
    }

    public void setThirdTitleUMeng(String thirdTitleUMeng) {
        this.thirdTitleUMeng = thirdTitleUMeng;
    }

    public Credit() {
    }
}