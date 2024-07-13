package com.haiercash.gouhua.gzx;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseDialogActivity;
import com.haiercash.gouhua.beans.login.QueryAgreementListBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.jsweb.WebPopLimitHeightActivity;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

//够多智授权页
public class GzxAgreementActivity extends BaseDialogActivity {
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    private Bundle bundle;

    @Override
    protected int getLayout() {
        return R.layout.activity_gzx_agreement;
    }


    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        //super.onViewCreated(savedInstanceState);
        setData();
        postGzxEvent("Gh_DiversionPage_Exposure", "确认授权");
    }

    //初始化数据
    private void setData() {
        bundle = getIntent().getExtras();
        List<QueryAgreementListBean> agreementListBeanList = (List<QueryAgreementListBean>) bundle.getSerializable("agreementList");
        String url = bundle.getString("url");
        if (CheckUtil.isEmpty(agreementListBeanList) || agreementListBeanList.size() <= 0 || CheckUtil.isEmpty(url)) {
            showDialog("提示", "数据有误，请在首页刷新重试", (dialog, which) -> finish());
        } else {
            tvAgreement.setText(getAgreement(agreementListBeanList));
            tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
            tvAgreement.setHighlightColor(Color.TRANSPARENT);
        }
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postGzxEvent("Gh_DiversionAgreement_Click", "确认授权");
                showProgress(true);
                Map<String, String> map = new HashMap<String, String>();
                map.put("idNo", SpHp.getUser(SpKey.USER_CERTNO));
                map.put("custName", SpHp.getUser(SpKey.USER_CUSTNAME));
                map.put("sceneType", "auth");
                netHelper.postService(ApiUrl.POST_SIGN_AGREEMENTS, map);
            }
        });
        ivClose.setOnClickListener(v -> {
            postGzxEvent("Gh_DiversionAgreement_Click", "关闭弹窗");
            finish();
        });

    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        showProgress(false);
        jumpNext();
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
    }

    private void jumpNext() {
        Intent intent = new Intent(GzxAgreementActivity.this, GzxTransitionActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    /**
     * 设置协议样式
     */
    public SpannableStringBuilder getAgreement(List<QueryAgreementListBean> list) {
        SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(this, "确认授权视为同意");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (i != 0) {
                    builder.append(" ").setForegroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                }
                int finalI = i;
                builder.append(list.get(i).getContName()).setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        KeyBordUntil.hideKeyBord2(GzxAgreementActivity.this);
                        Intent intent = new Intent();
                        intent.setClass(GzxAgreementActivity.this, WebPopLimitHeightActivity.class);
                        intent.putExtra("isHideCloseIcon", true);
                        intent.putExtra("showNow", true);
                        intent.putExtra("jumpKey", WebHelper.addUrlParam(ApiUrl.API_SERVER_URL + list.get(finalI).getContUrl(), null));
                        startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(ContextCompat.getColor(GzxAgreementActivity.this, R.color.colorPrimary));
                        ds.setUnderlineText(false);
                        ds.clearShadowLayer();
                    }
                });
            }
        }
        return builder.create();
    }

    @Override
    protected String getPageCode() {
        return "HomePage";
    }

    //Gzx事件
    private void postGzxEvent(String id, String buttonName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "够花-GZX推荐授权弹窗");
        map.put("button_name", buttonName);
        UMengUtil.onEventObject(id, map, getPageCode());

    }
}