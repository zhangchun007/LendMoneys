package com.haiercash.gouhua.tplibrary.ocr;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.FaceOcrHelper;
import com.haiercash.gouhua.tplibrary.bean.IDCardInfoCollection;
import com.haiercash.gouhua.tplibrary.bean.IdCardInfo;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.megvii.idcardlib.IDCardScanActivity;
import com.megvii.idcardlib.utils.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.functions.Consumer;

/**
 * Author: Sun<br/>
 * Date :    2017/11/10<br/>
 * FileName: BaseOCRActivity<br/>
 * Description: 作为OCR的基类<br/>
 */

public abstract class BaseOCRActivity extends BaseActivity {

    public static final int CardBack = 0x01;

    public static final int CardFront = 0x00;
    public static final int CardPhoto = 0x02;

    public static final int OCRResult = 0x20;

    protected IdCardInfo idInfo = new IdCardInfo();
    private AtomicBoolean mRefreshing = new AtomicBoolean(false);

    /**
     * 正面
     */
    protected byte[] tempCardFrontPath;
    /**
     * 背面
     */
    protected byte[] tempCardBackPath;
    /**
     * 头像
     */
    protected byte[] tempPhotoPath;


    private FaceOcrHelper faceHelper;

    private long currentTime = 0;
    public long gapTime = 0;
    private int current;
    public boolean isNeedShowCheckStudentDialog; //是否需要展示非学生承诺函
    public String contName; //非学生承诺函
    public String contUrl;  //地址

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        faceHelper = new FaceOcrHelper(this, true, null);
    }

