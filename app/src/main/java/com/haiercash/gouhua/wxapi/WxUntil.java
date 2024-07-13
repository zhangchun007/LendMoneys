package com.haiercash.gouhua.wxapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/1/4<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class WxUntil {
    //
//    private WxUntil() {
//
//    }
//
//    private static class WxHolder {
//        private final static WxUntil instance = new WxUntil();
//    }
//
//    public static WxUntil getInstance() {
//        return WxHolder.instance;
//    }
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    //1、分享到对话:           SendMessageToWX.Req.WXSceneSession
    //2、分享到朋友圈:         SendMessageToWX.Req.WXSceneTimeline
    //3、分享到收藏:           SendMessageToWX.Req.WXSceneFavorite
    public static int WX_SHARE_TYPE_SESSION = 1;
    public static int WX_SHARE_TYPE_LINE = 2;
    public static int WX_SHARE_TYPE_FAVORITE = 3;

    public static void regToWx(Context context, boolean b) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        IWXAPI api = getWxApi(context, b);
        // 将应用的appId注册到微信
        boolean isRegisterSuccess = api.registerApp(CommonConfig.WX_APP_ID);
        Logger.d("-------------->微信 注册=" + isRegisterSuccess);
    }

    public static boolean isReady(Context context) {
        if (!getWxApi(context).isWXAppInstalled()) {
            Logger.i("请先安装微信应用");
            return false;
        }
        return true;
    }

    /**
     * IWXAPI 是第三方app和微信通信的openApi接口
     */
    private static IWXAPI getWxApi(Context context, boolean b) {
        //return WXAPIFactory.createWXAPI(context, CommonConfig.WX_APP_ID, true);
        return WXAPIFactory.createWXAPI(context, CommonConfig.WX_APP_ID, b);
    }

    /**
     * IWXAPI 是第三方app和微信通信的openApi接口
     */
    private static IWXAPI getWxApi(Context context) {
        return WXAPIFactory.createWXAPI(context, CommonConfig.WX_APP_ID);
    }

    /**
     * 文字类型分享
     */
    public static void shareFriend(Context context, String text, int shareType) {
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = getWxScene(shareType);
        //调用api接口，发送数据到微信
        getWxApi(context).sendReq(req);
    }

    /**
     * 分享图片
     */
    public static void shareImage(Context context, int shareType, int resId) {//, String filePathUrl
        //分享资源文件
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId);
        WXImageObject imgObj = new WXImageObject(bmp);
        //分享本地文件
        //String path = filePathUrl;//SDCARD_ROOT + "/test.png";
        //File file = new File(path);
        //if (!file.exists()) {
        //    //String tip = SendToWXActivity.this.getString(R.string.send_img_file_not_exist);
        //    //Toast.makeText(SendToWXActivity.this, tip + " path = " + path, Toast.LENGTH_LONG).show();
        //    return;
        //}
        //WXImageObject imgObj = new WXImageObject();
        //imgObj.setImagePath(path);
        //Bitmap bmp = BitmapFactory.decodeFile(path);
        //网页图片
        //String url = "http://weixin.qq.com/zh_CN/htmledition/images/weixin/weixin_logo0d1938.png";
        //WXImageObject imgObj = new WXImageObject();
        //imgObj.imageUrl = url;
        //Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openStream());

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        int THUMB_SIZE = 150;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = bmpToByteArray(thumbBmp, true);  // 设置缩略图

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = getWxScene(shareType);
        req.openId = getOpenId();
        getWxApi(context).sendReq(req);
    }

    /**
     * 分享音乐
     */
    public static void shareMusic(Context context, int shareType, int resId, String url, String title, String des) {
        WXMusicObject music = new WXMusicObject();
        //第一种方式
        //music.musicUrl = "http://www.baidu.com";
        music.musicUrl = url;//"http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3";
        //music.musicUrl="http://120.196.211.49/XlFNM14sois/AKVPrOJ9CBnIN556OrWEuGhZvlDF02p5zIXwrZqLUTti4o6MOJ4g7C6FPXmtlh6vPtgbKQ==/31353278.mp3";
        //第二种方式
        //music.musicLowBandUrl = url;//"http://www.qq.com";

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = music;
        msg.title = title;//"Music Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
        msg.description = des;//"Music Album Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";

        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), resId);
        msg.thumbData = bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("music");
        req.message = msg;
        req.scene = getWxScene(shareType);
        req.openId = getOpenId();
        getWxApi(context).sendReq(req);
    }

    /**
     * 分享视频
     */
    public static void shareVideo(Context context, int shareType, int resId, String url, String title, String des) {
        WXVideoObject video = new WXVideoObject();
        //第一种方式
        video.videoUrl = url;//"http://www.baidu.com";
        //第二种方式
        //video.videoLowBandUrl = url;//"http://www.qq.com";

        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = title;//"Video Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
        msg.description = des;// "Video Description Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), resId);
        msg.thumbData = bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("video");
        req.message = msg;
        req.scene = getWxScene(shareType);
        req.openId = getOpenId();
        getWxApi(context).sendReq(req);
    }

    /**
     * 分享网页
     */
    public static void shareWebPage(Context context, int shareType, Bitmap thumb, String url, String title, String des, boolean needRecycle) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;//"WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title WebPage Title Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
        msg.description = des;//"WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description WebPage Description Very Long Very Long Very Long Very Long Very Long Very Long Very Long";
        if (thumb != null) {
            msg.thumbData = bmpToByteArray(thumb, needRecycle);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = getWxScene(shareType);
        req.openId = getOpenId();
        boolean shareFlg = getWxApi(context).sendReq(req);
        if (!shareFlg) {
            Logger.d("分享失败，请确认您设备的微信客户端正常");
            UiUtil.toast("分享失败");
        }
    }

    /**
     * 微信支付<p>
     * 需要先调用  WxUntil.regToWx(this, true);
     */
    public static boolean WxPayment(Context context, Map<String, Object> map) {
        String wx_pay_empty_message = "微信支付入参为空";
        if (map == null || map.isEmpty()) {
            //UiUtil.toast(wx_pay_empty_message);
            Logger.d(wx_pay_empty_message);
            return false;
        }
        if (!map.containsKey("partnerid") && CheckUtil.isEmpty(map.get("partnerid"))) {
            Logger.d(wx_pay_empty_message);
            return false;
        }
        if (!map.containsKey("prepayid") && CheckUtil.isEmpty(map.get("prepayid"))) {
            Logger.d(wx_pay_empty_message);
            return false;
        }
        if (!map.containsKey("noncestr") && CheckUtil.isEmpty(map.get("noncestr"))) {
            Logger.d(wx_pay_empty_message);
            return false;
        }
        if (!map.containsKey("timestamp") && CheckUtil.isEmpty(map.get("timestamp"))) {
            Logger.d(wx_pay_empty_message);
            return false;
        }
        if (!map.containsKey("package") && CheckUtil.isEmpty(map.get("package"))) {
            Logger.d(wx_pay_empty_message);
            return false;
        }
        if (!map.containsKey("sign") && CheckUtil.isEmpty(map.get("sign"))) {
            Logger.d(wx_pay_empty_message);
            return false;
        }
        PayReq req = new PayReq();
        req.appId = CommonConfig.WX_APP_ID;
        req.partnerId = String.valueOf(map.get("partnerid"));
        req.prepayId = String.valueOf(map.get("prepayid"));
        req.nonceStr = String.valueOf(map.get("noncestr"));
        req.timeStamp = String.valueOf(map.get("timestamp"));
        req.packageValue = String.valueOf(map.get("package"));
        req.sign = String.valueOf(map.get("sign"));
        //req.extData = amount + "#" + repaySeq; // optional
        Logger.d("正常调起支付---->微信支付入参");
        Logger.j(JsonUtils.toJson(req));
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        boolean payBoolean = getWxApi(context).sendReq(req);
        if (!payBoolean) {
            Logger.d("支付失败，请确认您设备的微信客户端正常");
            //UiUtil.toast("支付失败");
        }
        return payBoolean;
    }

    /**
     * 设置分享到哪里
     */
    private static int getWxScene(int shareType) {
        if (shareType == WX_SHARE_TYPE_SESSION) {
            return SendMessageToWX.Req.WXSceneSession;
        } else if (shareType == WX_SHARE_TYPE_LINE) {
            return SendMessageToWX.Req.WXSceneTimeline;
        } else if (shareType == WX_SHARE_TYPE_FAVORITE) {
            return SendMessageToWX.Req.WXSceneFavorite;
        }
        return SendMessageToWX.Req.WXSceneSession;
    }

    /**
     * transaction字段用于唯一标识一个请求
     */
    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private static String getOpenId() {
        //return "";//oXXSs5oU1D54a-7ooUJvf-9F1XIE
        return SpHp.getLogin(SpKey.WX_OPEN_ID);
    }

    /**
     * 检查是否支持发送到朋友圈
     */
    public static boolean checkSupported(Context context) {
        int wxSdkVersion = getWxApi(context).getWXAppSupportAPI();
        if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
            Logger.d("wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline supported");
            return true;
        } else {
            Logger.d("wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline not supported");
            return false;
        }
    }


    private static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        byte[] result = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            //bmp.compress(Bitmap.CompressFormat.JPEG, 95, output);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, output);
            if (needRecycle) {
                bmp.recycle();
            }

            result = output.toByteArray();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

