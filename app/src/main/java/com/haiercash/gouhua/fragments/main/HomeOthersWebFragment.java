package com.haiercash.gouhua.fragments.main;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.haiercash.gouhua.tplibrary.PagePath;

/**
 * Author: Sun
 * Date :    2018/12/17
 * FileName: HomeOthersWebFragment
 * Description:
 */
@Route(path = PagePath.FRAGMENT_HOME_OTHER_WEB)
public class HomeOthersWebFragment extends BaseHomeWebFragment {
    private static final String TAG_URL = "url";

    @Override
    protected void initEventAndData() {
        super.initEventAndData();
        getBinding().swipeRefreshLayout.setEnabled(false);
        mCurrentUrl = getArguments() == null ? "" : getArguments().getString(TAG_URL);
        getBinding().wvcView.getWebIHelper().setWebViewPageStarted((url) -> {
            System.out.println("HomeOthersWebFragment->WebViewPageStarted->当前的URL=" + url);
            //getBinding().wvcView.loadUrl("javascript:" + WebDataImpl.Javascript);
            setStyle(url);
        });
        getBinding().wvcView.getWebIHelper().setWebViewPageFinished((url) -> {
            setStyle(url);
            setTitle(getBinding().wvcView.getTitle());
            //getBinding().wvcView.loadUrl("javascript:window.handler.callBackHtml(d0ocument.body.innerHTML);");
            //getBinding().wvcView.loadUrl("javascript:" + WebDataImpl.Javascript);

//            getBinding().wvcView.loadUrl("javascript:if(window.gouhua){alert('存在gouhua')}else{alert('不存在gouhua')}" +
//                    "window.gouhua.onJsFunctionCalled(document.getElementsByTagName('html')[0].innerHTML);");

            //getBinding().wvcView.loadUrl("javascript:window.gouhua.onJsFunctionCalled(document.getElementsByTagName('html')[0].innerHTML);");
        });
//        getBinding().wvcView.addJavascriptInterface(new WebInterfaceHelper.WebViewJavaScriptFunction() {
//            @JavascriptInterface
//            @Override
//            public void onJsFunctionCalled(String data) {
//                if (!data.contains(WebDataImpl.Javascript)) {
//                    String html = "<html>" + WebDataImpl.Javascript + data + "</html>";
//                    getBinding().wvcView.loadDataWithBaseURL(getBinding().wvcView.getUrl(), html, "text/html", "UTF-8", null);
//                }
//            }
//        }, "gouhua");

        loadUrl();
    }
}
