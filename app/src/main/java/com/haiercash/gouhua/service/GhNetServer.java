package com.haiercash.gouhua.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.app.haiercash.base.bean.AreaListBean;
import com.app.haiercash.base.db.DbUtils;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/5/15<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class GhNetServer extends IntentService implements INetResult {
    public static final String CHANNEL_SET = "channelSet";
    public static final String AREA_CODE = "getAreaCode";
    private NetHelper netHelper;
    private String userStatue;

    public GhNetServer() {
        super("GhNetServer");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        netHelper = new NetHelper(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String doType = intent.getStringExtra("doType");
        Map<String, String> map = new HashMap<>();
        if (AREA_CODE.equals(doType)) {
            map.put("version", SpHp.getOther(SpKey.OTHER_DB_VERSION, "20210101"));
            netHelper.getService(ApiUrl.GET_AREA_CODE, map);
        } else if (CHANNEL_SET.equals(doType)) {
            userStatue = intent.getStringExtra("userStatue");
            map.put("channelFlag", SpHp.getLogin(SpKey.LOGIN_REGISTCHANNEL));
            netHelper.getService(ApiUrl.URL_CHANNEL_SET, map);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void onSuccess(T success, String url) {
        if (url.equals(ApiUrl.URL_CHANNEL_SET)) {
            SpHp.saveSpLogin(SpKey.LOAN_MARKET_EDU_STATUS, "N");
            Map<String, String> map = (Map<String, String>) success;
            //enableLoanMarket 是否开启贷超
            if (map != null && map.containsKey("enableLoanMarket") && "Y".equals(map.get("enableLoanMarket"))) {
                SpHp.saveSpLogin(SpKey.ENABLE_LOAN_MARKET, "Y");
                if (map.containsKey("eduStatus")) {
                    String eduStatus = map.get("eduStatus");
                    SpHp.saveSpLogin(SpKey.EDU_STATUS, eduStatus);
                    //不限状态
                    if (!CheckUtil.isEmpty(eduStatus) && eduStatus.contains("eduStatusAll")) {
                        SpHp.saveSpLogin(SpKey.LOAN_MARKET_EDU_STATUS, "Y");
                    } else if (!CheckUtil.isEmpty(eduStatus) && !CheckUtil.isEmpty(userStatue) && eduStatus.contains(userStatue)) {
                        SpHp.saveSpLogin(SpKey.LOAN_MARKET_EDU_STATUS, "Y");
                    }
                }
            } else {
                SpHp.saveSpLogin(SpKey.ENABLE_LOAN_MARKET, "N");
            }
            //enableMoreProduct是否开启更多产品
            if (map != null && map.containsKey("enableMoreProduct")) {
                SpHp.saveSpLogin(SpKey.ENABLE_MORE_PRODUCT, map.get("enableMoreProduct"));
            } else {
                SpHp.saveSpLogin(SpKey.ENABLE_MORE_PRODUCT, "N");
            }
        } else if (ApiUrl.GET_AREA_CODE.equals(url)) {
            AreaListBean bean = JsonUtils.fromJson(success, AreaListBean.class);
            if (bean != null) {
                if (!SpHp.getOther(SpKey.OTHER_DB_VERSION, "20210101").equals(bean.getMaxVersion()) && bean.getAreaCodeList() != null) {
                    DbUtils.getAddress().resetDbData(bean.getAreaCodeList());
                    SpHp.saveSpOther(SpKey.OTHER_DB_VERSION, bean.getMaxVersion());
                }
            }
        }
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        stopSelf();
    }

    @Override
    public void onError(BasicResponse error, String url) {
        Logger.e("onError-BasicResponse", url);
        if (ApiUrl.URL_CHANNEL_SET.equals(url)) {
            SpHp.saveSpLogin(SpKey.LOAN_MARKET_EDU_STATUS, "N");
        }
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        stopSelf();
    }

    /**
     * 启动调用接口：ApiUrl.URL_CHANNEL_SET<br/>
     * 调用接口：ApiUrl.GET_AREA_CODE
     */
    public static void startGhNetServer(Context context, String doType, String userStatue) {
        Intent intent = new Intent(context, GhNetServer.class);
        intent.putExtra("doType", doType);
        if (AREA_CODE.equals(doType)) {
            if (DbUtils.isCityDbValid()) {
                context.startService(intent);
            }
        } else if (CHANNEL_SET.equals(doType)) {
            if (AppApplication.isLogIn()) {
                intent.putExtra("userStatue", userStatue);
                context.startService(intent);
            }
        }
    }
}
