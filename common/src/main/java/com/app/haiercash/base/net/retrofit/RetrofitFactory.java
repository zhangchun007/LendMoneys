package com.app.haiercash.base.net.retrofit;

import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.app.haiercash.base.BuildConfig;
import com.app.haiercash.base.net.api.HttpMethod;
import com.app.haiercash.base.net.api.IApiService;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.interceptor.HeaderInterceptor;
import com.app.haiercash.base.net.rxjava.RxSchedulers;
import com.app.haiercash.base.utils.encrypt.InfoEncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import io.reactivex.Observable;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author: Sun<br/>
 * Date :    2017/12/19<br/>
 * FileName: RetrofitFactory<br/>
 * Description: retrofit 和Rxjava的封装<br/>
 * <p>
 * "app/appserver/appCustomerLogin"登录
 * "app/appserver/uauth/validateGesture”手势密码验证
 * "app/appserver/userinfo/getUserInfoByUserId”指纹验证
 * "app/appserver/apporder/saveAppOrderInfo"保存订单
 * "app/appserver/uauth/validatePayPasswd"验证交易密码
 * "/app/appserver/repayment/activeRepayNewService”主动还款
 * 上述接口需要做签名操作
 */
public class RetrofitFactory {

    private IApiService apiService;

    /**
     * 用于存放请求url是否需要请求头
     */
    private Map<String, Boolean> apiServiceMap;
    /**
     * 用于存放请求url是否需要请求头中传用户业务线，false则传本地固定配置业务线
     */
    private Map<String, Boolean> apiBusinessMap;

    private RetrofitFactory() {
        this(NetConfig.API_SERVER_URL);
    }

