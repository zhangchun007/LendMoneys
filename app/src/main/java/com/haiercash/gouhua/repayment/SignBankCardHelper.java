package com.haiercash.gouhua.repayment;

import android.text.TextUtils;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.BankInfoBean;
import com.haiercash.gouhua.beans.bankcard.AddBankCardBean;
import com.haiercash.gouhua.beans.bankcard.BankSignStatus;
import com.haiercash.gouhua.beans.bankcard.RequestSignBean;
import com.haiercash.gouhua.beans.repayment.SignBankCardNeed;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Sun
 * Date :    2018/5/17
 * FileName: SignBankCardHelper
 * Description: 1，卡号信息查询
 * 2，
 */
public class SignBankCardHelper {

    private NetHelper netHelper;

    public SignBankCardHelper(Object callBack) {
        netHelper = new NetHelper(callBack);
    }

    /**
     * 查询卡号信息
     */
    public void requestCardInfo(String cardNo) {
        requestCardInfo(cardNo, SpHp.getUser(SpKey.USER_CUSTNAME));
    }


    /**
     * 查询卡号信息
     */
    public void requestCardInfo(String cardNo, String acctName) {
        Map<String, String> map = new HashMap<>();
        map.put("cardNo", RSAUtils.encryptByRSA(cardNo));
        map.put("acctName", RSAUtils.encryptByRSA(acctName));
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.URL_BUSINESS, map, BankInfoBean.class, true);
    }

    /**
     * 请求签约
     */
    public void requestSign(BankInfoBean bankInfo) {
        requestSign(bankInfo, SpHp.getUser(SpKey.USER_CERTNO), SpHp.getUser(SpKey.USER_CUSTNAME));
    }

    /**
     * 请求是否需要签约
     */
    public void requestNeedSign(String cardNo, int signType) {
        Map<String, String> map = new HashMap<>();
        map.put("cardNo", RSAUtils.encryptByRSA(cardNo));//卡号
        //签约类型 credit：申额,loan：支用,repay：主动还款
        map.put("type", signType == 1 ? "credit" : signType == 2 ? "loan" : signType == 3 ? "repay" : "");
        netHelper.postService(ApiUrl.URL_QUERY_NEED_SIGN, map, SignBankCardNeed.class);
    }

    /**
     * 请求签约
     */
    public void requestSign(BankInfoBean bankInfo, String certNo, String acctName) {
        Map<String, String> map = new HashMap<>();
        map.put("cardNo", RSAUtils.encryptByRSA(bankInfo.getCardNo()));//卡号
        map.put("interId", bankInfo.getInterId()); //签约渠道接口ID
        map.put("certNo", RSAUtils.encryptByRSA(certNo)); //身份证
        map.put("cardMobile", RSAUtils.encryptByRSA(bankInfo.getCardMobile())); //银行预留手机号
        map.put("acctName", RSAUtils.encryptByRSA(acctName));
        map.put("bankUnionCode", bankInfo.getBankCode());
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.URL_SIGNING, map, RequestSignBean.class);
    }

    /**
     * 银行卡签约
     */
    public void signBankCard(AddBankCardBean bean) {
        signBankCard(bean, SpHp.getUser(SpKey.USER_CUSTNO), SpHp.getUser(SpKey.USER_CUSTNAME), SpHp.getUser(SpKey.USER_CERTNO));
    }

    /**
     * 银行卡签约
     *
     * @param custNo   客户编号
     * @param custName 姓名
     * @param certNo   身份证号
     */
    private void signBankCard(AddBankCardBean bean, String custNo, String custName, String certNo) {
        if (TextUtils.isEmpty(custNo)) {
            custNo = SpHp.getUser(SpKey.USER_CUSTNO);//客户编号
        }
        if (TextUtils.isEmpty(custName)) {
            custName = SpHp.getUser(SpKey.USER_CUSTNAME);//姓名
        }
        if (TextUtils.isEmpty(certNo)) {
            certNo = SpHp.getUser(SpKey.USER_CERTNO);//身份证号
        }
        bean.setCustNo(RSAUtils.encryptByRSA(custNo));
        bean.setCustName(RSAUtils.encryptByRSA(custName));
        bean.setCertNo(RSAUtils.encryptByRSA(certNo));
        bean.setIsRsa("Y");
        netHelper.postService(ApiUrl.urlSaveBankCard, bean, null, true);
    }

    /**
     * 重新签约
     */
    public void reSignBankCard(AddBankCardBean bean) {
        bean.setCustNo(RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
        bean.setIsRsa("Y");
        netHelper.postService(ApiUrl.URL_RESIGN, bean, null, true);
    }

    /**
     * 调用银行卡快捷支付协议
     *
     * @param url        service返回的地址
     * @param cardMobile 手机号码
     * @param certNo     身份证号码
     */
    public static void gotoSignAgreement(BaseActivity context, String url, String cardMobile, String certNo) {
        url = url.replace("{appserver}", ApiUrl.API_SERVER_URL);
        url = url.replace("{cardMobile}", cardMobile);
        url = url.replace("{certNo}", certNo);
        WebSimpleFragment.WebService(context, url, "银行卡快捷支付协议");
    }


    /**
     * 代扣协议
     */
    public void gotoWithholding(BaseActivity context, String cardNum, String bankName, List<BankSignStatus> list) {
        StringBuilder url = new StringBuilder(ApiUrl.URL_WITHHOLDING_AGREEMENT);
        url.append("?username=").append(SpHp.getUser(SpKey.USER_CUSTNAME));
        url.append("&cardnum=").append(cardNum);
        url.append("&idnumber=").append(SpHp.getUser(SpKey.USER_CERTNO));
        url.append("&bankname=").append(bankName);
        if (list != null) {
            url.append("&seq=");
            int length = list.size();
            BankSignStatus status;
            for (int i = 0; i < length; i++) {
                status = list.get(i);
                url.append(status.getLoanNo());
                if (i < length - 1) {
                    url.append("，");
                }
            }
        }
        WebSimpleFragment.WebService(context, url.toString(), "银行卡扣款授权书二");
    }


}
