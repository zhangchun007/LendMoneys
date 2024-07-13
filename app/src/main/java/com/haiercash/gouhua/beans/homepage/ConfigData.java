package com.haiercash.gouhua.beans.homepage;

import java.io.Serializable;

/**
 * 配置详情
 */
public class ConfigData implements Serializable {
    private String imgUrl;
    private String groupId;
    private String name;
    private String code;
    private String forwardUrl;
    private String cid;
    private String jumpType;
    private String title;
    private String summary;
    private String effectiveTime;
    private String id;
    private String contentText;

    //以下仅供首页通知使用
    private String recordId;//消息请求表ID
    private String receiver;//接收人
    private String content;//站内信是否存在副文本标识（待调整）
    private String tmplId;//模板ID
    private String sendChannel;//发送渠道
    private String channelAccount;//触达渠道发送账号
    private String subType;//发送渠道子类型
    private String status;//发送状态
    private String readStatus;//阅读状态 Y 已读
    private String responseCode;//    请求三方响应码值
    private String responseDesc;//    请求三方响应描述
    private String institution;//请求系统
    private String createBy;//创建人
    private String createTime; // 创建时间
    private String updateTime; //更新时间
    private String callbackStatus;  //回调处理状态
    private String inmailType; //站内信类型
    private String tmplTitle;//    模板标题
    private String appType;//载体
    private String pushTitle;//  标题
    private String pushSubTitle;    //副标题
    private String pushContentType;// 消息类型详情 1、自定义内容：CustomContent2、H5链接跳转：H5Link3、APP申额：EduApply4、APP支用：LoanApply5、APP近七日待还：RepayBill6、APP首页：HomePage
    private String h5Url;// H5链接跳转模式
    private String jumpUrl;//    跳转链接   Server处理后的跳转链接 pushContentType非 自定义内容、APP首页 时返回，H5链接跳转时链接不处理直接返回

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTmplId() {
        return tmplId;
    }

    public void setTmplId(String tmplId) {
        this.tmplId = tmplId;
    }

    public String getSendChannel() {
        return sendChannel;
    }

    public void setSendChannel(String sendChannel) {
        this.sendChannel = sendChannel;
    }

    public String getChannelAccount() {
        return channelAccount;
    }

    public void setChannelAccount(String channelAccount) {
        this.channelAccount = channelAccount;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDesc() {
        return responseDesc;
    }

    public void setResponseDesc(String responseDesc) {
        this.responseDesc = responseDesc;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCallbackStatus() {
        return callbackStatus;
    }

    public void setCallbackStatus(String callbackStatus) {
        this.callbackStatus = callbackStatus;
    }

    public String getInmailType() {
        return inmailType;
    }

    public void setInmailType(String inmailType) {
        this.inmailType = inmailType;
    }

    public String getTmplTitle() {
        return tmplTitle;
    }

    public void setTmplTitle(String tmplTitle) {
        this.tmplTitle = tmplTitle;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getPushTitle() {
        return pushTitle;
    }

    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    public String getPushSubTitle() {
        return pushSubTitle;
    }

    public void setPushSubTitle(String pushSubTitle) {
        this.pushSubTitle = pushSubTitle;
    }

    public String getPushContentType() {
        return pushContentType;
    }

    public void setPushContentType(String pushContentType) {
        this.pushContentType = pushContentType;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public ConfigData(String imgUrl, String groupId, String name, String code, String forwardUrl, String cid, String jumpType, String title, String summary, String effectiveTime, String id, String contentText) {
        this.imgUrl = imgUrl;
        this.groupId = groupId;
        this.name = name;
        this.code = code;
        this.forwardUrl = forwardUrl;
        this.cid = cid;
        this.jumpType = jumpType;
        this.title = title;
        this.summary = summary;
        this.effectiveTime = effectiveTime;
        this.id = id;
        this.contentText = contentText;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ConfigData(String imgUrl, String groupId, String name, String code, String forwardUrl, String cid, String jumpType, String title, String summary, String effectiveTime, String id) {
        this.imgUrl = imgUrl;
        this.groupId = groupId;
        this.name = name;
        this.code = code;
        this.forwardUrl = forwardUrl;
        this.cid = cid;
        this.jumpType = jumpType;
        this.title = title;
        this.summary = summary;
        this.effectiveTime = effectiveTime;
        this.id = id;
    }

    public ConfigData() {
    }
}