    public RetrofitFactory(String url) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .client(getHttpClient())
                .build();
        apiService = retrofit.create(IApiService.class);
        apiServiceMap = new HashMap<>();
        apiBusinessMap = new HashMap<>();
    }

    public OkHttpClient getHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                //设置连接超时
                .connectTimeout(NetConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //设置读超时
                .readTimeout(NetConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //设置写超时
                .writeTimeout(NetConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //设置网络连接失败时不允许自动重试
                .retryOnConnectionFailure(false)
                //设置请求头
                .addInterceptor(new HeaderInterceptor())
                //设置https
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
        if (!BuildConfig.DEBUG) {
            builder.proxy(Proxy.NO_PROXY);

        }
        //TODO 备案版本需注意打开，另外注意证书是否过期了，是否需要替换新的!!!!!!!!!
        //if (!NetConfig.mIsDebug || NetConfig.API_SERVER_URL.startsWith("https")) {
//        if (!BuildConfig.DEBUG) {
//            SSLSocketFactory socketFactory = getSocketFactory();
//            //测试、封测都是ip访问 不做证书校验
//            if (null != socketFactory) {
//                //增加证书校验
//                builder.sslSocketFactory(socketFactory);
//            }
//        }
        return builder.build();
    }

    /**
     * 获取ssl
     *
     * @return
     */
    private SSLSocketFactory getSocketFactory() {
        try {
            InputStream is = NetConfig.application.getAssets().open("haiercash.cer");
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("ca", certificateFactory.generateCertificate(is));
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <E> Observable<BasicResponse> get(String url, E requestParam) {
        return connectNetWork(url, null, requestParam, HttpMethod.GET, null, null);
    }

    public <E> Observable<BasicResponse> post(String url, Map query, E requestParam) {
        return connectNetWork(url, query, requestParam, HttpMethod.POST, null, null);
    }

    public <E> Observable<BasicResponse> put(String url, Map query, E requestParam) {
        return connectNetWork(url, query, requestParam, HttpMethod.PUT, null, null);
    }

    /**
     * 请求网络数据
     *
     * @param url          请求url
     * @param requestParam 请求实体
     * @param method       请求方式
     */
    @SuppressWarnings("unchecked")
    public Observable<BasicResponse> connectNetWork(String url, Map query, Object requestParam, HttpMethod method, String randomKey, String randomIv) {
        Observable<BasicResponse> observable;
        Map<String, String> getParam = new HashMap<>();
        switch (method) {
            case GET:
                if (noEncryptUrls(url)) {
                    getParam = JsonUtils.getRequest(requestParam);
                } else {
                    getParam.put("requestData", Base64.encodeToString(InfoEncryptUtil.aesEncryptWithKey(JsonUtils.toJson(requestParam), randomKey, randomIv).getBytes(), Base64.NO_WRAP));
                    getParam.put("randomKey", Base64.encodeToString(RSAUtils.encryptByRSANew(randomKey).getBytes(), Base64.NO_WRAP));
                    getParam.put("randomIv", Base64.encodeToString(RSAUtils.encryptByRSANew(randomIv).getBytes(), Base64.NO_WRAP));
                }
                if (NetConfig.IS_DEBUG_NET) {
                    Logger.i("GET\n" + url + "\n请求参数 " + JsonUtils.toJson(requestParam));
                }
                observable = apiService.get(url, getParam);
                break;
            case POST:
                String param = JsonUtils.toJson(requestParam);
                if (noEncryptUrls(url)) {
                    //param = JsonUtils.toJson(requestParam);
                    if (NetConfig.IS_DEBUG_NET) {
                        Logger.i("POST\n" + url + "\n请求参数 " + param);
                    }
                } else {
                    String lastParam = param;
                    getParam.put("requestData", InfoEncryptUtil.aesEncryptWithKey(param, randomKey, randomIv));
                    getParam.put("randomKey", RSAUtils.encryptByRSANew(randomKey));
                    getParam.put("randomIv", RSAUtils.encryptByRSANew(randomIv));
                    param = JsonUtils.toJson(getParam);
                    if (NetConfig.IS_DEBUG_NET) {
                        Logger.i("POST\n" + url + "\n请求参数 " + lastParam + "\n请求参数加密后 " + param);
                    }
                }
                RequestBody postParam = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"), param);
                observable = apiService.post(url, postParam);
                break;
            case UPLOAD:
                if (noEncryptUrls(url)) {
                    observable = upLoadFile(url, query, (Map<String, String>) requestParam);
                } else {
                    getParam.put("requestData", InfoEncryptUtil.aesEncryptWithKey(JsonUtils.toJson(requestParam), randomKey, randomIv));
                    getParam.put("randomKey", RSAUtils.encryptByRSANew(randomKey));
                    getParam.put("randomIv", RSAUtils.encryptByRSANew(randomIv));
                    observable = upLoadFile(url, query, (Map<String, String>) getParam);
                }
                break;
            default:
                String putString = JsonUtils.toJson(requestParam);
                if (noEncryptUrls(url)) {
                    //putString = JsonUtils.toJson(requestParam);
                    if (NetConfig.IS_DEBUG_NET) {
                        Logger.i("default\n" + url + "\n请求参数 " + putString);
                    }
                } else {
                    String lastString = putString;
                    getParam.put("requestData", InfoEncryptUtil.aesEncryptWithKey(putString, randomKey, randomIv));
                    getParam.put("randomKey", RSAUtils.encryptByRSANew(randomKey));
                    getParam.put("randomIv", RSAUtils.encryptByRSANew(randomIv));
                    putString = JsonUtils.toJson(getParam);
                    if (NetConfig.IS_DEBUG_NET) {
                        Logger.i("default\n" + url + "\n请求参数 " + lastString + "\n请求参数加密后 " + putString);
                    }
                }
                RequestBody putParam = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"), putString);
                observable = apiService.put(url, putParam);
                break;
        }
        return observable.compose(RxSchedulers.transform());
    }


    /**
     * 不需要加密的url
     *
     * @param url
     * @return
     */
    private boolean noEncryptUrls(String url) {
        if (url.contains(NetConfig.POST_MODEL_DATA)
                || url.contains(NetConfig.POST_URL_PSERSON_CENTER_NEW)
                || url.contains("app/appmanage")
                || url.contains(NetConfig.API_REQUEST_TOKEN)
                || url.contains("app/appserver/face/appFaceVerifyForCommon")
                || url.contains("app/appserver/face/appFaceVerifyForBusinessApply")
                || url.contains("app/appserver/userinfo/fcf/new/userfeedback")) {
            return true;
        }
        return false;
    }


    /**
     * 支持多张图片上传和参数上传
     */
    private Observable<BasicResponse> upLoadFile(String url, Map<String, String> param, Map<String, String> filePath) {
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
        multiBuilder.setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : filePath.entrySet()) {
            File file = new File(entry.getValue());
            RequestBody fileBody = MultipartBody.create(MediaType.parse("*/*"), file);
            multiBuilder.addFormDataPart(entry.getKey(), file.getName(), fileBody);
        }
        for (String key : param.keySet()) {
            String value = param.get(key);
            if (!TextUtils.isEmpty(value)) {
                multiBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, value));
            }
        }
        if (NetConfig.IS_DEBUG_NET) {
            Logger.i(url + " 请求参数 " + JsonUtils.toJson(param) + "\n文件" + JsonUtils.toJson(filePath));
        }
        MultipartBody multiBody = multiBuilder.build();
        return apiService.post(url, multiBody);
    }


    /**
     * 调用外部第三方接口
     */
    public void postOthers(final String url, Map<String, Object> map, final INetResult netResult) {
        Call<ResponseBody> responseBodyCall = apiService.post(url, map);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (netResult != null && response.body() != null) {
                        netResult.onSuccess(response.body().string(), url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (netResult != null) {
                    netResult.onError(new BasicResponse(NetConfig.NET_CODE_SOCKET_TIMEOUT, t.getMessage()), url);
                }
            }
        });
    }

    /**
     * 调用外部第三方接口
     */
    public Call<ResponseBody> getOthers(final String url, Map<String, Object> map, final INetResult netResult) {
        Call<ResponseBody> responseBodyCall = apiService.getOthers(url, map);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (netResult != null && response.body() != null) {
                        netResult.onSuccess(response.body().string(), url);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (netResult != null) {
                    netResult.onError(new BasicResponse(NetConfig.NET_CODE_SOCKET_TIMEOUT, t.getMessage()), url);
                }
            }
        });
        return responseBodyCall;
    }

    /**
     * 调用外部第三方接口
     */
    public Call<ResponseBody> getOthers(final String url, String data, final INetResult netResult) {
        RequestBody postParam = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"), data);
        Call<ResponseBody> responseBodyCall = apiService.getOthers(url, postParam);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (netResult != null && response.body() != null) {
                        netResult.onSuccess(response.body().string(), url);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (netResult != null) {
                    netResult.onError(new BasicResponse(NetConfig.NET_CODE_SOCKET_TIMEOUT, t.getMessage()), url);
                }
            }
        });
        return responseBodyCall;
    }

    /**
     * 支持多张图片上传和参数上传
     */
    public void upLoadFileOther(final String url, Map<String, String> param, Map<String, String> filePath, final INetResult netResult) {
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
        multiBuilder.setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : filePath.entrySet()) {
            File file = new File(entry.getValue());
            RequestBody fileBody = MultipartBody.create(MediaType.parse("*/*"), file);
            //fileBody = MultipartBody.create(MediaType.parse(guessMimeType(file.getName())), file);
            multiBuilder.addFormDataPart(entry.getKey(), file.getName(), fileBody);
        }
        for (String key : param.keySet()) {
            String value = param.get(key);
            if (!TextUtils.isEmpty(value) && value != null) {
                multiBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, value));
            }
        }
        if (NetConfig.IS_DEBUG_NET) {
            Logger.i(url + " 请求参数 " + JsonUtils.toJson(param) + "\n文件" + JsonUtils.toJson(filePath));
        }
        MultipartBody multiBody = multiBuilder.build();
        Call<ResponseBody> responseBodyCall = apiService.getOthers(url, multiBody);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (netResult != null && response.body() != null) {
                        netResult.onSuccess(response.body().string(), url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (netResult != null) {
                    netResult.onError(new BasicResponse<>(NetConfig.NET_CODE_SOCKET_TIMEOUT, t.getMessage()), url);
                }
            }
        });
    }

    public void download(String url, Map<String, String> header, final String filePathName) {
        final long startTime = System.currentTimeMillis();
        Call<ResponseBody> responseBodyCall = apiService.download(url, header);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    if (response.body() != null) {
                        is = response.body().byteStream();
                        //long total = response.body().contentLength();
                        File file = new File(filePathName);
                        fos = new FileOutputStream(file);
                        //long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            //sum += len;
                            //int progress = (int) (sum * 1.0f / total * 100);
                            //Logger.e("download progress : " + progress);
                            //mView.onDownloading("",progress);
                        }
                        fos.flush();
                        Logger.e("download success:" + filePathName);
                        Logger.e("totalTime=" + (System.currentTimeMillis() - startTime));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("download failed : " + e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Logger.e("download failed : " + t.getMessage());
            }
        });
    }


    /**
     * 添加是否需要token
     */
    public void addService(String key, boolean value) {
        apiServiceMap.put(key, value);
    }

    public boolean getServiceValue(String key) {
        Boolean result = apiServiceMap.get(key);
        if (result == null) {
            return false;
        }
        return result;
    }

    /**
     * 添加是否需要传用户业务线
     */
    public void addBusiness(String key, boolean value) {
        apiBusinessMap.put(key, value);
    }

    public boolean getBusinessValue(String key) {
        Boolean result = apiBusinessMap.get(key);
        if (result == null) {
            return false;
        }
        return result;
    }

    private static class SingletonHolder {
        private static RetrofitFactory instance = new RetrofitFactory();
    }

    public static RetrofitFactory getInstance() {
        return SingletonHolder.instance;
    }

}
