package com.haiercash.gouhua.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.activity.edu.PerfectInfoHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.CreditLifeBorrowBean;
import com.haiercash.gouhua.uihelper.CreditLifeHelp;
import com.haiercash.gouhua.utils.UiUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;

public class CreditApplyWebFragment extends WebSimpleFragment {
    private final int PHONE_NUM_REQUEST_CODE = 0x06;
    public static final int ID = CreditApplyWebFragment.class.hashCode();
    private CreditLifeBorrowBean creditLife;
    private final Handler mHandler = new MyHandler(this);
    private CreditLifeHelp help;

    private void todo(Message msg) {
        if (msg.what == 101) {//获取联系人
            String phone = (String) msg.obj;
            //回调js的获取原生方法返回值
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                x5WebView.loadUrl("javascript:contactSelectCallback(" + phone + ")");
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                x5WebView.evaluateJavascript("javascript:contactSelectCallback(" + phone + ")", s -> {
                    //js返回回调结果
                    //noinspection Convert2MethodRef
                    UiUtil.toast(s);
                });
            }
        }
    }

    private boolean shouldOverrideUrlLoading(String url) {
        if (url.contains("gouhuaUniteLogin")) {
            if (!CheckUtil.isEmpty(creditLife)) {
                help = new CreditLifeHelp(mActivity, creditLife);
                Map<String, String> map = help.getParameters2(url);
                if (map.containsKey("mobileNo")) {
                    map.put("mobileNo", RSAUtils.encryptByRSA(map.get("mobileNo")));
                }
                help.addOpenHistory(map);
                if (map.containsKey("callState") && "Y".equals(map.get("callState"))) {
                    help.addApplyRecord();
                }
            }
            return true;
        } else if (url.contains("contactSelect")) {
            //js调用原生获取手机联系人方法
            getNativeContact(url);
            return true;
        } else {
            return false;
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<CreditApplyWebFragment> mActivity;

        MyHandler(CreditApplyWebFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            mActivity.get().todo(msg);
        }
    }

    /**
     * 联合登录
     */
    public static void OpenWebFragment(BaseActivity mContext, String url, String channelName, String styleOthers, CreditLifeBorrowBean creditLife) {
        Bundle bundle = new Bundle();
        bundle.putString(WebSimpleFragment.WEB_URL, url);
        bundle.putString(WebSimpleFragment.WEB_TITLE, channelName);
        bundle.putString(WebSimpleFragment.STYLE, styleOthers);
        bundle.putBoolean(WebSimpleFragment.LEFTIMAGECLOSE, true);
        bundle.putSerializable("creditLife", creditLife);
        ContainerActivity.toForResult(mContext, ID, bundle, REQUEST_CODE);
    }

    public static CreditApplyWebFragment newInstance(Bundle bd) {
        final CreditApplyWebFragment f = new CreditApplyWebFragment();
        if (bd != null) {
            f.setArguments(bd);
        }
        return f;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initEventAndData() {
        if (null != getArguments()) {
            Bundle bundle = getArguments();
            mUrl = bundle.getString(WEB_URL);
            mTitle = bundle.getString(WEB_TITLE, "");//海尔消费金融
            mStyle = bundle.getString(STYLE);
            //IMAGEVISIBLITY = bundle.getBoolean(LEFTIMAGECLOSE);
            creditLife = (CreditLifeBorrowBean) getArguments().getSerializable("creditLife");
        }
        super.initEventAndData();
        x5WebView.getWebIHelper().setWebViewShouldOverride(this::shouldOverrideUrlLoading);//js调用原生获取手机联系人方法
        x5WebView.getWebIHelper().setWebViewOnReceivedHttpError((webResourceRequest, webResourceResponse) -> {
            if (webResourceRequest == null || webResourceResponse == null) {
                return;
            }
            WebResourceRequest resourceRequest = (WebResourceRequest) webResourceRequest;
            WebResourceResponse resourceResponse = (WebResourceResponse) webResourceResponse;
            //6.0以上处理404或500错误
            int statusCode = resourceResponse.getStatusCode();
            if (resourceRequest.isForMainFrame()) {
                if (403 == statusCode || 404 == statusCode || 500 == statusCode) {
                    sendAlrmLog(mTitle + "_" + statusCode + "_" + resourceRequest.getUrl().getScheme() + "://" + resourceRequest.getUrl().getAuthority() + resourceRequest.getUrl().getPath());
                }
            }
        });
        x5WebView.getWebIHelper().setWebViewOnReceivedError((webResourceRequest, webResourceError) -> {
            if (webResourceRequest == null || webResourceError == null) {
                return;
            }
            WebResourceRequest resourceRequest = (WebResourceRequest) webResourceRequest;
            // 6.0以上断网或者网络连接超时
            int errorCode = ((WebResourceError) webResourceError).getErrorCode();
            if (resourceRequest.isForMainFrame()) {
                if (errorCode == WebViewClient.ERROR_HOST_LOOKUP || errorCode == WebViewClient.ERROR_CONNECT || errorCode == WebViewClient.ERROR_TIMEOUT) {
                    sendAlrmLog(mTitle + "_" + errorCode + "_" + resourceRequest.getUrl().getScheme() + "://" + resourceRequest.getUrl().getAuthority() + resourceRequest.getUrl().getPath());
                }
            }
        });
        x5WebView.getWebIHelper().setWebViewLowOnReceivedError((errorCode, description, failingUrl) -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // 6.0以下断网或者网络连接超时
                if (errorCode == WebViewClient.ERROR_HOST_LOOKUP || errorCode == WebViewClient.ERROR_CONNECT || errorCode == WebViewClient.ERROR_TIMEOUT) {
                    if (failingUrl.contains("?")) {
                        sendAlrmLog(mTitle + "_" + errorCode + "_" + failingUrl.substring(0, failingUrl.indexOf("?")));
                    } else {
                        sendAlrmLog(mTitle + "_" + errorCode + "_" + failingUrl);
                    }
                }
            }
        });
        x5WebView.getWebIHelper().setWebViewOnReceivedTitle((title) -> {
            // android 6.0 以下通过title获取,404或500错误
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (title.contains("403") || title.contains("404") || title.contains("500") || title.contains("Error")) {
                    sendAlrmLog(mTitle + "_" + title);
                }
            }
        });
    }

    private void sendAlrmLog(String logContent) {
        Map<String, String> map = new HashMap<>();
        map.put("logContent", "贷款超市_" + logContent);
        netHelper.postService(ApiUrl.EXTERNAL_ALARM_LOG, map);
    }

    /**
     * 获取原生联系人
     */
    private void getNativeContact(String url) {
        if (url.contains("callback")) {
            requestPermission((Consumer<Boolean>) aBoolean -> {
                if (aBoolean) {
                    try {
                        mActivity.startActivityForResult(new Intent(Intent.ACTION_PICK,
                                ContactsContract.Contacts.CONTENT_URI), PHONE_NUM_REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showDialog("请确保通讯录权限已经开启");
                    }
                } else {
                    showDialog("请确保通讯录权限已经开启");
                }
            }, R.string.permission_read_contact, Manifest.permission.READ_CONTACTS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHONE_NUM_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                final List<String> userNumber = PerfectInfoHelper.getPhoneMailList(mActivity, data);
                if (userNumber.isEmpty()) {
                    showDialog("请确保通讯录权限已经开启，并选择正确的联系人手机号码");
                    return;
                }
                final Message msg = new Message();
                if (userNumber.size() == 1) {
                    msg.what = 101;
                    msg.obj = userNumber.get(0);
                } else if (userNumber.size() > 0) {
                    AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(mActivity);
                    singleChoiceDialog.setTitle("请选择手机号码");
                    singleChoiceDialog.setSingleChoiceItems(userNumber.toArray(new String[userNumber.size()]), 0,
                            (dialog, which) -> {
                                dialog.dismiss();
                                String phone = userNumber.get(which);
                                if (!TextUtils.isEmpty(phone)) {
                                    msg.what = 101;
                                    msg.obj = phone;
                                }
                            });
                    singleChoiceDialog.show();

                }
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (x5WebView != null) {
            creditLife = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        if (help != null) {
            help.destory();
        }
    }
}
