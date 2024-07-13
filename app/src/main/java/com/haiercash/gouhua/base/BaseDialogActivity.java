package com.haiercash.gouhua.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.SoftKeyBoardListenerUtil;
import com.appsafekb.safekeyboard.NKeyBoardTextField;
import com.appsafekb.safekeyboard.interfaces.KeyBoardListener;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.SplitEditTextView;
import com.haiercash.gouhua.widget.DelEditText;
import com.haiercash.gouhua.widget.NewNKeyBoardTextField;

/**
 * @Description: 带有dialog样式的activity
 * 继承自BaseDialogActivity的activity需要在manifest文件中设置
 * android:theme="@style/dialogstyle"
 * @Author: zhangchun
 * @CreateDate: 11/16/22
 * @Version: 1.0
 */
public abstract class BaseDialogActivity extends BaseActivity {


    /**
     * 动画的启动方式
     * bottom_top:从下往上进入，从上往下消失
     */
    public static String ANIM_TYPE_BOTTOM = "bottom_top";

    /**
     * 动画的启动方式
     * bottom_in_right_out:从下往上进入，往右侧划出
     */
    public static String ANIM_BOTTOM_IN_RIGHT_OUT = "bottom_in_right_out";


    /**
     * 动画的启动方式
     * right_left:从右往左进入，从左往右消失
     */
    public static String ANIM_TYPE_RIGHT = "right_left";

    //子类实现动效具体方式
    protected String animTypes = ANIM_TYPE_BOTTOM;


    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        //设置成dialog样式
//        Window win = this.getWindow();
//        win.getDecorView().setPadding(0, 0, 0, 0);
//        WindowManager.LayoutParams lp = win.getAttributes();
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
//        win.setAttributes(lp);
//        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //动画类型赋值
        animTypes = getIntent().getStringExtra("animTypes");

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof DelEditText || v instanceof NewNKeyBoardTextField || v instanceof SplitEditTextView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    KeyBordUntil.hideKeyBord2(this);
                    v.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * 跳转
     *
     * @param context
     * @param cls
     * @param animType 动画类型
     */
    public static void startDialogActivity(Activity context, Class<? extends BaseDialogActivity> cls, String animType) {
        startDialogActivity(context, cls, animType, new Intent());
    }

    /**
     * 跳转携带数据
     *
     * @param context
     * @param cls
     * @param animType 动画类型
     */
    public static void startDialogActivity(Activity context, Class<? extends BaseDialogActivity> cls, String animType, Intent intent) {
        if (context != null) {
            intent.setClass(context, cls);
            //Intent intent = new Intent(context, cls);
            intent.putExtra("animTypes", animType);
            context.startActivity(intent);
            if (ANIM_TYPE_BOTTOM.equals(animType) || ANIM_BOTTOM_IN_RIGHT_OUT.equals(animType)) {
                context.overridePendingTransition(R.anim.activity_dialog_in, 0);
            } else {
                context.overridePendingTransition(R.anim.slide_right_in, 0);
            }
        }
    }

