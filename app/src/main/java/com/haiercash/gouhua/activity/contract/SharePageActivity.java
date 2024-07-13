package com.haiercash.gouhua.activity.contract;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.wxapi.WxUntil;

import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/1/8<br/>
 * 描    述：<br/>
 * 修订历史：分享页面<br/>
 * ================================================================
 */
@Route(path = PagePath.ACTIVITY_SHARE)
public class SharePageActivity extends BaseActivity {

    private static final String SHARE_URL = "url";

    private String title = "够花";
    private String desc = "";
    private String imgThumb;
    private String shareUrl;

    @Override
    protected int getLayout() {
        return R.layout.activity_share_page;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        shareUrl = getParameter(SHARE_URL);
        title = CheckUtil.isEmpty(getParameter("title")) ? title : getParameter("title");
        desc = CheckUtil.isEmpty(getParameter("desc")) ? title : getParameter("desc");
        imgThumb = getParameter("img");
        //imgThumb = "https://pp.myapp.com/ma_icon/0/icon_5294_1582179300/96";
        if (CheckUtil.isEmpty(shareUrl)) {
            UiUtil.toast("无法获取分享链接内容");
            finish();
        }
        WxUntil.regToWx(this, false);
        //RxBus.getInstance().post(new VersionInfo());
    }


    private String getParameter(String parameter) {
        String param = getIntent().getStringExtra(parameter);
        if (CheckUtil.isEmpty(param)) {
            if (getIntent().getData() != null) {
                param = getIntent().getData().getQueryParameter(parameter);
            }
        }
        return param;
    }

    @OnClick({R.id.tv_wx_session, R.id.tv_wx_time_line, R.id.btn_cancel})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_wx_session:
                getBitmapToShare(1);
                break;
            case R.id.tv_wx_time_line:
                getBitmapToShare(2);
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    private void getBitmapToShare(final int flag) {
        if (!CheckUtil.isEmpty(imgThumb)) {
            showProgress(true);
            GlideUtils.getNetBitmap(this, imgThumb, new INetResult() {
                @Override
                public <T> void onSuccess(T t, String url) {
                    showProgress(false);
                    shareToWx(flag, (Bitmap) t);
                }

                @Override
                public void onError(BasicResponse error, String url) {
                    showProgress(false);
                    shareToWx(flag, null);
                }
            });
        } else {
            shareToWx(flag, null);
        }
    }

    public void shareToWx(int flag, Bitmap bitmap) {
        boolean needRecycle = false;
        if (bitmap == null) {
            needRecycle = true;
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        }
        if (WxUntil.isReady(mContext)) {
            if (flag == 1) {
                WxUntil.shareWebPage(mContext, WxUntil.WX_SHARE_TYPE_SESSION, bitmap, shareUrl, title, desc, needRecycle);
                finish();
            } else if (flag == 2) {
                if (WxUntil.checkSupported(mContext)) {
                    WxUntil.shareWebPage(mContext, WxUntil.WX_SHARE_TYPE_LINE, bitmap, shareUrl, title, desc, needRecycle);
                    finish();
                } else {
                    UiUtil.toast("您当前版本暂不支持朋友圈分享");
                }
            }
        } else {
            UiUtil.toast("请先安装微信客户端");
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public static void showSharePage(Activity context, String url) {
        Intent intent = new Intent(context, SharePageActivity.class);
        intent.putExtra(SHARE_URL, url);
        context.startActivity(intent);
        context.overridePendingTransition(0, 0);
    }
}
