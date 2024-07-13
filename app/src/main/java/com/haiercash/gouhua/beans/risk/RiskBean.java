package com.haiercash.gouhua.beans.risk;

import com.app.haiercash.base.CommonManager;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;

import java.io.Serializable;
import java.util.List;

public class RiskBean implements Serializable {
    private String batch_id;   //	批次号id
    private String user_id;    //	user_id
    private String mobile; //	mobile
    private String id_no;  //	id_no(实名用户)
    private String cust_no;//客户编号
    private String cust_name;  //	cust_name(实名用户)
    private String event_id;   //	采集节点名称
    private String create_dt;  //	采集时间
    private String appl_seq;   //	额度、支用流水号
    private String op_sys; //	操作系统 android、iOS
    private String app_name;   //	APP名称
    private String app_version;    //	APP的package编号
    private String device_id;  //	设备ID
    private String local_phone_number; //	本机手机号码

    public String getAuthorize_items() {
        return authorize_items;
    }

    private String authorize_items;//权限数据
    private String channel_no;//进件渠道
    private String channel_store;//应用市场标识（下载渠道）
    private String channel_regis;//注册渠道
    private String mq_method;//APP或者H5
    private String classFrom;//来源class
    private String messageType;//消息类型

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    private String wifi_name;//wifi名称
    private String wifi_mac;//wifi_mac
    private String ip_external;
    private String entry_label;//入口标签
    private String prod_code;//产品编码（仅对支用）

    private String ideaCode;            //创意code		否
    private String registerVector;      //注册载体		否
    private String currVector;      //当前载体		否
    private String business;            //运营业务线		否
    //private String channelNo;           //渠道号		是	如果运营业务线字段不为空，则按照运营业务线做转换，否则该字段同前端请求头中的渠道号
    //private String regisChannel;        //注册渠道		Y
    private String appDownFrom;         //投放渠道		Y

    private BrBean br_data;//百融sdk
    private String ydunToken;//网易设备指纹sdk

    public GeoBean getGeo_data() {
        return geo_data;
    }

    public void setGeo_data(GeoBean geo_data) {
        this.geo_data = geo_data;
    }

    private GeoBean geo_data;//百度数据
    private List<CallLogBean> call_log;//通话记录
    private List<Object> contact;//通讯录
    private List<Object> app_list;//AppList
    private AppPerson app_person;  //输入联系人电话时候报错

    public String getBatch_id() {
        return batch_id;
    }

