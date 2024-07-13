package com.haiercash.gouhua.base;

import com.app.haiercash.base.net.config.NetConfig;
import com.haiercash.gouhua.BuildConfig;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2017/11/22<br/>
 * 描    述：<br/>
 * ==========================================
 * 修订历史：<br/>======================
 */
public class ApiUrl extends NetConfig {
    //获取上次登录方式
    public static final String URL_GET_LAST_LOGIN_TYPE = "app/appserver/login/getLastLoginMethod";
    /**
     * 根据userId获取用户信息-
     */
    public static final String URL_GET_USER_INFO_BY_ID = "app/appserver/userinfo/getUserInfoByUserId";
    //头像地址
    public static final String urlCustImage = API_SERVER_URL + "app/uauth/getUserPic";

    //6.53.(POST) 外部风险信息采集**
    public static final String urlRiskInfo = "app/appserver/updateRiskInfo";
    public static final String urlRiskInfoNew = "app/appserver/riskinfo/uploadRiskInfoNew";

    /**
     * 通用风控信息上送接口
     */
    public static final String POST_URL_RISK_MSG = "app/appserver/message/commonMessageUpload";
    /**
     * Gio采集数据上送a)接口
     */
    public static final String URL_COLLECT_GIO_DATA = "app/appserver/riskinfo/collectGioData";
    /**
     * 个人登录
     **/
    public static final String loginUrl = "app/appserver/appCustomerLogin";


    /**
     * 一键登录URL
     */
    public static final String ONE_KEY_LOGIN_URL = "app/appserver/login/oneLogin";

    /**
     * 个人退出登录
     **/
    public static final String LOGOUT_URL = "app/appserver/appLoginOutNew";

    //3.4.23.	(POST) 校验验证码并绑定设备号
    public static final String urlVerifyAndBindDeviceId = "app/appserver/verifyAndBindDeviceId";
    /**
     * 手势密码设置
     */
    public static final String urlSetGesture = "app/appserver/uauth/gesture";
    /**
     * (GET)手势密码验证
     **/
    public static final String url_ValidateGestureCount = "app/appserver/uauth/validateGesture";
    /**
     * 删除手势密码
     */
    public static final String URL_DELETE_GESTURE = "app/appserver/login/deleteGesture";
    /**
     * 是否设置过手势密码和交易密码
     */
    public static final String urlExistPassword = "app/appserver/uauth/validateUserFlagNew";
    //头像设置
    public static final String urlSetPhoto = "app/appserver/uauth/avatarUrl";

    //修改绑定手机号**
    public static final String urlChangePhone = "app/appserver/uauth/updateMobile/needVerify";

    //6.1.116.	查询CRM中客户扩展信息（二）
    public static final String url_wanshanxinxi_all = "app/appserver/getAllCustExtInfo";
    //联系人添加
    public static final String url_lianxiren_post = "app/appserver/v4/crm/saveCustFCiCustContact";
    /**
     * 交易密码验证-》get<br/>
     * 用户账号	userId	varchar	是/是<br/>
     * 交易密码	payPasswd	varchar	是/是<br/>
     */
    public static final String URL_CHECK_PAY_SECRET = "app/appserver/uauth/validatePayPasswd";
    /**
     * 交易密码设置保存
     */
    public static final String url_zhifumimashezhi_put = "app/appserver/uauth/payPasswd";
    /**
     * 账号查询（是否注册过）
     */
    public static final String url_zhanghaochaxun_get = "app/appserver/uauth/isRegister";
    /**
     * 注册时发送手机验证码（有优化重复注册）
     */
    public static final String url_register_code_get = "app/appserver/register/sendSmsCodeForRegister";
    /**
     * 发送手机验证码
     * 4.1.1新加入参 deviceId不能传空的加密，否则后台不会走正常成功流程
     */
    public static final String url_yanzhengma_get = "app/appserver/smsSendVerify";
    /**
     * 注册时校验短信验证码->POST
     */
    public static final String url_register_yanzhengma_xiaoyan = "app/appserver/register/smsVerifyForRegister";
    /**
     * 校验短信验证码->POST
     */
    public static final String url_yanzhengma_xiaoyan = "app/appserver/smsVerify";
    /**
     * (POST)找回登录密码之修改登录密码
     */
    public static final String url_findLoginPwd2Update = "app/appserver/findLoginPwd2Update/needVerify";

    /**
     * 修改登录密码（已登录）
     */
    public static final String url_changeloginpwd_put = "app/appserver/loginAndResetPwd";
    /**
     * 用户注册
     */
    public static final String url_zhuceyonghu_post = "app/appserver/uauth/saveUauthUsers";

