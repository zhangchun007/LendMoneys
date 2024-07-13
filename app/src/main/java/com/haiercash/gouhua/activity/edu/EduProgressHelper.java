package com.haiercash.gouhua.activity.edu;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.activity.accountsettings.SetTransactionPwdActivity;
import com.haiercash.gouhua.activity.bankcard.AddBankCardInformaticaActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.ChaXunKeHuBianHao_get;
import com.haiercash.gouhua.beans.CheckIfMsgCompleteBean;
import com.haiercash.gouhua.beans.PostCheckIfMsgComplete;
import com.haiercash.gouhua.beans.WhiteListBean;
import com.haiercash.gouhua.beans.register.IsPasswordExistBean;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.tplibrary.livedetect.FaceRecognitionActivity;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：流程判断帮助类
 * 项目作者：胡玉君
 * 创建日期：2017/2/14 10:06.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * <p>
 * 状态码：(相反数为该流程接口调用失败或服务端返回错误信息(非00000))
 * 0：需要登录，但登录原因多种，有可能未登录，也有可能内存丢值需要重新登录
 * 1. 需要进行实名认证(准入资格查询归入该流程)
 * 2. 需要进行人脸识别
 * 3. 需要设置交易密码
 * 4. 需要进行完善信息(没有相反数-4)
 * 5. 新流程已完善(没有相反数-5，因为该流程已完善说明接口调用全部成功)
 * <p>
 * <p>
 * 现金贷：XJD
 * 申请分期：SPFQ
 * 额度激活：EDJH
 * ----------------------------------------------------------------------------------------------------
 */
public class EduProgressHelper implements INetResult {

    private static final String LOGIN = "请先登录";
    private static final String ERROR_LOGIN = "账号异常，请退出重试";
    private static final String NAME_AUTH = "请先进行实名认证";
    private static final String FACE = "人脸识别次数已达上限，不能进行额度申请";
    private static final String PASSWORD = "请先输入交易密码";
    //黑名单用户提示
    private static final String ERROR_NOT_PASS = "很抱歉，您的综合评定不足，暂不能为您提供借款服务，请30天后重试";
    private static final String ERROR_NOT_PASS_GRAY_LIST = "很抱歉，因您综合评分不足暂不能申请额度，7天后再尝试申请。";
    private static final String PERFECT_INFO = "请先完善信息";

    private CheckIfMsgCompleteBean msgCompleteBean;

    public static final int NORMAL_PROGRESS = 0x10; //正常流程
    public static final int ABNORMAL_PROGRESS = 0x20; //非正常流程
    public static final int RETURN_PROGRESS = 0x21; //退回流程
    public static final int REFUSE_SECOND_PROGRESS = 0x22; //拒绝/取消流程第二次
    public static final int REFUSE_OTHERS_PROGRESS = 0x23; //拒绝/取消流程第三次以上
    public static final int QUOTA_FAILURE_PROGRESS = 0x24; //额度已失效

    //默认为正常状态
    private int mCurrentStatus = NORMAL_PROGRESS;

    private String mCurrentClassName;

    private NetHelper netHelper;
    // 用来记录流程
    private static final Class[][] className =
            {
                    //正常流程(只需要定义征信授权书前后即可)
                    {FaceRecognitionActivity.class, PersonalCreditContractActivity.class},

                    //退回流程
                    {MainActivity.class, NameAuthStartActivity.class, NameAuthBankCardActivity.class, FaceRecognitionActivity.class, PerfectInfoActivity.class, ApplyWaiting.class},

                    //拒绝/取消流程第二次
                    {MainActivity.class, NameAuthStartActivity.class, NameAuthBankCardActivity.class,
                            FaceRecognitionActivity.class, SetTransactionPwdActivity.class, PerfectInfoActivity.class, ApplyWaiting.class},

                    //拒绝/取消流程第三次及以上
                    {MainActivity.class, NameAuthStartActivity.class, NameAuthBankCardActivity.class,
                            FaceRecognitionActivity.class, PersonalCreditContractActivity.class, SetTransactionPwdActivity.class, PerfectInfoActivity.class, ApplyWaiting.class},

                    //额度已失效
                    {MainActivity.class, NameAuthStartActivity.class, NameAuthBankCardActivity.class,
                            FaceRecognitionActivity.class, PersonalCreditContractActivity.class, SetTransactionPwdActivity.class, PerfectInfoActivity.class, ApplyWaiting.class}


            };
    private String cardNo;
    private String signStatus;

