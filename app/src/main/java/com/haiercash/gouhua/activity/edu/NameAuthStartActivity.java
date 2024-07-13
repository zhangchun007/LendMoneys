package com.haiercash.gouhua.activity.edu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.tplibrary.bean.IdCardInfo;
import com.haiercash.gouhua.tplibrary.ocr.BaseOCRActivity;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: Sun<br/>
 * Date :    2018/9/26<br/>
 * FileName: NameAuthStartActivity<br/>
 * Description: 额度申请流程：OCR实名认证<br/>
 */
public class NameAuthStartActivity extends BaseOCRActivity {

    @BindView(R.id.tv_lost_idcard)
    TextView tvLostIdcard;

    private final String PAGE_NAME = "实名认证入口";

    @Override
    protected int getLayout() {
        return R.layout.activity_edu_nameauth_start;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("额度申请");
        setRightImage(R.drawable.iv_blue_details, v -> ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation());
        //如果是从新用户专享页面过来的，关闭web页面
        ContainerActivity activity = ActivityUntil.findActivity(ContainerActivity.class);
        if (activity != null) {
            activity.finish();
        }
        //如果存在信息直接跳转至银行卡页面
        String cardInfo = SpHp.getLogin(SpKey.LOGIN_AUTHMESSAGE);
        if (!TextUtils.isEmpty(cardInfo)) {
            idInfo = JsonUtils.fromJson(cardInfo, IdCardInfo.class);
            NameAuthBankCardActivity.gotoBankCard(this, idInfo);
            finish();
        }
        String status = SpHp.getLogin(SpKey.LOGIN_STATUS);
        if (!TextUtils.isEmpty(status) && !String.valueOf(EduProgressHelper.NORMAL_PROGRESS).equals(status)) {
            tvLostIdcard.setVisibility(View.INVISIBLE);
        }
        addOpenHistory();
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.ADD_USER_RECORD.equals(url)) {
            return;
        }
        super.onSuccess(success, url);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.ADD_USER_RECORD.equals(url)) {
            return;
        }
        super.onError(error, url);
    }

    @Override
    public void onBackPressed() {
        EduCommon.onBackPressed(this, "要实名认证", getPageCode(), "身份证实名页面");
    }

    /**
     * 记录OCR页
     */
    private void addOpenHistory() {
        //调用用户操作记录接口
        Map<String, String> map = new HashMap<>();
        map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
        map.put("operationName", "OCR-额度提交");
        map.put("mobileNo", RSAUtils.encryptByRSA(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
        netHelper.postService(ApiUrl.ADD_USER_RECORD, map);
    }


    @OnClick({R.id.iv_nameauth, R.id.tv_start, R.id.tv_lost_idcard})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_start:
            case R.id.iv_nameauth:
                UMengUtil.commonClickEvent("Certification_Click", "点击图片扫描身份证", getPageCode());
                scanIdCard(CardFront);
                break;
            case R.id.tv_lost_idcard:
                Intent intent = getIntent();
                intent.setClass(this, NameAuthIdCardActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    public void ocrSuccess(int size) {
        if (CardFront == size) {
            postScanEvent("OCRCardFront_gouhua", gapTime, "true", "");
            scanIdCard(CardBack);
        } else {
            postScanEvent("OCRCardReverse_gouhua", gapTime, "true", "");
            Intent intent = getIntent();
            intent.putExtra("info", idInfo);
            intent.putExtra("tag", "EDJH");
            intent.putExtra("isNeedShow", isNeedShowCheckStudentDialog);
            intent.putExtra("contName", contName);
            intent.putExtra("contUrl", contUrl);
            intent.setClass(this, NameAuthIdCardActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected String getPageCode() {
        return "CertificationOCRPage_gouhua";
    }
}
