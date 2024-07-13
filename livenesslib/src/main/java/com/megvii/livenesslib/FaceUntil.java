package com.megvii.livenesslib;


import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.app.haiercash.base.bui.BaseGHActivity;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.megvii.meglive_sdk.manager.MegLiveManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 旷视人脸集成工具类
 */
public class FaceUntil implements IPreCallback, IDetectCallback {
    private MegLiveManager megLiveManager;
    private BaseGHActivity activity;
    private IFaceCallBack callBack;

    public FaceUntil(BaseGHActivity activity, IFaceCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
    }

    private String bizToken;

    public void startLiveDetect(String bizToken) {
        this.bizToken = bizToken;
        // 模型本地存放路径
        String modelPath = saveAssets("faceidmodel.bin", "model");
        megLiveManager = MegLiveManager.getInstance();
        megLiveManager.preDetect(activity, bizToken, "en", "https://api-ipv6.megvii.com", modelPath, this);
    }

    private String saveAssets(String fileName, String path) {
        File dir = new File(activity.getExternalFilesDir("megvii"), path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return null;
            }
        }
        File file = new File(dir, fileName);

        FileOutputStream fos = null;
        InputStream is = null;
        String ret = null;
        try {
            int count;
            byte[] buffer = new byte[1024];
            fos = new FileOutputStream(file);
            is = activity.getAssets().open(fileName);
            while ((count = is.read(buffer)) != -1) {
                fos.write(buffer, 0, count);
            }

            ret = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fos != null) fos.close();
                if (is != null) is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public void onDetectFinish(String token, int errorCode, String errorMessage, String data) {
        if (errorCode == 1000) {
          /*  Map<String,Object>map = new HashMap<>();
            map.put("sign","Nmm2hX4RSgpFOybovmxy6R5EV1dhPVU3c1NDZ2UzRkRURUVwcUJpZUFxRnlrdmpFaUowdE15JmI9MTYzOTA0Nzg2MiZjPTE2MzkwNDc3NjImZD0xMzc2NDQ0NDU2");
            map.put("sign_version","hmac_sha1");

            map.put("biz_token",bizToken);

            map.put("meglive_data",data.getBytes());




            RetrofitFactory.getInstance().postOthers("https://api.megvii.com/faceid/v3/sdk/verify", map, new INetResult() {
                @Override
                public <T> void onSuccess(T t, String url) {
                    Toast.makeText(activity, "preDetect:" + t, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onError(BasicResponse error, String url) {
                    Toast.makeText(activity, "preDetect:" + error, Toast.LENGTH_LONG).show();

                }
            });



*/


            // FileUtils.getFile(data.getBytes(),FileUtils.getExternalFilesDir(),"imgByte");
            //verify(token, data.getBytes());
            callBack.faceBack(true, token, data);
        } else {
            Logger.e("FaceUntil", "人脸识别 startDetect 失败：" + errorCode + "--" + errorMessage);
            //Toast.makeText(activity, "startDetect:" + errorMessage, Toast.LENGTH_LONG).show();
            callBack.faceBack(false, errorMessage, null);
        }
    }


    @Override
    public void onPreStart() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.showProgress(true);
            }
        });
    }

    @Override
    public void onPreFinish(String token, int errorCode, String errorMessage) {
        activity.showProgress(false);
        if (errorCode == 1000) {
            megLiveManager.setVerticalDetectionType(MegLiveManager.DETECT_VERITICAL_FRONT);
            megLiveManager.startDetect(this);
            //延迟一秒后执行屏蔽截屏录屏
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    SystemUtils.setWindowSecure(ActivityUntil.getTopActivity());
                }
            }, 1000);
        } else {
            Logger.e("FaceUntil", "人脸识别 preDetect 失败：" + errorMessage);
            Toast.makeText(activity, "preDetect:" + errorMessage, Toast.LENGTH_LONG).show();
            callBack.faceBack(false, errorMessage, null);
        }
    }
}
