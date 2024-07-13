package com.haiercash.gouhua.utils;

import android.content.Intent;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.activity.ChooseDischargeLoanActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.OpenDischargeBean;
import com.haiercash.gouhua.network.NetHelper;

/**
 * 结清证明util
 */
public class DischargeUtil implements INetResult {
    private final NetHelper netHelper;
    private final BaseActivity activity;


    public DischargeUtil(BaseActivity context) {
        this.activity = context;
        netHelper = new NetHelper(context, this);
    }

    //对应节点检查是否有弹窗
    public void getDischarge() {
        activity.showProgress(true);
        netHelper.postService(ApiUrl.POST_DISCHARGE_LIST, null, OpenDischargeBean.class);
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        activity.showProgress(false);
        OpenDischargeBean oDBean = (OpenDischargeBean) t;
        if (!CheckUtil.isEmpty(oDBean)) {
            if ("Y".equals(oDBean.getHasOdLoan())) {
                activity.showDialog(oDBean.getOdLoanMsg());
            } else if ("N".equals(oDBean.getHasSettlementLoan())) {
                activity.showDialog(oDBean.getSettlementLoanMsg());
            } else {
                Intent intent = new Intent(activity, ChooseDischargeLoanActivity.class);
                intent.putExtra("dischargeBean", oDBean);
                activity.startActivity(intent);
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        activity.onError(error, url);
    }
}
