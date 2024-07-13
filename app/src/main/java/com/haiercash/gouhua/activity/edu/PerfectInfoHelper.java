package com.haiercash.gouhua.activity.edu;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.beans.ModelBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/10/31
 * 描    述：
 * 修订历史：
 * ================================================================
 */
public class PerfectInfoHelper {
    private static final String unit = "请输入正确的工作单位";
    private static final String unit_error = "请输入2-50字的工作单位";
    //    private static final String unitAddress = "请选择正确的单位地址";
//    private static final String unitAddressDetail = "详细地址请输入4到50个字";
    private static final String phone = "请输入正确的座机号码";
    //private static final String phone_error = "请输入正确的座机号码";//电话号码格式不是11或12位时报出
    private static final String unitDuty = "请选择职务";
    private static final String income_empty = "请输入月收入";
    private static final String income_error = "请输入正确的月收入";
    private static final String income_error1 = "月收入金额需为1～100,000之间";
    private static final String personMarriage = "请选择婚姻状况";
    private static final String personLive = "请选择正确的居住地址";
    private static final String personLiveDetail = "请输入4到50字以内的详细地址";//没有输入详细地址时报出
    private static final String contactRelation = "请选择联系人关系";
    private static final String contactName = "请输入联系人姓名";
    private static final String contactNameChinese = "联系人姓名仅允许输入中文";
    private static final String contactNameReg = "请输入正确的联系人姓名";
    private static final String contactNameReg1 = "联系人姓名不能与申请人姓名一样，请修改";
    private static final String contactPhone = "请选择联系人电话";
    private static final String contactPhone_error = "请选择正确的联系人电话";
    private static final String contactPhone_error2 = "两个联系人手机号不可一致";
    private static final String contactPhone_error3 = "联系人手机号不能与当前用户手机号一样";
    private static final String contactPhone_error4 = "请输入11位有效手机号";
    private static final String contactPhone_error5 = "您的联系人1/2手机号无效";
    private static final String contactPhone_error6 = "您的联系人2/2手机号无效";
    private static final String workNature_Emp = "请选择公司性质";
    private static final String email_empty = "请输入您的邮箱";
    private static final String email_error = "请检查您的邮箱格式是否正确";
    public static String errMsg;  //错误信息

    /**
     * ^：表示开始，
     * 1：以1开始
     * \d：匹配任意数字
     * (?!)：这个是断言，断言后面不能怎么样
     * (\d)\\1{7}：表示匹配一个数字，捕获这个数字，并连续七次。最终意思表示，一个数字，重复八遍
     * (?!(\d)\\1{7})：断案后面不能用连续重复八次的相同的数字
     * \d{8}：匹配八位任意数字
     * ?：结尾
     */
    public static final String phone_pattern = "^1\\d\\d(?!(\\d)\\1{7})\\d{8}?";

    private static boolean checkEmpty(TextView tv, String toast) {
        String unitText = tv.getText().toString().trim();
        if (CheckUtil.isEmpty(unitText)) {
            showToast(toast);
            return false;
        }
        return true;
    }

    /**
     * 工作单位验证
     */
    private static boolean checkUnitNameValid(EditText etUnit) {
        String unitText = etUnit.getText().toString().trim();
        if (CheckUtil.isEmpty(unitText)) {
            showToast(unit_error);
            return false;
        }
        if (unitText.length() < 2 || unitText.length() > 50) {
            showToast(unit_error);
            return false;
        }
        return true;
    }

//    /**
//     * 单位地址验证->省市区
//     */
//    private static boolean checkUnitAddressCodeValid(TextView tvUnitAddress) {
//        if (CheckUtil.isEmpty(tvUnitAddress.getText().toString())) {
//            showToast(unitAddress);
//            //setFocus(etUnit);
//            setFocus(tvUnitAddress);
//            return false;
//        }
//        return true;
//    }

//    /**
//     * 单位地址验证->详细地址
//     */
//    private static boolean checkUnitAddressDetailValid(TextView etUnitAddressDetail) {
//        String tvText = etUnitAddressDetail.getText().toString();
//        if (CheckUtil.isEmpty(tvText) || tvText.length() < 4 || tvText.length() > 50) {
//            showToast(unitAddressDetail);
//            setFocus(etUnitAddressDetail);
//            return false;
//        }
//        return true;
//    }

    /**
     * 单位电话验证
     */
    private static boolean checkUnitPhoneValid(EditText etUnitPhone) {
        String unitPhone = etUnitPhone.getText().toString();
        if (CheckUtil.isEmpty(unitPhone)) {
            return true;
        }
        if (unitPhone.length() < 10 || unitPhone.length() > 13 || !(unitPhone.startsWith("1") || unitPhone.startsWith("0"))) {
            showToast(phone);
            return false;
        }
        return true;
    }

