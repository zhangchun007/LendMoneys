package com.haiercash.gouhua.x5webview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/5/21<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ApkDownloadListener implements android.webkit.DownloadListener {
    private Context context;

    public ApkDownloadListener(Context context) {
        this.context = context;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        DownloadByBrowsable(context, url);
    }

    /**
     * 由浏览器来进行下载安装
     */
    private void DownloadByBrowsable(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        context.startActivity(intent);
    }

}
