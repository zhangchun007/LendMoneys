package com.haiercash.gouhua.activity.contract;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.WebSettings;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.FileUtils;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.handler.CycleHandlerCallback;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.x5webview.CusWebView;

import java.io.File;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Created by Limige on 2016/6/14.
 * 合同或协议展示页面
 * 1,判断pdf插件是否存在
 * 2，如存在执行5.
 * 3，不存在则先下载pdf插件。
 * 4，下载之后解压pdfjs.zip至私有路径（不需要权限，且不易被误删）
 * 5，下载合同并加载显示。
 */
public class CheckContractActivity extends BaseActivity {
    @BindView(R.id.webview)
    CusWebView webview;

    private String pdf_url;
    private String pdfFilePath;
    private String apply_seq;

    @Override
    protected int getLayout() {
        return R.layout.activity_contract_check;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        String token = TokenHelper.getInstance().getCacheToken();
        Intent intent = getIntent();
        apply_seq = intent.getStringExtra("applseq");
        String docType = intent.getStringExtra("docType");
        String docDesc = intent.getStringExtra("docDesc");
        if (!CheckUtil.isEmpty(docType)) {
            setTitle(docDesc);
            pdf_url = ApiUrl.urlCheckContract + "?applseq=" + apply_seq + "&docType=" + docType + "&access_token=" + token;
        } else {
            setTitle("个人借款合同");
            pdf_url = ApiUrl.urlCheckContract + "?applseq=" + apply_seq + "&access_token=" + token;
        }
        showPDFView();
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (webview != null) {
            webview.destroy();
            webview = null;
        }
        super.onDestroy();
    }

    /**
     *
     */
    private void showPDFView() {
        String unZip = FileUtils.getExternalFilesDir();
        String pdfModelName = "pdfjs";
        File file = new File(unZip, pdfModelName);
        //判断pdf组件是否存在
        if (file.exists()) {
            Logger.e("组件存在");
            downLoadPDFFile();
        } else {
            downLoadPDFModel();
        }
    }

    /**
     * 下载合同
     */
    private void downLoadPDFFile() {
        showProgress(true);
        Logger.e("开始下载合同");
        FileUtils.downLoadFile(mHandler, FileUtils.getExternalFilesDir() + "/pdf", ".pdf", pdf_url);
    }

    /**
     * 下载pdf插件
     */
    private void downLoadPDFModel() {
        showProgress(true);
        String pdfModelUrl = ApiUrl.URL_PDF_MODEL;
        FileUtils.downLoadFile(mHandler, FileUtils.getExternalFilesDir(), ".zip", pdfModelUrl);
    }

    /*
     *压缩
     */
    private void unZip(String path) {
        FileUtils.unZipFile(mHandler, path, FileUtils.getExternalFilesDir());
    }

    private final Handler mHandler = new Handler(new CycleHandlerCallback(this) {
        @Override
        public void dispatchMessage(Message msg) {
            showProgress(false);
            switch (msg.what) {
                case FileUtils.DOWN_LOAD_ERROR:
                    showDialog("合同加载失败！");
                    break;
                case FileUtils.DOWN_LOAD_SUCC:
                    String path = (String) msg.obj;
                    if (TextUtils.isEmpty(path)) {
                        showDialog("合同加载失败！");
                        return;
                    } else if (path.endsWith("zip")) {
                        unZip(path);
                    } else {
                        displayFromFile(path);
                    }
                    break;
                case FileUtils.UNZIP_ERROR:
                    System.out.println("--->FileUtils.UNZIP_ERROR");
                    break;
                case FileUtils.UNZIP_SUCC:
                    downLoadPDFFile();
                    break;
                case FileUtils.COPY_FILE_ERROR:
                    showDialog("合同加载失败,请重试");
                    break;
                case FileUtils.COPY_FILE_SUCC:
                    openFDFFile((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    });

    private void copyFile() {
        requestPermission((Consumer<Boolean>) aBoolean -> {
            if (aBoolean) {
                String newPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String newName = "个人借款合同_" + apply_seq + ".pdf";
                FileUtils.copyFile(mHandler, pdfFilePath, newPath, newName);
            } else {
                showDialog("请授权“存储权限”");
            }
        }, R.string.permission_storage, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    /**
     * 其他方式打开pdf文件
     */
    private void openFDFFile(String path) {
        try {
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(this, "com.haiercash.gouhua.fileprovider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "application/pdf");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            }
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            UiUtil.toast("借款合同加载失败，请退出重试");
        }
    }

    private void showRightText() {
        getTitleBarView().setRightImage(R.drawable.iv_open_file, v -> copyFile());
    }

    /**
     * 初始化PDF文件
     */
    private void displayFromFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            showDialog("合同加载失败，请重试");
            return;
        } else {
            pdfFilePath = filePath;
            filePath = EncryptUtil.base64(filePath);
        }
        showRightText();
        if (webview == null) {
            return;
        }
        WebSettings webSettings = webview.getSettings();
        webSettings.setAllowFileAccess(true);
        // webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webview.loadUrl("file://" + FileUtils.getExternalFilesDir() + "pdfjs/web/viewer.html?file=" + filePath);
        Logger.e("file://" + FileUtils.getExternalFilesDir() + "pdfjs/web/viewer.html?file=" + filePath);
    }


}
