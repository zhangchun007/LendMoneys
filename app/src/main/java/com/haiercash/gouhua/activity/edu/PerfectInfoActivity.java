package com.haiercash.gouhua.activity.edu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.bean.ArrayBean;
import com.app.haiercash.base.db.DbUtils;
import com.app.haiercash.base.db.dao.AddressDao;
import com.app.haiercash.base.db.database.AddressDatabase;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.permission.PermissionUtils;
import com.app.haiercash.base.utils.picker.IPickerSelectCallBack;
import com.app.haiercash.base.utils.picker.PopLeftTitleSelectPick;
import com.app.haiercash.base.utils.picker.PopSelectPick;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.SoftHideKeyBoardUtil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.AllCustInfo;
import com.haiercash.gouhua.beans.Dao_LianXiRen;
import com.haiercash.gouhua.beans.ModelBean;
import com.haiercash.gouhua.beans.WanShanXinXi_Bean;
import com.haiercash.gouhua.beans.risk.AppPerson;
import com.haiercash.gouhua.beans.risk.RiskBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.OccupationPop;
import com.haiercash.gouhua.uihelper.PermissionDenyDialog;
import com.haiercash.gouhua.utils.GhLocation;
import com.haiercash.gouhua.utils.RiskKfaUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.EduProgressBottomBarView;
import com.haiercash.gouhua.widget.DelEditText;
import com.tbruyelle.rxpermissions2.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/22
 * 描    述：个人资料
 * 修订历史：
 * ================================================================
 */
public class PerfectInfoActivity extends BaseActivity implements IPickerSelectCallBack, View.OnFocusChangeListener {
    public static final String ID = PerfectInfoActivity.class.getSimpleName();
    /**
     * 通讯录
     */
    private final int PHONE_NUM_REQUEST_CODE = 0x06;
    @BindView(R.id.et_occupation)
    TextView etOccupation;
    @BindView(R.id.et_work_name)
    DelEditText etWorkName;
    @BindView(R.id.et_salary)
    DelEditText etSalary;
    @BindView(R.id.et_work_phone)
    DelEditText etWorkPhone;
    @BindView(R.id.et_home_place)
    TextView etHomePlace;
    @BindView(R.id.et_home_intimate)
    DelEditText etHomeIntimate;
    @BindView(R.id.et_marriage_status)
    TextView etMarriageStatus;
    @BindView(R.id.et_email)
    DelEditText etEmail;
    @BindView(R.id.bt_next)
    TextView btNext;
    @BindView(R.id.et_contact_relation)
    TextView etContactRelation;
    @BindView(R.id.et_contact_relation2)
    TextView etContactRelation2;
    @BindView(R.id.et_contact_name)
    DelEditText etContactName;
    @BindView(R.id.et_contact_phone)
    TextView etContactPhone;
    @BindView(R.id.et_contact_name2)
    DelEditText etContactName2;
    @BindView(R.id.et_contact_phone2)
    TextView etContactPhone2;
    @BindView(R.id.progress)
    EduProgressBottomBarView progress;
    @BindView(R.id.des)
    TextView des;
    @BindView(R.id.et_education)
    TextView etEducation;

    private boolean isFirstContact;  //是否是选择第一联系人
    private String custNo;

    //居住信息
    private String liveProCode;
    private String liveCityCode;
    private String liveAreaCode;

    //联系人
    private Dao_LianXiRen contact;

    private ModelBean marriageBean = new ModelBean();
    private ModelBean contactBean = new ModelBean();
    private ModelBean eduBean = new ModelBean();
    /***Y：超过法定结婚年龄;N：未超过法定结婚年龄*/
    private boolean isAllowMarriageAge = false;
    private PopSelectPick popSelectPick;
    private PopLeftTitleSelectPick popSelectPickAddress;
    private AtomicBoolean mRefreshing = new AtomicBoolean(false);
    private Class mNextClass;
    private String fromProcedure;
    private String isEditorTag;
    private int selectTag;
    private ArrayList<Class> classes; //支用时候资料不足，需要的后续流程
    private boolean borrowStep;
    private boolean isFistApply = true; //第一次点击联系人1才申请通话记录授权

