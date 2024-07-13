package com.haiercash.gouhua.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.network.NetHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sun on 2017/9/4.
 * 上送本地日志
 */
public class GhLogService extends IntentService {

    private NetHelper netHelper;

    public GhLogService() {
        super("GhLogService");
    }

    public GhLogService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        netHelper = new NetHelper();
    }

    //  启动上传
    public static void startUploadLog(Context context, String netMessage) {
        Intent intent = new Intent(context, GhLogService.class);
        intent.putExtra("netMessage", netMessage);
        context.startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            //上送的报文
            String bodyStr = intent.getStringExtra("netMessage");
            // 报文不为空的情况下进行上送
            if (!TextUtils.isEmpty(bodyStr)) {
                sendLogMessage(bodyStr);
            }
        }
    }

    /**
     * 上送log 日志到服务器
     */
    private void sendLogMessage(String netMessage) {
        Map<String, String> map = new HashMap<>();
        map.put("appLog", RSAUtils.encryptByRSA(netMessage));
        netHelper.postService(ApiUrl.url_uploadLog, map);
    }
}
