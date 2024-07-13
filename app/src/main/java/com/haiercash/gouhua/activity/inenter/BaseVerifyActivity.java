package com.haiercash.gouhua.activity.inenter;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.login.VersionInfo;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.uihelper.VersionHelper;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/6/11<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public abstract class BaseVerifyActivity extends BaseActivity {
    @BindView(R.id.user_logo)
    ImageView mImgUserLogo;//用户头像
    @BindView(R.id.tv_hello)
    TextView tvHello;//问候语
    @BindView(R.id.text_phone)
    TextView tvPhone;
    protected TextView mTextTip;//密码输入错误/正确
    @BindView(R.id.text_other_account)
    TextView mTextOther;

    protected String userId;
    /**
     * 用于标记是锁屏进来的还是从启动页进入的
     */
    protected String pageTag;
    private long touchTime = 0;
    private VersionHelper mVersionHelper;

    @Override
    protected int getLayout() {
        return R.layout.activity_base_verify;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        pageTag = getIntent().getStringExtra("tag");
        setHelloText();
        userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        String loginMobile = SpHp.getLogin(SpKey.LOGIN_MOBILE);
        if (!CheckUtil.isEmpty(loginMobile)) {
            tvPhone.setText(UiUtil.getStr(loginMobile.substring(0, 3), "****", loginMobile.substring(loginMobile.length() - 4)));
        }
        mTextTip = findViewById(R.id.text_tip);
        ViewStub vsContent = findViewById(R.id.vs_content);
        vsContent.setLayoutResource(getContentResId());
        vsContent.inflate();
        GlideUtils.loadHeadPortrait(this, ApiUrl.urlCustImage + "?userId=" + EncryptUtil.simpleEncrypt(userId), mImgUserLogo, true);
        mVersionHelper = new VersionHelper(this, mImgUserLogo, onVersionBackListener);
        mVersionHelper.startCheckVersionService();
    }

    /**
     * 显示规则：4：00—12：00上午好，12：00—20：00下午好，20：00—4：00晚上好
     */
    private void setHelloText() {
        Calendar c = Calendar.getInstance();//
        int mHour = c.get(Calendar.HOUR_OF_DAY);//时
        tvHello.setText(mHour >= 4 && mHour < 12 ? "上午好" : mHour >= 12 && mHour < 20 ? "下午好" : "晚上好");
    }

    protected abstract int getContentResId();

    /**
     * 检测版本更新：选择了取消、或不需要更新
     */
    protected abstract void versionCancel();

    /**
     * 设置所有View都隐藏
     */
    protected void goneAllView() {
        mImgUserLogo.setVisibility(View.GONE);
        tvHello.setVisibility(View.GONE);
        tvPhone.setVisibility(View.GONE);
        mTextTip.setVisibility(View.GONE);
        mTextOther.setVisibility(View.GONE);
    }

    //子类复写，页面名称，埋点用
    protected String getPageName() {
        return "";
    }

    @OnClick({R.id.text_other_account})
    public void viewOnClick(View view) {
        if (!CheckUtil.isEmpty(getPageCode())) {
            UMengUtil.commonClickEvent(this instanceof VerifyBiometricActivity ? "FflpOtherlogin_Click" : "GppOtherlogin_Click", "其他登录方式", getPageName(), getPageCode());
        }
        LoginSelectHelper.staticToGeneralLogin();
    }

    private VersionHelper.OnVersionBackListener onVersionBackListener = new VersionHelper.OnVersionBackListener() {
        @Override
        public void onVersionBack(int status, Object response) {
            showProgress(false);
            switch (status) {
                case VersionHelper.VERSION_CHECKED_ERROR_NO_BLOCK:
                case VersionHelper.VERSION_SUCCEED_CANCLE:
                case VersionHelper.VERSION_SUCCEED_NOUPDATE:
                    versionCancel();
                    break;
                case VersionHelper.VERSION_CHECKED_ERROR:
                    onError((BasicResponse) response, null);
                    break;
                case VersionHelper.VERSION_CHECKED_SUCC:
                    mVersionHelper.showNewUpdateDialog(BaseVerifyActivity.this, (VersionInfo) response);
                    break;
                case VersionHelper.VERSION_SUCCEED_UPDATE:
                    showProgress(true, "正在下载新版本,请稍候");
                    break;
                default:
                    break;
            }
        }
    };

    /*屏蔽返回键*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ("lock".equals(pageTag)) {
                long currentTime = System.currentTimeMillis();
                if ((currentTime - touchTime) >= 2000) {
                    UiUtil.toast("再按一次退出程序");
                    touchTime = currentTime;
                } else {
                    ActivityUntil.finishOthersActivity();
                    finish();
                    UMengUtil.onKillProcess();
                    System.exit(0);
                }
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mVersionHelper != null) {
            mVersionHelper.clearnHelper();
            mVersionHelper = null;
        }
        super.onDestroy();
    }
}
