package com.haiercash.gouhua.fragments.main;

import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.EduApplyBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.PromoteLimitPopupWindow;
import com.haiercash.gouhua.utils.BrAgentUtils;
import com.haiercash.gouhua.utils.GhLocation;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.WyDeviceIdUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 提升额度页面
 */
@Route(path = PagePath.FRAGMENT_PROMOTE_LIMIT)
public class PromoteLimitFragment extends BaseFragment {
    @BindView(R.id.tv_limit_money)
    TextView tvLimitMoney;
    @BindView(R.id.tv_promot_limit)
    TextView tvPromotLimit;
    //private LocationUtils locationUtils;
    private GhLocation ghLocation;
    private PromoteLimitPopupWindow promoteLimitPopupWindow;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_promote_limit;
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle("提升额度");
        tvLimitMoney.setTypeface(FontCustom.getMediumFont(mActivity));
        tvLimitMoney.setText(CheckUtil.showNewThound(SpHp.getUser(SpKey.USER_EDU_ALL)));
        ghLocation = new GhLocation(mActivity, true, (isSuccess, reason) -> {
            if (isSuccess) { //提交订单
                requestApplyInfoAndRiskInfo();
            } else {
                showDialog(reason);
            }
        });
    }

    @OnClick(R.id.tv_confirm)
    public void viewOnClick() {
        ghLocation.requestLocation();
    }

    private void requestApplyInfoAndRiskInfo() {
        showProgress(true, "提交中...");
        //获取gid后上送
        BrAgentUtils.getBrAgentGid((afSwiftNumber, brObject) -> submitApplyInfoAndRiskInfo());
    }

    /**
     * 额度申请流程+风险采集
     */
    private void submitApplyInfoAndRiskInfo() {
        final Map<String, Object> map = new HashMap<>();
        map.put("custNo", SpHp.getUser(SpKey.USER_CUSTNO));
        map.put("flag", "0");
        map.put("deviceId", RSAUtils.encryptByRSA(SystemUtils.getDeviceID(mActivity)));
        map.put("creditIncrease", "02");//02 为提额申请 01 或 为空 是普通申请 客户端可在提额是传02 其余场景不传该参数
        map.put("listRiskMap", RiskInfoUtils.getAllRiskInfo(mActivity, ""));
        map.put("entryLabel", "HRGH-xjd");//标准化产品必填  目前前端固定传：HRGH-xjd
        //设置网易设备指纹数据
        WyDeviceIdUtils.getInstance().getWyDeviceIDTokenFromNative(AppApplication.CONTEXT, (token, code, msg) -> {
            if (!TextUtils.isEmpty(token)) {
                map.put("ydunToken",token);
            }
            netHelper.postService(ApiUrl.URL_APPLY_INFO_RISK_INFO, map, EduApplyBean.class);
        });
    }


    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.URL_APPLY_INFO_RISK_INFO.equals(url)) {
            EduApplyBean bean = (EduApplyBean) success;
            String applSeq = CheckUtil.deletePointZero(bean.applSeq);
            showProgress(false);
            RiskNetServer.startRiskServer(mActivity, "increase_credit_apply_submit", applSeq, 3);
            RiskInfoUtils.updateRiskInfoByNode("BR013", "YES", applSeq);
            RiskInfoUtils.send(mActivity, "提额申请", applSeq);
            //进行百融采集
            useBrAgent(applSeq);
            promoteLimitPopupWindow = new PromoteLimitPopupWindow(mActivity, applSeq, (view, flagTag, obj) -> onBackPressed());
            promoteLimitPopupWindow.showAtLocation(tvPromotLimit);
        }
    }


    /*百融风险信息采集*/
    private void useBrAgent(final String applSeq) {
        SpHp.deleteLogin(SpKey.USER_CRD_SEQ_RETURN);
        //百融风险采集，借款
        BrAgentUtils.lendInfoBrAgent(mActivity, (afSwiftNumber, brObject) -> {
            RiskInfoUtils.postBrOrBigData(mActivity, "lend", applSeq, brObject);
            RiskInfoUtils.requestRiskInfoBrAgentInfo(afSwiftNumber, "antifraud_lend", applSeq);
        });
    }


    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.URL_APPLY_INFO_RISK_INFO.equals(url)) {
            showProgress(false);
            if (error.getHead().retFlag.equals(NetConfig.SOCKET_TIMEOUT_EXCEPTION)) {
                showBtn2Dialog("系统异常，额度申请提交失败，请重新提交", "重新提交",
                        (dialog, which) -> requestApplyInfoAndRiskInfo()).setTitle(null);
            } else {
                showDialog(error.getHead().getRetMsg());
            }
            RiskNetServer.startRiskServer(mActivity, "increase_credit_apply_submit", "", 3);
            RiskInfoUtils.updateRiskInfoByNode("BR013", "NO");
        }
    }

    @Override
    public void onDestroyView() {
        if (ghLocation != null) {
            ghLocation.onDestroy();
        }
        if (promoteLimitPopupWindow != null) {
            promoteLimitPopupWindow.cancelDownTime();
        }
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {
        RxBus.getInstance().post(new ActionEvent(ActionEvent.MainRefreshHomePage, "true", "true"));
        mActivity.finish();
        return false;
    }
}
