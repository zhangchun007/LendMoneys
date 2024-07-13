package com.haiercash.gouhua.fragments.mine;

import android.content.Intent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.bankcard.MyCreditCardActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.DischargeUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;

@Route(path = PagePath.FRAGMENT_TOOLS)
public class ToolsFragment extends BaseFragment {


    public ToolsFragment() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tools;
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle("工具服务");
    }

    @OnClick({R.id.tv_bank, R.id.tv_certificate,
            R.id.tv_account_security, R.id.tv_loan_calculator})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_bank:
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        startActivity(new Intent(mActivity, MyCreditCardActivity.class));
                    }
                });
                break;
            case R.id.tv_certificate:
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        if (CommomUtils.isRealName()) {
                            DischargeUtil util = new DischargeUtil(mActivity);
                            util.getDischarge();
                        } else {
                            showBtn2Dialog("您在过去24个月内暂无结清账单，如需开具24个月之前账单的结清证明,请联系客服人员。", "我知道了", ((dialog, which) -> dialog.dismiss()));
                        }
                    }
                });
                break;
            case R.id.tv_account_security:
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        ARouterUntil.getInstance(PagePath.ACTIVITY_SAFETY_SETTING).navigation(mActivity, 1);
                    }
                });
                break;
            case R.id.tv_loan_calculator:
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_CALCULATION).put("isShowTitle", false).navigation();
                break;
            case R.id.tv_feedback://我要留言
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        showProgress(true);
                        Map<String, String> map = new HashMap<>();
                        if (!CheckUtil.isEmpty(AppApplication.userid)) {
                            map.put("userId", EncryptUtil.simpleEncrypt(AppApplication.userid));
                        }
                        netHelper.getService(ApiUrl.URL_IS_FEEDBACK_ALLOW, map);
                    }
                });
                break;
        }
    }

}