    //借款合同
    public static final String urlContractUri = API_SERVER_URL + "app/appserver/contract";
    //查看合同
    public static final String urlCheckContract = API_SERVER_URL + "app/appserver/downContractPdf";
    /**
     * 银行相关:银行列表名称查询
     */
    public static final String url_yinghangmingcheng_get = "app/appserver/crm/cust/getBankList";
    //查询银行卡**
    public static final String url_QueryBankCard = "app/appserver/crm/cust/getBankCard";
    //查询银行卡信息**
    public static final String urlBankInfo = "app/appserver/crm/cust/getBankInfo";
    //public static final String BANK_ERROR = "C1203";
    //保存银行卡
    public static final String urlSaveBankCard = "app/appserver/bank/card/signing/confirm/saveCard";

    //删除银行卡  2016-12-7 更换删除银行卡列表接口调用后台
    public static final String urlDeleteBankCard = "app/appserver/deleteBankCard";

    //银行卡变更授权书展示
    public static final String urlCardContracts = API_SERVER_URL + "app/appserver/grant";
    //银行卡变更授权书签章
    public static final String urlCardGrant = "app/appserver/bankCardGrant";
    /**
     * (GET) 查询已认证客户的贷款品种及利率费率(CRM112)
     */
    //public static final String URL_GET_CUST_LOAN_CODE_AND_RAT_CRM = "app/appserver/crm/cust/getCustLoanCodeAndRatCRM";
    public static final String URL_GET_STANDARD_PRODUCT_INFO = "app/appserver/product/v2/getStandardProductInfo";

    //额度申请前置校验接口（信贷拒绝码判断）
    public static final String URL_GET_CREDIT_CHECK_INFO = "app/appserver/credit/preCheckForApplyCredit";

    //白名单查询客户准入资格**
    public static final String urlCustPassList = "app/appserver/crm/cust/getCustIsPass";
    //12.5.	(Get)审批进度查询-够花 额度申请、借款申请、首页展示
    //在已经进行申请额度或借款后，对额度申请或借款的进度进行查询时，可以调用该接口
    public static final String URL_PROCESS = "app/appserver/enoughspend/approvalProcessBySeq";

    //首页广告图片下载
    public static final String urlAdPic = API_SERVER_URL + "app/appserver/ad/getAdImagByAddr?adImg=";
    //6.35.	(GET)字典项查询
    public static final String url_zidian = "app/appserver/cmis/getDict";

    /**
     * 现金贷分期
     */
    //录单校验
    public static final String urlOrder = "app/appserver/apporder/getCustInfoAndEdInfoPerson";
    /**
     * 贷前还款试算（包含优惠券列表）
     */
    public static final String url_huankuanshisuan_useCoupon = "app/appserver/smy/loanApply/getPaySsAndCoupons";
    public static final String url_getProductIrrInfo = "app/appserver/product/getProductIrrInfo";

    //6.30.	(POST)订单合同确认**
    public static final String url_hetong = "app/appserver/apporder/updateOrderContract";
    /**
     * (GET)查询客户编号：获取用户实名信息
     */
    public static final String url_kehubianhao = "app/appserver/crm/cust/queryPerCustInfo";
    /**
     * 保存现金贷订单,商品分期订单（已修改）
     */
    public static final String urlSaveOrderInfo = "app/appserver/apporder/saveAppOrderInfo";
    /**
     * 6.111.	+(POST) 人脸识别（新） 国政通
     */
    //public static final String url_face_recognition = "app/appserver/facecheck/appNewFaceCheck2";
    //v4.0.0修改到如下接口
    public static final String url_face_recognition = "app/appserver/face/appFaceVerifyForBusinessApply";

    //v4.0.0修改到如下接口
    public static final String url_face_check = "app/appserver/face/appFaceVerifyForCommon";
    /**
     * 请求旷视token
     */
    public static final String POST_FACE_TOKEN = "app/appserver/face/getSdkBizToken";

    /*帮助中心*/
    /**
     * 帮助中心--所有问题类型
     */
    public static final String allQuestionType = "app/appmanage/problemType/findAll";
    /**
     * 帮助中心--常见问题
     */
    public static final String commonProblem = "app/appmanage/problem/findCommonProblems";
    /**
     * 帮助中心--某个分类的所有问题
     */
    public static final String allProblemByProblemType = "app/appmanage/problem/findAllProblemByProblemType";
    /**
     * (GET)检测app版本
     */
//    public static final String APP_MANAGE_VERSION_CHECK = "app/appmanage/version/check";
    public static final String APP_MANAGE_VERSION_CHECK = "app/appserver/common/appVersionCheck";

    //版本更新
    public static final String url_app_update = API_SERVER_URL + "app/appmanage/version/download";