    /**
     * 职业验证
     */
    private static boolean checkDutyValid(TextView tvDuty) {
        if (CheckUtil.isEmpty(tvDuty.getText().toString())) {
            showToast(unitDuty);
            return false;
        }
        return true;
    }


    /**
     * 验证Unit正确
     * <p>
     * 检测：职业、单位名称、单位电话、月收入<br />
     */
    static boolean isUnitPerfect(TextView tvDuty, EditText etUnit, EditText evIncome, EditText etUnitPhone) {
        return checkDutyValid(tvDuty) && checkEmpty(etUnit, unit) && checkUnitNameValid(etUnit) &&
                checkIncome(evIncome) && checkEmpty(etUnitPhone, phone) && checkUnitPhoneValid(etUnitPhone);
    }

    /**
     * 验证婚姻状况
     */
    private static boolean checkMarriageValid(TextView tvMarriage) {
        if (CheckUtil.isEmpty(tvMarriage.getText().toString())) {
            showToast(personMarriage);
            return false;
        }
        return true;
    }

    /**
     * 居住地址
     */
    private static boolean checkLiveAddressValid(TextView tvLive) {
        if (CheckUtil.isEmpty(tvLive.getText().toString())) {
            showToast(personLive);
            return false;
        }
        return true;
    }

    /**
     * 验证居住得详细地址\详细居住之地是否合规
     */
    private static boolean checkLiveAddressDetailValid(EditText etLiveDetail) {
        String tvText = etLiveDetail.getText().toString().trim();
        if (CheckUtil.isEmpty(tvText)) {
            showToast("请输入正确的详细地址");
            return false;
        }
        if (tvText.length() < 4 || tvText.length() > 50) {
            showToast(personLiveDetail);
            return false;
        } else if (tvText.contains("未知") || CheckUtil.isAbc123(tvText)) {
            showToast("请输入正确的详细地址");
            return false;
        }
        return true;
    }

    /**
     * 个人信息判断
     * <p>
     * 1:验证：婚姻状况<br/>
     * 2:验证：婚姻状况、居住地址<br/>
     * 3:验证：婚姻状况、居住地址、居住得详细地址<br/>
     */
    static boolean isPersonalPerfect(TextView tvLive, EditText etLiveDetail, TextView tvMarriage) {
        return checkLiveAddressValid(tvLive) &&
                checkEmpty(etLiveDetail, personLive) && checkLiveAddressDetailValid(etLiveDetail) &&
                checkMarriageValid(tvMarriage);
    }

    /**
     * 验证关系
     */
    private static boolean checkRelationValid(TextView tvRelation) {
        if (CheckUtil.isEmpty(tvRelation.getText().toString())) {
            showToast(contactRelation);
            return false;
        }
        return true;
    }

    /**
     * 验证月收入
     */
    private static boolean checkIncome(EditText tvIncome) {
        String income = tvIncome.getText().toString().trim();
        if (CheckUtil.isEmpty(income)) {
            showToast(income_empty);
            return false;
        }
        if (Double.parseDouble(income) <= 0 || Double.parseDouble(income) > 100000) {
            showToast(income_error1);
            return false;
        }
        if (income.startsWith("0")) {
            showToast(income_error);
            return false;
        }
        return true;
    }

    /**
     * 邮箱判断
     */
    static boolean isExpandsPerfect(EditText etEmail) {
        return checkEmail(etEmail);
    }

    /**
     * 联系人验证中文
     */
    public static boolean checkContactNameChinese(EditText etContactName) {
        if (!CheckUtil.isChinese(etContactName.getText().toString().trim())) {
            showToast(contactNameChinese);
            return false;
        }
        return true;
    }

    /**
     * 联系人验证
     */
    private static boolean checkContactName(EditText etContactName) {
        String tvText = etContactName.getText().toString().trim();
        if (CheckUtil.isEmpty(tvText)) {
            return true;
        }
        if (tvText.length() <= 0) {
            showToast(contactName);
            return false;
        }
        if (!CheckUtil.isChinese(tvText)) {
            showToast(contactNameReg);
            return false;
        }
        if (tvText.equals(SpHp.getUser(SpKey.USER_CUSTNAME))) {
            showToast(contactNameReg1);
            return false;
        }
        return true;
    }

    /**
     * 联系人手机号验证
     */
    private static boolean checkContactPhone(TextView etContactPhone) {
        return checkContactPhone(etContactPhone.getText().toString());
    }

