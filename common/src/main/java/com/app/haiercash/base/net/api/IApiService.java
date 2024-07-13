package com.app.haiercash.base.net.api;


import com.app.haiercash.base.net.bean.BasicResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @Author: Sun
 * @Date :    2017/12/19
 * @FileName: IApiService
 * @Description: 网络请求的底层接口
 */

public interface IApiService {

    /**
     * 用于调用get请求的接口
     *
     * @param url
     * @param params
     * @return
     */
    @GET
    Observable<BasicResponse> get(@Url String url, @QueryMap Map<String, String> params);

    /**
     * 用于调用post请求的接口
     *
     * @param url
     * @param body 用RequestBody的目的是可以支持多类型的入参
     * @return
     */
    @POST
    Observable<BasicResponse> post(@Url String url, @Body RequestBody body);

    @PUT
    Observable<BasicResponse> put(@Url String url, @Body RequestBody body);

    /**
     * 用于调用外部接口（非Appserver）
     *
     * @param url
     * @param params
     * @return
     */
    @POST
    Call<ResponseBody> post(@Url String url, @QueryMap Map<String, Object> params);

    /**
     * 用于调用外部接口（非Appserver）
     *
     * @param url
     * @return
     */
    @POST
    Call<ResponseBody> getOthers(@Url String url, @QueryMap Map<String, Object> params);

    /**
     * 用于调用外部接口（非Appserver）
     *
     * @param url
     * @return
     */
    @POST
    Call<ResponseBody> getOthers(@Url String url, @Body RequestBody body);


    @GET
    Call<ResponseBody> download(@Url String url, @HeaderMap Map<String, String> header);

}
