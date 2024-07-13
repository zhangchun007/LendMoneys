package com.app.haiercash.base.net.token;

import android.content.Context;
import android.text.TextUtils;

import com.app.haiercash.base.bean.TokenBean;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.CommonSpKey;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.retrofit.RetrofitFactory;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * @Author: Sun
 * @Date :    2018/3/19
 * @FileName: TokenHelper
 * @Description: 1，将token的保存和读取对上层隔离
 * 2，token失效时进行刷新token
 */

public class TokenHelper {

    private Context mApplication;
    private PublishSubject<BasicResponse> mPublishSubject;
    public static int TokenRefresh = 0;

    //允许token刷新的次数
    private static int maxTokenRefreshCount = 2;


    public void registerToken(Context context) {
        this.mApplication = context.getApplicationContext();
    }

    public static TokenHelper getInstance() {
        return Holder.INSTANCE;
    }

    private AtomicBoolean mRefreshing = new AtomicBoolean(false);

    private static class Holder {
        private static final TokenHelper INSTANCE = new TokenHelper();
    }

    public void saveToken(String token) {
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_TOKEN_SP_VALUE, token);
    }

    public String getClientSecret() {
        return SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_TOKEN_CLIENT_SECRET);
    }

    public void saveClientSecret(String clientSecret) {
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_TOKEN_CLIENT_SECRET, clientSecret);
    }

    public String getRefreshToken() {
        return SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_TOKEN_REFRESH_VALUE);
    }

    public void saveRefreshToken(String refresh) {
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_TOKEN_REFRESH_VALUE, refresh);
    }

    //获取h5的token
    public String getH5Token() {
        return SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_H5_TOKEN_VALUE);
    }

    //保存H5的token
    public void saveH5Token(String h5Token) {
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_H5_TOKEN_VALUE, h5Token);
    }

    //获取h5的processId
    public String getH5ProcessId() {
        return SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_H5_PROCESS_ID_VALUE);
    }

    //保存H5的processId
    public void saveH5ProcessId(String h5Token) {
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_H5_PROCESS_ID_VALUE, h5Token);
    }

    //获取H5登录信息
    public String getH5LoginInfo() {
        return SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_H5_LOGIN_INFO);
    }

    //保存H5登录信息
    public void saveH5LoginInfo(String h5LoginInfo) {
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_H5_LOGIN_INFO, h5LoginInfo);
    }


    public String getCacheToken() {
        return SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_TOKEN_SP_VALUE);
    }

    public String getMobile() {
        return SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_MOBILE_VALUE);
    }

    public void seveMobile(String value) {
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_MOBILE_VALUE, value);
    }

    public void saveSmyParameter(String ideaCode, String registerVector, String business, String channelNo, String regisChannel, String appDownFrom) {
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_IDEA_CODE, ideaCode);
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_REGISTER_VECTOR, getRegisterVector(registerVector));
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_BUSINESS, getBusiness(business));
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_CHANNEL_NO, channelNo);
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_REGIS_CHANNEL, regisChannel);
        SpHelper.getInstance().saveMsgToSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_APP_DOW_FORM, appDownFrom);
    }

    //如果检测到用户业务线为空（老用户等），则转化为NetConfig.TD_BUSINESS_EMPTY
    private String getBusiness(String business) {
        return CheckUtil.isEmpty(business) ? NetConfig.TD_BUSINESS_EMPTY : business;
    }

    //如果检测到用户业注册载体为空（老用户等），则转化为NetConfig.TD_REGIS_VECTOR_EMPTY
    private String getRegisterVector(String registerVector) {
        return CheckUtil.isEmpty(registerVector) ? NetConfig.TD_REGIS_VECTOR_SMY : registerVector;
    }

    public String getSmyParameter(String key) {
        if ("ideaCode".equals(key)) {
            return SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_IDEA_CODE);
        } else if ("registerVector".equals(key)) {
            //注册载体取app包的
            return NetConfig.TD_REGIS_VECTOR_SMY;
        } else if ("business".equals(key)) {
            return getBusiness(SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_BUSINESS));
        } else if ("regisChannel".equals(key)) {
            return SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_REGIS_CHANNEL);
        } else if ("appDownFrom".equals(key)) {
            return SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_APP_DOW_FORM);
        } else if ("channelNo".equals(key)) {
            return SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_CHANNEL_NO);
        } else {
            return "";
        }
    }

    public static void initTokenRefresh() {
        TokenRefresh = 0;
    }

    public static boolean canRefreshToken() {
        return TokenRefresh <= maxTokenRefreshCount;
    }


    /**
     * Refresh the token when the current token is invalid.
     *
     * @return Observable
     */
    public Observable<?> refreshTokenWhenTokenInvalid() {
        if (mRefreshing.compareAndSet(false, true)) {
            Logger.e("刷新token");
            startTokenRequest();
        } else {
            Logger.e("已经存在刷新token的任务");
        }
        return mPublishSubject;
    }

    /**
     * 请求token
     */
    public void startTokenRequest() {
        mPublishSubject = PublishSubject.create();
        TokenRefresh++;
        Map<String, String> map = new HashMap<>();
        map.put("refreshToken", getRefreshToken());
        map.put("clientId", EncryptUtil.simpleEncrypt("AND-" + SystemUtils.getDeviceID(mApplication) + "-" + getMobile()));
        RetrofitFactory.getInstance().addService(NetConfig.API_REQUEST_TOKEN, false);
        RetrofitFactory.getInstance().post(NetConfig.API_REQUEST_TOKEN, null, map)
                .subscribe(new Observer<BasicResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BasicResponse basicResponse) {
                        mRefreshing.set(false);
                        if (basicResponse.isSuccess()) {
                            TokenBean tokenBean = JsonUtils.fromJson(basicResponse.getBody(), TokenBean.class);
                            Logger.e("token刷新成功" + tokenBean.getAccess_token());
                            if (tokenBean != null && !CheckUtil.isEmpty(tokenBean.getAccess_token())) {
                                TokenHelper.getInstance().saveToken(tokenBean.getAccess_token());
                            }
                            if (tokenBean != null && !CheckUtil.isEmpty(tokenBean.getRefresh_token())) {
                                TokenHelper.getInstance().saveRefreshToken(tokenBean.getRefresh_token());
                            }
                            mPublishSubject.onNext(basicResponse);
                        } else {
                            onError(new TokenException());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRefreshing.set(false);
                        Logger.e("token刷新失败");
                        mPublishSubject.onError(new TokenException());
                        initTokenRefresh();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void refreshTokenFromSplash(){
        if(TextUtils.isEmpty(getRefreshToken())){
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("refreshToken", getRefreshToken());
        map.put("clientId", EncryptUtil.simpleEncrypt("AND-" + SystemUtils.getDeviceID(mApplication) + "-" + getMobile()));
        RetrofitFactory.getInstance().addService(NetConfig.API_REQUEST_TOKEN, false);
        RetrofitFactory.getInstance().post(NetConfig.API_REQUEST_TOKEN, null, map)
                .subscribe(new Observer<BasicResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BasicResponse basicResponse) {
                        if (basicResponse.isSuccess()) {
                            TokenBean tokenBean = JsonUtils.fromJson(basicResponse.getBody(), TokenBean.class);
                            Logger.e("token刷新成功" + tokenBean.getAccess_token());
                            if (tokenBean != null && !CheckUtil.isEmpty(tokenBean.getAccess_token())) {
                                TokenHelper.getInstance().saveToken(tokenBean.getAccess_token());
                            }
                            if (tokenBean != null && !CheckUtil.isEmpty(tokenBean.getRefresh_token())) {
                                TokenHelper.getInstance().saveRefreshToken(tokenBean.getRefresh_token());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
