package com.app.haiercash.base.utils.system;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.app.haiercash.base.CommonManager;
import com.app.haiercash.base.net.config.CommonSpKey;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.permission.PermissionUtils;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.time.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/21
 * 描    述：系统工具类
 * 修订历史：
 * ================================================================
 */
public class SystemUtils {
    private static final String TAG = "SystemUtils";
    /**
     * 是否允许获取相关的设备信息
     */
    public static boolean isAllow = false;

    /**
     * 获取包名
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }


    public static String getCurrentProcessNameByFile() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return "com.haiercash.gouhua";
        }
    }


    /**
     * 获得当前进程的名字
     *
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }


    /**
     * 判断应用是否已经启动
     *
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        //获取Android设备中所有正在运行的App
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : processInfos) {
            if (appProcess.processName.equals(packageName)) {
                Log.i("NotificationLaunch", String.format("the %s is running, isAppAlive return " + "true", packageName));
                return true;
            }
        }
        Log.i("NotificationLaunch", String.format("the %s is not running, isAppAlive return " + "false", packageName));
        return false;
    }

    /**
     * 判断APP是否前台运行
     */
    public static boolean isBackground(Context context) {
        context = context.getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Logger.i(context.getPackageName(), "此appimportace =" + appProcess.importance + "," + "context.getClass().getName()=" + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Logger.i(context.getPackageName(), "处于后台" + appProcess.processName);
                    return true;
                } else {
                    Logger.i(context.getPackageName(), "处于前台" + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 查看系统是否是64 or 32
     *
     * @return
     */
    public static boolean isSupport64Bit() {
        boolean isSupport64bit = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isSupport64bit = Build.SUPPORTED_64_BIT_ABIS.length > 0;
        } else {
            try {
                BufferedReader localBufferedReader =
                        new BufferedReader(new FileReader("/proc/cpuinfo"));
                if (localBufferedReader.readLine().contains("aarch64")) {
                    isSupport64bit = true;
                }
                localBufferedReader.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return isSupport64bit;
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @return true 表示开启
     */
    public static boolean isOpenLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    /**
     * 判断GPS是否开启，
     *
     * @return true 表示开启
     */
    public static boolean isOpenGpsProvider(final Context context) {
        LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断是否安装过APP
     */
    public static boolean isAppInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    /**
     * 获取Android Id
     */
    public static String getAndroidId(Context context) {
        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        //return Settings.Secure.ANDROID_ID;
        //System.out.println("Android Id: Settings.System.getString " + Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        //System.out.println("Android Id: Settings.System.getString " + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        //return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        //}
        //return "";
    }

//    /**
//     * 获取电池容量
//     */
//    public static String getBatteryCapacity(Context context) {
//        Object mPowerProfile;
//        double batteryCapacity = 0; //电池的容量mAh
//        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
//        try {
//            mPowerProfile = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(context);
//            batteryCapacity = (double) Class.forName(POWER_PROFILE_CLASS).getMethod("getBatteryCapacity").invoke(mPowerProfile);
//        } catch (Exception e) {
//            Logger.e("get batteryCapacity mAh error: " + batteryCapacity);
//            e.printStackTrace();
//        }
//        return batteryCapacity + "mAh";
//    }

//    /**
//     * 获取电池电量百分比
//     */
//    public static String getBatteryLevel(Context context) {
//        BatteryManager manager = (BatteryManager) context.getSystemService(context.BATTERY_SERVICE);
//        int currentLevel = 0;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            currentLevel = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//            return currentLevel + "";
//        }
//        return "";
//    }

    /**
     * 获取设备标识码
     */
    public static String getDeviceID(Context context) {
        return getDeviceID(context, false);
    }

    /**
     * @param isOnlyDeviceId 仅获取deviceid
     */
    @SuppressLint({"MissingPermission"})
    public static String getDeviceID(Context context, boolean isOnlyDeviceId) {
        String deviceId = isOnlyDeviceId ? CommonManager.DEFAULT_DEVICEID : SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_COMMON_DEVICE, CommonManager.DEFAULT_DEVICEID);
        if (isAllow && CommonManager.DEFAULT_DEVICEID.equals(deviceId)) {
            try {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O && !isOnlyDeviceId) {
                    deviceId = getAndroidId(context);
                } else {
                    TelephonyManager mTelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    if (mTelephonyMgr != null) {
                        deviceId = mTelephonyMgr.getDeviceId();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = CommonManager.DEFAULT_DEVICEID;
            }
            SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_COMMON_DEVICE, deviceId);
        }
        return deviceId;
    }

    /**
     * 获取手机iccid
     */
    public static String getDeviceICCID(Context context) {
        return getSimInfo(context);
    }

    @SuppressLint("MissingPermission")
    private static String getDeviceIccId(Context context) {
        String imei = "";
        if (isAllow) { //&& PermissionUtils.getRequestPermission(context, "android.permission.READ_PRIVILEGED_PHONE_STATE")
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                imei = tm.getSimSerialNumber();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TextUtils.isEmpty(imei) ? "00000000000000000000" : imei;
    }

    public static String getSimInfo(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        Uri uri = Uri.parse("content://telephony/siminfo");
        Cursor cursor;
        if (isAllow) {
            try {
                ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
                cursor = contentResolver.query(uri, new String[]{"_id", "sim_id", "icc_id", "display_name"}, "0=0", new String[]{}, null);
                if (null != cursor) {
                    while (cursor.moveToNext()) {
                        String icc_id = cursor.getString(cursor.getColumnIndex("icc_id"));
                        String display_name = cursor.getString(cursor.getColumnIndex("display_name"));
                        int sim_id = cursor.getInt(cursor.getColumnIndex("sim_id"));
//                    int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                        if (sim_id != -1) {
                            if (stringBuilder.length() > 0) {
                                stringBuilder.append("|");
                            }
                            stringBuilder.append(icc_id);
                        }
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "".equals(stringBuilder.toString()) ? getDeviceIccId(context) : stringBuilder.toString();
    }

    /**
     * 获取本机号码
     */
    @SuppressLint("MissingPermission")
    public static String getPhoneNum(Context context) {
        if (isAllow) {
            //创建电话管理,与手机建立连接
            TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            //获取手机号码
            if (tm != null) {
                try {
                    if (PermissionUtils.getRequestPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                        return tm.getLine1Number();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * 获取设备型号model、设备品牌brand
     *
     * @param type：model返回model
     */
    public static String getModelOrBrand(String type) {
        if ("model".equals(type)) {
            return Build.MODEL;
        } else {
            return Build.BRAND;
        }
    }


    /**
     * 获取BSSID（所连接的WIFI设备的MAC地址）/获取ssid是否隐藏/获取IP地址/mac地址/联网速度/WiFi名称
     * type: bssid-获取bssid；ssidHint-获取ssid是否隐藏；ip-IP地址；mac-mac地址；speed-联网速度；name-WiFi名称
     *
     * @param type bssid        获取bssid<br/>
     *             ssidHint     获取ssid是否隐藏<br/>
     *             ip           IP地址<br/>
     *             mac          mac地址<br/>
     *             speed       联网速度<br/>
     *             name       WiFi名称<br/>
     */
    public static String getUnderWifiMessage(Context context, String type) {
        if (isAllow) {
            try {
                if ("name".equals(type)) {
                    return getWifiName(context);
                }
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager == null) {
                    return "";
                }
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if ("bssid".equals(type)) {
                    return wifiInfo.getBSSID();
                } else if ("ssidHint".equals(type)) {
                    boolean ssid = wifiInfo.getHiddenSSID();
                    if (ssid) {
                        return "true";
                    } else {
                        return "false";
                    }
                } else if ("ip".equals(type)) {
                    int ip = wifiInfo.getIpAddress();
                    return String.valueOf(ip);
                } else if ("mac".equals(type)) {
                    return wifiInfo.getMacAddress();
                } else if ("speed".equals(type)) {
                    int speed = wifiInfo.getLinkSpeed();
                    return String.valueOf(speed);
                } else {
                    return wifiInfo.getSSID();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取wifi 名称
     */
    public static String getWifiName(Context context) {
        if (isAllow) {
            try {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    assert mWifiManager != null;
                    WifiInfo info = mWifiManager.getConnectionInfo();
                    String wifiName = info.getSSID();
                    if (!CheckUtil.isEmpty(wifiName) && wifiName.startsWith("\"")) {
                        wifiName = wifiName.replace("\"", "");
                    }
                    return wifiName;
                } else {
                    //安卓10需要换一种方式取wifi名称
                    ConnectivityManager connManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    assert connManager != null;
                    NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        if (networkInfo.getExtraInfo() != null) {
                            return networkInfo.getExtraInfo().replace("\"", "");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取WiFi mac地址
     */
    public static String getWifiMac(Context context) {
        String mac = "";
        try {
            if (isAllow && isWifiConnected(context)) {
                //WiFi已连接
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    mac = getMacAddress();
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                    //6.0及以上系统
                    StringBuffer fileData = new StringBuffer(1000);
                    BufferedReader reader = new BufferedReader(new FileReader("/sys/class/net/wlan0/address"));
                    char[] buf = new char[1024];
                    int numRead = 0;
                    while ((numRead = reader.read(buf)) != -1) {
                        String readData = String.valueOf(buf, 0, numRead);
                        fileData.append(readData);
                    }
                    reader.close();
                    mac = fileData.toString().toUpperCase().substring(0, 17);
                } else {
                    WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = wifi.getConnectionInfo();
                    mac = info.getMacAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;
    }

    /**
     * 获取app版本号，当前仅打印Log用，其他地方需要使用再修改
     */
    public static String getAppVersion(Context context) {
        String appVersion;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            appVersion = pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            appVersion = "";
        }
        return appVersion;
    }

    /**
     * 获取app版本号，当前仅打印Log用，其他地方需要使用再修改
     */
    public static int getAppVersionCode(Context context) {
        int appVersionCode;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            appVersionCode = pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            appVersionCode = 0;
        }
        return appVersionCode;
    }

    /**
     * 获取手机版本号，当前仅打印Log用，其他地方需要使用再修改
     */
    public static String getSysVersion() {
        String sysVersion;
        try {
            sysVersion = Build.VERSION.RELEASE;
        } catch (Exception e) {
            e.printStackTrace();
            sysVersion = "";
        }
        return sysVersion;
    }

    /**
     * 获取手机型号，当前仅打印Log用，其他地方需要使用再修改
     */
    public static String getDeviceModel() {
        String deviceModel;
        try {
            deviceModel = Build.MODEL;
        } catch (Exception e) {
            e.printStackTrace();
            deviceModel = "";
        }
        return deviceModel;
    }


    /**
     * @return Serial
     */
    public static String getSerial() {
        String serial;
        try {
            serial = Build.SERIAL;
        } catch (Exception exception) {
            serial = "";
        }
        if (TextUtils.isEmpty(serial)) {
            serial = "";
        }
        Log.e("serial:", serial);
        return serial;
    }


    /**
     * 判断当前wifi连接是否可用
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断当前网络是否可用
     */
    public static boolean checkNetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        @SuppressLint("MissingPermission") NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * 获取屏幕宽度
     */
    public static int getDeviceWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowMgr == null) {
            return 0;
        }
        windowMgr.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getDeviceHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowMgr == null) {
            return 0;
        }
        windowMgr.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    /**
     * 获取渠道ID
     */
    public static String metaDataValueForTDChannelId(Context context) {
        return metaDataValue(context, "TD_CHANNEL_ID");
    }

    /**
     * 获取meta-data值
     */
    public static String metaDataValue(Context context, String name) {
        String value;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = info.metaData.getString(name);
            if (TextUtils.isEmpty(value)) {
                value = String.valueOf(info.metaData.get(name));
            }
        } catch (PackageManager.NameNotFoundException e) {
            value = "NameNotFoundException";
        }
        return value;
    }

//    /**
//     * 获取用户手机app列表
//     */
//    public static List<ApplicationInfo> getAppList(Context context) {
//        List<ApplicationInfo> applicationInfos = new ArrayList<>();
//        try {
//            PackageManager pm = context.getPackageManager();
//            // 查询所有已经安装的应用程序
//            List<ApplicationInfo> appInfos = pm.getInstalledApplications(0);
//            // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
//            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
//            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//            // 通过getPackageManager()的queryIntentActivities方法遍历,得到所有能打开的app的packageName
//            List<ResolveInfo> infoList = pm.queryIntentActivities(resolveIntent, 0);
//            Set<String> allowPackages = new HashSet();
//            for (ResolveInfo resolveInfo : infoList) {
//                allowPackages.add(resolveInfo.activityInfo.packageName);
//            }
//            for (ApplicationInfo app : appInfos) {
//                if (allowPackages.contains(app.packageName)) {
//                    //过滤掉系统app
//                    if ((ApplicationInfo.FLAG_SYSTEM & app.flags) != 0) {
//                        continue;
//                    }
//                    Logger.e("ApplicationInfo", context.getPackageManager().getApplicationLabel(app).toString());
//                    applicationInfos.add(app);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return applicationInfos;
//    }

    //获取有图标的应用
    public static List<Object> getRealAppList(Context context, String remark2, String applySeq) {
        List<Object> mapList = new ArrayList<>();
        if (isAllow) {
            try {
                String deviceID = getDeviceID(context);
                PackageManager pm = context.getPackageManager();
                // 查询所有已经安装的应用程序
                List<PackageInfo> packages = pm.getInstalledPackages(0);  //所有package
                // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
                Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
                resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                // 通过getPackageManager()的queryIntentActivities方法遍历,得到所有能打开的app的packageName
                List<ResolveInfo> infoList = pm.queryIntentActivities(resolveIntent, 0);
                HashSet<String> allowPackages = new HashSet<>();
                for (ResolveInfo resolveInfo : infoList) {
                    allowPackages.add(resolveInfo.activityInfo.packageName);
                }
                List<String> addedPackage = new ArrayList<>();
                for (PackageInfo info : packages) {
                    if (allowPackages.contains((info.packageName))) {
                        if (addedPackage.contains(info.packageName)) {
                            continue;
                        }
                        addedPackage.add(info.packageName);
                        Map<String, String> map = new HashMap<>();
                        map.put("dataTyp", "A701");//风险类型
                        map.put("content", EncryptUtil.simpleEncrypt(info.applicationInfo.loadLabel(context.getPackageManager()).toString()));//app名称
                        map.put("reserved1", deviceID);//设备号
                        map.put("reserved2", TimeUtil.longToString(info.firstInstallTime));//安装时间
                        map.put("reserved3", TimeUtil.longToString(info.lastUpdateTime));//最后一次更新时间
                        map.put("reserved7", info.packageName);//对应安装包名称
                        map.put("reserved6", applySeq);
                        //map.put("remark1", String.valueOf(isSystemApp(packageInfo)));//对应是否系统应用标识
                        map.put("remark2", remark2);
                        map.put("remark3", info.versionName);//对应App版本号
                        mapList.add(map);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mapList;
    }

    public static List<Object> getRealAppListNew(Context context) {
        List<Object> packageInfo = new ArrayList<>();
        if (isAllow) {
            try {
                PackageManager pm = context.getPackageManager();
                // 查询所有已经安装的应用程序
                List<PackageInfo> packages = pm.getInstalledPackages(0);  //所有package
                // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
                Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
                resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                // 通过getPackageManager()的queryIntentActivities方法遍历,得到所有能打开的app的packageName
                List<ResolveInfo> infoList = pm.queryIntentActivities(resolveIntent, 0);
                Set<String> allowPackages = new HashSet<>();
                for (ResolveInfo resolveInfo : infoList) {
                    allowPackages.add(resolveInfo.activityInfo.packageName);
                }
                List<String> addedPackage = new ArrayList<>();
                for (PackageInfo info : packages) {
                    if (allowPackages.contains((info.packageName))) {
                        if (allowPackages.contains((info.packageName))) {
                            if (addedPackage.contains(info.packageName)) {
                                continue;
                            }
                        }
                        addedPackage.add(info.packageName);
                        Map<String, String> map = new HashMap<>();
                        map.put("app_name", info.applicationInfo.loadLabel(context.getPackageManager()).toString());
                        //map.put("isSysApp", EncryptUtil.simpleEncrypt((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 ? "false" : "true"));
                        map.put("app_package", info.packageName);
                        map.put("download_time", TimeUtil.getLongToStringDate(info.firstInstallTime));
                        map.put("update_time", TimeUtil.getLongToStringDate(info.lastUpdateTime));
                        map.put("app_version", info.versionName);
                        packageInfo.add(map);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return packageInfo;
    }
//    /**
//     * 判断是否被Root
//     */
//    public static boolean isRootSystem() {
//        if (isRootSystem1() || isRootSystem2()) {
//            //TODO 可加其他判断 如是否装了权限管理的apk，大多数root 权限 申请需要app配合，也有不需要的，这个需要改su源码。因为管理su权限的app太多，无法列举所有的app，特别是国外的，暂时不做判断是否有root权限管理app
//            //多数只要su可执行就是root成功了，但是成功后用户如果删掉了权限管理的app，就会造成第三方app无法申请root权限，此时是用户删root权限管理app造成的。
//            //市场上常用的的权限管理app的包名   com.qihoo.permmgr  com.noshufou.android.su  eu.chainfire.supersu   com.kingroot.kinguser  com.kingouser.com  com.koushikdutta.superuser
//            //com.dianxinos.superuser  com.lbe.security.shuame com.geohot.towelroot 。。。。。。
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private static boolean isRootSystem1() {
//        File f = null;
//        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/",
//                "/system/sbin/", "/sbin/", "/vendor/bin/"};
//        try {
//            for (int i = 0; i < kSuSearchPaths.length; i++) {
//                f = new File(kSuSearchPaths[i] + "su");
//                if (f != null && f.exists() && f.canExecute()) {
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//        }
//        return false;
//    }
//
//    private static boolean isRootSystem2() {
//        List<String> pros = getPath();
//        File f = null;
//        try {
//            for (int i = 0; i < pros.size(); i++) {
//                f = new File(pros.get(i), "su");
//                System.out.println("f.getAbsolutePath():" + f.getAbsolutePath());
//                if (f != null && f.exists() && f.canExecute()) {
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//        }
//        return false;
//    }
//
//    private static List<String> getPath() {
//        return Arrays.asList(System.getenv("PATH").split(":"));
//    }

//    /**
//     * 判断设备 是否使用代理上网
//     */
//    public static boolean isWifiProxy(Context context) {
//        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
//        String proxyAddress;
//        int proxyPort;
//        if (IS_ICS_OR_LATER) {
//            proxyAddress = System.getProperty("http.proxyHost");
//            String portStr = System.getProperty("http.proxyPort");
//            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
//        } else {
//            proxyAddress = android.net.Proxy.getHost(context);
//            proxyPort = android.net.Proxy.getPort(context);
//        }
//        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
//    }

    /**
     * 是否使用代理   true:使用代理
     */
    public static boolean isWifiProxy(Context context) {
        boolean wifiPrixyState = false;
        try {
            if (isAllow && isWifiConnected(context)) {//wifi 已连接
                final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
                String proxyAddress;
                int proxyPort;
                if (IS_ICS_OR_LATER) {
                    proxyAddress = System.getProperty("http.proxyHost");
                    String portStr = System.getProperty("http.proxyPort");
                    proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
                } else {
                    proxyAddress = android.net.Proxy.getHost(context);
                    proxyPort = android.net.Proxy.getPort(context);
                }
                wifiPrixyState = (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wifiPrixyState;
    }

    /**
     * 是否正在使用VPN
     */
    public static boolean isVpnUsed() {
        try {
            if (isAllow) {
                Enumeration niList = NetworkInterface.getNetworkInterfaces();
                if (niList != null) {
                    @SuppressWarnings("unchecked")
                    ArrayList<NetworkInterface> list = Collections.list(niList);
                    for (NetworkInterface intf : list) {
                        if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                            continue;
                        }
                        Log.d("-----", "isVpnUsed() NetworkInterface Name: " + intf.getName());
                        if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                            return true; // The VPN is up
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            @SuppressLint("InternalInsetResource") int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取底部导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        @SuppressLint("InternalInsetResource") int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //判断底部导航栏是否为显示状态
            boolean navigationBarShowing = isNavigationBarShowing(context);
            if (navigationBarShowing) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    /**
     * 手机是否存在底部导航栏
     */
    private static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            @SuppressLint("PrivateApi") Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            @SuppressWarnings("unchecked") Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            //
        }
        return hasNavigationBar;
    }

    /**
     * 判断导航栏是否为显示状态
     */
    public static boolean isNavigationBarShowing(Context context) {
        //判断手机底部是否支持导航栏显示
        boolean haveNavigationBar = checkDeviceHasNavigationBar(context);
        if (haveNavigationBar) {
                String brand = Build.BRAND;
                String mDeviceInfo;
                if (brand.equalsIgnoreCase("HUAWEI")) {
                    mDeviceInfo = "navigationbar_is_min";
                } else if (brand.equalsIgnoreCase("XIAOMI")) {
                    mDeviceInfo = "force_fsg_nav_bar";
                } else if (brand.equalsIgnoreCase("VIVO")) {
                    mDeviceInfo = "navigation_gesture_on";
                } else if (brand.equalsIgnoreCase("OPPO")) {
                    mDeviceInfo = "navigation_gesture_on";
                } else {
                    mDeviceInfo = "navigationbar_is_min";
                }
                return Settings.Global.getInt(context.getContentResolver(), mDeviceInfo, 0) == 0;
        }
        return false;
    }

    /**
     * 获取外网IP地址，需要异步！！！！！！
     */
    public static String getOutNetIP() {
        URL infoUrl;
        InputStream inStream;
        String line = "";
        try {
            infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    strber.append(line).append("\n");
                }
                inStream.close();
                // 从反馈的结果中提取出IP地址
                int start = strber.indexOf("{");
                int end = strber.indexOf("}");
                String json = strber.substring(start, end + 1);
                if (json != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        line = jsonObject.optString("cip");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    /**
     * 内网IP地址，需要异步！！！！！！
     */
    public static String getHostIp() {
        String hostIp = "";
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hostIp;
    }

    /**
     * 获取IP地址，需要异步！！！！！
     */
    public static String getIpAddress(Context context) {
        if (context == null) {
            return "";
        }
        ConnectivityManager conManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo info = conManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 3/4g网络
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return getHostIp();
                } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    return getOutNetIP(); // 外网地址
                } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    // 以太网有限网络
                    return getHostIp();
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    public static String getMacAddress() {
        String strMacAddr = "";
        try {
            // 获得IpD地址
            InetAddress ip = getLocalInetAddress();
            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
        }
        return strMacAddr;
    }

    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            // 列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface
                    .getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {// 是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface
                        .nextElement();// 得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 设置窗口安全，屏蔽截屏录屏
     */
    public static void setWindowSecure(Activity activity) {
        if (activity != null) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    /**
     * 清除窗口安全设置，允许截屏录屏
     */
    public static void clearWindowSecure(Activity activity) {
        if (activity != null) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    //调用该方法获取是否开启通知栏权限
    public static boolean isNotifyEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return isEnableV26(context);
        } else {
            return isEnabledV19(context);
        }
    }

    /**
     * 8.0以下判断
     *
     * @param context api19  4.4及以上判断
     * @return
     */
    private static boolean isEnabledV19(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            Class appOpsClass;

            try {
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod =
                        appOpsClass.getMethod(CHECK_OP_NO_THROW,
                                Integer.TYPE, Integer.TYPE, String.class);

                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (Integer) opPostNotificationValue.get(Integer.class);

                return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) ==
                        AppOpsManager.MODE_ALLOWED);

            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        } else return true;
    }


    /**
     * 8.0及以上通知权限判断
     *
     * @param context
     * @return
     */
    private static boolean isEnableV26(Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            @SuppressLint("DiscouragedPrivateApi")
            Method sServiceField = notificationManager.getClass().getDeclaredMethod("getService");
            sServiceField.setAccessible(true);
            Object sService = sServiceField.invoke(notificationManager);

            Method method = null;
            if (sService != null) {
                method = sService.getClass().getDeclaredMethod("areNotificationsEnabledForPackage"
                        , String.class, Integer.TYPE);
                method.setAccessible(true);
            }
            return (boolean) method.invoke(sService, pkg, uid);
        } catch (Exception e) {
            return true;
        }
    }

}