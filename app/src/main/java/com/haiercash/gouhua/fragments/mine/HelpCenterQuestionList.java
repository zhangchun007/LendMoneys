package com.haiercash.gouhua.fragments.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.AIServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 单个问题类型的常见问题
 */
@Route(path = PagePath.FRAGMENT_HELPER_CENTER_QUESTIONLIST)
public class HelpCenterQuestionList extends BaseFragment {
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.tvHelpOK)
    TextView tvHelpOK;
    @BindView(R.id.ll_error)
    TextView ll_error;
    private String mHelpId;
    private String mHelpTitle;
    private HelperCenterAdapter helperCenterAdapter;
    private List<HelpCenterBean> data;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_help_question_list;
    }

    @Override
    protected void initEventAndData() {
        rvList.setLayoutManager(new LinearLayoutManager(mActivity));
        rvList.setNestedScrollingEnabled(false);
        if (getArguments() != null) {
            mHelpId = getArguments().getString("problemTypeId");
            mHelpTitle = getArguments().getString("problemTypeTitle");
            mActivity.setTitle(CheckUtil.isEmpty(mHelpTitle) ? "额度申请" : mHelpTitle);
        }
        initData();
    }

    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("problemTypeId", mHelpId);
        netHelper.postService(ApiUrl.allProblemByProblemType, map);
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (response == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        if (ApiUrl.allProblemByProblemType.equals(flag)) {
            data = JsonUtils.fromJsonArray(response, HelpCenterBean.class);
            if (data.size() > 0) {
                ll_error.setVisibility(View.GONE);
                rvList.setVisibility(View.VISIBLE);
                tvHelpOK.setVisibility(View.VISIBLE);
                initRecclerview();
            } else {
                ll_error.setVisibility(View.VISIBLE);
                rvList.setVisibility(View.GONE);
                tvHelpOK.setVisibility(View.GONE);
            }
        }
    }

    private void initRecclerview() {
        helperCenterAdapter = new HelperCenterAdapter();
        helperCenterAdapter.setNewData(data);
        rvList.setAdapter(helperCenterAdapter);
        helperCenterAdapter.setOnItemClickListener((adapter, view, position) -> {
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
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER_DETAIL)
                    .put(bd).navigation();
        });
    }

    @OnClick(R.id.tvHelpOK)
    public void onViewClicked() {
        AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
            @Override
            public void onLoginSuccess() {
                AIServer.showAiWebServer(mActivity, CheckUtil.isEmpty(mHelpTitle) ? "额度申请" : mHelpTitle);
            }
        });
    }
}