    //提额征信授权书展示
    public static final String url_edCredit = "app/appserver/edCredit";
    //6.102.	(GET) 系统参数列表查询
    public static final String url_selectByParams = "app/appserver/appmanage/param/selectByParams";
    /**
     * 额度申请信息完整查询接口
     */
    public static final String url_CheckIfMsgComplete_EDJH = "app/appserver/shh/EDJH/checkIfMsgComplete";
    /**
     * 借款信息完整查询
     */
    public static final String url_CheckIfMsgComplete_XJD = "app/appserver/SHH/XJD/checkIfMsgComplete";
    /**
     * 个人中心信息详情
     */
    public static final String URL_CUST_DETAIL_INFO = "app/appserver/personal/getCustDetailInfo";
    /**
     * 账户与安全信息详情
     */
    public static final String URL_ACCOUNT_DETAIL_INFO = "app/appserver/personal/getAccountSecPageInfo";
    /**
     * 微信解绑
     */
    public static final String URL_UNBIND_WX = "app/appserver/login/wechatLogin/unbind";
    /**
     * 修改保存客户所有扩展信息
     */
    public static final String URL_SAVE_ALL_CUST_EXT_INFO = "app/appserver/crm/cust/saveAllCustExtInfo";

    /**
     * 6.1.120.	(POST)保存身份证信息
     */
    public static final String URL_SAVE_ID_CARD = "app/appserver/saveCardMsg";

    //12.16.	(GET) 根据借据号查询该借款详情
    public static final String url_getBillDetails = "app/appserver/bills/getLoanBillDetailInfoForUniteLoan";
    /**
     * 借款记录分组
     * 身份证号 idNo String 是 idNo需要进行加密处理，客户端使用对deviceId相同的加密逻辑即可。<br/>
     * 当前页  page Integer 否<br/>
     * 每页显示的条数 size Integer 否<br/>
     */
    public static final String URL_ALL_BORROWING_RECORD_GROUP = "app/appserver/borrowing/borrowingRecordListGrouped";
    /**
     * 还款记录支付网关
     * 客户身份证号     certNo     String     是<br/>
     * 时间类型 dateType String 是 threeMonth 三个月  sixMonth 六个月/半年 oneYear 一年 customize 自定义<br/>
     * 开始时间  startRequestTime  String 否  dateType 为 customize 时必填 格式: yyyy-MM-dd HH:mm:ss<br/>
     * 结束时间 endRequestTime   String  否  dateType 为 customize 时必填  格式: yyyy-MM-dd HH:mm:ss<br/>
     */
    public static final String URL_PWG_REPAY_RECORD = "app/appserver/repayment/getPwgRepayRecord";//app/appserver/repayment/getRepayRecordIntegratePwg
    /**
     * 借款记录详情<br/>
     * 申请编号  applSeq String  是
     */
    public static final String URL_BORROWING_RECORD_DETAIL = "app/appserver/fkb/borrowing/borrowingRecordDetail";
    /**
     * 我要吐槽(反馈)
     * 反馈信息类型	feedbackType	String	否
     * 反馈信息标题	feedbackTitle	String	否
     * 反馈信息内容	feedbackContent	String	是
     * 用户手机号	userMobile	String	是
     * 用户名称	userName	String	否
     * app/appserver/userinfo/fcf/userfeedback";
     */
    public static final String URL_FEEDBACK = "app/appserver/userinfo/fcf/new/userfeedback";

