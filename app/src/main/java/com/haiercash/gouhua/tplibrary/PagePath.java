package com.haiercash.gouhua.tplibrary;

import com.app.haiercash.base.utils.router.PageKeyPath;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/3/23<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class PagePath extends PageKeyPath {

    /* **************************************Activity**********************************************/

    //短信验证码登录
    public static final String ACTIVITY_SMS_LOGIN = "/gh/SmsWayLoginActivity";

    public static final String ACTIVITY_VERIFY_BIOMETRIC = "/enter/VerifyBiometricActivity";
    public static final String ACTIVITY_VERIFY_WX = "/enter/VerifyWxActivity";
    public static final String ACTIVITY_GESTURES_SECRET = "/enter/GesturesPasswordActivity";
    /**
     * 老用户绑定微信或新用户绑定微信
     */
    public static final String ACTIVITY_SOCIAL_BIND = "/user/SocialBindActivity";
    /**
     * 账号安全设置
     */
    public static final String ACTIVITY_SAFETY_SETTING = "/mine/SafetySettingActivity";
    /**
     * 个人中心工具服务
     */
    public static final String FRAGMENT_TOOLS = "/mine/ToolsFragment";
    /**
     * 分享
     */
    public static final String ACTIVITY_SHARE = "/gh/SharePageActivity";

    /* **************************************Fragment**********************************************/

    /**
     * 筛选条件
     */
    public static final String FRAGMENT_FILTER_CRITERIA = "/gh/FilterCriteriaFragment";
    /**
     * 由首页："会员" 点击过来的网页跳转
     */
    public static final String FRAGMENT_HOME_OTHER_WEB = "/gh/HomeOthersWebFragment";
    /**
     * 用于WebView的简单显示 如协议展示
     */
    public static final String FRAGMENT_WEB_SIMPLE = "/gh/WebSimpleFragment";
    /**
     * 设置
     */
    public static final String FRAGMENT_SETTING = "/mine/SettingFragment";
    /**
     * 消息中心
     */
    public static final String FRAGMENT_MESSAGE = "/mine/MessageFragment";
    /**
     * 公告详情
     */
    public static final String FRAGMENT_NOTICE = "/mine/NoticeFragment";
    /**
     * 我的留言+意见反馈
     */
    public static final String FRAGMENT_FEEDBACK = "/mine/FeedBackFragment";
    public static final String FRAGMENT_TMP = "/mine/TmpFragment";
    /**
     * 帮助中心 列表
     */
    public static final String FRAGMENT_HELPER_CENTER = "/mine/HelpCenterFragment";
    /**
     * 帮助中心 详情
     */
    public static final String FRAGMENT_HELPER_CENTER_DETAIL = "/mine/HelpCenterDetail";
    /**
     * 单个问题类型的常见问题
     */
    public static final String FRAGMENT_HELPER_CENTER_QUESTIONLIST = "/mine/HelpCenterQuestionList";
    /**
     * 关于我们
     */
    public static final String FRAGMENT_ABOUT_US = "/mine/AboutUsFragment";
    /**
     * 注销说明
     */
    public static final String FRAGMENT_CANCELLATION = "/mine/CancellationFragment";
    /**
     * 贷款计算器
     */
    public static final String FRAGMENT_CALCULATION = "/mine/LoanCalculationFragment";
    /**
     * 贷款计算器计算结果
     */
    public static final String FRAGMENT_CALCULATION2 = "/mine/LoanCalculationFragment2";
    /**
     * 券包
     */
    public static final String ACTIVITY_COUPON_BAG = "/mine/CouponBagActivity";
    /**
     * 券包V2,支持viewPager2左右滑动
     */
    public static final String ACTIVITY_COUPON_BAG_V2 = "/mine/CouponBagActivityV2";
    /**
     * 优惠券功能
     */
    public static final String FRAGMENT_DISCOUNT_COUPON = "/mine/coupon/DiscountCouponFragment";
    /**
     * 免息券
     */
    public static final String FRAGMENT_INTEREST_FREE_STAMPS = "/mine/coupon/InterestFreeStampsFragment";
    /**
     * 额度申请被拒权益发放
     */
    public static final String FRAGMENT_EQUITY_GRANT = "/edu/EquityGrantFragment";
    /**
     * 还款记录
     */
    public static final String ORDER_REPAY_LIST = "/order/RePayListFragment";
    /**
     * 借款记录
     */
    public static final String ORDER_RECORD_LIST = "/order/BorrowRecordFragment";
    /**
     * 提升额度页面
     */
    public static final String FRAGMENT_PROMOTE_LIMIT = "/main/PromoteLimitFragment";

    /**
     * 系统参数
     */
    public static final String FRAGMENT_ACRACK = "/main/ACrackFragment";
    /**
     * 关联贷款 --批量替换卡
     */
    public static final String FRAGMENT_LOAN_ASSOCIATION = "/card/LoanAssociationFragment";

}
