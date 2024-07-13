package com.haiercash.gouhua.uihelper;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.interfaces.OnPopClickListener;
import com.app.haiercash.base.utils.system.CheckUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: liuyaxun
 * Date :    2019/2/26
 * FileName: CreditApplyDetailFragment
 * Description: 联合登录授权弹窗
 */
public class CreditApplyPopupWindow extends BasePopupWindow {
    @BindView(R.id.tv_popo_title)
    TextView tvPopoTitle;
    @BindView(R.id.tv_des)
    TextView tvDes;
    @BindView(R.id.tv_submit_agree)
    TextView tvSubmitAgree;
    @BindView(R.id.iv_close_detail)
    ImageView ivClose;
    private OnPopClickListener onPopClickListener;

    public CreditApplyPopupWindow(BaseActivity context, Object data, OnPopClickListener onPopClickListener) {
        super(context, data);
        this.onPopClickListener = onPopClickListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.view_dialog_credit_apply;
    }

    @Override
    protected void onViewCreated(Object data) {
        String title = (String) data;
        tvPopoTitle.setText(CheckUtil.isEmpty(title) ? "" : title);
        Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.icon_blue_t);
        if (drawable != null) {
            drawable.setBounds(0, 0, 40, 40);
        }
        tvDes.setCompoundDrawables(drawable, null, null, null);
        setPopupOutsideTouchable(true);
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    @OnClick({R.id.tv_submit_agree, R.id.iv_close_detail})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close_detail:
                dismiss();
                break;
            case R.id.tv_submit_agree:
                dismiss();
                onPopClickListener.onViewClick(view, -1, null);
                break;
            default:
                break;

        }
    }
}
