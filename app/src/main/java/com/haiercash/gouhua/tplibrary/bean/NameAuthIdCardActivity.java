//package com.haiercash.gouhua.tplibrary.bean;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//
//import com.app.haiercash.base.net.bean.BasicResponse;
//import com.app.haiercash.base.net.config.NetConfig;
//import com.app.haiercash.base.utils.json.JsonUtils;
//import com.app.haiercash.base.utils.sp.SpHelper;
//import com.app.haiercash.base.utils.system.CheckUtil;
//import com.app.haiercash.base.utils.system.KeyBordUntil;
//import com.app.haiercash.base.utils.system.SoftHideKeyBoardUtil;
//import com.haiercash.gouhua.R;
//import com.haiercash.gouhua.activity.edu.EduCommon;
//import com.haiercash.gouhua.activity.edu.EduProgressActivity;
//import com.haiercash.gouhua.activity.edu.EduProgressHelper;
//import com.haiercash.gouhua.activity.edu.NameAuthBankCardActivity;
//import com.haiercash.gouhua.activity.edu.NameAuthStartActivity;
//import com.haiercash.gouhua.base.ApiUrl;
//import com.haiercash.gouhua.beans.IdCardInfo;
//import com.haiercash.gouhua.interfaces.SpKey;
//import com.haiercash.gouhua.service.RiskNetServer;
//import com.haiercash.gouhua.tplibrary.ocr.BaseOCRActivity;
//import com.haiercash.gouhua.uihelper.CheckAgePopupWindow;
//import com.app.haiercash.base.utils.encrypt.RSAUtils;
//import com.haiercash.gouhua.utils.SpHp;
//import com.haiercash.gouhua.utils.UMengUtil;
//import com.haiercash.gouhua.view.EduProgressBottomBarView;
//import com.haiercash.gouhua.widget.DelEditText;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//import butterknife.OnTextChanged;
//
///**
// * 实名认证->实名认证入口
// * 1,根据实名信息区分页面样式 是输入还是扫描
// * 2,根据style 判断是否补传，以及是哪个补传路径
// */
//public class NameAuthIdCardActivity extends BaseOCRActivity {
//
//    @BindView(R.id.tv_tips)
//    TextView tvTips;
//    @BindView(R.id.et_idcard_name)
//    DelEditText etIdcardName;
//    @BindView(R.id.iv_edit_name)
//    ImageView ivEditName;
//    @BindView(R.id.et_idcard_number)
//    DelEditText etIdcardNumber;
//    @BindView(R.id.iv_scan_number)
//    ImageView ivScanNumber;
//    @BindView(R.id.tv_idcard_expiry)
//    TextView tvIdcardExpiry;
//    @BindView(R.id.iv_scan_expiry)
//    ImageView ivScanExpiry;
//    @BindView(R.id.line)
//    View line;
//    @BindView(R.id.bt_next)
//    Button btNext;
//    @BindView(R.id.tv_expiry_title)
//    TextView tvExpiryTitle;
//    @BindView(R.id.tv_back_start)
//    TextView tvBackStart;
//    @BindView(R.id.progressbar)
//    EduProgressBottomBarView progressbar;
//    private Class className;
//
//    public final static String ID = NameAuthIdCardActivity.class.getSimpleName();
//
//    //输入
//    private final int Input = 0x10;
//    //扫描
//    private final int Scan = 0x11;
//    //补传
//    public final static int Supplement = 0x12;
//
//    private int mCurrentStyle;
//
//    private String fromProcedure;
//
//
//    @Override
//    protected int getLayout() {
//        return R.layout.activity_name_auth;
//    }
//
//    @Override
//    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
//        initView();
//        className = (Class) getIntent().getSerializableExtra(ID);
//    }
//
//    private void setTitle() {
//        fromProcedure = getIntent().getStringExtra("tag");
//        EduCommon.setTitle(this, progressbar);
//    }
//
//
//    private void initView() {
//        setTitle();
//        idInfo = (IdCardInfo) getIntent().getSerializableExtra("info");
//        mCurrentStyle = getIntent().getIntExtra("style", Scan);
//        if (idInfo == null) {
//            idInfo = new IdCardInfo();
//        }
//        //输入样式
//        if (CheckUtil.isEmpty(idInfo.ivBackPath)) {
//            line.setVisibility(View.GONE);
//            tvExpiryTitle.setVisibility(View.GONE);
//            tvIdcardExpiry.setVisibility(View.GONE);
//            ivEditName.setVisibility(View.GONE);
//            ivScanNumber.setVisibility(View.GONE);
//            ivScanExpiry.setVisibility(View.GONE);
//            tvTips.setText("请在正式提交额度申请之前完成扫描身份证");
//            mCurrentStyle = Input;
//        } else if (mCurrentStyle == Supplement) {
//            //补传
//            etIdcardName.setFocusable(false);
//            etIdcardNumber.setFocusable(false);
//            ivEditName.setVisibility(View.GONE);
//            tvBackStart.setVisibility(View.GONE);
//            etIdcardName.setTextColor(ContextCompat.getColor(this, R.color.text_gray_light));
//        } else {
//            //扫描
//            etIdcardNumber.setFocusable(false);
//            tvBackStart.setVisibility(View.GONE);
//        }
//        if (idInfo != null) {
//            etIdcardName.setText(idInfo.custName);
//            etIdcardNumber.setText(idInfo.certNo);
//            tvIdcardExpiry.setText(idInfo.certDt);
//            if (!CheckUtil.isEmpty(idInfo.custName)) {
//                etIdcardName.setSelection(etIdcardName.getInputText().length());
//            }
//        }
//        btNext.setEnabled(getButtonEnable());
//        SoftHideKeyBoardUtil.assistActivity(this);
//    }
//
//
//    @OnTextChanged(value = {R.id.et_idcard_name, R.id.et_idcard_number}, callback = OnTextChanged.Callback.TEXT_CHANGED)
//    public void onTextAfterChanged() {
//        btNext.setEnabled(getButtonEnable());
//    }
//
//
//    private boolean getButtonEnable() {
//        if (TextUtils.isEmpty(etIdcardName.getInputText())) {
//            return false;
//        }
//        if (TextUtils.isEmpty(etIdcardNumber.getInputText()) || etIdcardNumber.getInputText().length() != 18) {
//            return false;
//        }
//        return ivScanExpiry.getVisibility() != View.VISIBLE || tvIdcardExpiry.getVisibility() != View.VISIBLE || !TextUtils.isEmpty(idInfo.certDt);
//    }
//
////    /**
////     * 获取用户身份状态
////     */
////    private void getUserNature() {
////        Map<String, String> map = new HashMap<>();
////        map.put("certNo", EncryptUtil.simpleEncrypt(idInfo.getCertNo()));
////        netHelper.getService(ApiUrl.URL_CHECK_USER_NATURE, map);
////    }
//
//    private void addTDPoint(String viewName) {
//        if (mCurrentStyle == Input) {
//            addTalkingDataPoint("实名认证输入", viewName);
//        } else if (mCurrentStyle == Scan) {
//            addTalkingDataPoint("实名认证扫描", viewName);
//        }
//    }
//
//
//    /**
//     * 检查客户的出生日期是否符合
//     */
//    private void checkUserAge() {
//        Map<String, String> map = new HashMap<>();
//        map.put("userBirthDay", CheckUtil.getBirthDayFromCertNo(idInfo.certNo));
//        netHelper.getService(ApiUrl.URL_CHECK_USER_AGE, map);
//        showProgress(true);
//    }
//
//
//    @Override
//    public void ocrSuccess(int size) {
//        showProgress(false);
//        if (size == CardFront) {
//            //身份证正面
//            if (mCurrentStyle == Supplement) {
//                idInfo.custName = etIdcardName.getInputText();
//            }
//            etIdcardName.setText(idInfo.custName);
//            etIdcardNumber.setText(idInfo.certNo);
//        } else if (size == CardBack) {
//            //身份证反面
//            tvIdcardExpiry.setText(idInfo.certDt);
//        } else {
//            IdCardInfo idCardInfo = (IdCardInfo) idInfo.clone();
//            if (idCardInfo != null) {
//                idCardInfo.certNo = RSAUtils.encryptByRSA(idInfo.certNo);
//                idCardInfo.custName = RSAUtils.encryptByRSA(idInfo.custName);
//                idCardInfo.isRsa = "Y";
//            }
//            //保存身份信息到服务器
//            netHelper.postService(ApiUrl.URL_SAVE_ID_CARD, idCardInfo);
//        }
//        btNext.setEnabled(getButtonEnable());
//    }
//
//    /**
//     * 前往下一个额度申请流程
//     */
//    private void toNextEduProcess() {
//        showProgress(false);
//        SpHp.saveSpLogin(SpKey.LOGIN_AUTHMESSAGE, JsonUtils.toJson(idInfo));
//        NameAuthBankCardActivity.gotoBankCard(NameAuthIdCardActivity.this, idInfo);
//    }
//
//
//    @Override
//    public void onSuccess(Object response, String flag) {
//        if (ApiUrl.URL_SAVE_ID_CARD.equals(flag)) {
//            showProgress(false);
//            RiskNetServer.startRiskServer1(this, "verified_success", "", 0);
//            if (mCurrentStyle == Supplement) {
//                //补传后续流程
//                if (className != null) {
//                    if (!ID.equals(className.getSimpleName())) {
//                        Intent intent = getIntent();
//                        intent.setClass(mContext, className);
//                        startActivity(intent);
//                    }
//                    //回到AuthenticationFragment刷新实名认证信息状态
//                    SpHelper.getInstance().saveMsgToSp(SpKey.REFRESH_SMRZ_SUCCESS, SpKey.REFRESH_SMRZ_SUCCESS_STATE, "true");
//                    finish();
//                } else {
//                    EduProgressHelper.getInstance().checkProgress(this, true);
//                }
//            } else {
//                //正常流程
//                toNextEduProcess();
//            }
//        } else if (ApiUrl.URL_CHECK_USER_AGE.equals(flag)) {
//            UMengUtil.commonClickCompleteEvent("RealnameAuthConfirm", "下一步", "true", "");
//            toNext();
//        } else if (ApiUrl.POST_SAVA_OCR.equals(flag)) {
//            super.onSuccess(response, flag);
//        }
//
//    }
//
//    //新增年龄校验，所以合并为方法
//    private void toNext() {
//        //如果是 手输流程
//        if (mCurrentStyle == Input) {
//            toNextEduProcess();
//        } else {
//            getImgInfo(idInfo.ivPhoto, CardPhoto);
//        }
//    }
//
//    @Override
//    public void onError(BasicResponse error, String url) {
//        showProgress(false);
//        if (ApiUrl.URL_CHECK_USER_AGE.equals(url)) {
//            if (error.getHead().retFlag.equals("A18855")) {
//                new CheckAgePopupWindow(this, error.getHead().getRetMsg(), (view1, flagTag, obj) -> {
//                    UMengUtil.commonClickCompleteEvent("RealnameAuthConfirm", "下一步", "true", "");
//                    toNext();
//                }).showAtLocation(btNext);
//            } else if (!error.getHead().retFlag.equals(NetConfig.NET_CODE_SOCKET_TIMEOUT)) {
//                UMengUtil.commonClickCompleteEvent("RealnameAuthConfirm", "下一步", "false", error.getHead().getRetMsg());
//                Intent intent = new Intent(this, EduProgressActivity.class);
//                intent.putExtra("Result", error.head.retMsg);
//                startActivity(intent);
//                finish();
//            }
//        } else {
//            super.onError(error, url);
//        }
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        addTDPoint("返回");
//        if ("EDJH".equals(fromProcedure)) {
//            EduCommon.onBackPressed(this);
//        } else {
//            finish();
//        }
//    }
//
//
//    @OnClick({R.id.tv_back_start, R.id.bt_next, R.id.iv_edit_name, R.id.iv_scan_number, R.id.iv_scan_expiry})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.iv_edit_name:
//                addTDPoint("修改姓名");
//                etIdcardName.requestFocus();
//                KeyBordUntil.openKeyBord(this);
//                break;
//            case R.id.iv_scan_number:
//                addTDPoint("重新扫描正面");
//                scanIdCard(CardFront);
//                break;
//            case R.id.iv_scan_expiry:
//                addTDPoint("重新扫描反面");
//                scanIdCard(CardBack);
//                break;
//            case R.id.bt_next:
//                addTDPoint("下一步");
//                idInfo.certNo = etIdcardNumber.getInputText();
//                idInfo.custName = etIdcardName.getInputText();
//                checkUserAge();
//                break;
//            case R.id.tv_back_start:
//                addTDPoint("返回扫描");
//                Intent intent = getIntent();
//                intent.setClass(this, NameAuthStartActivity.class);
//                startActivity(intent);
//                SpHp.deleteLogin(SpKey.LOGIN_AUTHMESSAGE);
//                finish();
//                break;
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        EduProgressHelper.getInstance().onDestroy();
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        UMengUtil.pageEnd("RealnameAuthConfirmPage");
//    }
//
//    @Override
//    public void onResume() {
//        UMengUtil.pageStart("RealnameAuthConfirmPage");
//        super.onResume();
//    }
//}
