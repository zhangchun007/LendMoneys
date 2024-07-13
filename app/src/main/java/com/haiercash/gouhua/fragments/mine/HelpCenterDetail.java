package com.haiercash.gouhua.fragments.mine;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.x5webview.CusWebView;

import butterknife.BindView;

/**
 * ================================================================
 * 作    者：yuelibiao
 * 邮    箱：yuelibiao@haiercash.com
 * 版    本：1.0
 * 创建日期：2017/7/6
 * 描    述：帮助中心 详情
 * 修订历史：1、stone修改，界面样式，去除第二个title
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_HELPER_CENTER_DETAIL)
public class HelpCenterDetail extends BaseFragment {
    private String mHelpTitle;
    @BindView(R.id.help_content)
    CusWebView mContent;


    @Override
    protected int getLayoutId() {
        return R.layout.a_helpcenter_detail;
    }

    @Override
    protected void initEventAndData() {
        if (getArguments() == null) {
            UiUtil.toast("获取参数失败");
            return;
        }
        String helpContent = getArguments().getString("helpContent");
        mHelpTitle = getArguments().getString("helpTitle");
        mActivity.setTitle("帮助详情");
        //requestHelpDetail();
        //修改图片成为自适应
        if (!CheckUtil.isEmpty(helpContent) && helpContent.contains("style='max-width: 100%;'")) {
            helpContent = helpContent.replace("style='max-width: 100%;'", "style='display:;max-width:100%;'");
        }
        if (mContent != null) {
            //mContent.loadDataWithBaseURL(ApiUrl.API_SERVER_URL,"<!DOCTYPE html><html><head><meta charset='utf-8'><meta name='viewport' content='initial-scale=1, maximum-scale=1, user-scalable=no'></head><body>"+helpContent+"</body></html>","text/html", "UTF-8", null);
            mContent.loadDataWithBaseURL(ApiUrl.API_SERVER_URL, helpContent, "text/html", "UTF-8", null);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 网络请求
     */
    /*帮助详情*/
 /*   private void requestHelpDetail() {
        HashMap<String, String> map = new HashMap<>();
        netHelper.getService(ApiUrl.urlHelpContent + mHelpId, map, HelpContentBean.class);
    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        if (success == null) {
            onError(ApiUrl.DATA_PARSER_ERROR);
            return;
        }
        if ((ApiUrl.urlHelpContent + mHelpId).equals(url)) {
            HelpContentBean helpContentBean = (HelpContentBean) success;
            String html = helpContentBean.getHelpContent();
            //修改图片成为自适应
            if (html.contains("style='max-width: 100%;'")) {
                html = html.replace("style='max-width: 100%;'", "style='display:;max-width:100%;'");
            }
            if (mContent != null) {
                mContent.loadDataWithBaseURL(ApiUrl.API_SERVER_URL, html, "text/html", "UTF-8", null);
            }
        }
    }*/
    @Override
    public void onDestroy() {
        if (mContent != null) {
            mContent.destroy();
            mContent = null;
        }
        super.onDestroy();
    }

}
