package com.haiercash.gouhua.beans.risk;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.app.haiercash.base.utils.system.SystemUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class SysCollect {

    /**
     * 获取CPU架构（指令集）
     */
    public static String cpuAbi() {
        String[] abis;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        StringBuilder abiStr = new StringBuilder();
        for (String abi : abis) {
            abiStr.append(abi);
            abiStr.append(',');
        }
        //Log.i("ceshi", "CPU架构: " + abiStr.toString());
        return abiStr.toString();
    }

    /**
     * 传感器信息
     */
    public static String getSensorList(Context context) {
        // 打印每个传感器信息
        StringBuilder strLog = new StringBuilder();
        if (SystemUtils.isAllow) {
            // 获取传感器管理器
            SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            // 获取全部传感器列表
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            int iIndex = 1;
            for (Sensor item : sensors) {
                strLog.append(iIndex + ".");
                strLog.append(" Sensor Type - " + item.getType() + "\r\n");
                strLog.append(" Sensor Name - " + item.getName() + "\r\n");
                strLog.append(" Sensor Version - " + item.getVersion() + "\r\n");
                strLog.append(" Sensor Vendor - " + item.getVendor() + "\r\n");
                strLog.append(" Maximum Range - " + item.getMaximumRange() + "\r\n");
                strLog.append(" Minimum Delay - " + item.getMinDelay() + "\r\n");
                strLog.append(" Power - " + item.getPower() + "\r\n");
                strLog.append(" Resolution - " + item.getResolution() + "\r\n");
                strLog.append("\r\n");
                iIndex++;
            }
        }
        //System.out.println(strLog.toString());
        return strLog.toString();
    }

    /**
     * 计算总空间大小
     */
    public static long getAllSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getBlockCount();
        return availableBlocks * blockSize;
        //(availableBlocks * blockSize)/1024      KIB 单位
        //(availableBlocks * blockSize)/1024 /1024  MIB单位
    }

    /**
     * 获取 CPU 核数
     */
    public static int getNumberOfCPUCores() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Gingerbread doesn't support giving a single application access to both cores, but a
            // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
            // chipset and Gingerbread; that can let an app in the background run without impacting
            // the foreground application. But for our purposes, it makes them single core.
            return 1;
        }
        int cores = 1;
        try {
            if (SystemUtils.isAllow) {
                FileFilter CPU_FILTER = pathname -> {
                    String path = pathname.getName();
                    //regex is slow, so checking char by char.
                    if (path.startsWith("cpu")) {
                        for (int i = 3; i < path.length(); i++) {
                            if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                };
                cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
            }
        } catch (SecurityException e) {
            cores = 1;
        } catch (NullPointerException e) {
            cores = 1;
        }
        return cores;
    }

    /**
     * 基站信息
     */
    public static String getCell(Context context) {
        String cell = "";
        try {
            if (SystemUtils.isAllow) {
                TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                @SuppressLint("MissingPermission")
                CellLocation cel = tel.getCellLocation();
                if (tel.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {//如果是电信卡的话
                    CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cel;
                    int cid = cdmaCellLocation.getBaseStationId();
                    int lac = cdmaCellLocation.getNetworkId();
                    cell = cid + " " + lac;
                } else {//如果是移动和联通的话  移动联通一致
                    GsmCellLocation gsmCellLocation = (GsmCellLocation) cel;
                    int cid = gsmCellLocation.getCid();
                    int lac = gsmCellLocation.getLac();
                    cell = cid + " " + lac;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cell;
    }

    /**
     * 判断当前是否是debug模式
     *
     * @param context 上下文
     * @return true或false
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取国家码
     */
    public static String getCountryZipCode(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getSimCountryIso().toUpperCase();
    }

    /**
     * 获取系统语言
     */
    public static String getSysLanguage() {
        //方式一
        //Locale locale = getResources().getConfiguration().locale;
        //方式二
        Locale locale = Locale.getDefault();
        //获取当前系统语言
        return locale.getLanguage();
    }

    /**
     * 获取网络状态
     */
    public static String[] getDns(Context context) {
        if (SystemUtils.isAllow) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (Network network : cm.getAllNetworks()) {
                    NetworkInfo networkInfo = cm.getNetworkInfo(network);
                    if (networkInfo.getType() == activeNetworkInfo.getType()) {
                        LinkProperties lp = cm.getLinkProperties(network);
                        for (InetAddress addr : lp.getDnsServers()) {
                            // Get DNS IP address here:
                            return new String[]{addr.getHostAddress(), addr.getHostName()};
                        }

                    }
                }
            }
        }
        return new String[]{"", ""};
    }

    /**
     * 获取eth0的ip： getIpAddrForInterfaces("eth0")java
     */
    public static String getIpAddrForInterfaces(String interfaceName) {
        try {
            if (SystemUtils.isAllow) {
                Enumeration<NetworkInterface> enNetworkInterface = NetworkInterface.getNetworkInterfaces(); //获取本机全部的网络接口
                while (enNetworkInterface.hasMoreElements()) { //判断 Enumeration 对象中是否还有数据
                    NetworkInterface networkInterface = enNetworkInterface.nextElement(); //获取 Enumeration 对象中的下一个数据
                    if (!networkInterface.isUp()) { // 判断网口是否在使用
                        continue;
                    }
                    if (!interfaceName.equals(networkInterface.getDisplayName())) { // 网口名称是否和须要的相同
                        continue;
                    }
                    Enumeration<InetAddress> enInetAddress = networkInterface.getInetAddresses(); //getInetAddresses 方法返回绑定到该网卡的全部的 IP 地址。
                    while (enInetAddress.hasMoreElements()) {
                        InetAddress inetAddress = enInetAddress.nextElement();
                        if (inetAddress instanceof Inet4Address) { //判断是否未ipv4
                            return inetAddress.getHostAddress();
                        }
                        // 判断未lo时
                        // if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        // return inetAddress.getHostAddress();

                        // }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * 获取eth0的子网掩码：getIpAddrMaskForInterfaces("eth0")网络
     */
    public static String getIpAddrMaskForInterfaces(String interfaceName) {
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces(); //获取本机全部的网络接口
            while (networkInterfaceEnumeration.hasMoreElements()) { //判断 Enumeration 对象中是否还有数据
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement(); //获取 Enumeration 对象中的下一个数据
                if (!networkInterface.isUp() && !interfaceName.equals(networkInterface.getDisplayName())) { //判断网口是否在使用，判断是否时咱们获取的网口
                    continue;
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) { //
                    if (interfaceAddress.getAddress() instanceof Inet4Address) { //仅仅处理ipv4
                        return calcMaskByPrefixLength(interfaceAddress.getNetworkPrefixLength()); //获取掩码位数，经过 calcMaskByPrefixLength 转换为字符串
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "error";
    }

    private static String calcMaskByPrefixLength(int length) {
        int mask = 0xffffffff << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;
        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }
        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }


    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;

    /**
     * 获取WIFI热点的状态：
     */
    public int getWifiApState(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(wifiManager);
            //Log.i(TAG,"wifi state: " + i);
            return i;
        } catch (Exception e) {
            //Log.e(TAG,"Cannot get WiFi AP state" + e);
            return WIFI_AP_STATE_FAILED;
        }
    }

    /**
     * 判断Wifi热点是否可用：
     */
    public static ArrayList getConnectedHotIP() {
        ArrayList connectedIP = new ArrayList();
        if (SystemUtils.isAllow) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] splitted = line.split(" +");
                    if (splitted != null && splitted.length >= 4) {
                        String ip = splitted[0];
                        connectedIP.add(ip);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connectedIP;
    }

    /**
     * 获取热点主机ip地址的代码：
     */
    public static String getHostIp(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo info = wifiManager.getDhcpInfo();
        //System.out.println(info.serverAddress);
        return info.serverAddress + "";
    }

    /**
     * 当前wifi的信号强度
     */
    public static String getWifiRssi(Context mContext) {
        WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        int wifi = mWifiInfo.getRssi();//获取wifi信号强度
        if (wifi > -50 && wifi < 0) {//最强
            //Log.e(TAG, "最强");
            return "最强";
        } else if (wifi > -70 && wifi < -50) {//较强
            //Log.e(TAG, "较强");
            return "较强";
        } else if (wifi > -80 && wifi < -70) {//较弱
            //Log.e(TAG, "较弱");
            return "较弱";
        } else if (wifi > -100 && wifi < -80) {//微弱
            //Log.e(TAG, "微弱");
            return "微弱";
        } else {
            return "未知";
        }
    }

    /**
     * 获取当前网络连接类型
     */
    public static String getNetworkState(Context context) {
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //如果当前没有网络
        if (null == connManager)
            return "没有网络连接";
        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return "没有网络连接";
        }
        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return "wifi连接";
                }
        }
        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return "2G连接";
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return "3G连接";
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return "4G连接";
                        default:
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA")
                                    || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return "3G连接";
                            } else {
                                return "NETWORN_MOBILE";
                            }
                    }
                }
        }
        return "wifi连接";
    }
}
