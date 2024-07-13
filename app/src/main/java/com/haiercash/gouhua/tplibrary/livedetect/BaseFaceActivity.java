package com.haiercash.gouhua.tplibrary.livedetect;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.FaceOcrHelper;
import com.haiercash.gouhua.tplibrary.bean.BizTokenBean;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.megvii.livenesslib.FaceUntil;
import com.megvii.livenesslib.IFaceCallBack;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * Author: Sun<br/>
 * Date :    2018/10/19<br/>
 * FileName: BaseFaceActivity<br/>
 * Description: 作为人脸识别的基类<br/>
 * 人脸识别分成两个子类：
 * 1，上传人脸照片
 * 2，检查人脸是否匹配
 */
public abstract class BaseFaceActivity extends BaseActivity implements IFaceCallBack {
    protected Serializable mNextPageClass;
    public FaceUntil faceUntil;
    private FaceOcrHelper faceHelper;
    protected String bizToken;//token
    @BindView(R.id.iv_start)
    Button ivStart;

    @Override
    protected int getLayout() {
        return R.layout.activity_main_face;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        SystemUtils.setWindowSecure(this);
        ivStart.setTypeface(FontCustom.getMediumFont(this));
        faceUntil = new FaceUntil(this, this);
        faceHelper = new FaceOcrHelper(this, false, authorize -> {
            if (authorize) {
                ivStart.setEnabled(true);
            } else {
                ivStart.setText("人脸识别授权失败，请退出重试.");
                ivStart.setEnabled(false);
            }
        });
    }

    @OnClick(R.id.iv_start)
    public void viewOnClick(View view) {
        UMengUtil.commonClickEvent("FaceRecognitionBeginShoot_Click", "开始拍摄", getPageCode());
        requestPermission((Consumer<Boolean>) aBoolean -> {
            if (aBoolean) {
                startLiveDetectActivity();
            } else {
                showDialog("请授权“相机”权限");
            }
        }, R.string.permission_camera, Manifest.permission.CAMERA);
    }

    /**
     * 开始扫描
     */
    private void startLiveDetectActivity() {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        String custName = SpHp.getUser(SpKey.USER_CUSTNAME);
        String custCertNo = SpHp.getUser(SpKey.USER_CERTNO);
        map.put("certNo", RSAUtils.encryptByRSA(custCertNo));
        map.put("custName", RSAUtils.encryptByRSA(custName));
        netHelper.postService(ApiUrl.POST_FACE_TOKEN, map, BizTokenBean.class);
    }

    /**
     * 跳转至下一个页面
     */
    protected void isPageGo() {
        if (mNextPageClass == null) {
            return;
        }
        Intent intent = getIntent();
        intent.setClass(this, (Class<?>) mNextPageClass);
        startActivity(intent);
    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        if (ApiUrl.POST_FACE_TOKEN.equals(url)) {
            BizTokenBean bean = (BizTokenBean) success;
            if (bean != null && !TextUtils.isEmpty(bean.getBizToken())) {
                bizToken = bean.getBizToken();
                faceUntil.startLiveDetect(bean.getBizToken());
            } else {
                UiUtil.toast("获取必要参数失败，请稍后重试");
            }
        }

    }

}
