package com.haiercash.gouhua.fragments.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.haiercash.base.net.token.TokenHelper
import com.app.haiercash.base.utils.router.ARouterUntil
import com.app.haiercash.base.utils.router.ActivityUntil
import com.app.haiercash.base.utils.system.CheckUtil
import com.app.haiercash.base.utils.system.FontCustom
import com.app.haiercash.base.utils.system.SystemUtils
import com.haiercash.gouhua.MainActivity
import com.haiercash.gouhua.R
import com.haiercash.gouhua.activity.ScanQrCodeActivity
import com.haiercash.gouhua.activity.login.SmsWayLoginActivity
import com.haiercash.gouhua.adaptor.bean.LoanMarketBean
import com.haiercash.gouhua.adaptor.loan.LoanMarketAdapter
import com.haiercash.gouhua.base.ApiUrl
import com.haiercash.gouhua.base.AppApplication
import com.haiercash.gouhua.base.BaseFragment
import com.haiercash.gouhua.beans.homepage.HomeQuotaBean
import com.haiercash.gouhua.databinding.FragmentLoanMarketBinding
import com.haiercash.gouhua.gzx.GzxAgreementActivity
import com.haiercash.gouhua.gzx.GzxTransitionActivity
import com.haiercash.gouhua.interfaces.LoginCallbackC
import com.haiercash.gouhua.interfaces.SpKey
import com.haiercash.gouhua.jsweb.JsWebBaseActivity
import com.haiercash.gouhua.tplibrary.PagePath
import com.haiercash.gouhua.utils.GlideUtils
import com.haiercash.gouhua.utils.SpHp
import com.haiercash.gouhua.utils.UMengUtil
import java.io.Serializable

class LoanMarketFragment : BaseFragment() {

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoanMarketBinding {
        return FragmentLoanMarketBinding.inflate(inflater, container, false)
    }

    private lateinit var loanMarketAdapter: LoanMarketAdapter

    private fun getBinding(): FragmentLoanMarketBinding {
        return _binding as FragmentLoanMarketBinding
    }

    override fun initEventAndData() {
        val loanMarketList =
            arguments?.getParcelableArrayList<LoanMarketBean>("LoanMarketData")
        if (loanMarketList == null || loanMarketList.size == 0) {
            return
        }
        val loanMarketBean = loanMarketList?.get(0)
        loanMarketList?.remove(loanMarketBean)
        loanMarketAdapter = LoanMarketAdapter(loanMarketList)
        initCardView(loanMarketBean)
        getBinding().rvOrganizationList.layoutManager = LinearLayoutManager(context)
        getBinding().rvOrganizationList.adapter = loanMarketAdapter
        loanMarketAdapter.addChildClickViewIds(R.id.tv_apply)
        loanMarketAdapter.setOnItemChildClickListener { adapter, view, position ->
            val loanMarketBean = loanMarketList?.get(position)
            jumpToLoanMarket(loanMarketBean)
        }

        getBinding().ivScan.setOnClickListener {
            AppApplication.setLoginCallbackTodo(object : LoginCallbackC() {
                override fun onLoginSuccess() {
                    jumpToScanQrCode()
                }
            })
        }

        getBinding().ivMessage.setOnClickListener {
            AppApplication.setLoginCallbackTodo(object : LoginCallbackC() {
                override fun onLoginSuccess() {
                    jumpToMessage()
                }
            })
        }

        getBinding().srlRefresh.setOnRefreshListener {
            loadHomeData()
        }
        UMengUtil.commonExposureEvent("LoanMarketPage_Exposure", "贷超首页", "", "");
    }

    private fun initCardView(loanMarketBean: LoanMarketBean?) {
        GlideUtils.loadFit(context, getBinding().ivLogo, loanMarketBean?.organizationIcon)
        getBinding().tvOrganizationName.text = loanMarketBean?.organizationName
        getBinding().tvQuotaCardMoney.text = loanMarketBean?.creditAmount
        getBinding().tvQuotaCardMoney.typeface = FontCustom.getDinFont(context)
        getBinding().tvSubmit.setOnClickListener {
            jumpToLoanMarket(loanMarketBean)
        }
    }

    private fun jumpToLoanMarket(loanMarketBean: LoanMarketBean?) {
        if (loanMarketBean?.specialParam == "GDZ") {
            val intent = Intent()
            val bundle = Bundle()
            bundle.putString("url", loanMarketBean.specialData.gzxUrl)
            if (!CheckUtil.isEmpty(loanMarketBean.specialData.agreementList)) {
                bundle.putSerializable(
                    "agreementList",
                    loanMarketBean.specialData.agreementList as Serializable
                )

                intent.putExtras(bundle)
                GzxAgreementActivity.startDialogActivity(
                    activity,
                    GzxAgreementActivity::class.java,
                    SmsWayLoginActivity.ANIM_BOTTOM_IN_RIGHT_OUT,
                    intent
                )
            } else {
                intent.setClass(context!!, GzxTransitionActivity::class.java)
                intent.putExtras(bundle)
                activity?.startActivity(intent)
            }
            val map = mutableMapOf<String, Any>("firm_name" to "够多智")
            UMengUtil.commonClickEvent("LoanMarketPage_Item_Click", "我要借钱", map, "")

        } else {
            val intent = Intent()
            intent.setClass(mActivity, JsWebBaseActivity::class.java)
            intent.putExtra("jumpKey", loanMarketBean?.applyUrl)
            mActivity.startActivity(intent)
            if (loanMarketBean != null) {
                val map = mutableMapOf<String, Any>("firm_name" to loanMarketBean.organizationName)
                UMengUtil.commonClickEvent("LoanMarketPage_Item_Click", "我要借钱", map, "")
            }
        }
    }

    private fun jumpToScanQrCode() {
        startActivity(Intent(mActivity, ScanQrCodeActivity::class.java))
    }

    private fun jumpToMessage() {
        SpHp.saveSpLogin(SpKey.NOTICE_POINT_OPERATE, "0")
        ARouterUntil.getContainerInstance(PagePath.FRAGMENT_MESSAGE).navigation()
    }

    private fun finishRefresh() {
        getBinding().srlRefresh.finishRefresh()
    }

    private fun loadHomeData() {

        if (AppApplication.isLogIn()) {
            val map: MutableMap<String, String> = HashMap()
            map["deviceId"] = SystemUtils.getDeviceID(activity)
            map["h5token"] = TokenHelper.getInstance().h5Token
            map["processId"] = TokenHelper.getInstance().h5ProcessId
            netHelper.postService<Map<String, String>>(
                ApiUrl.POST_HOME_CREDIT_INFO, map,
                HomeQuotaBean::class.java
            )
        } else {
            MainEduBorrowUntil.INSTANCE(mActivity)
                .postHomePageButtonEvent("QuotaElement_Exposure", "未登录-立即登录")
        }

    }

    override fun <T : Any?> onSuccess(response: T, url: String?) {
        showProgress(false)
        finishRefresh()
        if (ApiUrl.POST_HOME_CREDIT_INFO == url) {
            val homeQuotaBean: HomeQuotaBean = response as HomeQuotaBean
            if (!homeQuotaBean.credit.status.equals("M")) {
                showDialog("页面加载有些小问题，您可以尝试后台退出app后重新打开")
                return
            }

            val loanExcess = homeQuotaBean.loanExcess
            val loanMarketBean = loanExcess[0]
            loanExcess.remove(loanMarketBean)
            loanMarketAdapter.setNewData(loanExcess)
            initCardView(loanMarketBean)
        }
    }


}