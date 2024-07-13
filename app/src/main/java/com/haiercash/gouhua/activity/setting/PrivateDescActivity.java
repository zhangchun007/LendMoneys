package com.haiercash.gouhua.activity.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.login.QueryAgreementListBean;
import com.haiercash.gouhua.databinding.ActivityPrivateDescBinding;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设置-隐私说明
 */
public class PrivateDescActivity extends BaseActivity {
    private ActivityPrivateDescBinding mPrivateDescBinding;
    //用户隐私协议url
    private String userPrivacyUrl = "";
    private String otherShareUrl = "";
    private String infoCollectUrl = "";

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return mPrivateDescBinding = ActivityPrivateDescBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.setting_private_desc);
        userPrivacyUrl = ApiUrl.API_SERVER_URL + getString(R.string.about_us_privacy_protocols);
        otherShareUrl = ApiUrl.API_SERVER_URL + getString(R.string.other_share_protocols);
        infoCollectUrl = ApiUrl.API_SERVER_URL + getString(R.string.info_collect_protocols);
        mPrivateDescBinding.llPrivateProtocol.setOnClickListener(this);
        mPrivateDescBinding.llOtherShareList.setOnClickListener(this);
        mPrivateDescBinding.llInfoCollectList.setOnClickListener(this);
        mPrivateDescBinding.tvWithdrawPrivacy.setOnClickListener(this);
        showProgress(true);
        getAgreementList();
    }

    @Override
    public void onClick(View v) {
        if (v == mPrivateDescBinding.llPrivateProtocol) {//隐私政策
            WebSimpleFragment.WebService(this, userPrivacyUrl, getString(R.string.private_desc_private_protocol), WebSimpleFragment.STYLE_OTHERS);
        } else if (v == mPrivateDescBinding.llOtherShareList) {//第三方共享清单
            WebSimpleFragment.WebService(this, otherShareUrl, getString(R.string.private_desc_other_share_list), WebSimpleFragment.STYLE_OTHERS);
        } else if (v == mPrivateDescBinding.llInfoCollectList) {//个人信息收集清单
            WebSimpleFragment.WebService(this, infoCollectUrl, getString(R.string.private_desc_info_collect_list), WebSimpleFragment.STYLE_OTHERS);
        } else if (v == mPrivateDescBinding.tvWithdrawPrivacy) {//撤销隐私权
            showDialog("撤销同意隐私政策", "当您撤回同意《隐私政策》后，您将退出登录，仅能进入浏览页面，我们将无法继续为您提供应用内信贷业务等相关功能，即您无法在应用上进一步使用额度申请、借款及消费等功能。您确认撤销后，需要您手动关闭应用后台运行进程，以确保后台运行终止请求获取相关信息。\n" +
                    "特别说明：\n" +
                    "1、如您处于授信审批、放款中或借款待还状态时，为了避免逾期，导致金融机构上报至央行个人征信，影响您的个人生活，将不支持《隐私政策》同意的撤回，请您结清后再进行操作。\n" +
                    "\n" +
                    "2、虽然您撤回同意《隐私政策》，但您已填写的资料以及提供的信息，以及已经获取的信贷服务资格、优惠权益等将不受影响。", "确认撤销", "返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 1) {
                        showProgress(true);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("userId", AppApplication.userid);
                        map.put("haierDeviceId", SystemUtils.getDeviceID(mContext));
                        netHelper.postService(ApiUrl.POST_REPEAL_AGREEMENT, map);
                    }
                }
            }).setStandardStyle(2).setContentViewHeight(180);
        }
        else {
            super.onClick(v);
        }
    }

    /**
     * 获取协议列表----隐私说明
     */
    private void getAgreementList() {
        HashMap<String, String> map = new HashMap<>();
        map.put("sceneType", "bill_collect");//场景入参-隐私说明
        netHelper.postService(ApiUrl.URL_GET_QUERY_AGREEMENT_LIST, map);
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (response == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        showProgress(false);
        if (ApiUrl.URL_GET_QUERY_AGREEMENT_LIST.equals(flag)) {
            List<QueryAgreementListBean> agreementListBeanList = JsonUtils.fromJsonArray(response, QueryAgreementListBean.class);
            for (int i = 0; i < agreementListBeanList.size(); i++) {
                QueryAgreementListBean bean = agreementListBeanList.get(i);
                if (bean == null) {
                    continue;
                }
                if (!TextUtils.isEmpty(bean.getContUrl())) {
                    String url;
                    if (bean.getContUrl().startsWith("http")) {
                        url = bean.getContUrl();
                    } else {
                        url = ApiUrl.API_SERVER_URL + bean.getContUrl();
                    }
                    if ("PrivacyAgreement".equals(bean.getContType())) {
                        //隐私协议
                        userPrivacyUrl = url;
                    } else if ("CommonBill".equals(bean.getContType())) {
                        otherShareUrl = url;
                    } else if ("CollectionAndUse".equals(bean.getContType())) {
                        infoCollectUrl = url;
                    }
                }
            }
        } else if (ApiUrl.POST_REPEAL_AGREEMENT.equals(flag)) {
            if (!CheckUtil.isEmpty(AppApplication.userid)) {
                showProgress(true);
                Map<String, String> logoutMap = new HashMap<>();
                logoutMap.put("userId", EncryptUtil.simpleEncrypt(AppApplication.userid));
                logoutMap.put("h5Token", TokenHelper.getInstance().getH5Token());
                netHelper.postService(ApiUrl.LOGOUT_URL, logoutMap);
            } else {
                clearStateAndGoMain();
            }
        } else if (ApiUrl.LOGOUT_URL.equals(flag)) {
            clearStateAndGoMain();
        }
    }

    //清除相应的登录状态,设置标志位并回到首页
    private void clearStateAndGoMain() {
        SpHp.saveSpOther(SpKey.OTHER_TOURIST_MODE, "Y");
        CommomUtils.clearSp();
        RxBus.getInstance().post(new ActionEvent(ActionEvent.MainFragmentReset));
        RxBus.getInstance().post(new ActionEvent(ActionEvent.REFRESHUSERINFO));
        MainHelper.backToMainHome();
    }
}