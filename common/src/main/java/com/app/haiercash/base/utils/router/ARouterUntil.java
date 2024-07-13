package com.app.haiercash.base.utils.router;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.haiercash.base.BuildConfig;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/4/12<br/>
 * 描    述：https://github.com/alibaba/ARouter/blob/master/README_CN.md<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ARouterUntil {
    private Postcard postcard;
    private NavigationCallback interceptorCallback;//拦截回调

    private ARouterUntil(String path) {
        postcard = ARouter.getInstance().build(path);
    }

    /**
     * @param path like "/test/activity"
     */
    public static ARouterUntil getInstance(String path) {
        return new ARouterUntil(path);
    }

    public void setInterceptorCallback(NavigationCallback interceptorCallback) {
        this.interceptorCallback = interceptorCallback;
    }

    /**
     * 尽可能早，推荐在Application中初始化
     */
    public static void initARouter(Application application) {
        if (BuildConfig.DEBUG) {   // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application); // 尽可能早，推荐在Application中初始化
    }

    /**
     * 应用内简单的跳转(通过URL跳转在'进阶用法'中)
     */
    public Object navigation() {
        return postcard.navigation(null, interceptorCallback);
    }

    /**
     * Navigation to the route with path in postcard.
     *
     * @param mContext    Activity and so on.
     * @param requestCode startActivityForResult's param  必须大于0，否则没有回调
     */
    public void navigation(Activity mContext, @IntRange(from = 1) int requestCode) {
        postcard.navigation(mContext, requestCode, interceptorCallback);
    }


    /**
     * Set special flags controlling how this intent is handled.  Most values
     * here depend on the type of component being executed by the Intent,
     * specifically the FLAG_ACTIVITY_* flags are all for use with
     * {@link Context#startActivity Context.startActivity()} and the
     * FLAG_RECEIVER_* flags are all for use with
     * {@link Context#sendBroadcast(Intent) Context.sendBroadcast()}.
     */
    public ARouterUntil put(int flag) {
        postcard.withFlags(flag);
        return this;
    }


    /**
     * 跳转并携带参数
     */
    public ARouterUntil put(Bundle mBundle) {
        if (mBundle != null) {
            if (postcard.getExtras() == null) {
                postcard.with(mBundle);
            } else {
                postcard.getExtras().putAll(mBundle);
            }
        }
        return this;
    }

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String, or null
     */
    public ARouterUntil put(@Nullable String key, @Nullable String value) {
        postcard.withString(key, value);
        return this;
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a boolean
     */
    public ARouterUntil put(@Nullable String key, boolean value) {
        postcard.withBoolean(key, value);
        return this;
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value an int
     */
    public ARouterUntil put(@Nullable String key, int value) {
        postcard.withInt(key, value);
        return this;
    }

    /**
     * Inserts an ArrayList value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public ARouterUntil put(@Nullable String key, @Nullable ArrayList<String> value) {
        postcard.withStringArrayList(key, value);
        return this;
    }

    /**
     * Inserts a Serializable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Serializable object, or null
     * @return current
     */
    public ARouterUntil put(@Nullable String key, @Nullable Serializable value) {
        postcard.withSerializable(key, value);
        return this;
    }

    /**
     * 获取ContainerActivity的路由对象
     *
     * @param fragmentPath ContainerActivity (/gh/ContainerActivity) 中指定的Fragment对象
     */
    public static ARouterUntil getContainerInstance(String fragmentPath) {
        return ARouterUntil.getInstance(PageKeyPath.ACTIVITY_CONTAINER).put(PageKeyPath.PARAMETER_KEY, fragmentPath);
    }

    /**
     * fragment的startActivityForResult
     *
     * @param fragment     当前fragment
     * @param fragmentPath 需要跳转的fragment的path
     */
    public static void startActivityForResult(Fragment fragment, String fragmentPath, int requestCode) {
        Postcard postcard = ARouter.getInstance().build(PageKeyPath.ACTIVITY_CONTAINER);
        LogisticsCenter.completion(postcard);
        Class<?> destination = postcard.getDestination();
        Intent intent = new Intent(fragment.getActivity(), destination);
        intent.putExtra(PageKeyPath.PARAMETER_KEY, fragmentPath);
        fragment.startActivityForResult(intent, requestCode);
    }


    /////////////////////////////////////////////////////////

    /**
     * 根据PageKeyParameter中指定的Activity的path路径查找activity
     *
     * @param path PageKeyParameter中指定的路径
     */
    public static Class getClassByPath(String path) {
        Postcard postcard = ARouter.getInstance().build(path);
        LogisticsCenter.completion(postcard);
        return postcard.getDestination();
    }
}
