package com.haiercash.gouhua.interfaces;

/**
 * Created by zhangjie on 2017/6/5.
 * 定义一个接口，用来存放存取SP时的key值
 * 外部使用时，直接 SpKey. 即可
 * ex：SpKey.LOGIN
 */

public interface SpKey {
    /**
     * 登录相关信息
     */
    String LOGIN = "LOGIN";
    String LOGIN_USERID = "userId";//用户id
    String LOGIN_MOBILE = "login_mobile";//绑定手机号
    String LOGIN_PROCESS_ID = "login_process_id";//登录用户processId
    String LOGIN_PASSWORD_STATUS = "login_password_status";//用户密码状态，Y-有设置密码，N-没有设置密码
    String LOGIN_ACCESSTOKEN = "accessToken";//接口请求中用到的token

    String LOGIN_REGISTCHANNEL = "registChannel";//注册渠道

    String CHANNEL_NO = "channelNo";//渠道号

    String LOGIN_AUTHMESSAGE = "auth_card_info"; // 实名界面 身份证相关信息保存
    String LOGIN_STATUS = "userStatus";//用户当前的状态 正常/退回/取消/拒绝
    String USER_CRD_SEQ_RETURN = "UserCrdSeqReturn";//用户额度申请被退回时候得流水号
    String NOTICE_POINT_OPERATE = "NoticePointOperate";
    String WX_OPEN_ID = "WxOpenId";//微信分享所需入参openID
    String LOAN_MARKET_EDU_STATUS = "LoanMarketAndEduStatus";//贷款超市开关为Y并且manager配置额度状态包含当前用户状态   就设置为Y
    String IS_IN_WHITE_LIST = "IsInWhiteList";//是否为贷超白名单：白名单内的用户全面开启贷款超市栏目，不区分任何状态
    String ENABLE_MORE_PRODUCT = "enableMoreProduct";//是否开启更多产品   Y：是   N：否
    String ENABLE_LOAN_MARKET = "enableLoanMarket";///URL_CHANNEL_SET 接口获取的参数
    String EDU_STATUS = "eduStatus";///URL_CHANNEL_SET 接口获取的参数
    String VIP_STATUS = "vipStatus";//会员状态，Y-是会员，其他默认不是会员
    String HOME_CREDIT = "homeCredit";//首页查询的用户credit，见bean类Credit

    /**
     * 实名相关信息，启动页会清除该sp文件
     */
    String USER = "USER";
    String USER_CUSTNO = "custNo"; //客户编号
    String USER_CUSTNAME = "custName";//用户姓名
    String USER_CERTNO = "certNo";//证件号 身份证号
    String USER_MOBILE = "mobile";//实名认证手机号
    String USER_STATE_TYPE = "userStateType";//对应各个借款状态

    String USER_EDU_ALL = "userCrdAmt";//额度总金额
    String USER_EDU_AVLIABLE = "userCrdNorAmt";//剩余额度
    String USER_EDU_SPE_SEQ = "UserEduSpeSeq";//首页接口获取的额度编号，用于查询利率等信息

    /**
     * 位置相关信息，启动页会清除该sp文件
     */
    String LOCATION = "LOCATION";
    String LOCATION_LON = "longitude";//经度
    String LOCATION_LAT = "latitude";//纬度
    String LOCATION_PEOVINCENAME = "provinceName";//省名称
    String LOCATION_PROVINCECODE = "provinceCode";//省代码
    String LOCATION_CITYNAME = "cityName";//市名称
    String LOCATION_CITYCODE = "cityCode";//市代码
    String LOCATION_AREANAME = "areaName";//区名称
    String LOCATION_AREACODE = "areaCode";//区代码
    String LOCATION_UPDATE = "locationUpdateTime";//百度定位更新时间
    String LOCATION_JSON = "locationJson";//风险数据埋点所需的Json数据

