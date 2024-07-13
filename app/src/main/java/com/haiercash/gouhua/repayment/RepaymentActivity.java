package com.haiercash.gouhua.repayment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.HmacSHA256Utils;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.accountsettings.SetTransactionPwdActivity;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.InterestFreeBean;
import com.haiercash.gouhua.beans.repayment.CashierInfo;
import com.haiercash.gouhua.beans.repayment.PaymentResult;
import com.haiercash.gouhua.beans.repayment.Repayment;
import com.haiercash.gouhua.fragments.mine.CheckCertNoFragment;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.livedetect.FaceCheckActivity;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.CustomCodeView;
import com.haiercash.gouhua.wxapi.WxUntil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: Sun<p>
 * Date :    2018/5/12<p>
 * FileName: RepaymentActivity<p>
 * Description: 输入交易密码还款<p>
 */

public class RepaymentActivity extends BaseActivity {

    @BindView(R.id.view_pay)
    CustomCodeView payPassWord;
    private static final String TAG_BALANCE = "info";
    private static final String TAG_COUPON = "interestFreeCoupon";

    private CashierInfo mCashierInfo;
    //主动还款返回的数据集
    private List<PaymentResult> resultList;
    private boolean isStartWxPay = false;

    @Override
    protected int getLayout() {
        return R.layout.activity_repayment;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != payPassWord) {
            payPassWord.clearnData();
        }
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        SystemUtils.setWindowSecure(this);
        mCashierInfo = (CashierInfo) getIntent().getSerializableExtra(TAG_BALANCE);
        getEnterKey();
        WxUntil.regToWx(this, false);
        RxBus.getInstance().addSubscription(this, RxBus.getInstance().register(ActionEvent.class, actionEvent -> {
            if (actionEvent.getActionType() == ActionEvent.WxPayResult) {
                isStartWxPay = false;
                if ("SUCCESS".equals(actionEvent.getActionMsg())) {
                    showPaymentResult(null);
                } else if ("CANCEL".equals(actionEvent.getActionMsg())) {
                    UiUtil.toast("微信还款取消");
                } else if ("FAIL".equals(actionEvent.getActionMsg())) {
                    UiUtil.toast("微信还款失败");
                } else {
                    if (resultList != null && resultList.size() == 1) {
                        cancelOrder();
                    } else {
                        UiUtil.toastLongTime("微信支付失败");
                    }
                }
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStartWxPay) {
            showProgress(true);
            List<Map<String, String>> list = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("repayFormExceedingSeq", resultList.get(0).getRepayFormExceedingSeq());
            list.add(map);
            Map<String, Object> param = new HashMap<>();
            param.put("list", list);
            netHelper.postService(ApiUrl.URL_REPAYMENT_STATUS, param);
        }
    }

    /**
     * 输入交易密码还款
     */
    public static void gotoRepaymentActivity(BaseActivity activity, CashierInfo mCashierInfo, InterestFreeBean.RepayCouponsBean chooseCoupon) {
        Intent intent = new Intent(activity, RepaymentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG_BALANCE, mCashierInfo);
        bundle.putSerializable(TAG_COUPON, chooseCoupon);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * 确定键
     */
    private void getEnterKey() {
        payPassWord.setOnInputFinishedListener(password -> {
            if (TextUtils.isEmpty(password)) {
                System.out.println("输入信息为空。。。");
            } else if (password.length() != 6) {
                showDialog("请输入正确的交易密码");
            } else {
                checkPayPassWord(payPassWord.getCurrentWord());
            }
        });
      /*  numBerKeyBoard.setOnOkClick(new NumBerKeyBoard.OnOkClick() {
            @Override
            public void onOkClick() {
                String password = payPassWord.getPassWord().getText().toString();
                if (TextUtils.isEmpty(password)) {
                    showDialog("请输入密码");
                } else if (password.length() != 6) {
                    showDialog("请输入正确的交易密码");
                } else {
                    //为了防止重复点击
                    if (!mRefreshing.compareAndSet(false, true)) {
                        return;
                    }
                    checkPayPassWord(password);
                }
            }
        });*/
    }

    //验证交易密码是否正确
    private void checkPayPassWord(String password) {
        String numPhone = SpHp.getLogin(SpKey.LOGIN_USERID);//用户名
        if (CheckUtil.isEmpty(numPhone)) {
            showDialog("账号异常，请退出重试");
            return;
        }
        String deviceId = SystemUtils.getDeviceID(this);
        if (CheckUtil.isEmpty(deviceId)) {
            showDialog("deviceId获取失败");
            return;
        }
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("userId", EncryptUtil.simpleEncrypt(numPhone));//用户账号
        map.put("payPasswd", password);//交易密码
        map.put("deviceId", RSAUtils.encryptByRSA(deviceId));
        //必须放在map最后一行，是对整个map参数进行签名对
        map.put("sign", HmacSHA256Utils.buildNeedSignValue(map));
        netHelper.getService(ApiUrl.URL_CHECK_PAY_SECRET, map, null, true);
    }

    /**
     * 取消订单支付
     */
    private void cancelOrder() {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("repaySeq", EncryptUtil.simpleEncrypt(resultList.get(0).getRepaySeq()));
        netHelper.getService(ApiUrl.URL_CANCEL_REPAYMENT, map);
    }

    /**
     * 主动还款
     */
    private void repaymentSubmit() {
        Map<String, Object> map = new HashMap<>();
        List<Repayment> list = mCashierInfo.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map mapBean = JsonUtils.fromJson(JsonUtils.toJson(list.get(i)), Map.class);
                //必须放在map最后一行，是对整个map参数进行签名对
                String signValue = HmacSHA256Utils.buildNeedSignValue(mapBean);
                Repayment repayment = list.get(i);
                repayment.setSign(signValue);
            }
        }
        map.put("list", list);
        netHelper.postService(ApiUrl.URL_Active_Repayment, map);
    }


    @Override
    @OnClick({R.id.iv_back, R.id.tv_forget_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                KeyBordUntil.hideKeyBord(this);
                finish();
                break;
            case R.id.tv_forget_password:
                KeyBordUntil.hideKeyBord(this);
                Bundle bundle = new Bundle();
                bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
                bundle.putSerializable(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                bundle.putString(SetTransactionPwdActivity.TAG, "WJJYMM");
                ContainerActivity.to(this, CheckCertNoFragment.class.getSimpleName(), bundle);
                break;
        }
    }

    /**
     * 跳转至结果页
     */
    private void showPaymentResult(ArrayList<PaymentResult> list) {
        InThePaymentFragment.toRePayMentResult(mContext, mCashierInfo.getStayAmount(), list);
        RepaymentConfirmActivity repaymentActivity = ActivityUntil.findActivity(RepaymentConfirmActivity.class);
        if (repaymentActivity != null) {
            repaymentActivity.finish();
        }
        this.finish();
    }

    @Override
    public void onSuccess(Object response, String flag) {
        super.onSuccess(response, flag);
        switch (flag) {
            case ApiUrl.URL_CHECK_PAY_SECRET:
                postPassEvent("true", "");
                repaymentSubmit();
                break;
            case ApiUrl.URL_Active_Repayment:
                showProgress(false);
                resultList = JsonUtils.fromJsonArray(response, "list", PaymentResult.class);
                if (mCashierInfo.getList() != null) {
                    //判断是否为微信支付：微信支付只支持单笔、单期支付
                    if (mCashierInfo.getList().size() == 1 && "02".equals(mCashierInfo.getList().get(0).getRepayWay())) {
                        if (!WxUntil.isReady(this)) {
                            cancelOrder();
                            UiUtil.toast("未安装微信，请安装后再进行支付");
                            return;
                        }
                        isStartWxPay = WxUntil.WxPayment(this, resultList.get(0).getWxPayExtMap());
                        if (isStartWxPay) {
                            Logger.d("正常启动SDK进行支付:进入 WXPayEntryActivity");
                        } else {
                            Logger.d("启动SDK失败，主动进行订单取消");
                            cancelOrder();
                        }
                        return;
                    }
                }
                showPaymentResult((ArrayList<PaymentResult>) resultList);
                break;
            case ApiUrl.URL_CANCEL_REPAYMENT:
                showProgress(false);
                UiUtil.toast("微信还款失败，请重新操作");
                break;
            case ApiUrl.URL_REPAYMENT_STATUS:
                showProgress(false);
                List<PaymentResult> list = JsonUtils.fromJsonArray(response, "list", PaymentResult.class);
                if (isStartWxPay) {
                    String repaySts = list.get(0).getRepaySts();
                    if ("01".equals(repaySts)) {
                        showDialog("支付结果处理中，请勿重复还款~");
                    } else if ("02".equals(repaySts)) {
                        showPaymentResult(null);
                    } else if ("03".equals(repaySts)) {
                        Logger.d("还款失败:用户可在收银台继续还款。");
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (url.equals(ApiUrl.URL_CHECK_PAY_SECRET)) {
            showProgress(false);
            Map<String, Double> map = (Map<String, Double>) error.getBody();
            if (map != null && map.containsKey("payErrorNumber") && map.containsKey("maxPayErrorNumber")) {//交易密码验证错误次数
                postPassEvent("false", "交易密码输入错误");
                int num = Double.valueOf(map.get("maxPayErrorNumber")).intValue() - Double.valueOf(map.get("payErrorNumber")).intValue();
                if (Double.valueOf(map.get("payErrorNumber")).intValue() <= 5) {
                    //1.用户输入前5次密码错误
                    showBtn2Dialog("交易密码输入错误", "重新输入", (dialog, which) -> {
                        //清空并重新输入
                        payPassWord.clearnData();
                    }).setButtonTextColor(2, R.color.colorPrimary);
                } else if (Double.valueOf(map.get("payErrorNumber")).intValue() == 6 || Double.valueOf(map.get("payErrorNumber")).intValue() == 7) {
                    //2、用户输入第6/7次密码错误时，提示用户重新输入
                    showDialog("交易密码输入错误,\n   还可以输入" + num + "次", "忘记密码", "重新输入", (dialog, which) -> {
                        if (which == 1) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
                            bundle.putSerializable(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                            bundle.putString(SetTransactionPwdActivity.TAG, "WJJYMM");
                            ContainerActivity.to(mContext, CheckCertNoFragment.class.getSimpleName(), bundle);
                            payPassWord.clearnData();
                        } else {
                            //清空并重新输入
                            payPassWord.clearnData();
                        }
                    }).setStandardStyle(4);
                }
            } else {
                postPassEvent("false", "交易密码输入错误次数过多");
                //3、当用户第8次输入错误时，提示用户交易密码锁定，24小时后尝试，仅提供找回密码入口
                showDialog("交易密码输入错误次数过多,您\n的账号已被锁定,请点击忘记密\n码进行找回或明日重试", "忘记密码", "我知道了", (dialog, which) -> {
                    if (which == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
                        bundle.putSerializable(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                        bundle.putString(SetTransactionPwdActivity.TAG, "WJJYMM");
                        ContainerActivity.to(mContext, CheckCertNoFragment.class.getSimpleName(), bundle);
                        payPassWord.clearnData();
                    } else {
                        payPassWord.clearnData();
                        dialog.dismiss();
                    }
                }).setStandardStyle(4);
            }
        } else if (ApiUrl.URL_Active_Repayment.equals(url)) {
            super.onError(error, url);
        } else {
            super.onError(error, url);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected String getPageCode() {
        return "CheckstandTransactionPasswordPage";
    }

    @Override
    protected void onDestroy() {
        RxBus.getInstance().unSubscribe(this);
        super.onDestroy();
    }

    //验证密码事件
    private void postPassEvent(String success, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("button_name", "确定");
        UMengUtil.commonCompleteEvent("TransactionPasswordConfirmed_Click", map, success, failReason, getPageCode());
    }

    //免息券使用成功与否还款友盟埋点
    private void postCouponUmEvent(String isSuccess, String failReason) {
        try {
            RepaymentUmHelper.postUmClickEvent((InterestFreeBean.RepayCouponsBean) getIntent().getSerializableExtra(TAG_COUPON),
                    mCashierInfo.getStayAmount(), mCashierInfo.getList().get(0).getLoanNo(), isSuccess, failReason, getPageCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