    /**
     * 获取意见反馈类型
     * 字典类型	parentCode = feedBackType
     */
    public static final String URL_FEEDBACK_TYPE = "app/appmanage/dict/getDictByParentCode";
    /**
     * 用户当天是否还可以进行反馈:目前当日每用户限制反馈次数为3次
     * 用户id     userId     String     是/加密
     */
    public static final String URL_IS_FEEDBACK_ALLOW = "app/appserver/userinfo/fcf/isuserfeedbackallow";
    /**
     * 客户编号	    custNo	String	必输
     * 操作标识	    flag		必输	0、额度申请（不传默认为0）2额度信息修改
     * 期望额度	    expectCredit	String	否
     * 申请流水号	applSeq	String	否	额度信息修改时必输
     * listRsikMap	List<Map>	必输
     * 身份证号	    idNo	string	是，加密
     * 姓名		    name	String	是，加密
     * 手机号		mobile	String	是，加密
     * remark3	String	否	摩羯启用，上传custNo
     * 数据类型	    dataTyp	string	是	人脸识别(分值)、联系人、短信、经纬度(当前位置)、本机号码、通话记录、A01-百融风险数据流水号、A04-红星美凯龙APP、A601 魔蝎风险信息采集
     * 数据来源	    source	string	是	1-APP商户版、2-APP个人版、其他:同head头部channel
     * 信息列表(内容)content	List<String>	是，加密	转成JSON格式后：[“2”,”3”,”4”,”5”,”6”]，如果为星巢贷借款、登录事件上传，或者摩羯风险采集，规则如下：[{“content”:”3”,”reserved6”:”1”,”reserved7”:”1”},{“content”:”3”,”reserved6”:”2”,”reserved7”:”2”}]
     * 申请编号	    applSeq	string	否
     */
    public static final String URL_APPLY_INFO_RISK_INFO = "app/appserver/integration/fkb/edApplInfoAndRiskInfo";
    /**
     * 支用申请及风控信息更新整合接口(整合以下两个接口)
     * 提交订单 public static final String urlOrderCommit = "app/appserver/apporder/commitAppOrder"
     * <p>
     * (POST) 外部风险信息采集 public static final String urlListRiskInfo = "app/appserver/updateListRiskInfo"
     * 订单号	    orderNo	String	必输
     * 提交类型	    opType	String	必输	个人版订单提交给商户确认时传2，其余传1
     * 短信验证码	msgCode	String	个人版现金贷必填
     * 期望额度	    expectCredit	String	可选
     * listRsikMap	List<Map>	必输
     * 身份证号	    idNo	string	是，加密
     * 姓名		    name	String	是，加密
     * 手机号		mobile	String	是，加密
     * remark3	String	否	摩羯启用，上传custNo
     * 数据类型	    dataTyp	string	是	人脸识别(分值)、联系人、短信、经纬度(当前位置)、本机号码、通话记录、A01-百融风险数据流水号、A04-红星美凯龙APP、A601 魔蝎风险信息采集
     * 数据来源	    source	string	是	1-APP商户版、2-APP个人版、其他:同head头部channel
     * 信息列表(内容)content	List<String>	是，加密	转成JSON格式后：[“2”,”3”,”4”,”5”,”6”]，如果为星巢贷借款、登录事件上传，或者摩羯风险采集，规则如下：[{“content”:”3”,”reserved6”:”1”,”reserved7”:”1”},{“content”:”3”,”reserved6”:”2”,”reserved7”:”2”}]
     * 申请编号	    applSeq	string	否
     */
    public static final String URL_COMMIT_ORDER_RISK_INFO = "app/appserver/integration/fkb/commitApplAndUpdateRiskInfo";
    /**
     * 查询用户年龄是否为法定允许结婚年龄<br/>
     * 身份证号     certNo     String     是/加密
     */
    public static final String URL_USER_ALLOW_MARRIAGE = "app/appserver/userinfo/checkUserAgeIsAllowMarriage";
    /**
     * 还款金额计算接口 <br/>
     * 需计算还款金额借据     needCalculatedLoans     List     是
     * 还款借据类型     type     String     是     overdue: 逾期还款试算     all ： 全部借据还款
     * 身份证号     idNo     是
     * 客户号     custNo     是
     * 原接口："app/appserver/repayment/calculateRepaymentAmount";
     */
    public static final String URL_CALCULATE_REPAYMENT_AMOUNT = "app/appserver/repayment/repayCalNewService";

    public static final String URL_CHANGE_SYSTEM_CARD = "app/appserver/repayment/changeSystemDeductedCard";

    public static final String url_uploadLog = "app/appserver/fcf/applogrecord";//日志采集上送 2017-09-04

    //获取借款合同列表
    public static final String URL_CONTRACTS_LIST = "app/appserver/getContractList";

    /**
     * 检测用户年龄是否符合贷款要求--Get 请求
     * userBirthDay
     */
    public static final String URL_CHECK_USER_AGE = "app/appserver/v417/userinfo/fcf/checkuserage";
    /**
     * V 2.0新版改造
     * 用户id     custNo     String     否     要查询用户贷款品种时，custNo和typGrp必传
     * 贷款类别     typGrp     String     否     空：全部     01:耐用消费品贷款     02: 一般消费贷款
     * 用户身份证号     idNo     String     是
     */
    public static final String URL_HOME_INFO = "app/appserver/homepage/v4/getHomePageInfo";

    /**
     * 首页获取H5链接
     */
    public static final String URL_HOME_H5_DATA = "app/appserver/homepage/getInitH5HomeConfigure";
    /**
     * 查询借款用途（2021.5.8修改为新接口）
     */
    public static final String URL_PURPOSE_MAP = "app/appserver/fcf/getPurpose";

    /**
     * 消息列表查询<br/>
     * 用户id     userId     String     是
     * 查询类型     tagCode     String     是   1：通知 2：还款提醒 3：贷款进度 4：额度状态 5：优惠活动  6：我的交易
     * 页数     page     String     是
     * 每页显示条数     pageNum     String     是
     */
    public static final String URL_MESSAGE_LIST = "app/appserver/msgcenter/getMsgList";

    public static final String URL_MESSAGE_LIST_new = "app/appserver/customer/getMsgCenterListNew";

    /**
     * 设备类型上送接口
     * 用户ID     userId     String     是
     * 用户手机号     mobileNo     String     是
     * 身份证号     certNo     String     是
     * 手机型号     mobileType     String     是
     * 设备类型     deviceType     String     是     None, AND,IOS 只能是三者之一
     */
    public static final String URL_MESSAGE_DEVICE = "app/appserver/msgcenter/pushMsgDeviceInfo";
    // 查询省市区编码
    public static final String url_queryareacode = "app/appserver/appmanage/param/queryareacode";
    /**
     * 可配置文案(首页、借款结果页、申请结果页)<br/>
     * 文案位置 copyWriterPosition   String  是 客户端传递的参数要和配置的内容一致
     */
    public static final String URL_CONFIG_NOTICE = "app/appserver/common/getConfigurableCopyWriter";