    /**
     * 联系人手机号
     * 联系人电话不能跟当前登录手机号一样
     */
    static boolean checkContactPhone(String phoneNumber) {
        String loginPhone = SpHp.getLogin(SpKey.LOGIN_MOBILE);//用户手机号
        if (CheckUtil.isEmpty(phoneNumber)) {
            showToast(contactPhone);
            errMsg = contactPhone;
            return false;
        } else if (!CheckUtil.checkPhone(phone_pattern, phoneNumber)) {
            showToast(contactPhone_error);
            errMsg = contactPhone_error;
            return false;
        } else if (loginPhone != null && loginPhone.replace(" ", "").equals(phoneNumber.replace(" ", ""))) {
            showToast(contactPhone_error3);
            errMsg = contactPhone_error3;
            return false;
        }
        return true;
    }

    /**
     * 联系人电话不能一样,且为11位后八位不相等
     */
    static boolean checkContactPhone(String phoneNumber1, String phoneNumber2) {
        if (phoneNumber1 != null && phoneNumber1.replace(" ", "").equals(phoneNumber2.replace(" ", ""))) {
            showToast(contactPhone_error2);
            errMsg = contactPhone_error2;
            return false;
        }
        return true;
    }

    static boolean checkPhoneNum(String phoneNumber1, String phoneNumber2) {
        if (!CheckUtil.isEmpty(phoneNumber1)) {
            if (phoneNumber1.replace(" ", "").length() != 11) {
                showToast(contactPhone_error4);
                errMsg = contactPhone_error4;
                return false;
            }
            if (isSameCharsEnd(phoneNumber1.replace(" ", ""))) {
                showToast(contactPhone_error5);
                errMsg = contactPhone_error5;
                return false;
            }
        }
        if (!CheckUtil.isEmpty(phoneNumber2)) {
            if (phoneNumber2.replace(" ", "").length() != 11) {
                showToast(contactPhone_error4);
                errMsg = contactPhone_error4;
                return false;
            }
            if (isSameCharsEnd(phoneNumber2.replace(" ", ""))) {
                showToast(contactPhone_error6);
                errMsg = contactPhone_error6;
                return false;
            }
        }
        return true;
    }

    //判断最后8位是否相等
    public static boolean isSameCharsEnd(String str) throws IllegalArgumentException {
        if (str == null)
            throw new IllegalArgumentException("Input string should not be null.");
        else if (str.length() < 2)
            return true;
        char first = str.charAt(3);
        for (int i = 4; i < str.length(); i++) {
            if (str.charAt(i) != first) {
                return false;
            }
        }
        return true;
    }

    /**
     * 联系人手机号验证
     */
    private static boolean checkEmail(TextView etEmail) {
        if (CheckUtil.isEmpty(etEmail.getText().toString())) {
            showToast(email_empty);
            return false;
        } else if (!CheckUtil.checkEmail(etEmail.getText().toString().trim())) {
            showToast(email_error);
            return false;
        }
        return true;
    }

    /**
     * 联系关系判断
     * <p>
     * 关系、联系人、联系人手机号<br/>
     */
    static boolean isContactPerfect(EditText etContactName, TextView tvRelation, TextView etContactPhone) {
        return checkEmpty(etContactName, contactName) && checkContactName(etContactName) &&
                checkRelationValid(tvRelation) &&
                checkContactPhone(etContactPhone);
    }

    public static boolean isWorkMessagePerfect(TextView editWorkNature, EditText editTextIncome) {
//        return checkEmpty(editWorkNature, workNature_Emp) && checkEmpty(editTextIncome, income_error)
//                && checkIncome(editTextIncome);
        return checkEmpty(editWorkNature, workNature_Emp) && checkIncome(editTextIncome);
    }

//    /**
//     * 设置当前模块的显示隐藏
//     *
//     * @param layoutIndex 1：单位信息
//     *                    2：个人信息
//     *                    3：联系人信息
//     */
//    static void showSelectedView(int layoutIndex, View tvUnit, View layoutUnit, View tvPerson, View layoutPerson, View tvContact, View layoutContact) {
//        switch (layoutIndex) {
//            case 1:
//                tvUnit.setSelected(layoutUnit.getVisibility() == View.VISIBLE);
//                layoutUnit.setVisibility(layoutUnit.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
//                tvPerson.setSelected(false);
//                layoutPerson.setVisibility(View.GONE);
//                tvContact.setSelected(false);
//                layoutContact.setVisibility(View.GONE);
//                break;
//            case 2:
//                tvUnit.setSelected(false);
//                layoutUnit.setVisibility(View.GONE);
//                tvPerson.setSelected(layoutUnit.getVisibility() == View.VISIBLE);
//                layoutPerson.setVisibility(layoutUnit.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
//                tvContact.setSelected(false);
//                layoutContact.setVisibility(View.GONE);
//                break;
//            case 3:
//                tvUnit.setSelected(false);
//                layoutUnit.setVisibility(View.GONE);
//                tvPerson.setSelected(false);
//                layoutPerson.setVisibility(View.GONE);
//                tvContact.setSelected(layoutUnit.getVisibility() == View.VISIBLE);
//                layoutContact.setVisibility(layoutUnit.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
//                break;
//        }
//    }