//    protected void setIdInfo(IdCardInfo idInfo) {
//        this.idInfo = idInfo;
//    }

    /**
     * 权限检查以及开启OCR
     *
     * @param requestCode code
     */
    protected void scanIdCard(final int requestCode) {
        //为了防止重复点击
        if (!mRefreshing.compareAndSet(false, true)) {
            return;
        }
        requestPermission((Consumer<Boolean>) aBoolean -> {
            mRefreshing.set(false);
            if (aBoolean) {
                enterNextPage(requestCode, requestCode);
            } else {
                showDialog("请授权“相机”权限");
            }
        }, R.string.permission_camera, Manifest.permission.CAMERA);
        mRefreshing.set(false);
    }

    private void enterNextPage(int side, int requestCode) {
        currentTime = System.currentTimeMillis();
        Intent intent = new Intent(this, IDCardScanActivity.class);
        if (side == CardFront) {
            Configuration.setCardType(this, 1);
        } else if (side == CardBack) {
            Configuration.setCardType(this, 2);
        }
        intent.putExtra("side", side);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 延迟启动
     */
    protected void scanIdCardDelay(final int side) {
        showProgress(true, "识别中");
        new Handler().postDelayed(() -> {
            showProgress(false);
            scanIdCard(side);
        }, 2000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gapTime = System.currentTimeMillis() - currentTime;
        if (resultCode == RESULT_OK) {
            //intent.putExtra("portraitimg_bitmap", data.getByteArrayExtra("portraitimg_bitmap"));
            //intent.putExtra("idcardimg_bitmap", data.getByteArrayExtra("idcardimg_bitmap"));
            byte[] portraitImg = data.getByteArrayExtra("portraitimg_bitmap");
            byte[] iDCardImg = data.getByteArrayExtra("idcardimg_bitmap");
            if (requestCode == CardBack) {
                //tempCardBackPath = data.getStringExtra("idcardImg");
                tempCardBackPath = iDCardImg;
                if (CheckUtil.isEmpty(tempCardBackPath)) {
                    postScanEvent("OCRCardReverse_gouhua", gapTime, "false", "身份证照片识别错误");
                    showDialog("身份证照片识别错误,请重试");
                } else {
                    current = CardBack;
                    getImgInfo(tempCardBackPath, CardBack);
                }
            } else if (requestCode == CardFront) {
                //tempCardFrontPath = data.getStringExtra("idcardImg");
                //tempPhotoPath = data.getStringExtra("portraitImg");
                tempCardFrontPath = iDCardImg;
                tempPhotoPath = portraitImg;
                if (CheckUtil.isEmpty(tempCardFrontPath) || CheckUtil.isEmpty(tempPhotoPath)) {
                    postScanEvent("OCRCardFront_gouhua", gapTime, "false", "身份证照片识别错误");
                    showDialog("身份证照片识别错误,请重试");
                } else {
                    current = CardFront;
                    getImgInfo(tempCardFrontPath, CardFront);
                }
            }
        }
    }


    /**
     * 获取照片对应的信息
     */
    protected void getImgInfo(byte[] iDCardImg, int side) {
        showProgress(true, "识别中");
        Map<String, String> map = new HashMap<>();
        String dataPackage = Base64.encodeToString(iDCardImg, Base64.DEFAULT);
        map.put("dataPackage", CheckUtil.isEmpty(dataPackage) ? "" : dataPackage);
        map.put("picType", String.valueOf(side + 1));
        map.put("organization", "06");
        map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
        netHelper.postService(ApiUrl.POST_SAVA_OCR, map, IDCardInfoCollection.class, true);
    }

//    /**
//     * 获取照片对应的信息
//     */
//    protected void getImgInfo(String path, int side) {
//        showProgress(true, "识别中");
//        Map<String, String> map = new HashMap<>();
//        String dataPackage = EncryptUtil.getImageStr(path);
//        map.put("dataPackage", CheckUtil.isEmpty(dataPackage) ? "" : dataPackage);
//        map.put("picType", String.valueOf(side + 1));
//        map.put("organization", "06");
//        map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
//        netHelper.postService(ApiUrl.POST_SAVA_OCR, map, IDCardInfoCollection.class);
//    }

    @Override
    public void onSuccess(Object success, String url) {
        showProgress(false);
        //保存ocr数据
        IDCardInfoCollection infoCollection = (IDCardInfoCollection) success;
        if (infoCollection == null) {
            showDialog("服务器开小差了，请稍后再试");
            return;
        }
        if (String.valueOf(CardFront).equals(infoCollection.getSide())) {
            isNeedShowCheckStudentDialog = "Y".equals(infoCollection.getMaybeStudent())
                    && "N".equals(infoCollection.getSignAgreementStatus())
                    && !CheckUtil.isEmpty(infoCollection.getContName())
                    && !CheckUtil.isEmpty(infoCollection.getContUrl());
            contName = infoCollection.getContName();
            contUrl = infoCollection.getContUrl();
        }
        if ((String.valueOf(CardFront).equals(infoCollection.getSide()) && checkCardFrontInfo(infoCollection))
                || (String.valueOf(CardBack).equals(infoCollection.getSide()) && checkCardBackInfo(infoCollection))) {
            ocrSuccess(Integer.parseInt(infoCollection.getSide()));
        } else if (TextUtils.isEmpty(infoCollection.getSide())) {
            ocrSuccess(CardPhoto);
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        if (current == CardFront) {
            postScanEvent("OCRCardFront_gouhua", gapTime, "false", error.getHead().getRetMsg());
        } else if (current == CardBack) {
            postScanEvent("OCRCardReverse_gouhua", gapTime, "false", error.getHead().getRetMsg());
        }
    }

    /**
     * 检查身份证反面信息是否完整
     *
     * @param infoCollection 扫描出来的信息
     */
    protected boolean checkCardBackInfo(IDCardInfoCollection infoCollection) {
        if (infoCollection == null
                || CheckUtil.isEmpty(infoCollection.path)
                || CheckUtil.isEmpty(infoCollection.issuedBy)
                || CheckUtil.isEmpty(infoCollection.validDateStart)
                || CheckUtil.isEmpty(infoCollection.validDateEnd)) {
            UiUtil.toast("身份证反面扫描失败，请重新扫描");
            return false;
        }
        //保存相关信息
        idInfo.certOrga = infoCollection.issuedBy;
        //idInfo.ivBackPath = infoCollection.path;
        idInfo.certStrDt = infoCollection.validDateStart;
        idInfo.certEndDt = infoCollection.validDateEnd;
        idInfo.ivBackPath = tempCardBackPath;
        idInfo.certDt = infoCollection.validDateStart + "-" + infoCollection.validDateEnd;
        return true;
    }

    /**
     * 检查身份证正面信息是否完整
     *
     * @param infoCollection 扫描出来的信息
     */
    protected boolean checkCardFrontInfo(IDCardInfoCollection infoCollection) {
        if (infoCollection == null
                || CheckUtil.isEmpty(infoCollection.idcardNumber)
                || infoCollection.idcardNumber.length() != 18
                || CheckUtil.isEmpty(infoCollection.name)
                || CheckUtil.isEmpty(infoCollection.nationality)
                || CheckUtil.isEmpty(infoCollection.birthDay)
                || CheckUtil.isEmpty(infoCollection.address)
                || CheckUtil.isEmpty(infoCollection.gender)) {
            UiUtil.toast("身份证正面扫描失败，请重新扫描");
            return false;
        }

        idInfo.custName = infoCollection.name;
        idInfo.certNo = infoCollection.idcardNumber;
        //idInfo.ivFrontPath = infoCollection.path;
        idInfo.ivFrontPath = tempCardFrontPath;
        idInfo.ivPhoto = tempPhotoPath;
        String sex = infoCollection.gender;
        if ("女".equals(sex)) {
            idInfo.gender = "20";
        } else if ("男".equals(sex)) {
            idInfo.gender = "10";
        }
        idInfo.birthDt = infoCollection.birthDay;
        idInfo.regAddr = infoCollection.address;
        idInfo.ethnic = infoCollection.nationality;
        return true;
    }

    /**
     * 网络OCR校验成功
     */
    public abstract void ocrSuccess(int size);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (faceHelper != null) {
            faceHelper.onDestroy();
        }
    }

    //上报扫描事件
    public void postScanEvent(String eventId, long stayTime, String success, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("stay_time", stayTime);
        UMengUtil.commonCompleteEvent(eventId, map, success, failReason, getPageCode());
    }

}
