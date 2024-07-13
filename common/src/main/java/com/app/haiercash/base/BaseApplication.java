package com.app.haiercash.base;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.app.haiercash.base.utils.system.SystemUtils;

import java.lang.reflect.Field;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/5/8<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BaseApplication extends Application {
    public static Context CONTEXT;//可全局使用的context，不能进行ui操作
    //命名规则：所有最后带"/"均为文件夹，不是单个文件
    //String path = Environment.getExternalStoragePublicDirectory(SAVE_DATA_PATH).getPath;//注意：此path最后不带"/"
    public static final String SAVE_DATA_PATH = "gouhua/";//所有保存内容需要在该文件夹中分别建立不同类型的存储文件夹

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        fixOppoAssetManager();
    }

    /**
     * 修复bug:android.content.res.AssetManager.finalize() timed out after 120 seconds
     * 延长计时时间
     */
    private void fixOppoAssetManager() {
        String device = SystemUtils.getDeviceModel();
        if (!TextUtils.isEmpty(device)) {
            if (device.contains("OPPO") && (device.contains("R9") || device.contains("A5"))) {
                try {
                    Class<?> c = Class.forName("java.lang.Daemons");
                    Field maxField = c.getDeclaredField("MAX_FINALIZE_NANOS");
                    maxField.setAccessible(true);
                    maxField.set(null, Long.MAX_VALUE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    /**
//     * 关掉这个负责计时的
//     */
//    private void fixOppoAssetManager() {
//        String device = SystemUtils.getDeviceModel();
//        if (!TextUtils.isEmpty(device)) {
//            if (device.contains("OPPO R9") || device.contains("OPPO A5")) {
//                try {
//                    // 关闭掉FinalizerWatchdogDaemon
//                    Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
//                    Method method = clazz.getSuperclass().getDeclaredMethod("stop");
//                    method.setAccessible(true);
//                    Field field = clazz.getDeclaredField("INSTANCE");
//                    field.setAccessible(true);
//                    method.invoke(field.get(null));
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
