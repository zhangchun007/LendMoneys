package com.app.haiercash.base.net.rxjava;

import android.text.TextUtils;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.token.TokenException;
import com.app.haiercash.base.net.token.TokenHelper;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Author: Sun<br/>
 * Date :    2018/2/7<br/>
 * FileName: RxSchedulers<br/>
 * Description: rxjava 线程相关<br/>
 */

public class RxSchedulers {

    /**
     * 线程转换
     */
    public static ObservableTransformer transform() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(@NonNull Observable upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    /**
     * retryWhen
     * 处理token异常的情况
     */
    public static Function<Observable<Throwable>, ObservableSource<?>> throwableFlatMap() {
        return new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {

                    @Override
                    public ObservableSource<?> apply(Throwable throwable) {
                        //判断当前报错是否为token失效
                        boolean isTokenInvalid = getThrowableType(throwable);
                        if (isTokenInvalid) {
                            //如果token失效且允许继续刷新，则再次请求token
                            if (TokenHelper.canRefreshToken()) {
                                return TokenHelper.getInstance().refreshTokenWhenTokenInvalid();
                            } else {
                                //token刷新次数超限，则报错
                                return Observable.error(new TokenException());
                            }
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                });
            }
        };
    }

    /**
     * 判断当前错误是否为token失效
     */
    private static boolean getThrowableType(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        if (!(throwable instanceof HttpException)) {
            return false;
        }
        Response<?> response = ((HttpException) throwable).response();
        if (response == null) {
            return false;
        }
        ResponseBody responseBody = response.errorBody();
        if (responseBody == null) {
            return false;
        }
        try {
            String string = responseBody.string();
            return !TextUtils.isEmpty(string) && BasicResponse.isTokenInvalid(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
