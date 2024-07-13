package com.haiercash.gouhua.activity.comm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.fragments.FragmentController;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.CommomUtils;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/6/15<br/>
 * 描    述：Fragment 容器<br/>
 * 修订历史：入参：isShowTitle：true有title<br/>
 * ================================================================
 */
@Route(path = PagePath.ACTIVITY_CONTAINER)
public class ContainerActivity extends BaseActivity {
    public static final String ID_FRAGMENT = "id_fragment";
    public static final String FRAGMENT_SIMPLE_NAME = "FragmentSimpleName";
    String pageNameKey;
    public int mFragmentId;
    private boolean isFromPush;//push来的需要返回到首页

    @Override
    protected int getLayout() {
        return R.layout.a_container;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (!TextUtils.isEmpty(intent.getStringExtra("titles"))) {
            setTitle(intent.getStringExtra("titles"));
        }
        if (bd != null && bd.containsKey("isShowTitle")) {
            //设置无title
            setTitleVisibility(bd.getBoolean("isShowTitle"));
        }
        if (bd != null && bd.containsKey("fromPush")) {
            isFromPush = bd.getBoolean("fromPush", false);
            if (isFromPush) {
                ImageView head_left_close = findViewById(com.app.haiercash.base.R.id.head_left_close);
                head_left_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommomUtils.goHomePage(true);
                    }
                });
            }
        }
        pageNameKey = intent.getStringExtra(PagePath.PARAMETER_KEY);
        if (!CheckUtil.isEmpty(pageNameKey)) {
            currentSupportFragment = (BaseFragment) ARouterUntil.getInstance(pageNameKey).put(bd).navigation();
        } else {
            if (mFragmentId == 0) {
                mFragmentId = getIntent().getExtras() != null ? getIntent().getExtras().getInt(ID_FRAGMENT) : 0;
            }
            if (mFragmentId == 0) {
                String simpleName = getIntent().getStringExtra(FRAGMENT_SIMPLE_NAME);
                currentSupportFragment = FragmentController.obtainByName(simpleName, bd);
            } else {
                currentSupportFragment = FragmentController.obtain(mFragmentId, bd);
            }
        }
        initContainer();
    }

    public void initContainer() {
        if (currentSupportFragment == null) {
            System.out.println("-------------------》未找到你所需要的界面跳转");
            finish();
            return;
        }
        //GrowingIOUtils.setFragment(this, currentSupportFragment);
        replaceContent(R.id.content, currentSupportFragment);
    }

    public void addContent(int layoutId, Fragment bf) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(layoutId, bf);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentSupportFragment != null) {
            currentSupportFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        if (currentSupportFragment != null) {
            currentSupportFragment.onDestroy();
            currentSupportFragment = null;
        }
        super.onDestroy();
    }

    public void replaceContent(int layoutId, Fragment bf) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(layoutId, bf);
        ft.commit();
    }

    public static void to(Context context, int id) {
        Intent i = new Intent(context, ContainerActivity.class);
        i.putExtra(ID_FRAGMENT, id);
        context.startActivity(i);
    }

    public static void toForResult(Activity activity, int id, int requestCode) {
        toForResult(activity, id, null, requestCode);
    }

    public static void toForResult(Activity activity, int id, Bundle bd, int requestCode) {
        Intent i = new Intent(activity, ContainerActivity.class);
        if (bd != null) {
            i.putExtras(bd);
        }
        i.putExtra(ID_FRAGMENT, id);//可能部分页面存在相同的入参，故需要覆盖新入参
        activity.startActivityForResult(i, requestCode);
    }

    public static void toForResult(Fragment f, int id, Bundle bd, int requestCode) {
        Intent i = new Intent(f.getActivity(), ContainerActivity.class);
        if (bd != null) {
            i.putExtras(bd);
        }
        i.putExtra(ID_FRAGMENT, id);
        f.startActivityForResult(i, requestCode);
    }


    public static void to(Context context, int id, Bundle bd) {
        Intent i = new Intent(context, ContainerActivity.class);
        if (bd != null) {
            i.putExtras(bd);
        }
        i.putExtra(ID_FRAGMENT, id);
        context.startActivity(i);
    }

    public static void to(Context context, String classSimpleName, Bundle extra) {
        Intent i = new Intent(context, ContainerActivity.class);
        if (extra != null) {
            i.putExtras(extra);
        }
        i.putExtra(FRAGMENT_SIMPLE_NAME, classSimpleName);
        context.startActivity(i);
    }


    @Override
    public void onBackPressed() {
        if (((BaseFragment) currentSupportFragment).onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (((BaseFragment) currentSupportFragment).onBackPressed()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
