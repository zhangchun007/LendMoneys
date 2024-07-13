package com.app.haiercash.base.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Looper;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com<br/>
 * 版    本：1.0<br/>
 * 创建日期：2021/3/15<br/>
 * 描    述：Activity反劫持检测工具<br/>
 * 修订历史：在用户使用app的时候,如果被恶意程序劫持跳转到别的界面,
 * 这个时候我们就要做出预警提示用户,告诉用户当前界面已经不是我们的app有潜在的危险.
 * 代码的工作原理很简单就是在我们所写的activity对象的Onstop生命周期判断,
 * 将要跳转的界面是否是安全的。具体代码如下：<br/>
 * ================================================================
 */
public class AntiHijackingUtil {
    public static final String TAG = "AntiHijackingUtil";

    /**
     * 检测当前Activity是否安全
     */
    public static boolean checkActivity(Context context) {
        PackageManager pm = context.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations =
                pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm));// 排序

        List<String> safePackages = new ArrayList<>();
        for (ApplicationInfo app : listAppcations) {// 这个排序必须有.
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                safePackages.add(app.packageName);
            }
        }
        // 得到所有的系统程序包名放进白名单里面.
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivityPackageName;
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        if (sdkVersion >= 21) {// 获取系统api版本号,如果是5x系统就用这个方法获取当前运行的包名
            runningActivityPackageName = getCurrentPkgName(context);
        } else {
            runningActivityPackageName =
                    activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }
        // 如果是4x及以下,用这个方法.
        if (runningActivityPackageName != null) {
            // 有些情况下在5x的手机中可能获取不到当前运行的包名，所以要非空判断。
            if (runningActivityPackageName.equals(context.getPackageName())) {
                return true;
            }
            // 白名单比对
            for (String safePack : safePackages) {
                if (safePack.equals(runningActivityPackageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getCurrentPkgName(Context context) {
        // 5x系统以后利用反射获取当前栈顶activity的包名.
        ActivityManager.RunningAppProcessInfo currentInfo = null;
        Field field = null;
        int START_TASK_TO_FRONT = 2;
        String pkgName = null;
        try {
            // 通过反射获取进程状态字段.
            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List appList = am.getRunningAppProcesses();
        ActivityManager.RunningAppProcessInfo app;
        for (int i = 0; i < appList.size(); i++) {
            //ActivityManager.RunningAppProcessInfo app : appList
            app = (ActivityManager.RunningAppProcessInfo) appList.get(i);
            //表示前台运行进程.
            if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                Integer state = null;
                try {
                    state = field.getInt(app);// 反射调用字段值的方法,获取该进程的状态.
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 根据这个判断条件从前台中获取当前切换的进程对象
                if (state != null && state == START_TASK_TO_FRONT) {
                    currentInfo = app;
                    break;
                }
            }
        }
        if (currentInfo != null) {
            pkgName = currentInfo.processName;
        }
        return pkgName;
    }

    /**
     * 判断当前是否在桌面
     *
     * @param context 上下文
     */
    private static boolean isHome(Context context) {
        ActivityManager mActivityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes(context).contains(rti.get(0).topActivity.getPackageName());
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private static List<String> getHomes(Context context) {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    /**
     * 判断当前是否在锁屏再解锁状态
     *
     * @param context 上下文
     */
    private static boolean isReflectScreen(Context context) {
        KeyguardManager mKeyguardManager =
                (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }


    public static void activityPageOnPause(final Context context) {
//若程序进入后台不是用户自身造成的，则需要弹出警示
//        if(needAlarm) {
        //弹出警示信息
//            Toast.makeText(getApplicationContext(), "您的登陆界面被覆盖，请确认登陆环境是否安全", Toast.LENGTH_SHORT).show();
//            //启动我们的AlarmService,用于给出覆盖了正常Activity的类名
//            Intent intent = new Intent(this, AlarmService.class);
//            startService(intent);
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 白名单
                boolean safe = AntiHijackingUtil.checkActivity(context);
                // 系统桌面
                boolean isHome = AntiHijackingUtil.isHome(context);
                // 锁屏操作
                boolean isReflectScreen = AntiHijackingUtil.isReflectScreen(context);
                // 判断程序是否当前显示
                if (!safe && !isHome && !isReflectScreen) {
                    Looper.prepare();
                    Toast.makeText(context, "您的登陆界面被覆盖，请确认登陆环境是否安全", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        }).start();
    }
}
