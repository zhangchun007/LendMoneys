package com.haiercash.gouhua.beans;

import com.haiercash.gouhua.beans.getpayss.LoanCoupon;

import java.util.List;

/**
 * 整合弹窗查询结果bean
 */
public class PopDialogNewBean {
    private PopDialogBean oldPopupResp;//旧弹窗数据
    private String popupId;//弹窗ID
    private String templateId;//模板Id
    private String popupType;//弹窗类型	1.营销类2.非营销类
    private String popupName;//弹窗名称
    private String effectTime;//生效时间	yyyy-MM-dd hh:mm:ss
    private String unEffectTime;//失效时间	yyyy-MM-dd hh:mm:ss
    private String effectCarrier;//生效载体	载体的枚举值
    private String appVersion;//APP版本
    private String touchPage;//触发页面
    private String touchNode;//页面触发节点
    private String priorityLevel;//优先级	优先级1是最高。1~99
    private String popupTotalNum;//弹窗总次数	-1代表不限次数
    private String touchPeriod;//触发时间间隔	每隔多长时间弹出一次（秒）
    private String groupList;//客群/对象
    private String equityType;//权益类型
    private String batchNo;//模板号/批次号
    private String templateName;//模板名称
    /*
    模板类型
    PopupA-图片+一个关闭按钮
    PopupB-图片+俩按钮
    PopupC-图片+免息券数据+横向俩按钮
    PopupD-图片+免息券数据+纵向俩按钮+第一个按钮倒计时
    PopupE-图片固定+两个动参
    PopupF-图片固定+两个动参
     */
    private String templateType;
    private String dynamicContentOne;//弹窗类型5.6 内容一
    private String dynamicContentTwo;//弹窗类型5.6 内容二
    private String contentImageUrl;//图片地址
    private String routeType;//路由类型
    private String routeAddress;//跳转链接（H5或者原生都要支持）
    private List<ButtonInfo> buttonList;//按钮信息
    private String cancelIcon;//取消图标icon
    private String buttonLayout;//按钮布局
    private String copyWriteColor;//文案颜色
    private String controlTimeLimit;//控件时间限制	以秒为单位
    private LoanCoupon couponInfo;//权益信息（最近一张可用免息券），模板3或者4会返回

    private String popupDelayTime;//弹窗延迟显示时间

    public String getPopupDelayTime() {
        return popupDelayTime;
    }

    public void setPopupDelayTime(String popupDelayTime) {
        this.popupDelayTime = popupDelayTime;
    }
    public String getDynamicContentOne() {
        return dynamicContentOne;
    }

    public void setDynamicContentOne(String dynamicContentOne) {
        this.dynamicContentOne = dynamicContentOne;
    }

    public String getDynamicContentTwo() {
        return dynamicContentTwo;
    }

    public void setDynamicContentTwo(String dynamicContentTwo) {
        this.dynamicContentTwo = dynamicContentTwo;
    }

    public LoanCoupon getCouponInfo() {
        return couponInfo;
    }

    public void setCouponInfo(LoanCoupon couponInfo) {
        this.couponInfo = couponInfo;
    }

    public List<ButtonInfo> getButtonList() {
        return buttonList;
    }

    public void setButtonList(List<ButtonInfo> buttonList) {
        this.buttonList = buttonList;
    }

    public PopDialogBean getOldPopupResp() {
        return oldPopupResp;
    }

    public void setOldPopupResp(PopDialogBean oldPopupResp) {
        this.oldPopupResp = oldPopupResp;
    }

    public String getPopupId() {
        return popupId;
    }

