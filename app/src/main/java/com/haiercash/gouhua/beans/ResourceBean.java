package com.haiercash.gouhua.beans;

import java.util.List;

/**
 * 资源位bean
 */
public class ResourceBean {
    private String groupId;//客群
    /**
     * X0-平台运营
     * X1-萨摩耶
     * X2-互联网自营
     */
    private String biz;//业务线
    /**
     * ghAPP-够花APP
     * bzH5-H5
     * xjAPP-消金APP
     * ghXcx-够花小程序
     * xjXcx-消金小程序
     * ghGzh-够花公众号
     * xjGzh-消金公众号
     */
    private String carrier;//载体
    /**
     * OPEN-开屏页
     * INDEX-首页
     * CREATERESULT-额度申请结果页
     * LOANRESULT-借款申请结果页
     * REPAYRESULT-还款结果页
     */
    private String page;//页面
    /**
     * 位置：入参 BANNER/TOPIC
     * 首页：（banner/主题）  BANNER/TOPIC
     * 其他页面：（banner） BANNER
     */
    private String module;//模块
    private String resourceName;//资源位名称
    private String level;//优先级
    private String validStartDt;//生效时间
    private String validEndDt;//失效时间
    private String cid;//活动id
    private String jumpType;//跳转方式,page ： 页面;pup  ： 半弹窗
    private List<ContentBean> contents;//内容列表

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getBiz() {
        return biz;
    }

    public void setBiz(String biz) {
        this.biz = biz;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getValidStartDt() {
        return validStartDt;
    }

    public void setValidStartDt(String validStartDt) {
        this.validStartDt = validStartDt;
    }

    public String getValidEndDt() {
        return validEndDt;
    }

    public void setValidEndDt(String validEndDt) {
        this.validEndDt = validEndDt;
    }

    public List<ContentBean> getContents() {
        return contents;
    }

    public void setContents(List<ContentBean> contents) {
        this.contents = contents;
    }

    /**
     * 资源详细位
     */
    public static class ContentBean {
        private String order;//组内顺序
        private String title;//标题
        private String desc;//描述
        private String picUrl;//图片URL
        private String h5Url;//H5URL
        private String picSize;//B1   [B1-1242*2208 B2-1242*2688] （开屏页有该字段）

        public String getPicSize() {
            return picSize;
        }

        public void setPicSize(String picSize) {
            this.picSize = picSize;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getH5Url() {
            return h5Url;
        }

        public void setH5Url(String h5Url) {
            this.h5Url = h5Url;
        }
    }
}
