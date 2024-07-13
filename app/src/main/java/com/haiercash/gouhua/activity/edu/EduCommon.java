package com.haiercash.gouhua.activity.edu;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import com.app.haiercash.base.utils.router.ARouterUntil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.PageBackDialog;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.view.EduProgressBottomBarView;

/**
 * Author: Sun<br/>
 * Date :    2018/11/6<br/>
 * FileName: EduCommon<br/>
 * Description: 额度申请公共类<br/>
 */
public class EduCommon {
    public static void resetSaveEdHasBackStatus() {
        //回到首页后挽留弹窗状态重置
        SpHp.deleteOther(SpKey.ED_HAS_BACK);
    }

    /**
     * 额度申请过程退出提示
     * umPageNameCn：友盟统计需要传
     */
    public static void onBackPressed(BaseActivity baseActivity, String authPageDesc, String pageCode, String umPageNameCn) {
        //挽留弹窗一个页面自身存在期间只弹一次
        if (!"true".equals(SpHp.getOther(SpKey.ED_HAS_BACK))) {
            SpHp.saveSpOther(SpKey.ED_HAS_BACK, "true");//回到首页后挽留弹窗状态重置
            PageBackDialog.showDialog(baseActivity, authPageDesc, pageCode, umPageNameCn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 2) {//放弃则回首页
                        MainHelper.backToMainHome();
                    }
                }
            });
        } else {
            MainHelper.backToMainHome();
        }
    }

    public static void setTitle(BaseActivity activity, EduProgressBottomBarView progressBar) {
        String from = activity.getIntent().getStringExtra("tag");
        if (!TextUtils.isEmpty(from) && "EDJH".equals(from)) {
            activity.setTitle("额度申请");
            activity.setRightImage(R.drawable.iv_blue_details, v -> ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation());
        } else if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }


}
