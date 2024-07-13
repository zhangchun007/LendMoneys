package com.haiercash.gouhua.beans.homepage;

import com.haiercash.gouhua.adaptor.bean.LoanMarketBean;
import com.haiercash.gouhua.adaptor.bean.ScenePopupBean;
import com.haiercash.gouhua.beans.CreditLifeBorrowBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页额度接口返回的信息
 */
public class HomeQuotaBean implements Serializable {
    private Credit credit; //额度类信息Map
    private Product product;  //产品类信息
    private List<CreditLifeBorrowBean> loanMarket;  //贷超信息

    private ArrayList<LoanMarketBean> loanExcess;  //贷超信息

    private ScenePopupBean sceneOrderPopup;

    public GzxData getGzxData() {
        return gzxData;
    }

    public void setGzxData(GzxData gzxData) {
        this.gzxData = gzxData;
    }

    public HomeQuotaBean(Credit credit, Product product, List<CreditLifeBorrowBean> loanMarket, GzxData gzxData, List<ResidentRibbon> residentRibbon, CustomerInfo customerInfo, int showFloatingWindow, FloatingWindow floatingWindow, String isInList, String creditIncreaseSwitch, HomeH5Data initH5HomeConfigure) {
        this.credit = credit;
        this.product = product;
        this.loanMarket = loanMarket;
        this.gzxData = gzxData;
        this.residentRibbon = residentRibbon;
        this.customerInfo = customerInfo;
        this.showFloatingWindow = showFloatingWindow;
        this.floatingWindow = floatingWindow;
        this.isInList = isInList;
        this.creditIncreaseSwitch = creditIncreaseSwitch;
        this.initH5HomeConfigure = initH5HomeConfigure;
    }

    private GzxData gzxData; //够智选导流数据
    private List<ResidentRibbon> residentRibbon; //金刚区
    private CustomerInfo customerInfo;  //用户实名信息
    private int showFloatingWindow; //是否展示浮窗1是0否
    private FloatingWindow floatingWindow; //浮窗数据
    private String isInList; //是否在贷超白名单是false否
    private String creditIncreaseSwitch;  //提额入口开关StringON打开OFF关闭
    private HomeH5Data initH5HomeConfigure;//给app上的H5的参数Map给app上的H5的参数

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ArrayList<LoanMarketBean> getLoanExcess() {
        return loanExcess;
    }

    public void setLoanExcess(ArrayList<LoanMarketBean> loanExcess) {
        this.loanExcess = loanExcess;
    }

    public ScenePopupBean getSceneOrderPopup() {
        return sceneOrderPopup;
    }

    public void setSceneOrderPopup(ScenePopupBean sceneOrderPopup) {
        this.sceneOrderPopup = sceneOrderPopup;
    }

    public List<CreditLifeBorrowBean> getLoanMarket() {
        return loanMarket;
    }

    public void setLoanMarket(List<CreditLifeBorrowBean> loanMarket) {
        this.loanMarket = loanMarket;
    }

    public List<ResidentRibbon> getResidentRibbon() {
        return residentRibbon;
    }

    public void setResidentRibbon(List<ResidentRibbon> residentRibbon) {
        this.residentRibbon = residentRibbon;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public int getShowFloatingWindow() {
        return showFloatingWindow;
    }

    public void setShowFloatingWindow(int showFloatingWindow) {
        this.showFloatingWindow = showFloatingWindow;
    }

    public FloatingWindow getFloatingWindow() {
        return floatingWindow;
    }

    public void setFloatingWindow(FloatingWindow floatingWindow) {
        this.floatingWindow = floatingWindow;
    }

    public String getIsInList() {
        return isInList;
    }

    public void setIsInList(String isInList) {
        this.isInList = isInList;
    }

    public String getCreditIncreaseSwitch() {
        return creditIncreaseSwitch;
    }

    public void setCreditIncreaseSwitch(String creditIncreaseSwitch) {
        this.creditIncreaseSwitch = creditIncreaseSwitch;
    }

    public HomeH5Data getInitH5HomeConfigure() {
        return initH5HomeConfigure;
    }

    public void setInitH5HomeConfigure(HomeH5Data initH5HomeConfigure) {
        this.initH5HomeConfigure = initH5HomeConfigure;
    }

    public HomeQuotaBean(Credit credit, Product product, List<CreditLifeBorrowBean> loanMarket, List<ResidentRibbon> residentRibbon, CustomerInfo customerInfo, int showFloatingWindow, FloatingWindow floatingWindow, String isInList, String creditIncreaseSwitch, HomeH5Data initH5HomeConfigure) {
        this.credit = credit;
        this.product = product;
        this.loanMarket = loanMarket;
        this.residentRibbon = residentRibbon;
        this.customerInfo = customerInfo;
        this.showFloatingWindow = showFloatingWindow;
        this.floatingWindow = floatingWindow;
        this.isInList = isInList;
        this.creditIncreaseSwitch = creditIncreaseSwitch;
        this.initH5HomeConfigure = initH5HomeConfigure;
    }

    public HomeQuotaBean() {
    }
}
