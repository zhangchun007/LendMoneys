package com.app.haiercash.base.utils.system;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.content.Context.APP_OPS_SERVICE;

/**
 * 跳转系统消息通知和权限页面
 */
public class PermissionPageUtils {

    public static void jumpPermissionPage(Context context, String applicationId) {
        String brand = Build.BRAND;
        switch (brand.toLowerCase()) {
            case "xiaomi":
                gotoMiuiPermission(context);//小米
                break;
            case "redmi":
                gotoMiuiPermission(context);//小米
                break;
            case "huawei":
                gotoHuaweiPermission(context);
                break;
            case "honor":
                gotoHuaweiPermission(context);
                break;
            case "meizu":
                gotoMeizuPermission(context, applicationId);
                break;
            case "samsung":
                context.startActivity(getAppDetailSettingIntent(context));
                break;
            case "sony":
                goSonyMainager(context);
                break;
            case "lg":
                goLGMainager(context);
                break;
            case "vivo":
                goVivoPermission(context, applicationId);
                break;
            case "letv":
                goLetvPermission(context, applicationId);
                break;
            case "oppo":
                goOppoPermission(context, applicationId);
                break;
            default:
                context.startActivity(getAppDetailSettingIntent(context));
                break;
        }
    }

    private static void goLetvPermission(Context context, String applicationId) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", applicationId);
            ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    private static void goVivoPermission(Context context, String applicationId) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", applicationId);
            ComponentName comp = new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    private static void goOppoPermission(Context context, String applicationId) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", applicationId);
            ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }


    private static void goLGMainager(Context context) {
        try {
            Intent intent = new Intent(context.getPackageName());
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    private static void goSonyMainager(Context context) {
        try {
            Intent intent = new Intent(context.getPackageName());
            ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    /**
     * meizu
     *
     * @param context
     */
    private static void gotoMeizuPermission(Context context, String applicationId) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", applicationId);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }

    }

    /**
     * 跳转到huawei的权限管理页面
     *
     * @param context
     */
    private static void gotoHuaweiPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    /**
     * 跳转到miui的权限管理页面
     *
     * @param context
     */
    private static void gotoMiuiPermission(Context context) {
        try { // MIUI 8
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(localIntent);
        } catch (Exception e) {
            try { // MIUI 5/6/7
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(localIntent);
            } catch (Exception e1) { // 否则跳转到应用详情
                context.startActivity(getAppDetailSettingIntent(context));
            }
        }
    }

    private static Intent getAppDetailSettingIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }

    /*
     * 判断通知权限是否打开
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnable(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            return notificationManagerCompat.areNotificationsEnabled();
        } else {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();

            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;

            try {
                Method checkOpNoThrowMethod = AppOpsManager.class.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = AppOpsManager.class.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
