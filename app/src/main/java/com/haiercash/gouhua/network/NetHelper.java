package com.haiercash.gouhua.network;

import android.os.Bundle;
import android.text.TextUtils;

import com.app.haiercash.base.net.api.HttpMethod;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.retrofit.RetrofitFactory;
import com.app.haiercash.base.net.rxjava.BaseObserver;
import com.app.haiercash.base.net.rxjava.DisposableUtils;
import com.app.haiercash.base.net.rxjava.RxSchedulers;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.InfoEncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.service.GhLogService;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;


/**
 * Author: Sun<br/>
 * Date :    2018/3/14<br/>
 * FileName: NetHelper<br/>
 * Description: 1，网络请求封装<br/>
 * 2，供BaseActivity/BaseFragment 调用，处理Lifecycle<br/>
 * 3,token刷新失败处理<br/>
 * 4，url是否需要token处理
 */
public class NetHelper implements INetResult {
    private Object aClass;
    private INetResult netResult;
    private LifecycleProvider mProvider;

    private final int TAG_NO_EVENT = 0x00;
    public static final int TAG_FRAGMENT_EVENT = 0x01;
    public static final int TAG_ACTIVITY_EVENT = 0x02;
    private Map<String, Object> requestData = new HashMap<>();

    private DisposableUtils disposableUtils = new DisposableUtils();

    private int mLifecycleEvent = TAG_NO_EVENT;
    //不需要token接口添加到此处
    private final String NO_TOKEN = ApiUrl.loginUrl//登录
            + ApiUrl.POST_QUERY_RESOURCE_BY_PAGE//获取资源位资源
            + ApiUrl.API_REQUEST_TOKEN//获取token
            + ApiUrl.url_yanzhengma_get//发送手机验证码
            + ApiUrl.url_zhanghaochaxun_get//查询是否该手机是否注册过
            + ApiUrl.url_zhuceyonghu_post//用户注册
            + ApiUrl.url_yinghangmingcheng_get//银行列表名称查询
            + ApiUrl.url_yanzhengma_xiaoyan//校验短信验证码
            + ApiUrl.APP_MANAGE_VERSION_CHECK//6.82.	(GET)检测app版本
            + ApiUrl.urlVerifyAndBindDeviceId//3.4.23.	(POST) 校验验证码并绑定设备号
            + ApiUrl.url_selectByParams//字典项
            + ApiUrl.url_findLoginPwd2Update//12.3.	(POST)找回登录密码之修改登录密码
            + ApiUrl.URL_CONFIG_NOTICE//请求文案
            + ApiUrl.URL_GET_LAST_LOGIN_TYPE//上一次登录方式
            //帮助中心
            + ApiUrl.allQuestionType //urlHelpCenter
            + ApiUrl.commonProblem//urlHelpContent
            + ApiUrl.allProblemByProblemType
            //账户体系相关接口
            + ApiUrl.POST_USER_STATUS + ApiUrl.POST_FOUR_INFO + ApiUrl.POST_UPDATE_LOGIN_PWD + ApiUrl.POST_UPDATE_LOGIN_PWD_BY_REAL_INFO + ApiUrl.POST_CHECK_THREE_INFO + ApiUrl.POST_RELEASE_MOBILE + ApiUrl.POST_ChANGE_MOBILE_REALINFO
            + ApiUrl.POST_APPEAL_CERT_INFO_CHECK
            + ApiUrl.POST_APPEAL_COMMIT;
    //header不传业务线相关（主要是注册相关接口，包括显示注册和隐式注册）
    private final String NO_USER_BUSINESS = ApiUrl.POST_SMS_LOGIN//短信验证码登录（新用户可以直接注册）
            + ApiUrl.url_zhuceyonghu_post//用户注册
            + ApiUrl.URL_POST_LOGIN_REGISTER;//微信绑定及注册或登陆接口

