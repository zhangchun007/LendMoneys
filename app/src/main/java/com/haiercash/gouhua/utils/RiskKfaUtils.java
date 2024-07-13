package com.haiercash.gouhua.utils;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;

import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.permission.PermissionUtils;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.Contact_Data;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.baidu.location.Address;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.risk.BrBean;
import com.haiercash.gouhua.beans.risk.CallLogBean;
import com.haiercash.gouhua.beans.risk.GeoBean;
import com.haiercash.gouhua.beans.risk.RiskBean;
import com.haiercash.gouhua.beans.risk.SysCollect;
import com.haiercash.gouhua.interfaces.ICallBack;
import com.haiercash.gouhua.interfaces.SpKey;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================================
 * 作    者：L14-14<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2021/12/16-13:55<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class RiskKfaUtils {
    public static final boolean IS_DEBUG_RISK = false;

    private static final long FIVE_SECONDS = 5 * 60 * 1000;
    /**
     *
     */
    private static final Map<String, Object[]> cacheMap = new HashMap<>();


    public synchronized static void getRiskBean(Context context, int doType, int brType, String eventId, ICallBack iCallBack) {
        upKfaRiskNew(context, RiskInfoUtils.getUserId(), doType, brType, context.getClass().getSimpleName(), eventId, null, iCallBack);
    }

    public synchronized static void getRiskBean(Context context, int doType, int brType, String eventId, String channelNo, ICallBack iCallBack) {
        upKfaRiskNew(context, RiskInfoUtils.getUserId(), doType, brType, context.getClass().getSimpleName(), eventId, null, channelNo, iCallBack);
    }

    public synchronized static void upKfaRiskNew(Context context, String userId, int doType, int brType, String classFrom, String eventId, String applySeq, ICallBack iCallBack) {
        upKfaRiskNew(context, userId, doType, brType, classFrom, eventId, applySeq, "", iCallBack);
    }

    public synchronized static void upKfaRiskNew(Context context, String userId, int doType, int brType, String classFrom, String eventId, String applySeq, String channelNo, ICallBack iCallBack) {
        RiskBean bean = new RiskBean(eventId, applySeq, userId);
        if (!CheckUtil.isEmpty(channelNo)) {
            bean.setChannel_no(channelNo);
        }
        String authorize = "";
        if (PermissionUtils.getRequestPermission(context, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            authorize += "gps,";
        }
        if (PermissionUtils.getRequestPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            authorize += "device,";
        }
        if (PermissionUtils.getRequestPermission(context, Manifest.permission.READ_CONTACTS)) {
            authorize += "contact,";
        }
        if (PermissionUtils.getRequestPermission(context, Manifest.permission.READ_CALL_LOG)) {
            authorize += "call_log,";
        }
        if (!CheckUtil.isEmpty(authorize) && authorize.endsWith(",")) {
            authorize = authorize.substring(0, authorize.length() - 1);
        }
        bean.setAuthorize_items(authorize);
        bean.setClassFrom(EncryptUtil.simpleEncrypt(classFrom));
        if (doType == 0) {
//            if (Contact_Data.CONTACTS_LIST == null || Contact_Data.CONTACTS_LIST.isEmpty()) {
//                Contact_Data.getPhoneContacts(context);
//            }
//            bean.setContact(Contact_Data.CONTACTS_LIST);
            if (Contact_Data.CONTACTS_LISTS == null || Contact_Data.CONTACTS_LISTS.isEmpty()) {
                Contact_Data.getContactInformation(context);
            }
            bean.setContact(Contact_Data.CONTACTS_LISTS);
            bean.setAppList(getAppList(context));
            bean.setCallLog(getCallLogList(context));
        } else if (doType == 1) {
            bean.setAppList(getAppList(context));
        } else if (doType == 2) {
//            if (Contact_Data.CONTACTS_LIST == null || Contact_Data.CONTACTS_LIST.isEmpty()) {
//                Contact_Data.getPhoneContacts(context);
//            }
//            bean.setContact(Contact_Data.CONTACTS_LIST);
            if (Contact_Data.CONTACTS_LISTS == null || Contact_Data.CONTACTS_LISTS.isEmpty()) {
                Contact_Data.getContactInformation(context);
            }
            bean.setContact(Contact_Data.CONTACTS_LISTS);
            bean.setCallLog(getCallLogList(context));
            WyDeviceIdUtils wyDeviceIdInstance = WyDeviceIdUtils.getInstance();
            wyDeviceIdInstance.getWyDeviceIDTokenFromNative(AppApplication.CONTEXT, (token, code, msg) -> {
                if (!TextUtils.isEmpty(token)) {
                    bean.setYdunToken(token);
                }
                iCallBack.back(bean);
            });
            //netHelper.postService(ApiUrl.POST_URL_RISK_MSG, bean);
            return;
        } else if (doType == -2) {
            //设置网易设备指纹数据
            WyDeviceIdUtils wyDeviceIdInstance = WyDeviceIdUtils.getInstance();
            wyDeviceIdInstance.getWyDeviceIDTokenFromNative(AppApplication.CONTEXT, (token, code, msg) -> {
                if (!TextUtils.isEmpty(token)) {
                    bean.setYdunToken(token);
                }
                iCallBack.back(bean);
            });
        } else {
            return;
        }

        //设置网易设备指纹数据
        if (doType == 0||doType == 1){//防止异步没有数据
            //设置网易设备指纹数据
            WyDeviceIdUtils wyDeviceIdInstance = WyDeviceIdUtils.getInstance();
            wyDeviceIdInstance.getWyDeviceIDTokenFromNative(AppApplication.CONTEXT, (token, code, msg) -> {
                if (!TextUtils.isEmpty(token)) {
                    bean.setYdunToken(token);
                }
                updateGeoThenRisk(context, bean, brType, iCallBack);
            });
        }else {
            updateGeoThenRisk(context, bean, brType, iCallBack);
        }
    }

    /**
     * 百度信息
     */
    private synchronized static void updateGeoThenRisk(Context context, RiskBean bean, final int brType, ICallBack iCallBack) {
        try {
            if (cacheMap.containsKey("baiduGeo")) {
                Object[] objects = cacheMap.get("baiduGeo");
                if (objects != null && objects.length == 2 && objects[1] != null) {
                    String times = objects[0].toString();
                    long a = System.currentTimeMillis() - Long.parseLong(times);
                    if (a < FIVE_SECONDS) {
                        GeoBean bean1 = (GeoBean) objects[1];
                        bean.setGeoData(bean1);
                        updateBrRisk(context, bean, brType, iCallBack);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            if (IS_DEBUG_RISK) {
                Logger.e("RiskKfaUtils--updateGeoThenRisk--" + e.getMessage());
            }
            e.printStackTrace();
        }

        new GhLocation(context, true, (isSuccess, reason) -> {
            GeoBean bean1 = new GeoBean();
            SpHelper baiduSp = SpHelper.getInstance();
            //判断更新时间,因为定位失败或者没有权限等情况也会走此回调，所以判断是否能用缓存里的定位信息
            String time = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_UPDATE);
            if (!CheckUtil.isEmpty(time)) {
                if (System.currentTimeMillis() - Long.parseLong(time) < 1000 * 60 * 5) {//4.1.0改为五分钟缓存可用策略
                    String json = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_JSON);
                    Address address = JsonUtils.fromJson(json, Address.class);
                    if (address != null) {
                        bean1.adcode = address.adcode;
                        bean1.address = address.address;
                        bean1.country = address.country;
                        bean1.district = address.district;
                        bean1.city_name = address.city;
                        bean1.province_name = address.province;
                        bean1.street = address.street;
                        bean1.street_number = address.streetNumber;
                        bean1.town = address.town;
                        bean1.longitude = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_LON);
                        bean1.latitude = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_LAT);
                        cacheMap.put("baiduGeo", new Object[]{System.currentTimeMillis(), bean1});
                    }
                }
            }
            bean.setGeoData(bean1);
            updateBrRisk(context, bean, brType, iCallBack);
        }).requestLocationNoPermission();
    }

    /**
     * 百融之后上送
     */
    private synchronized static void updateBrRisk(Context context, RiskBean bean, final int brType, ICallBack iCallBack) {
        if (IS_DEBUG_RISK) {
            Logger.d("百融获取数据计时Start：" + TimeUtil.calendarToString(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
        }
        try {
            if (cacheMap.containsKey("brData")) {
                Object[] objects = cacheMap.get("brData");
                if (objects != null && objects.length == 2 && objects[1] != null) {
                    String times = objects[0].toString();
                    long a = System.currentTimeMillis() - Long.parseLong(times);
                    if (a < FIVE_SECONDS) {
                        BrBean brBean = (BrBean) objects[1];
                        bean.setBrData(brBean);
                        if (IS_DEBUG_RISK) {
                            Logger.d("百融获取数据计时End：" + TimeUtil.calendarToString(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
                        }
                        iCallBack.back(bean);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            if (IS_DEBUG_RISK) {
                Logger.e("RiskKfaUtils--updateBrRisk--" + e.getMessage());
            }
            e.printStackTrace();
        }

        if (SystemUtils.isAllow) {
            BrAgentUtils.defaultBrAgent(context, brType, ((afSwiftNumber, brObject) -> {
                BrBean brBean = getBrData(afSwiftNumber, brObject);
                if (CheckUtil.isEmpty(brBean.getGid())) {
                    brBean.setGid(BrAgentUtils.getSpHelperGid());
                }
                bean.setBrData(brBean);
                cacheMap.put("brData", new Object[]{System.currentTimeMillis(), brBean});
                if (IS_DEBUG_RISK) {
                    Logger.d("百融获取数据计时End：" + TimeUtil.calendarToString(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
                }
                iCallBack.back(bean);
                //netHelper.postService(ApiUrl.POST_URL_RISK_MSG, bean);
            }));
        } else {
            BrBean brBean = getBrData("", "");
            if (CheckUtil.isEmpty(brBean.getGid())) {
                brBean.setGid(BrAgentUtils.getSpHelperGid());
            }
            bean.setBrData(brBean);
            cacheMap.put("brData", new Object[]{System.currentTimeMillis(), brBean});
            //netHelper.postService(ApiUrl.POST_URL_RISK_MSG, bean);
            if (IS_DEBUG_RISK) {
                Logger.d("百融获取数据计时End：" + TimeUtil.calendarToString(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
            }
            iCallBack.back(bean);
        }
    }

    private synchronized static BrBean getBrData(String afSwiftNumber, Object brObject) {
        BrBean bean = new BrBean();
        try {
            if (BrAgentUtils.objInitBean != null) {
                @SuppressWarnings("unchecked")
                HashMap<String, String> map = JsonUtils.fromJson(BrAgentUtils.objInitBean, HashMap.class);
                String param = map.get("param");
                bean = JsonUtils.fromJson(param, BrBean.class);
            } else {
                bean.setObj(brObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bean == null) {
            bean = new BrBean();
        }
        try {
            bean.afSwiftNumber = afSwiftNumber;
            //自采集
            //android_id = SystemUtils.getAndroidId(AppApplication.CONTEXT);
            //battery_capacity = SystemUtils.getBatteryCapacity(AppApplication.CONTEXT);
            //battery_level = SystemUtils.getBatteryLevel(AppApplication.CONTEXT);
            bean.bluetooth_mac = android.provider.Settings.Secure.getString(AppApplication.CONTEXT.getContentResolver(), "bluetooth_address");
            bean.cell = SysCollect.getCell(AppApplication.CONTEXT);
            bean.cpuCount = SysCollect.getNumberOfCPUCores() + "";
            bean.sd = SysCollect.getAllSize() + "";
            bean.sensor = SysCollect.getSensorList(AppApplication.CONTEXT);
            bean.cpu_abi2 = SysCollect.cpuAbi();
            bean.cpu_model = android.os.Build.CPU_ABI;
            bean.ro_debuggable = SysCollect.isApkInDebug(AppApplication.CONTEXT) ? "1" : "2";
            bean.persist_sys_country = SysCollect.getCountryZipCode(AppApplication.CONTEXT);
            bean.persist_sys_language = SysCollect.getSysLanguage();
            String[] strs = SysCollect.getDns(AppApplication.CONTEXT);
            bean.net_dns1 = strs[0];
            bean.net_hostname = strs[1];
            bean.net_eth0_gw = SysCollect.getIpAddrForInterfaces("eth0");
            bean.bssid = SysCollect.getHostIp(AppApplication.CONTEXT);
            bean.network = SysCollect.getNetworkState(AppApplication.CONTEXT);
            bean.signal_strength = SysCollect.getWifiRssi(AppApplication.CONTEXT);
            bean.wifiname = SystemUtils.getUnderWifiMessage(AppApplication.CONTEXT, "name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    private synchronized static List<Object> getAppList(Context context) {
        try {
            if (cacheMap.containsKey("appList")) {
                Object[] objects = cacheMap.get("appList");
                if (objects != null && objects.length == 2 && objects[1] != null) {
                    String times = objects[0].toString();
                    long a = System.currentTimeMillis() - Long.parseLong(times);
                    if (a < FIVE_SECONDS) {
                        return (List<Object>) objects[1];
                    }
                }
            }
        } catch (Exception e) {
            if (IS_DEBUG_RISK) {
                Logger.e("RiskKfaUtils--getAppList--" + e.getMessage());
            }
            e.printStackTrace();
        }
        List<Object> appList = SystemUtils.getRealAppListNew(context);
        cacheMap.put("appList", new Object[]{System.currentTimeMillis(), appList});
        return appList;
    }

    @SuppressWarnings("unchecked")
    public synchronized static List<Object> getAppListOld(Context context, String remark2, String applySeq) {
        try {
            if (cacheMap.containsKey("appListOld")) {
                Object[] objects = cacheMap.get("appListOld");
                if (objects != null && objects.length == 2 && objects[1] != null) {
                    String times = objects[0].toString();
                    long a = System.currentTimeMillis() - Long.parseLong(times);
                    if (a < FIVE_SECONDS) {
                        List<Object> appList = (List<Object>) objects[1];
                        for (Object obj : appList) {
                            Map<String, String> map = (Map<String, String>) obj;
                            map.put("reserved6", applySeq);
                            //map.put("remark1", String.valueOf(isSystemApp(packageInfo)));//对应是否系统应用标识
                            map.put("remark2", remark2);
                        }
                        return appList;
                    }
                }
            }
        } catch (Exception e) {
            if (IS_DEBUG_RISK) {
                Logger.e("RiskKfaUtils--getAppListOld--" + e.getMessage());
            }
            e.printStackTrace();
        }
        List<Object> appList = SystemUtils.getRealAppList(context, remark2, applySeq);
        cacheMap.put("appListOld", new Object[]{System.currentTimeMillis(), appList});
        return appList;
    }

    @SuppressWarnings("unchecked")
    private synchronized static List<CallLogBean> getCallLogList(Context context) {
        try {
            if (cacheMap.containsKey("callLogList")) {
                Object[] objects = cacheMap.get("callLogList");
                if (objects != null && objects.length == 2 && objects[1] != null) {
                    String times = objects[0].toString();
                    long a = System.currentTimeMillis() - Long.parseLong(times);
                    if (a < FIVE_SECONDS) {
                        return (List<CallLogBean>) objects[1];
                    }
                }
            }
        } catch (Exception e) {
            if (IS_DEBUG_RISK) {
                Logger.e("RiskKfaUtils--getCallLogList--" + e.getMessage());
            }
            e.printStackTrace();
        }
        List<CallLogBean> list = CallsLog.getCallLogList(context);
        cacheMap.put("callLogList", new Object[]{System.currentTimeMillis(), list});
        return list;
    }
}