    /**
     * 消息中心列表
     */
    public static final String HOME_NOTICE_LIST = "app/appmanage/homepagenotice/list";
    public static final String HOME_NOTICE_LIST_NEW = "app/appserver/inmail/getMessageList";
    /**
     * 首页，个人中心，消息列表未读消息数量
     */
    public static final String GET_UNREAD_MESSAGE_COUNT = "app/appserver/inmail/getUnReadCount";
    /**
     * 消息列表已读&一键已读
     */
    public static final String MESSAGE_READ = "app/appserver/inmail/messageRead";

    /**
     * 消息详情
     */
    public static final String MESSAGE_DETAIL_INFO = "app/appserver/inmail/getMessageDetail";

    //4.0.0.	(POST) 消息中心还款提醒和申请进度消息读取
    public static final String URL_getMsgCenterList = "app/appserver/customer/getMsgCenterListNew";

    //4.0.0.	(POST)确认消息已读
    public static final String URL_UPDATE_MSG_STATUS = "app/appserver/customer/setMsgIsReadNew";

    /**
     * 主动还款
     * "app/appserver/repayment/activeRepay"
     */
    public static final String URL_Active_Repayment = "app/appserver/repayment/activeRepayNewService";
    /**
     * 主动还款取消动作<p>
     * 还款流水号  repaySeq String  是/加密
     */
    public static final String URL_CANCEL_REPAYMENT = "app/appserver/repayment/cancelRepayment";
    /**
     * 批量查询还款状态接口 <p>
     * 查询参数     list   List<Map>  是<p>
     * 借据号 loanNo   String 否      两者不能同时为空
     * 申请流水号 applSeq String 否
     */
    public static final String URL_REPAYMENT_STATUS = "app/appserver/repayment/batchQueryRepaymentStatusNew";

    /**
     * 首页会员中心
     */
    public static final String URL_LEAGUER = BuildConfig.IS_RELEASE ? "https://www.goudzi.com/equityHome?homeId=16420957942964709" : ("sealingB".equals(BuildConfig.FLAVOR) ? "http://www-p2.goudzi.com/equityHome?homeId=16412738687486852" : "http://www-test.goudzi.com/equityHome?homeId=16406765031543429");//4.0.0换成够多智的

    /**
     * 首页信用卡
     */
    public static final String URL_CREDITCARD = "static/ghtest/credit.html#/";
    /**
     * 获取卡的业务信息
     */
    public static final String URL_BUSINESS = "app/appserver/bank/card/businessInfo";
    /**
     * 查询银行卡是否需要签约请求接口
     */
    public static final String URL_QUERY_NEED_SIGN = "app/appserver/smy/card/cardBatchRouter";
    /**
     * 业务签约请求接口
     */
    public static final String URL_SIGNING = "app/appserver/bank/card/signing/request";
    /**
     * 老卡签约
     */
    public static final String URL_RESIGN = "app/appserver/bank/card/signing/confirm/reSignCard";

    /* *************************************协议链接******************************************* */
    /**
     * 注册协议
     */
    public static final String URL_REGISTER_AGREEMENT = API_SERVER_URL + "app/appserver/register";
    /**
     * APP个人信息使用协议
     */
    public static final String URL_AGREEMENT_URL_1 = API_SERVER_URL + "app/appserver/personalInfoAuth";
    /**
     * 消费信贷服务协议
     */
    public static final String URL_AGREEMENT_URL_2 = API_SERVER_URL + "app/appserver/custCreditServiceAgreement";
    /**
     * 隐私协议
     */
    public static final String URL_AGREEMENT_URL_3 = API_SERVER_URL + "app/appserver/ghysxx";
    /**
     * 电子认证服务协议
     */
    public static final String URL_AGREEMENT_URL_4 = "app/appserver/cfcaAgreement";
    /**
     * 查询用户近七日账单列表（包含逾期数据，信贷100352提供原始数据）
     */
    public static final String URL_PERIODBILLS_OD = "app/appserver/bills/getPeriodBillsContainsOdByCmis";

    /**
     * 银行卡列表
     */
    public static final String URL_GETBANCARDLIST = "app/appserver/bank/card/getBankCardList";

    /**
     * 全部待还
     */
    public static final String URL_GETALLBILLS = "app/appserver/bills/getCustLoanBillsForUniteLoan";

    //银行卡更换协议
    public static final String URL_WITHHOLDING_AGREEMENT = API_SERVER_URL + "static/contract/changecard.html";
    /**
     * pdf插件下载路径
     */
    public static final String URL_PDF_MODEL = API_SERVER_URL + "static/gh_pdf_view/pdfjs.zip";

    /**
     * 无身份证照片的实名认证
     */
    public static final String URL_NAME_AUTH_NO_PHOTO = "app/appserver/identityverify/custIdentityVerify";