//        int i;
//        int j;
//        if (bmp.getHeight() > bmp.getWidth()) {
//            i = bmp.getWidth();
//            j = bmp.getWidth();
//        } else {
//            i = bmp.getHeight();
//            j = bmp.getHeight();
//        }
//
//        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
//        Canvas localCanvas = new Canvas(localBitmap);
//
//        while (true) {
//            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
//            if (needRecycle)
//                bmp.recycle();
//            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
//            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
//            localBitmap.recycle();
//            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
//            try {
//                localByteArrayOutputStream.close();
//                return arrayOfByte;
//            } catch (Exception e) {
//                //F.out(e);
//            }
//            i = bmp.getHeight();
//            j = bmp.getHeight();
//        }
    }

    public static Bundle mBundle;

    /**
     * get token
     */
    public static void sendAuthForLogin(Context context, Bundle bundle) {
        // send oauth request
        SendAuth.Req req;
        if (bundle == null) {
            req = new SendAuth.Req();
        } else {
            req = new SendAuth.Req(bundle);
        }
        mBundle = bundle;
        req.scope = "snsapi_userinfo";
        req.state = "gouHuaLogin" + UUID.randomUUID().toString().replaceAll("-", "");
        req.openId = getOpenId();
        boolean authFlg = getWxApi(context).sendReq(req);
        if (!authFlg) {
            UiUtil.toast("微信登录失败，请重试");
        }
    }
}
