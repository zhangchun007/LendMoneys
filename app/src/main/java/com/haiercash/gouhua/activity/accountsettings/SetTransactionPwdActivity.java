package com.haiercash.gouhua.activity.accountsettings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.edu.EduCommon;
import com.haiercash.gouhua.activity.edu.PerfectInfoActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.postputbean.Shezhizhifumima;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.CustomCodeView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/16
 * 描    述：设置交易密码
 * 修订历史：
 * ================================================================
 */
public class SetTransactionPwdActivity extends BaseActivity {
    public static final String TAG = "setTransactionPwdTag";
    private ArrayList<Class> classes; //支用时候资料不足，需要的后续流程
    @BindView(R.id.tv_set_password)
    TextView mSet;
    @BindView(R.id.password_set_transaction)
    CustomCodeView payPassWordView;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    /**
     * tag标识
     * XGJYMM:修改交易密码
     * WJJYMM:忘记交易密码
     * EDJH:额度激活
     * XJD ：现金贷
     */
    private String pageTag = "XGJYMM";

    @Override
    protected int getLayout() {
        return R.layout.activity_set_transaction_password;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        SystemUtils.setWindowSecure(this);
        pageTag = getIntent().getStringExtra("tag");
        classes = (ArrayList<Class>) getIntent().getSerializableExtra("followStep");
        if (!TextUtils.isEmpty(pageTag) && "EDJH".equals(pageTag)) {
            setTitle("额度申请");
            setRightImage(R.drawable.iv_blue_details, v -> ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation());
        } else {
            pageTag = getIntent().getStringExtra(TAG);
        }
        if (!TextUtils.isEmpty(pageTag) && "XGJYMM".equals(pageTag)) {
            setTitle("修改交易密码");
        }
        boolean borrowStep = getIntent().getBooleanExtra("borrowStep", false);
        if (classes != null || borrowStep) {
            setTitle("完善信息");
        }
        mSet.setTypeface(FontCustom.getMediumFont(this));
        mSet.setEnabled(false);
        payPassWordView.setOnInputFinishedListener(onInputFinishedListener);
    }


    @OnClick(R.id.tv_set_password)
    public void viewOnClick(View v) {
        if (v.getId() == R.id.tv_set_password) {
            UMengUtil.commonClickEvent("TransactionPasswordSet_Click_gouhua", "设置", getPageCode());
            savePayPwd();
        }
    }

    /**
     * 请求数据:设置交易密码
     */
    private void savePayPwd() {
        Log.e("-------->", "开始传递设置交易密码数据");

        if (payPassWordView.getPassWord() != null
                && payPassWordView.getPassWord().getText().length() != 6) {
            UiUtil.toast("请输入完整的交易密码");
            return;
        }
        //用户名
        String numPhone = SpHp.getLogin(SpKey.LOGIN_USERID);
        String passwor_zhifu = CheckUtil.clearBlank(payPassWordView.getCurrentWord());//交易密码

        Shezhizhifumima map = new Shezhizhifumima();
        if (CheckUtil.isEmpty(numPhone)) {
            showDialog("账号异常，请退出重试");
            return;
        }
        if ("XGJYMM".equals(pageTag)) {
            map.setVerifyPayPassword("Y");
        } else {
            map.setVerifyPayPassword("N");
        }
        map.setUserId(EncryptUtil.simpleEncrypt(numPhone));
        map.setPayPasswd(passwor_zhifu);
        map.setPromotionChannel(SystemUtils.metaDataValueForTDChannelId(this));
        map.setAreaCode(SpHelper.getInstance().readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_CITYCODE));
        map.setSixNum("1");
        netHelper.putService(ApiUrl.url_zhifumimashezhi_put, map);
        showProgress(true);
    }

    @Override
    public void onSuccess(Object success, String url) {
        showProgress(false);
        UiUtil.toast("设置成功");
        SpHelper.getInstance().saveMsgToSp(SpKey.STATE, SpKey.STATE_HASPAYPAS, "Y");
        if ("WJJYMM".equals(pageTag)) {
            RiskNetServer.startRiskServer1(this, "reset_transaction_password_complete", "", 0);
            //RiskInoSendHelper.send(this, "重置交易密码");
            RiskInfoUtils.updateRiskInfoByNode("BR08", "YES");
            finish();
            return;
        } else if ("XGJYMM".equals(pageTag)) {
            RiskNetServer.startRiskServer1(this, "change_transaction_password_complete", "", 0);
            //RiskInoSendHelper.send(this, "修改交易密码");
            RiskInfoUtils.updateRiskInfoByNode("BR05", "YES");
            finish();
            return;
        } else if ("SZJYMM".equals(pageTag)) {//安全设置-设置交易密码
            RiskNetServer.startRiskServer1(this, "transaction_password_set_success", "", 0);
            RiskInfoUtils.updateRiskInfoByNode("BR03", "YES");
            finish();
            return;
        } else if ("EDJH".equals(pageTag)) {//额度申请中第一次设置交易密码
            RiskNetServer.startRiskServer1(this, "transaction_password_set_success", "", 0);
            //RiskInoSendHelper.send(this, "设置交易密码");
            RiskInfoUtils.updateRiskInfoByNode("BR03", "YES");
        }
        ///* ***该段代码目测无效*** */
        //String name = BorrowPayPwdActivity.class.getSimpleName();
        //if (name.equals(pageTag)) {
        //    ActivityUntil.finishOthersActivity(MainActivity.class, BorrowPayPwdActivity.class);
        //} else {
        //    ActivityUntil.finishOthersActivity(MainActivity.class);
        //}
        if (classes == null) {
            ActivityUntil.finishOthersActivity(MainActivity.class);
        }
        /* ***该段代码目测无效*** */
        if ("EDJH".equals(pageTag) || (classes != null && classes.size() != 0)) {
            Intent intent = new Intent(this, PerfectInfoActivity.class);
            intent.putExtra("tag", "EDJH");
            if (classes != null && (classes.get(0).getSimpleName().equals("PerfectInfoActivity"))) {
                intent.putExtra(PerfectInfoActivity.ID, PerfectInfoActivity.class);
                intent.putExtra("followStep", classes);
            }
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        showProgress(false);
        if ("WJJYMM".equals(pageTag)) {
            RiskInfoUtils.updateRiskInfoByNode("BR08", "NO");
            super.onError(error, url);
        } else if ("XGJYMM".equals(pageTag)) {
            RiskInfoUtils.updateRiskInfoByNode("BR05", "NO");
            String errorMsg = error == null || error.getHead() == null ? NetConfig.DATA_PARSER_ERROR : error.getHead().getRetMsg();
            showDialog("提示", errorMsg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (payPassWordView != null) {
                        payPassWordView.clearnData();
                    }
                }
            });
        } else if ("SZJYMM".equals(pageTag)) {
            RiskInfoUtils.updateRiskInfoByNode("BR03", "NO");
            super.onError(error, url);
        } else {
            super.onError(error, url);
        }
    }

    private CustomCodeView.OnInputFinishedListener onInputFinishedListener = new CustomCodeView.OnInputFinishedListener() {
        @Override
        public void onInputFinished(String password) {
            if (TextUtils.isEmpty(password)) {
                mSet.setEnabled(false);
            } else {
                mSet.setEnabled(true);
            }
        }
    };

    @Override
    public void onBackPressed() {
        if ("EDJH".equals(pageTag) && classes == null) {
            EduCommon.onBackPressed(this, "要设置密码", getPageCode(), "设置交易密码页面");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected String getPageCode() {
        return "TransactionPasswordPage_gouhua";
    }
}