    public void setPopupId(String popupId) {
        this.popupId = popupId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getPopupType() {
        return popupType;
    }

    public void setPopupType(String popupType) {
        this.popupType = popupType;
    }

    public String getPopupName() {
        return popupName;
    }

    public void setPopupName(String popupName) {
        this.popupName = popupName;
    }

    public String getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(String effectTime) {
        this.effectTime = effectTime;
    }

    public String getUnEffectTime() {
        return unEffectTime;
    }

    public void setUnEffectTime(String unEffectTime) {
        this.unEffectTime = unEffectTime;
    }

    public String getEffectCarrier() {
        return effectCarrier;
    }

    public void setEffectCarrier(String effectCarrier) {
        this.effectCarrier = effectCarrier;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getTouchPage() {
        return touchPage;
    }

    public void setTouchPage(String touchPage) {
        this.touchPage = touchPage;
    }

    public String getTouchNode() {
        return touchNode;
    }

    public void setTouchNode(String touchNode) {
        this.touchNode = touchNode;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getPopupTotalNum() {
        return popupTotalNum;
    }

    public void setPopupTotalNum(String popupTotalNum) {
        this.popupTotalNum = popupTotalNum;
    }

    public String getTouchPeriod() {
        return touchPeriod;
    }

    public void setTouchPeriod(String touchPeriod) {
        this.touchPeriod = touchPeriod;
    }

    public String getGroupList() {
        return groupList;
    }

    public void setGroupList(String groupList) {
        this.groupList = groupList;
    }

    public String getEquityType() {
        return equityType;
    }

    public void setEquityType(String equityType) {
        this.equityType = equityType;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getContentImageUrl() {
        return contentImageUrl;
    }

    public void setContentImageUrl(String contentImageUrl) {
        this.contentImageUrl = contentImageUrl;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getRouteAddress() {
        return routeAddress;
    }

    public void setRouteAddress(String routeAddress) {
        this.routeAddress = routeAddress;
    }

    public String getCancelIcon() {
        return cancelIcon;
    }

    public void setCancelIcon(String cancelIcon) {
        this.cancelIcon = cancelIcon;
    }

    public String getButtonLayout() {
        return buttonLayout;
    }

    public void setButtonLayout(String buttonLayout) {
        this.buttonLayout = buttonLayout;
    }

    public String getCopyWriteColor() {
        return copyWriteColor;
    }

    public void setCopyWriteColor(String copyWriteColor) {
        this.copyWriteColor = copyWriteColor;
    }

    public String getControlTimeLimit() {
        return controlTimeLimit;
    }

    public void setControlTimeLimit(String controlTimeLimit) {
        this.controlTimeLimit = controlTimeLimit;
    }

    /**
     * 按钮信息
     */
    public static class ButtonInfo {
        private String buttonLocation;//按钮位置
        private String copyWriteName;//文案
        private String copyWriteColor;//文案颜色
        private String imageUrl;//图片
        private String routeAddress;//路由地址
        private String routeType;//路由类型
        private String controlTimeLimit;//控件时间限制	以秒为单位
        private String controlTimeRoute;//倒计时结束后自动跳转路由

        public String getControlTimeRoute() {
            return controlTimeRoute;
        }

        public void setControlTimeRoute(String controlTimeRoute) {
            this.controlTimeRoute = controlTimeRoute;
        }

        public String getCopyWriteName() {
            return copyWriteName;
        }

        public void setCopyWriteName(String copyWriteName) {
            this.copyWriteName = copyWriteName;
        }

        public String getCopyWriteColor() {
            return copyWriteColor;
        }

        public void setCopyWriteColor(String copyWriteColor) {
            this.copyWriteColor = copyWriteColor;
        }

        public String getButtonLocation() {
            return buttonLocation;
        }

        public void setButtonLocation(String buttonLocation) {
            this.buttonLocation = buttonLocation;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getRouteAddress() {
            return routeAddress;
        }

        public void setRouteAddress(String routeAddress) {
            this.routeAddress = routeAddress;
        }

        public String getRouteType() {
            return routeType;
        }

        public void setRouteType(String routeType) {
            this.routeType = routeType;
        }

        public String getControlTimeLimit() {
            return controlTimeLimit;
        }

        public void setControlTimeLimit(String controlTimeLimit) {
            this.controlTimeLimit = controlTimeLimit;
        }
    }
}