    @Override
    protected int getLayout() {
        return R.layout.activity_perfect_info;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setContactPhoneFocusable(false);
        setRightImage(R.drawable.iv_blue_details, ((v) -> ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation()));
        SoftHideKeyBoardUtil.assistActivity(this);
        if (getIntent() != null) {
            isEditorTag = getIntent().getStringExtra("isEditorTag");
        }
        custNo = SpHp.getUser(SpKey.USER_CUSTNO);
        mNextClass = (Class) getIntent().getSerializableExtra(ID);
        classes = (ArrayList<Class>) getIntent().getSerializableExtra("followStep");
        borrowStep = getIntent().getBooleanExtra("borrowStep", false);
        setTitle();
        btNext.setTypeface(FontCustom.getMediumFont(this));
        etContactName.addFocusChangeListener(this);
        etContactName2.addFocusChangeListener(this);
        getPerfectInfo();
        getAllowMarriage();
        etContactName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isFistApply) {
                    isFistApply = false;
                    requestPermission((Consumer<Boolean>) aBoolean -> {

                    }, R.string.permission_read_call_log, Manifest.permission.READ_CALL_LOG);
                }
                return false;
            }
        });
        //实名信息页面--个人资料页面不可编辑
        if (!CheckUtil.isEmpty(isEditorTag) & "N".equals(isEditorTag)) {
            des.setVisibility(View.GONE);
            etOccupation.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            etHomePlace.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            etMarriageStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            etContactRelation.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            etContactPhone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            etContactRelation2.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            etContactPhone2.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            setEditTextEnable(etWorkName, etWorkPhone, etHomeIntimate, etContactName, etContactName2, etSalary, etEmail);
            etOccupation.setClickable(false);
            etHomePlace.setClickable(false);
            etMarriageStatus.setClickable(false);
            etContactRelation.setClickable(false);
            etContactRelation2.setClickable(false);
            etContactPhone.setClickable(false);
            etContactPhone2.setClickable(false);
            etContactPhone.setEnabled(false);
            etContactPhone2.setEnabled(false);
            etContactName.setEnabled(false);
            etContactName2.setEnabled(false);
            etEducation.setEnabled(false);
            btNext.setVisibility(View.GONE);
            setRightImageCloseVisibility(false);
        }
    }

    /**
     * 设置EditeText是否可编辑
     * mode true可编辑，false不可编辑
     */
    private void setEditTextEnable(EditText... editTexts) {
        if (editTexts != null) {
            for (EditText editText : editTexts) {
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
                editText.setLongClickable(false);
                //editText.setInputType(false ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
                editText.setInputType(InputType.TYPE_NULL);
            }
        }
    }

    private void setTitle() {
        fromProcedure = getIntent().getStringExtra("tag");
        EduCommon.setTitle(this, progress);
        if (classes != null || borrowStep) {
            setTitle("完善信息");
        }
    }


    /**
     * 拉取用户的当前信息
     */
    private void getPerfectInfo() {
        //因服务端数据保存的是code，要显示在界面上需要转换为对应的name,所以需要确保先获取字典项
        if (!PerfectInfoDicHelper.getInstance().isHasData(PerfectInfoDicHelper.TAG_DUTY)) {
            showProgress(true);
            PerfectInfoDicHelper.getInstance().requestDics(new INetResult() {
                @Override
                public void onSuccess(Object response, String flag) {
                    requestCustExtInfo();
                }

                @Override
                public void onError(BasicResponse error, String url) {
                    showProgress(false);
                    showDialog(error.getHead().getRetMsg());
                }
            });
        } else {
            requestCustExtInfo();
        }
    }

    /**
     * 查询用户是否达到法定结婚年龄
     */
    private void getAllowMarriage() {
        Map<String, String> map = new HashMap<>();
        map.put("certNo", EncryptUtil.simpleEncrypt(SpHp.getUser(SpKey.USER_CERTNO)));
        netHelper.getService(ApiUrl.URL_USER_ALLOW_MARRIAGE, map);
    }

    private GhLocation ghLocation;

    private void showLivePicker(View view, Permission permission) {
        if (popSelectPickAddress == null) {
            popSelectPickAddress = new PopLeftTitleSelectPick(this, null, this);
        }
        View subV = LayoutInflater.from(this).inflate(R.layout.layout_picker_sub_location, null, false);
        TextView tvLocationDesc = subV.findViewById(R.id.tvLocationDesc);
        TextView tvRelocation = subV.findViewById(R.id.tvRelocation);
        TextView tvOpenLocation = subV.findViewById(R.id.tvOpenLocation);
        if (permission.granted) {
            tvRelocation.setVisibility(View.VISIBLE);
            tvOpenLocation.setVisibility(View.GONE);
            if (ghLocation == null) {//有权限自动定位
                ghLocation = new GhLocation(this, null);
            }
            ghLocation.setCallBack(new GhLocation.ILocationCallBack() {
                @Override
                public void callBack(boolean isSuccess, String reason) {
                    if (isSuccess) {
                        SpHelper spLocation = SpHelper.getInstance();
                        String province = spLocation.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_PEOVINCENAME);
                        String city = spLocation.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_CITYNAME);
                        String district = spLocation.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_AREANAME);
                        String addressTmp = getLiveAddressFromLocation(province, city, district, false);
                        //跟server提供的省市区信息有匹配才算定位成功，否则算是定位失败
                        if (!CheckUtil.isEmpty(addressTmp) && tvLocationDesc != null) {
                            tvLocationDesc.setText(UiUtil.getStr(province, city, district));
                            tvLocationDesc.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popSelectPickAddress.dismiss();
                                    String address = getLiveAddressFromLocation(province, city, district, true);
                                    etHomePlace.setText(address);
                                }
                            });
                            return;
                        }
                    }
                    if (tvLocationDesc != null) {
                        tvLocationDesc.setText("无法获取位置信息");
                        tvLocationDesc.setOnClickListener(null);
                    }
                }
            });
            ghLocation.requestLocationNoPermission();
            tvRelocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvLocationDesc != null) {
                        tvLocationDesc.setText("正在获取位置...");
                        tvLocationDesc.setOnClickListener(null);
                    }
                    ghLocation.requestLocationNoPermission();
                }
            });
        } else {
            tvLocationDesc.setText("定位未开启");
            tvRelocation.setVisibility(View.GONE);
            tvOpenLocation.setVisibility(View.VISIBLE);
            tvOpenLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popSelectPickAddress.dismiss();
                    PermissionUtils.gotoPermissionSetting(mContext);
                }
            });
        }
        popSelectPickAddress.addSubView(subV, true);
        popSelectPickAddress.showProvince(view, "居住地址");
    }

    private OccupationPop occupationPop;

    /**
     * 职业选择弹窗
     */
    private void showOccupationPicker(View view) {
        if (occupationPop == null) {
            Map<String, String> map = PerfectInfoDicHelper.getInstance().getCOM_KIND();
            if (map != null && map.size() > 0) {
                List<ModelBean> occupations = new ArrayList<>();
                for (String key : map.keySet()) {
                    occupations.add(new ModelBean(key, map.get(key)));
                }
                occupationPop = new OccupationPop(this, occupations, bean -> {
                    etOccupation.setText(bean != null ? bean.name : "");
                    etOccupation.setTag(bean != null ? bean.code : null);
                });
            } else {
                return;
            }
        }
        occupationPop.showOccupationPop(view, etOccupation.getText().toString());
    }

    @OnClick({R.id.et_home_place, R.id.et_education, R.id.et_marriage_status, R.id.et_contact_relation,
            R.id.et_contact_relation2, R.id.et_contact_phone, R.id.et_contact_phone2, R.id.bt_next,
            R.id.et_occupation})
    public void viewOnClick(View view) {
        if (!mRefreshing.compareAndSet(false, true)) {
            return;
        }
        switch (view.getId()) {
            case R.id.et_occupation://职业
                selectTag = PerfectInfoDicHelper.TAG_DUTY;
                selectPerfectInfo("职业");
                break;
            case R.id.et_home_place://居住地址-》省市区
                selectTag = PerfectInfoDicHelper.TAG_LIVE;
                requestPermissionEachCombined(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        showLivePicker(view, permission);
                    }
                }, R.string.permission_location, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
                break;
            case R.id.et_education://婚姻状况
                selectTag = PerfectInfoDicHelper.TAG_EDU;
                selectPerfectInfo("学历");
                break;
            case R.id.et_marriage_status://婚姻状况
                selectTag = PerfectInfoDicHelper.TAG_MARITALSTATUS;
                selectPerfectInfo("婚姻状况");
                break;
            case R.id.et_contact_relation://关系
                selectTag = PerfectInfoDicHelper.TAG_RELATION;
                selectPerfectInfo("关系");
                break;
            case R.id.et_contact_relation2://关系
                selectTag = PerfectInfoDicHelper.TAG_RELATION2;
                selectPerfectInfo("关系");
                break;
            case R.id.et_contact_phone://联系人手机
                isFirstContact = true;
                if (!etContactPhone.isFocusable()) {
                    getReadContacts();
                } else {
                    setContactPhoneFocusable(true);
                    etContactPhone.setFocusableInTouchMode(true);
                }
                break;
            case R.id.et_contact_phone2://联系人手机
                isFirstContact = false;
                if (!etContactPhone2.isFocusable()) {
                    getReadContacts();
                } else {
                    setContactPhoneFocusable(true);
                    etContactPhone2.setFocusableInTouchMode(true);
                }
                break;
            case R.id.bt_next:
                if (!checkMessgeCompleted()) {
                    //存在验证不通过
                    UMengUtil.commonClickCompleteEvent("PersonalData", "完成", "false", "资料验证未通过", getPageCode());
                    mRefreshing.set(false);
                    return;
                }
                new GhLocation(this, true, (isSuccess, reason) -> {
                    if (isSuccess) {
                        requestSaveCustExtInfo();
                    } else {
                        UMengUtil.commonClickCompleteEvent("PersonalData", "完成", "false", "位置获取异常", getPageCode());
                        showProgress(false);
                        showDialog(reason);
                    }
                }).requestLocation();
                break;
            default:
                finish();
                break;
        }
        mRefreshing.set(false);
    }


    @Override
    protected void onPause() {
        super.onPause();
        KeyBordUntil.hideKeyBord2(this);
    }

    @Override
    protected String getPageCode() {
        return "PersonalDataPage";
    }

    /**
     * 读取联系人
     */
    private void getReadContacts() {
        KeyBordUntil.hideKeyBord2(this);//避免软键盘占位不消失而留下空白，所以特意加上这行代码主动消失
        requestPermissionEachCombined(((Consumer<Permission>) permission -> {
            if (permission.granted) {
                RiskNetServer.startRiskServer2(this, "choose_contact", "");
                postReadContactEvent("true");
                try {
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                            ContactsContract.Contacts.CONTENT_URI), PHONE_NUM_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    setContactPhoneFocusable(true);
                }
            } else if (!permission.shouldShowRequestPermissionRationale) {
                //勾选了不再提示
                SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(this, "您还没有开启通讯录访问权限，开启后即可选择联系人电话哦～请您在")
                        .append("“系统设置-隐私-通讯录”").setForegroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .append("中开启权限").setForegroundColor(ContextCompat.getColor(this, R.color.color_303133));
                new PermissionDenyDialog(this)
                        .setPermissionTitle("开启通讯录访问权限")
                        .setPermissionContent(builder.create())
                        .setOnClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 1) {
                                    PermissionUtils.gotoPermissionSetting(mContext);
                                } else if (which == 2) {
                                    postReadContactEvent("false");
                                    setContactPhoneFocusable(true);
                                }
                            }
                        }).show();
            } else {
                postReadContactEvent("false");
                setContactPhoneFocusable(true);
            }
        }), R.string.permission_read_contact, Manifest.permission.READ_CONTACTS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHONE_NUM_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> userNumber = PerfectInfoHelper.getPhoneMailList(this, data);
            if (userNumber.isEmpty()) {
                //showDialog("请确保通讯录权限已经开启，并选择正确的联系人手机号码");
                setContactPhoneFocusable(true);
                return;
            }
            //如果通讯录选择的联系人姓名不为空且页面对应联系人姓名为空，则填充页面对应位置
            String contractName = data.getStringExtra("contractName");
            if (!TextUtils.isEmpty(contractName)) {
                if (isFirstContact) {
                    if (TextUtils.isEmpty(etContactName.getInputText())) {
                        etContactName.setText(contractName);
                        etContactName.setSelection(etContactName.getInputText().length());
                    }
                } else {
                    if (TextUtils.isEmpty(etContactName2.getInputText())) {
                        etContactName2.setText(contractName);
                        etContactName2.setSelection(etContactName2.getInputText().length());
                    }
                }
            }
            if (userNumber.size() == 1) {
                if (PerfectInfoHelper.checkContactPhone(userNumber.get(0)) &&
                        PerfectInfoHelper.checkContactPhone(userNumber.get(0), isFirstContact ? etContactPhone2.getText().toString() : etContactPhone.getText().toString())) {
                    if (isFirstContact) {
                        etContactPhone.setText(userNumber.get(0));
                    } else {
                        etContactPhone2.setText(userNumber.get(0));
                    }
                }
            } else {
                choosePhoneNumber(userNumber);
            }
        }
    }

    private void choosePhoneNumber(final List<String> userNumber) {
        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(this);
        singleChoiceDialog.setTitle("请选择手机号码");
        //noinspection ToArrayCallWithZeroLengthArrayArgument
        singleChoiceDialog.setSingleChoiceItems(userNumber.toArray(new String[userNumber.size()]), 0,
                ((dialog, which) -> {
                    dialog.dismiss();
                    String phone = userNumber.get(which);
                    if (PerfectInfoHelper.checkContactPhone(phone) &&
                            PerfectInfoHelper.checkContactPhone(phone, isFirstContact ? etContactPhone2.getText().toString() : etContactPhone.getText().toString())) {
                        if (isFirstContact) {
                            etContactPhone.setText(phone);
                        } else {
                            etContactPhone2.setText(phone);
                        }
                    }
                }));
        singleChoiceDialog.show();
    }

    /**
     * 进行数据字典是否为空判断
     **/
    private void selectPerfectInfo(final String titleName) {
        //如果数据为空
        if (!PerfectInfoDicHelper.getInstance().isHasData(selectTag)) {
            showProgress(true);
            PerfectInfoDicHelper.getInstance().requestDics(new INetResult() {
                @Override
                public void onSuccess(Object response, String flag) {
                    showProgress(false);
                    gotoSelectActivity(titleName);
                }

                @Override
                public void onError(BasicResponse error, String url) {
                    showProgress(false);
                    showDialog(error.getHead().getRetMsg());
                }
            });
        } else {//如果数据不为空
            gotoSelectActivity(titleName);
        }
    }

    /**
     * 选择器：职业、婚姻状态选择、关系选择
     */
    private void gotoSelectActivity(String titleName) {
        if ("职业".equals(titleName)) {
            showOccupationPicker(etOccupation);
            return;
        }
        Map<String, String> map = PerfectInfoDicHelper.getInstance().getSelectData(selectTag);
        if (map == null) {
            return;
        }
        //如果未到结婚法定年龄，则不允许选择“已婚”
        if (selectTag == PerfectInfoDicHelper.TAG_MARITALSTATUS && isAllowMarriageAge) {
            //已婚
            map.remove("20");
            //离异
            map.remove("40");
            //丧偶
            map.remove("50");
        }
        String marital = !marriageBean.isEmpty() ? marriageBean.name : "";
        List<ArrayBean> dataList = new ArrayList<>();

        for (String key : map.keySet()) {
            //如果婚姻状况为：未婚/丧偶/离异 则屏蔽夫妻关系
            if (selectTag == PerfectInfoDicHelper.TAG_RELATION && !TextUtils.isEmpty(marital)
                    && !"已婚".equals(marital) && "06".equals(key)) {
                continue;
            } else if (selectTag == PerfectInfoDicHelper.TAG_RELATION && !TextUtils.isEmpty(marital)
                    && "已婚".equals(marital)) {
                if ("06".equals(key)) {
                    dataList.add(new ArrayBean(key, map.get(key)));
                }
                continue;
            } else if (selectTag == PerfectInfoDicHelper.TAG_RELATION2 && "06".equals(key)) {
                continue;
            }
            dataList.add(new ArrayBean(key, map.get(key)));
        }
        if (popSelectPick == null) {
            popSelectPick = new PopSelectPick(this, null, this);
        }
        popSelectPick.showSelect(etMarriageStatus, titleName, dataList);
    }

    /**
     * 初始化加载用户曾经填入的信息
     */
    private void requestCustExtInfo() {
        if (CheckUtil.isEmpty(custNo)) {
            showDialog("账号异常，请退出重试");
            return;
        }
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("custNo", custNo);
        netHelper.getService(ApiUrl.url_wanshanxinxi_all, map, WanShanXinXi_Bean.class, true);
    }

    /**
     * 数据保存-》请求保存客户扩展信息
     */
    private void requestSaveCustExtInfo() {
        AllCustInfo params = new AllCustInfo();
        params.setCustNo(custNo);
        if (etOccupation != null) {
            String occName = etOccupation.getText().toString();
            String occCode = (String) etOccupation.getTag();
            if (CheckUtil.isEmpty(occCode)) {//匹配职业code
                Map<String, String> com_kind = PerfectInfoDicHelper.getInstance().getCOM_KIND();
                if (com_kind != null) {
                    for (String key : com_kind.keySet()) {
                        if (occName.equals(com_kind.get(key))) {
                            occCode = key;
                            break;
                        }
                    }
                }
            }
            if (occCode != null) {
                params.setOfficeTyp(occCode);
            }
        }
        if (etWorkName != null) {
            params.setOfficeName(RSAUtils.encryptByRSA(etWorkName.getInputText()));
        }
        if (etWorkPhone != null) {
            params.setOfficeTel(RSAUtils.encryptByRSA(etWorkPhone.getInputText().replaceAll("-", "")));
        }
        params.setMaritalStatus(marriageBean.code);
        params.setLiveProvince(liveProCode);
        params.setLiveCity(liveCityCode);
        params.setLiveArea(liveAreaCode);
        if (etSalary != null) {
            params.setMthInc(etSalary.getInputText());
        }
        if (etEmail != null) {
            params.setEmail(RSAUtils.encryptByRSA(etEmail.getInputText()));
        }
        params.setEducation(eduBean.code);
        if (etHomeIntimate != null) {
            params.setLiveAddr(RSAUtils.encryptByRSA(etHomeIntimate.getInputText()));
        }
        params.setDataFrom("app_person");
        netHelper.postService(ApiUrl.URL_SAVE_ALL_CUST_EXT_INFO, params);
    }

    /**
     * 联系人添加
     */
    private void saveCustFCiCustContact() {
        Map<String, String> map = new HashMap<>();
        map.put("custNo", custNo);
        map.put("contactName", RSAUtils.encryptByRSA(etContactName.getInputTextReplace()));//联系人名称
        map.put("contactMobile", RSAUtils.encryptByRSA(etContactPhone.getText().toString().trim()));//联系人手机
        map.put("relationType", contactBean.code);//关系类型
        map.put("contactName2", RSAUtils.encryptByRSA(etContactName2.getInputTextReplace()));//联系人名称
        map.put("contactMobile2", RSAUtils.encryptByRSA(etContactPhone2.getText().toString().trim()));//联系人手机
        map.put("relationType2", contactBean.code2);//关系类型
        if (contact != null) {
            map.put("id", String.valueOf(contact.id));
        }
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.url_lianxiren_post, map);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.url_wanshanxinxi_all.equals(url)) {
            showProgress(false);
            return;
        } else if (ApiUrl.url_lianxiren_post.equals(url)) {
            postErrMsg(error.getHead().getRetMsg());
        }
        super.onError(error, url);
    }

    /**
     * 根据网络信息填充界面
     */
    private void fillView(WanShanXinXi_Bean map) {
        //职业
        String occCode = map.getOfficeTyp();
        if (!CheckUtil.isEmpty(occCode)) {//匹配职业code,显示名称
            Map<String, String> com_kind = PerfectInfoDicHelper.getInstance().getCOM_KIND();
            if (com_kind != null) {
                for (String key : com_kind.keySet()) {
                    if (occCode.equals(key)) {
                        etOccupation.setText(com_kind.get(key));
                        etOccupation.setTag(key);
                        break;
                    }
                }
            }
        }
        //单位信息
        if (!CheckUtil.isEmpty(map.getOfficeName())) {
            //工作单位
            etWorkName.setText(map.getOfficeName());
        }
        //单位电话
        String unitPhone = map.getOfficeTel();
        if (!CheckUtil.isEmpty(unitPhone)) {
            unitPhone = unitPhone.trim().replaceAll("-", "");
            etWorkPhone.setText(CheckUtil.clearBlank(unitPhone));
        }

        //个人信息
        String marrige = map.getMaritalStatus();//婚姻状况
        if (!CheckUtil.isEmpty(marrige)) {
            marriageBean.code = marrige;
            marriageBean.name = PerfectInfoDicHelper.getInstance().getMARR_STS().get(marriageBean.code);
            if (!CheckUtil.isEmpty(marriageBean.name)) {
                etMarriageStatus.setText(marriageBean.name);
            }
        }
        //月收入
        if (!CheckUtil.isEmpty(map.getMthInc())) {
            etSalary.setText(map.getMthInc());
        }
        //学历
        String edu = map.getEducation();//婚姻状况
        if (!CheckUtil.isEmpty(edu)) {
            eduBean.code = edu;
            eduBean.name = PerfectInfoDicHelper.getInstance().getEDU_TYP().get(eduBean.code);
            if (!CheckUtil.isEmpty(eduBean.name)) {
                etEducation.setText(eduBean.name);
            }
        }
        //居住省
        liveProCode = CheckUtil.optText(map.getLiveProvince(), liveProCode);
        //居住市
        liveCityCode = CheckUtil.optText(map.getLiveCity(), liveCityCode);
        //居住区
        liveAreaCode = CheckUtil.optText(map.getLiveArea(), liveAreaCode);
        String liveAddress = getLiveAddress(liveProCode, liveCityCode, liveAreaCode);
        if (TextUtils.isEmpty(liveAddress)) {
            liveProCode = "";
            liveCityCode = "";
            liveAreaCode = "";
        } else {
            etHomePlace.setText(liveAddress);
        }
        if (!CheckUtil.isEmpty(map.getLiveAddr())) {
            etHomeIntimate.setText(map.getLiveAddr());
        }

        //联系人信息
        List<Dao_LianXiRen> contactList = map.getLxrList();
        if (contactList != null && contactList.size() > 1) {
            Dao_LianXiRen relation2 = contactList.get(1);
            etContactRelation2.setText(PerfectInfoDicHelper.getInstance().getRELATION().get(relation2.getRelationType()));
            etContactName2.setText(relation2.contactName);
            etContactPhone2.setText(CheckUtil.clearBlank(relation2.contactMobile));
            contactBean.code2 = relation2.getRelationType();
            contactBean.name2 = PerfectInfoDicHelper.getInstance().getRELATION().get(relation2.getRelationType());
        }
        if (contactList != null && contactList.size() > 0) {
            contact = getContactCouple(contactList);
            if (contact != null) {
                etContactRelation.setText(PerfectInfoDicHelper.getInstance().getRELATION().get(contact.getRelationType()));
                etContactName.setText(contact.contactName);
                etContactPhone.setText(CheckUtil.clearBlank(contact.contactMobile));
                contactBean.code = contact.getRelationType();
                contactBean.name = PerfectInfoDicHelper.getInstance().getRELATION().get(contact.getRelationType());
            }
        }

        etEmail.setText(CheckUtil.isEmpty(map.getEmail()) ? "" : map.getEmail());
        // etMonthIncome.setText(CheckUtil.isZero(map.getMthInc()) ? "" : map.getMthInc());
    }

    /**
     * 获取联系人中夫妻关系联系人
     *
     * @param contactList
     */
    private Dao_LianXiRen getContactCouple(List<Dao_LianXiRen> contactList) {
        Dao_LianXiRen contact = contactList.get(0);
        for (Dao_LianXiRen bean : contactList) {
            if ("06".equals(bean.relationType)) {
                contact = bean;
                break;
            }
        }
        return contact;
    }


    /**
     * 根据code获取居住地址
     *
     * @param proCode
     * @param cityCode
     * @param areaCode
     * @return
     */
    private String getLiveAddress(String proCode, String cityCode, String areaCode) {
        String address = "";
        if (CheckUtil.isEmpty(proCode)
                || CheckUtil.isEmpty(cityCode)
                || CheckUtil.isEmpty(areaCode)) {
            return address;
        }

        AddressDao addressDao = DbUtils.getAddress().addressDao();
        String privinceName = "";
        String cityName = "";
        String areaName = "";
        privinceName = addressDao.getAreaName(proCode);
        if (TextUtils.isEmpty(privinceName)) {
            return address;
        }


        cityName = addressDao.getAreaName(cityCode);
        if (TextUtils.isEmpty(cityName)) {
            return address;
        }


        areaName = addressDao.getAreaName(areaCode);
        if (TextUtils.isEmpty(areaName)) {
            return address;
        }

        return privinceName + cityName + areaName;
    }

    private String getLiveAddressFromLocation(String proName, String cityName, String areaName, boolean isResult) {
        String address = "";
        if (isResult) {
            liveProCode = null;
            liveCityCode = null;
            liveAreaCode = null;
        }
        if (CheckUtil.isEmpty(proName)
                || CheckUtil.isEmpty(cityName)
                || CheckUtil.isEmpty(areaName)) {
            return address;
        }

        AddressDatabase addressDao = DbUtils.getAddress();
        String privinceCode = addressDao.getProvinceCode(proName);
        if (TextUtils.isEmpty(privinceCode)) {
            return address;
        }
        if (cityName.equals(proName)) {
            cityName = "市辖区";
        }
        String cityCode = addressDao.getCityCode(privinceCode, cityName);
        if (TextUtils.isEmpty(cityCode)) {
            return address;
        }
        String areaCode = addressDao.getAreaCode(cityCode, areaName);
        if (TextUtils.isEmpty(areaCode)) {
            return address;
        }
        if (isResult) {
            liveProCode = privinceCode;
            liveCityCode = cityCode;
            liveAreaCode = areaCode;
        }
        return proName + cityName + areaName;
    }

    @Override
    public void onSuccess(Object response, String flag) {
        //解决：java.lang.NullPointerException:Attempt to invoke virtual method 'android.text.Editable android.widget.EditText.getText()' on a null object reference
        if (isFinishing()) {
            return;
        }
        if (ApiUrl.url_wanshanxinxi_all.equals(flag)) {
            //客户扩展信息查询
            showProgress(false);
            fillView((WanShanXinXi_Bean) response);
        } else if (ApiUrl.URL_SAVE_ALL_CUST_EXT_INFO.equals(flag)) {
            UMengUtil.commonClickCompleteEvent("PersonalData", "完成", "true", "", getPageCode());
            //RiskInfoUtils.requestRiskInfoContacts(this, "");
            saveCustFCiCustContact();
        } else if (ApiUrl.url_lianxiren_post.equals(flag)) {
            showProgress(false);
            if (mNextClass != null) {
                if (!ID.equals(mNextClass.getSimpleName())) {
                    Intent intent = getIntent();
                    intent.setClass(this, mNextClass);
                    startActivity(intent);
                }
                finish();
            } else {
                EduProgressHelper.getInstance().checkProgress(this, true);
            }
        } else if (ApiUrl.URL_USER_ALLOW_MARRIAGE.equals(flag)) {
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) response;
            isAllowMarriageAge = "N".equals(map.get("isAllowMarriageAge"));
        }
    }

    @Override
    public void timeSelect(Object... time) {
        if (selectTag == PerfectInfoDicHelper.TAG_MARITALSTATUS) {
            ArrayBean bean = (ArrayBean) time[0];
            marriageBean.code = bean.getCode();
            marriageBean.name = bean.getName();
            etMarriageStatus.setText(marriageBean.name);
            if ("20".equals(marriageBean.code)) {
                //选择了已婚则联系人中要有夫妻关系，若没有需自动填充夫妻，填充了夫妻关系的就不可点击了
                if ("06".equals(contactBean.code)) {
                    etContactRelation.setClickable(false);
                    etContactRelation.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                } else if ("06".equals(contactBean.code2)) {
                    etContactRelation2.setClickable(false);
                    etContactRelation2.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                } else {
                    etContactRelation.setText("夫妻");
                    contactBean.code = "06";
                    contactBean.name = "夫妻";
                    etContactName.setText("");
                    etContactPhone.setText("");
                    //为空时会被系统自动移除掉内嵌drawable
                    etContactPhone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.the_arrow_r_grey, 0);
                    etContactRelation.setClickable(false);
                    etContactRelation.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                }
            } else if ("06".equals(contactBean.code)) {
                //如果选择的不是已婚，那么联系人关系选择夫妻的联系人信息自动清空,且联系人关系变可点击
                etContactRelation.setText("");
                etContactName.setText("");
                etContactPhone.setText("");
                etContactPhone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.the_arrow_r_grey, 0);
                etContactRelation.setClickable(true);
                etContactRelation.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.the_arrow_r_grey, 0);
            } else if ("06".equals(contactBean.code2)) {
                //如果选择的不是已婚，那么联系人关系选择夫妻的联系人信息自动清空
                etContactRelation2.setText("");
                etContactName2.setText("");
                etContactPhone2.setText("");
                etContactPhone2.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.the_arrow_r_grey, 0);
                etContactRelation2.setClickable(true);
                etContactRelation2.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.the_arrow_r_grey, 0);
            }
        } else if (selectTag == PerfectInfoDicHelper.TAG_RELATION) {
            ArrayBean bean = (ArrayBean) time[0];
            contactBean.code = bean.getCode();
            contactBean.name = bean.getName();
            etContactRelation.setText(contactBean.name);
        } else if (selectTag == PerfectInfoDicHelper.TAG_RELATION2) {
            ArrayBean bean = (ArrayBean) time[0];
            contactBean.code2 = bean.getCode();
            contactBean.name2 = bean.getName();
            etContactRelation2.setText(contactBean.name2);
        } else if (selectTag == PerfectInfoDicHelper.TAG_LIVE) {//居住地址
            ArrayBean pBean = (ArrayBean) time[0];
            ArrayBean cBean = (ArrayBean) time[1];
            ArrayBean aBean = (ArrayBean) time[2];
            String placeAdd = pBean.getName() + cBean.getName() + aBean.getName();
            etHomePlace.setText(placeAdd);
            liveProCode = pBean.getCode();
            liveCityCode = cBean.getCode();
            liveAreaCode = aBean.getCode();
        } else if (selectTag == PerfectInfoDicHelper.TAG_EDU) {
            ArrayBean eBean = (ArrayBean) time[0];
            eduBean.code = eBean.getCode();
            etEducation.setText(eBean.getName());
        }
    }


    @Override
    public void onBackPressed() {
        if ("EDJH".equals(fromProcedure) && classes == null) {
            EduCommon.onBackPressed(this, "填写完资料", getPageCode(), "个人资料页面");
        } else {
            finish();
        }
    }


    /**
     * 检查信息完整性
     */
    private boolean checkMessgeCompleted() {
        //验证信息是否符合标准
        if (PerfectInfoHelper.isUnitPerfect(etOccupation, etWorkName, etSalary, etWorkPhone)
                && PerfectInfoHelper.isPersonalPerfect(etHomePlace, etHomeIntimate, etMarriageStatus)
                && PerfectInfoHelper.isExpandsPerfect(etEmail)
                && PerfectInfoHelper.checkPhoneNum(etContactPhone.getText().toString(), etContactPhone2.getText().toString())
                && PerfectInfoHelper.isContactPerfect(etContactName, etContactRelation, etContactPhone)
                && PerfectInfoHelper.isContactPerfect(etContactName2, etContactRelation2, etContactPhone2)
                && PerfectInfoHelper.checkContactPhone(etContactPhone.getText().toString(), etContactPhone2.getText().toString())
        ) {
            String contactPhone = etContactPhone.getText().toString().trim();
            String contactPhone2 = etContactPhone2.getText().toString().trim();
            String isSave = PerfectInfoHelper.checkPerfectInfo(contactPhone, marriageBean, liveProCode, liveCityCode,
                    liveAreaCode, contactBean, contactPhone2);
            if (!TextUtils.isEmpty(isSave)) {
                UiUtil.toast(isSave);
                return false;
            }
            ////获取用户的通讯录数据
            //Contact_Data.getPhoneContacts(this);
            ////如果获取到的通讯录数据小于10人，则跳转至指定页面
            //if (Contact_Data.mContactsNumber.size() <= 0) {
            //    showDialog("请确保通讯录权限已经开启");
            //    return false;
            //} else if (Contact_Data.mContactsNumber.size() < CONTACT_COUNT) {
            //    //1.2.0 需求
            //    Intent intent = new Intent(this, EduProgressActivity.class);
            //    intent.putExtra("Result", getString(R.string.edu_result_error));
            //    startActivity(intent);
            //    finish();
            //    return false;
            //}
            return true;
        } else {
            if (!CheckUtil.isEmpty(PerfectInfoHelper.errMsg)) {
                postErrMsg(PerfectInfoHelper.errMsg);
            }
        }
        return false;
    }

    //上送风险信息
    private void postErrMsg(String errMsg) {
        try {
            RiskKfaUtils.getRiskBean(this, -2, 3, "submission_personal_application", obj -> {
                RiskBean riskBean = (RiskBean) obj;
                if (riskBean != null) {
                    riskBean.setMessageType("USER_ACTION");
                    AppPerson appPerson = new AppPerson();
                    appPerson.setErr_message(errMsg);
                    appPerson.setContact_phone1(etContactPhone.getText().toString());
                    appPerson.setContact_phone2(etContactPhone2.getText().toString());
                    riskBean.setApp_person(appPerson);
                    RiskNetServer.startService(this, riskBean, "");
                }

            });
        } catch (Exception e) {
            Logger.e("submission_personal_application事件出了异常");
        }

    }

    private void setContactPhoneFocusable(boolean focusable) {
        etContactPhone.setFocusable(focusable);
        etContactPhone.setFocusableInTouchMode(focusable);
        etContactPhone2.setFocusable(focusable);
        etContactPhone2.setFocusableInTouchMode(focusable);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {//fragment销毁时会出现异常（也会走这里，并且linePhone为null）
            if (!hasFocus) {
                if (v.getId() == R.id.et_contact_name) {
                    if (!CheckUtil.isEmpty(etContactName.getInputText())) {
                        PerfectInfoHelper.checkContactNameChinese(etContactName);
                    }
                } else if (v.getId() == R.id.et_contact_name2) {
                    if (!CheckUtil.isEmpty(etContactName2.getInputText())) {
                        PerfectInfoHelper.checkContactNameChinese(etContactName2);
                    }
                }
            }
        } catch (Exception e) {
            Logger.e("fragment销毁而使设置了onFocusChange的View失去焦点");
        }
    }

    @Override
    protected void onDestroy() {
        if (popSelectPick != null) {
            popSelectPick.onDestroy();
            popSelectPick = null;
        }
        if (ghLocation != null) {
            ghLocation.onDestroy();
        }
        PerfectInfoDicHelper.getInstance().destory();
        EduProgressHelper.getInstance().onDestroy();
        super.onDestroy();
    }

    private void postReadContactEvent(String success) {
        Map<String, Object> map = new HashMap<>();
        map.put("is_success", success);
        UMengUtil.onEventObject("AddressPermissions", map, getPageCode());
    }
}
