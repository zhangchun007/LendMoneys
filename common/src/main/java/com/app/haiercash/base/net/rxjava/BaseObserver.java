package com.app.haiercash.base.net.rxjava;

import android.text.TextUtils;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.token.TokenException;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.InfoEncryptUtil;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;

import io.reactivex.observers.DisposableObserver;

/**
 * Author: Sun<br/>
 * Date :    2018/2/7<br/>
 * FileName: BaseObserver<br/>
 * Description: 返回结果预处理<br/>
 */
public class BaseObserver extends DisposableObserver<BasicResponse> {

    private Class mClass;
    private INetResult mResult;
    private String mUrl;
    private boolean isIEncrypt;//为true则需要先解密

    private String randomKey;//统一加密的key
    private String randomIv;//统一加密的iv


    public BaseObserver(INetResult netResult) {
        this(null, netResult);
    }

    public void setUrl(String url, Class className) {
        mUrl = url;
        mClass = className;
    }

    public BaseObserver(Class className, INetResult netResult) {
        this(null, className, netResult);
    }

    public BaseObserver(String url, Class className, INetResult netResult) {
        this(url, className, netResult, false, null, null);
    }

    public BaseObserver(String url, Class className, INetResult netResult, boolean isIEncrypt, String randomKey, String randomIv) {
        mUrl = url;
        mClass = className;
        mResult = netResult;
        this.isIEncrypt = isIEncrypt;
        this.randomKey = randomKey;
        this.randomIv = randomIv;
    }

    @Override
    public void onNext(BasicResponse response) {
        if (!TextUtils.isEmpty(response.getResponseData())) {
            String responseJson = InfoEncryptUtil.aesDecrypt(response.getResponseData(), randomKey, randomIv);
            response = JsonUtils.fromJson(responseJson, BasicResponse.class);
        }
        if (NetConfig.IS_DEBUG_NET && response != null && response.getBody() != null) {
            String json = String.valueOf(JsonUtils.toJson(response.getBody()));
            Logger.i(mUrl + "\n返回 \n" + (json.length() >= 100000 ? json.substring(0, 100000) : json));
        }

        if (response == null) {
            BasicResponse basicResponse = new BasicResponse(NetConfig.NET_CODE_PARSER_ERROR, mClass.getSimpleName() + NetConfig.DATA_PARSER_ERROR);
            mResult.onError(basicResponse, mUrl);
            return;
        }
        if (!response.isSuccess()) {
            //因返回结构不固定，存在非"00000" 有body的现象。
            mResult.onError(response, mUrl);
        } else if (mClass != null) {
            Object result;
            try {
                result = JsonUtils.fromJson(response.getBody(), mClass);
                if (result != null) {
                    mResult.onSuccess(result, mUrl);
                } else {
                    BasicResponse basicResponse = new BasicResponse(NetConfig.NET_CODE_PARSER_ERROR, mClass.getSimpleName() + NetConfig.DATA_PARSER_ERROR);
                    mResult.onError(basicResponse, mUrl);
                }
            } catch (Exception exception) {
                BasicResponse basicResponse = new BasicResponse(NetConfig.NET_CODE_PARSER_ERROR, mClass.getSimpleName() + NetConfig.DATA_PARSER_ERROR);
                mResult.onError(basicResponse, mUrl);
            }
        } else {
            try {
                mResult.onSuccess(response.body, mUrl);
            } catch (Exception exception) {
                BasicResponse basicResponse = new BasicResponse(NetConfig.NET_CODE_PARSER_ERROR, NetConfig.DATA_PARSER_ERROR);
                mResult.onError(basicResponse, mUrl);
            }

        }
        TokenHelper.initTokenRefresh();
    }

    @Override
    public void onError(Throwable t) {
        if (NetConfig.IS_DEBUG_NET) {
            Logger.e(mUrl + " 返回 " + JsonUtils.toJson(t));
        }
        BasicResponse response;
        //处理常见的几种连接错误
        if (!(t instanceof TokenException)) {
            response = new BasicResponse(NetConfig.NET_CODE_SOCKET_TIMEOUT, NetConfig.SOCKET_TIMEOUT_EXCEPTION);
        } else {
            response = new BasicResponse(NetConfig.NET_CODE_TOKEN_INVALID, NetConfig.TOKEN_INVALID);
        }
        mResult.onError(response, mUrl);
    }

    @Override
    public void onComplete() {

    }
}
