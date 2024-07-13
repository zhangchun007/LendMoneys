package com.haiercash.gouhua.hybrid;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.homepage.HomeH5Data;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;
import java.util.Map;

public class H5ConfigHelper implements INetResult {
    private final IConfigCallback callback;
    private final NetHelper netHelper;
    private final String status;
    private final String applSeq;
    public static String h5CreditUrl;  //混合开发申额跳转的
    public static String h5LoanUrl;  //混合开发支用跳转的
    public static String h5RepayJumpUrl;  //混合开发还款入口
    public static String h5RepayJumpUrlY;  //逾期待还款url
    public static String h5SceneLoanUrl;  //场景下支用跳转链接
    public static String h5SceneCreditUrl;  //场景下申额跳转链接

    public interface IConfigCallback {
        void configSuccess();

        void configFail();
    }

    public H5ConfigHelper(String status, String applSeq, IConfigCallback callback) {
        this.status = status;
        this.applSeq = applSeq;
        this.callback = callback;
        netHelper = new NetHelper(this);

    }

    //获取到H5需要跳转的链接
    public void getH5LinkData() {
        Map<String, String> map = new HashMap<>();
        map.put("h5token", TokenHelper.getInstance().getH5Token());
        map.put("status", status);
        map.put("applSeq", applSeq);
        map.put("custNo", SpHp.getUser(SpKey.USER_CUSTNO));
        map.put("deviceId", SystemUtils.getDeviceID(AppApplication.CONTEXT));
        map.put("processId", TokenHelper.getInstance().getH5ProcessId());
        if (netHelper != null) {
            netHelper.postService(ApiUrl.URL_HOME_H5_DATA, map, HomeH5Data.class);
        }
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        if (ApiUrl.URL_HOME_H5_DATA.equals(url)) {
            HomeH5Data data = (HomeH5Data) t;
            dealData(data);
        }
        if (callback != null) {
            callback.configSuccess();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.URL_HOME_H5_DATA.equals(url)) {
            if (callback != null) {
                callback.configFail();
            }
        }
    }

    private void dealData(HomeH5Data data) {
        if (CheckUtil.isEmpty(data)) {
            return;
        }
        TokenHelper.getInstance().saveH5ProcessId(data.getProcessId());
        TokenHelper.getInstance().saveH5Token(data.getH5token());
        String jumpUrl = data.getNodeJumpUrl();
        String loanUrl = data.getLoanJumpUrl();
        String repayJumpUrl = data.getRepayJumpUrl();
        String repayJumpUrlY = data.getRepayJumpUrlY();


        if (!CheckUtil.isEmpty(jumpUrl)) {
            if (jumpUrl.contains("?")) {
                jumpUrl += "&h5token=";
            } else {
                jumpUrl += "?h5token=";
            }
            jumpUrl += TokenHelper.getInstance().getH5Token();
            h5CreditUrl = jumpUrl;
        }
        if (!CheckUtil.isEmpty(loanUrl)) {
            if (loanUrl.contains("?")) {
                loanUrl += "&h5token=";
            } else {
                loanUrl += "?h5token=";
            }
            loanUrl += TokenHelper.getInstance().getH5Token();
            h5LoanUrl = loanUrl;
        }
        if (!CheckUtil.isEmpty(repayJumpUrl)) {
            if (repayJumpUrl.contains("?")) {
                repayJumpUrl += "&h5token=";
            } else {
                repayJumpUrl += "?h5token=";
            }
            repayJumpUrl += TokenHelper.getInstance().getH5Token();
            h5RepayJumpUrl = repayJumpUrl;
        }
        if (!CheckUtil.isEmpty(repayJumpUrlY)) {
            if (repayJumpUrlY.contains("?")) {
                repayJumpUrlY += "&h5token=";
            } else {
                repayJumpUrlY += "?h5token=";
            }
            repayJumpUrlY += TokenHelper.getInstance().getH5Token();
            h5RepayJumpUrlY = repayJumpUrlY;
        }
        //保存H5信息
        if (data.getH5CustLoginInfo() != null) {
            TokenHelper.getInstance().saveH5LoginInfo(JsonUtils.toJson(data.getH5CustLoginInfo()));
        }

    }

    public static void setH5SceneInfo(String loanUrl,String creditUrl){
        h5SceneLoanUrl = loanUrl;
        h5SceneCreditUrl = creditUrl;
    }
}
