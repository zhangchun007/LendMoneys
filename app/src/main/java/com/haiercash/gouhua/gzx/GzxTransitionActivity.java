package com.haiercash.gouhua.gzx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.utils.GlideUtils;

public class GzxTransitionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gzx_transition);
        ImageView ivLoading = findViewById(R.id.iv_loading);
        GlideUtils.loadDrawableSourceGif(this, R.drawable.gzx_loading, ivLoading);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = getIntent().getExtras();
                String url = bundle.getString("url");
                Intent intent = new Intent(GzxTransitionActivity.this, JsWebBaseActivity.class);
                intent.putExtra("jumpKey", url);
                startActivity(intent);
                finish();
            }
        }, 500);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {

    }
}