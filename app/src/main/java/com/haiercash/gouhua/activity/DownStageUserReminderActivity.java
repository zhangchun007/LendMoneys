package com.haiercash.gouhua.activity;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 降期用户提示，页面不能关闭，不能返回上一页面
 */
public class DownStageUserReminderActivity extends BaseActivity {
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.tv_down_des)
    TextView tvDownDes;
    @BindView(R.id.tv_open_bank)
    TextView tvOpenBank;
    @BindView(R.id.iv_state_error)
    ImageView ivStateError;

    @Override
    protected int getLayout() {
        return R.layout.activity_down_stage_user_reminder;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("温馨提示");
        setBarLeftImage(0, null);
        tvDownDes.setText(getAmountBuilder("*****"));
        tvAccountName.setText(getTransferAccountBuilder());
        tvAccountName.setMovementMethod(LinkMovementMethod.getInstance());
        tvAccount.setText(getAccountBuilder());
        tvAccount.setMovementMethod(LinkMovementMethod.getInstance());
        tvOpenBank.setText(getBankBuilder());
        tvOpenBank.setMovementMethod(LinkMovementMethod.getInstance());
        viewOnClick();
    }

    private SpannableStringBuilder getAmountBuilder(String amount) {
        return SpannableStringUtils.getBuilder(this, "您尚有").setBold()
                .append(amount).setForegroundColor(UiUtil.getColor(R.color.colorPrimary)).setBold()
                .append("元欠款未归还，请先结清欠款哦！").setBold()
                .append("您可将欠款转入如下账户结清欠款,在转账时备注您的姓名,身份证号,以便确认您的身份。").create();
    }

    private SpannableStringBuilder getTransferAccountBuilder() {
        return SpannableStringUtils.getBuilder(this, "转账账户名称:\n").setBold()
                .append("海尔消费金融有限公司  ")
                .append("  ").setResourceId(R.drawable.icon_copy_number, SpannableStringUtils.AlignImageSpan.CENTRE).setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        UiUtil.copyValueToShearPlate(mContext, "海尔消费金融有限公司");
                    }
                })
                .append("\n(海尔消费金融唯一指定还款账户)").create();
    }

    private SpannableStringBuilder getAccountBuilder() {
        return SpannableStringUtils.getBuilder(this, "账号：\n").setBold()
                .append("37150198551000000762  ")
                .append("  ").setResourceId(R.drawable.icon_copy_number, SpannableStringUtils.AlignImageSpan.CENTRE).setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        UiUtil.copyValueToShearPlate(mContext, "37150198551000000762");
                    }
                }).create();
    }

    private SpannableStringBuilder getBankBuilder() {
        return SpannableStringUtils.getBuilder(this, "开户行：\n").setBold()
                .append("中国建设银行股份有限公司青岛海尔路支行  ")
                .append("  ").setResourceId(R.drawable.icon_copy_number, SpannableStringUtils.AlignImageSpan.CENTRE).setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        UiUtil.copyValueToShearPlate(mContext, "中国建设银行股份有限公司青岛海尔路支行");
                    }
                }).create();
    }

    @OnClick(R.id.iv_state_error)
    public void viewOnClick() {
        String custNo = getIntent().getStringExtra("custNo");
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("custNo", EncryptUtil.simpleEncrypt(custNo));
        netHelper.getService(ApiUrl.URL_ALL_BILL_AMOUNT, map);
        ActivityUntil.finishOthersActivity(this.getClass());
    }

    @Override
    public void onSuccess(Object success, String url) {
        ivStateError.setVisibility(View.GONE);
        showProgress(false);
        Map<String, String> map = (Map<String, String>) success;
        if (map != null && map.containsKey("amount")) {
            tvDownDes.setText(getAmountBuilder(map.get("amount")));
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        //super.onError(error, url);
        showProgress(false);
        ivStateError.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //拦截返回键
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //只要是返回事件，直接返回true，表示消费掉
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
