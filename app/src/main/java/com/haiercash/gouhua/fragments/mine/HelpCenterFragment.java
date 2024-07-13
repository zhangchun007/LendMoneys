package com.haiercash.gouhua.fragments.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.HelperCenterAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.help.HelpCenterBean;
import com.haiercash.gouhua.beans.help.HelpCenterProblemBean;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.CallPhoneNumberHelper;
import com.haiercash.gouhua.uihelper.HelpCenterHeadHelper;
import com.haiercash.gouhua.utils.AIServer;
import com.haiercash.gouhua.widget.CirclePageIndicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：yuelibiao
 * 邮    箱：yuelibiao@haiercash.com
 * 版    本：1.0
 * 创建日期：2017/7/6
 * 描    述：帮助中心 列表
 * 修订历史：1、stone修改，添加模拟数据、列表item的样式，
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_HELPER_CENTER)
public class HelpCenterFragment extends BaseFragment {
    @BindView(R.id.rv_help_head)
    ViewPager rvHelpHead;
    @BindView(R.id.indicator)
    CirclePageIndicator indicator;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.rv_help_head100)
    ViewPager rv_help_head100;
    @BindView(R.id.iv_empty_view)
    TextView iv_empty_view;
    @BindView(R.id.sv)
    NestedScrollView sv;
    @BindView(R.id.view_divder_type)
    View view_divder_type;
    public static final String NAME = "帮助中心";
    private HelpCenterHeadHelper helpCenterHeadHelper;
    private HelperCenterAdapter helperCenterAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_helpcenter;
    }


    @Override
    protected void initEventAndData() {
        mActivity.setTitle(NAME);
        rvList.setLayoutManager(new LinearLayoutManager(mActivity));
        rvList.setNestedScrollingEnabled(false);
        helpCenterHeadHelper = new HelpCenterHeadHelper(getLayoutInflater(), mActivity);
        helperCenterAdapter = new HelperCenterAdapter();
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        netHelper.postService(ApiUrl.commonProblem, map);
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (ApiUrl.commonProblem.equals(flag)) {
            List<HelpCenterBean> data = JsonUtils.fromJsonArray(response, HelpCenterBean.class);
            if (data.size() > 0) {
                Map<String, String> map = new HashMap<>();
                netHelper.postService(ApiUrl.allQuestionType, map);
                initAdapter(data);
            } else {
                showProgress(false);
                rvList.setVisibility(View.GONE);
                iv_empty_view.setVisibility(View.VISIBLE);
            }
        } else if (ApiUrl.allQuestionType.equals(flag)) {
            showProgress(false);
            List<HelpCenterProblemBean> data = JsonUtils.fromJsonArray(response, HelpCenterProblemBean.class);
            if (data.size() > 0) {
                if (data.size() > 4) {
                    rv_help_head100.setVisibility(View.GONE);
                    rvHelpHead.setVisibility(View.VISIBLE);
                    helpCenterHeadHelper.initHeadView(data, rvHelpHead, indicator);
                } else {
                    rv_help_head100.setVisibility(View.VISIBLE);
                    rvHelpHead.setVisibility(View.GONE);
                    helpCenterHeadHelper.initHeadView(data, rv_help_head100, indicator);
                }

            } else {
                rv_help_head100.setVisibility(View.GONE);
                rvHelpHead.setVisibility(View.GONE);
                view_divder_type.setVisibility(View.GONE);
            }
        }
    }

    private void initAdapter(List<HelpCenterBean> data) {
        helperCenterAdapter.setNewData(data);
        rvList.setAdapter(helperCenterAdapter);
        helperCenterAdapter.setOnItemClickListener((adapter, view, position) -> {
            //跳转帮助中心详情
            HelpCenterBean bean = helperCenterAdapter.getData().get(position);
            if (bean == null) {
                return;
            }
            if (CheckUtil.isEmpty(bean.getProblemContent()) || CheckUtil.isEmpty(bean.getProblemTitle())) {
                return;
            }
            Bundle bd = new Bundle();
            bd.putString("helpContent", bean.getProblemContent());
            bd.putString("helpTitle", bean.getProblemTitle());
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER_DETAIL).put(bd).navigation();
        });
    }

    @OnClick({R.id.ll_online_server, R.id.ll_phone_server})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_online_server:
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        AIServer.showAiWebServer(mActivity, "帮助中心");
                    }
                });
                break;
            case R.id.ll_phone_server:
                CallPhoneNumberHelper.callServiceNumber(mActivity, getString(R.string.about_us_phone_number),
                        "呼叫", "取消");
                break;
            default:
                break;
        }
    }
}
