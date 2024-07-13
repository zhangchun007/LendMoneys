package com.haiercash.gouhua.hybrid;

import android.content.Intent;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.fragments.main.MainEduBorrowUntil;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;

public class H5LinkJumpHelper {
    private H5LinkJumpHelper() {
    }

    private static H5LinkJumpHelper helper;

    public static H5LinkJumpHelper INSTANCE() {
        if (helper == null) {
            helper = new H5LinkJumpHelper();
        }
        return helper;
    }

    //默认不是从首页卡片进去的
    public void goH5RepayPage(BaseActivity mActivity) {
        goH5RepayPage(mActivity, false);
    }

    public void goH5RepayPage(BaseActivity mActivity, boolean isHomeCard) {
        if (isHomeCard && !CheckUtil.isEmpty(H5ConfigHelper.h5RepayJumpUrlY)) {
            goH5Page(mActivity, H5ConfigHelper.h5RepayJumpUrlY);
        } else if (!isHomeCard && !CheckUtil.isEmpty(H5ConfigHelper.h5RepayJumpUrl)) {
            goH5Page(mActivity, H5ConfigHelper.h5RepayJumpUrl);
        } else {
            mActivity.showProgress(true);
            H5ConfigHelper helper = new H5ConfigHelper(MainEduBorrowUntil.oldStatus, "", new H5ConfigHelper.IConfigCallback() {
                @Override
                public void configSuccess() {
                    mActivity.showProgress(false);
                    if (isHomeCard && !CheckUtil.isEmpty(H5ConfigHelper.h5RepayJumpUrlY)) {
                        goH5Page(mActivity, H5ConfigHelper.h5RepayJumpUrlY);
                    } else if (!isHomeCard && !CheckUtil.isEmpty(H5ConfigHelper.h5RepayJumpUrl)) {
                        goH5Page(mActivity, H5ConfigHelper.h5RepayJumpUrl);
                    } else {
                        configFail();
                    }
                }

                //再次调用失败的话走原生
                @Override
                public void configFail() {
                    mActivity.showProgress(false);
                    mActivity.showDialog("网络异常,请重启APP~");
                    /*if (isHomeCard) {
                        ContainerActivity.to(mActivity, PeriodBillsFragment.ID, null);
                    } else {
                        ContainerActivity.to(mActivity, AllBillsFragment.ID);
                    }*/
                }
            });
            helper.getH5LinkData();
        }
    }

    public void goLoanPage(BaseActivity mActivity) {
        if (!CheckUtil.isEmpty(H5ConfigHelper.h5SceneCreditUrl)) {
            goH5Page(mActivity, H5ConfigHelper.h5SceneCreditUrl);
        } else if (!CheckUtil.isEmpty(H5ConfigHelper.h5CreditUrl)) {
            goH5Page(mActivity, H5ConfigHelper.h5CreditUrl);
        } else {
            mActivity.showProgress(true);
            H5ConfigHelper helper = new H5ConfigHelper(MainEduBorrowUntil.oldStatus, "", new H5ConfigHelper.IConfigCallback() {
                @Override
                public void configSuccess() {
                    mActivity.showProgress(false);
                    if (!CheckUtil.isEmpty(H5ConfigHelper.h5SceneCreditUrl)) {
                        goH5Page(mActivity, H5ConfigHelper.h5SceneCreditUrl);
                    } else if (!CheckUtil.isEmpty(H5ConfigHelper.h5CreditUrl)) {
                        goH5Page(mActivity, H5ConfigHelper.h5CreditUrl);
                    } else {
                        configFail();
                    }
                }

                //再次调用失败的话走原生
                @Override
                public void configFail() {
                    mActivity.showProgress(false);
                    mActivity.showDialog("网络异常，请稍后重试");
                }
            });
            helper.getH5LinkData();
        }
    }

    //跳转到h5页面
    private void goH5Page(BaseActivity mActivity, String url) {
        mActivity.showProgress(false);
        Intent intent = new Intent(mActivity, JsWebBaseActivity.class);
        intent.putExtra("jumpKey", url);
        intent.putExtra("isHideCloseIcon", true);
        mActivity.startActivity(intent);
    }
}