    /**
     * 信息状态标识（是否登录，实名，有无交易密码等）等信息，启动页会清除该sp文件
     */
    String STATE = "STATE";
    String STATE_LOGINSTATE = "loginState";//是否登录（登录状态）Y：已登录，N：未登录
    String STATE_HASGESTURESPAS = "hasGesturesPas";//是否有手势密码。Y：有，N：没有，E：查询失败，默认没有
    String STATE_SHOW_GESTURE_WAY = "showGesturesWay";//是否展示手势轨迹。N：不展示，其他展示
    String STATE_HASPAYPAS = "hasPayPas";//是否有交易密码。Y：有，N：没有，E：查询失败

    /**
     * spKey为userId，userId对应的sp存储，退出登录后不清除
     * SpHp.getOther(SpKey.LAST_LOGIN_SUCCESS_USERID)
     */
    String STATE_HAS_BIOMETRIC = "hasBiometric";//是否设置了生物识别。Y：有，N：没有

    /**
     * 其他未分类信息
     */
    String OTHER = "OTHER";
    String OTHER_GUIDE_PAGE = "guidePage";//是否要进引导页，"N"是不要进，其余进
    String OTHER_DB_VERSION = "cityDbUpDateVersion";//数据库版本号
    String OTHER_PRIVACY = "privacyProtocolNew";//个人隐私协议:首页弹窗隐私协议
    String OTHER_TOURIST_MODE = "touristMode";//不同意隐私权,游客模式
    String OTHER_FIRST_ENTER = "firstEnter";//首次进入
    String VERSION_CANCEL_UPDATE_TIME = "versinCancelUpdateTime";
    String HOME_ICON_MD5 = "md5IconStyleContent";//首页Icon上一次被更改的标识
    String CREDIT_CARD_SWITCH = "creditCardSwitch";//信用卡开关
    String FEEDBACK_SWITCH = "feedbackSwitch";//反馈页面中是否允许上传图片
    String OTHER_BR_LOGIN = "br_login";//百融login
    String OTHER_BR_SWITCH = "BRSwitch";//百融开关
    String OTHER_VERSION_NUMBER = "version_number";//最新版本号
    String OTHER_CANCELLATION_SWITCH = "CancellationLogoutSwitch";//我的-安全中心-注销开关
    String OTHER_BILL_BEAR_SWITCH = "BillBearSwitch";// 首页烈熊浮动入口
    String CONFIGURE_SWITCH_WY_SLIDER = "dunSliderSwitch";//是否开网易启滑块验证，默认开启   Y 是； 其他  否
    String CONFIGURE_SWITCH_PERSONAL_RECOMMEND = "personalizedRecommendSwitch";//个性化推荐开关  Y 是； 其他  否
    String OTHER_CURRENT_URL = "currentUrl";//debug模式下切换环境，保存url
    String INTEREST_FREE_STAMPS_GUIDE = "INTEREST_FREE_STAMPS_GUIDE";//免息券引导页
    String ED_HAS_BACK = "ED_HAS_BACK";//申额流程返回弹窗标记，true已经跳转过，一次申额流程只弹挽留弹窗一次，注意初始化
    String BORROW_BANNER_HAS_JUMP = "BORROW_BANNER_HAS_JUMP";//借款页面banner跳转过标记，true已经跳转过，一次支用流程只弹挽留弹窗一次，注意初始化
    String WEB_CACHE_CURRENT = "webCacheCurrentTime";//WEBVIEW缓存的时间戳
    String LAST_LOGIN_SUCCESS_MOBILE = "lastLoginSuccessMobile";//本机最近一次登录成功的手机号
    String LAST_LOGIN_SUCCESS_USERID = "lastLoginSuccessUserId";//本机最近一次登录成功的userId

    String WY_DEVICEID_TOKEN = "wy_deviceid_token";//网易设备指纹存储的token
    String WY_DEVICEID_TOKEN_TIME = "wy_deviceid_token_time";//网易设备指纹存储的token时间戳

