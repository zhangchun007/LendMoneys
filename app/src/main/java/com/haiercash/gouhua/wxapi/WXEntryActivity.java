package com.haiercash.gouhua.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.activity.login.LoginNetHelper;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/1/4<br/>
 * 描    述：微信发送的请求将回调到onReq方法，发送到微信请求的响应结果将回调到onResp方法<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private final String TAG = "WXEntryActivity";

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private LoginNetHelper loginNetHelper;

    @Override
    protected int getLayout() {
        return -1;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, CommonConfig.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    //微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        UiUtil.toast("openid = " + req.openId);
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            case ConstantsAPI.COMMAND_LAUNCH_BY_WX:
                UiUtil.toast("Launch From Weixin");
                break;
            default:
                break;
        }
        finish();
    }

    boolean isFromRedBag = false;

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        Logger.d(TAG, "openid = " + resp.openId);
        Bundle bundle = WxUntil.mBundle;
        isFromRedBag = !TextUtils.isEmpty(bundle.getString("fromRedBag")) && "Y".equals(bundle.getString("fromRedBag"));
        //这里需要赋值为空，防止微信登录还是有这个标识
        WxUntil.mBundle.putString("fromRedBag", "");
        switch (resp.getType()) {
            case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                wxShareResult(resp);
                break;
            case ConstantsAPI.COMMAND_PAY_BY_WX:
                //wxPayResult(resp);
                Logger.e("微信支付:默认已由WXPayEntryActivity处理了");
                break;
            case ConstantsAPI.COMMAND_SENDAUTH://发送OpenAPI Auth验证 -- 授权登陆
                SendAuth.Resp resp1 = ((SendAuth.Resp) resp);
                Logger.d(TAG, "code = " + resp1.code);
                postAuthLogin(resp1);
                break;
            default:
                break;
        }
    }

    /**
     * 分享回调
     */
    private void wxShareResult(BaseResp resp) {
        if (AppApplication.isLogIn() && !CheckUtil.isEmpty(resp.openId)) {
            SpHp.saveSpLogin(SpKey.WX_OPEN_ID, resp.openId);
        }
        String result;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                RxBus.getInstance().post(new ActionEvent(ActionEvent.WxShareSuccess));
                result = "分享成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "分享取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "分享被拒绝";
                break;
            default:
                result = "分享返回";
                break;
        }
        if (!CheckUtil.isEmpty(result)) {
            UiUtil.toast(result);
        }
        //RxBus.getInstance().post(new ActionEvent(ActionEvent.WxShareSuccess));
        Logger.d(TAG, result);
        finish();
    }

    private void postAuthLogin(SendAuth.Resp resp) {
        //UiUtil.toastLongTime(JsonUtils.toJson(resp));
        String respMsg;
        if (resp != null) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    if (isFromRedBag) {
                        String wxCode = resp.code;
                        RxBus.getInstance().post(new ActionEvent(ActionEvent.RED_BAG_WX_AUTH, wxCode));
                        finish();
                    } else {
                        if (loginNetHelper == null) {
                            loginNetHelper = new LoginNetHelper(this, LoginNetHelper.WX_LOGIN, "BR015", null);
                        }
                        loginNetHelper.authSuccessIn(resp.code,"");
                    }
                    return;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    respMsg = "授权取消";
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    respMsg = "授权拒绝";
                    break;
                default:
                    respMsg = "授权返回";
                    break;
            }
        } else {
            respMsg = "授权返回";
        }
        if (isFromRedBag) {
            RxBus.getInstance().post(new ActionEvent(ActionEvent.RED_BAG_WX_AUTH, ""));
        }
        RiskInfoUtils.updateRiskInfoByNode("BR015", "NO");
        if (!TextUtils.isEmpty(respMsg)) {
            if (!isFromRedBag) {
                UiUtil.toast(respMsg);
            }
//            try {
//                Activity activity = ActivityUntil.findActivity(PageKeyParameter.ACTIVITY_VERIFY_WX);
//                if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
//                    activity.finish();
//                }
//            } catch (Exception e) {
//                Logger.e(e.getMessage());
//            }
            finish();
        }
    }

    private void goToGetMsg() {
        //Intent intent = new Intent(this, GetFromWXActivity.class);
        //intent.putExtras(getIntent());
        //startActivity(intent);
        //finish();
    }

    private void goToShowMsg(ShowMessageFromWX.Req showReq) {
        //System.out.println(showReq.openId);
        //WXMediaMessage wxMsg = showReq.message;
        //WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;

        //StringBuffer msg = new StringBuffer(); // 组织一个待显示的消息内容
        //msg.append("description: ");
        //msg.append(wxMsg.description);
        //msg.append("\n");
        //msg.append("extInfo: ");
        //msg.append(obj.extInfo);
        //msg.append("\n");
        //msg.append("filePath: ");
        //msg.append(obj.filePath);

        //Intent intent = new Intent(this, ShowFromWXActivity.class);
        //intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
        //intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
        //intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
        //startActivity(intent);
        //finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginNetHelper.REQUEST_CODE) {
            if (resultCode == 1) {
                //判断是否设置过手势密码
                AppApplication.userid = SpHp.getLogin(SpKey.LOGIN_USERID);
                loginNetHelper.checkGesturePwd();
            } else {//设备验证失败或者取消 默认用户没登录成功  清除所有缓存数据
                CommomUtils.clearSp();
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (loginNetHelper != null) {
            loginNetHelper.onDestroy();
        }
        super.onDestroy();
        AppApplication.doLoginCallback();
    }
}
