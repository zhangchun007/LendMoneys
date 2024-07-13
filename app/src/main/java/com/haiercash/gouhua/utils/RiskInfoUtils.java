package com.haiercash.gouhua.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.Contact_Data;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.CollectInfoBean;
import com.haiercash.gouhua.beans.RiskInfoBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.service.RequestRiskInfoServer;
import com.stericson.RootShell.RootShell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Sun
 * Date :    2018/6/29
 * FileName: RiskInfoUtils
 * Description: 风险信息上送
 * <p>
 * 02 通讯录
 * 03 短信
 * 04 当前位置
 * 05 本机号码
 * 06 通话记录
 * A501 百融注册
 * A502 市Code
 * A504 市名称
 * A505 设备号
 * A507 百融Gid
 * A508 ICCID
 * A701 app列表
 * A801 AndroidId
 */
public class RiskInfoUtils {
    /*信息采集03-短信*/
//    public static void requestRiskInfoSms(Context context, String remark2,String applSeq) {
//        if (GetMessageContent.listMessage.size() == 0) {
//            Logger.e("短信采集失败");
//            return;
//        }
//        if (CheckUtil.isEmpty(getUserId())) {
//            return;
//        }
//        List<Object> listMsg = new ArrayList<>();//短信内容
//        List<Object> listMsgTime = new ArrayList<>();//短信收发时间
//        List<Object> listMsgPhone = new ArrayList<>();//短信号码
//        List<Object> listMsgType = new ArrayList<>();//短信接收/发送类型
//        for (int i = 0; i < GetMessageContent.listMessage.size(); i++) {
//            listMsg.add(EncryptUtil.simpleEncrypt(GetMessageContent.listMessageContent.get(i)));
//            listMsgTime.add(EncryptUtil.simpleEncrypt(GetMessageContent.listMessageDate.get(i)));
//            listMsgPhone.add(EncryptUtil.simpleEncrypt(GetMessageContent.listMessagePhone.get(i)));
//            listMsgType.add(EncryptUtil.simpleEncrypt(GetMessageContent.listMessageType.get(i)));
//        }
//        if (listMsg.size() <= 0 || listMsgTime.size() <= 0 || listMsgPhone.size() <= 0 || listMsgType.size() <= 0) {
//            return;
//        }
//        RiskInfoBean riskInfoBean = getRiskInfo();
//        riskInfoBean.setDataTyp("03");
//        riskInfoBean.setSource("18");
//        riskInfoBean.setApplSeq(applSeq);
//        riskInfoBean.setContent(listMsg);//存短信内容
//        riskInfoBean.setReserved1(SystemUtils.getDeviceID(context));//设备号
//        riskInfoBean.setReserved2(listMsgTime);//存短信收发时间
//        riskInfoBean.setReserved3(listMsgPhone);//存短信号码
//        riskInfoBean.setReserved4(listMsgType);//存短信接收/发送类型
//        if (!CheckUtil.isEmpty(remark2)) {
//            riskInfoBean.setRemark2(remark2);//采集节点
//        }
//        riskInfoBean.setDataDt(TimeUtil.longToString(TimeUtil.currentTimestamp(), "yyyy-MM-dd"));
//        NetHelper netHelper = new NetHelper();
//        netHelper.postService(ApiUrl.urlRiskInfo, riskInfoBean, CollectInfoBean.class);
//    }

    /**
     * if ("WJDLMM".equals(pwdTag)) {BR09 个人: 忘记登录密码<br/>
     * ("BR09", isSuccess ? "YES" : "NO");<br/>
     * } else if ("XGDLMM".equals(pwdTag)) {BR06 个人: 修改登录密码<br/>
     * ("BR06", isSuccess ? "YES" : "NO");<br/>
     * }
     */
    public static void updateRiskBro09Or06(boolean isSuccess, String riskTag) {
        if ("WJDLMM".equals(riskTag)) {
            updateRiskInfoByNode("BR09", isSuccess ? "YES" : "NO");
        } else if ("XGDLMM".equals(riskTag)) {
            updateRiskInfoByNode("BR06", isSuccess ? "YES" : "NO");
        }
    }

    public static void updateRiskInfoByNode(final String dataType, final String remark) {
        updateRiskInfoByNode(dataType, remark, null);
    }

