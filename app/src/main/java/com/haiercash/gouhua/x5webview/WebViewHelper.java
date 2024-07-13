package com.haiercash.gouhua.x5webview;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import com.app.haiercash.base.net.retrofit.RetrofitFactory;
import com.app.haiercash.base.utils.FileUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okio.ByteString;

/**
 * ================================================================
 * 作    者：L14-14<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2022/10/28-16:29<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class WebViewHelper {

    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        WebResourceResponse webResourceResponse = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 如果是 assets 目录下的文件
            if (isAssetsResource(request)) {
                webResourceResponse = assetsResourceRequest(view.getContext(), request);
            }
            // 如果是可以缓存的文件
            if (isCacheResource(request)) {
                webResourceResponse = cacheResourceRequest(view.getContext(), request);
            }
        }
        return webResourceResponse;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isAssetsResource(WebResourceRequest webRequest) {
        String url = webRequest.getUrl().toString();
        return url.startsWith("file:///android_asset/");
    }

    /**
     * assets 文件请求
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private WebResourceResponse assetsResourceRequest(Context context, WebResourceRequest webRequest) {
        String url = webRequest.getUrl().toString();
        try {
            int filenameIndex = url.lastIndexOf("/") + 1;
            String filename = url.substring(filenameIndex);
            int suffixIndex = url.lastIndexOf(".");
            String suffix = url.substring(suffixIndex + 1);
            WebResourceResponse webResourceResponse = new WebResourceResponse(
                    getMimeTypeFromUrl(url),
                    "UTF-8",
                    context.getAssets().open(suffix + "/" + filename)
            );
            Map<String, String> map = new HashMap<>();
            map.put("access-control-allow-origin", "*");
            webResourceResponse.setResponseHeaders(map);
            return webResourceResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否是可以被缓存等资源
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isCacheResource(WebResourceRequest webRequest) {
        String url = webRequest.getUrl().toString();
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        return Objects.equals(extension, "ico") || Objects.equals(extension, "bmp")
                || Objects.equals(extension, "gif") || Objects.equals(extension, "jpeg")
                || Objects.equals(extension, "jpg") || Objects.equals(extension, "png")
                || Objects.equals(extension, "svg") || Objects.equals(extension, "webp")
                || Objects.equals(extension, "css") || Objects.equals(extension, "js")
                || Objects.equals(extension, "json") || Objects.equals(extension, "eot")
                || Objects.equals(extension, "otf") || Objects.equals(extension, "ttf")
                || Objects.equals(extension, "woff");
    }


    /**
     * 可缓存文件请求
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private WebResourceResponse cacheResourceRequest(Context context, WebResourceRequest webRequest) {
        String url = webRequest.getUrl().toString();
        String mimeType = getMimeTypeFromUrl(url);

//        // WebView 中的图片利用 Glide 加载(能够和App其他页面共用缓存)
//        if (isImageResource(webRequest)) {
//            WebResourceResponse webResourceResponse = null;
//            try {
//                File file = Glide.with(context).download(url).submit().get();
//                webResourceResponse = new WebResourceResponse(mimeType, "UTF-8", new FileInputStream(file));
//                Map<String, String> map = new HashMap<>();
//                map.put("access-control-allow-origin", "*");
//                webResourceResponse.setResponseHeaders(map);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return webResourceResponse;
//        }

        /*
         * 其他文件缓存逻辑
         * 1.寻找缓存文件，本地有缓存直接返回缓存文件
         * 2.无缓存，下载到本地后返回
         * 注意！！！
         * 一定要确保文件下载完整，我这里采用下载完成后给文件加 "success-" 前缀的方法
         */
        String webCachePath = getWebViewCachePath(context);
        // 下载文件
        String sourceFilePath = webCachePath + File.separator + encodeUtf8(url);
        File cacheFile = new File(sourceFilePath);
        if (!cacheFile.exists() || !cacheFile.isFile()) {
            RetrofitFactory.getInstance().download(url, webRequest.getRequestHeaders(), sourceFilePath);
        }

        // 缓存文件存在则返回
        if (cacheFile.exists() && cacheFile.isFile()) {
            try {
                WebResourceResponse response = new
                        WebResourceResponse(mimeType, "UTF-8", new FileInputStream(cacheFile));
                Map<String, String> map = new HashMap<>();
                map.put("access-control-allow-origin", "*");
                response.setResponseHeaders(map);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取webView缓存目录（包含2天清除一次缓存数据）
     */
    public static synchronized String getWebViewCachePath(Context context) {
        String time = SpHp.getOther(SpKey.WEB_CACHE_CURRENT, "0");
        if (System.currentTimeMillis() - Long.parseLong(time) > 1000 * 60 * 60 * 24 * 2) {
            clearWebCache(context);
            SpHp.saveSpOther(SpKey.WEB_CACHE_CURRENT, System.currentTimeMillis() + "");
            Logger.i("清除WebView的缓存数据");
        }

        String cachePath;
        if (Objects.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            cachePath = context.getExternalCacheDir().getAbsolutePath() + "/webCache";
        } else {
            cachePath = context.getCacheDir().getAbsolutePath() + "/webCache";
        }
        File cacheDir = new File(cachePath);
        // 缓存目录
        if (!cacheDir.exists() || !cacheDir.isDirectory()) {
            boolean isMkdirs = cacheDir.mkdirs();
            System.out.println("设置缓存目录：" + isMkdirs);
        }
        return cachePath;
    }

    /**
     * 清除WebView的缓存
     */
    public static void clearWebCache(Context context) {
        FileUtils.deleteFile(new File(context.getCacheDir().getAbsolutePath() + "/webCache"));
        if (Objects.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            FileUtils.deleteFile(new File(context.getExternalCacheDir().getAbsolutePath() + "/webCache"));
        }
    }

    /**
     * 将需要缓存得URL 进行md5编码
     */
    private static String encodeUtf8(String str) {
        ByteString byteString = ByteString.encodeUtf8(str);
        return byteString.md5().hex();
//        return str.substring(str.lastIndexOf("/") + 1);
    }

    private String getExtensionFromUrl(String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                return MimeTypeMap.getFileExtensionFromUrl(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private String getMimeTypeFromUrl(String url) {
        try {
            String extension = getExtensionFromUrl(url);
            if (!TextUtils.isEmpty(extension)) {
                if (Objects.equals(extension, "json")) {
                    return "application/json";
                }
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                return TextUtils.isEmpty(mimeType) ? "*/*" : mimeType;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "*/*";
    }
}
