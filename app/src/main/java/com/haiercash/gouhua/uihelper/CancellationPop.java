package com.haiercash.gouhua.uihelper;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.utils.AIServer;
import com.app.haiercash.base.utils.system.SpannableStringUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/2/10<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class CancellationPop extends BasePopupWindow {
    @BindView(R.id.tv_phone_server)
    TextView tvPhoneServer;

    public CancellationPop(BaseActivity context, Object data) {
        super(context, data);
    }

    @Override
    protected int getLayout() {
        return R.layout.pop_cancellation;
    }

    @Override
    protected void onViewCreated(Object data) {
        tvPhoneServer.setText(SpannableStringUtils.getBuilder(mActivity, "电话客服注销").append("\n")
                .append("9:00-18:00(含节假日)").setProportion(0.7125F)
                .setForegroundColor(Color.parseColor("#666666")).create());
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    @OnClick({R.id.tv_customer_service, R.id.tv_phone_server, R.id.iv_close})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_customer_service: //客服
                AIServer.showAiWebServer(mActivity, "注销账户");
                break;
            case R.id.tv_phone_server:
                CallPhoneNumberHelper.callServiceNumber(mActivity, mActivity.getString(R.string.about_us_phone_number),
                        "呼叫", "取消");
                break;
        }
        dismiss();
    }
}
