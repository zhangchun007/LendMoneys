package com.haiercash.gouhua.uihelper;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.login.QueryAgreementListBean;
import com.haiercash.gouhua.databinding.PopUpdateProtocolBinding;
import com.haiercash.gouhua.interfaces.OnPopClickListener;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.List;

/**
 * 协议更新弹窗
 */
public class UpdateProtocolPopupWindow extends BasePopupWindow implements INetResult {
    private OnPopClickListener listener;
    private NetHelper netHelper;
    private final View demoView;
    private List<QueryAgreementListBean> agreementList;

    private PopUpdateProtocolBinding getBinding() {
        return (PopUpdateProtocolBinding) _binding;
    }

    public UpdateProtocolPopupWindow(BaseActivity activity, View demoView) {
        super(activity, null);
        netHelper = new NetHelper(activity, this);
        this.demoView = demoView;
    }

    @Override
    protected PopUpdateProtocolBinding initBinding(LayoutInflater inflater) {
        return PopUpdateProtocolBinding.inflate(inflater);
    }

    @Override
    public void onDestroy() {
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
            netHelper = null;
        }
        super.onDestroy();
    }

    public void queryNeedResignAgreements(OnPopClickListener listener) {
        this.listener = listener;
        //登录且实名用户才需要查询是否有需要重签协议
        if (AppApplication.isLogIn() && !CheckUtil.isEmpty(SpHp.getUser(SpKey.USER_CUSTNO))) {
            //showProgress(true, "");
            HashMap<String, String> map = new HashMap<>();
            map.put("idNo", SpHp.getUser(SpKey.USER_CERTNO));
            map.put("custName", SpHp.getUser(SpKey.USER_CUSTNAME));
            map.put("sceneType", "app_start_popup_check");
            netHelper.postService(ApiUrl.URL_GET_QUERY_AGREEMENT_LIST, map);
        } else {
            callbackAction();
        }
    }

    @Override
    protected void onViewCreated(Object data) {
        getBinding().tvPopTitle.setTypeface(FontCustom.getMediumFont(mContext));
        getBinding().tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        getBinding().tvContent.setHighlightColor(Color.TRANSPARENT);
        getBinding().tvNext.setOnClickListener(this);
        getBinding().tvCancel.setOnClickListener(this);
//        initView(agreementList);
    }

    private void initView(List<QueryAgreementListBean> agreementList) {
        if (agreementList == null || agreementList.isEmpty()) return;
        getBinding().tvPopTitle.setText("协议更新提醒");
        getBinding().tvNext.setText("同意并继续");
        getBinding().tvContentDes.setText("如果您同意此条款，请点击“同意并继续”并开始使用我们的产品和服务，我们将尽全力保护您的个人信息安全。");
        getBinding().tvContentDes.setVisibility(View.GONE);
        SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(mActivity, "尊敬的用户您好，为了方便对您提供更好的服务，海尔消费金融对");
        String url = "";
        for (int i = 0; i < agreementList.size(); i++) {
            if (!TextUtils.isEmpty(agreementList.get(i).getContUrl()) && agreementList.get(i).getContUrl().startsWith("http")) {
                url = agreementList.get(i).getContUrl();
            }else {
                url = ApiUrl.API_SERVER_URL + agreementList.get(i).getContUrl();
            }
            builder.append(agreementList.get(i).getContName()).setClickSpan(new ClickableSpanListener(agreementList.get(i).getContName(), url));
            if (i < agreementList.size() - 1) {
                builder.append("、");
            }
        }
        SpannableStringBuilder spannableStringBuilder = builder.append("进行更新，请您认真阅读，确认相关内容。").create();
        getBinding().tvContent.setText(spannableStringBuilder);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvNext) {
            if ("查看协议".equals(getBinding().tvNext.getText().toString())) {
                initView(agreementList);
                return;
            } else {
                showProgress(true, "");
                HashMap<String, String> map = new HashMap<>();
                map.put("idNo", SpHp.getUser(SpKey.USER_CERTNO));
                map.put("custName", SpHp.getUser(SpKey.USER_CUSTNAME));
                map.put("sceneType", "app_start_popup_check");
//                List<String> list = new ArrayList<>();
//                if (!CheckUtil.isEmpty(agreementList)) {
//                    for (QueryAgreementListBean agreement : agreementList) {
//                        if (agreement != null) {
//                            list.add(agreement.getContType());
//                        }
//                    }
//                }
//                map.put("signTypeList", JsonUtils.toJson(list));
                netHelper.postService(ApiUrl.POST_SIGN_AGREEMENTS, map);
            }
        } else if (view.getId() == R.id.tvCancel) {
            if ("无情拒绝".equals(getBinding().tvCancel.getText().toString())) {
                getBinding().tvPopTitle.setText("您需要同意本协议才能继续使用够花");
                getBinding().tvContent.setText("若您选择不同意，很遗憾我们将无法为您提供服务。");
                getBinding().tvContentDes.setVisibility(View.GONE);
                getBinding().tvNext.setText("查看协议");
                getBinding().tvCancel.setText("不同意并退出应用");
            } else {
                ActivityUntil.finishOthersActivityByPageKey();
            }
            return;
        }
        dismiss();
    }

    private void callbackAction() {
        if (listener != null) {
            listener.onViewClick(null, 0, null);
        }
    }

    @Override
    public void showAtLocation(View view) {
        if (view != null) {
            showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public <T> void onSuccess(T success, String url) {
        showProgress(false, "");
        if (ApiUrl.URL_GET_QUERY_AGREEMENT_LIST.equals(url)) {
            agreementList = JsonUtils.fromJsonArray(success, QueryAgreementListBean.class);
            initView(agreementList);
            if (!CheckUtil.isEmpty(agreementList)) {
                showAtLocation(demoView);
            } else {
                callbackAction();
            }
        } else if (ApiUrl.POST_SIGN_AGREEMENTS.equals(url)) {
            callbackAction();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        showProgress(false, "");
        if (ApiUrl.URL_GET_QUERY_AGREEMENT_LIST.equals(url)) {
            callbackAction();
        } else if (ApiUrl.POST_SIGN_AGREEMENTS.equals(url)) {
            UiUtil.toast(mContext, "服务器开小差了，请稍后再试");
        }
    }

    public class ClickableSpanListener extends ClickableSpan {
        private final String linkUrl;
        private final String linkTitle;

        ClickableSpanListener(String linkTitle, String linkUrl) {
            super();
            this.linkTitle = linkTitle;
            this.linkUrl = linkUrl;
        }

        @Override
        public void onClick(@NonNull View widget) {
            WebSimpleFragment.WebService(mActivity, linkUrl, linkTitle, WebSimpleFragment.STYLE_COMPLEX3);
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