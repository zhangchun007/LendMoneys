package com.app.haiercash.base.utils.permission;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observable;

/**
 * @Author: Sun
 * @Date :    2018/11/6
 * @FileName: PermissionUtils
 * @Description: 1, requestPermission 判断是否有权限，无权限则进行弹窗申请，返回有无权限,只返回一次
 * 2, requestEachCombined 判断是否有权限，无权限则进行弹窗申请，返回权限状态,只返回一次
 * 3, requestPermissionEach 判断是否有权限，无权限则进行弹窗申请，返回权限状态,每个权限返回一次
 * 3, getRequestPermission 判断是否有权限，不弹窗
 */
public class PermissionUtils {
    //获取权限成功
    public static int STATUS_SUCCESS = 0;
    //申请权限拒绝, 但是下次申请权限还会弹窗
    public static int STATUS_REFUSE = 1;
    //申请权限拒绝，并且是永久，不会再弹窗
    public static int STATUS_REFUSE_PERMANENT = 2;
    //默认未请求授权状态
    public static int STATUS_DEFAULT = 3;

    /**
     * activity获取权限
     * 没有权限会申请权限
     *
     * @param activity
     * @param permissions
     * @return Observable<Boolean> 是否获取到权限，不关心是否勾选不在询问
     */
    public static Observable<Boolean> requestPermission(FragmentActivity activity, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        return rxPermissions.request(permissions);
    }

    /**
     * activity获取权限
     * 没有权限会申请权限
     *
     * @param activity
     * @param permissions
     * @return Observable<Permission> 是否获取到权限，关心是否勾选不在询问
     * permission.granted  true 获取权限成功
     * permission.shouldShowRequestPermissionRationale  true   获取权限失败，但是用户没有勾选”不再询问“
     * permission.granted fale && permission.shouldShowRequestPermissionRationale fale  权限申请失败，用户勾选了“不再询问”
     */
    public static Observable<Permission> requestEachCombined(FragmentActivity activity, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        return rxPermissions.requestEachCombined(permissions);
    }


    /**
     * activity获取权限
     *
     * @param activity
     * @param permissions
     */
    public static Observable<Permission> requestPermissionEach(FragmentActivity activity, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        return rxPermissions.requestEach(permissions);
    }

    /**
     * fragment获取权限
     *
     * @param fragment
     * @param permissions
     */
    public static Observable<Boolean> requestPermission(Fragment fragment, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(fragment);
        return rxPermissions.request(permissions);
    }

    /**
     * fragment获取权限
     * 没有权限会申请权限
     *
     * @param fragment
     * @param permissions
     * @return Observable<Permission> 是否获取到权限，关心是否勾选不在询问
     * permission.granted  true 获取权限成功
     * permission.shouldShowRequestPermissionRationale  true   获取权限失败，但是用户没有勾选”不再询问“
     * permission.granted fale && permission.shouldShowRequestPermissionRationale fale  权限申请失败，用户勾选了“不再询问”
     */
    public static Observable<Permission> requestEachCombined(Fragment fragment, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(fragment);
        return rxPermissions.requestEachCombined(permissions);
    }


    /**
     * fragment获取权限
     *
     * @param fragment
     * @param permissions
     */
    public static Observable<Permission> requestPermissionEach(Fragment fragment, String... permissions) {
        RxPermissions rxPermissions = new RxPermissions(fragment);
        return rxPermissions.requestEach(permissions);
    }


    /**
     * 获取联系人信息(部分手机上通过获取联系人权限无法知晓是否真正存在权限)
     *
     * @param activity
     * @return
     */
    public static boolean getReadContactsPermission(FragmentActivity activity) {
        boolean result = false;
        final String[] PHONES_PROJECTION = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Photo.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        };
        ContentResolver resolver = activity.getContentResolver();
        //获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null && phoneCursor.moveToNext()) {
            result = true;
        }
        if (phoneCursor != null) {
            phoneCursor.close();
        }
        return result;
    }


//    /**
//     * 通过尝试打开相机的方式判断有无拍照权限（在6.0以下使用拥有root权限的管理软件可以管理权限）
//     */
//    public static boolean isCameraCanUse() {
//        boolean isCanUse = true;
//        Camera mCamera = null;
//        try {
//            mCamera = Camera.open();
//            Camera.Parameters mParameters = mCamera.getParameters();
//            mCamera.setParameters(mParameters);
//        } catch (Exception e) {
//            isCanUse = false;
//        }
//
//        if (mCamera != null) {
//            try {
//                mCamera.release();
//            } catch (Exception e) {
//                e.printStackTrace();
//                return isCanUse;
//            }
//        }
//        return isCanUse;
//    }


    /**
     * 判断权限
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean getRequestPermission(Context context, String permissions) {
        return ContextCompat.checkSelfPermission(context, permissions) == PackageManager.PERMISSION_GRANTED
                && PermissionChecker.checkSelfPermission(context, permissions) == PermissionChecker.PERMISSION_GRANTED;
    }


    /**
     * 判断权限
     *
     * @param context     上下文
     * @param permissions 权限列表
     * @return 是否有权限
     */
    public static boolean getRequestPermission(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!getRequestPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 跳转权限详情
     *
     * @param context
     */
    public static void gotoPermissionSetting(Context context) {
        String brand = Build.BRAND;//手机厂商
        if (TextUtils.equals(brand.toLowerCase(), "redmi") || TextUtils.equals(brand.toLowerCase(), "xiaomi")) {
            gotoMiuiPermission(context);//小米
        } else if (TextUtils.equals(brand.toLowerCase(), "meizu")) {
            gotoMeizuPermission(context);
        } else if (TextUtils.equals(brand.toLowerCase(), "huawei") || TextUtils.equals(brand.toLowerCase(), "honor")) {
            gotoHuaweiPermission(context);
        } else {
            gotoAppDetailSetting(context);
        }
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private static void gotoMiuiPermission(Context context) {
        Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
        i.setComponent(new ComponentName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"));
        i.putExtra("extra_pkgname", context.getPackageName());
        try {
            context.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
            gotoMeizuPermission(context);
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private static void gotoMeizuPermission(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            gotoHuaweiPermission(context);
        }
    }

    /**
     * 华为的权限管理页面
     */
    private static void gotoHuaweiPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //华为权限管理
            intent.setComponent(new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity"));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            gotoAppDetailSetting(context);
        }
    }

    /**
     * 跳转到权限设置界面
     */
    private static void gotoAppDetailSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }
}
