package com.app.haiercash.base.utils.system;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.app.haiercash.base.bui.BaseGHActivity;
import com.app.haiercash.base.utils.FileUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.view.CustomToast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/10
 * 描    述：下载文件
 * 修订历史：
 * ================================================================
 */
public class Downloader {
    private static final String TAG = "Downloader";
    private static final String FILE_NAME = "Gouhua.apk";

    private static List<Info> query(Context context, int status) {
        List<Info> result = new ArrayList<>();
        DownloadManager dm = (DownloadManager) context.getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(status);
        if (dm == null) {
            Log.e(TAG, "query（）-》DownloadManager 为空");
            return result;
        }
        Cursor cursor = dm.query(query);
        if (cursor == null) {
            return result;
        }
        while (cursor.moveToNext()) {
            Info obj = new Info();
            obj.setId(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID)));
            obj.setTitle(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)));
            obj.setLocalUri(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
            obj.setBytesDownloadedSoFar(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
            obj.setTotalSizeBytes(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
            obj.setUri(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI)));
            obj.setStatus(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)));
            result.add(obj);
        }
        cursor.close();
        Log.e(TAG, "download tasks: " + result.toString());
        return result;
    }

    private static List<Info> queryRunning(Context context) {
        return query(context, DownloadManager.STATUS_RUNNING);
    }


    private static void enqueue(Context context, String uri) {
        File apkFile = new File(FileUtils.getExternalFilesDir(), FILE_NAME);
        if (apkFile.exists()) {
            apkFile.delete();
        }
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(uri));
        r.setDestinationInExternalFilesDir(context, null, FILE_NAME);
        r.setMimeType("application/vnd.android.package-archive");
        r.allowScanningByMediaScanner();
        r.setTitle("够花");
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager dm = (DownloadManager) context.getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
        if (dm == null) {
            Log.e(TAG, "enqueue（）-》DownloadManager 为空");
            return;
        }
        dm.enqueue(r);
    }

    public static void enqueueOnly(Context context, String uri, Runnable call) {
        List<Info> list = queryRunning(context);
        for (int i = 0; i < list.size(); i++) {
            Info info = list.get(i);
            if (uri.equals(info.getUri())) {
                call.run();
                Log.e(TAG, String.format("enqueueOnly: %s is download running", uri));
                return;
            }
        }
        enqueue(context, uri);
    }

    public static class DownloadBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId != -1) {
                try {
                    if (ActivityUntil.getTopActivity() != null) {
                        ((BaseGHActivity) ActivityUntil.getTopActivity()).showProgress(false, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                File apkFile = new File(FileUtils.getExternalFilesDir(), FILE_NAME);
                if (apkFile == null || !apkFile.exists()) {
                    CustomToast.makeText(context, "安装文件不存在或已损坏");
                    return;
                }

                Logger.e("开始安装apk" + apkFile.getAbsolutePath());
                Intent installIntent = new Intent();
                installIntent.setAction(Intent.ACTION_VIEW);
                installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri apkFileUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    apkFileUri = FileProvider.getUriForFile(context, "com.haiercash.gouhua.fileprovider", apkFile);
                    installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    apkFileUri = Uri.fromFile(apkFile);
                }
                installIntent.setDataAndType(apkFileUri, "application/vnd.android.package-archive");
                try {
                    context.startActivity(installIntent);
                } catch (ActivityNotFoundException e) {
                    Logger.e(e.toString());
                }
            }
        }
    }

    public static class Info {
        private String id;
        private String title;
        private String localUri;
        private String bytesDownloadedSoFar;
        private String totalSizeBytes;
        private String uri;
        private String status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLocalUri() {
            return localUri;
        }

        public void setLocalUri(String localUri) {
            this.localUri = localUri;
        }

        public String getBytesDownloadedSoFar() {
            return bytesDownloadedSoFar;
        }

        public void setBytesDownloadedSoFar(String bytesDownloadedSoFar) {
            this.bytesDownloadedSoFar = bytesDownloadedSoFar;
        }

        public String getTotalSizeBytes() {
            return totalSizeBytes;
        }

        public void setTotalSizeBytes(String totalSizeBytes) {
            this.totalSizeBytes = totalSizeBytes;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return String.format("id=%s,title=%s,local_uri=%s,size=%s,totalsize=%s.uri=%s",
                    id, title, localUri, bytesDownloadedSoFar, totalSizeBytes, uri);
        }
    }
}
