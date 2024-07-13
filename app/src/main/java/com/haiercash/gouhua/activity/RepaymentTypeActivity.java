package com.haiercash.gouhua.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.utils.json.JsonUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.reflect.TypeToken;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.adaptor.ProtacalPopAdapter;
import com.haiercash.gouhua.adaptor.RepayTypeAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.borrowmoney.LoanRat;
import com.haiercash.gouhua.beans.login.QueryAgreementListBean;
import com.haiercash.gouhua.databinding.ActivityRepaymentTypeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 9/23/22
 * @Version: 1.0
 */
public class RepaymentTypeActivity extends BaseActivity {

    private ActivityRepaymentTypeBinding binding;

    private RepayTypeAdapter mAdapter;
    private List<LoanRat> mList;

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {

        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        String loanRatStr = getIntent().getStringExtra("LoanRatList");
        if (!TextUtils.isEmpty(loanRatStr)) {
            mList = JsonUtils.fromJson(loanRatStr, new TypeToken<List<LoanRat>>() {
            }.getType());
        }
        if (mList == null) return;
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RepayTypeAdapter(mList);
        binding.rv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            LoanRat loanRat = mList.get(position);
            Intent intent = new Intent();
            intent.putExtra("loanRat", loanRat);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
    }


    @Override
    protected ActivityRepaymentTypeBinding initBinding(LayoutInflater inflater) {
        return binding = ActivityRepaymentTypeBinding.inflate(inflater);
    }


    @OnClick({R.id.layout_close})
    public void onClick(View v) {
        if (v.getId() == R.id.layout_close) {
            finish();
        }
    }

}
