package com.haiercash.gouhua;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.fragments.main.MainFragment;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.fragments.main.MineFragmentNew;

import java.io.File;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/5/20<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class MainHomeHelper {
    public static final String ACTION_RECEIVER = "com.haiercash.gouhua.Notifaction.show";
    private BaseActivity mActivity;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    MainHomeHelper(BaseActivity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * 初始化MainActivity辅助类
     */
    void initHome() {
        localBroadcastManager = LocalBroadcastManager.getInstance(mActivity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_RECEIVER);
        localReceiver = new LocalReceiver();
        //注册本地接收器
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    /**
     * 设置显示新的icon
     */
    void showNewIcon(LinearLayout rgMenu, boolean isShowNew) {
        if (isShowNew) {
            String filePath = MainHelper.getIconFilePath();
            if (CheckUtil.isEmpty(filePath)) {
                return;
            }
            File file = new File(filePath);
            //固定四个tab占位
            if (!file.exists() || file.list() == null || file.list().length == 0 || file.list().length % 2 != 0) {
                return;
            }
            for (int i = 0; i < rgMenu.getChildCount(); i++) {
                Drawable drawableDefault = Drawable.createFromPath(new File(file, String.format(MainHelper.strFormatDefault, i)).getAbsolutePath());
                Drawable drawableSelect = Drawable.createFromPath(new File(file, String.format(MainHelper.strFormatSelect, i)).getAbsolutePath());

                StateListDrawable drawable = new StateListDrawable();
                drawable.addState(new int[]{-android.R.attr.state_selected}, drawableDefault);
                drawable.addState(new int[]{android.R.attr.state_selected}, drawableSelect);
                ((ImageView) rgMenu.getChildAt(i)).setImageDrawable(drawable);
            }
        } else {
            try {
                //固定四个tab占位
                int[] resIds = {R.drawable.selector_bottom_loan_market,R.drawable.selector_bottom_home_top, R.drawable.selector_bottom_member_top, R.drawable.selector_bottom_mine_top};
                for (int i = 0; i < rgMenu.getChildCount(); i++) {
                    ((ImageView) rgMenu.getChildAt(i)).setImageResource(resIds[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (ACTION_RECEIVER.equals(action)) {
                    Fragment current = mActivity.getSupportFragmentManager().findFragmentById(R.id.fl_main_content);
                    if (current instanceof MainFragment) {
                        ((MainFragment) current).isHidePoint(false);
                    } else if (current instanceof MineFragmentNew) {
                        ((MineFragmentNew) current).isHidePoint();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
}