    /**
     * 需要监听报文的接口
     */
    private final String CONTROL_URL = ApiUrl.URL_APPLY_INFO_RISK_INFO + ApiUrl.URL_COMMIT_ORDER_RISK_INFO + ApiUrl.URL_Active_Repayment;

    public NetHelper() {
    }

    /**
     * @param object 可以的值为继承BaseActivity、BaseFragment或实现LifecycleProvider、INetResult或为NULL
     */
    public NetHelper(Object object) {
        this(object, (object instanceof INetResult) ? (INetResult) object : null);
    }

    public NetHelper(Object object, INetResult result) {
        this(object, (object instanceof LifecycleProvider) ? (LifecycleProvider) object : null, result);
    }

    private NetHelper(Object activityOrFragment, LifecycleProvider provider, INetResult result) {
        if (activityOrFragment != null) {
            aClass = activityOrFragment;
            if (activityOrFragment instanceof BaseActivity) {
                mLifecycleEvent = TAG_ACTIVITY_EVENT;
            } else if (activityOrFragment instanceof BaseFragment) {
                mLifecycleEvent = TAG_FRAGMENT_EVENT;
            }
        }
        mProvider = provider;
        netResult = result;
    }

    /**
     * get 请求数据
     *
     * @param url  地址
     * @param data 请求参数
     */
    public <E> void getService(String url, E data) {
        getService(url, data, null);
    }

    /**
     * get 请求数据
     *
     * @param url       请求地址
     * @param data      请求参数
     * @param className body类型
     */
    public <E> void getService(String url, E data, Class className) {
        getService(url, data, className, false);
    }

    /**
     * get 请求数据
     *
     * @param url       请求地址
     * @param data      请求参数
     * @param className body类型
     */
    public <E> void getService(String url, E data, Class className, boolean isIEncrypt) {
        String randomKey = "";
        String randomIv = "";
        if (!noEncryptUrls(url)) {
            randomKey = InfoEncryptUtil.getSixteenRandomKey();
            randomIv = InfoEncryptUtil.getSixteenRandomIV();
        }
        Observable observer = getObservable(url, null, data, HttpMethod.GET, randomKey, randomIv);
        startCommunication(observer, url, className, isIEncrypt, randomKey, randomIv);
    }


    /**
     * post 请求数据
     *
     * @param url  地址
     * @param data 请求参数
     */
    public <E> void postService(String url, E data) {
        postService(url, data, null);
    }


    /**
     * post 请求数据
     *
     * @param url       请求地址
     * @param data      请求参数
     * @param className body类型
     */
    public <E> void postService(String url, E data, Class className) {
        postService(url, data, className, false);

    }

    /**
     * post 请求数据
     *
     * @param url       请求地址
     * @param data      请求参数
     * @param className body类型
     */
    public <E> void postService(String url, E data, Class className, boolean isIEncrypt) {
        String randomKey = "";
        String randomIv = "";
        if (!noEncryptUrls(url)) {
            randomKey = InfoEncryptUtil.getSixteenRandomKey();
            randomIv = InfoEncryptUtil.getSixteenRandomIV();
        }
        Observable observer = getObservable(url, null, data, HttpMethod.POST, randomKey, randomIv);
        startCommunication(observer, url, className, isIEncrypt, randomKey, randomIv);
    }

    /**
     * post 请求数据（文件上送）
     *
     * @param url          地址
     * @param requestParam 请求参数
     * @param filePaths    文件路径
     */
    public void postService(String url, Map<String, String> requestParam, Map<String, String> filePaths) {
        postService(url, requestParam, filePaths, null);
    }

    /**
     * post 请求数据（文件上送）
     *
     * @param url          请求地址
     * @param requestParam 请求参数
     * @param filePaths    文件路径
     * @param className    body类型
     */
    public void postService(String url, Map<String, String> requestParam, Map<String, String> filePaths, Class className) {
        postService(url, requestParam, filePaths, className, false);
    }

