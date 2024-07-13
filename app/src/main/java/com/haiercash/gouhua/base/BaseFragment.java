package com.haiercash.gouhua.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.bui.BaseGHFragment;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.sms.SmsTimePresenter;
import com.haiercash.gouhua.sms.SmsTimeView;
import com.haiercash.gouhua.utils.ControlDialogUtil;
import com.haiercash.gouhua.utils.UMengUtil;

import java.io.Serializable;

import io.reactivex.functions.Consumer;

/**
 * 项目名称：所有碎片的基类
 * 项目作者：胡玉君
 * 创建日期：2017/4/11 19:19.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * <p>
 * ----------------------------------------------------------------------------------------------------
 */
public abstract class BaseFragment extends BaseGHFragment implements View.OnClickListener, TextWatcher, INetResult, Serializable {
    protected BaseActivity mActivity;

    private boolean isDark = true;

    //短信验证码模块
    private SmsTimePresenter presenter;
    protected NetHelper netHelper;
    protected ControlDialogUtil controlDialogUtil;

    public BaseActivity getMActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Context context) {
        mActivity = (BaseActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        if (mActivity == null) {
            mActivity = (BaseActivity) activity;
        }
        super.onAttach(activity);
    }

    /**
     * 是否使用baseActivity处理友盟页面统计
     * 子类可以自己处理
     */
    protected boolean useBaseToUmPage() {
        return true;
    }

    /**
     * 当前页面code，友盟埋点用
     */
    protected String getPageCode() {
        return "";
    }

    @Override
    public void onResume() {
        super.onResume();
        String pageCode = getPageCode();
        if (useBaseToUmPage() && !CheckUtil.isEmpty(pageCode)) {
            UMengUtil.pageStart(pageCode);
        }
    }

    @Override
    public void onPause() {
        String pageCode = getPageCode();
        if (useBaseToUmPage() && !CheckUtil.isEmpty(pageCode)) {
            UMengUtil.pageEnd(pageCode);
        }
        super.onPause();
    }

    public void showProgress(boolean flag) {
        showProgress(flag, null);
    }

    public void showProgress(boolean flag, String msg) {
        if (mActivity != null) {
            mActivity.showProgress(flag, msg);
        }
    }


    public void setStatusBarTextColor(boolean isDark) {
        this.isDark = isDark;
        mActivity.setStatusBarTextColor(isDark);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setStatusBarTextColor(isDark);
        }
    }

    @Override
    public void onDestroyView() {
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
            netHelper = null;
        }
        super.onDestroyView();
    }

    /**
     * 权限申请优化
     */
    public void requestPermission(Consumer<Boolean> consumer, int notice, String... strings) {
        //将权限申请转给activity去处理
        mActivity.requestPermission(consumer, notice, strings);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        netHelper = new NetHelper(this);
        controlDialogUtil = new ControlDialogUtil(mActivity);
        initEventAndData();
    }

    /**
     * 统一设置界面上View的点击事件
     *
     * @param ids 所有View的id
     */
    protected void setonClickByViewId(int... ids) {
        if (ids != null) {
            for (int resId : ids) {
                mView.findViewById(resId).setOnClickListener(this);
            }
        }
    }

    /**
     * 统一设置界面上View的点击事件
     *
     * @param views 所有View
     */
    protected void setonClickByView(View... views) {
        if (views != null) {
            for (View view : views) {
                view.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    public SmsTimePresenter registerSmsTime(SmsTimeView timeView) {
        presenter = SmsTimePresenter.getSmsTime(mActivity, timeView);
        return presenter;
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.stopTime();
        }
        showProgress(false);
        if (controlDialogUtil != null) {
            controlDialogUtil.onDestroy();
        }
        super.onDestroy();
    }


    /**
     * fragment name
     */
    @Override
    public String getFragmentName() {
        TextView view = (getActivity() != null ? getActivity() : mActivity).findViewById(R.id.head_rightText);
        return view == null ? "够花" : view.getText().toString();
    }

    /**
     * 显示第二个按钮
     */
    public BaseDialog showBtn2Dialog(CharSequence msg, CharSequence btn2, DialogInterface.OnClickListener listener) {
        return mActivity.showBtn2Dialog(msg, btn2, listener);
    }

    /**
     * 两个按钮
     */
    public BaseDialog showDialog(CharSequence title, CharSequence msg, CharSequence btn1, CharSequence btn2, DialogInterface.OnClickListener listener) {
        return mActivity.showDialog(title, msg, btn1, btn2, listener);
    }

    /**
     * 两个按钮无title
     */
    public BaseDialog showDialog(CharSequence msg, CharSequence btn1, CharSequence btn2, DialogInterface.OnClickListener listener) {
        return mActivity.showDialog(msg, btn1, btn2, listener);
    }

    /**
     * 展示一个Dialog提示，每个页面使用一个AlertDialog，防止多层覆盖
     */
    public BaseDialog showDialog(String msg) {
        return mActivity.showDialog(msg);
    }

    public DialogHelper getDHelper() {
        return mActivity.dHelper;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public boolean onBackPressed() {
        return false;
    }


    @Override
    public <T> void onSuccess(T t, String url) {


    }

    public void onError(String error) {
        showProgress(false);
        showDialog(error);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        String errorMsg = error == null || error.getHead() == null ? NetConfig.DATA_PARSER_ERROR : error.getHead().getRetMsg();
        showProgress(false);
        if (!isHidden() && !mActivity.isShowingDialog()) {
            showDialog(errorMsg);
        }
    }

    /**
     * 重置fragment数据
     */
    public void resetData() {

    }
}