    /* *******************************************账户体系*****************************************************/
    /**
     * 检查手机号对应的用户状态（免Token）
     */
    public static final String POST_USER_STATUS = "app/appserver/user/status";
    //查询用户实名状态
    public static final String POST_USER_REAL_STATE = "app/appserver/login/getUserRealState";
    /**
     * 未登录用户实名信息校验（四要素）（免Token）
     */
    public static final String POST_FOUR_INFO = "app/appserver/user/checkFourKeysInfo";
    /**
     * （免Token）新账号体系的用户使用新设备登录流程中，根据实名信息，更新对应账号的登录手机号，同时添加新的设备号。
     * 通过实名信息认证渠道
     */
    public static final String POST_ChANGE_MOBILE_REALINFO = "app/appserver/user/changeCustMobileByRealInfo";
    /**
     * 更新手机号对应账号的登录密码（免Token） <BR/><BR/>新账号体系的忘记密码流程中，根据实名信息，更新手机号对应账号的登录密码。（haierUAC用户报错）
     */
    public static final String POST_UPDATE_LOGIN_PWD = "app/appserver/user/updateLoginPassword";
    /**
     * 根据实名信息，更新手机号对应账号的登录密码（免Token） <BR/><BR/>新账号体系的忘记密码流程中，根据实名信息，更新手机号对应账号的登录密码。（haierUAC用户报错）
     */
    public static final String POST_UPDATE_LOGIN_PWD_BY_REAL_INFO = "app/appserver/user/updateLoginPwByRealInfo";
    /**
     * 未登录用户实名信息校验（三要素）（免Token）<BR/><br/>账号体系中，验证手机号对应的四要素是否匹配
     */
    public static final String POST_CHECK_THREE_INFO = "app/appserver/user/checkThreeKeysInfo";
    /**
     * 根据实名信息，申诉登录手机号（免Token） <BR/><BR/>新账号体系的用户登录手机号申诉流程中，根据用户实名信息，解除用户手机号对应账号绑定关系，手机号后续可以注册新账户
     */
    public static final String POST_RELEASE_MOBILE = "app/appserver/user/releaseMobile";
    /**
     * 采集设备信息数据上送接口:上送百融数据或大数据
     */
    public static final String BR_BIG_DATA_COLLECT = "app/appserver/riskinfo/equipmentInfoCollect";
    /**
     * 我的背景图获取
     */
    public static final String MINE_BACKGROUND_IMG = API_SERVER_URL + "app/appmanage/mine/selectEffective";
    /**
     * 检测Manage是否配置了新的ICON
     */
    public static final String HOME_ICON_CHECK_URL = "app/appmanage/iconConfig/checkLatest";
    /**
     * app页面底部tab按钮配置接口
     */
    public static final String HOME_ICON_URL = "app/appmanage/iconConfig/latest";
    /**
     * 查询够花指定渠道导流开关信息 <p>
     * 渠道标识 channelFlag  String  是/否
     */
    public static final String URL_CHANNEL_SET = "app/appmanage/channelSetting/find";
    /**
     * 用户分享记录接口
     */
    public static final String USER_SHARE_RECORD = "app/appserver/newyearactivities/userShareRecord";

    /**
     * OCR 身份证识别
     */
    public static final String POST_SAVA_OCR = "app/appserver/ocr/saveOcrInfoForFacePlus";
    /**
     * 查询第三方额度申请记录接口
     */
    public static final String QUERY_APPL_RECOCED = "app/appserver/edappl/queryThirdPartEdApplReoced";
    /**
     * 新增第三方额度申请记录接口
     */
    public static final String ADD_APPLY_RECOCRD = "app/appserver/edappl/addThirdPartEdApplReoced";
    /**
     * 新增用户操作记录接口
     */
    public static final String ADD_USER_RECORD = "app/appserver/common/addUserOperationRecord";
    /**
     * 是否是贷超白名单
     * 白名单内的用户全面开启贷款超市栏目，不区分任何状态
     * 白名单控制范围：
     * 1）【信用生活】栏目，白名单用户全量可见，不区分用户状态——配置人员确保借钱频道内容已配置
     * 2）借钱频道内所有导流渠道白名单可见
     * 手机号      mobile   String     是
     */
    public static final String URL_IS_IN_WHITE_LIST = "app/appmanage/mobilewhitelist/isInList";
    /**
     * 开启灰度限制查询所有贷超产品信息
     */
    public static final String QUERY_GRAY_ALL = "app/appmanage/recommendChannel/allMyRecommendChannel";
    /**
     * 联合登陆生成用户信息签名信息
     */
    public static final String SIGN_DATA_FOR_UNITELOGIN = "app/appserver/userinfo/getSignDataForUniteLogin";

    /*
     * 热门推荐
     */
    public static final String GET_LIST_HOT = "app/appmanage/recommendChannel/listHot";

