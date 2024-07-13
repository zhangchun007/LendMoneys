package com.haiercash.gouhua.fragments.mine;

import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SoftKeyBoardListenerUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.accountsettings.AppealActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.databinding.FragmentAppealRealNameBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 账号申诉-实名认证
 */
public class AppealRealNameFragment extends BaseFragment implements View.OnFocusChangeListener {
    private FragmentAppealRealNameBinding appealRealNameBinding;
    private boolean mNameError, mCertError;//输入框下方是否显示错误文案

    @Override
    protected ViewBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return appealRealNameBinding = FragmentAppealRealNameBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initEventAndData() {
        appealRealNameBinding.tvRealName.setTypeface(FontCustom.getMediumFont(mActivity));
        appealRealNameBinding.tvBankCard.setTypeface(FontCustom.getMediumFont(mActivity));
        appealRealNameBinding.etRealName.setOnFocusChangeListener(this);
        appealRealNameBinding.etCert.setOnFocusChangeListener(this);
        appealRealNameBinding.etRealName.addTextChangedListener(this);
        appealRealNameBinding.etCert.addTextChangedListener(this);
        appealRealNameBinding.tvNext.setOnClickListener(this);
        if(mActivity instanceof AppealActivity){
            ((AppealActivity) mActivity).addTouchOutOfViewList(appealRealNameBinding.etRealName,appealRealNameBinding.etCert);
        }
        SoftKeyBoardListenerUtil.setListener(mActivity, new SoftKeyBoardListenerUtil.MySoftKeyboardListener(appealRealNameBinding.etRealName));
        SoftKeyBoardListenerUtil.setListener(mActivity, new SoftKeyBoardListenerUtil.MySoftKeyboardListener(appealRealNameBinding.etCert));
    }

    @Override
    public void onClick(View v) {
        if (v == appealRealNameBinding.tvNext) {
            showProgress(true);
            String name = appealRealNameBinding.etRealName.getText() != null ? appealRealNameBinding.etRealName.getText().toString() : "";
            String cert = appealRealNameBinding.etCert.getText() != null ? appealRealNameBinding.etCert.getText().toString() : "";
            HashMap<String, String> map = new HashMap<>();
            map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
            map.put("mobile", SpHp.getLogin(SpKey.LOGIN_MOBILE));
            map.put("custName", name);
            map.put("certNo", cert);
            netHelper.postService(ApiUrl.POST_APPEAL_CERT_INFO_CHECK, map);
        } else {
            super.onClick(v);
        }
    }

    private void setBtnStatus() {
        String name = appealRealNameBinding.etRealName.getText() != null ? appealRealNameBinding.etRealName.getText().toString() : "";
        String cert = appealRealNameBinding.etCert.getText() != null ? appealRealNameBinding.etCert.getText().toString() : "";
        //为空时不显示错误文案
        boolean isEmpty = TextUtils.isEmpty(name) || TextUtils.isEmpty(cert);
        mNameError = !TextUtils.isEmpty(name) && !CheckUtil.isLegalName(name);
        mCertError = !TextUtils.isEmpty(cert) && !CheckUtil.checkIdNumber(cert);
        appealRealNameBinding.tvNext.setEnabled(!isEmpty && !mNameError && !mCertError);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {
            if (v == appealRealNameBinding.etRealName) {
                if (hasFocus) {
                    appealRealNameBinding.lineRealName.setBackgroundResource(R.color.color_606166);
                    appealRealNameBinding.tvNameError.setVisibility(View.INVISIBLE);
                } else {
                    appealRealNameBinding.lineRealName.setBackgroundResource(mNameError ? R.color.color_ff5151 : R.color.color_e8eaef);
                    appealRealNameBinding.tvNameError.setVisibility(mNameError ? View.VISIBLE : View.INVISIBLE);
                }
            } else if (v == appealRealNameBinding.etCert) {
                if (hasFocus) {
                    appealRealNameBinding.lineCert.setBackgroundResource(R.color.color_606166);
                    appealRealNameBinding.tvCertError.setVisibility(View.INVISIBLE);
                } else {
                    appealRealNameBinding.lineCert.setBackgroundResource(mCertError ? R.color.color_ff5151 : R.color.color_e8eaef);
                    appealRealNameBinding.tvCertError.setVisibility(mCertError ? View.VISIBLE : View.INVISIBLE);
                }
            }
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        super.afterTextChanged(s);
        setBtnStatus();
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        showProgress(false);
        if (ApiUrl.POST_APPEAL_CERT_INFO_CHECK.equals(url)) {
            Map<String, String> map = JsonUtils.getRequest(t);
            String errorCode = map.containsKey("errorCode") ? map.get("errorCode") : "";
            String errorMsg = map.containsKey("errorMsg") ? map.get("errorMsg") : "";
            if (TextUtils.isEmpty(errorMsg)) {
                if (mActivity instanceof AppealActivity) {
                    String name = appealRealNameBinding.etRealName.getText() != null ? appealRealNameBinding.etRealName.getText().toString() : "";
                    String cert = appealRealNameBinding.etCert.getText() != null ? appealRealNameBinding.etCert.getText().toString() : "";
                    ((AppealActivity) mActivity).setRealNameInfo(name, cert);
                    ((AppealActivity) mActivity).setFragment(new AppealBankCardFragment());
                }
            } else if ("02".equals(errorCode)) {
                showDialog(errorMsg);
            } else {
                UiUtil.toast(errorMsg);
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        String errorMsg = error == null || error.getHead() == null ? NetConfig.DATA_PARSER_ERROR : error.getHead().getRetMsg();
        showProgress(false);
        if (!isHidden()) {
            UiUtil.toast(errorMsg);
        }
    }

    @Override
    public void onDestroyView() {
        if(mActivity instanceof AppealActivity){
            ((AppealActivity) mActivity).addTouchOutOfViewList();
        }
        super.onDestroyView();
    }
}
