package com.haiercash.gouhua.uihelper;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.ShowPrivicyActivity;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.activity.login.LoginNetHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppUntil;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.login.QueryAgreementListBean;
import com.haiercash.gouhua.databinding.PopPrivacyProtocolBinding;
import com.haiercash.gouhua.interfaces.OnPopClickListener;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/9/25<br/>
 * 描    述：R.layout.pop_privacy_protocol<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class PrivacyProtocolPopupWindow extends BasePopupWindow {
    private String flag;
    private OnPopClickListener listener;
    private List<QueryAgreementListBean> agreementListBeanList;

    public static final int BUTTON_AGREE_FLAG = 1;

    public static final int BUTTON_DISAGREE_FLAG = 0;

    private PopPrivacyProtocolBinding getBinding() {
        return (PopPrivacyProtocolBinding) _binding;
    }

    public PrivacyProtocolPopupWindow(BaseActivity activity, Object data) {
        super(activity, data);
    }

    @Override
    protected PopPrivacyProtocolBinding initBinding(LayoutInflater inflater) {
        return PopPrivacyProtocolBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(Object data) {
        agreementListBeanList = (List<QueryAgreementListBean>) data;
        if (CheckUtil.isEmpty(agreementListBeanList)) {
            agreementListBeanList = LoginNetHelper.getDefaultAgreementList();
        }
        agreementListBeanList = filterAgreement(agreementListBeanList);
        setContent();
        getBinding().tvCancel.setVisibility(View.VISIBLE);
        getBinding().tvClose.setVisibility(View.GONE);

        getBinding().tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        getBinding().tvContent.setHighlightColor(Color.TRANSPARENT);
        getBinding().tvNext.setOnClickListener(this);
        getBinding().tvClose.setOnClickListener(this);
        getBinding().tvCancel.setOnClickListener(this);
    }

    private List<QueryAgreementListBean> filterAgreement(List<QueryAgreementListBean> agreementList) {
        CopyOnWriteArrayList<QueryAgreementListBean> queryAgreementListBeans = new CopyOnWriteArrayList<>(agreementList);
        for (QueryAgreementListBean queryAgreementListBean : queryAgreementListBeans) {
            if (!"PrivacyAgreement".equals(queryAgreementListBean.getContType())) {
                queryAgreementListBeans.remove(queryAgreementListBean);
            }
        }
        return queryAgreementListBeans;
    }

    private void setContent() {
        SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(mActivity, "感谢您使用够花APP！我们非常重视您的个人信息和隐私保护，请您在使用够花APP前认真阅读");
        if (agreementListBeanList != null) {
            for (int i = 0; i < agreementListBeanList.size(); i++) {
                if (i != 0) {
                    builder.append("，").setForegroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                }
                int finalI = i;
                builder.append(agreementListBeanList.get(i).getContName())
                        .setClickSpan(new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                KeyBordUntil.hideKeyBord2(mActivity);
                                ShowPrivicyActivity.skip(mActivity, ApiUrl.API_SERVER_URL + agreementListBeanList.get(finalI).getContUrl(), agreementListBeanList.get(finalI).getContName());
                            }

                            @Override
                            public void updateDrawState(@NonNull TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                                ds.setUnderlineText(false);
                                ds.clearShadowLayer();
                            }
                        });
            }
        }
        builder.append("我们将严格按照经您同意的条款使用您的个人信息，以便为您提供更好的服务。");

        getBinding().tvPopTitle.setText("用户隐私政策");
        getBinding().tvContentDes.setVisibility(View.VISIBLE);
        getBinding().tvContent.setVisibility(View.VISIBLE);
        getBinding().sllv.setVisibility(View.GONE);
        getBinding().tvNext.setText("同意并继续");
        getBinding().tvContentDes.setText("如果您同意此条款，请点击“同意并继续”并开始使用我们的产品和服务，我们将尽全力保护您的个人信息安全。");
        getBinding().tvContent.setText(builder.create());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvNext) {
            if (CheckUtil.isEmpty(flag)) {
                if ("查看协议".equals(getBinding().tvNext.getText().toString())) {
                    setContent();
                    getBinding().tvNext.setText("同意并继续");
                    getBinding().tvCancel.setText("不同意");
                    return;
                }
                if (listener != null) {
                    listener.onViewClick(view, BUTTON_AGREE_FLAG, null);
                } else {
                    UiUtil.toast("签订协议失败，请稍后重试！");
                    ActivityUntil.finishOthersActivityByPageKey();
                }
            }
        } else if (view.getId() == R.id.tvCancel) {
            if ("不同意".equals(getBinding().tvCancel.getText().toString())) {
                setRetainContent("同意", "暂不同意，先看看");
            } else {
                //ActivityUntil.finishOthersActivityByPageKey();
                listener.onViewClick(view, BUTTON_DISAGREE_FLAG, null);
                dismiss();
            }
            return;
        }
        dismiss();
    }

    public void setRetainContent(String button1, String button2) {
        getBinding().tvPopTitle.setText("够花需要您同意隐私政策");

        SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(mActivity, "除非取得您的同意或符合其他法律法规规定之情形，否则我们不会获取和使用您的任何信息。\n" +
                "如您不同意");
        if (agreementListBeanList != null) {
            for (int i = 0; i < agreementListBeanList.size(); i++) {
                if (i != 0) {
                    builder.append("，").setForegroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                }
                int finalI = i;
                builder.append(agreementListBeanList.get(i).getContName())
                        .setClickSpan(new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                KeyBordUntil.hideKeyBord2(mActivity);
                                ShowPrivicyActivity.skip(mActivity, ApiUrl.API_SERVER_URL + agreementListBeanList.get(finalI).getContUrl(), agreementListBeanList.get(finalI).getContName());
                            }

                            @Override
                            public void updateDrawState(@NonNull TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                                ds.setUnderlineText(false);
                                ds.clearShadowLayer();
                            }
                        });
            }
        }
        builder.append("很遗憾我们将无法为您提供服务，您可先进入浏览模式进行相关产品浏览及客服咨询。\n" +
                "如您同意此条款，请点击下方同意按钮开启我们的产品和服务，我们将全力保护您的个人信息安全。");

        getBinding().tvContent.setText(builder.create());
        getBinding().tvContentDes.setVisibility(View.GONE);
        getBinding().tvNext.setText(button1);
        getBinding().tvCancel.setText(button2);
    }

    public void showAtLocation(View view, OnPopClickListener listener) {
        this.listener = listener;
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    public class ClickableSpanListener extends ClickableSpan {
        private String linkUrl;
        private String linkTitle;

        ClickableSpanListener(String linkTitle, String linkUrl) {
            super();
            this.linkTitle = linkTitle;
            this.linkUrl = linkUrl;
        }

        @Override
        public void onClick(@NonNull View widget) {
            WebSimpleFragment.WebService(mActivity, linkUrl, linkTitle, WebSimpleFragment.STYLE_OTHERS);
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(UiUtil.getColor(R.color.colorPrimary));
            ds.setUnderlineText(false);
            ds.clearShadowLayer();
        }
    }
}
