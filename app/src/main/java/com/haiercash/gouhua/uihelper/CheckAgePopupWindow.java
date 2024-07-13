package com.haiercash.gouhua.uihelper;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.PopupWindowBean;
import com.haiercash.gouhua.interfaces.OnPopClickListener;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 中间弹出，右上角带关闭按钮
 * 中间是内容
 * 最下面为一个按钮
 */
public class CheckAgePopupWindow extends BasePopupWindow {
    @BindView(R.id.tv_content)
    TextView tvContent;
    private OnPopClickListener onPopClickListener;

    public CheckAgePopupWindow(BaseActivity activity, Object data, OnPopClickListener onPopClickListener) {
        super(activity, data);
        this.onPopClickListener = onPopClickListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.pop_normal;
    }

    @Override
    protected void onViewCreated(Object data) {
        if (data != null) {
            tvContent.setText((String) data);
        }
    }

    @OnClick({R.id.tv_commit, R.id.iv_close})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.tv_commit:
                dismiss();
                onPopClickListener.onViewClick(view, -1, null);
                break;
            default:
                break;

        }
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

}
