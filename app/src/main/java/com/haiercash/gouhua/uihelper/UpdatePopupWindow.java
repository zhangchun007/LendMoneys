package com.haiercash.gouhua.uihelper;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.login.VersionInfo;
import com.haiercash.gouhua.interfaces.OnPopClickListener;
import com.haiercash.gouhua.utils.GlideUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: Sun
 * @Date :    2017/11/17
 * @FileName: UpdatePopupWindow
 * @Description: 用于公告弹窗
 */

public class UpdatePopupWindow extends BasePopupWindow {

    private OnPopClickListener popClickListener;

    //顶部图片
    @BindView(R.id.iv_version_update)
    ImageView ivHead;

    //取消
    @BindView(R.id.iv_version_update_cancel)
    ImageView ivCancle;

    //标题
    @BindView(R.id.tv_title)
    TextView tvTitle;

    //内容
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_version_update_qiangzhi)
    ImageView iv_version_update_qiangzhi;
    @BindView(R.id.ll_no_update)
    LinearLayout ll_no_update;
    //公告内容
    private VersionInfo mVersionInfo;

    //取消下载
    public static final int UPDATE_CANCLE = 0x01;
    // 开始下载
    public static final int UPDATE_CONFIRM = 0x02;


    public UpdatePopupWindow(BaseActivity context, VersionInfo versionInfo, OnPopClickListener onPopClickListener) {
        super(context, versionInfo);
        popClickListener = onPopClickListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.include_version_update;
    }

    @Override
    protected void onViewCreated(Object data) {
        mVersionInfo = (VersionInfo) data;
        // 顶部图片
        GlideUtils.loadFit(mActivity, ivHead, mVersionInfo.getTitleImage(), R.drawable.iv_version_update_head);

        //标题
        if (!TextUtils.isEmpty(mVersionInfo.getNoticeTitle())) {
            tvTitle.setText(mVersionInfo.getNoticeTitle());
        }
        //内容
        if (!TextUtils.isEmpty(mVersionInfo.getNoticeContent())) {
            tvContent.setText(mVersionInfo.getNoticeContent());
        }
        tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        //是否强制
        if (mVersionInfo.isForcedUpdate()) {
            iv_version_update_qiangzhi.setVisibility(View.VISIBLE);
            ll_no_update.setVisibility(View.GONE);
            ivCancle.setVisibility(View.GONE);
            setPopupOutsideTouchable(true);
        } else {
            iv_version_update_qiangzhi.setVisibility(View.GONE);
            ll_no_update.setVisibility(View.VISIBLE);
            ivCancle.setVisibility(View.VISIBLE);
            setPopupOutsideTouchable(false);
        }
    }

    @Override
    public void showAtLocation(View view) {
        //居中显示popup
        showAtLocation(view, Gravity.CENTER, 0, 0);
        update();
    }

    // 下载或取消
    @OnClick({R.id.iv_version_update_cancel, R.id.btn_update,R.id.iv_version_update_qiangzhi,R.id.btn_cancle1})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle1:
            //取消
            case R.id.iv_version_update_cancel:
                VersionHelper.cancelVersionUpdate();
                dismiss();
                if (popClickListener != null) {
                    popClickListener.onViewClick(v, UPDATE_CANCLE, null);
                }
                break;
            case R.id.iv_version_update_qiangzhi:
            case R.id.btn_update:
                //下载
                dismiss();
                if (popClickListener != null) {
                    popClickListener.onViewClick(v, UPDATE_CONFIRM, null);
                }
                break;
        }

    }
}
