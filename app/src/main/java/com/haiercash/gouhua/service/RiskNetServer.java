package com.haiercash.gouhua.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.risk.RiskBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.RiskKfaUtils;
import com.haiercash.gouhua.utils.SpHp;

/**
 * doType:
 * 0  通讯录、  Applist、  本机手机号码、 通话记录、 百度sdk、 百融sdk
 * 1  百融sdk、Applist、本机手机号码、百度sdk
 * 2  通讯录
 * -1 未知
 * -2 已经拼接好数据，直接上送
 */
public class RiskNetServer extends IntentService implements INetResult {
    private NetHelper netHelper;

    public RiskNetServer() {
        super("RiskNetServer");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        netHelper = new NetHelper(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        String classFrom = intent.getStringExtra("ClassFrom");
        String eventId = intent.getStringExtra("eventId");
        String applySeq = intent.getStringExtra("applySeq");
        String userId = intent.getStringExtra("userId");
        String channelNo = intent.getStringExtra("channelNo");
        int brType = intent.getIntExtra("brType", -1);
        int doType = intent.getIntExtra("doType", -1);
        if (doType == -2) {
            RiskBean riskBean = (RiskBean) intent.getSerializableExtra("riskBean");
            netHelper.postService(ApiUrl.POST_URL_RISK_MSG, riskBean);
        } else {
            try {
                RiskKfaUtils.upKfaRiskNew(this, userId, doType, brType, classFrom, eventId, applySeq, channelNo,
                        obj -> netHelper.postService(ApiUrl.POST_URL_RISK_MSG, obj));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        RiskBean bean = new RiskBean(eventId, applySeq, userId);
//        String authorize = "";
//        if (PermissionUtils.getRequestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            authorize += "gps,";
//        }
//        if (PermissionUtils.getRequestPermission(this, Manifest.permission.READ_PHONE_STATE)) {
//            authorize += "device,";
//        }
//        if (PermissionUtils.getRequestPermission(this, Manifest.permission.READ_CONTACTS)) {
//            authorize += "contact,";
//        }
//        if (PermissionUtils.getRequestPermission(this, Manifest.permission.READ_CALL_LOG)) {
//            authorize += "call_log,";
//        }
//        if (!CheckUtil.isEmpty(authorize) && authorize.endsWith(",")) {
//            authorize = authorize.substring(0, authorize.length() - 1);
//        }
//        bean.setAuthorize_items(authorize);
//        bean.setClassFrom(EncryptUtil.simpleEncrypt(classFrom));
//        if (doType == 0) {
//            if (Contact_Data.CONTACTS_LIST == null || Contact_Data.CONTACTS_LIST.isEmpty()) {
//                Contact_Data.getPhoneContacts(this);
//            }
//            bean.setContact(Contact_Data.CONTACTS_LIST);
//            bean.setAppList(SystemUtils.getRealAppListNew(this));
//            bean.setCallLog(CallsLog.getCallLogList(this));
//        } else if (doType == 1) {
//            bean.setAppList(SystemUtils.getRealAppListNew(this));
//        } else if (doType == 2) {
//            if (Contact_Data.CONTACTS_LIST == null || Contact_Data.CONTACTS_LIST.isEmpty()) {
//                Contact_Data.getPhoneContacts(this);
//            }
//            bean.setContact(Contact_Data.CONTACTS_LIST);
//            netHelper.postService(ApiUrl.POST_URL_RISK_MSG, bean);
//            return;
//        } else {
//            return;
//        }
//        updateGeoThenRisk(bean, brType);
    }

//    /**
//     * 百度信息
//     */
//    private void updateGeoThenRisk(RiskBean bean, final int brType) {
//        new GhLocation(this, true, (isSuccess, reason) -> {
//            GeoBean bean1 = new GeoBean();
//            SpHelper baiduSp = SpHelper.getInstance();
//            String time = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_UPDATE);
//            if (!CheckUtil.isEmpty(time)) {
//                if (System.currentTimeMillis() - Long.parseLong(time) < 1000 * 60 * 60 * 12) {//是否为当天
//                    String json = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_JSON);
//                    Address address = JsonUtils.fromJson(json, Address.class);
//                    bean1.adcode = address.adcode;
//                    bean1.address = address.address;
//                    bean1.country = address.country;
//                    bean1.district = address.district;
//                    bean1.city_name = address.city;
//                    bean1.province_name = address.province;
//                    bean1.street = address.street;
//                    bean1.street_number = address.streetNumber;
//                    bean1.town = address.town;
//                    bean1.longitude = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_LON);
//                    bean1.latitude = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_LAT);
//                }
//            }
//            bean.setGeoData(bean1);
//            if (SystemUtils.isAllow) {
//                BrAgentUtils.defaultBrAgent(this, brType, ((afSwiftNumber, brObject) -> {
//                    BrBean brBean = getBrData(afSwiftNumber, brObject);
//                    if (CheckUtil.isEmpty(brBean.getGid())) {
//                        brBean.setGid(BrAgentUtils.getSpHelperGid());
//                    }
//                    bean.setBrData(brBean);
//                    netHelper.postService(ApiUrl.POST_URL_RISK_MSG, bean);
//                }));
//            } else {
//                BrBean brBean = getBrData("", "");
//                if (CheckUtil.isEmpty(brBean.getGid())) {
//                    brBean.setGid(BrAgentUtils.getSpHelperGid());
//                }
//                bean.setBrData(brBean);
//                netHelper.postService(ApiUrl.POST_URL_RISK_MSG, bean);
//            }
//        }).requestLocationNoPermission();
//    }

    /**
     * 通讯录<br/>
     * Applist<br/>
     * 本机手机号码<br/>
     * 通话记录<br/>
     * 百度sdk<br/>
     * 百融sdk<br/>
     */
    public static void startRiskServer(Context context, String eventId, String applySeq, int brType) {
        startService(context, eventId, applySeq, 0, brType);
    }

    /**
     * 百融sdk<br/>
     * Applist<br/>
     * 本机手机号码<br/>
     * 百度sdk<br/>
     */
    public static void startRiskServer1(Context context, String eventId, String applySeq, int brType) {
        startService(context, eventId, applySeq, 1, brType);
    }

    /**
     * 通讯录、通话记录<br/>
     */
    public static void startRiskServer2(Context context, String eventId, String applySeq) {
        startService(context, eventId, applySeq, 2, -1);
    }

    public static void startService(Context context, RiskBean riskBean, String applySeq) {
        if (riskBean == null) {
            return;
        }
        if (RiskKfaUtils.IS_DEBUG_RISK) {
            Logger.e("启动分线数据上送。。。。。 " + riskBean.getEvent_id());
        }
        riskBean.setAppl_seq(applySeq);
        Intent intent = new Intent(context, RiskNetServer.class);
        intent.putExtra("doType", -2);
        intent.putExtra("riskBean", riskBean);
        context.startService(intent);
    }

    public static void startService(Context context, String eventId, String applySeq, int doType, int brType) {
        startService(context, eventId, applySeq, doType, brType, "");
    }

    public static void startService(Context context, String eventId, String applySeq, int doType, int brType, String channelNo) {
        //UiUtil.toastDeBug("事件名称【" + eventId + "】");
        String userId = getUserId();
        if (!CheckUtil.isEmpty(userId)) {
            if (RiskKfaUtils.IS_DEBUG_RISK) {
                Logger.e("启动分线数据上送。。。。。 " + eventId);
            }
            Intent intent = new Intent(context, RiskNetServer.class);
            intent.putExtra("ClassFrom", context.getClass().getSimpleName());
            intent.putExtra("doType", doType);
            intent.putExtra("eventId", eventId);
            intent.putExtra("userId", userId);
            intent.putExtra("applySeq", applySeq);
            intent.putExtra("brType", brType);
            intent.putExtra("channelNo", channelNo);
            context.startService(intent);
        } else {
            if (RiskKfaUtils.IS_DEBUG_RISK) {
                Logger.e("无法启动分线数据上送。。。。。 " + eventId);
            }
        }
    }


    public static String getUserId() {
        if (CheckUtil.isEmpty(AppApplication.userid)) {
            return SpHp.getLogin(SpKey.LOGIN_USERID);
        } else {
            return AppApplication.userid;
        }
    }

    @Override
    public void onSuccess(Object t, String url) {
        //System.out.println(JsonUtils.toJson(t));
    }

    @Override
    public void onError(BasicResponse error, String url) {
        //System.out.println(JsonUtils.toJson(t));
    }

//    private BrBean getBrData(String afSwiftNumber, Object brObject) {
//        BrBean bean = new BrBean();
//        try {
//            if (BrAgentUtils.objInitBean != null) {
//                @SuppressWarnings("unchecked")
//                HashMap<String, String> map = JsonUtils.fromJson(BrAgentUtils.objInitBean, HashMap.class);
//                String param = map.get("param");
//                bean = JsonUtils.fromJson(param, BrBean.class);
//            } else {
//                bean.setObj(brObject);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (bean == null) {
//            bean = new BrBean();
//        }
//        try {
//            bean.afSwiftNumber = afSwiftNumber;
//            //自采集
//            //android_id = SystemUtils.getAndroidId(AppApplication.CONTEXT);
//            //battery_capacity = SystemUtils.getBatteryCapacity(AppApplication.CONTEXT);
//            //battery_level = SystemUtils.getBatteryLevel(AppApplication.CONTEXT);
//            bean.bluetooth_mac = android.provider.Settings.Secure.getString(AppApplication.CONTEXT.getContentResolver(), "bluetooth_address");
//            bean.cell = SysCollect.getCell(AppApplication.CONTEXT);
//            bean.cpuCount = SysCollect.getNumberOfCPUCores() + "";
//            bean.sd = SysCollect.getAllSize() + "";
//            bean.sensor = SysCollect.getSensorList(AppApplication.CONTEXT);
//            bean.cpu_abi2 = SysCollect.cpuAbi();
//            bean.cpu_model = android.os.Build.CPU_ABI;
//            bean.ro_debuggable = SysCollect.isApkInDebug(AppApplication.CONTEXT) ? "1" : "2";
//            bean.persist_sys_country = SysCollect.getCountryZipCode(AppApplication.CONTEXT);
//            bean.persist_sys_language = SysCollect.getSysLanguage();
//            String[] strs = SysCollect.getDns(AppApplication.CONTEXT);
//            bean.net_dns1 = strs[0];
//            bean.net_hostname = strs[1];
//            bean.net_eth0_gw = SysCollect.getIpAddrForInterfaces("eth0");
//            bean.bssid = SysCollect.getHostIp(AppApplication.CONTEXT);
//            bean.network = SysCollect.getNetworkState(AppApplication.CONTEXT);
//            bean.signal_strength = SysCollect.getWifiRssi(AppApplication.CONTEXT);
//            bean.wifiname = SystemUtils.getUnderWifiMessage(AppApplication.CONTEXT, "name");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return bean;
//    }
}