    public void setBatch_id(String batch_id) {
        this.batch_id = batch_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getId_no() {
        return id_no;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
    }

    public String getCust_no() {
        return cust_no;
    }

    public void setCust_no(String cust_no) {
        this.cust_no = cust_no;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getCreate_dt() {
        return create_dt;
    }

    public void setCreate_dt(String create_dt) {
        this.create_dt = create_dt;
    }

    public String getOp_sys() {
        return op_sys;
    }

    public void setOp_sys(String op_sys) {
        this.op_sys = op_sys;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getLocal_phone_number() {
        return local_phone_number;
    }

    public void setLocal_phone_number(String local_phone_number) {
        this.local_phone_number = local_phone_number;
    }

    public String getChannel_no() {
        return channel_no;
    }

    public void setChannel_no(String channel_no) {
        this.channel_no = channel_no;
    }

    public String getChannel_store() {
        return channel_store;
    }

    public void setChannel_store(String channel_store) {
        this.channel_store = channel_store;
    }

    public String getChannel_regis() {
        return channel_regis;
    }

    public void setChannel_regis(String channel_regis) {
        this.channel_regis = channel_regis;
    }

    public String getMq_method() {
        return mq_method;
    }

    public void setMq_method(String mq_method) {
        this.mq_method = mq_method;
    }

    public String getClassFrom() {
        return classFrom;
    }

    public String getWifi_name() {
        return wifi_name;
    }

    public void setWifi_name(String wifi_name) {
        this.wifi_name = wifi_name;
    }

    public String getWifi_mac() {
        return wifi_mac;
    }

    public void setWifi_mac(String wifi_mac) {
        this.wifi_mac = wifi_mac;
    }

    public String getIp_external() {
        return ip_external;
    }

    public void setIp_external(String ip_external) {
        this.ip_external = ip_external;
    }

    public String getEntry_label() {
        return entry_label;
    }

    public void setEntry_label(String entry_label) {
        this.entry_label = entry_label;
    }

    public String getIdeaCode() {
        return ideaCode;
    }

    public void setIdeaCode(String ideaCode) {
        this.ideaCode = ideaCode;
    }

    public String getRegisterVector() {
        return registerVector;
    }

    public void setRegisterVector(String registerVector) {
        this.registerVector = registerVector;
    }

    public String getCurrVector() {
        return currVector;
    }

    public void setCurrVector(String currVector) {
        this.currVector = currVector;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getAppDownFrom() {
        return appDownFrom;
    }

    public void setAppDownFrom(String appDownFrom) {
        this.appDownFrom = appDownFrom;
    }

    public BrBean getBr_data() {
        return br_data;
    }

    public void setBr_data(BrBean br_data) {
        this.br_data = br_data;
    }

    public List<CallLogBean> getCall_log() {
        return call_log;
    }

    public void setCall_log(List<CallLogBean> call_log) {
        this.call_log = call_log;
    }

    public List<Object> getContact() {
        return contact;
    }

    public List<Object> getApp_list() {
        return app_list;
    }

    public void setApp_list(List<Object> app_list) {
        this.app_list = app_list;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    private String dataSource;//定位来源
    private String carrier;  //载体----默认GH


    public AppPerson getApp_person() {
        return app_person;
    }

    public void setApp_person(AppPerson app_person) {
        this.app_person = app_person;
    }

    public RiskBean(String eventId, String applySeq, String userId) {
        ip_external = "";
        dataSource = "baidu";
        batch_id = TimeUtil.calendarToString3() + CheckUtil.randomNumber(6);
        user_id = userId;
        mobile = EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_MOBILE));
        id_no = EncryptUtil.simpleEncrypt(SpHp.getUser(SpKey.USER_CERTNO));
        cust_no = SpHp.getUser(SpKey.USER_CUSTNO);
        cust_name = EncryptUtil.simpleEncrypt(SpHp.getUser(SpKey.USER_CUSTNAME));
        create_dt = TimeUtil.calendarToString();
        this.event_id = eventId;
        this.appl_seq = applySeq;
        op_sys = "android";
        app_name = AppApplication.CONTEXT.getString(R.string.app_name);
        carrier = "GH";
        app_version = SystemUtils.getAppVersion(AppApplication.CONTEXT);
        device_id = SystemUtils.getDeviceID(AppApplication.CONTEXT);
        local_phone_number = SystemUtils.getPhoneNum(AppApplication.CONTEXT);
        //channel_no = CommonManager.CHANNEL_ID;
        channel_no = TokenHelper.getInstance().getSmyParameter("channelNo");//42替换成T5、T6
        if (CheckUtil.isEmpty(channel_no)) {//默认app默认渠道42
            channel_no = CommonManager.CHANNEL_ID;
        }
        channel_store = SystemUtils.metaDataValueForTDChannelId(AppApplication.CONTEXT);//haiercash、guangwang、oppo、

        business = TokenHelper.getInstance().getSmyParameter("business");
        appDownFrom = TokenHelper.getInstance().getSmyParameter("appDownFrom");
        ideaCode = TokenHelper.getInstance().getSmyParameter("ideaCode");
        registerVector = TokenHelper.getInstance().getSmyParameter("registerVector");
        currVector = NetConfig.TD_REGIS_VECTOR_SMY;//ghAPP
        channel_regis = TokenHelper.getInstance().getSmyParameter("regisChannel");//SpHp.getLogin(SpKey.LOGIN_REGISTCHANNEL);

        mq_method = "APP";
        messageType = "RISK_INFO";

        wifi_name = SystemUtils.getWifiName(AppApplication.CONTEXT);
        String mac = SystemUtils.getWifiMac(AppApplication.CONTEXT);
        mac = CheckUtil.isEmpty(mac) ? SystemUtils.getUnderWifiMessage(AppApplication.CONTEXT, "mac") : mac;
        wifi_mac = mac;
        entry_label = "HRGH-xjd";
    }

    public String getYdunToken() {
        return ydunToken;
    }

    public void setYdunToken(String ydunToken) {
        this.ydunToken = ydunToken;
    }

    public void setClassFrom(String classFrom) {
        this.classFrom = classFrom;
    }

    public void setBrData(BrBean br_data) {
        this.br_data = br_data;
    }

    public void setGeoData(GeoBean geo_data) {
        this.geo_data = geo_data;
    }

    public void setCallLog(List<CallLogBean> call_log) {
        this.call_log = call_log;
    }

    public void setContact(List<Object> contact) {
        this.contact = contact;
    }

    public void setAppList(List<Object> app_list) {
        this.app_list = app_list;
    }

    public void setAuthorize_items(String authorize_items) {
        this.authorize_items = authorize_items;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getAppl_seq() {
        return appl_seq;
    }

    public void setAppl_seq(String appl_seq) {
        this.appl_seq = appl_seq;
    }

    public String getProd_code() {
        return prod_code;
    }

    //产品编码（仅对支用）
    public void setProd_code(String prod_code) {
        this.prod_code = prod_code;
    }
}
