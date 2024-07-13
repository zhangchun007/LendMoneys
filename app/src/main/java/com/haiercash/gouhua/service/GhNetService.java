//package com.haiercash.gouhua.service;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.IBinder;
//
//import com.app.haiercash.base.net.api.INetResult;
//import com.app.haiercash.base.net.bean.BasicResponse;
//import com.app.haiercash.base.utils.json.JsonUtils;
//import com.app.haiercash.base.utils.log.Logger;
//import com.app.haiercash.base.utils.rxbus.ActionEvent;
//import com.app.haiercash.base.utils.rxbus.RxBus;
//import com.app.haiercash.base.utils.sp.SpHelper;
//import com.haiercash.gouhua.base.ApiUrl;
//import com.haiercash.gouhua.beans.IconMain;
//import com.haiercash.gouhua.interfaces.SpKey;
//import com.haiercash.gouhua.network.NetHelper;
//import com.haiercash.gouhua.fragments.main.MainHelper;
//import com.app.haiercash.base.utils.system.CheckUtil;
//import com.app.haiercash.base.utils.FileUtils;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import androidx.work.Constraints;
//import androidx.work.NetworkType;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;
//
///**
// * ================================================================
// * 作    者：stone<br/>
// * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
// * 版    本：1.0<br/>
// * 创建日期：2019/1/22<br/>
// * 描    述：<br/>
// * 修订历史：<br/>
// * ================================================================
// */
//public class GhNetService extends Service implements INetResult {
//
//    /**
//     * 1:日志上送
//     * 2：加载首页ICON
//     */
//    public static final String DO_TYPE_FLAG = "doTypeFlag";
//
//    private static final String TAG_MESSAGE = "message";
//
//    private NetHelper netHelper;
//    private String md5IconStyleContent;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
////            NotificationChannel Channel = new NotificationChannel("123", "主服务", NotificationManager.IMPORTANCE_HIGH);
////            Channel.enableLights(true);//设置提示灯
////            Channel.setLightColor(Color.RED);//设置提示灯颜色
////            Channel.setShowBadge(true);//显示logo
////            Channel.setDescription("ytzn");//设置描述
////            Channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); //设置锁屏可见 VISIBILITY_PUBLIC=可见
////            manager.createNotificationChannel(Channel);
////
////            Notification notification = new Notification.Builder(this).setChannelId("123").setContentTitle("主服务").setContentText("运行中...").setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).build();
////            startForeground(1, notification);
////        }
//
//        netHelper = new NetHelper(this);
//    }
//
//
//    /**
//     * 此函数具有一个int型的返回值，具体的可选值及含义如下：
//     * START_NOT_STICKY：当Service因为内存不足而被系统kill后，接下来未来的某个时间内，即使系统内存足够可用，系统也不会尝试重新创建此Service
//     * 。除非程序中Client明确再次调用startService(...)启动此Service。
//     * START_STICKY：当Service因为内存不足而被系统kill后，接下来未来的某个时间内，当系统内存足够可用的情况下，系统将会尝试重新创建此Service
//     * ，一旦创建成功后将回调onStartCommand(...)方法，但其中的Intent将是null，pendingintent除外。
//     * START_REDELIVER_INTENT：与START_STICKY唯一不同的是，回调onStartCommand(...)
//     * 方法时，其中的Intent将是非空，将是最后一次调用startService(...)中的intent。
//     */
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null) {
//            int doFlag = intent.getIntExtra(DO_TYPE_FLAG, 1);
//            if (doFlag == 1) {
//                netHelper.getService(ApiUrl.HOME_ICON_CHECK_URL, null);
//            }
//        }
//        return START_NOT_STICKY;
//    }
//
//
//    @Override
//    public <T> void onSuccess(T t, String url) {
//        if (ApiUrl.HOME_ICON_CHECK_URL.equals(url)) {
//            Map iconTypeMap = (Map) t;
//            String localContent = SpHelper.getInstance().readMsgFromSp(SpKey.OTHER, SpKey.HOME_ICON_MD5);
//            md5IconStyleContent = String.valueOf(iconTypeMap.get("md5IconStyleContent"));
//            String showSetting = String.valueOf(iconTypeMap.get("showSetting"));
//            if (!CheckUtil.isEmpty(md5IconStyleContent)) {
//                if (!localContent.equals(md5IconStyleContent)) {
//                    Map<String, String> map = new HashMap<>(1);
//                    map.put("iconType", "base64");
//                    netHelper.getService(ApiUrl.HOME_ICON_URL, map);
//                } else {
//                    String filePath = MainHelper.getIconFilePath();
//                    if (!CheckUtil.isEmpty(filePath)) {
//                        File file = new File(filePath);
//                        if (!file.exists() || file.list() == null || file.list().length >= 8) {
//                            FileUtils.deleteFile(file);
//                            //下载文件;
//                            Map<String, String> map = new HashMap<>(1);
//                            map.put("iconType", "base64");
//                            netHelper.getService(ApiUrl.HOME_ICON_URL, map);
//                        } else {
//                            stopSelf();
//                        }
//                    } else {
//                        stopSelf();
//                    }
//                }
//            } else {
//                deleteIconAndReset();
//                stopSelf();
//            }
//        } else if (ApiUrl.HOME_ICON_URL.equals(url)) {
//            final List<IconMain> list = JsonUtils.fromJsonArray(t, "iconStyles", IconMain.class);
//            ExecutorService executorService = Executors.newSingleThreadExecutor();
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    if (MainHelper.saveHomeIcon(list)) {
//                        SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.HOME_ICON_MD5, md5IconStyleContent);
//                        //通知首页更新Icon
//                        RxBus.getInstance().post(new ActionEvent(ActionEvent.MainShowIcon, "newIcon"));
//                    } else {
//                        deleteIconAndReset();
//                    }
//                    stopSelf();
//                }
//            });
//        }
//    }
//
//    private void deleteIconAndReset() {
//        String filePath = MainHelper.getIconFilePath();
//        if (!CheckUtil.isEmpty(filePath)) {
//            FileUtils.deleteFile(new File(filePath));
//        }
//        SpHelper.getInstance().deleteMsgFromSp(SpKey.OTHER, SpKey.HOME_ICON_MD5);
//        //通知首页更新Icon
//        RxBus.getInstance().post(new ActionEvent(ActionEvent.MainShowIcon, "default"));
//    }
//
//    @Override
//    public void onError(BasicResponse error, String url) {
//        if (ApiUrl.HOME_ICON_CHECK_URL.equals(url) || ApiUrl.HOME_ICON_URL.equals(url)) {
//            Logger.d("数据请求失败：" + url + "\n" + error.getHead().getRetMsg());
//        }
//        stopSelf();
//    }
//
//
//    @Override
//    public void onDestroy() {
//        if (netHelper != null) {
//            netHelper.recoveryNetHelper();
//        }
//        Logger.e("Service 完成任务  销毁");
//        super.onDestroy();
//    }
//
//
//    /**
//     * @param flag 1:加载首页ICON<p>
//     *             <p>
//     *             Android 8.0的应用尝试在不允许其创建后台服务的情况下使用 startService() 函数，则该函数将引发一个
//     *             IllegalStateException。
//     */
//    public static void startIntentService(Context context, int flag, String str) {
////        try {
//        Intent intent = new Intent(context, GhNetService.class);
//        intent.putExtra(DO_TYPE_FLAG, flag);
//        intent.putExtra(TAG_MESSAGE, str);
//        context.startService(intent);
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                context.startForegroundService(intent);
////            } else {
////                context.startService(intent);
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }
//
//}
