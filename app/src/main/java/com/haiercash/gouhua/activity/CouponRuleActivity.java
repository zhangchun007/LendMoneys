package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.databinding.PopSimpleInfoBinding;

public class CouponRuleActivity extends BaseActivity {
    private PopSimpleInfoBinding binding;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        binding = PopSimpleInfoBinding.inflate(inflater, null, false);
        return binding;
    }

    public static void startCouponRuleActivity(BaseActivity activity, int height,CharSequence rule, int requestCode) {
        Intent intent = new Intent(activity, CouponRuleActivity.class);
        intent.putExtra("rule", rule);
        intent.putExtra("height", height);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        binding.tvPopTitle.setText("使用规则");
        int height = getIntent().getIntExtra("height", 0);
        if (height != 0) {
            binding.ivBack.setVisibility(View.VISIBLE);//pop左箭头是否需要显示
            ViewGroup.LayoutParams params = binding.rlRoot.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = height;
            binding.rlRoot.setLayoutParams(params);
        } else {
            binding.ivBack.setVisibility(View.INVISIBLE);
        }
        CharSequence rule = getIntent().getCharSequenceExtra("rule");
        binding.tvContent.setText(rule == null || rule.length() == 0 ? "暂无说明" : SpannableStringUtils.getBuilder(this, rule).create());
        binding.tvClose.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.tvOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == binding.ivBack) {//关闭当前pop
            finish();
        } else if (v == binding.tvClose) {//关闭当前pop的同时也关掉上一页面
            Intent intent = new Intent();
            intent.putExtra("closeThis", true);
            setResult(RESULT_CANCELED, intent);
            finish();
        } else if (v == binding.tvOk) {//确定按钮
            finish();
        }
    }
}