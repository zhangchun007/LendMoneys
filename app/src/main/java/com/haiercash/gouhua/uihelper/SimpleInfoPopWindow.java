package com.haiercash.gouhua.uihelper;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.PopupWindowBean;
import com.haiercash.gouhua.databinding.PopSimpleInfoBinding;
import com.haiercash.gouhua.interfaces.OnPopClickListener;

/**
 * ================================================================
 * 描    述：展示简单信息介绍的PopupWindow
 * 修订历史：<br/>
 * ================================================================
 */
public class SimpleInfoPopWindow extends BasePopupWindow {

    private final OnPopClickListener onPopClickListener;

    public SimpleInfoPopWindow(BaseActivity activity, Object data, int height, boolean isHalfTransparent, OnPopClickListener onPopClickListener) {
        super(activity, data);
        if (height != 0) {
            getBinding().ivBack.setVisibility(View.VISIBLE);//pop左箭头是否需要显示
            ViewGroup.LayoutParams params = getBinding().rlRoot.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = height;
            getBinding().rlRoot.setLayoutParams(params);
        } else {
            getBinding().ivBack.setVisibility(View.INVISIBLE);
        }
        ColorDrawable dw = new ColorDrawable(Color.parseColor(isHalfTransparent ? "#99000000" : "#00000000"));
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        this.onPopClickListener = onPopClickListener;
    }

    public static void showCouponRulePop(BaseActivity baseActivity, View rootV, int height, boolean isHalfTransparent, String rule, OnPopClickListener onPopClickListener) {
        PopupWindowBean bean = new PopupWindowBean();
        bean.setTitle("使用规则");
        bean.setContent(rule);
        SimpleInfoPopWindow popWindow = new SimpleInfoPopWindow(baseActivity, bean, height, isHalfTransparent, onPopClickListener);
        popWindow.setAnimationStyle(R.style.Animation_Design_BottomSheetDialog);
        popWindow.setPopupOutsideTouchable(true);
        popWindow.showAtLocation(rootV);
    }

    public static void showCouponRulePop(BaseActivity baseActivity, View rootV, int height, String rule, OnPopClickListener onPopClickListener) {
        showCouponRulePop(baseActivity, rootV, height, false, rule, onPopClickListener);
    }

    @Override
    protected PopSimpleInfoBinding initBinding(LayoutInflater inflater) {
        return PopSimpleInfoBinding.inflate(inflater, null, false);
    }

    private PopSimpleInfoBinding getBinding() {
        return (PopSimpleInfoBinding) _binding;
    }

    @Override
    protected void onViewCreated(Object data) {
        PopupWindowBean bean = (PopupWindowBean) data;
        getBinding().tvPopTitle.setText(bean.getTitle());
        getBinding().tvContent.setText(SpannableStringUtils.getBuilder(mActivity, TextUtils.isEmpty(bean.getContent()) ? "暂无说明" : bean.getContent()).create());
        getBinding().tvClose.setOnClickListener(this);
        getBinding().ivBack.setOnClickListener(this);
        getBinding().tvOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        dismiss();
        if (onPopClickListener != null) {
            if (v == getBinding().ivBack) {//关闭当前pop
                onPopClickListener.onViewClick(v, 1, null);
            } else if (v == getBinding().tvClose) {//关闭当前pop的同时也关掉上一页面
                onPopClickListener.onViewClick(v, 2, null);
            } else if (v == getBinding().tvOk) {//确定按钮
                onPopClickListener.onViewClick(v, 3, null);
            }
        }
    }

    @Override
    public void showAtLocation(View view) {
        if (view != null) {
            showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }
}
