package com.haiercash.gouhua.tplibrary.livedetect;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.edu.EduCommon;
import com.haiercash.gouhua.activity.edu.EduProgressHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.beans.face.FaceBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：人脸识别开始界面
 * 项目作者：胡玉君
 * 创建日期：2017/6/23 9:15.
 * ------------------------------
 */
public class FaceRecognitionActivity extends BaseFaceActivity {
    public static final String ID = FaceRecognitionActivity.class.getSimpleName();
    private ArrayList<Class> classes; //支用时候资料不足，需要的后续流程
//    private String applSeq;  //流水号

    private String fromProcedure;

    @Override
    protected int getLayout() {
        return R.layout.activity_main_face;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        EduCommon.setTitle(this, null);
        setRightImage(R.drawable.iv_blue_details, v -> ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation());
        mNextPageClass = getIntent().getSerializableExtra(ID);
        classes = (ArrayList<Class>) getIntent().getSerializableExtra("followStep");
        boolean borrowStep = getIntent().getBooleanExtra("borrowStep", false);
        if (classes != null || borrowStep) {
            setTitle("完善信息");
        }
//        applSeq = getIntent().getStringExtra("applSeq");
        fromProcedure = getIntent().getStringExtra("tag");
    }


    /**
     * 额度申请流程上传人脸 和 额度支用人脸补传
     */
    private void facePost(String dataPackage) {
        //姓名
        String name = SpHp.getUser(SpKey.USER_CUSTNAME);
        //身份证号
        String idNumber = SpHp.getUser(SpKey.USER_CERTNO);
        String mobile = SpHp.getUser(SpKey.USER_MOBILE);
        String custNo = SpHp.getUser(SpKey.USER_CUSTNO);
        Map<String, String> map = new HashMap<>();
        if (CheckUtil.isEmpty(idNumber)) {
            postFaceEvent("false", "身份信息获取失败");
            showDialog("身份信息获取失败，请退出重试");
            return;
        }
        if (CheckUtil.isEmpty(name)) {
            postFaceEvent("false", "身份信息获取失败");
            showDialog("身份信息获取失败，请退出重试");
            return;
        }
//        if (CheckUtil.isEmpty(mobile)) {
//            postFaceEvent("false", "身份信息获取失败");
//            showDialog("身份信息获取失败，请退出重试");
//            return;
//        }
        if (dataPackage == null) {
            postFaceEvent("false", "人脸识别异常");
            showDialog("人脸识别异常，请稍后重试");
            return;
        }

        //身份证号
        map.put("certNo", RSAUtils.encryptByRSANew(idNumber));
        //姓名
        map.put("custName", RSAUtils.encryptByRSANew(name));
        //手机号
        map.put("mobileNo", RSAUtils.encryptByRSANew(mobile));
        map.put("custNo", RSAUtils.encryptByRSANew(custNo));
        //map.put("delta", delta);
        //face++
        //map.put("organization", "06");
        //bizToken
        map.put("bizToken", RSAUtils.encryptByRSANew(bizToken));
        map.put("dataPackage", dataPackage);
        //map.put("panoramicPhoto", (panoramicPhoto == null || panoramicPhoto.length <= 0) ? "" : EncryptUtil.Base64Encode(panoramicPhoto));
        map.put("applSeq", RSAUtils.encryptByRSANew("0"));
        //map.put("isRsa", "Y");
        //map.put("applSeq", applSeq);
        showProgress(true);
        netHelper.postService(ApiUrl.url_face_recognition, map, new HashMap<>(), FaceBean.class);
    }

    @Override
    public void onBackPressed() {
        if ("EDJH".equals(fromProcedure) && classes == null) {
            EduCommon.onBackPressed(this, "要人脸验证", getPageCode(), "人脸OCR页面");
        } else {
            finish();
        }
    }

    @Override
    public void onSuccess(Object response, final String flag) {
        if (response == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        if (ApiUrl.POST_FACE_TOKEN.equals(flag)) {
            super.onSuccess(response, flag);
        } else if (ApiUrl.url_face_recognition.equals(flag)) {
            RiskNetServer.startRiskServer1(this, "face_recognition", "", 0);
            showProgress(false);
            postFaceEvent("true", "");
            FaceBean faceBean = (FaceBean) response;
            if ("Y".equals(faceBean.isOK)) {
                RiskInfoUtils.updateRiskInfoByNode("BR02", "YES");
                UiUtil.toast("人脸识别成功");
                //人脸识别成功
                if (mNextPageClass != null) {
                    isPageGo();
                    finish();
                } else if (classes != null && classes.size() > 0) {

                    Intent intent = getIntent();
                    intent.setClass(this, classes.get(0));
                    classes.remove(0);
                    if (classes.size() != 0) {
                        intent.putExtra("followStep", classes);
                    }
                    startActivity(intent);
                    finish();
                } else if ("EDJH".equals(fromProcedure)) {
                    //RiskInoSendHelper.send(this, "FACE");
                    EduProgressHelper.getInstance().checkProgress(this, true);
                } else if ("XMRZ".equals(fromProcedure)) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    finish();
                }
            } else {
                RiskInfoUtils.updateRiskInfoByNode("BR02", "NO");
                postFaceEvent("false", "人脸识别有误");
                onError("人脸识别有误，请重试");
            }
        }
    }

    @Override
    public void faceBack(boolean isSuccess, String errorOrToken, String data) {
        facePost(data);
    }

    @Override
    protected void onDestroy() {
        EduProgressHelper.getInstance().onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected String getPageCode() {
        return "FaceRecognitionPage";
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void postFaceEvent(String success, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("recognition_number", 1);
        UMengUtil.commonCompleteEvent("FaceRecognition", map, success, failReason, getPageCode());
    }
}
