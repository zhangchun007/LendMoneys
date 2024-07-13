package com.haiercash.gouhua.base;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.PopDialogBean;
import com.haiercash.gouhua.interfaces.PopCallBack;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UMengUtil;

import java.util.HashMap;

/**
 * 描    述：通知类型的弹窗（一张图片+关闭按钮）
 */
public class BaseNotifyDialog extends Dialog implements View.OnClickListener {
    protected BaseActivity mContext;// 上下文
    private ImageView popImageView;//内容图
    private PopDialogBean popDialogBean;
    private PopCallBack callBack;
    private String mPage;

    private ImageView ivClose;

    public BaseNotifyDialog(@NonNull BaseActivity context, PopCallBack callBack) {
        super(context, R.style.Theme_Light_FullScreenDialogAct);
        this.callBack = callBack;
        initViewAndEvent(context);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void setCallback(PopCallBack callBack) {
        this.callBack = callBack;
    }

    private void initViewAndEvent(BaseActivity context) {
        mContext = context;
        setContentView(R.layout.dialog_base_notify);
        popImageView = findViewById(R.id.iv_image);
        popImageView.setOnClickListener(this);
        //关闭
        ivClose = findViewById(R.id.iv_close_dialog);
        ivClose.setOnClickListener(this);
    }

    public void showDialog(PopDialogBean popDialogBean, String page) {
        if (popDialogBean == null || CheckUtil.isEmpty(popDialogBean.getShowImageUrl())) {
            return;
        }
        this.popDialogBean = popDialogBean;
        this.mPage = page;
        GlideUtils.getNetBitmap(mContext, popDialogBean.getShowImageUrl(), new INetResult() {
            @Override
            public <T> void onSuccess(T t, String url) {
                initDialog((Bitmap) t);
            }

            @Override
            public void onError(BasicResponse error, String url) {
                Logger.e("弹框内容图片加载失败...因此弹窗未显示");
            }
        });
    }

    //初始化弹窗
    private void initDialog(Bitmap bitmap) {
        this.show();
        try {//要求宽高比定死310:360，屏幕一定比率宽度  310:375
            ViewGroup.LayoutParams layoutParams = popImageView.getLayoutParams();
            layoutParams.width = SystemUtils.getDeviceWidth(mContext) * 310 / 375;
            layoutParams.height = layoutParams.width * 360 / 310;
            popImageView.setLayoutParams(layoutParams);
            popImageView.setImageBitmap(bitmap);


        } catch (Exception e) {
            Logger.e("通知弹窗异常");
        }
        if ("fpage".equals(mPage) && "NOTIFY".equals(popDialogBean.getPopUpsType())) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("page_name_cn", "首页");
            map.put("pop_name", popDialogBean.getPopUpsName());
            UMengUtil.onEventObject("HomePageMemberPop_Exposure", map, "HomePage");
        }
    }

    private void postClickBtnEvent(String buttonName) {
        if ("fpage".equals(mPage)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("page_name_cn", "首页");
            if (popDialogBean != null && !CheckUtil.isEmpty(popDialogBean.getPopUpsName())) {
                map.put("pop_name", popDialogBean.getPopUpsName());
            }
            UMengUtil.commonClickEvent("HomePagePop_Click", buttonName, map, "HomePage");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_close_dialog) {
            dismiss();
            postClickBtnEvent("取消");
            if (popDialogBean != null && !CheckUtil.isEmpty(popDialogBean.getNoticeId())) {
                callBack.clickNode(popDialogBean,true);
            }
        } else if (v.getId() == R.id.iv_image) {
            postClickBtnEvent("立即领取");
            if (popDialogBean != null && !CheckUtil.isEmpty(popDialogBean.getNoticeId())) {
                callBack.clickNode(popDialogBean,false);
                dismiss();
            }
        }
    }
}
