package com.app.haiercash.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * *@Author: Sun
 * *@Date :    2019/6/13
 * *@FileName: HookConfig
 * *@Description: 配置hook位置
 */
public class HookConfig {


    public static final Map<String, List<String>> HOOK_CLASS = new HashMap<>();


    public static final String INSERT_CLASS = "com/app/haiercash/base/analysis/AnalysisManager";


    /**
     * activity
     */
    public static final String HOOK_ACTIVITY_LIFECYCLE = "android/support/v4/app/FragmentActivity.class";
    public static final String HOOK_FRAGMENT_LIFECYCLE = "android/support/v4/app/Fragment.class";

    /**
     * 网络请求
     */
    public static final String HOOK_NET_REQUEST = "com/app/haiercash/base/net/retrofit/RetrofitFactory.class";

    /**
     * 网络返回
     */
    public static final String HOOK_NET_RESPONSE = "com/haiercash/gouhua/network/NetHelper.class";


    static {
        //检测activity中的下列方法
        List<String> activityMethod = new ArrayList<>();
        activityMethod.add("onCreate");
        activityMethod.add("onDestroy");
        activityMethod.add("onPause");
        activityMethod.add("onResume");
        HOOK_CLASS.put(HOOK_ACTIVITY_LIFECYCLE, activityMethod);

        //检测网络请求
        List<String> fragmentMethod = new ArrayList<>();
        fragmentMethod.add("onCreateView");
        fragmentMethod.add("onActivityCreated");
        fragmentMethod.add("onHiddenChanged");
        fragmentMethod.add("onDestroyView");
        fragmentMethod.add("onDetach");
        HOOK_CLASS.put(HOOK_FRAGMENT_LIFECYCLE, fragmentMethod);



        //检测网络请求
        List<String> netRequest = new ArrayList<>();
        netRequest.add("connectNetWork");
        HOOK_CLASS.put(HOOK_NET_REQUEST, netRequest);

        //检测网络返回
        List<String> netResponse = new ArrayList<>();
        netResponse.add("onSuccess");
        netResponse.add("onError");
        HOOK_CLASS.put(HOOK_NET_RESPONSE, netResponse);
    }
    public static Map<String, List<String>> getHookClass() {
        return HOOK_CLASS;
    }

}
