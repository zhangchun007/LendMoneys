package com.haiercash.gouhua.fragments.mine;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.view.CollapsedTextView;
import com.haiercash.gouhua.x5webview.CusWebView;
import com.haiercash.gouhua.x5webview.WebDataImpl;

import butterknife.BindView;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/12/12<br/>
 * 描    述：公告详情<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_NOTICE)
public class NoticeFragment extends BaseFragment {
    @BindView(R.id.tv_notice_title)
    TextView tvNoticeTitle;

    @BindView(R.id.tv_sub_title)
    CollapsedTextView tv_sub_title;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.notice_content)
    CusWebView x5WebView;
    private WebDataImpl webImpl;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notice;
    }

    @Override
    protected void initEventAndData() {
        if (getArguments() == null) {
            mActivity.finish();
            return;
        }
        String pageTitle = getArguments().getString("title");
        mActivity.setTitle(pageTitle == null ? "消息详情" : pageTitle);
        //HomeNoticeBean bean = (HomeNoticeBean) getArguments().getSerializable("noticeInfo");
        String noticeTitle = getArguments().getString("noticeTitle");
        String pushSubTitle = getArguments().getString("pushSubTitle");
        String noticeTime = getArguments().getString("noticeTime");
        String noticeContent = getArguments().getString("noticeContent");
        tvNoticeTitle.setText(noticeTitle);
        tv_sub_title.setText(pushSubTitle);
        tvTime.setText(noticeTime);
        if (!CheckUtil.isEmpty(noticeContent)) {
            String content = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name='viewport' content='initial-scale=1, maximum-scale=1, user-scalable=no'>\n" +
                    "    <title></title>\n" +
                    "    <style type=\"text/css\">\n" +
                    "        img {\n" +
                    "            width: 100% !important;\n" +
                    "            height: auto\n" +
                    "        }\n" +
                    "       body{  word-break: break-all}" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" + noticeContent +
                    "</body>\n" +
                    "</html>";
            x5WebView.loadDataWithBaseURL(ApiUrl.API_SERVER_URL, content, "text/html", "UTF-8", null);
        }
        //new WebDataImpl(mActivity, x5WebView).initWebMethod();
        webImpl = WebDataImpl.getInstance(mActivity, x5WebView);
        x5WebView.getWebIHelper().setWebViewCreateWindow((url -> {
            //打开新的页面
            if (!CheckUtil.isEmpty(url)) {
                Intent intent = new Intent();
                intent.setClass(mActivity, JsWebBaseActivity.class);
                intent.putExtra("jumpKey", url);
                mActivity.startActivity(intent);
            }
        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (webImpl != null) {
            webImpl.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (webImpl != null) {
            webImpl.onDestroy();
            webImpl = null;
        }
        if (x5WebView != null) {
            x5WebView.destroy();
            x5WebView = null;
        }
        super.onDestroy();

    }
}
