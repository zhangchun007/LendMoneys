package com.haiercash.gouhua.tplibrary.receiver;

import android.content.Context;
import android.content.Intent;
import android.webkit.URLUtil;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.MainHomeHelper;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/6/12<br/>
 * 描    述：信鸽推送消息<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class XgPushReceiver extends XGPushBaseReceiver {
    //private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");
    public static final String LogTag = "TPushReceiver";
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) {
            return;
        }
        String text;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }
        if (XGUntil.IS_DEBUG_XG) {
            //UiUtil.toast(text);
            Logger.d(LogTag, text);
        }
    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        if (XGUntil.IS_DEBUG_XG) {
            Logger.d(LogTag, text);
        }
    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }
        String text;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
            String number = SpHp.getLogin(SpKey.NOTICE_POINT_OPERATE, "0");
            int numbers = Integer.parseInt(number) - 1;
            SpHp.saveSpLogin(SpKey.NOTICE_POINT_OPERATE, String.valueOf(numbers));
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        if (XGUntil.IS_DEBUG_XG) {
            Logger.d(LogTag, text);
        }
    }

    @Override
    public void onSetAccountResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteAccountResult(Context context, int i, String s) {

    }

    @Override
    public void onSetAttributeResult(Context context, int i, String s) {

    }

    @Override
    public void onQueryTagsResult(Context context, int i, String s, String s1) {

    }

    @Override
    public void onDeleteAttributeResult(Context context, int i, String s) {

    }

    @Override
    public void onRegisterResult(Context context, int errorCode, XGPushRegisterResult message) {
        if (context == null || message == null) {
            return;
        }
        String text;
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "注册成功--" + message;
            // 在这里拿token
            //String token = message.getToken();
        } else {
            if (errorCode == 10100) {
                text = "注册失败，请打开网络！";
            } else {
                text = "注册失败，错误码：" + errorCode;
            }
        }
        if (XGUntil.IS_DEBUG_XG) {
            //UiUtil.toastLongTime(text);
            //Logger.file(text);
            Logger.d(LogTag, text);
            //GhLogService.startUploadLog(context, "信鸽注册 code：" + errorCode + "\n信息：" + message);
        }
    }

    // 消息透传
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        String text = "收到消息:" + message.toString();
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    if (XGUntil.IS_DEBUG_XG) {
                        Logger.d(LogTag, "get custom value:" + value);
                    }
                }
            } catch (Exception e) {
                if (XGUntil.IS_DEBUG_XG) {
                    Logger.e(LogTag, "onTextMessage: " + e.getMessage());
                }
            }
        }
        if (XGUntil.IS_DEBUG_XG) {
            // APP自主处理消息的过程...
            Logger.d(LogTag, text);
            //UiUtil.toastLongTime(context, text);
        }
    }

    @Override
    public void onNotificationClickedResult(Context context, XGPushClickedResult message) {
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancelAll();
        String number = SpHp.getLogin(SpKey.NOTICE_POINT_OPERATE, "0");
        int numbers = Integer.parseInt(number) - 1;
        SpHp.saveSpLogin(SpKey.NOTICE_POINT_OPERATE, String.valueOf(Math.max(numbers, 0)));

        if (context == null || message == null) {
            return;
        }
        if (XGUntil.IS_DEBUG_XG) {
            //Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show();
            Logger.d(LogTag, "消息通知 onNotifactionClickedResult：" + message.toString());
        }
        // 获取自定义key-value
        String customContent = message.getCustomContent();
        String text = "";
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开！";
            UMengUtil.appStartEvent("推送");
            String jumpType, jumpKey;
            Map map = JsonUtils.fromJson(customContent, HashMap.class);//如果是没有集成厂商的此数据格式为Map，如果是集成了第三方厂商就变成数组格式了
            if (map == null || map.isEmpty()) {
                customContent = customContent.replace("[", "").replace("]", "").replace("},{", ",");
                map = JsonUtils.fromJson(customContent, HashMap.class);
            }
            if (map == null || map.isEmpty()) {
                return;
            }
            jumpType = String.valueOf(map.get("jumpType"));
            jumpKey = String.valueOf(map.get("jumpKey"));

            String contentType = String.valueOf(map.get("contentType"));
            String contentValue = String.valueOf(map.get("contentValue"));
            CommomUtils.clearPushSp();
            Logger.e("存活" + SystemUtils.isAppAlive(context, "com.haiercash.gouhua") + "  topA" + ActivityUntil.getTopActivity().getClass().getSimpleName());
            if (!CheckUtil.isEmpty(jumpType) && !CheckUtil.isEmpty(jumpKey) && "h5".equals(jumpType)) {
                if (jumpKey.contains("gouhua://")) {
                    if (XGUntil.IS_DEBUG_XG) {
                        Logger.d(LogTag, "消息通知 onNotifactionClickedResult：111" + "jumpType==" + jumpType + "--jumpKey==" + jumpKey);
                    }
                    jumpKey = jumpKey.substring(jumpKey.indexOf("gouhua://"));
                    String finalJumpKey = jumpKey;
                    AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                        @Override
                        public void onLoginSuccess() {
                            ActivityUntil.startOtherApp(context, finalJumpKey);

                        }
                    });
                } else if (URLUtil.isNetworkUrl(jumpKey)) {
                   /* Intent i = new Intent(context, ContainerActivity.class);
                    i.putExtra("title", "消息详情");
                    i.putExtra("url", X5WebView.getUrlWithHeadParam(jumpKey));
                    i.putExtra("style", WebSimpleFragment.STYLE_OTHERS);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(ContainerActivity.ID_FRAGMENT, WebSimpleFragment.ID);
                    context.startActivity(i);*/
                    if (CheckUtil.isEmpty(jumpKey)) {
                        return;
                    }
                    Intent i = new Intent(context, JsWebBaseActivity.class);
                    i.putExtra("jumpKey", jumpKey);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } else {
                    String finalJumpKey1 = jumpKey;
                    AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                        @Override
                        public void onLoginSuccess() {
                            ActivityUntil.startOtherApp(context, finalJumpKey1);

                        }
                    });
                }
            } else if (!CheckUtil.isEmpty(contentType)
                    && !CheckUtil.isEmpty(contentValue)
                    && ("H5Link".equals(contentType))
                    && URLUtil.isNetworkUrl(contentValue)) {
                if (message.getPushChannel() == 100 && !isVerifyFirst()) {
                    Intent i = new Intent(context, JsWebBaseActivity.class);
                    i.putExtra("jumpKey", contentValue);
                    i.putExtra("fromPush", true);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } else {
                    SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_TYPE, contentType);
                    SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_VALUE, contentValue);

                }
            } else if (!CheckUtil.isEmpty(contentType)
                    && ("HomePage".equals(contentType))) {
                if (message.getPushChannel() == 100) {
                    //手势密码页则需要先校验
                    if (ActivityUntil.getTopActivity() != null
                            && isVerifyFirst()) {
                        SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_TYPE, contentType);
                        SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_VALUE, contentValue);
                    } else {
                        CommomUtils.goHomePage(true);
                    }
                }
            }
            /**
             * //其他情况调接口：富文本：customContent、
             * // APP申额：EduApply
             * // APP支用：LoanApply、
             * //APP近七日待还：RepayBill
             */
            else if (!CheckUtil.isEmpty(contentType)
                    && !CheckUtil.isEmpty(contentValue)) {
                if (message.getPushChannel() != 100) { //说明是厂商通道，冷启动
                    SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_TYPE, contentType);
                    SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_VALUE, contentValue);
                } else {
                    if (isVerifyFirst()) {
                        SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_TYPE, contentType);
                        SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_VALUE, contentValue);
                    } else {
                        SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_TYPE, contentType);
                        SpHelper.getInstance().saveMsgToSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_VALUE, contentValue);
                        RxBus.getInstance().post(new ActionEvent(ActionEvent.DEAL_WITH_PUSH_INFO));
                    }
                }

            }

        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除！";
        }
        if (XGUntil.IS_DEBUG_XG) {
            Logger.e(LogTag, "消息通知：" + customContent + "\n" + text);
            //UiUtil.toastLongTime("点击消息:" + message.toString());
            //如果app存活，暂时不做处理
            //if (SystemUtils.isAppAlive(context, "com.haiercash.gouhua")) {
            //    return;
            //}
            // APP自主处理的过程。。。
            //Logger.d(LogTag, text + "\n" + customContent);
        }
    }

    //手势或者指纹在顶部
    private boolean isVerifyFirst() {
        return ActivityUntil.getTopActivity() != null && (ActivityUntil.getTopActivity().getClass().getSimpleName().equals("GesturesPasswordActivity")
                || ActivityUntil.getTopActivity().getClass().getSimpleName().equals("VerifyBiometricActivity"));
    }

    @Override
    public void onNotificationShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        if (context == null || xgPushShowedResult == null) {
            return;
        }
        String number = SpHp.getLogin(SpKey.NOTICE_POINT_OPERATE, "0");
        int numbers = Integer.parseInt(number) + 1;
        SpHp.saveSpLogin(SpKey.NOTICE_POINT_OPERATE, String.valueOf(numbers));
        sendReceiver(context);
        //XGNotification notification = new XGNotification();
        //notification.setMsg_id(xgPushShowedResult.getMsgId());
        //notification.setTitle(xgPushShowedResult.getTitle());
        //notification.setContent(xgPushShowedResult.getContent());
        //// notificationActionType==1为Activity，2为url，3为intent
        //notification.setNotificationActionType(xgPushShowedResult.getNotificationActionType());
        //// Activity,url,intent都可以通过getActivity()获得
        //notification.setActivity(xgPushShowedResult.getActivity());
        //notification.setUpdate_time(TimeUtil.calendarToString());
        //NotificationService.getInstance(context).save(notification);
        //context.sendBroadcast(intent);
        //GhNotificationManager.showChannel2Notification(context, xgPushShowedResult);
        if (XGUntil.IS_DEBUG_XG) {
            Logger.d(LogTag, "海尔消费金融：您有1条新消息！" + xgPushShowedResult.toString());
        }
    }

//    public String log(Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//        am.getRunningAppProcesses().get(0).importanceReasonComponent.getClassName();
//        Logger.e(TAG, "pkg:" + cn.getPackageName());
//        Logger.e(TAG, "cls:" + cn.getClassName());
//        Logger.e(TAG, "am.getRunningAppProcesses().get(0).importanceReasonComponent.getClassName()=" + am.getRunningAppProcesses().get(0).importanceReasonComponent.getClassName());
//        Logger.e(TAG, "am.getRunningAppProcesses().get(0).importanceReasonComponent.getPackageName()=" + am.getRunningAppProcesses().get(0).importanceReasonComponent.getPackageName());
//        return null;
//    }

    /**
     * 发送广播通知到首页
     */
    private void sendReceiver(Context context) {
        if (localBroadcastManager == null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(context);
        }
        Intent intent = new Intent(MainHomeHelper.ACTION_RECEIVER);
        //发送本地广播
        localBroadcastManager.sendBroadcast(intent);
    }
}
