package com.haiercash.gouhua.beans.unity;

import com.haiercash.gouhua.beans.homepage.HomeRepayBean;

import java.io.Serializable;
import java.util.List;

public class PersonalRepayBean implements Serializable {
    private List<HomeRepayBean> repayList;//额度卡片还款列表

    public PersonalRepayBean() {
    }

    public PersonalRepayBean(List<HomeRepayBean> repayList) {
        this.repayList = repayList;
    }

    public List<HomeRepayBean> getRepayList() {
        return repayList;
    }

    public void setRepayList(List<HomeRepayBean> repayList) {
        this.repayList = repayList;
    }
}
