package com.haiercash.gouhua.activity.login;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.WyDeviceIdUtils;
import com.haiercash.gouhua.wxapi.WxUntil;

import butterknife.BindView;

/**
 * @Description: 用activity写是因为如果用dialog写软键盘弹出会把dialog顶上去
 * @Author: zhangchun
 * @CreateDate: 12/3/22
 * @Version: 1.0
 */
public class ProtocolActivity extends BaseActivity {

    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_no)
    TextView tv_no;
    @BindView(R.id.tv_yes)
    TextView tv_yes;

    private String title;
    private String content;
    public static String TYPE_PROTOCOL = "type_protocol";
    public static String TYPE_WECHAT_PROTOCOL = "type_wechat_protocol";
    public static String TYPE_PERMISSION = "type_permission";
    private String type = TYPE_PROTOCOL;
    private String permissionName; //要申请的权限

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        type = getIntent().getStringExtra("type");
        permissionName = getIntent().getStringExtra("permissionName");
        if (!CheckUtil.isEmpty(permissionName)) {
            tv_yes.setText("开启");
            tv_no.setText("拒绝");
            if (permissionName.equals(Manifest.permission.READ_PHONE_STATE)) {
                SpHelper.getInstance().saveMsgToSp(SpKey.READ_PHONE_STATE, SpKey.LAST_SHOW_DIALOG_TIME, TimeUtil.calendarToString());
            }
        }

        tv_no.setOnClickListener(v -> {
            finish();
        });

        tv_yes.setOnClickListener(v -> {
            if (TYPE_PROTOCOL.equals(type)) {
                OneKeyLoginUtils.getInstance().setPrivacyCheck(true, this);
                OneKeyLoginUtils.getInstance().postPrivacyCheckBoxEvent(true, this);
                finish();

            } else if (TYPE_PERMISSION.equals(type)) {
                requestPermission(aBoolean -> {
                }, 0, permissionName);
                finish();

            } else if (TYPE_WECHAT_PROTOCOL.equals(type)){
                WxUntil.sendAuthForLogin(this, getIntent().getExtras());
                finish();
            }
        });

        if (TYPE_WECHAT_PROTOCOL.equals(type) || TYPE_PERMISSION.equals(type)) {
            tv_content.setText(content);
            tv_title.setText(title);
        } else {
            tv_content.setText(OneKeyLoginUtils.getInstance().setAgreement(this, "为了更好的给您提供服务，并保障您的合法权益，请您阅读并同意以下协议"));
            tv_title.setText("服务协议及隐私保护");
        }
        tv_content.setMovementMethod(LinkMovementMethod.getInstance());
        tv_content.setHighlightColor(Color.TRANSPARENT);

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_protacol_dialog;
    }

}

