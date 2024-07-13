package com.haiercash.gouhua.uihelper;

import android.content.Intent;

import com.app.haiercash.base.bean.ArrayBean;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.RepaymentDetailsNewActivity;
import com.haiercash.gouhua.activity.accountsettings.SetTransactionPwdActivity;
import com.haiercash.gouhua.activity.edu.NameAuthIdCardActivity;
import com.haiercash.gouhua.activity.edu.NameAuthIdCardPatchActivity;
import com.haiercash.gouhua.activity.edu.PerfectInfoActivity;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.CheckIfMsgCompleteBean;
import com.haiercash.gouhua.beans.getpayss.LoanCoupon;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.livedetect.FaceCheckActivity;
import com.haiercash.gouhua.tplibrary.livedetect.FaceRecognitionActivity;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/9/18<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BorrowMoneyHelper {
    public static String errTv = "";

    private static Intent getIntent() {
        Intent intent = new Intent();
        intent.putExtra("tag", "XJD");
        return intent;
    }


    /**
     * 借款页面--检查信息是否完成
     */
    public static boolean checkIfMsgComplete(BaseActivity activity, Object object) {
        CheckIfMsgCompleteBean completeBean = (CheckIfMsgCompleteBean) object;
        boolean personalInfoComplete = "Y".equals(completeBean.getPersonalInfoComplate());
        String state = completeBean.getMsgCompleteState();
        Intent intent = getIntent();
        ArrayList<Class> list = new ArrayList<>();
        if ("02".equals(state) || "03".equals(state) || "04".equals(state) || "05".equals(state)) {
            if (personalInfoComplete) {
                switch (state) {
                    case "02":  //缺少OCR+活体检测+交易密码
                        intent.putExtra(NameAuthIdCardActivity.ID, FaceRecognitionActivity.class);
                        intent.setClass(activity, NameAuthIdCardPatchActivity.class);
                        list.add(SetTransactionPwdActivity.class);
                        intent.putExtra("followStep", list);
                        break;
                    case "03":  //缺少OCR+交易密码
                        intent.putExtra(NameAuthIdCardActivity.ID, SetTransactionPwdActivity.class);
                        intent.setClass(activity, NameAuthIdCardPatchActivity.class);
                        intent.putExtra("followStep", list);  //传递空list是为了防止在设置密码页设置完毕直接跳到首页
                        break;
                    case "04":  //缺少活体检测+交易密码
                        intent.putExtra(FaceRecognitionActivity.ID, SetTransactionPwdActivity.class);
                        intent.setClass(activity, FaceRecognitionActivity.class);
                        intent.putExtra("followStep", list);
                        break;
                    case "05":  //缺少交易密码
                        intent.putExtra(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                        intent.setClass(activity, FaceCheckActivity.class);
                        intent.putExtra("followStep", list);
                        break;
                }

            } else {
                switch (state) {
                    case "02":  // 缺少OCR+活体检测+交易密码+个人资料
                        intent.putExtra(NameAuthIdCardActivity.ID, FaceRecognitionActivity.class);
                        intent.setClass(activity, NameAuthIdCardPatchActivity.class);
                        list.add(SetTransactionPwdActivity.class);
                        list.add(PerfectInfoActivity.class);
                        intent.putExtra("followStep", list);
                        break;
                    case "03":  //缺少OCR+交易密码+个人资料
                        intent.putExtra(NameAuthIdCardActivity.ID, SetTransactionPwdActivity.class);
                        intent.setClass(activity, NameAuthIdCardPatchActivity.class);
                        list.add(PerfectInfoActivity.class);
                        intent.putExtra("followStep", list);
                        break;
                    case "04":  //缺少活体检测+交易密码+个人资料
                        intent.putExtra(FaceRecognitionActivity.ID, SetTransactionPwdActivity.class);
                        intent.setClass(activity, FaceRecognitionActivity.class);
                        list.add(PerfectInfoActivity.class);
                        intent.putExtra("followStep", list);
                        break;
                    case "05":  //缺少交易密码+个人资料
                        intent.putExtra(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                        intent.setClass(activity, FaceCheckActivity.class);
                        list.add(PerfectInfoActivity.class);
                        intent.putExtra("followStep", list);
                        break;
                }

            }
            intent.putExtra("borrowStep", true);
            activity.startActivity(intent);
            return false;
        }
        Map map = JsonUtils.fromJson(completeBean.getRLSB(), HashMap.class);
        String faceCode = String.valueOf(map.get("code"));
        if ("01".equals(faceCode) || "02".equals(faceCode)) {
            activity.showDialog("人脸识别已超过上限次数，暂不能继续办理，详询4000187777");
            return false;
        } else {
            //补传身份证 +人脸
            if (!"Y".equals(completeBean.getCERTFLAG())
                    && !"Y".equals(completeBean.getFacePhotoFlag())) {
                intent.putExtra(NameAuthIdCardActivity.ID, FaceRecognitionActivity.class);
                intent.setClass(activity, NameAuthIdCardPatchActivity.class);
                intent.putExtra("borrowStep", true);
                showConfirmDialog(activity, activity.getResources().getString(R.string.borrow_face_idcard), intent);
                return false;
            } else if (!"Y".equals(completeBean.getCERTFLAG()) || "Y".equals(completeBean.getCertInfoExpired())) {
                intent.putExtra(NameAuthIdCardActivity.ID, NameAuthIdCardActivity.class);
                intent.setClass(activity, NameAuthIdCardPatchActivity.class);
                intent.putExtra("borrowStep", true);
                //身份证照片不存在
                if (!"Y".equals(completeBean.getCERTFLAG())) {
                    showConfirmDialog(activity, activity.getResources().getString(R.string.borrow_idcard), intent);
                } else {
                    //身份证有效期失效
                    showConfirmDialog(activity, activity.getResources().getString(R.string.idcard_timeout), intent);
                }
                return false;
            } else if (!"Y".equals(completeBean.getFacePhotoFlag())
                    || !"00".equals(faceCode)) {
                //人脸识别照片不存在或人脸分值不够
                intent.setClass(activity, FaceRecognitionActivity.class);
                intent.putExtra("borrowStep", true);
                showConfirmDialog(activity, activity.getResources().getString(R.string.borrow_face), intent);
                return false;
            } else if (!"Y".equals(completeBean.getGRJBXX())
                    || !"Y".equals(completeBean.getDWXX())
                    || !"Y".equals(completeBean.getEmailExist())
                    //|| !"Y".equals(completeBean.getHasMthInc())
                    //|| !"Y".equals(completeBean.getContactRiskFlag())
                    || !"Y".equals(completeBean.getLXRXX())) {
                //个人资料信息不完善
                intent.putExtra(PerfectInfoActivity.ID, PerfectInfoActivity.class);
                intent.putExtra("borrowStep", true);
                intent.setClass(activity, PerfectInfoActivity.class);
                activity.startActivity(intent);
                return false;
            }
        }
        return true;
    }

    private static void showConfirmDialog(final BaseActivity activity, String msg, final Intent intent) {
        activity.showProgress(false);
        activity.showDialog(msg, "取消", "确定", (dialog, which) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (which == 2) {
                activity.startActivity(intent);
            }
        }).setButtonTextColor(2, R.color.colorPrimary).setButtonTextColor(1, R.color.colorPrimary);
    }

    /**
     * show还款计划
     *
     * @param inputAmount      借款金额
     * @param maxAmount        最大可借金额
     * @param minAmount        最小可借金额
     * @param typCde           还款期限（贷款品种代码）
     * @param typLevelTwo      还款期限（贷款品种小类）
     * @param applyTnrTyp      还款期限（期限类型）
     * @param applyTnr         借款期限
     * @param mtdCde           还款方式
     * @param selectLoanCoupon 选择的免息券
     */
    public static void showRepaymentDetail(BaseActivity activity, String inputAmount, double maxAmount,
                                           double minAmount, String typCde, String typLevelTwo,
                                           String applyTnrTyp, String applyTnr, String mtdCde,
                                           LoanCoupon selectLoanCoupon) {
        if (!isBorrowInRange(inputAmount, maxAmount, minAmount, true)) {
            return;
        }
        if (CheckUtil.isEmpty(typCde)) {
            UiUtil.toast("请先选择借多久");
            return;
        }
        if (CheckUtil.isEmpty(typLevelTwo)) {
            UiUtil.toast("请先选择借多久");
            return;
        }
        if (CheckUtil.isEmpty(applyTnrTyp)) {
            UiUtil.toast("请先选择借多久");
            return;
        }
        if (CheckUtil.isEmpty(applyTnr)) {
            UiUtil.toast("请先选择借多久");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("inputAmount", inputAmount);
        intent.putExtra("typCde", typCde);
        intent.putExtra("applyTnrTyp", applyTnrTyp);
        intent.putExtra("applyTnr", applyTnr);
        intent.putExtra("mtdCde", mtdCde);
        if (selectLoanCoupon != null) {
            intent.putExtra(LoanCoupon.class.getSimpleName(), selectLoanCoupon);
        }
        intent.setClass(activity, RepaymentDetailsNewActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 还款计划试算
     *
     * @param inputAmount 借款金额
     * @param maxAmount   最大可借金额
     * @param minAmount   最小可借金额
     * @param typCde      还款期限（贷款品种代码）
     * @param typLevelTwo 还款期限（贷款品种小类）
     * @param applyTnrTyp 还款期限（期限类型）
     * @param applyTnr    借款期限
     */
    public static boolean checkGetPaySs(String inputAmount, double maxAmount, double minAmount, String typCde, String typLevelTwo, String
            applyTnrTyp, String applyTnr) {
        if (!isBorrowInRange(inputAmount, maxAmount, minAmount, false)) {
            return false;
        } else if (CheckUtil.isEmpty(typCde)) {
            return false;
        } else if (CheckUtil.isEmpty(typLevelTwo)) {
            return false;
        } else if (CheckUtil.isEmpty(applyTnrTyp)) {
            return false;
        } else if (CheckUtil.isEmpty(applyTnr)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 开始录单流程前确认
     *
     * @param inputAmount 借款金额
     * @param maxAmount   最大可借金额
     * @param minAmount   最小可借金额
     * @param typCde      还款期限（贷款品种代码）
     * @param typLevelTwo 还款期限（贷款品种小类）
     * @param applyTnrTyp 还款期限（期限类型）
     * @param applyTnr    借款期限
     * @param cardNo      收款银行卡
     */
    public static boolean confirmSaveOrder(String inputAmount, double maxAmount, double minAmount,
                                           String typCde, String typLevelTwo, String applyTnrTyp, String applyTnr, String cardNo,ShowBankCardListener listener) {
        if (!isBorrowInRange(inputAmount, maxAmount, minAmount, false)) {
            return false;
        }
        if (CheckUtil.isEmpty(typCde)) {
            errTv = "贷款产品获取失败，请退出重试";
            UiUtil.toast(errTv);
            return false;
        }
        if (CheckUtil.isEmpty(typLevelTwo)) {
            errTv = "贷款产品小类获取失败，请退出重试";
            UiUtil.toast(errTv);
            return false;
        }
        if (CheckUtil.isEmpty(applyTnrTyp)) {
            errTv = "请选择借款期限";
            UiUtil.toast(errTv);
            return false;
        }
        if (CheckUtil.isEmpty(applyTnr)) {
            errTv = "请选择借款期限";
            UiUtil.toast(errTv);
            return false;
        }
        if (CheckUtil.isEmpty(cardNo)) {
            errTv = "请选择收款银行卡";
            UiUtil.toast(errTv);
            if (listener!=null){
                listener.showBankCardDialog();
            }
            return false;
        }
        return true;
    }

    public interface ShowBankCardListener{
        void showBankCardDialog();
    }

    /**
     * 订单 入参收集
     *
     * @param custNo       客户编号
     * @param typCde       贷款品种代码
     * @param inputAmount  借款金额
     * @param applyTnr     借款期限
     * @param applyTnrTyp  还款期限（期限类型）
     * @param totalNormInt 利息总额
     * @param totalFeeAmt  费用总额
     * @param cardNo       银行卡
     * @param loanUsage    用途
     */
    public static Map<String, String> getOrderParam(String custNo, String typCde, String
            inputAmount, String applyTnr, String applyTnrTyp, String totalNormInt, String totalFeeAmt
            , String cardNo, String loanUsage, LoanCoupon coupon, String kind) {
        Map<String, String> map = new HashMap<>();
        map.put("custNo", custNo);
        map.put("typCde", typCde);
        map.put("applyAmt", inputAmount);
        map.put("applyTnr", applyTnr);
        map.put("applyTnrTyp", applyTnrTyp);
        map.put("totalnormint", totalNormInt);
        map.put("totalfeeamt", totalFeeAmt);
        map.put("applCardNo", RSAUtils.encryptByRSA(cardNo));//放款卡号
        map.put("repayApplCardNo", RSAUtils.encryptByRSA(cardNo));//还款卡号
        map.put("purpose", loanUsage);
        map.put("typGrp", "02");
        map.put("source", "18");
        map.put("version", "2");
        map.put("whiteType", "SHH");
        map.put("indivMobile", RSAUtils.encryptByRSA(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
        map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
        map.put("isRsa", "Y");
        if (coupon != null && "Y".equals(coupon.getCanUseState())) {
            map.put("couponNo", coupon.getCouponNo());
            map.put("kind", kind);
            map.put("calVol", coupon.getCalVol());
            map.put("maxDiscValue", coupon.getMaxDiscValue());
            map.put("batchDeduction", coupon.getBatchDeduction());
        }

        return map;
    }

    /**
     * 借款入参收集
     */
    public static Map<String, String> getCompleteParam() {
        Map<String, String> map = new HashMap<>();
        map.put("isOrder", "N");//是否为订单
        map.put("noEduLocal", "No");
        map.put("channel", "18");//渠道号
        map.put("userId", RSAUtils.encryptByRSA(SpHp.getLogin(SpKey.LOGIN_USERID)));//用户id
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));//用户编号
        map.put("custName", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNAME)));//姓名
        map.put("idNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CERTNO)));//身份证号
        map.put("isRsa", "Y");
        return map;
    }

    /**
     * 贷款用途数据处理
     */
    public static List<ArrayBean> getBorrowPurpose(Object object) {
        List<HashMap> hashMapList = JsonUtils.fromJsonArray(object, HashMap.class);
        if (hashMapList != null && hashMapList.size() > 0) {
            List<ArrayBean> listData = new ArrayList<>();
            for (Map map : hashMapList) {
                listData.add(new ArrayBean(map.get("cdCode").toString(), map.get("codeDesc").toString(), map.get("isDefault").toString()));
            }
            return listData;
        }
        return null;
    }

    /**
     * 判断输入金额是否在合规
     *
     * @param inputAmount 借款金额
     * @param maxAmount   最大可借金额
     * @param minAmount   最小可借金额
     */
    private static boolean isBorrowInRange(String inputAmount, double maxAmount, double minAmount, boolean isToast) {
        if (CheckUtil.isEmpty(inputAmount)) {
            UiUtil.toast("请输入借款金额");
            return false;
        }
        if (maxAmount < Double.parseDouble(inputAmount) || minAmount > Double.parseDouble(inputAmount)) {
            UiUtil.toast("可借金额范围" + CheckUtil.formattedAmount(String.valueOf(minAmount)) + "-" + CheckUtil.formattedAmount(String.valueOf(maxAmount)) + "元");
            return false;
        }
        return true;
    }
}