    String RED_BAG_SWTICH = "redBagSwtich";//个人中心红包开关
    String RED_BAG_URL = "redBagUrl";//个人中心红包url
    String MESSAGE_NOTIFICATION = "message_notification";//消息通知

    String TAG_PUSH_CONTENT_TYPE = "pushContentType";  //push的type
    String TAG_PUSH_CONTENT_VALUE = "pushContentValue";//push的值

    /**
     * 锁屏计时相关
     */
    String LOCK = "LOCK";
    String LOCK_CURRENT_TIME = "currentTime";//
    String AMBIENT_SPACE = "ambientSpace";
    String NOTICE_REMIND = "NoticeRemind";//消息推送开启权限通知：如果用户不开启通知提醒，则每一个月提醒一次

    String CHOOSE_SHOW_CONTROL = "choosePosition";
    String CHECK_POSITION = "check_bank_position";
    String CREATE_CALENDAR = "creat_calendar";
    String CREATE_CALENDAR_SHOW = "creat_calendar_show";

    /**
     * 个人征信授权失效
     */
    String PERSONAL_CREDIT_AUTHORIZATION = "PCA_TIMEOUT";
    String PERSONAL_CREDIT_AUTHORIZATION_STATUE = "PCA_STATUE";
    /**
     * 显示账单明细
     */
    String BILL_DETAIL_SHOW = "ishow";
    String BILL_DETAIL_SHOW_STATE = "show_state";

    /**
     * 临时方案，在应用开启时判断该参数是否被修改，没有的话就删除掉所有的SP
     */
    String TEMP = "temp";
    String TEMP_KEY = "temp_key1";
    /**
     * 记录当前设备点击微信登录前是否弹过窗
     */
    String WECHAT_LOGIN_DIALOG_STATE = "wechatLoginDialogState";
    String HAS_SHOW_WECHAT_LOGIN_DIALOG = "hasShowWechatLoginDialog";

    /**
     * 本地记录需要认证的手机号
     */
    String NEED_CHANGE_DEVICE_STATE = "changeDevice"; //保存需要设备认证的手机号和状态
    String NEED_VALIDATE = "needValidateNewDevice";  //是否需要认证，需要为Y
    String NEED_VALIDATE_PHONE = "needValidatePhone"; //需要认证的登录手机号

    String LATEST_AGREEMENT = "latestAgreement";  //最近请求成功的协议
    String LATEST_AGREEMENT_LIST = "latestAgreementList";

    String READ_PHONE_STATE = "readPhoneState";//读取设备状态
    String LAST_SHOW_DIALOG_TIME = "lastShowStateDialogTime"; //最后一次记录的时间

    /**
     * 本地记录当前token是否提示过开启快捷登录
     */
    String QUICK_LOGIN_STATE = "quickLoginState"; //保存需要当前token对应的弹窗状态
    String LATEST_LONG_TOKEN = "latestLongToken";  //最新的长token

    /**
     * 个人中心申额记录当前token
     */
    String QUICK_APPLY_STATE = "quickApplyState"; //保存需要当前token对应的弹窗状态
     String QUICK_APPLY_TOKEN= "quickApplyToken"; //保存需要当前token对应的弹窗状态

    /**
     * 消息通知
     */
    String MESSAGE_NOTIFICATION_STATE = "messageState"; //保存需要当前token对应的弹窗状态
     String MESSAGE_NOTIFICATION_TOKEN= "messageToken"; //保存需要当前token对应的弹窗状态


    /**
     * 本地记录当前权限是否申请过
     */
    String PERMISSION_STATE = "permissionState";

    /**
     * 本地记录还款记录的开始结束时间
     */
    String FILTER_CRITERIA_KEY = "filterCriteriaKey";
    String FILTER_CRITERIA_START_TIME = "filterCriteriaStartTime";
    String FILTER_CRITERIA_END_TIME = "filterCriteriaEndTime";

}
