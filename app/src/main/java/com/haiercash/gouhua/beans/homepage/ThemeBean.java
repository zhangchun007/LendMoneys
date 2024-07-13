package com.haiercash.gouhua.beans.homepage;

import java.util.List;

public class ThemeBean {

    private List<ImageLinkBean> theme;
    private Credit credit;

    public void setTheme(List<ImageLinkBean> theme) {
        this.theme = theme;
    }

    public List<ImageLinkBean> getTheme() {
        return theme;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public Credit getCredit() {
        return credit;
    }
}
