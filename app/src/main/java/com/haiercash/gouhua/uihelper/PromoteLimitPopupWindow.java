package com.haiercash.gouhua.uihelper;

import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.ProcessBean;
import com.haiercash.gouhua.interfaces.OnPopClickListener;
import com.haiercash.gouhua.network.NetHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 提额弹窗
 */
public class PromoteLimitPopupWindow extends BasePopupWindow implements INetResult {
    @BindView(R.id.tv_top_limit_des)
    TextView tvTopLimitDes;
    @BindView(R.id.tv_limit_des)
    TextView tvLimitDes;
    @BindView(R.id.tv_promot_limit_money)
    TextView tvPromotLimitMoney;
    @BindView(R.id.iv_limit_load_or_load)
    ImageView ivLimitLoadOrLoad;
    @BindView(R.id.tv_limit_comite)
    TextView tvLimitComite;
    private long countTime = 10 * 1000;
    private long spaceTime = 1000; //间隔时间
    private NetHelper netHelper;
    private String applSeq;
    private boolean finnishTag = false;
    private OnPopClickListener onPopClickListener;

    public PromoteLimitPopupWindow(BaseActivity context, Object data, OnPopClickListener onPopClickListener) {
        super(context, data);
        this.onPopClickListener = onPopClickListener;
    }

    @Override
    protected int getLayout() {
        return R.layout.pop_promote_limit;
    }

    @Override
    protected void onViewCreated(Object data) {
        netHelper = new NetHelper(this);
        applSeq = CheckUtil.deletePointZero(String.valueOf(data));
        tvTopLimitDes.setText("正在审批中…");
        tvLimitDes.setText("请您耐心等待");
        tvPromotLimitMoney.setVisibility(View.GONE);
        ivLimitLoadOrLoad.setVisibility(View.VISIBLE);
        GlideUtils.loadDrawableSourceGif(mActivity, R.drawable.img_promote_limit_load, ivLimitLoadOrLoad);
    }

    private void failStatueShow() {
        tvTopLimitDes.setText("您暂时无法申请提额");
        tvLimitDes.setText("额度调整是根据借款情况综合评估,\n请保持良好的借款和还款记录");
        tvPromotLimitMoney.setVisibility(View.GONE);
        ivLimitLoadOrLoad.setVisibility(View.VISIBLE);
        ivLimitLoadOrLoad.setImageResource(R.drawable.img_limit_fail);
        tvLimitComite.setText("返回首页");
        tvLimitComite.setEnabled(true);
        tvLimitComite.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_limit_radius_2175f4));
    }

    @Override
    public void showAtLocation(View view) {
        /*开始倒计时*/
        countDownTimer.start();
        tvLimitComite.setEnabled(false);
        tvLimitComite.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_limit_radius_bbb));

        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    @OnClick(R.id.tv_limit_comite)
    public void viewClick(View view) {
        dismiss();
        onPopClickListener.onViewClick(view, -1, null);
    }

    private CountDownTimer countDownTimer = new CountDownTimer(countTime, spaceTime) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvLimitComite.setText(UiUtil.getStr("返回首页(", millisUntilFinished / 1000, "s)"));
            if (new Long(millisUntilFinished / 1000).intValue() % 2 == 0) {
                reloadCheckResult();
            }
        }

        @Override
        public void onFinish() {
            setBackHome();
        }
    };

    private void setBackHome() {
        tvLimitComite.setText("返回首页");
        tvLimitComite.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_limit_radius_2175f4));
        tvLimitComite.setEnabled(true);
    }

    /**
     * 提额查询结果
     */
    private synchronized void reloadCheckResult() {
        if (finnishTag) {
            return;
        }
        if (CheckUtil.isEmpty(applSeq)) {
            mActivity.showProgress(false);
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("applSeq", applSeq);
        netHelper.getService(ApiUrl.URL_PROCESS, params, ProcessBean.class);
    }

    public void cancelDownTime() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public <T> void onSuccess(T response, String url) {
        if (ApiUrl.URL_PROCESS.equals(url)) {
            mActivity.showProgress(false);
            ProcessBean data = (ProcessBean) response;
            if (data != null && "02".equals(data.appStatus)) {//成功
                finnishTag = true;
                cancelDownTime();
                tvTopLimitDes.setText("恭喜您 提额成功");
                tvLimitDes.setText("目前您的总额度为");
                tvPromotLimitMoney.setVisibility(View.VISIBLE);
                ivLimitLoadOrLoad.setVisibility(View.GONE);
                tvPromotLimitMoney.setText(CheckUtil.showNewThound(CheckUtil.formattedAmount(data.getApplyAmt())));
                setBackHome();
            } else if (data != null && "03".equals(data.appStatus)) {//失败
                finnishTag = true;
                cancelDownTime();
                failStatueShow();
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        mActivity.showProgress(false);
        if (error.getHead().retFlag.equals(NetConfig.SOCKET_TIMEOUT_EXCEPTION)) {
            mActivity.showDialog(error.getHead().getRetMsg());
        }
    }
}
