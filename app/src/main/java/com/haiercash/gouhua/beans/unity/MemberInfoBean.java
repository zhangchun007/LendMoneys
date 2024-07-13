package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/28
 * @Version: 1.0
 */
public class MemberInfoBean implements Serializable {
    private String memberResourceShow;
    private String hyOpenState;
    private List<MenberPicsBean> memberPics;

    public String getMemberResourceShow() {
        return memberResourceShow;
    }

    public void setMemberResourceShow(String memberResourceShow) {
        this.memberResourceShow = memberResourceShow;
    }

    public String getHyOpenState() {
        return hyOpenState;
    }

    public void setHyOpenState(String hyOpenState) {
        this.hyOpenState = hyOpenState;
    }

    public List<MenberPicsBean> getMemberPics() {
        return memberPics;
    }

    public void setMemberPics(List<MenberPicsBean> memberPics) {
        this.memberPics = memberPics;
    }
}