    /**
     * post 请求数据（文件上送）
     *
     * @param url          请求地址
     * @param requestParam 请求参数
     * @param filePaths    文件路径
     * @param className    body类型
     */
    public void postService(String url, Map<String, String> requestParam, Map<String, String> filePaths, Class className, boolean isIEncrypt) {
        String randomKey = "";
        String randomIv = "";
        if (!noEncryptUrls(url)) {
            randomKey = InfoEncryptUtil.getSixteenRandomKey();
            randomIv = InfoEncryptUtil.getSixteenRandomIV();
        }
        if (noEncryptUrlsWithKeyAndIVParams(url)) {
            requestParam.put("randomKey", RSAUtils.encryptByRSANew(randomKey));
            requestParam.put("randomIv", RSAUtils.encryptByRSANew(randomIv));
        }
        Observable observer = getObservable(url, requestParam, filePaths, HttpMethod.UPLOAD, randomKey, randomIv);
        startCommunication(observer, url, className, isIEncrypt, randomKey, randomIv);
    }


    /**
     * put 请求数据
     *
     * @param url  地址
     * @param data 请求参数
     */
    public <E> void putService(String url, E data) {
        putService(url, data, null);
    }

    /**
     * put 请求数据
     *
     * @param url       请求地址
     * @param data      请求参数
     * @param className body类型
     */
    public <E> void putService(String url, E data, Class className) {
        putService(url, data, null, false);
    }

    /**
     * put 请求数据
     *
     * @param url        请求地址
     * @param data       请求参数
     * @param className  body类型
     * @param isIEncrypt 出参是否加密
     */
    public <E> void putService(String url, E data, Class className, boolean isIEncrypt) {
        String randomKey = "";
        String randomIv = "";
        if (!noEncryptUrls(url)) {
            randomKey = InfoEncryptUtil.getSixteenRandomKey();
            randomIv = InfoEncryptUtil.getSixteenRandomIV();
        }
        Observable observer = getObservable(url, null, data, HttpMethod.PUT, randomKey, randomIv);
        startCommunication(observer, url, className, isIEncrypt, randomKey, randomIv);
    }

    /**
     * 获取观察者
     */
    private Observable<BasicResponse> getObservable(String url, Map map, Object data, HttpMethod method, String randomKey, String randomIv) {
        boolean hasToken = NO_TOKEN.contains(url);
        RetrofitFactory factory = RetrofitFactory.getInstance();
        //设置是否token
        factory.addService(url, !hasToken);
        //设置是否使用用户的业务线
        boolean useUserBusiness = !NO_USER_BUSINESS.contains(url);
        factory.addBusiness(url, useUserBusiness);
        //如果为检测接口，则保存请求数据
        addRequest(url, data);
        return factory.connectNetWork(url, map, data, method, randomKey, randomIv);
    }

    /**
     * 如果当前请求接口不是指定的接口，则不保存数据
     */
    private void addRequest(String url, Object data) {
        if (CONTROL_URL.contains(url)) {
            requestData.put(url, data);
        }
    }

    /**
     * 开始进行网络通讯
     */
    private void startCommunication(Observable observable, String url, Class className, boolean isIEncrypt, String randomKey, String randomIv) {
        BaseObserver observer = new BaseObserver(url, className, this, isIEncrypt, randomKey, randomIv);
        lifecycle(observable, observer);
        observable.retryWhen(RxSchedulers.throwableFlatMap()).subscribe(observer);
    }

    /**
     * 不需要加密的url
     *
     * @param url
     * @return
     */
    private boolean noEncryptUrls(String url) {
        if (TextUtils.isEmpty(url)) return false;
        if (url.contains("app/appmanage")
                || url.contains(NetConfig.API_REQUEST_TOKEN)
                ||url.contains(ApiUrl.POST_URL_PSERSON_CENTER_NEW)
                ||url.contains(ApiUrl.POST_MODEL_DATA)) {
            return true;
        }
        return false;
    }

