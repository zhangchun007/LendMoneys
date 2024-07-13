package com.haiercash.gouhua.uihelper;

import android.net.Uri;
import android.os.Bundle;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.CreditLifeBorrowBean;
import com.haiercash.gouhua.fragments.CreditApplyDetailFragment;
import com.haiercash.gouhua.fragments.CreditApplyWebFragment;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.utils.SpHp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Author: Sun<br/>
 * Date :    2019/3/5<br/>
 * FileName: CreditLifeHelp<br/>
 * Description:<br/>
 */
public class CreditLifeHelp implements INetResult {

    private NetHelper netHelper;
    private CreditLifeBorrowBean creditLife;
    private BaseActivity mContext;

    public CreditLifeHelp(BaseActivity context, CreditLifeBorrowBean creditLifeBorrowBean) {
        netHelper = new NetHelper(this);
        creditLife = creditLifeBorrowBean;
        mContext = context;
    }

    /**
     * 分发是否联合登陆
     */
    public void dispatchUniteLogin() {
        //记录打开
        addOpenHistory(null);
        if ("N".equals(creditLife.getIsUniteLogin())) { //是否是联合登录
            // WebSimpleFragment.WebService(mContext, creditLife.getJumpLink(), creditLife.getChannelName(), WebSimpleFragment.STYLE_OTHERS);
            CreditApplyWebFragment.OpenWebFragment(mContext, creditLife.getJumpLink(), creditLife.getChannelName(), WebSimpleFragment.STYLE_OTHERS, null);
            if (!(mContext instanceof MainActivity)) {
                mContext.finish();
            }
        } else {
            getProductRecord();
        }
    }

    /**
     * 记录打开
     */
    public void addOpenHistory(Map<String, String> maps) {
        if (CheckUtil.isEmpty(maps)) {
            //调用用户操作记录接口
            Map<String, String> map = new HashMap<>();
            map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
            map.put("channelName", creditLife.getChannelName());
            map.put("operationName", "贷超导流");
            netHelper.postService(ApiUrl.ADD_USER_RECORD, map);
        } else {
            maps.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
            maps.put("channelName", creditLife.getChannelName());
            maps.put("operationName", "贷超导流提交");
            netHelper.postService(ApiUrl.ADD_USER_RECORD, maps);
        }
    }

    /**
     * 获取指定产品的申请记录信息
     */
    public void getProductRecord() {
        mContext.showProgress(true);
        Map<String, String> map = new HashMap<>();
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        map.put("userId", EncryptUtil.simpleEncrypt(userId));
        map.put("productId", CheckUtil.isEmpty(creditLife.getId()) ? creditLife.getProductId() : creditLife.getId());
        netHelper.getService(ApiUrl.GET_QUERYRECORD, map);
    }

    /**
     * 新增第三方记录
     */
    public void addApplyRecord() {
        String userID = SpHp.getLogin(SpKey.LOGIN_USERID);
        Map<String, String> map = new HashMap<>();
        map.put("userId", userID);
        map.put("productId", CheckUtil.isEmpty(creditLife.getId()) ? creditLife.getProductId() : creditLife.getId());
        netHelper.postService(ApiUrl.ADD_APPLY_RECOCRD, map);
    }

    /**
     * 获取联合登陆信息
     */
    public void getUniteLoginInfo() {
        mContext.showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("channelName", creditLife.getChannelName());
        netHelper.getService(ApiUrl.SIGN_DATA_FOR_UNITELOGIN, map);
    }


    @Override
    public <T> void onSuccess(T t, String url) {
        mContext.showProgress(false);
        if (ApiUrl.GET_QUERYRECORD.equals(url)) {
            dispatchRecord((Map<String, String>) t);
        } else if (ApiUrl.SIGN_DATA_FOR_UNITELOGIN.equals(url)) {
            urlAddSignData((Map<String, String>) t);
        }
    }

    /**
     * 打开联合登陆后的h5页面
     */
    private void urlAddSignData(Map<String, String> map) {
        String signData = "";
        if (map != null && !CheckUtil.isEmpty(map.get("signmainData"))) {
            signData = map.get("signData");
        }
        Uri.Builder builder = Uri.parse(creditLife.getJumpLink()).buildUpon();
        builder.appendQueryParameter("signData", signData);
        CreditApplyWebFragment.OpenWebFragment(mContext, builder.toString(), creditLife.getChannelName(), WebSimpleFragment.STYLE_OTHERS, creditLife);
        if (!(mContext instanceof MainActivity)) {
            mContext.finish();
        }
    }


    /**
     * 分发联合登陆状态
     */
    private void dispatchRecord(Map<String, String> map) {
        if (map == null
                || CheckUtil.isEmpty(map.get("hasRecord"))
                || !"Y".equals(map.get("hasRecord"))) {
            //是联合登录跳原生详情页面
            Bundle bundle = new Bundle();
            bundle.putSerializable("creditLifeBorrowBean", creditLife);
            ContainerActivity.toForResult(mContext, CreditApplyDetailFragment.ID, bundle, 0);
            if (!(mContext instanceof MainActivity)) {
                mContext.finish();
            }
        } else {
            getUniteLoginInfo();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        mContext.onError(error, url);
    }

    /**
     * 解析url
     */
    public Map<String, String> getParameters2(String url) {
        Uri parse = Uri.parse(url);
        Map<String, String> params = new HashMap<>();
        if (url == null || "".equals(url.trim())) {
            return params;
        }
        Set<String> queryParameterNames = parse.getQueryParameterNames();
        for (String key : queryParameterNames) {
            params.put(key, parse.getQueryParameter(key));
        }
        return params;
    }

    /**
     * 解析url
     */
    public Map<String, String> getParameters1(String url) {
        Map<String, String> params = new HashMap<>();
        if (url == null || "".equals(url.trim())) {
            return params;
        }
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String[] split = url.split("[?]");
        if (split.length == 2 && !"".equals(split[1].trim())) {
            String[] parameters = split[1].split("&");
            if (parameters != null && parameters.length != 0) {
                for (String parameter : parameters) {
                    if (parameter != null && parameter.trim().contains("=")) {
                        String[] split2 = parameter.split("=");
                        //split2可能为1，可能为2
                        if (split2.length == 1) {
                            //有这个参数但是是空的
                            params.put(split2[0], "");
                        } else if (split2.length == 2) {
                            if (!"".equals(split2[0].trim())) {
                                params.put(split2[0], split2[1]);
                            }
                        }
                    }
                }
            }
        }
        return params;
    }


    /**
     * 手动断开
     */
    public void destory() {
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        mContext = null;
        if (creditLife != null) {
            creditLife = null;
        }
    }
}
