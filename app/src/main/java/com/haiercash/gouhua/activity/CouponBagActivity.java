package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.databinding.ActivityTabListBinding;
import com.haiercash.gouhua.tplibrary.PagePath;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：L14-14<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2021/12/14-16:35<br/>
 * 描    述：券包<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.ACTIVITY_COUPON_BAG)
public class CouponBagActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private ActivityTabListBinding binding;
    private Map<String, BaseFragment> fragmentMap = new HashMap<>();

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return binding = ActivityTabListBinding.inflate(inflater);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Activity已存活且复用才走这里，即scheme跳转且该activity已在前台展示
        if (intent != null && intent.hasExtra("tab")) {
            String tab="";
            if (getIntent() != null && getIntent().getData() != null) {
                tab = getIntent().getData().getQueryParameter("tab");
            }
            String index_scheme = TextUtils.isEmpty(tab) ? "0" : tab;//scheme跳转携带tab=1为展示免息券tab，其他默认
            int index = index_scheme.equals("1") ? 2 :1;
            initTabSelect(index);
        }
    }

    /**
     * @param index 为1则显示tab0（优惠券），为1则显示tab1（免息券）
     */
    private void initTabSelect(int index) {
        try {
            if (index == 1) {
                binding.rbType1.setChecked(true);
                setTabTextSize(R.id.rb_type1);
                initFragment(1);
            } else {
                binding.rbType2.setChecked(true);
                initFragment(2);
                setTabTextSize(R.id.rb_type2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("券包");
        binding.rbType1.setText("优惠券");
        binding.rbType2.setText("免息券");
        binding.rbType1.setOnCheckedChangeListener(this);
        binding.rbType2.setOnCheckedChangeListener(this);
        String tab="";
        if (getIntent() != null && getIntent().getData() != null) {
            tab = getIntent().getData().getQueryParameter("tab");
        }
        String index_scheme = TextUtils.isEmpty(tab) ? "0" : tab;//scheme跳转携带tab=1为展示免息券tab，其他默认
        int index = index_scheme.equals("1") ? 2 : getIntent().getIntExtra("couponType", 1);
        initTabSelect(index);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.getId() == R.id.rb_type1) {
                setTabTextSize(R.id.rb_type1);
                initFragment(1);//优惠券
            } else if (buttonView.getId() == R.id.rb_type2) {
                setTabTextSize(R.id.rb_type2);
                initFragment(2);//免息券
            }
        }
    }

    /**
     * 设置选中Tab字体大小跟加粗状态
     *
     * @param id
     */
    private void setTabTextSize(int id) {
        if (id == R.id.rb_type1) {
            binding.rbType1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            binding.rbType2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        } else if (id == R.id.rb_type2) {
            binding.rbType1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            binding.rbType2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }

    }

    /**
     * 加载fragment
     */
    private void initFragment(int position) {
        String tagKey = position == 1 ? PagePath.FRAGMENT_DISCOUNT_COUPON : PagePath.FRAGMENT_INTEREST_FREE_STAMPS;
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
}
