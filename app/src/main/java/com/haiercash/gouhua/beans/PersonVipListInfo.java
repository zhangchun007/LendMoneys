package com.haiercash.gouhua.beans;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 3/20/23
 * @Version: 1.0
 */
public class PersonVipListInfo {
    //图片url
    private String imgUrl;
    //客群
    private String groupId;
    //资源位名称
    private String name;
    //
    private String code;
    //跳转url
    private String forwardUrl;
    //活动id
    private String cid;
    private String jumpType;//跳转方式,page ： 页面;pup  ： 半弹窗

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getForwardUrl() {
        return forwardUrl;
    }

    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }
}