    /**
     * 获取指定的记录信息
     */
    public static final String GET_QUERYRECORD = "app/appserver/edappl/queryHasRecordByProductId";
    /**
     * 获取短信登陆验证码（APP）
     */
    public static final String POST_LOGIN_SMS_SEND = "app/appserver/login/getCaptchaForSmsLogin";

    /**
     * 获取短信验证码 （滑块校验）
     */
    public static final String POST_SMS_SENDVERIFY_BY_POST = "app/appserver/smsSendVerifyByPost";
    /**
     * 短信验证码登录
     */
    public static final String POST_SMS_LOGIN = "app/appserver/login/appSmsLogin";
    /**
     * 检测微信绑定状态接口
     */
    public static final String URL_POST_IS_BANDING = "app/appserver/login/wechatLogin/isBanding";
    /**
     * 微信绑定及注册或登陆接口
     */
    public static final String URL_POST_LOGIN_REGISTER = "app/appserver/login/wechatLogin/loginOrRegist";
    /**
     * app监控告警日志接口
     */
    public static final String EXTERNAL_ALARM_LOG = "app/appserver/logger/externalAlarmLog";
    /**
     * 获取用户结清借据列表
     */
    public static final String POST_DISCHARGE_LIST = "app/appserver/loan/getSettlementLoansNew";
    /**
     * 结清证明申请开具接口
     */
    public static final String DISCHARGE_ORDER_SUBMIT = "app/appserver/loan/settlementLoanSignNew";
    /**
     * 是否可以提额查询 <p>
     * 客户姓名         custName      String      是<p>
     * 客户身份证号     certNo        String      是
     */
    public static final String URL_CREDIT_PROMOTION = "app/appserver/creditlimit/getCreditLimitPromotionInfo";
    /**
     * 用户全部账单待还金额查询
     * 客户身份证号     certNo        String      是/加密
     */
    public static final String URL_ALL_BILL_AMOUNT = "app/appserver/bills/getAllBillAmount";
    /**
     * 获取优惠券列表
     */
    //public static final String URL_ALL_MEMBER_COUPONS = "app/appserver/member/getMemberCoupons";
    public static final String URL_ALL_MEMBER_COUPONS = "app/appserver/personalcenter/getMemberCoupons";

    /**
     * 获取用户全部还款免息券接口
     */
    public static final String URL_ALL_REPAY_COUPONS = "app/appserver/smy/personal/getCustRepayCoupons";
    /**
     * 获取某个订单可使用全部还款免息券接口
     */
    public static final String URL_ALL_REPAY_COUPONS_FOR_ORDER = "app/appserver/smy/repay/v2/getCustRepayCouponsForOrder";

    /**
     * 获取逾期数据的绑定券，都不可用
     */
    public static final String URL_COUPONS_FOR_OVERDUE_ORDER = "app/appserver/repay/getCouponsForOverDueLoan";

    /**
     * 额度申请被拒后的权益发放 相关配置数据
     */
    public static final String URL_MEMBER_SHIP_SELECT = "app/appmanage/membership/select";
    /**
     * 用户账号是否可以注销验证接口
     */
    public static final String URL_LOGIN_OUT_VERIFY = "app/appserver/useraccount/cancellationVerify";
    /**
     * 账号申诉前置校验流程，校验是否有在途业务和未结清借据
     */
    public static final String URL_ACCOUNT_APPEAL_VERIFY = "app/appserver/accountcomplaint/inTransitBusinessCheck";
    /**
     * 用户状态查询
     */
    public static final String POST_USER_LEND_STATUS = "app/appserver/user/getUserStatus";
    /**
     * 是否需要登录-for烈熊联合登陆
     */
    public static final String URL_GET_CHECK_BILL_BEAR_LOGIN = "app/appserver/billbear/checkLoginStatusForBillBear";
    /**
     * 烈熊联合登陆接口
     */
    public static final String URL_GET_UNION_BILL_BEAR_LOGIN = "app/appserver/billbear/unionLoginForBillBear";
    /**
     * 用户待换卡借据列表
     */
    public static final String URL_POST_LOAN_BILL_CHANGE_CARD = "app/appserver/loanbill/getLoanBillsForChangeCard";
    /**
     * 批量更换用户借据代扣卡
     */
    public static final String URL_POST_BATCH_CHANGE_CARD = "app/appserver/loanbill/batchChangeLoanBillBankCard";
    /**
     * 联合登录协议
     */
    public static final String URL_LINK_UNION_LOGIN = API_SERVER_URL + "app/appserver/agreement/unionLogin";
    /**
     * 烈熊相关协议
     */
    public static final String URL_LINK_BILL_BEAR = API_SERVER_URL + "app/appserver/agreement/billBear";
    /**
     * 省市区编码查询接口
     */
    public static final String GET_AREA_CODE = "app/appserver/area/getAreaCode";

