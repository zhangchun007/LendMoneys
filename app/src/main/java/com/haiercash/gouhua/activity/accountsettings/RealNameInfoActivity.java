package com.haiercash.gouhua.activity.accountsettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.edu.KeyBoardConfirmPresenter;
import com.haiercash.gouhua.activity.edu.NameAuthIdCardActivity;
import com.haiercash.gouhua.activity.edu.NameAuthIdCardPatchActivity;
import com.haiercash.gouhua.activity.edu.PerfectInfoActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.account.CustDetailInfoBean;
import com.haiercash.gouhua.databinding.ActivityRealNameInfoBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.tplibrary.livedetect.FaceRecognitionActivity;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;

/**
 * 设置-账号安全-实名认证（已实名才能进来）
 */
public class RealNameInfoActivity extends BaseActivity {
    private ActivityRealNameInfoBinding mInfoBinding;
    private boolean mHasInfo;//是否有个人资料信息
    private KeyBoardConfirmPresenter keyBoardConfirmPresenter;
    private boolean hasShowAnim;//是否已经展示光效，只展示一次
    private String mInfoUrl;//个人资料H5链接
    private String mErrorMsg;//个人资料入口拦截文案

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return mInfoBinding = ActivityRealNameInfoBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitleBarBackgroundResource(R.color.transparent);
        setTitle(R.string.person_info_real_name);
        mInfoBinding.tvRealName.setTypeface(FontCustom.getMediumFont(this));
        mInfoBinding.tvIdAction.setOnClickListener(this);
        mInfoBinding.tvFaceAction.setOnClickListener(this);
        mInfoBinding.tvDetailAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mInfoBinding.tvIdAction) {//身份证影像
            showPopupWindow("TMP_006", 1);
        } else if (view == mInfoBinding.tvFaceAction) {//人脸识别
            showPopupWindow("TMP_007", 2);
        } else if (view == mInfoBinding.tvDetailAction) {//个人资料
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
    protected void onResume() {
        super.onResume();
        showProgress(true);
        mInfoBinding.tvRealName.setText(getString(R.string.person_info_real_name_name, CheckUtil.getNameOnlyLastWords(SpHp.getUser(SpKey.USER_CUSTNAME))));
        mInfoBinding.tvCert.setText(CheckUtil.getCertNo(SpHp.getUser(SpKey.USER_CERTNO), 1, 1));
        requestDetailInfo();
    }

    private void requestDetailInfo() {
        netHelper.postService(ApiUrl.URL_CUST_DETAIL_INFO, new HashMap<>(), CustDetailInfoBean.class);
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (ApiUrl.URL_CUST_DETAIL_INFO.equals(flag)) {
            setDataToView((CustDetailInfoBean) response);
            showProgress(false);
            showLightAnim();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        if (ApiUrl.URL_CUST_DETAIL_INFO.equals(url)) {
            showLightAnim();
        }
    }

    private void showLightAnim() {
        try {
            if (!hasShowAnim) {
                hasShowAnim = true;
                mInfoBinding.alv.startAnim();
            }
        } catch (Exception e) {
            //
        }
    }

    private void setDataToView(CustDetailInfoBean custDetailInfoBean) {
        try {
            mInfoBinding.tvRealName.setText(getString(R.string.person_info_real_name_name, UiUtil.getEmptyStr(custDetailInfoBean.getMashCustName())));
            mInfoBinding.tvCert.setText(custDetailInfoBean.getMashCertNo());
            boolean hasCertImg = "Y".equals(custDetailInfoBean.getCertImgExist());
            mInfoBinding.tvIdAction.setText(getString(hasCertImg ? R.string.person_info_update : R.string.person_info_no_complete_1));
            mInfoBinding.tvIdAction.setTextColor(getResources().getColor(hasCertImg ? R.color.colorPrimary : R.color.white));
            mInfoBinding.tvIdAction.setBackgroundResource(hasCertImg ? R.drawable.bg_btn_real_name_info_2 : R.drawable.bg_btn_real_name_info_0);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mInfoBinding.vBgIdShadow.getLayoutParams();
            //不同状态的UI不一样，阴影也不一样
            if ("Y".equals(custDetailInfoBean.getCertInfoState())) {
                mInfoBinding.vBgIdBig.setVisibility(View.VISIBLE);
                mInfoBinding.vBgIdShadowSmall.setVisibility(View.VISIBLE);
                mInfoBinding.imgIdTip.setVisibility(View.VISIBLE);
                mInfoBinding.tvIdTip.setVisibility(View.VISIBLE);
                mInfoBinding.tvIdLabel.setText(R.string.person_info_has_expire);
                mInfoBinding.tvIdLabel.setVisibility(View.VISIBLE);
                mInfoBinding.vBgIdShadow.setBackgroundResource(R.drawable.bg_real_name_info_shadow_big);
                layoutParams.height = UiUtil.dip2px(this, 128);
            } else if ("E".equals(custDetailInfoBean.getCertInfoState())) {
                mInfoBinding.vBgIdBig.setVisibility(View.VISIBLE);
                mInfoBinding.vBgIdShadowSmall.setVisibility(View.VISIBLE);
                mInfoBinding.imgIdTip.setVisibility(View.VISIBLE);
                mInfoBinding.tvIdTip.setVisibility(View.VISIBLE);
                mInfoBinding.tvIdLabel.setText(R.string.person_info_will_expire);
                mInfoBinding.tvIdLabel.setVisibility(View.VISIBLE);
                mInfoBinding.vBgIdShadow.setBackgroundResource(R.drawable.bg_real_name_info_shadow_big);
                layoutParams.height = UiUtil.dip2px(this, 128);
            } else {
                mInfoBinding.vBgIdBig.setVisibility(View.GONE);
                mInfoBinding.vBgIdShadowSmall.setVisibility(View.GONE);
                mInfoBinding.imgIdTip.setVisibility(View.GONE);
                mInfoBinding.tvIdTip.setVisibility(View.GONE);
                mInfoBinding.tvIdLabel.setVisibility(View.GONE);
                mInfoBinding.tvIdLabel.setText(null);
                mInfoBinding.vBgIdShadow.setBackgroundResource(R.drawable.bg_real_name_info_shadow_mid);
                layoutParams.height = UiUtil.dip2px(this, 100);
            }
            mInfoBinding.vBgIdShadow.setLayoutParams(layoutParams);

            boolean hasFace = "Y".equals(custDetailInfoBean.getFaceImgExist());
            mInfoBinding.tvFaceAction.setText(getString(hasFace ? R.string.person_info_update : R.string.person_info_no_complete_1));
            mInfoBinding.tvFaceAction.setTextColor(getResources().getColor(hasFace ? R.color.colorPrimary : R.color.white));
            mInfoBinding.tvFaceAction.setBackgroundResource(hasFace ? R.drawable.bg_btn_real_name_info_2 : R.drawable.bg_btn_real_name_info_0);

            mErrorMsg = custDetailInfoBean.getErrorMsg();
            mInfoUrl = custDetailInfoBean.getPersonDetailUrl();
            mHasInfo = "Y".equals(custDetailInfoBean.getCustInfoExist());
            //跳转链接不为空的话始终显示“更新”
            mInfoBinding.tvDetailAction.setText(getString(!CheckUtil.isEmpty(mInfoUrl) ? R.string.person_info_update : (mHasInfo ? R.string.person_info_look : R.string.person_info_no_info)));
            mInfoBinding.tvDetailAction.setTextColor(getResources().getColor(!CheckUtil.isEmpty(mInfoUrl) || mHasInfo ? R.color.colorPrimary : R.color.color_BFC2CC));
            mInfoBinding.tvDetailAction.setBackgroundResource(!CheckUtil.isEmpty(mInfoUrl) || mHasInfo ? R.drawable.bg_btn_real_name_info_2 : R.drawable.bg_btn_real_name_info_1);
        } catch (Exception e) {
            //
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
                        intent = new Intent(RealNameInfoActivity.this, NameAuthIdCardPatchActivity.class);
                        intent.putExtra(NameAuthIdCardActivity.ID, NameAuthIdCardActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(RealNameInfoActivity.this, FaceRecognitionActivity.class);
                        intent.putExtra("tag", "XMRZ");
                        startActivity(intent);
                        break;
                    case 3:
                        if (CheckUtil.isEmpty(mInfoUrl)) {//走原生页面不能修改，只能查看
                            intent = new Intent(RealNameInfoActivity.this, PerfectInfoActivity.class);
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
