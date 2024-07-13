package com.haiercash.gouhua.activity.personalinfor;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.crop.Crop;
import com.app.haiercash.base.interfaces.PicturesToChoose;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.image.PhotographUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.bankcard.MyCreditCardActivity;
import com.haiercash.gouhua.activity.edu.KeyBoardConfirmPresenter;
import com.haiercash.gouhua.activity.edu.NameAuthIdCardActivity;
import com.haiercash.gouhua.activity.edu.NameAuthIdCardPatchActivity;
import com.haiercash.gouhua.activity.edu.PerfectInfoActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.account.CustDetailInfoBean;
import com.haiercash.gouhua.beans.gesture.ValidateUserBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.tplibrary.livedetect.FaceRecognitionActivity;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.UtilPhoto;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/6/15<br/>
 * 描    述：个人中心<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class PersonalInformationActivity extends BaseActivity implements PicturesToChoose {
    private View v_my_authentication;
    private ImageView ivPersonalHeadPortrait;
    private TextView tv_account;
    private TextView tv_bank_title;
    private View ll_my_banks;
    private TextView tv_bind_card_count;
    private TextView tv_authentication_title;
    private TextView tv_name, tv_cert;
    private TextView tv_idcard_detail, tv_idcard_label;
    private TextView tv_face_detail;
    private TextView tv_info_detail;

    private boolean mHasInfo;//是否有个人资料信息
    private String mInfoUrl;//个人资料H5链接
    private String mErrorMsg;//个人资料入口拦截文案
    private KeyBoardConfirmPresenter keyBoardConfirmPresenter;

    @Override
    protected int getLayout() {
        return R.layout.personal_infoemation_activity;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.person_info_title);
        v_my_authentication = findViewById(R.id.v_my_authentication);
        ivPersonalHeadPortrait = findViewById(R.id.ivPersonalHeadPortrait);
        tv_account = findViewById(R.id.tv_account);
        tv_bank_title = findViewById(R.id.tv_bank_title);
        ll_my_banks = findViewById(R.id.ll_my_banks);
        tv_bind_card_count = findViewById(R.id.tv_bind_card_count);
        tv_authentication_title = findViewById(R.id.tv_authentication_title);
        tv_name = findViewById(R.id.tv_name);
        tv_cert = findViewById(R.id.tv_cert);
        tv_cert.setTypeface(FontCustom.getMediumFont(this));
        tv_idcard_detail = findViewById(R.id.tv_idcard_detail);
        tv_idcard_label = findViewById(R.id.tv_idcard_label);
        tv_face_detail = findViewById(R.id.tv_face_detail);
        tv_info_detail = findViewById(R.id.tv_info_detail);
        GlideUtils.loadHeadPortrait(this, ApiUrl.urlCustImage + "?userId=" + EncryptUtil.simpleEncrypt(AppApplication.userid), ivPersonalHeadPortrait, true);
        getTitleBarView().setOnClickListener(v -> goCrack());
        findViewById(R.id.llPersonalHeadPortrait).setOnClickListener(this);
        findViewById(R.id.ll_account).setOnClickListener(this);
        findViewById(R.id.ll_my_banks).setOnClickListener(this);
        findViewById(R.id.ll_my_banks).setOnClickListener(this);
        findViewById(R.id.v_idcard).setOnClickListener(this);
        findViewById(R.id.v_face).setOnClickListener(this);
        findViewById(R.id.v_info).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_account.setText(CheckUtil.hidePhoneNumber(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
        if (CommomUtils.isRealName()) {
            showProgress(true);
            tv_name.setText(CheckUtil.getNameOnlyLastWords(SpHp.getUser(SpKey.USER_CUSTNAME)));
            tv_cert.setText(CheckUtil.getCertNo(SpHp.getUser(SpKey.USER_CERTNO), 1, 1));
            requestDetailInfo();
        } else {
            tv_authentication_title.setVisibility(View.GONE);
            v_my_authentication.setVisibility(View.GONE);
            tv_bank_title.setVisibility(View.GONE);
            ll_my_banks.setVisibility(View.GONE);
            tv_bind_card_count.setText(null);
        }
    }

    private void requestDetailInfo() {
        netHelper.postService(ApiUrl.URL_CUST_DETAIL_INFO, new HashMap<>(), CustDetailInfoBean.class);
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (ApiUrl.URL_CUST_DETAIL_INFO.equals(flag)) {
            setDataToView((CustDetailInfoBean) response);
        } else if (ApiUrl.urlSetPhoto.equals(flag)) {
            GlideUtils.loadHeadPortrait(this, ApiUrl.urlCustImage + "?userId=" + EncryptUtil.simpleEncrypt(AppApplication.userid), ivPersonalHeadPortrait, true);
        }
        showProgress(false);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
    }

    private void setDataToView(CustDetailInfoBean custDetailInfoBean) {
        try {
            tv_bind_card_count.setText(getString(R.string.person_info_bank_count, UiUtil.getEmptyStr(custDetailInfoBean.getBankCardNumber(), "0")));
            tv_account.setText(custDetailInfoBean.getMashLoginMobile());
            tv_name.setText(custDetailInfoBean.getMashCustName());
            tv_cert.setText(custDetailInfoBean.getMashCertNo());
            boolean hasCertImg = "Y".equals(custDetailInfoBean.getCertImgExist());
            tv_idcard_detail.setText(getString(hasCertImg ? R.string.person_info_update : R.string.person_info_no_complete));
            tv_idcard_detail.setTextColor(getResources().getColor(hasCertImg ? R.color.colorPrimary : R.color.color_ff5151));
            if ("Y".equals(custDetailInfoBean.getCertInfoState())) {
                tv_idcard_label.setText(R.string.person_info_has_expire);
                tv_idcard_label.setVisibility(View.VISIBLE);
            } else if ("E".equals(custDetailInfoBean.getCertInfoState())) {
                tv_idcard_label.setText(R.string.person_info_will_expire);
                tv_idcard_label.setVisibility(View.VISIBLE);
            } else {
                tv_idcard_label.setVisibility(View.GONE);
                tv_idcard_label.setText(null);
            }
            boolean hasFace = "Y".equals(custDetailInfoBean.getFaceImgExist());
            tv_face_detail.setText(getString(hasFace ? R.string.person_info_update : R.string.person_info_no_complete));
            tv_face_detail.setTextColor(getResources().getColor(hasFace ? R.color.colorPrimary : R.color.color_ff5151));
            mErrorMsg = custDetailInfoBean.getErrorMsg();
            mInfoUrl = custDetailInfoBean.getPersonDetailUrl();
            mHasInfo = "Y".equals(custDetailInfoBean.getCustInfoExist());
            tv_info_detail.setText(getString(!CheckUtil.isEmpty(mInfoUrl) ? R.string.person_info_update : (mHasInfo ? R.string.person_info_look : R.string.person_info_no_info)));
            tv_info_detail.setTextColor(getResources().getColor(!CheckUtil.isEmpty(mInfoUrl) || mHasInfo ? R.color.colorPrimary : R.color.color_909199));

            tv_authentication_title.setVisibility(View.VISIBLE);
            v_my_authentication.setVisibility(View.VISIBLE);
            tv_bank_title.setVisibility(View.VISIBLE);
            ll_my_banks.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ll_account) {//绑定手机号
            CommomUtils.clickToUpdateBindMobile(this);
        } else if (view.getId() == R.id.ll_my_banks) {//银行管理
            startActivity(new Intent(this, MyCreditCardActivity.class));
        } else if (view.getId() == R.id.llPersonalHeadPortrait) {
            new UtilPhoto().showDialog(this, this, true);
        } else if (view.getId() == R.id.v_idcard) {//身份证影像
            showPopupWindow("TMP_006", 1);
        } else if (view.getId() == R.id.v_face) {//人脸识别
            showPopupWindow("TMP_007", 2);
        } else if (view.getId() == R.id.v_info) {//个人资料
            //先判断是否拦截（后端判断在途或者黑灰名单），然后返回的个人资料H5链接不为空时才能修改
            if (CheckUtil.isEmpty(mErrorMsg)) {
                if (!CheckUtil.isEmpty(mInfoUrl) || mHasInfo) {
                    showPopupWindow("TMP_008", 3);
                } else {
                    UiUtil.toast(getString(R.string.person_info_no_support_add_info));
                }
            } else {
                showDialog(mErrorMsg);
            }
        } else {
            super.onClick(view);
        }
    }

    @Override
    public void setPicturesToChoose(String code, String name) {
        switch (code) {
            //拍照
            case "1":
                requestPermission(aBoolean -> {
                    if (aBoolean) {
                        PhotographUtils.startCameraCapture(PersonalInformationActivity.this);
                    }
                }, R.string.permission_camera, Manifest.permission.CAMERA);
                break;
            //从相册库中选择
            case "2":
                requestPermission(aBoolean -> {
                    if (aBoolean) {
                        PhotographUtils.startPhotoAlbum(PersonalInformationActivity.this);
                    }
                }, R.string.permission_storage, Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                //拍照
                case PhotographUtils.THECAMERA:
                    PhotographUtils.startPhotoZoom(this, PhotographUtils.getCameraUri(this, PhotographUtils.IMAGE_FILE_NAME));
                    break;
                //相册
                case PhotographUtils.PHOTOALBUM:
                    PhotographUtils.startPhotoZoom(this, data.getData());
                    break;
                //裁剪完
                case PhotographUtils.THEEDITOR:
                    String avatarUrl = EncryptUtil.base64Encode(Crop.getOutput(data));
                    if (avatarUrl != null) {
                        requestUserPhoto(avatarUrl);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 上传头像
     */
    private void requestUserPhoto(String avatarUrl) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", EncryptUtil.simpleEncrypt(AppApplication.userid));
        map.put("avatarUrl", avatarUrl);
        netHelper.putService(ApiUrl.urlSetPhoto, map, ValidateUserBean.class, true);
        showProgress(true);
    }

    private int goCrackFlag = 0;

    private void goCrack() {
        goCrackFlag++;
        if (goCrackFlag >= 10) {
            goCrackFlag = 0;
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_ACRACK).navigation();
        }
    }

    /**
     * 短信验证码弹窗
     *
     * @param bizCode  短信验证码所需bizCode
     * @param whichWay 哪一个流程跳转，1-身份证影像，2-人脸识别，3-个人资料
     */
    private void showPopupWindow(String bizCode, int whichWay) {
        if (keyBoardConfirmPresenter == null) {
            keyBoardConfirmPresenter = new KeyBoardConfirmPresenter(this, SpHp.getLogin(SpKey.LOGIN_MOBILE), bizCode);
        } else {
            keyBoardConfirmPresenter.setBizCode(bizCode);
        }
        keyBoardConfirmPresenter.showPopupWindows(((view, flagTag, obj) -> {
            if (flagTag == 1) {
                Intent intent;
                switch (whichWay) {
                    case 1:
                        intent = new Intent(PersonalInformationActivity.this, NameAuthIdCardPatchActivity.class);
                        intent.putExtra(NameAuthIdCardActivity.ID, NameAuthIdCardActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(PersonalInformationActivity.this, FaceRecognitionActivity.class);
                        intent.putExtra("tag", "XMRZ");
                        startActivity(intent);
                        break;
                    case 3:
                        if (CheckUtil.isEmpty(mInfoUrl)) {//走原生页面不能修改，只能查看
                            intent = new Intent(PersonalInformationActivity.this, PerfectInfoActivity.class);
                            intent.putExtra("isEditorTag", "N");
                            startActivity(intent);
                        } else {
                            WebHelper.startActivityForUrl(this, mInfoUrl);
                        }
                        break;
                }
            }
        }));
    }

    @Override
    protected void onDestroy() {
        if (keyBoardConfirmPresenter != null) {
            keyBoardConfirmPresenter.onDestroy();
            keyBoardConfirmPresenter = null;
        }
        super.onDestroy();
    }
}
