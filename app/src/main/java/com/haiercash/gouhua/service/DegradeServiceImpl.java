package com.haiercash.gouhua.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.DegradeService;
import com.app.haiercash.base.utils.log.Logger;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/6/28<br/>
 * 描    述：自定义全局降级策略<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = "/gh/DegradeServiceImpl")
public class DegradeServiceImpl implements DegradeService {

    @Override
    public void onLost(Context context, Postcard postcard) {
        Logger.w("PathReplaceServiceImpl", context.getClass().getSimpleName());
        Logger.w("PathReplaceServiceImpl", postcard.toString());
    }

    @Override
    public void init(Context context) {
        Logger.w("PathReplaceServiceImpl", context.getClass().getSimpleName());
    }
}
