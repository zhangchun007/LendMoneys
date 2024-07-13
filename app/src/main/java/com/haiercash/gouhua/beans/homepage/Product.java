package com.haiercash.gouhua.beans.homepage;

import com.haiercash.gouhua.beans.borrowmoney.LoanRatAndProduct;

import java.io.Serializable;
import java.util.List;

/**
 * 期数列表
 */
public class Product implements Serializable {
    private List<LoanRatAndProduct> tnrOptList;

    public List<LoanRatAndProduct> getTnrOptList() {
        return tnrOptList;
    }

    public void setTnrOptList(List<LoanRatAndProduct> tnrOptList) {
        this.tnrOptList = tnrOptList;
    }

    public Product(List<LoanRatAndProduct> tnrOptList) {
        this.tnrOptList = tnrOptList;
    }

    public Product() {
    }
}