    /**
     * @param dataType BR01 	个人: 注册/设置登陆密码<br/>
     *                 BR02 	个人: FACE活体认证<br/>
     *                 BR03 	个人: 设置交易密码<br/>
     *                 BR04 	个人: 设置手势密码<br/>
     *                 BR05 	个人: 修改交易密码<br/>
     *                 BR06 	个人: 修改登录密码<br/>
     *                 BR07 	个人: 修改手势密码<br/>
     *                 BR08 	个人: 忘记交易密码<br/>
     *                 BR09 	个人: 忘记登录密码<br/>
     *                 BR010 	个人: 密码登录<br/>
     *                 BR011 	个人: 手势密码登录<br/>
     *                 BR012 	个人: 指纹密码登录<br/>
     *                 BR013 	个人: 额度申请<br/>
     *                 BR014 	个人: 支用<br/>
     */
    public static void updateRiskInfoByNode(final String dataType, final String remark, final String applSeq) {
        if (CheckUtil.isEmpty(getUserId())) {
            return;
        }
        if (CheckUtil.isEmpty(dataType)) {
            return;
        }
        BrAgentUtils.getBrAgentGid((afSwiftNumber, brObject) -> {
            RiskInfoBean riskInfoBean = getRiskInfo();
            riskInfoBean.setDataTyp(dataType);
            riskInfoBean.setSource("18");
            List<Object> list = new ArrayList<>();
            list.add(EncryptUtil.simpleEncrypt(BrAgentUtils.getSpHelperGid()));
            riskInfoBean.setContent(list);
            riskInfoBean.setRemark1(remark);
            riskInfoBean.setRemark4("AND");
            if (!CheckUtil.isEmpty(applSeq)) {
                riskInfoBean.setReserved6(applSeq);
                riskInfoBean.setApplSeq(applSeq);
            }
            riskInfoBean.setRemark5(SystemUtils.getAppVersion(AppApplication.CONTEXT));
            NetHelper netHelper = new NetHelper();
            netHelper.postService(ApiUrl.urlRiskInfo, riskInfoBean);
        });
    }


