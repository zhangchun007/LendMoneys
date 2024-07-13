package com.haiercash.gouhua.activity.accountsettings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.account.AccountDetailIndoBean;
import com.haiercash.gouhua.databinding.ActivityOtherBindBinding;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;

/**
 * 第三方账号绑定
 */
public class OtherBindActivity extends BaseActivity {
    private ActivityOtherBindBinding mOtherBindBinding;
    private boolean wxBind;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return mOtherBindBinding = ActivityOtherBindBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.other_bind_title);
        mOtherBindBinding.llWxBind.setOnClickListener(this);

        netHelper.postService(ApiUrl.URL_ACCOUNT_DETAIL_INFO, new HashMap<>(), AccountDetailIndoBean.class);
    }

    @Override
    public void onClick(View v) {
        if (v == mOtherBindBinding.llWxBind) {
            if (wxBind) {
                showDialog(getString(R.string.other_bind_wx_dialog_title), getString(R.string.other_bind_wx_dialog_content), getString(R.string.other_bind_wx_dialog_cancel),
                        getString(R.string.other_bind_wx_dialog_release_bind), (dialog, which) -> {
                            if (which == 2) {
                                netHelper.postService(ApiUrl.URL_UNBIND_WX, new HashMap<>());
                            }
                        }).setStandardStyle(3);
            } else {
                showDialog(getString(R.string.other_bind_wx_dialog_bind_content));
            }
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.URL_ACCOUNT_DETAIL_INFO.equals(url)) {
            if (success instanceof AccountDetailIndoBean) {
                wxBind = "Y".equals(((AccountDetailIndoBean) success).getWechatBindState());
                setWxBindStatus();
                showProgress(false);
            } else {
                onError(new BasicResponse("-1", NetConfig.DATA_PARSER_ERROR), url);
            }
        } else if (ApiUrl.URL_UNBIND_WX.equals(url)) {
            wxBind = false;
            showProgress(false);
            UiUtil.toast(getString(R.string.other_bind_wx_release_bind_success));
            setWxBindStatus();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.URL_ACCOUNT_DETAIL_INFO.equals(url)) {
            showProgress(false);
            showDialog(getString(R.string.notice), NetConfig.DATA_PARSER_ERROR, (dialog, which) -> finish());
        } else {
            super.onError(error, url);
        }
    }

    private void setWxBindStatus() {
        try {
            mOtherBindBinding.tvWxBindStatus.setText(wxBind ? R.string.other_bind_has_bind : R.string.other_bind_not_bind);
        } catch (Exception e) {
            //
        }
    }
}