    /**
     * 不需要加密的接口 却要设置加解密的key跟iv偏移量
     *
     * @param url
     * @return
     */
    private boolean noEncryptUrlsWithKeyAndIVParams(String url) {
        if (TextUtils.isEmpty(url)) return false;
        if (url.contains(ApiUrl.url_face_check) || url.contains(ApiUrl.url_face_recognition) || url.contains(ApiUrl.URL_FEEDBACK)) {
            return true;
        }
        return false;
    }

    /**
     * 设置网络层的断开方式
     * 1，rxlife 自动断开(DESTROY时)
     * 2，CompositeDisposable 手动断开
     */
    private void lifecycle(Observable observable, BaseObserver observer) {
        if (mProvider == null) {
            return;
        }
        if (mLifecycleEvent == TAG_ACTIVITY_EVENT) {
            observable.compose(mProvider.bindUntilEvent(ActivityEvent.DESTROY));
        } else if (mLifecycleEvent == TAG_FRAGMENT_EVENT) {
            observable.compose(mProvider.bindUntilEvent(FragmentEvent.DESTROY));
        }
        addDisposable(observer);
    }


    public void addDisposable(Disposable disposable) {
        if (disposableUtils != null) {
            disposableUtils.addDisposable(disposable);
        }
    }

    /**
     * 手动断开当前页面的所有网络链接
     */
    public void recoveryNetHelper() {
        if (disposableUtils != null) {
            disposableUtils.removeDisposable();
        }
        netResult = null;
    }


    /**
     * 上送接口监听内容
     *
     * @param requestUrl   请求的URL
     * @param responseJson 返回的报文
     */
    private <T> void sendLogMsg(String requestUrl, T responseJson) {
        if (!requestData.containsKey(requestUrl)) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("serviceUrl", requestUrl);             //请求url
            map.put("requestTime", TimeUtil.calendarToString()); // 时间戳
            map.put("requestJson", requestData.get(requestUrl));  // 请求报文
            map.put("responseJson", responseJson);        //相应报文
            map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
            map.put("certNo", SpHp.getUser(SpKey.USER_CERTNO));
            map.put("custName", SpHp.getUser(SpKey.USER_CUSTNAME));
            map.put("ChannelId", SystemUtils.metaDataValueForTDChannelId(AppApplication.CONTEXT));
            // 启动intentservice
            GhLogService.startUploadLog(AppApplication.CONTEXT.getApplicationContext(), JsonUtils.toJson(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSuccess(Object success, String url) {
        //Logger.file((aClass == null ? "nu-ll" : aClass.getClass().getSimpleName()) + "\n" + url + "----onSuccess>  " + JsonUtils.toJson(success));
        if (netResult != null) {
            netResult.onSuccess(success, url);
        }
        sendLogMsg(url, success);
    }


    @Override
    public void onError(BasicResponse error, String url) {
        if (error != null && error.getHead() != null && (error.getHead().getRetFlag().equals(NetConfig.NET_CODE_TOKEN_INVALID) || error.getHead().getRetFlag().equals(NetConfig.NET_CODE_LOGIN_INVALID))) {
            CommomUtils.clearSp();
            TokenHelper.initTokenRefresh();
            String msg = error.getHead().retMsg;
            Bundle bundle = new Bundle();
            if (!CheckUtil.isEmpty(msg)) {
                bundle.putString("reason", msg);
            }
            MainActivity mainActivity;
            if ((mainActivity = ActivityUntil.findActivity(MainActivity.class)) == null) {//防止没有首页
                ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).navigation();
            } else {
                //消除拦截后导致loading框不消失的问题，因为正常onError里走取消loading框，拦截后就不走了
                //只需要注意MainActivity就行，因为其他页面跳到登录后会finish，loading框随之自动消失
                mainActivity.showProgress(false);
            }
            LoginSelectHelper.staticToGeneralLogin(bundle);
        } else if (netResult != null) {
            netResult.onError(error, url);
        }
        sendLogMsg(url, error);
    }
}
