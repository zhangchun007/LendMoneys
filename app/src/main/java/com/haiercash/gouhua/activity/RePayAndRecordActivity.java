package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.widget.ListEmptyOrErrorView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/1/8<br/>
 * 描    述：还款/借还记录<br/>
 * PageType:RECORD_TYPE->借还记录<br/>
 * ================================================================
 */
public class RePayAndRecordActivity extends BaseActivity {
    @BindView(R.id.rb_type1)
    RadioButton rb_type1;
    @BindView(R.id.rb_type2)
    RadioButton rb_type2;
    @BindView(R.id.ll_root)
    LinearLayout llRoot;

    @BindView(R.id.fl_content)
    FrameLayout fl_content;
    private Map<String, BaseFragment> fragmentMap = new HashMap<>();
    private int tabRePayListFragment;

    @Override
    protected int getLayout() {
        return R.layout.activity_tab_list;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {//还款结果页跳转
            tabRePayListFragment = intent.getIntExtra("tabRePayListFragment", -1);
        }
        initView();
    }


    private void initView() {
        setTitle("借还记录");
        rb_type1.setText("借款记录");
        rb_type2.setText("还款记录");
        String custNo = SpHp.getUser(SpKey.USER_CUSTNO);
        if (TextUtils.isEmpty(custNo)) {
            llRoot.removeAllViews();
            llRoot.setVisibility(View.GONE);
            ListEmptyOrErrorView view = new ListEmptyOrErrorView(this);
            view.setEmptyData(R.drawable.img_empty_bill, getString(R.string.empty_record));
            fl_content.addView(view);
            return;
        }
        if (tabRePayListFragment == 2) {
            rb_type1.setChecked(false);
            rb_type2.setChecked(true);
            setTabTextSize(R.id.rb_type1);
            initFragment(2);
        } else {
            rb_type1.setChecked(true);
            rb_type2.setChecked(false);
            setTabTextSize(R.id.rb_type2);
            initFragment(1);
        }
    }

    @OnCheckedChanged({R.id.rb_type1, R.id.rb_type2})
    public void checkChangListener(CompoundButton compoundButton, boolean b) {
        if (b) {
            if (compoundButton.getId() == R.id.rb_type1) {
                setTabTextSize(R.id.rb_type1);
                initFragment(1);//借款记录
            } else if (compoundButton.getId() == R.id.rb_type2) {
                setTabTextSize(R.id.rb_type2);
                initFragment(2);//还款记录
            }
        }
    }


    /**
     * 加载fragment
     */
    private void initFragment(int position) {
        String tagKey = position == 1 ? PagePath.ORDER_RECORD_LIST : PagePath.ORDER_REPAY_LIST;
        BaseFragment fragment;
        if (!fragmentMap.containsKey(tagKey)) {
            fragment = (BaseFragment) ARouterUntil.getInstance(tagKey).navigation();
            fragmentMap.put(tagKey, fragment);
        } else {
            fragment = fragmentMap.get(tagKey);
        }
        if (fragment != null) {
            changeFragment(R.id.fl_content, fragment);
        }
    }


    /**
     * 设置选中Tab字体大小跟加粗状态
     *
     * @param id
     */
    private void setTabTextSize(int id) {
        if (id == R.id.rb_type1) {
            rb_type1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            rb_type2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        } else if (id == R.id.rb_type2) {
            rb_type1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            rb_type2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }

    }


}
