package com.app.haiercash.base.utils.router;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.app.haiercash.base.utils.log.Logger;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/5/6<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ActivityUntil {

    private static WeakReference<Activity> sTopActivityWeakRef;
    private static List<Activity> sActivityList = new LinkedList<>();

    public static List<Activity> getActivityList() {
        return sActivityList;
    }

    /**
     * 初始化工具类
     *
     * @param app 应用
     */
    public static void init(@NonNull final Application app) {
        app.registerActivityLifecycleCallbacks(mCallbacks);
    }


    private static Application.ActivityLifecycleCallbacks mCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            sActivityList.add(activity);
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            sActivityList.remove(activity);
            setTopActivityWeakRef(null);
        }
    };

    private static void setTopActivityWeakRef(Activity activity) {
        if (activity == null) {//为空时意味着清除引用
            if (sTopActivityWeakRef != null) {
                sTopActivityWeakRef.clear();
                sTopActivityWeakRef = null;
            }
        } else if (sTopActivityWeakRef == null || !activity.equals(sTopActivityWeakRef.get())) {
            sTopActivityWeakRef = new WeakReference<>(activity);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 关闭指定的全部activity
     */
    public static void finishActivity(List<Class> classList) {
        if (classList == null || classList.size() <= 0) {
            return;
        }
        for (Activity activity : sActivityList) {
            if (classList.contains(activity.getClass())) {
                activity.finish();
            }
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity
     */
    public static void finishOthersActivity(Class<?>... cls) {
        if (cls == null || cls.length <= 0) {
            for (Activity activity : sActivityList) {
                activity.finish();
            }
        } else {
            List<?> list = Arrays.asList(cls);
            for (Activity activity : sActivityList) {
                if (!(list.contains(activity.getClass()))) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity
     */
    public static void finishOthersActivity(List<Class> classList, Class<?>... cls) {
        List<?> list = cls != null && cls.length > 0 ? Arrays.asList(cls) : new ArrayList<>();
        for (Activity activity : sActivityList) {
            if (classList != null && classList.contains(activity.getClass())) {
                continue;
            }
            if (list.contains(activity.getClass())) {
                continue;
            }
            activity.finish();
        }
    }

    /**
     * 关闭指定的Activity
     *
     * @param paths PageKeyParameter中指定的Activity的path路径
     */
    public static void finishActivityByPageKey(String... paths) {
        if (paths != null && paths.length > 0) {
            List<Object> list = new ArrayList<>();
            for (String str : paths) {
                list.add(findActivity(str));
            }
            for (Activity activity : sActivityList) {
                if (list.contains(activity)) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 关闭除了指定activity（PageKeyParameter中指定的activity路径）以外的全部activity
     *
     * @param paths PageKeyParameter中指定的Activity的path路径
     */
    public static void finishOthersActivityByPageKey(String... paths) {
        if (paths == null || paths.length <= 0) {
            for (Activity activity : sActivityList) {
                activity.finish();
            }
        } else {
            List<Object> list = new ArrayList<>();
            for (String str : paths) {
                list.add(findActivity(str));
            }
            for (Activity activity : sActivityList) {
                if (!(list.contains(activity))) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 根据PageKeyParameter中指定的Activity的path路径查找activity
     *
     * @param path PageKeyParameter中指定的路径
     * @return Activity对象
     */
    public static <T> T findActivity(String path) {
        return findActivity(ARouterUntil.getClassByPath(path));
    }


    /**
     * 查询指定的activity
     */
    public static <T> T findActivity(Class cls) {
        for (Activity activity : sActivityList) {
            if (activity.getClass().equals(cls)) {
                return (T) activity;
            }
        }
        return null;
    }

    /**
     * 查询指定的activity是否还没被销毁
     */
    public static boolean findActivity(Class... cls) {
        List<Class> list = Arrays.asList(cls);
        return findActivity(list);
    }

    /**
     * 查询指定的activity是否还没被销毁
     */
    public static boolean findActivity(List<Class> list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        for (Activity activity : sActivityList) {
            if (list.contains(activity.getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取栈顶 Activity
     *
     * @return 栈顶 Activity
     */
    public static Activity getTopActivity() {
        if (sTopActivityWeakRef != null) {
            Activity activity = sTopActivityWeakRef.get();
            if (activity != null) {
                return activity;
            }
        }
        int size = sActivityList.size();
        return size > 0 ? sActivityList.get(size - 1) : null;
    }

    public static int getActivitySize() {
        return sActivityList.size();
    }


    /**
     * 跳转至其他第三方
     *
     * @return 能跳转成功就是true
     */
    public static boolean startOtherApp(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        url = url.trim();
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Logger.e("跳转url不正确");
            return false;
        }
    }

    /**
     * 选择图片
     */
    public static void openFileChooseProcess(Activity context) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        context.startActivityForResult(Intent.createChooser(i, "选择图片"), 0);
    }

    public static void openFilePath(Activity activity, String path) {
        File file = new File(path);
        if (null == file || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file.getParentFile()), "file/*");
        activity.startActivity(intent);
    }

    /**
     * 由浏览器来进行下载安装
     */
    public static void DownloadByBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        context.startActivity(intent);
    }
}