    private EduProgressHelper() {
    }

    private static final class SingleTonHolder {
        private static final EduProgressHelper instance = new EduProgressHelper();
    }

    public static EduProgressHelper getInstance() {
        return SingleTonHolder.instance;
    }

    /**
     * 检查是否登陆
     */
    private boolean checkLogIn() {
        return AppApplication.isLogIn();
    }

    /**
     * 跳转至登陆
     */
    private void gotoLogIn(Activity activity) {
        activity.finish();
        LoginSelectHelper.staticToGeneralLogin();
    }

    /**
     * 根据用户状态获取当前流程
     */
    private Class[] getCurrentProgress() {
        Class[] currentProgress;
        //当前用户状态属于非正常状态
        if (mCurrentStatus != NORMAL_PROGRESS) {
            currentProgress = className[mCurrentStatus - ABNORMAL_PROGRESS];
        } else {
            currentProgress = className[0];
        }
        return currentProgress;
    }

    /**
     * 获取用户当前的状态
     */
    private void getUserStatus() {
        String status = SpHp.getLogin(SpKey.LOGIN_STATUS);
        if (!TextUtils.isEmpty(status)) {
            mCurrentStatus = Integer.parseInt(status);
        }
    }

    /**
     * 处理非正常流程
     */
    private boolean transactProgress() {
        getUserStatus();
        Class[] progress = getCurrentProgress();
        int i = 0;
        for (; i < progress.length; i++) {
            if (mCurrentClassName.equals(progress[i].getSimpleName())) {
                break;
            }
        }
        // 正常流程
        if (i >= progress.length - 1 && mCurrentStatus == NORMAL_PROGRESS) {
            return false;
        }
        if (listener != null) {
            listener.inProgress(0, progress[i + 1], "");
        }
        return true;
    }

    public void checkProgress(final BaseActivity context, final boolean finish) {
        // 额度激活下一步
        context.showProgress(true);
        EduProgressHelper eduProgressHelper = new EduProgressHelper();
        eduProgressHelper.getProgress(context, (((progress, reason, cardNo1) -> {
            context.showProgress(false);
            if (progress < 0) {
                context.showDialog((String) reason);
            } else {
                EduProgressHelper.gotoRelativelyClass(context, progress, reason, cardNo);
                if (finish) {
                    context.finish();
                }
            }
        })));
    }

    private void getProgress(Activity context, ProgressListener lis) {
        this.listener = lis;
        netHelper = new NetHelper(this);
        mCurrentClassName = context.getClass().getSimpleName();
        //未登录
        if (!checkLogIn()) {
            AppApplication.setLoginCallback(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    getProgress(context, lis);
                }
            });
            gotoLogIn(context);
            return;
        }
        //处理已经定义的流程
        if (transactProgress()) {
            return;
        }

        //判断userid是否为空
        if (CheckUtil.isEmpty(AppApplication.userid)) {
            AppApplication.setLoginCallback(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    getProgress(context, lis);
                }
            });
            gotoLogIn(context);
            return;
        }
        //第四步：查询客户编号
        String custNo = SpHp.getUser(SpKey.USER_CUSTNO);
        String custName = SpHp.getUser(SpKey.USER_CUSTNAME);
        String custMobile = SpHp.getUser(SpKey.USER_MOBILE);
        String custCertNo = SpHp.getUser(SpKey.USER_CERTNO);
        if (CheckUtil.isEmpty(custNo) || CheckUtil.isEmpty(custName) || CheckUtil.isEmpty(custMobile) || CheckUtil.isEmpty(custCertNo)) {
            //不知道是否实名认证了
            Map<String, String> map = new HashMap<>();
            map.put("userId", AppApplication.userid);
            netHelper.getService(ApiUrl.url_kehubianhao, map, ChaXunKeHuBianHao_get.class, true);
        } else {
            //已经实名认证
            requestIsPass(custName, custMobile, custCertNo);
        }

    }

    private ProgressListener listener;

