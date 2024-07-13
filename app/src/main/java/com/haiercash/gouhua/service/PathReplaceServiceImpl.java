package com.haiercash.gouhua.service;

import android.content.Context;
import android.net.Uri;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.PathReplaceService;
import com.app.haiercash.base.utils.log.Logger;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/6/28<br/>
 * 描    述：重写跳转URL<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = "/gh/PathReplaceServiceImpl") // 必须标明注解
public class PathReplaceServiceImpl implements PathReplaceService {

    @Override
    public String forString(String path) {
        System.out.println("PathReplaceServiceImpl:" + path);
        return path;// 按照一定的规则处理之后返回处理后的结果
    }

    @Override
    public Uri forUri(Uri uri) {
        System.out.println("PathReplaceServiceImpl:" + uri.toString());
        return uri;    // 按照一定的规则处理之后返回处理后的结果
    }

    @Override
    public void init(Context context) {
        System.out.println("PathReplaceServiceImpl：" + context.getClass().getSimpleName());
    }
}