    /*信息采集04-经纬度(当前位置)*/
    private static RiskInfoBean requestLocation(String applSeq, boolean autoSend) {
        SpHelper spHelper = SpHelper.getInstance();
        String lon = spHelper.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_LON);
        String lat = spHelper.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_LAT);
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("04");
        riskInfoBean.setSource("18");
        riskInfoBean.setRemark4("AND");
        riskInfoBean.setRemark5(SystemUtils.getAppVersion(AppApplication.CONTEXT));
        //riskInfoBean.setRemark5("AND"); //--（具体参数待定）
        List<Object> listLocation = new ArrayList<>();
        listLocation.add(EncryptUtil.simpleEncrypt("经度" + lon + "纬度" + lat));
        riskInfoBean.setContent(listLocation);
        if (!CheckUtil.isEmpty(applSeq)) {
            riskInfoBean.setReserved6(applSeq);
            riskInfoBean.setApplSeq(applSeq);
        }
        if (autoSend) {
            NetHelper netHelper = new NetHelper();
            netHelper.postService(ApiUrl.urlRiskInfo, riskInfoBean);
        }
        return riskInfoBean;
    }


    /*信息采集A504-市名称(当前位置)*/
    private static RiskInfoBean requestRiskCityName(String applSeq) {
        SpHelper spHelper = SpHelper.getInstance();
        String getCity = spHelper.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_CITYNAME);
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("A504");
        riskInfoBean.setSource("18");
        List<Object> listLocation = new ArrayList<>();
        listLocation.add(EncryptUtil.simpleEncrypt(getCity));
        riskInfoBean.setContent(listLocation);
        if (!CheckUtil.isEmpty(applSeq)) {
            riskInfoBean.setReserved6(applSeq);
            riskInfoBean.setApplSeq(applSeq);
        }
        return riskInfoBean;
    }

    /*信息采集A502-市Code(当前位置)*/
    private static RiskInfoBean requestRiskCityCode(String applSeq) {
        SpHelper spHelper = SpHelper.getInstance();
        String cityCode = spHelper.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_CITYCODE);
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("A502");
        riskInfoBean.setSource("18");
        List<Object> listLocation = new ArrayList<>();
        listLocation.add(EncryptUtil.simpleEncrypt(cityCode));
        riskInfoBean.setContent(listLocation);
        if (!CheckUtil.isEmpty(applSeq)) {
            riskInfoBean.setReserved6(applSeq);
            riskInfoBean.setApplSeq(applSeq);
        }
        return riskInfoBean;
    }

    /*信息采集A509-省名称(当前位置)*/
    private static RiskInfoBean requestRiskProvinceName(String applSeq) {
        SpHelper spHelper = SpHelper.getInstance();
        String provinceName = spHelper.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_PEOVINCENAME);
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("A509");
        riskInfoBean.setSource("18");
        List<Object> listLocation = new ArrayList<>();
        listLocation.add(EncryptUtil.simpleEncrypt(provinceName));
        riskInfoBean.setContent(listLocation);
        if (!CheckUtil.isEmpty(applSeq)) {
            riskInfoBean.setReserved6(applSeq);
            riskInfoBean.setApplSeq(applSeq);
        }
        return riskInfoBean;
    }

    /*信息采集A510-省Code(当前位置)*/
    private static RiskInfoBean requestRiskProvinceCode(String applSeq) {
        SpHelper spHelper = SpHelper.getInstance();
        String provinceCode = spHelper.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_PROVINCECODE);
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("A510");
        riskInfoBean.setSource("18");
        List<Object> listLocation = new ArrayList<>();
        listLocation.add(EncryptUtil.simpleEncrypt(provinceCode));
        riskInfoBean.setContent(listLocation);
        if (!CheckUtil.isEmpty(applSeq)) {
            riskInfoBean.setReserved6(applSeq);
            riskInfoBean.setApplSeq(applSeq);
        }
        return riskInfoBean;
    }


    /*信息采集A505-设备号(当前位置)*/
    private static RiskInfoBean requestRiskDeviceId(Context context, String applSeq) {
        String deviceId = SystemUtils.getDeviceID(context, true);
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("A505");
        riskInfoBean.setSource("18");
        List<Object> listLocation = new ArrayList<>();
        listLocation.add(EncryptUtil.simpleEncrypt(deviceId));
        riskInfoBean.setContent(listLocation);
        if (!CheckUtil.isEmpty(applSeq)) {
            riskInfoBean.setReserved6(applSeq);
            riskInfoBean.setApplSeq(applSeq);
        }
        return riskInfoBean;
    }

    /*信息采集A801-AndroidId*/
    private static RiskInfoBean requestRiskAndroidId(Context context, String applSeq) {
        String androidId = SystemUtils.getAndroidId(context);
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("A801");
        riskInfoBean.setSource("18");
        List<Object> listLocation = new ArrayList<>();
        listLocation.add(EncryptUtil.simpleEncrypt(androidId));
        riskInfoBean.setContent(listLocation);
        if (!CheckUtil.isEmpty(applSeq)) {
            riskInfoBean.setReserved6(applSeq);
            riskInfoBean.setApplSeq(applSeq);
        }
        return riskInfoBean;
    }

    /*信息采集A508-ICCID*/
    private static RiskInfoBean requestRiskICCID(Context context, String applSeq) {
        String deviceICCID = SystemUtils.getDeviceICCID(context);
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("A508");
        riskInfoBean.setSource("18");
        List<Object> listLocation = new ArrayList<>();
        listLocation.add(EncryptUtil.simpleEncrypt(deviceICCID));
        riskInfoBean.setContent(listLocation);
        if (!CheckUtil.isEmpty(applSeq)) {
            riskInfoBean.setReserved6(applSeq);
            riskInfoBean.setApplSeq(applSeq);
        }
        return riskInfoBean;
    }

    /**
     * 新增采集APP列表-A701
     */
    public static void requestRiskAppList(Context context, String remark2, String applySeq) {
        //获取设备app列表
        //List<Object> listMap = SystemUtils.getRealAppList(context, remark2, applySeq);
        List<Object> listMap = RiskKfaUtils.getAppListOld(context, remark2, applySeq);
        //List<ApplicationInfo> appList = SystemUtils.getAppList(context);
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("A701");
        riskInfoBean.setSource("18");
        riskInfoBean.setApplSeq(applySeq);
        riskInfoBean.setRiskList(listMap);
        NetHelper netHelper = new NetHelper();
        Logger.d("requestRiskAppList - A701 - appList 上送");
        netHelper.postService(ApiUrl.urlRiskInfoNew, riskInfoBean);
    }

    /*信息采集A507-百融Gid*/
    private static RiskInfoBean requestRiskBrGid(String applSeq) {
        String gid = BrAgentUtils.getSpHelperGid();
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("A507");
        riskInfoBean.setSource("18");
        riskInfoBean.setRemark4("AND");
        riskInfoBean.setRemark5(SystemUtils.getAppVersion(AppApplication.CONTEXT));
        List<Object> listLocation = new ArrayList<>();
        listLocation.add(EncryptUtil.simpleEncrypt(gid));
        riskInfoBean.setContent(listLocation);
        if (!CheckUtil.isEmpty(applSeq)) {
            riskInfoBean.setReserved6(applSeq);
            riskInfoBean.setApplSeq(applSeq);
        }
        return riskInfoBean;
    }


    /*信息采集05-本机号码*/
    public static void requestRiskInfoPhone(Context context) {
        String phone = SystemUtils.getPhoneNum(context);
        if (CheckUtil.isEmpty(phone)) {
            Logger.e("本机号码无法获取");
            return;
        }
        if (CheckUtil.isEmpty(getUserId())) {
            return;
        }
        List<Object> listPhone = new ArrayList<>();//本机号码
        listPhone.add(EncryptUtil.simpleEncrypt(phone));
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("05");
        riskInfoBean.setSource("18");
        riskInfoBean.setContent(listPhone);
        NetHelper netHelper = new NetHelper();
        netHelper.postService(ApiUrl.urlRiskInfo, riskInfoBean);
    }

    /**
     * A501百融支用事件和额度申请事件流水号上送
     */
    public static void requestRiskInfoBrAgentInfo(String af_swift_number, String antifraudType, String applSeq) {
        List<Object> content = new ArrayList<>();
        List<Map<String, String>> contentList = new ArrayList<>();
        if (!CheckUtil.isEmpty(applSeq)) {
            String brString = SpHp.getOther(SpKey.OTHER_BR_LOGIN);
            if (TextUtils.isEmpty(brString)) {
                brString = "APPRCERR000001";
            }
            Map<String, String> brLogin = new HashMap<>();
            brLogin.put("content", EncryptUtil.simpleEncrypt(brString));
            brLogin.put("reserved6", applSeq);
            brLogin.put("reserved7", "antifraud_login");
            contentList.add(brLogin);
        }
        Map<String, String> mapLoadEvent = new HashMap<>();
        mapLoadEvent.put("content", EncryptUtil.simpleEncrypt(af_swift_number));
        mapLoadEvent.put("reserved6", applSeq);
        mapLoadEvent.put("reserved7", antifraudType);
        contentList.add(mapLoadEvent);
        content.add(contentList);
        //风险采集接口
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("A501");
        riskInfoBean.setSource("18");
        riskInfoBean.setReserved7(antifraudType);
        riskInfoBean.setContent(content);
        riskInfoBean.setRemark4("AND");
        riskInfoBean.setRemark5(SystemUtils.getAppVersion(AppApplication.CONTEXT));
        if (!CheckUtil.isEmpty(applSeq)) {
            riskInfoBean.setReserved6(applSeq);
            riskInfoBean.setApplSeq(applSeq);
        }
        riskInfoBean.setRemark4("AND");
        riskInfoBean.setRemark5(SystemUtils.getAppVersion(AppApplication.CONTEXT));
        NetHelper netHelper = new NetHelper();
        netHelper.postService(ApiUrl.urlRiskInfo, riskInfoBean, CollectInfoBean.class);
    }


    /*
     * 通话记录  06
     */
    public static void requestRiskInfoCallLog(Context context, String remark2, String applySeq) {
        if (CheckUtil.isEmpty(getUserId())) {
            return;
        }
        List<Object> listMap = CallsLog.getCallLogList(context, remark2, applySeq);
        if (CheckUtil.isEmpty(listMap)) {
            return;
        }
        NetHelper netHelper = new NetHelper();
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("06");
        riskInfoBean.setSource("18");
        riskInfoBean.setApplSeq(applySeq);
        riskInfoBean.setRiskList(listMap);

        //if (!CheckUtil.isEmpty(remark2)) {
        //    riskInfoBean.setRemark2(remark2);
        //}
        //riskInfoBean.setDataDt(TimeUtil.longToString(TimeUtil.currentTimestamp(), "yyyy-MM-dd"));
        netHelper.postService(ApiUrl.urlRiskInfoNew, riskInfoBean);
    }


    /**
     * 信息采集1-联系人 02
     */
    public static void requestRiskInfoContacts(Context context, String remark2, String applySeq) {
        if (CheckUtil.isEmpty(getUserId())) {
            return;
        }
        if (Contact_Data.mContactsNumber.size() == 0) {
            return;
        }
        List<Object> listContactNumber = new ArrayList<>();//联系人
        List<Object> listContactName = new ArrayList<>();//联系人号码
        for (int i = 0; i < Contact_Data.mContactsNumber.size(); i++) {
            listContactNumber.add(EncryptUtil.simpleEncrypt(Contact_Data.mContactsNumber.get(i)));
            listContactName.add(EncryptUtil.simpleEncrypt(Contact_Data.mContactsName.get(i)));
        }
        RiskInfoBean riskInfoBean = getRiskInfo();
        riskInfoBean.setDataTyp("02");
        riskInfoBean.setSource("18");
        riskInfoBean.setApplSeq(applySeq);
        riskInfoBean.setContent(listContactNumber);//存手机号
        riskInfoBean.setReserved1(SystemUtils.getDeviceID(context));//设备号
        riskInfoBean.setReserved2(listContactName);//存备注名
        if (!CheckUtil.isEmpty(remark2)) {
            riskInfoBean.setRemark2(remark2);
        }
        riskInfoBean.setRemark4("AND");//SystemUtils.getDeviceModel() // 手机型号：vivo X20A
        riskInfoBean.setRemark5(SystemUtils.getAppVersion(AppApplication.CONTEXT));
        riskInfoBean.setDataDt(TimeUtil.longToString(TimeUtil.currentTimestamp(), "yyyy-MM-dd"));
        NetHelper netHelper = new NetHelper();
        netHelper.postService(ApiUrl.urlRiskInfo, riskInfoBean);
    }

    /**
     * 借款和支用时获取风险信息
     */
    public static List<RiskInfoBean> getAllRiskInfo(Context context, String applSeq) {
        //新风险采集接口，上传经纬度、市名称、市编码
        List<RiskInfoBean> listRisk = new ArrayList<>();
        //经纬度信息收集 04
        listRisk.add(requestLocation(applSeq, false));
        //市名称信息收集 A504
        listRisk.add(requestRiskCityName(applSeq));
        //市code信息收集 A502
        listRisk.add(requestRiskCityCode(applSeq));
        //省名称信息收集 A509
        listRisk.add(requestRiskProvinceName(applSeq));
        //省code信息收集 A510
        listRisk.add(requestRiskProvinceCode(applSeq));
        //deviceId  A505
        listRisk.add(requestRiskDeviceId(context, applSeq));
        //AndroidId  A801   只有Android Q以上才需要上报Android ID
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            listRisk.add(requestRiskAndroidId(context, applSeq));
        }
        //iccid信息收集  A508
        listRisk.add(requestRiskICCID(context, applSeq));
        //百融gid  A507
        listRisk.add(requestRiskBrGid(applSeq));

        return listRisk;
    }

    /**
     * 返回携带有userId、name、mobile、idNo的RiskInfoBean实体
     */
    private static RiskInfoBean getRiskInfo() {
        RiskInfoBean riskInfoBean = new RiskInfoBean();
        String name = SpHp.getUser(SpKey.USER_CUSTNAME);
        String mobile = SpHp.getLogin(SpKey.LOGIN_MOBILE);
        String idNo = SpHp.getUser(SpKey.USER_CERTNO);
        //String userId = SpHp.getLogin(SpKey.LOGIN_USERID);

        riskInfoBean.setUserId(EncryptUtil.simpleEncrypt(getUserId()));
        riskInfoBean.setName(EncryptUtil.simpleEncrypt(name));
        riskInfoBean.setCustName(EncryptUtil.simpleEncrypt(name));
        riskInfoBean.setMobile(EncryptUtil.simpleEncrypt(mobile));
        riskInfoBean.setMobileNo(EncryptUtil.simpleEncrypt(mobile));
        riskInfoBean.setIdNo(EncryptUtil.simpleEncrypt(idNo));
        return riskInfoBean;
    }

    /**
     * 百融采集成功或者失败都上送
     */
    public static void postBrToBigData(Object brObject) {
        if (brObject != null) {
            postBrOrBigData("BR", "", brObject);
        }
    }

    /**
     * 采集设备信息数据上送接口<br/>
     *
     * @param type 注册-register，登录-login，支用-lend，申请额度-cash
     */
    public static void postBrOrBigData(Context context, String type, String applseq, Object brObject) {
        if (CheckUtil.isEmpty(type)) {
            Logger.e("postBrOrBigData--采集设备信息数据上送接口 --上送type为空");
        }
        postBrOrBigData("BR", applseq, brObject);

        String gid = BrAgentUtils.getSpHelperGid();
        Map<String, Object> jsonData = new HashMap<>();
        String mac = SystemUtils.getWifiMac(context);
        mac = CheckUtil.isEmpty(mac) ? SystemUtils.getUnderWifiMessage(context, "mac") : mac;
        jsonData.put("wifiMac", mac);
        String wifiName = SystemUtils.getUnderWifiMessage(context, "name");
        if (!CheckUtil.isEmpty(wifiName) && wifiName.startsWith("\"")) {
            wifiName = wifiName.replace("\"", "");
        }
        jsonData.put("wifiName", wifiName);
        jsonData.put("is_root", RootShell.isRootAvailable());
        jsonData.put("is_wifi_proxy", SystemUtils.isWifiProxy(context));
        //collectData.put("is_vpn_on", SystemUtils.isVpnUsed());
        jsonData.put("gid", gid);
        jsonData.put("token_id", BrAgentUtils.getTokenId());
        /*
         * 注册-register，登录-login，支用-lend，申请额度-cash
         */
        jsonData.put("collectType", type);
        postBrOrBigData("HC", applseq, jsonData);
    }

    /**
     * BR:  百融
     * <p>
     * HC:  海尔自采集
     */
    private static void postBrOrBigData(String collectType, String applseq, Object collectData) {
        Map<String, Object> map = new HashMap<>();
        map.put("collectType", collectType);
        map.put("applseq", applseq);
        map.put("collectData", collectData);
        map.put("remark6", BrAgentUtils.getSpHelperGid());
        //map.put("remark5", "");//渠道  是否需要此参数
        NetHelper netHelper = new NetHelper();
        netHelper.postService(ApiUrl.BR_BIG_DATA_COLLECT, map);
    }

    /**
     * Gio采集数据上送接口
     */
    public static void postGioData(Context context, String eventType) {
        Map<String, String> map = new HashMap<>();
        map.put("remark1", "GIO");
        map.put("remark2", getUserId());
        map.put("remark3", eventType);//事件类型
        map.put("remark4", RSAUtils.encryptByRSA(SystemUtils.getDeviceID(context, true)));
        map.put("remark5", SystemUtils.getModelOrBrand(null) + "-" + SystemUtils.getDeviceModel());
        map.put("remark6", "AND-" + SystemUtils.getAppVersion(context));
        map.put("remark7", SystemUtils.metaDataValueForTDChannelId(context));
        new NetHelper().postService(ApiUrl.URL_COLLECT_GIO_DATA, map);
    }


    /**
     * 获取userId
     */
    static String getUserId() {
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        if (CheckUtil.isEmpty(userId)) {
            userId = AppApplication.userid;
        }
        return userId;
    }


    public static void send(BaseActivity context, String remark, String applySeq) {
        startServer(context, "app_list", remark, applySeq);
        context.requestPermission(aBoolean -> {
            startServer(context, "contacts", remark, applySeq);
            startServer(context, "call_log", remark, applySeq);
        }, -1, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG);

        /*if (PermissionUtils.getRequestPermission(context, Manifest.permission.READ_CONTACTS)) {
            if (PermissionUtils.getReadContactsPermission(context)) {
                startServer(context, "contacts", remark, applySeq);
            }
        }
//        if (PermissionUtils.getRequestPermission(context, Manifest.permission.READ_SMS)) {
//            startServer(context, "sms", remark);
//        }
        if (PermissionUtils.getRequestPermission(context, Manifest.permission.READ_CALL_LOG)) {
            Logger.e("授权成功：call_log");
            startServer(context, "call_log", remark, applySeq);
        } else {
            Logger.e("授权失败：call_log");
        }*/
    }

    private static void startServer(BaseActivity context, String type, String remark, String applySeq) {
        Intent intentSms = new Intent(context, RequestRiskInfoServer.class);
        intentSms.putExtra("type", type);
        intentSms.putExtra("applySeq", applySeq);
        intentSms.putExtra("remark", remark);
        context.startService(intentSms);
    }
}
