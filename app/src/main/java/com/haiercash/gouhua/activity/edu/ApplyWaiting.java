package com.haiercash.gouhua.activity.edu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.app.haiercash.base.BaseApplication;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.EduApplyBean;
import com.haiercash.gouhua.beans.risk.RiskBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.utils.BrAgentUtils;
import com.haiercash.gouhua.utils.GhLocation;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.RiskKfaUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.WyDeviceIdUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Sun
 * Date :    2019/1/4
 * FileName: ApplyWaiting
 * Description:
 */
public class ApplyWaiting extends BaseActivity {

    private String applSeq;
    private RiskBean riskBean;

    @Override
    protected int getLayout() {
        return R.layout.activity_applywaiting;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        applSeq = SpHp.getLogin(SpKey.USER_CRD_SEQ_RETURN);
        applSeq = CheckUtil.deletePointZero(applSeq);
        requestLocation();
    }

    /**
     * 进行定位
     */
    private void requestLocation() {
        showProgress(true);
        new GhLocation(this, (isSuccess, reason) -> {
            if (isSuccess) {
                //提交订单
                requestApplyInfoAndRiskInfo();
            } else {
                showProgress(false);
                showDialog(reason);
            }
        }).requestLocation();
    }

    /**
     * 请求风险信息
     */
    private void requestApplyInfoAndRiskInfo() {
        showProgress(true, "提交中...");
        //获取gid后上送
        BrAgentUtils.getBrAgentGid((afSwiftNumber, brObject) -> submitApplyInfoAndRiskInfo());
    }


    /**
     * 额度申请流程+风险采集
     */
    private void submitApplyInfoAndRiskInfo() {
        try {
            RiskKfaUtils.getRiskBean(this, 0, 3, "credit_apply_submit_success", obj -> riskBean = (RiskBean) obj);
        } catch (Exception e) {
            e.printStackTrace();
            UiUtil.toastDeBug("Exception  submitApplyInfoAndRiskInfo  " + e.getMessage());
        }
        String custNo = SpHp.getUser(SpKey.USER_CUSTNO);
        final Map<String, Object> map = new HashMap<>();
        map.put("custNo", custNo);
        if (!CheckUtil.isEmpty(applSeq)) {
            map.put("flag", "2");
            map.put("applSeq", applSeq);
        } else {
            map.put("flag", "0");
        }
        map.put("deviceId", RSAUtils.encryptByRSA(SystemUtils.getDeviceID(this)));
        map.put("listRiskMap", RiskInfoUtils.getAllRiskInfo(this, applSeq));
        map.put("entryLabel", "HRGH-xjd");//标准化产品必填  目前前端固定传：HRGH-xjd
        //设置网易设备指纹数据
        WyDeviceIdUtils.getInstance().getWyDeviceIDTokenFromNative(AppApplication.CONTEXT, (token, code, msg) -> {
            if (!TextUtils.isEmpty(token)) {
                map.put("ydunToken", token);
            }
            netHelper.postService(ApiUrl.URL_APPLY_INFO_RISK_INFO, map, EduApplyBean.class);
        });
    }


    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.URL_APPLY_INFO_RISK_INFO.equals(url)) {
            EduApplyBean bean = (EduApplyBean) success;
            //进行百融采集
            applSeq = CheckUtil.deletePointZero(bean.applSeq);
            //RiskNetServer.startRiskServer(this, "credit_apply_submit_success", applSeq, 3);
            RiskNetServer.startService(this, riskBean, applSeq);
            showProgress(false);
            RiskInfoUtils.updateRiskInfoByNode("BR013", "YES", applSeq);
            RiskInfoUtils.send(this, "额度申请", applSeq);
            useBrAgent(applSeq);
            Intent intent = new Intent(this, EduProgressActivity.class);
            intent.putExtra("applSeq", applSeq);
            startActivity(intent);
            finish();
        }
    }


    /*百融风险信息采集*/
    private void useBrAgent(final String applSeq) {
        SpHp.deleteLogin(SpKey.USER_CRD_SEQ_RETURN);
        //百融风险采集，借款
        BrAgentUtils.lendInfoBrAgent(this, (afSwiftNumber, brObject) -> {
            RiskInfoUtils.postBrOrBigData(mContext, "lend", applSeq, brObject);
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
            RiskInfoUtils.updateRiskInfoByNode("BR013", "NO", applSeq);
        }
    }
}
