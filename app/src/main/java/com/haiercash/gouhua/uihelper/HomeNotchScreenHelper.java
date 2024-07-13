package com.haiercash.gouhua.uihelper;

import android.app.Activity;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.WindowInsets;

import com.app.haiercash.base.utils.system.CheckUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 首页刘海屏或全面屏适配
 */
public class HomeNotchScreenHelper {
    /**
     * 是否有刘海屏
     */
    public static boolean hasNotchInScreen(Activity activity) {
        try {
            // 通过其他方式判断是否有刘海屏  目前官方提供有开发文档的就 小米，vivo，华为（荣耀），oppo,meizu,
            String manufacturer = Build.MANUFACTURER;
            if (CheckUtil.isEmpty(manufacturer)) {
                return false;
            } else if ("HUAWEI".equalsIgnoreCase(manufacturer)) {
                return hasNotchHw(activity);
            } else if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                return hasNotchXiaoMi(activity);
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                return hasNotchOPPO(activity);
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                return hasNotchVIVO();
            } else if ("Meizu".equalsIgnoreCase(manufacturer)) {
                return hasNotchMEIZU(activity);
            } else if ("Lenovo".equalsIgnoreCase(manufacturer)) {
                return hasNotchLenovo(activity);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // android  P 以上有标准 API 来判断是否有刘海屏
                WindowInsets rootWindowInsets = activity.getWindow().getDecorView().getRootWindowInsets();
                if (rootWindowInsets != null) {
                    DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
                    // 说明有刘海屏
                    return displayCutout != null;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 联想刘海屏
     * http://open.lenovo.com/sdk/%E5%85%A8%E9%9D%A2%E5%B1%8F%E9%80%82%E9%85%8D%E6%8C%87%E5%8D%97/
     */
    private static boolean hasNotchLenovo(Activity activity) {
        return activity.getResources().getIdentifier("config_screen_has_notch", "bool", "android") > 0;
    }

    /**
     * 魅族刘海屏
     */
    private static boolean hasNotchMEIZU(Activity activity) {
        try {
            Class<?> clazz = Class.forName("flyme.config.FlymeFeature");
            Field field = clazz.getDeclaredField("IS_FRINGE_DEVICE");
            return (Boolean) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 判断vivo是否有刘海屏
     * https://swsdl.vivo.com.cn/appstore/developer/uploadfile/20180328/20180328152252602.pdf
     */
    private static boolean hasNotchVIVO() {
        try {
            Class<?> c = Class.forName("android.util.FtFeature");
            Method get = c.getMethod("isFeatureSupport", int.class);
            return (boolean) (get.invoke(c, 0x20));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断oppo是否有刘海屏
     * https://open.oppomobile.com/wiki/doc#id=10159
     */
    private static boolean hasNotchOPPO(Activity activity) {
        return activity.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    /**
     * 判断xiaomi是否有刘海屏
     * https://dev.mi.com/console/doc/detail?pId=1293
     */
    private static boolean hasNotchXiaoMi(Activity activity) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("getInt", String.class, int.class);
            return (int) (get.invoke(c, "ro.miui.notch", 0)) == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断华为是否有刘海屏
     * https://devcenter-test.huawei.com/consumer/cn/devservice/doc/50114
     */
    private static boolean hasNotchHw(Activity activity) {

        try {
            ClassLoader cl = activity.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            return (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            return false;
        }
    }
}
