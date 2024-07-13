package com.haiercash.gouhua.tplibrary.livedetect;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Sun
 * Date :    2018/10/19
 * FileName: FaceCheckActivity
 * Description:
 */
public class FaceCheckActivity extends BaseFaceActivity {

    public static final String FROM = "from";//来自哪个页面，用于判断页面而区分使用埋点
    public static final String ID = FaceCheckActivity.class.getSimpleName();
    private boolean borrowStep;

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        mNextPageClass = getIntent().getSerializableExtra(ID);
        borrowStep = getIntent().getBooleanExtra("borrowStep", false);
        if (borrowStep) {
            setTitle("完善信息");
        }
        String activityTitle = getIntent().getStringExtra("activityTitle");
        if(!TextUtils.isEmpty(activityTitle)){
            setTitle(activityTitle);
        }
    }

    @Override
    public void faceBack(boolean isSuccess, String errorOrToken, String data) {
        if (isSuccess) {
            faceCheck(data);
        }
    }

    /**
     * 人脸校验
     */
    private void faceCheck(String dataPackage) {
        String name = SpHp.getUser(SpKey.USER_CUSTNAME);//姓名
        String idNumber = SpHp.getUser(SpKey.USER_CERTNO);//身份证号
        Map<String, String> map = new HashMap<>();
        if (CheckUtil.isEmpty(idNumber)) {
            showDialog("身份信息获取失败，请退出重试");
            return;
        }
        if (CheckUtil.isEmpty(name)) {
            showDialog("身份信息获取失败，请退出重试");
            return;
        }
        showProgress(true);
        map.put("certNo", RSAUtils.encryptByRSANew(idNumber));//身份证号
        map.put("custName", RSAUtils.encryptByRSANew(name));//姓名
        map.put("bizToken", RSAUtils.encryptByRSANew(bizToken));
        map.put("dataPackage", dataPackage);
        netHelper.postService(ApiUrl.url_face_check, map, new HashMap<>());
    }

    @Override
    public void onSuccess(Object response, final String url) {
        if (response == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        if (ApiUrl.POST_FACE_TOKEN.equals(url)) {
            super.onSuccess(response, url);
        } else if (ApiUrl.url_face_check.equals(url)) {
            showProgress(false);
            Map<String, String> map = (Map<String, String>) response;
            if (!"Y".equals(map.get("isOK"))) {
                onError("人脸认证失败~~");
            } else {
                UiUtil.toast("人脸校验成功");
                if ("WJJYMM".equals(getIntent().getStringExtra(FROM))) {
                    RiskNetServer.startRiskServer1(this, "reset_pin_face_verify_success", "", 0);
                } else if ("XGSJH".equals(getIntent().getStringExtra(FROM))) {
                    RiskNetServer.startRiskServer1(this, "modify_reg_phone_face_verify_success", "", 0);
                }
                isPageGo();
                finish();
            }
        }
    }
}
