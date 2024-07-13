package com.haiercash.gouhua.base;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.Coupon;
import com.haiercash.gouhua.beans.PopDialogBean;
import com.haiercash.gouhua.interfaces.PopCallBack;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/17
 * 描    述：权益弹窗
 * 修订历史：
 * ================================================================
 */
public class BaseRaisDialog extends Dialog {
    protected BaseActivity mContext;// 上下文
    private ImageView ivHead;   //头部图
    private TextView tvBottom;    //底部文字
    private RecyclerView rvRais;  //权益列表
    private PopDialogBean popDialogBean;
    private PopCallBack callBack;
    private String mPage;
    //private LinearLayout llRaisRoot;//recyclerview父布局，供设置大小用

    public BaseRaisDialog(@NonNull BaseActivity context, PopCallBack callBack) {
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
        setContentView(R.layout.dialog_common_rais);
        ivHead = findViewById(R.id.iv_head);
        tvBottom = findViewById(R.id.tv_dialog_bottom);
        rvRais = findViewById(R.id.rv_rais);
        //llRaisRoot = findViewById(R.id.ll_rais_root);
        //关闭
        ImageView ivClose = findViewById(R.id.iv_close_dialog);
        ivClose.setOnClickListener(v -> {
            dismiss();
            postClickBtnEvent("取消");
            if (popDialogBean != null && !CheckUtil.isEmpty(popDialogBean.getNoticeId())) {
                callBack.clickNode(popDialogBean, true);
            }
        });
    }

    public void showDialog(PopDialogBean popDialogBean, String page) {
        if (popDialogBean == null
                || popDialogBean.getCouponList() == null
                || popDialogBean.getCouponList().size() < 1
                || CheckUtil.isEmpty(popDialogBean.getTopImageUrl())) {
            return;
        }
        this.popDialogBean = popDialogBean;
        this.mPage = page;
        GlideUtils.getNetBitmap(mContext, popDialogBean.getTopImageUrl(), new INetResult() {
            @Override
            public <T> void onSuccess(T t, String url) {
                initDialog((Bitmap) t);
            }

            @Override
            public void onError(BasicResponse error, String url) {
                Logger.e("弹框头图加载失败...因此弹窗未显示");
            }
        });

    }

    //初始化弹窗
    private void initDialog(Bitmap bitmap) {
        ivHead.setImageBitmap(bitmap);
        rvRais.setBackgroundColor(Color.parseColor(popDialogBean.getBackgroundColor()));
        GradientDrawable gd = (GradientDrawable) tvBottom.getBackground();
        try {
            gd.setColor(Color.parseColor(popDialogBean.getBackgroundColor()));
        } catch (Exception e) {
            Logger.e("PopDialogAdapter解析背景色失败");
        }

        if (!CheckUtil.isEmpty(popDialogBean.getBottomText())) {
            tvBottom.setText(popDialogBean.getBottomText());
        }
        if (popDialogBean.getCouponList().size() > 3) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rvRais.getLayoutParams();
            lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
            lp.height = UiUtil.dip2px(mContext, 300);
            rvRais.setLayoutParams(lp);
        }
        PopDialogAdapter adapter = new PopDialogAdapter(popDialogBean.getButtonColor());
        rvRais.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRais.setAdapter(adapter);
        adapter.addChildClickViewIds(R.id.tv_go);
        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
            if (view.getId() == R.id.tv_go) {
                postClickBtnEvent("立即领取");
                Coupon coupon = (Coupon) adapter1.getData().get(position);
                if (coupon == null) {
                    return;
                }
                if (popDialogBean != null && !CheckUtil.isEmpty(popDialogBean.getNoticeId())) {
                    callBack.clickNode(popDialogBean, false);
                    dismiss();
                }
                if ("COUPON".equals(coupon.getCouponType())) {  //免息券
                    if (AppApplication.isLogIn()) {
                        ARouterUntil
                                .getInstance(PagePath.ACTIVITY_COUPON_BAG)
                                .put("couponType", 2)
                                .navigation();
                    }
                } else if ("OUT_COUPON".equals(coupon.getCouponType()) && CheckUtil.isEmpty(coupon.getLink())) {  //外部券
                    if (AppApplication.isLogIn()) {
                        ARouterUntil
                                .getInstance(PagePath.ACTIVITY_COUPON_BAG)
                                .put("couponType", 1)
                                .navigation();
                    }
                } else if (!CheckUtil.isEmpty(coupon.getLink())) {
                    Bundle argc = new Bundle();
                    argc.putString("title", "够花");
                    argc.putBoolean("isShowWebViewTitle", false);
                    WebHelper.startActivityForUrl(mContext, coupon.getLink(), argc);
                }
            }
        });
        adapter.setNewData(popDialogBean.getCouponList());
        this.show();
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
}
