package com.haiercash.gouhua.beans.borrowmoney;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 9/23/22
 * @Version: 1.0
 */
public class LoanRatAndProduct implements Serializable {
    //期数
    private String tnrOpt;
    //产品
    private List<LoanRat> products;

    public String getTnrOpt() {
        return tnrOpt;
    }

    public void setTnrOpt(String tnrOpt) {
        this.tnrOpt = tnrOpt;
    }

    public List<LoanRat> getProducts() {
        return products;
    }

    public void setProducts(List<LoanRat> products) {
        this.products = products;
    }
}
