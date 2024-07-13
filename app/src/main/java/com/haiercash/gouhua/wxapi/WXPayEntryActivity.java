package com.haiercash.gouhua.wxapi;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.service.GhLogService;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/1/7<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    protected int getLayout() {
        return -1;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, CommonConfig.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    //0     成功            展示成功页面
    //-1    错误           可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
    //-2    用户取消       无需处理。发生场景：用户不支付了，点击取消，返回APP。
    @Override
    public void onResp(BaseResp resp) {
        Logger.d(TAG, "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            Logger.d("微信支付结果：" + resp.errCode);
            if (resp.errCode == 0) {
                RxBus.getInstance().post(new ActionEvent(ActionEvent.WxPayResult, "SUCCESS"));
            } else if (resp.errCode == -1) {
                RxBus.getInstance().post(new ActionEvent(ActionEvent.WxPayResult, "FAIL"));
                GhLogService.startUploadLog(this, JsonUtils.toJson(resp));
            } else if (resp.errCode == -2) {
                RxBus.getInstance().post(new ActionEvent(ActionEvent.WxPayResult, "CANCEL"));
                GhLogService.startUploadLog(this, JsonUtils.toJson(resp));
            } else {
                RxBus.getInstance().post(new ActionEvent(ActionEvent.WxPayResult, "UNKNOWN"));
                GhLogService.startUploadLog(this, JsonUtils.toJson(resp));
            }
        }
        Logger.d("微信支付：" + WXPayEntryActivity.TAG + "\n" + JsonUtils.toJson(resp));
        finish();

    }
}