//    public void setProgressListener(ProgressListener listener) {
//        this.listener = listener;
//    }

    public interface ProgressListener {
        void inProgress(int progress, Object reason, String cardNo);
    }

    /**
     * 检查人脸识别
     */
    private boolean checkFaceCode() {
        /*
         * 00：已经通过了人脸识别（得分合格），不需要再做人脸识别
         01：未通过人脸识别，剩余次数为0，不能再做人脸识别，录单终止
         02：未通过人脸识别，剩余次数为0，不能再做人脸识别，但可以上传替代影像
         10：未通过人脸识别，可以再做人脸识别（剩余次数为remainCount）
         */
        Map map = JsonUtils.fromJson(msgCompleteBean.getRLSB(), HashMap.class);
        String faceCode = String.valueOf(map.get("code"));
        if (!"Y".equals(msgCompleteBean.getFacePhotoFlag())) {
            //人脸未完善 10
            if (listener != null) {
                listener.inProgress(1, FaceRecognitionActivity.class, "");
                listener = null;
            }
            return false;
        }
        if ("00".equals(faceCode)) {
            return true;
        }
        if ("01".equals(faceCode) || "02".equals(faceCode)) {
            //不可以再次进行人脸
            if (listener != null) {
                listener.inProgress(-1, FACE, "");
                listener = null;
            }
        } else {
            //人脸未完善 10
            if (listener != null) {
                listener.inProgress(1, FaceRecognitionActivity.class, "");
                listener = null;
            }
        }
        return false;
    }


    /**
     * 检查是否设置交易密码
     */
    private void getPassWord() {
        //信息已完善
        Map<String, String> params = new HashMap<>();
        params.put("userId", EncryptUtil.simpleEncrypt(AppApplication.userid));
        netHelper.postService(ApiUrl.urlExistPassword, params, IsPasswordExistBean.class);
    }

    /**
     * 检查个人资料是否完整
     */
    private void checkPersonInfo() {
        if ("Y".equals(msgCompleteBean.getGRJBXX())
                && "Y".equals(msgCompleteBean.getDWXX())
                && "Y".equals(msgCompleteBean.getEmailExist())
                //&& "Y".equals(msgCompleteBean.getHasMthInc())
                //&& "Y".equals(msgCompleteBean.getContactRiskFlag())
                && "Y".equals(msgCompleteBean.getLXRXX())) {
            if (!"Y".equals(msgCompleteBean.getCERTFLAG()) || "Y".equals(msgCompleteBean.getCertInfoExpired())) {
                //身份证信息不完善
                if (listener != null) {
                    listener.inProgress(2, NameAuthIdCardPatchActivity.class, "");
                    listener = null;
                }
            } else {
                //信息完善，检查签约状态
                checkSignStatues();
            }
        } else {
            //信息未完善
            if (listener != null) {
                if (mCurrentClassName.equals(PersonalCreditContractActivity.class.getSimpleName())) {
                    listener.inProgress(0, PerfectInfoActivity.class, "");
                } else {
                    listener.inProgress(0, PersonalCreditContractActivity.class, "");
                }
                listener = null;
            }
        }

    }

