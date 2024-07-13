package com.haiercash.gouhua.uihelper;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseDialog;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 挽留弹窗
 */
public class PageBackDialog extends BaseDialog {
    private final String authPageDesc;
    private final String umPageNameCn;
    private final String pageCode;
    private final OnClickListener pageBackDialogListener;

    public PageBackDialog(@NonNull Context context, String authPageDesc, String pageCode, String umPageNameCn, OnClickListener pageBackDialogListener) {
        super(context, false);
        this.authPageDesc = authPageDesc;
        this.umPageNameCn = umPageNameCn;
        this.pageCode = pageCode;
        this.pageBackDialogListener = pageBackDialogListener;
    }

    public static void showDialog(@NonNull BaseActivity baseActivity, String pageName, String pageCode, String umPageNameCn, OnClickListener pageBackDialogListener) {
        new PageBackDialog(baseActivity, pageName, pageCode, umPageNameCn, pageBackDialogListener).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_page_back);
        SpannableStringBuilder spannableStringBuilder = SpannableStringUtils.getBuilder(getContext(), "为您准备了")
                .append(SpannableStringUtils.getBuilder(getContext(), "200,000").setForegroundColor(0xFFFF5151).create())
                .append("元额度")
                .create();
        ((TextView) findViewById(R.id.tvTitle)).setText(spannableStringBuilder);
        ((TextView) findViewById(R.id.tvContent)).setText(UiUtil.getStr(CheckUtil.isEmpty(authPageDesc) ? "" : "只需" + authPageDesc + "，", "真的放弃吗？"));
        findViewById(R.id.btnContinue).setOnClickListener(this);
        findViewById(R.id.btnGiveUp).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btnContinue) {
            dismiss();
            postUmEvent((TextView) v);
            if (pageBackDialogListener != null) {
                pageBackDialogListener.onClick(PageBackDialog.this, 1);
            }
        } else if (v.getId() == R.id.btnGiveUp) {
            dismiss();
            postUmEvent((TextView) v);
            if (pageBackDialogListener != null) {
                pageBackDialogListener.onClick(PageBackDialog.this, 2);
            }
        }
    }

    private String getPageCode() {
        return this.pageCode;
    }

    private void postUmEvent(TextView button) {
        String buttonName = button.getText().toString();
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", umPageNameCn);
        map.put("button_name", buttonName);
        UMengUtil.commonClickEvent("CreditReturnPop_Click", buttonName, map, getPageCode());
    }
}