    /**
     * 判断是否能够提交完善信息
     */
    static String checkPerfectInfo(String contactPhone, ModelBean marriageBean, String liveProCode,
                                   String liveCityCode, String liveAreaCode, ModelBean contactBean, String contactPhone2) {
        String mobile = SpHp.getUser(SpKey.USER_MOBILE);
        String phone = SpHp.getLogin(SpKey.LOGIN_MOBILE);
        //个人信息
        if (marriageBean.isEmpty()) {
            return personMarriage;
        } else if (CheckUtil.isEmpty(liveProCode) || CheckUtil.isEmpty(liveCityCode) || CheckUtil.isEmpty(liveAreaCode)) {//
            return "请选择居住地址";
        }
        //联系人
        else if (contactBean.isEmpty()) {
            return contactRelation;
        } else if ("20".equals(marriageBean.code) && !"06".equals(contactBean.code)) {
            return "您已婚，联系人请选择夫妻";
        } else if (!"20".equals(marriageBean.code) && "06".equals(contactBean.code)) {
            return "您的婚姻状况和联系人关系不匹配,请修改";
        } else if (mobile.equals(contactPhone) || phone.equals(contactPhone)) {
            return "联系人手机号不能与申请人手机号一样，请修改";
        } else if (contactPhone.equals(contactPhone2)) {
            return "两个联系人联系电话不能一致";
        }
        return null;
    }

//    /**
//     * 设置View得到焦点
//     */
//    static void setFocus(View view) {
//        //if(view instanceof EditText){}
////        view.setFocusable(true);
////        view.setFocusableInTouchMode(true);
////        view.requestFocus();
//    }

    private static void showToast(String msg) {
        UiUtil.toast(msg);
    }
//
//    /* ****************************** 缓存数据处理Start******************************************* */
//    final static String DUTY_CODE = "duty_code";
//    final static String MARRIAGE_CODE = "marriage_code";
//    final static String CONTACT_CODE = "contact_code";
//    final static String UNIT_PRO_CODE = "unitProCode";
//    final static String UNIT_CITY_CODE = "unitCityCode";
//    final static String UNIT_AREA_CODE = "unitAreaCode";
//    final static String LIVE_PRO_CODE = "liveProCode";
//    final static String LIVE_CITY_CODE = "liveCityCode";
//    final static String LIVE_AREA_CODE = "liveAreaCode";
//
//    private final static SparseArray<String> sPerfectInfoMap = new SparseArray<>();
//    private final static Map<String, String> sPerfectCodeMap = new HashMap<>();
//
//    static void saveTextViewVale(TextView... views) {
//        if (views != null && views.length > 0) {
//            for (TextView tv : views) {
//                sPerfectInfoMap.put(tv.getId(), tv.getText().toString());
//            }
//        }
//    }
//
//    static void readTextViewVale(TextView... views) {
//        if (views != null && views.length > 0) {
//            for (TextView tv : views) {
//                if (sPerfectInfoMap.indexOfKey(tv.getId()) >= 0) {
//                    tv.setText(sPerfectInfoMap.get(tv.getId()));
//                }
//            }
//        }
//    }

//    static void saveCodeValue(String code, String value) {
//        sPerfectCodeMap.put(code, value);
//    }
//
//    static String readCodeValue(String code) {
//        return sPerfectCodeMap.get(code);
//    }

//
//    public static void cleanAll() {
//        sPerfectInfoMap.clear();
//        sPerfectCodeMap.clear();
//    }
    /* *********************************** 缓存数据处理End******************************************* */

    /**
     * 手机通讯录带回
     */
    public static List<String> getPhoneMailList(Context context, Intent data) {
        List<String> userNumber = new ArrayList<>();
        try {
            ContentResolver reContentResolverol = context.getContentResolver();
            Uri contactData = data.getData();
            if (contactData == null) {
                return userNumber;
            }
            Cursor cursor = context.getContentResolver().query(contactData, null, null, null, null);
            if (cursor == null) {
                return userNumber;
            }
            if (cursor.moveToFirst()) {
                // 获得联系人的ID号
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                // 获得联系人的电话号码的cursor;
                Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null,
                        null);
                if (phone == null) {
                    return userNumber;
                }

                while (phone.moveToNext()) {
                    String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
                    //联系人的名字
                    String name = phone.getString(phone.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    number = CheckUtil.getPhoneNum(number);
                    if (!TextUtils.isEmpty(number)) {
                        userNumber.add(number);
                    }
                    if (!TextUtils.isEmpty(name)) {
                        data.putExtra("contractName", name);
                    }
                }
                phone.close();
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userNumber;
    }
}
