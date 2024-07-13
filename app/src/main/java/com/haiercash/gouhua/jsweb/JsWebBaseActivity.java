package com.haiercash.gouhua.jsweb;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.app.haiercash.base.utils.image.PhotographUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.inenter.BaseVerifyActivity;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.BillBearLoginPop;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.StatusBar;
import com.haiercash.gouhua.wxapi.WxUntil;
import com.haiercash.gouhua.x5webview.CusWebView;
import com.haiercash.gouhua.x5webview.WebDataImpl;
import com.haiercash.gouhua.x5webview.WebInterfaceHelper;
import com.haiercash.gouhua.x5webview.WebViewHelper;
import com.umeng.spm.SpmAgent;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class JsWebBaseActivity extends BaseActivity {
    @BindView(R.id.ll_parent)
    View llParent;
    @BindView(R.id.status_bar)
    StatusBar statusBar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.head_left)
    ImageView headLeft;
    @BindView(R.id.head_close)
    ImageView headClose;
    @BindView(R.id.bkWebview)
    CusWebView bkWebview;
    @BindView(R.id.cl_header)
    ConstraintLayout clHeader;
    @BindView(R.id.v_top_space)
    View vTopSpace;
    @BindView(R.id.head_right)
    ImageView headRight;
    @BindView(R.id.head_rightText)
    TextView headRightText;
    private Intent dataIntent;
    private WebDataImpl webImpl;
    private WebViewHelper webViewHelper;
    //CallURL()方法调用生命周期标志，区分是否由onCreate()方法发起，若不是需要强制刷新网页。
    private BillBearLoginPop bearLoginPop;
    private boolean fromPush;

    @Override
    protected int getLayout() {
        return R.layout.activity_js_base_webview;
    }

    private boolean showHeader = true;//是否展示app的导航栏，标志位

    public void showHeader(boolean show, boolean always) {
        if (always) {//保存此flag
            showHeader = show;
        }
        if (clHeader != null) {
            clHeader.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    protected void setTopSpaceHeight(int height) {//半弹窗效果，顶部距离,设置遮罩层的高度
        if (vTopSpace != null) {
            vTopSpace.getLayoutParams().height = height;
        }
    }

    protected void setStatusBarVisible(boolean isVisible) {
        if (statusBar != null) {
            statusBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    protected void setTitleVisible(boolean isVisible) {
        if (tvTitle != null) {
            tvTitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    public void setTitleTextColor(String titleColor) {
        if (tvTitle != null) {
            try {
                if (!CheckUtil.isEmpty(titleColor)) {
                    tvTitle.setTextColor(Color.parseColor(titleColor));
                } else {
                    tvTitle.setTextColor(ContextCompat.getColor(this, R.color.text_black));
                }
            } catch (Exception e) {
                tvTitle.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            }
        }
    }

    //isWhite = Y则设置返回键为白色
    public void setHeadLeftIcon(boolean isWhite) {
        if (isWhite) {
            headLeft.setImageResource(R.drawable.iv_back_white);
        } else {
            headLeft.setImageResource(R.drawable.iv_back_arrow_blues);
        }

    }

    protected void setBackgroundTransparent() {
        setWebBackgroundColor(0);
    }

    protected void setWebBackgroundColor(int color) {
        if (bkWebview != null) {
            //以下两行代码，webView背景色
            bkWebview.setBackgroundColor(color);
            //禁止硬件加速,webView背景透明化需要
            bkWebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    protected void setRootBackgroundColor(int color) {
        if (llParent != null) {
            llParent.setBackgroundColor(color);
        }
    }

    protected void setWebVisible(boolean visible) {
        if (bkWebview != null) {
            bkWebview.setAlpha(visible ? 1 : 0);
        }
    }

    public void setLeftImageVisible(boolean isShow) {
        if (isShow) {
            headLeft.setVisibility(View.VISIBLE);
            headClose.setVisibility(View.VISIBLE);
        } else {
            headLeft.setVisibility(View.GONE);
            headClose.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        AndroidBug5497Workaround.assistActivity(this);
        dataIntent = getIntent();
        setStyle();
        webViewHelper = new WebViewHelper();
        String isCloseUmengBridging = dataIntent.getStringExtra("isCloseUmengBridging");
        if (CheckUtil.isEmpty(isCloseUmengBridging) || (!"Y".equals(isCloseUmengBridging) && !"y".equals(isCloseUmengBridging))) {
            SpmAgent.attach(bkWebview);
        }
        String appUserAgent = dataIntent.getStringExtra("appUserAgent");
        bkWebview.reInit(appUserAgent);
        bkWebview.getWebIHelper().setWebViewShouldOverride(url -> {
            String scheme = Uri.parse(url).getScheme();
            if (TextUtils.equals("bestvike", scheme)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (!CheckUtil.isEmpty(url) && url.contains("gouhua://help_center")) {//帮助中心
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation();
                return true;
            } else if (!CheckUtil.isEmpty(url) && url.contains("gouhua://")) {
                ActivityUntil.startOtherApp(mContext, url);
                return true;
            }else if (!CheckUtil.isEmpty(url) && (url.contains("alipays://")||url.contains("alipay://"))){
                try {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                    UiUtil.toast("打开支付宝失败了,请刷新重试~");
                    return true;
                }
            }else if (!CheckUtil.isEmpty(url) && url.contains("weixin://wap/pay?")) {
                try {
                    //微信登录
                    if (!WxUntil.isReady(this)) {
                        UiUtil.toast("请先安装微信应用");
                        return true;
                    }
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                    UiUtil.toast("打开微信失败了,请刷新重试~");
                    return true;
                }
            } else if (!CheckUtil.isEmpty(url) && url.contains("alipay.com")) {
                //拦截阿里native支付
                /**
                 * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
                 */
                final PayTask task = new PayTask(JsWebBaseActivity.this);
                boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                    @Override
                    public void onPayResult(final H5PayResultModel result) {
                    }
                });
                /**
                 * 判断是否成功拦截
                 * 若成功拦截，则无需继续加载该URL；否则继续加载
                 */
                if (!isIntercepted) {
                    bkWebview.loadUrl(url);
                }
                return true;
            }else if (!CheckUtil.isEmpty(url) && url.contains("appmarket://")) {
                Intent intent= new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
                return true;
            }
            return false;
        });
        bkWebview.getWebIHelper().setWebResourceResponse(new WebInterfaceHelper.WebViewResourceResponse() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Uri uri = request.getUrl();
                    String path = uri.getPath();//仅路径，不带参数
                    //Logger.d("cut", "Request  uri拦截路径uri：：" + uri + "------" + path);
                    //System.out.println("webview 请求头：" + JsonUtils.toJson(request.getRequestHeaders()));
                    return webViewHelper.shouldInterceptRequest(view, request);
                }
                return null;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return null;
            }
        });
        bkWebview.getWebIHelper().setWebViewPageStarted(new WebInterfaceHelper.WebViewPageStarted() {
            @Override
            public void onPageStarted(String url) {
                showHeader(showHeader, false);//防止导航栏错误出现/隐藏，加载失败会显示导航栏，所以重新加载任何url需要处理导航栏设置
            }
        });
        bkWebview.getWebIHelper().setWebViewPageFinished((url) -> {
            //加载完页面后再加载图片
            if (!bkWebview.getSettings().getLoadsImagesAutomatically()) {
                bkWebview.getSettings().setLoadsImagesAutomatically(true);
            }
            JsWebBaseActivity.this.onPageFinished(url);
        });
        bkWebview.getWebIHelper().setWebViewOnReceivedError(new WebInterfaceHelper.WebViewOnReceivedError() {
            @Override
            public void onReceivedError(Object webResourceRequest, Object webResourceError) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (webResourceRequest instanceof WebResourceRequest && ((WebResourceRequest) webResourceRequest).isForMainFrame()) {
                        onPageError();
                    }
                }
            }
        });
        bkWebview.getWebIHelper().setWebViewLowOnReceivedError(new WebInterfaceHelper.WebViewLowOnReceivedError() {
            @Override
            public void onReceivedError(int errorCode, String description, String failingUrl) {
                onPageError();
            }
        });
        bkWebview.getWebIHelper().setWebViewOnReceivedTitle(title -> {
            tvTitle.setText(CheckUtil.isEmpty(title) ? "" : title);
        });
        bkWebview.getWebIHelper().setWebViewonJsPrompt((url, message, defaultValue, jsPromptResult) -> {
            //bestvike://bk_bridge:773058588/synchMethod?{"method":"cashBus"}
            String methodName = "";
            String className;
            String param;
            String port;
            if (!TextUtils.isEmpty(message) && message.startsWith("bestvike")) {
                Uri uri = Uri.parse(message);
                className = uri.getHost();
                param = uri.getQuery();
                port = uri.getPort() + "";
                String path = uri.getPath();
                if (!CheckUtil.isEmpty(path)) {
                    methodName = path.replace("/", "");
                }
                try {
                    deelSynchMethod(new JSONObject(param), className);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("--->" + methodName + "-->" + port);
                ((JsPromptResult) jsPromptResult).confirm();
                return true;
            } else {
                return false;
            }
        });
        bkWebview.getWebIHelper().setWebViewFileChoose(() -> {
            //UiUtil.toast("选择文件");
            if (CheckUtil.isEmpty(bkWebview.getFileType())) {
                ActivityUntil.openFileChooseProcess(mContext);
            } else {
                if (bkWebview.getFileType().contains("video")) {
                    Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    //设置视频录制的最长时间
                    //i.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                    //设置视频录制的画质
                    i.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(Intent.createChooser(i, "File Chooser"), 0);
                } else {
                    if (bkWebview.isCaptureEnabled()) {//拍照
                        requestPermission((Consumer<Boolean>) aBoolean -> {
                            if (aBoolean) {
                                PhotographUtils.startCameraCapture(mContext);
                            } else {
                                showDialog("请授权“相机”权限");
                            }
                        }, R.string.permission_camera, Manifest.permission.CAMERA);
                    } else {//打开相册
                        requestPermission((Consumer<Boolean>) aBoolean -> {
                            if (aBoolean) {
                                PhotographUtils.startPhotoAlbum(mContext);
                            } else {
                                showDialog("请授权手机存储权限");
                            }
                        }, R.string.permission_storage, Manifest.permission.READ_EXTERNAL_STORAGE);
                        //Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        //i.addCategory(Intent.CATEGORY_OPENABLE);
                        //i.setType(bkWebview.getFileType());
                        //startActivityForResult(Intent.createChooser(i, "File Chooser"), 0);
                    }
                }
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                if (bkWebview.getFileType().contains("video")) {
//                    i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                    //设置视频录制的最长时间
//                    //i.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
//                    //设置视频录制的画质
//                    i.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//                } else {
//                    i.addCategory(Intent.CATEGORY_OPENABLE);
//                    i.setType(bkWebview.getFileType());
//                }
//                startActivityForResult(Intent.createChooser(i, "File Chooser"), 0);
                //showPhotoChooser();
                //new UtilPhoto().showDialog(this, picturesToChoose);
            }

        });
        //bkWebview.clearCache(true);//清除网页缓存
        bkWebview.setShowProgressBar(showProgressBar());
        loaDatas();
        webImpl = WebDataImpl.getInstance(this, bkWebview);
        //new WebDataImpl(this, bkWebview).initWebMethod();


        RxBus.getInstance().addSubscription(this, RxBus.getInstance().register(ActionEvent.class, (actionEvent -> {
            if (actionEvent.getActionType() == ActionEvent.RED_BAG_WX_AUTH) {
                webImpl.wxCodeInfoCallBack(actionEvent.getActionMsg());
            }
        })));
    }

    @Override
    protected void onResume() {
        bkWebview.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        bkWebview.onPause();
        super.onPause();
    }

    protected CusWebView getWebView() {
        return bkWebview;
    }

    protected boolean showProgressBar() {
        return true;
    }

    protected void onPageFinished(String url) {
        //Logger.e("---web onPageFinished---");
    }

    public void onH5LoadFinished() {
        Logger.e("---H5通知加载完毕---");
    }

    protected void onPageError() {
        showHeader(true, false);
    }
//    private Uri imageUri;
//
//    /**
//     * 打开选择文件/相机
//     */
//    private void showPhotoChooser() {
//        Intent intentPhoto = new Intent(Intent.ACTION_PICK);
//        intentPhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
////        Intent intentPhoto = new Intent(Intent.ACTION_GET_CONTENT);
////        intentPhoto.addCategory(Intent.CATEGORY_OPENABLE);
////        intentPhoto.setType("*/*");
//        File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
//        imageUri = Uri.fromFile(fileUri);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
//        }
//        //调用系统相机
//        Intent intentCamera = new Intent();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
//        }
//        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//        //将拍照结果保存至photo_file的Uri中，不保留在相册中
//        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
//        chooser.putExtra(Intent.EXTRA_TITLE, "Photo Chooser");
//        chooser.putExtra(Intent.EXTRA_INTENT, intentPhoto);
//        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentCamera});
//        startActivityForResult(chooser, 0);
//    }


    private void loaDatas() {
        if (!CheckUtil.isEmpty(dataIntent)) {
            if (!CheckUtil.isEmpty(dataIntent.getStringExtra("jumpKey"))) {
                String jumpKey = dataIntent.getStringExtra("jumpKey");
                if (!CheckUtil.isEmpty(jumpKey)) {
                    if (jumpKey.contains("?showHeader=1")
                            || jumpKey.contains("&showHeader=1")) {
                        //"showHeader=1"涉及显示H5导航栏，并且隐藏app导航栏
                        showHeader(false, true);
                    }
                    //WebUntil.addUseIdUrlAndReload(bkWebview, jumpKey);
                    if (jumpKey.contains("ghscheme://com.haiercash.gouhua/billbear_login")) {
                        if (bearLoginPop == null) {
                            bearLoginPop = new BillBearLoginPop(this, null);
                        }
                        bearLoginPop.checkLoginStatus(bkWebview);
                        finish();   //中间会出现空白的页面
                    } else {
                        bkWebview.loadUrl(jumpKey);
                    }
                } else {
                    showProgress(false);
                }
            }
            if (!CheckUtil.isEmpty(getIntent().getBooleanExtra("fromPush", false))) {
                fromPush = getIntent().getBooleanExtra("fromPush", false);
            }
        }
    }

    protected void setStyle() {
        bkWebview.setPadding(0, clHeader.getHeight(), 0, 0);
        headLeft.setImageResource(R.drawable.iv_back_arrow_blues);
        if (dataIntent != null && dataIntent.getBooleanExtra("isHideCloseIcon", false)) {
            headClose.setVisibility(View.GONE);
        } else {
            headClose.setImageResource(R.drawable.ic_web_close);
            headClose.setVisibility(View.VISIBLE);
        }
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.text_black));
        setStatusBarTextColor(true);
    }

    @OnClick({R.id.head_left, R.id.head_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.head_left:
                if (fromPush) {
                    CommomUtils.goHomePage(true);
                } else {
                    onBackPressed();
                }
                break;
            case R.id.head_close:
                if (fromPush) {
                    CommomUtils.goHomePage(true);
                } else {
                    isCloseCurrentActivity();
                }
                break;
            default:
                break;
        }
    }

    public void isCloseCurrentActivity() {
        if (AppApplication.isLogIn()) {
            backToWhere();
        } else {
            if (ActivityUntil.findActivity(MainActivity.class) == null && ActivityUntil.getActivitySize() <= 1) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (webImpl != null) {
            webImpl.onDestroy();
            webImpl = null;
        }
        if (bkWebview != null) {
            SpmAgent.detach();
            bkWebview.destroy();
            bkWebview = null;
        }
        if (bearLoginPop != null) {
            bearLoginPop.onDestroy();
        }
        RxBus.getInstance().unSubscribe(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!bkWebview.onBackPressed()) {
            if (AppApplication.isLogIn()) {
                backToWhere();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private void backToWhere() {
        Class className = (Class) getIntent().getSerializableExtra("class");
        if (!CheckUtil.isEmpty(className)) {
            //UiUtil.toast("测试：" + className.getSimpleName() + "---" + className.getSuperclass().getSimpleName());
            if (!CheckUtil.isEmpty(className.getSuperclass()) && className.getSuperclass().equals(BaseVerifyActivity.class)) {
                startActivity(new Intent(mContext, className));
            } else if (ActivityUntil.findActivity(MainActivity.class) == null) {
                startActivity(new Intent(mContext, className));
            } else {
                super.onBackPressed();
                return;
            }
            finish();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * webview 内js同步调用java方法，
     *
     * @param param 传参数
     * @param host  主机名
     */
    public void deelSynchMethod(JSONObject param, String host) {
        if ("bk_bridge".equals(host)) {
            String method = optString(param, "method");
            if (!CheckUtil.isEmpty(method) && "cashBus".equals(method)) {
                AppApplication.setLoginCallback(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        deelSynchMethodLoginAction();
                    }
                });
            }
        }

    }

    private void deelSynchMethodLoginAction() {
        showDialog("提示", SpannableStringUtils.getBuilder(this, "成功获得额度和成功申请借款均可获得抽奖机会，超多大奖等你来抢!").create(), "取消", "确定",
                (dialog, which) -> {
                    if (which == 2) {
                        isCloseCurrentActivity();
                    } else {
                        dialog.dismiss();
                    }
                }).setStandardStyle(4);
    }

    /**
     * 判断value是否为空
     */
    public static String optString(JSONObject json, String key) {
        if (null == json) {
            return null;
        } else if (json.isNull(key)) {
            return null;
        } else {
            return json.optString(key, null);
        }
    }

    public void setTitleBarRightText(CharSequence sequence, View.OnClickListener listener) {
        if (CheckUtil.isEmpty(sequence)) {
            headRightText.setVisibility(View.GONE);
            return;
        } else {
            headRightText.setVisibility(View.VISIBLE);
        }
        headRightText.setText(sequence);
        headRightText.setOnClickListener(listener);
    }

    public void setTitleBarRightImage(boolean isShow, int resourceId, View.OnClickListener listener) {
        if (!isShow) {
            headRight.setVisibility(View.GONE);
            return;
        } else {
            headRight.setVisibility(View.VISIBLE);
        }
        headRight.setImageDrawable(ContextCompat.getDrawable(this, resourceId));
        headRight.setOnClickListener(listener);
    }

    public void setTitleBarRightImageParam(int marginTop, int marginEnd, int marginBottom) {
        if (headRight != null) {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) headRight.getLayoutParams();
            lp.topMargin = UiUtil.dip2px(this, marginTop);
            lp.rightMargin = UiUtil.dip2px(this, marginEnd);
            lp.bottomMargin = UiUtil.dip2px(this, marginBottom);
            headRight.setLayoutParams(lp);

        }
    }

    public void setTitleBarBackground(boolean isImg, int resourceId, String color) {
        if (isImg) {
            clHeader.setBackgroundResource(resourceId);
        } else {
            if (!CheckUtil.isEmpty(color)) {
                try {
                    clHeader.setBackgroundColor(Color.parseColor(color));
                } catch (Exception e) {
                    clHeader.setBackgroundColor(getResources().getColor(R.color.white));
                }
            } else {
                clHeader.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //目前只有ocr会用到这个，如果有新增需要在这同步
        if (webImpl != null && (requestCode == WebDataImpl.CardBack
                || requestCode == WebDataImpl.CardFront
                || requestCode == WebDataImpl.START_VIP_COUPON
                || requestCode == WebDataImpl.PHONE_NUM_REQUEST_CODE
                || requestCode == PhotographUtils.PHOTOALBUM_WEB_JS)) {
            webImpl.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (bkWebview != null) {
            bkWebview.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
