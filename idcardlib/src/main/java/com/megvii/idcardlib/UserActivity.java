package com.megvii.idcardlib;

import static android.os.Build.VERSION_CODES.M;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.megvii.idcardlib.utils.Configuration;
import com.megvii.idcardquality.IDCardQualityAssessment;
import com.megvii.idcardquality.IDCardQualityLicenseManager;
import com.megvii.licensemanager.Manager;

public class UserActivity extends Activity implements View.OnClickListener {

    private TextView mBuildVersion, build_info;
    private RadioButton mRbVertical, mRbHorizontal, mRbFront, mRbBack;
    private IDCardQualityLicenseManager mIdCardLicenseManager;
    private Button mBtStart;
    private static final int INTO_IDCARDSCAN_PAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBuildVersion = (TextView) findViewById(R.id.build_version);
        build_info = (TextView) findViewById(R.id.build_info);
        mBtStart = (Button) findViewById(R.id.bt_start);
        mRbVertical = (RadioButton) findViewById(R.id.rb_v);
        mRbHorizontal = (RadioButton) findViewById(R.id.rb_h);
        mRbFront = (RadioButton) findViewById(R.id.rb_front);
        mRbBack = (RadioButton) findViewById(R.id.rb_back);


        mBtStart.setOnClickListener(this);

        mBuildVersion.setText("版本号：" + new IDCardQualityAssessment().getVersion());

        //调用build info信息，仅做演示
        build_info.setText("build info：" + new IDCardQualityAssessment().getBuildInfo());

        requestCameraPerm();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_start) {//1、初始化配置
            initConfig();


            //2、请求授权信息
            startGetLicense();
        }

    }


    private void initConfig() {
        if (mRbVertical.isChecked()) {
            Configuration.setIsVertical(this, true);
        } else {
            Configuration.setIsVertical(this, false);
        }

        if (mRbFront.isChecked()) {
            Configuration.setCardType(this, 1);
        } else {
            Configuration.setCardType(this, 2);
        }

    }

    public void startGetLicense() {
        mIdCardLicenseManager = new IDCardQualityLicenseManager(
                UserActivity.this);

        long status = 0;
        try {
            status = mIdCardLicenseManager.checkCachedLicense();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (status > 0) {//大于0，已授权或者授权未过期
            Intent intent = new Intent(UserActivity.this, IDCardScanActivity.class);
            UserActivity.this.startActivityForResult(intent, INTO_IDCARDSCAN_PAGE);
            Toast.makeText(UserActivity.this, "授权成功", Toast.LENGTH_SHORT).show();

        } else { //需要重新授权
            Toast.makeText(UserActivity.this, "没有缓存的授权信息，开始授权", Toast.LENGTH_SHORT).show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getLicense();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }

    }

    private void getLicense() {
        //参数2 是否使用ipv6
        Manager manager = new Manager(UserActivity.this, true);
        manager.registerLicenseManager(mIdCardLicenseManager);

        String uuid = Configuration.getUUID(UserActivity.this);
        String authMsg = mIdCardLicenseManager.getContext(uuid);
        manager.takeLicenseFromNetwork(authMsg);
        if (mIdCardLicenseManager.checkCachedLicense() > 0) {//大于0，已授权或者授权未过期
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(UserActivity.this, IDCardScanActivity.class);
                    UserActivity.this.startActivityForResult(intent, INTO_IDCARDSCAN_PAGE);
                    Toast.makeText(UserActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void requestCameraPerm() {
        if (Build.VERSION.SDK_INT >= M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA},
                        100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //@TODO 权限通过后的回调
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTO_IDCARDSCAN_PAGE && resultCode == RESULT_OK) {
            Intent intent = new Intent(UserActivity.this, ResultActivity.class);
            intent.putExtra("portraitimg_bitmap", data.getByteArrayExtra("portraitimg_bitmap"));
            intent.putExtra("idcardimg_bitmap", data.getByteArrayExtra("idcardimg_bitmap"));
            startActivity(intent);
        }
    }
}
