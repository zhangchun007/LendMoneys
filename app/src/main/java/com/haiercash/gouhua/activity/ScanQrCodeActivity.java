package com.haiercash.gouhua.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.fragments.ScanQrCodeFragment;
import com.haiercash.gouhua.interfaces.IScanResult;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * ================================================================
 * 作    者：stone<p/>
 * 邮    箱：shixiangfei@haiercash.com<p/>
 * 版    本：1.0<p/>
 * 创建日期：2021/7/22<p/>
 * 描    述：<p/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ScanQrCodeActivity extends BaseActivity implements IScanResult {
    ScanQrCodeFragment fragment;

    @Override
    protected int getLayout() {
        return R.layout.activity_scan_qr_code;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("分期扫码", "#ffffff");
        setTitleBarBackgroundResource(R.color.transparent);
        setBarLeftImage(R.drawable.iv_back_white, v -> {
            finish();
        });
        setBarRightText("相册", Color.WHITE, v -> {
            ActivityUntil.openFileChooseProcess(this);
        });
        requestPermission((Consumer<Boolean>) aBoolean -> {
            if (aBoolean) {
                setScanFragment();
            } else {
                showBtn2Dialog("请授权“相机”权限", "我知道了", (dialogInterface, i) -> {
                    finish();
                });
            }
        }, R.string.permission_camera, Manifest.permission.CAMERA);
    }

    private void setScanFragment() {
        //可以打开摄像头
        fragment = new ScanQrCodeFragment();
        fragment.setIResult(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, fragment);
        transaction.commitAllowingStateLoss();
        fragment.continueToScan();
    }

    @Override
    public void scanResults(String str) {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("weChatUrl", str);
        netHelper.getService(ApiUrl.GET_SCAN_URL, map);




      /*  if (!CheckUtil.isEmpty(str) && URLUtil.isNetworkUrl(str) && str.contains("processId=")
                && str.contains("channelNo=")) {
            //https://standardpay-stg.haiercash.com?processId=P16159458692514716&&channelNo=A6&&firstSpell=CKD01
            Intent intent = new Intent(this, JsWebBaseActivity.class);
            intent.putExtra("jumpKey", str);
            intent.putExtra("title", "够花");
            intent.putExtra("isShowWebViewTitle", false);
            startActivity(intent);
            finish();
        } else {
            showDialog("请扫描合作商户的二维码哦～", "重试", "退出", (dialog, which) -> {
                if (which == 1) {
                    fragment.continueToScan();
                } else {
                    finish();
                }
            });
        }*/
    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        Map<String, String> map = JsonUtils.getRequest(success);
        if (map.containsKey("type") && map.containsKey("processUrl")) {
            if ("01".equals(map.get("type")) && !TextUtils.isEmpty(map.get("processUrl"))) {
                Intent intent = new Intent(this, JsWebBaseActivity.class);
                String targetUrl = map.get("processUrl").trim();
                intent.putExtra("jumpKey", targetUrl);
                intent.putExtra("title", "够花");
                intent.putExtra("isShowWebViewTitle", false);
                intent.putExtra("isCloseUmengBridging", map.get("isCloseUmengBridging"));
                intent.putExtra("appUserAgent", map.get("appUserAgent"));
                startActivity(intent);
                finish();
            } else {
                showErrorDialog("暂不支持，请稍后重试");

            }
        } else {
            showErrorDialog("获取链接失败，请稍后重试");
        }

    }

    @Override
    public void onError(BasicResponse error, String url) {
        //super.onError(error, url);
        showErrorDialog(error.getHead().getRetMsg());


    }

    //扫码出现失败弹窗
    private void showErrorDialog(String errMsg) {
        showProgress(false);
        showBtn2Dialog(errMsg, "我知道了", (dialogInterface, i) -> {
            if (fragment != null) {
                fragment.continueToScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null && fragment != null) {
                Uri uri = data.getData();
                fragment.dealImg(uri);
            }
        }
    }
}