    /**
     * 跳转
     *
     * @param context
     * @param cls
     * @param requestCode 请求code
     * @param animType    动画类型
     */
    public static void startDialogActivityWithRequestCode(Activity context, Class<? extends BaseDialogActivity> cls, String animType, Intent intent, int requestCode) {
        if (context != null) {
            if (intent == null) {
                intent = new Intent(context, cls);
            }
            intent.setClass(context, cls);
            intent.putExtra("animTypes", animType);
            context.startActivityForResult(intent, requestCode);
            if (ANIM_TYPE_BOTTOM.equals(animType) || ANIM_BOTTOM_IN_RIGHT_OUT.equals(animType)) {
                context.overridePendingTransition(R.anim.activity_dialog_in, 0);
            } else {
                context.overridePendingTransition(R.anim.slide_right_in, 0);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (ANIM_TYPE_BOTTOM.equals(animTypes)) {
            overridePendingTransition(0, R.anim.activity_dialog_out);
        } else if (ANIM_TYPE_RIGHT.equals(animTypes) || ANIM_BOTTOM_IN_RIGHT_OUT.equals(animTypes)) {
            overridePendingTransition(0, R.anim.slide_right_out);
        }
    }

    /**
     * 适配软键盘高度，调整rootView高度，使软键盘离demoView最小距离minDistanceWithDemoViewToSoft
     * 只适用于弹窗式窗口，且等待界面都完全显示出来了之后再调用此方法，才能计算出视图具体高度和坐标
     * post是因为可能此时View还没draw出来
     *
     * @param rootView                      最外层能调整高度View
     * @param demoView                      参照物View
     * @param minDistanceWithDemoViewToSoft 软键盘离demoView最小距离
     */
    protected void softAdapter(Activity activity, View rootView, View demoView, int minDistanceWithDemoViewToSoft, NewNKeyBoardTextField nKeyBoardTextField) {
        try {
            demoView.post(() -> {
                try {
                    int[] rootViewXy = new int[2];
                    int[] demoViewXy = new int[2];
                    rootView.getLocationOnScreen(rootViewXy);
                    demoView.getLocationOnScreen(demoViewXy);
                    int rootViewHeight = rootView.getHeight();
                    //计算出demoView距离rootView底部的距离，用于判断是否有足够软键盘弹出的空间，并且不遮挡demoView（保留minDistanceWithDemoViewToSoft最小高度的间隔）
                    int distanceWithDemoViewToRootViewBottom = rootViewXy[1] + rootViewHeight - demoViewXy[1] - demoView.getHeight() - minDistanceWithDemoViewToSoft;
                    final ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
                    SoftKeyBoardListenerUtil.setListener(activity, new SoftKeyBoardListenerUtil.OnSoftKeyBoardChangeListener() {
                        @Override
                        public void keyBoardShow(int height) {
                            try {
                                int diff = distanceWithDemoViewToRootViewBottom - height;
                                if (diff < 0) {
                                    layoutParams.height = rootViewHeight - diff;
                                    rootView.setLayoutParams(layoutParams);
                                }
                            } catch (Exception e) {
                                //try...catch防止rootView突然null
                            }
                        }

                        @Override
                        public void keyBoardHide(int height) {
                            try {
                                if (nKeyBoardTextField == null || !nKeyBoardTextField.keyboardIsShow()) {
                                    layoutParams.height = rootViewHeight;
                                    rootView.setLayoutParams(layoutParams);
                                }
                            } catch (Exception e) {
                                //try...catch防止rootView突然null
                            }
                        }
                    });
                    if (nKeyBoardTextField == null) {
                        return;
                    }
                    nKeyBoardTextField.setSoftInputMode(NKeyBoardTextField.SoftInputMode.SOFT_INPUT_NOT_THING);//这行代码是为了保证爱加密软键盘弹出时不把内容往上推
                    nKeyBoardTextField.setKeyBoardListener(new KeyBoardListener() {
                        @Override
                        public void onKey(int i) {

                        }

                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            try {
                                layoutParams.height = rootViewHeight;
                                rootView.setLayoutParams(layoutParams);
                            } catch (Exception e) {
                                //try...catch防止rootView突然null
                            }
                        }

                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            try {
                                double diff = distanceWithDemoViewToRootViewBottom - nKeyBoardTextField.getNKeyboardHigh();
                                if (diff >= 0) {
                                    layoutParams.height = rootViewHeight;
                                } else {
                                    layoutParams.height = (int) (rootViewHeight - diff);
                                }
                                rootView.setLayoutParams(layoutParams);
                            } catch (Exception e) {
                                //try...catch防止rootView突然null
                            }
                        }

                        @Override
                        public void onConfigLoadSucc() {

                        }

                        @Override
                        public boolean onCompleteKeyboardKeepShow() {
                            return false;
                        }
                    });
                } catch (Exception e) {
                    //
                }
            });
        } catch (Exception e) {
            //
        }
    }
}