//    /**
//     * 检测用户是否黑名单
//     *
//     * @param name
//     * @param mobile
//     * @param certNo
//     */
//    private void checkUserBlackStatus(String name, String mobile, String certNo) {
//        String isPass = SpHp.getUser( SpKey.USER_BLACK_STATUS);
//        if (CheckUtil.isEmpty(isPass)) {
//            requestIsPass(name, mobile, certNo);
//        } else if ("-1".equals(isPass)) {
//            if (listener != null) {
//                listener.inProgress(-1, ERROR_NOT_PASS);
//                listener = null;
//            }
//        } else {
//            requestIsPerfectInfo();
//        }
//    }


    @Override
    public void onSuccess(Object response, String flag) {
        if (ApiUrl.url_kehubianhao.equals(flag)) {
            ChaXunKeHuBianHao_get cust = (ChaXunKeHuBianHao_get) response;
            //把查询的客户编号全部保存在sp中
            SpHp.saveUser(SpKey.USER_CUSTNAME, cust.getCustName());//客户姓名
            SpHp.saveUser(SpKey.USER_CUSTNO, cust.getCustNo());//客户编号
            SpHp.saveUser(SpKey.USER_CERTNO, cust.getCertNo());//证件号
            SpHp.saveUser(SpKey.USER_MOBILE, cust.getMobile());//实名认证手机号
            //查询准入资格
            requestIsPass(cust.getCustName(), cust.getMobile(), cust.getCertNo());
        } else if (ApiUrl.urlCustPassList.equals(flag)) {
            //准入资格
            WhiteListBean whiteListBean = (WhiteListBean) response;
            if ("-1".equals(whiteListBean.isPass)) {
                //不准入
                if (listener != null) {
                    listener.inProgress(-1, ERROR_NOT_PASS, "");
                    listener = null;
                }
                return;
            }
            if (whiteListBean.isGrayListTypeFlag()) {
                //不准入
                if (listener != null) {
                    listener.inProgress(-1, ERROR_NOT_PASS_GRAY_LIST, "");
                    listener = null;
                }
                return;
            }
            //查询信息是否完善
            requestIsPerfectInfo();
        } else if (ApiUrl.url_CheckIfMsgComplete_EDJH.equals(flag)) {
            msgCompleteBean = (CheckIfMsgCompleteBean) response;
            String hasPassWord = SpHelper.getInstance().readMsgFromSp(SpKey.STATE, SpKey.STATE_HASPAYPAS);
            if (!CheckUtil.isEmpty(msgCompleteBean.getDefaultCardInfo())) {
                cardNo = msgCompleteBean.getDefaultCardInfo().getCardNo();
                signStatus = msgCompleteBean.getDefaultCardInfo().getSignStatus();
            }
            //人脸已经完善
            if (checkFaceCode() && !"Y".equals(hasPassWord)) {
                //检查交易密码是否存在
                getPassWord();
            } else {
                //检查个人资料信息
                checkPersonInfo();
            }
        } else if (ApiUrl.urlExistPassword.equals(flag)) {
            //交易密码是否设置过了
            IsPasswordExistBean isPasswordExistBean = (IsPasswordExistBean) response;
            //已经设置交易密码
            if ("1".equals(isPasswordExistBean.payPasswdFlag)) {
                SpHelper.getInstance().saveMsgToSp(SpKey.STATE, SpKey.STATE_HASPAYPAS, "Y");
                //检查个人资料信息
                checkPersonInfo();
            } else {
                //信息未完善
                if (listener != null) {
                    if (mCurrentClassName.equals(MainActivity.class.getSimpleName())) {
                        listener.inProgress(0, FaceRecognitionActivity.class, "");
                    } else {
                        listener.inProgress(0, SetTransactionPwdActivity.class, "");
                    }
                    listener = null;
                }
            }
        }

    }

    private void checkSignStatues() {
        //需要重新签约
        if (!"SIGN_SUCCESS".equals(signStatus)) {
            if (listener != null) {
                listener.inProgress(6, AddBankCardInformaticaActivity.class, cardNo);
                listener = null;
            }
        } else {
            //信息完善
            if (listener != null) {
                listener.inProgress(5, ApplyWaiting.class, "");
                listener = null;
            }
        }
    }

    private void requestIsPerfectInfo() {
        msgCompleteBean = null;
        String custNo = SpHp.getUser(SpKey.USER_CUSTNO);
        String custName = SpHp.getUser(SpKey.USER_CUSTNAME);
        String custCertNo = SpHp.getUser(SpKey.USER_CERTNO);
        PostCheckIfMsgComplete params = new PostCheckIfMsgComplete();
        //额度激活
        params.setIsOrder("N");
        params.setNoEduLocal("NO");
        params.setChannel("18");//渠道号
        params.setUserId(RSAUtils.encryptByRSA(AppApplication.userid));//用户id
        params.setCustNo(RSAUtils.encryptByRSA(custNo));//用户编号
        params.setCustName(RSAUtils.encryptByRSA(custName));//姓名
        params.setIdNo(RSAUtils.encryptByRSA(custCertNo));//身份证号
        params.setIsRsa("Y");
        netHelper.postService(ApiUrl.url_CheckIfMsgComplete_EDJH, params, CheckIfMsgCompleteBean.class, true);
    }

    /**
     * 请求准入资格
     */
    private void requestIsPass(String name, String mobile, String certNo) {
        Map<String, String> map = new HashMap<>();
        map.put("custName", RSAUtils.encryptByRSA(name));
        map.put("phonenumber", RSAUtils.encryptByRSA(mobile));
        map.put("certNo", RSAUtils.encryptByRSA(certNo));
        map.put("isRsa", "Y");
        netHelper.getService(ApiUrl.urlCustPassList, map, WhiteListBean.class, true);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        switch (url) {
            case ApiUrl.url_kehubianhao:
                SpHelper.getInstance().deleteAllMsgFromSp(SpKey.USER);
                if ("不存在该客户的实名信息".equals(error.getHead().getRetMsg())) {
                    if (listener != null) {
                        listener.inProgress(0, NameAuthStartActivity.class, "");
                        listener = null;
                    }
                } else {
                    if (listener != null) {
                        listener.inProgress(-1, error.getHead().getRetMsg(), "");
                        listener = null;
                    }
                }
                break;
            case ApiUrl.url_CheckIfMsgComplete_EDJH:
                //完善信息查询失败
                if (listener != null) {
                    if ("A1157".equals(error.getHead().getRetFlag())) {
                        listener.inProgress(-2, "身份证过期", "");
                    } else {
                        listener.inProgress(-2, error.getHead().getRetMsg(), "");
                    }
                    listener = null;
                }
                break;
            case ApiUrl.urlExistPassword:
                if (listener != null) {
                    listener.inProgress(-3, error.getHead().getRetMsg(), "");
                    listener = null;
                }
                break;
            case ApiUrl.urlCustPassList:
                if (listener != null) {
                    listener.inProgress(-1, error.getHead().getRetMsg(), "");
                    listener = null;
                }
                break;
            default:
                break;
        }
    }


    private static void gotoClass(Activity activity, Class reason, String cardNo) {
        Intent intent = new Intent(activity, (Class<?>) reason);
        if (reason.isAssignableFrom(AddBankCardInformaticaActivity.class)) {
            intent.putExtra("signCardNum", cardNo);
            intent.putExtra("tag", "EDJH");
        } else {
            intent.putExtra("tag", "EDJH");
        }
        activity.startActivity(intent);
    }

    /**
     * 跳转至对应的class
     */
    private static void gotoRelativelyClass(Activity activity, int progress, Object className, String cardNo) {
        gotoClass(activity, (Class) className, cardNo);
    }

    /**
     * 手动关闭连接
     */
    public void onDestroy() {
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
    }
}