    /**
     * 4.0新增 个人中心页底部
     */
    public static final String BOTTOM_BANNER = "app/appserver/homepage/config/bottom";
    /**
     * 贷款计算器
     */
    public static final String POST_URL_LOAN_CALC = "app/appserver/common/getLoanCalcResult";
    /**
     * 4.0.0新增，根据扫码结果获取url
     */
    public static final String GET_SCAN_URL = "app/appserver/newpay/getProcessUrl";

    /**
     * 4.0.0新增，个人中心查询用户七日待还
     */
    public static final String GET_SEVEN_PAY = "app/appserver/bills/recentSevenAmount";
    /**
     * 4.0.0新增，个人中心查询我的借款url
     */
    public static final String GET_MY_LOAN_URL = "app/appserver/personal/viewConfig";
    /**
     * 个人中心数据查询接口
     */
    public static final String GET_PERSON_CENTER_INFO = "app/appserver/personal/getPersonalCenterInfo";

    /**
     * 4.0.0新增 弹窗获取
     */
    public static final String POST_POP_INFO = "app/appserver/common/getPopupInfo";
    /**
     * 4.0.0新增 弹窗点击关闭后的交互
     */
    public static final String POST_POP_INFO_CALLBACK = "app/appserver/common/popupCallBack";
    /**
     * 5.0.1新增 弹窗总的查询接口
     */
    public static final String POST_POP_INFO_NEW = "app/appserver/popup/getPopupInfo";
    /**
     * 5.0.1新增 弹窗记录接口
     */
    public static final String POST_POP_INFO_RECORD = "app/appserver/popup/writePopupRecord";

    /**
     * 4.1.0新增 协议组信息查询
     */
    public static final String POST_AGREEMENTS_INFO_URL = "app/appserver/smy/agreement/preview";

    /**
     * 4.1.0新增  资源位查询
     */
    public static final String POST_QUERY_RESOURCE_BY_PAGE = "app/appserver/resource/getResourceBitByPage";

    /**
     * 4.1.5 新增
     * 查询当前微信登录用户在消金是否已存在绑定过的微信
     */
    public static final String POST_CHECK_HAS_WECHAT_LOGIN = "app/appserver/wechatlogin/checkIfCurrentUserExist";

    /**
     * 批量签章
     */
    public static final String POST_SIGN_AGREEMENTS = "app/appserver/signAgreementList";
    /**
     * 中收项目4.1.6新增
     */
    public static final String POST_QUERY_COUPON_DETAIL = "app/appserver/smy/personal/getCustCouponDetail";

    //查询固定场景下需要展示的协议列表接口请求地址
    public static final String URL_GET_QUERY_AGREEMENT_LIST = "app/appserver/queryAgreementList";
    //个性化状态查询
    public static final String POST_QUERY_RECOMMEND_STATE = "app/appserver/common/personalizedRecommend/query";
    //更改个性化状态
    public static final String POST_CHANGE_RECOMMEND_STATE = "app/appserver/common/personalizedRecommend/config";
    /**
     * 查询优惠卷数量
     */
    public static final String POST_QUERY_COUPON_NUMBER = "app/appserver/loan/getCouponNumber";

    /**
     * 防止获取不到实名信息和流水号，调用接口获取
     */
    public static final String GET_HOME_CUST_INFO = "app/appserver/homepage/getHomeCustInfo";

    /**
     * 第三方sdk报错上报接口
     */
    public static final String POST_APP_ACTION_LOG = "app/appserver/logger/appActionLog";

    /**
     * 账号申诉实名信息校验
     */
    public static final String POST_APPEAL_CERT_INFO_CHECK = "app/appserver/accountcomplaint/certInfoCheck";
    /**
     * 账号申诉提交
     */
    public static final String POST_APPEAL_COMMIT = "app/appserver/accountcomplaint/commit";

    //获取首页配置信息
    public static final String POST_HOME_CONFIG_INFO = "app/appserver/home/v5/getHomePageConfigInfoWithParam";

    //获取首页额度相关信息
    public static final String POST_HOME_CREDIT_INFO = "app/appserver/home/v5/getHomePageCreditInfo";

    //供ClickCouponToUseUtil使用的获取额度信息的接口
    public static final String POST_CREDIT_INFO = "app/appserver/coupon/getCreditStateInfo";

    /**
     * push消息详情
     */
    public static final String POST_PUSH_DETAIL_INFO = "app/appserver/inmail/getDetailForPush";

    /**
     * 领取营销弹窗免息券
     */
    public static final String POST_RECEIVE_COUPON = "app/appserver/popup/receiveCoupon";

    /**
     * 撤销隐私授权
     */
    public static final String POST_REPEAL_AGREEMENT = "app/appserver/repealAgreement";


    /**
     * 个人中心还款卡片按钮跳转
     */
    public static final String POST_GO_REPAY = "app/appserver/multi/personalCenterRepayCard";

    /**
     * 场景半进件弹窗退出记录
     */
    public static final String ORDER_REJECT_MARK = "app/appserver/order/scene/orderRejectMark";
}