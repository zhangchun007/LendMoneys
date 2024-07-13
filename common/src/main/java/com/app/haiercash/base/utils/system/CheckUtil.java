package com.app.haiercash.base.utils.system;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.collection.LongSparseArray;
import androidx.collection.SimpleArrayMap;

import com.app.haiercash.base.utils.log.Logger;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/21
 * 描    述：数据工具类
 * 修订历史：
 * ================================================================
 */
public class CheckUtil {
    /**
     * 仅允许输入汉字
     */
    public static boolean isChinese(String str) {
        //String regEx = "[\u4E00-\u9FA5]{1,5}(?:·[\u4E00-\u9FA5]{1,5})*";
        String regEx = "[\u4E00-\u9FA5]{1,}(?:·[\u4E00-\u9FA5]{1,})*";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 检查是否包含汉字
     */
    public static boolean isContainChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 判断是否为纯数字或字母或字母数字组合
     */
    public static boolean isAbc123(String str) {
        String reg = "[a-zA-Z0-9]*";
        return Pattern.matches(reg, str);
    }

    /**
     * 删掉除中文以外的其他文字
     */
    public static String isMandarin(String str) {
        String regEx = "[^\u4E00-\u9FCC·.]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 删掉字符中的数字
     */
    public static String limitNubber(String str) {
        String regEx = "[\\d]";
        Pattern p = Pattern.compile(regEx);
        return p.matcher(str).replaceAll("").trim();
    }

    /**
     * 检查密码是否合法
     */
    public static boolean checkPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        String regEx = ".*[a-zA-Z].*[0-9]|.*[0-9].*[a-zA-Z]";
        Pattern p = Pattern.compile(regEx);
        Matcher match = p.matcher(password);
        return password.length() >= 8 && password.length() <= 20 && match.matches();
    }

    /**
     * 验证输入的名字是否为“中文”或者是否包含“·”
     * <p>
     * 验证规则是：姓名由汉字或汉字加“•”、"·"组成，并且，“点”只能有一个，“点”的位置不能在首位也不能在末尾，只有在汉字之间才会验证经过。
     */
    public static boolean isLegalName(String name) {
        if (TextUtils.isEmpty(name) || name.length() < 2 || name.length() > 20) {
            return false;
        }
        if (name.contains("·") || name.contains("•")) {
            return name.matches("^[\\u4e00-\\u9fa5]+[·•][\\u4e00-\\u9fa5]+$");
        } else {
            return name.matches("^[\\u4e00-\\u9fa5]+$");
        }
    }

    /**
     * 验证输入的身份证号是否合法
     * <p>
     * 规则是：由15位数字或18位数字（17位数字加“x”）组成，15位纯数字没什么好说的，18位的话，能够是18位纯数字，或者17位数字加“x”
     */
    public static boolean checkIdNumber(String id) {
        return !TextUtils.isEmpty(id) && id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)");
    }

    /**
     * 校验银行卡号
     */
    public static boolean checkBankCard(String cardNo) {
        return !TextUtils.isEmpty(cardNo) && cardNo.toUpperCase().matches("^\\d{16,20}$");
    }

    /**
     * 邮箱的合法性
     *
     * @param email 邮箱
     * @return true 合法 false 不合法
     * <p>校验规则：     数字、字母、下划线 + @ + 数字、英文 + . +英文（长度是2-4））
     */
    public static boolean checkEmail(String email) {
        String reg = "[a-z0-9A-Z]+[(\\w+)*]+[a-z0-9A-Z]@[a-z0-9A-Z]+\\.[A-z]{2,4}$";
        return Pattern.matches(reg, email);
        //        if (email.contains("@") && email.contains(".")) {
//            String[] strs = email.split("@");
//            if (strs.length != 2 || isEmpty(strs[0]) || isEmpty(strs[1])) {
//                return false;
//            }
//            if (!strs[1].contains(".")) {
//                return false;
//            }
//            String reg1 = "[a-z0-9A-Z]+[(\\w+)*]+[a-z0-9A-Z]";
//            if (Pattern.matches(reg1, strs[0])) {
//                String[] strs2 = strs[1].split("\\.");
//                if (strs2.length != 2 || isEmpty(strs2[0]) || isEmpty(strs2[1]) || strs2[1].length() < 2 || strs2[1].length() > 4) {
//                    return false;
//                }
//                String reg2 = "^[A-Za-z0-9]+$";
//                if (Pattern.matches(reg2, strs2[0])) {
//                    return Pattern.matches("^[A-Za-z]+$", strs2[1]);
//                }
//            }
//        }
//        return false;
    }

    /**
     * 检查手机号
     */
    public static boolean checkPhone(String phone) {
        return checkPhone("^1[\\d]{10}", phone);
    }

    public static boolean checkPhone(String pattern, String phone) {
        return Pattern.matches(pattern, String.valueOf(phone));
    }

    /**
     * 过滤所有空格
     */
    public static String clearBlank(String s) {
        if (s != null && s.contains(" ")) {
            s = s.replaceAll(" ", "").trim();
        }
        return s;
    }

    public static String clearEmpty(String s) {
        if (isEmpty(s)) {
            return "";
        }
        return s;
    }

    /**
     * 检查是否为空
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0 || "".equals(value.trim()) || "null".equals(value.trim());
    }

    /**
     * 检查是否为空
     */
    public static boolean isEmpty(List list) {
        return list == null || list.size() <= 0;
    }

    /**
     * 检查是否为空
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.size() <= 0;
    }

//    public static boolean isEmptyOr(String obj, final Object... objects) {
//        boolean flag = isEmpty(obj);
//        for (Object o : objects) {
//            if (flag) {
//                return true;
//            }
//            flag = flag || isEmpty(o);
//        }
//        return flag;
//    }

    /**
     * 判断对象是否为空
     *
     * @param obj 对象
     * @return {@code true}: 为空<br>{@code false}: 不为空
     */
    public static boolean isEmpty(final Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof CharSequence && obj.toString().length() == 0) {
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof SimpleArrayMap && ((SimpleArrayMap) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof SparseArray && ((SparseArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseBooleanArray && ((SparseBooleanArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseIntArray && ((SparseIntArray) obj).size() == 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (obj instanceof SparseLongArray && ((SparseLongArray) obj).size() == 0) {
                return true;
            }
        }
        if (obj instanceof LongSparseArray && ((LongSparseArray) obj).size() == 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (obj instanceof android.util.LongSparseArray && ((android.util.LongSparseArray) obj).size() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数字串安全转化为Integer——parseInt
     *
     * @param string 数字串
     * @return Integer数字
     */
    public static Integer mIntegerParseInt(String string) {
        if (isEmpty(string)) {
            return 0;
        } else {
            if (string.contains(".")) {
                float a = Float.valueOf(string);
                return (int) a;
            } else {
                return Integer.parseInt(string);
            }
        }
    }

    /**
     * 数字串安全转化为Float——valueOf
     *
     * @param string 数字串
     * @return Float数字
     */
    public static float mFloatValueOf(String string) {
        if (CheckUtil.isEmpty(string)) {
            return 0;
        } else {
            return Float.valueOf(string);
        }
    }

    /**
     * 数字串安全转化为Double————valueOf
     *
     * @param string 数字串
     * @return Double数字
     */
    public static double mDoubleValueOf(String string) {
        if (CheckUtil.isEmpty(string)) {
            Logger.e("CommonUtils", "DoubleValueOf接收到了空值");
            return 0;
        } else {
            return Double.valueOf(string);
        }
    }

    /**
     * 隐藏手机号中间4位
     */
    public static String hidePhoneNumber(String string) {
        if (TextUtils.isEmpty(string) || string.length() != 11) {
            return "手机号格式错误！";
        }
        StringBuilder sb = new StringBuilder(string);
        for (int i = sb.length() - 8; i < sb.length() - 4; i++) {
            sb.replace(i, i + 1, "*");
        }
        return sb.toString();
    }

    /**
     * 格式化银行卡号
     *
     * @param cardNum
     * @return
     */
    public static String farmatCard(String cardNum) {
        String len = cardNum.trim().replaceAll(" ", "");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len.length(); i++) {
            builder.append(len.charAt(i));
            if (i == 3 || i == 7 || i == 11 || i == 15) {
                if (i != len.length() - 1) {
                    builder.append(" ");
                }
            }
        }
        return builder.toString();
    }

    /**
     * 去除字符串中存在-和空格，去除头部86或者+86
     */
    public static String getPhoneNum(String number) {
        if (isEmpty(number)) {
            return "";
        }
        String phoneNum = number.replaceAll("-", "");
        phoneNum = phoneNum.trim().replaceAll(" ", "");
        if (phoneNum.startsWith("86")) {
            phoneNum = phoneNum.substring(2);
        } else if (phoneNum.startsWith("+86")) {
            phoneNum = phoneNum.substring(3);
        } else if (phoneNum.startsWith("0086")) {
            phoneNum = phoneNum.substring(4);
        }
        if (phoneNum.length() != 11 || !phoneNum.startsWith("1")) {
            return "";
        }
        return phoneNum;
    }

    /**
     * 格式化银行卡号
     */
    public static String formattedBankCardNo(String bankCardNo) {
        String newStr = bankCardNo.toString();
        newStr = newStr.replace(" ", "");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < newStr.length(); i += 4) {
            if (i > 0) {
                sb.append(" ");
            }
            if (i + 4 <= newStr.length()) {
                sb.append(newStr.substring(i, i + 4));
            } else {
                sb.append(newStr.substring(i, newStr.length()));
            }
        }
        return sb.toString();
    }

    /**
     * 如果字符串以".00"、".0"、"."结尾，则去掉应的字符串，否则不做任何处理
     */
    public static String formattedAmount(String amount) {
        if (isEmpty(amount)) {
            return amount;
        }
        if (amount.endsWith(".00")) {
            return amount.substring(0, amount.length() - ".00".length());
        }
        if (amount.endsWith(".0")) {
            return amount.substring(0, amount.length() - ".0".length());
        }
        if (amount.endsWith(".")) {
            return amount.substring(0, amount.length() - ".".length());
        }
        return amount;
    }

    public static boolean isZero(String num) {
        return TextUtils.isEmpty(num) || "0".equals(num) || "0.0".equals(num) || "0.00".equals(num);
    }


    /**
     * 针对”100“、”100.“、”100.0“、”100.00“ 几种类型的金额数据，确认保留两位小数<br/>
     * 其他数据类型，不做任何处理
     */
    public static String formattedAmount1(String amount) {
        if (isEmpty(amount)) {
            return "0.00";
        }
        String[] strS = amount.split("\\.");
        if (strS.length > 2) {
            return amount;
        }
        if (strS.length == 1) {
            return strS[0] + ".00";
        }
        if (strS[1].length() == 1) {
            return strS[0] + "." + strS[1] + "0";
        }
        return amount;
    }

    /**
     * 将String型数字字符串保留两位小数
     */
    @SuppressLint("DefaultLocale")
    public static String round(String number) {
        if (isEmpty(number)) {
            return "0.00";
        }
        try {
            BigDecimal b = new BigDecimal(number);
            BigDecimal one = new BigDecimal("1");
            number = String.format("%.2f", b.divide(one, 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    /**
     * 去.0
     */
    public static String deletePointZero(String number) {
        if (isEmpty(number)) {
            return "";
        }
        if (number.endsWith(".0")) {
            return number.substring(0, number.length() - 2);
        }
        if (number.endsWith(".00")) {
            return number.substring(0, number.length() - 3);
        }
        return number;
    }

    /**
     * 千分位的添加
     *
     * @param amount 金额数值
     */
    public static String toThousands(String amount) {
        return toThousandsOrNot(amount, true);
    }

    /**
     * 千分位的添加和去除
     *
     * @param amount   金额数值
     * @param isFormat true:设置千分位；false：去除千分位
     */
    public static String toThousandsOrNot(String amount, boolean isFormat) {
        try {
            NumberFormat numberFormat1 = NumberFormat.getNumberInstance();
            numberFormat1.setGroupingUsed(isFormat); //设置了以后不会有千分位，如果不设置，默认是有的
            return numberFormat1.format(Double.valueOf(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amount;
    }

    /**
     * 若位数不足小数点后两位，则添加小数点后两位，若小数点后有多位，则不处理
     */
    private static String moreTwoDigits(String str) {
        if (isEmpty(str)) {
            return str;//空字符串不做处理
        }
        try {
            if (!str.contains(".")) {
                return str + ".00";//没有小数，直接+.00
            }
            String[] mString = str.split("\\.");
            if (1 >= mString.length) {
                //str = "."  或者 str = "100."类的String
                if (".".equals(str)) {
                    return "";
                }
                return str + "00";
            }
            if (1 == mString[1].length()) {
                //只有一位小数的情况
                return str + "0";
            }
        } catch (Exception e) {
            Log.e("--------->", " Decimals:" + e);
        }
        return str;
    }

    /**
     * 千分位判断有无小数点
     */
    public static String showNewThound(String str) {
        if (!isEmpty(str) && str.contains(".")) {
            return showThound(str);
        } else {
            return showThounds(str);
        }
    }

    public static String formatCircleAndNormalValue(String available) {
        return (isEmpty(available) || Double.parseDouble(available) <= 0) ? "0.00" : showNewThound(available);
    }

    /**
     * 千分位方式展示
     * ex: 输入：10000 输出：10,000.00
     */
    public static String showThound(String str) {
        if (isEmpty(str)) {
            return str;//空字符串不做处理
        }
//        NumberFormat numberFormat1 = NumberFormat.getNumberInstance();
//        return numberFormat1.format(Double.valueOf(str));
        String str1 = "";
        boolean isContainsPoint = false;//是否有小数
        boolean isContainsSymbol = false;//是否有负号

        if (str.contains("-")) {
            str = str.substring(1, str.length());
            isContainsSymbol = true;
        }
        if (str.contains(".")) {
            isContainsPoint = true;
            String[] strings = str.split("\\.");
            str = strings[0];
            if (1 < strings.length) {
                str1 = strings[1];
            }
            if (3 >= str.length()) {
                return moreTwoDigits(str + "." + str1);
            }
        }
        int lenth = str.length();
        int size = lenth / 3 + 1;
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            if (i == (size - 1)) {
                strings[i] = str.substring(0, lenth - i * 3);
            } else {
                strings[i] = str.substring(lenth - (i + 1) * 3, lenth - i * 3);
            }
        }
        for (int i = 0; i < size; i++) {
            if (0 == i) {
                str = strings[0];
            } else {
                if (null != strings[i] && !"".equals(strings[i])) {
                    str = strings[i] + "," + str;
                }
            }
        }
        if (isContainsPoint) {
            str = str + "." + str1;
        }
        if (isContainsSymbol) {
            str = "-" + str;
        }
        return moreTwoDigits(str);
    }

    /**
     * 千分位方式展示
     * ex: 输入：10000 输出：10,000
     */
    public static String showThounds(String str) {
        if (isEmpty(str)) {
            return str; //空字符串不做处理
        }
        int lenth = str.length();
        int size = lenth / 3 + 1;
        String[] strings = new String[size];
        for (int i = 0; i < size; i++) {
            if (i == (size - 1)) {
                strings[i] = str.substring(0, lenth - i * 3);
            } else {
                strings[i] = str.substring(lenth - (i + 1) * 3, lenth - i * 3);
            }
        }
        for (int i = 0; i < size; i++) {
            if (0 == i) {
                str = strings[0];
            } else if (null != strings[i] && !"".equals(strings[i])) {
                str = strings[i] + "," + str;
            }
        }
        return str;
    }

    /**
     * 姓名只展示部分
     */
    public static String getName(final String uNumber) {
        if (isEmpty(uNumber)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (uNumber.length() == 2) {
            for (int i = 0; i < uNumber.length(); i++) {
                char c = uNumber.charAt(i);
                if (i == 1) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
        } else if (uNumber.length() >= 3) {
            for (int i = 0; i < uNumber.length(); i++) {
                char c = uNumber.charAt(i);
                if (i == 0 || i == uNumber.length() - 1) {
                    sb.append(c);
                } else {
                    sb.append('*');
                }
            }
        }
        return sb.toString();
    }

    /**
     * 姓名只展示名
     */
    public static String getNames(final String uNumber) {
        if (isEmpty(uNumber)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (uNumber.length() == 2) {
            for (int i = 0; i < uNumber.length(); i++) {
                char c = uNumber.charAt(i);
                if (i != 1) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
        } else if (uNumber.length() >= 3) {
            for (int i = 0; i < uNumber.length(); i++) {
                char c = uNumber.charAt(i);
                if (i == 0) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 姓名只展示最后一个字
     */
    public static String getNameOnlyLastWords(final String uNumber) {
        if (isEmpty(uNumber)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uNumber.length(); i++) {
            if (i != uNumber.length() - 1) {
                sb.append('*');
            } else {
                sb.append(uNumber.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * 手机只展示部分
     */
    public static String getPhone(final String pNumber) {
        if (isEmpty(pNumber) || pNumber.length() <= 6) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pNumber.length(); i++) {
            char c = pNumber.charAt(i);
            if (i >= 3 && i <= 6) {
                sb.append('*');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 手机只展示部分，且344格式空格
     */
    public static String getPhone344(final String pNumber) {
        if (pNumber == null || pNumber.length() != 11) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(pNumber.substring(0, 3)).append(' ');
        for (int i = 0; i < 4; i++) {
            sb.append('*');
        }
        sb.append(' ');
        sb.append(pNumber.substring(7));
        return sb.toString();
    }

//    public static void main(String[] args) {
//        System.out.println("身份证号：" + getCerNo("3408261999004118716", 4, 4));
//        System.out.println("身份证号：" + getCerNo("3408261999004118716", 6, 2));
//    }

    public static String getCertNo(String certNo, int leftShow, int rightShow) {
        if (isEmpty(certNo)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < certNo.length(); i++) {
                char c = certNo.charAt(i);
                if (i < leftShow) {
                    sb.append(c);
                } else if (i >= certNo.length() - rightShow) {
                    sb.append(c);
                } else {
                    sb.append("*");
                }
            }
        } catch (Exception e) {
            sb.setLength(0);//清空
            sb.append("************");
        }
        return sb.toString();
    }

    /***
     * 身份证显示前6后2
     */
    public static String getCertNo(String certNo) {
        if (isEmpty(certNo)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < certNo.length(); i++) {
            char c = certNo.charAt(i);
            if (i >= 5 && i < certNo.length() - 2) {
                sb.append('*');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 截取银行卡后四位
     */
    public static String getBankNum(String str) {
        if (isEmpty(str)) {
            return null;
        }
        if (str.length() >= 4) {
            return str.substring(str.length() - 4, str.length());
        }
        return str;
    }


    /**
     * 从身份证号码上获取出生日期
     *
     * @param certNo 身份证号
     * @return 出生日期
     */
    public static String getBirthDayFromCertNo(String certNo) {
        //  空/非18位先排除
        if (TextUtils.isEmpty(certNo) || certNo.length() != 18) {
            return "";
        }
        //获取出生日期
        String birthDay = certNo.substring(6, 14);
        //将19900101 转为1990-01-01
        birthDay = birthDay.substring(0, 4) + "-" + birthDay.substring(4, 6) + "-" + birthDay.substring(6);
        return birthDay;
    }

    /**
     * 判断是否需要验证是否是学生
     *
     * @param certNo 身份证号
     * @return 18<= 年龄 < = 2 2 返回true
     */
    public static boolean isNeedCheckStudent(String certNo) {
        //  空/非18位  需要弹窗
        if (TextUtils.isEmpty(certNo) || certNo.length() != 18) {
            return true;
        }
        //获取出生日期
        String birthDay = certNo.substring(6, 14);
        //当前日期
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //计算年龄差
        int ageBit = Integer.parseInt(date) - Integer.parseInt(birthDay);
        //当年龄差的长度大于4位时，即出生年份小于当前年份
        if (Integer.toString(ageBit).length() > 4) {
            //截取掉后四位即为年龄
            int personAge = Integer.parseInt(Integer.toString(ageBit).substring(0, Integer.toString(ageBit).length() - 4));
            return 18 <= personAge && personAge <= 22;
        } else {//当前年份出生，直接赋值为0岁
            return false;

        }
    }

    /**
     * 如果字符串为空则返回一个默认值
     */
    public static String optText(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }


    /**
     * obj是否属于superClass的子类
     */
    public static boolean checkSupperClass(Object obj, Class superClass) {
        if (obj instanceof Class) {
            Class cls = (Class) obj;
            do {
                if (cls == superClass) {
                    return true;
                }
                cls = cls.getSuperclass();
            } while (cls != null);
            //noinspection unchecked
            //return superClass.isAssignableFrom(((Class) obj));

            //return superClass.getClass().isAssignableFrom(obj.getClass());
            //return ((Class) obj).getSuperclass().equals(superClass);
        }
        return false;

    }

    /**
     * 输入手机号3-4-4显示
     * 23-2-21更新，删除某一位后光标不移动到最后一位
     */
    public static void formatPhone344(final EditText edt) {
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                        continue;
                    } else {
                        stringBuilder.append(s.charAt(i));
                        if ((stringBuilder.length() == 4 || stringBuilder.length() == 9)
                                && stringBuilder.charAt(stringBuilder.length() - 1) != ' ') {
                            stringBuilder.insert(stringBuilder.length() - 1, ' ');
                        }
                    }
                }
                if (!stringBuilder.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (stringBuilder.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }
                    edt.setText(stringBuilder.toString());
                    edt.setSelection(index);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        String phone = s.toString();
//        String value = phone.replace(" ", "");
//        if (phone.length() > 3) {
//            value = value.substring(0, 3) + " " + value.substring(3, value.length());
//        }
//        if (value.length() > 8) {
//            value = value.substring(0, 8) + " " + value.substring(8, value.length());
//        }
//        edt.setText(value);
    }

    /**
     * 设置银行卡输入时每隔4位多一位空格
     *
     * @param cardEt 输入框
     */
    public static void formatBankCard44x(final AppCompatEditText cardEt, final TextWatcher listener) {
        //设置输入长度不超过24位(包含空格)
        cardEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
        cardEt.addTextChangedListener(new TextWatcher() {
            char kongge = ' ';
            //改变之前text长度
            int beforeTextLength = 0;
            //改变之后text长度
            int onTextLength = 0;
            //是否改变空格或光标
            boolean isChanged = false;
            // 记录光标的位置
            int location = 0;
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            //已有空格数量
            int konggeNumberB = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') {
                        konggeNumberB++;
                    }
                }
                if (listener != null) {
                    listener.onTextChanged(s, start, count, after);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3
                        || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
                if (listener != null) {
                    listener.onTextChanged(s, start, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isChanged) {
                    location = cardEt.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == kongge) {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }
                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        if ((index == 4 || index == 9 || index == 14 || index == 19)) {
                            buffer.insert(index, kongge);
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if (konggeNumberC > konggeNumberB) {
                        location += (konggeNumberC - konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (location > str.length()) {
                        location = str.length();
                    } else if (location < 0) {
                        location = 0;
                    }

                    cardEt.setText(str);
                    Editable etable = cardEt.getText();
                    Selection.setSelection(etable, location);
                    isChanged = false;
                    if (listener != null) {
                        listener.afterTextChanged(s);
                    }
                }
            }
        });
    }


    /**
     * 设置金额千分位
     */
    public static void formatMoneyThousandth(final AppCompatEditText cardEt) {
        cardEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                afterViewTextChanged(cardEt, this);
            }
        });
    }

    public static void afterViewTextChanged(final AppCompatEditText cardEt, TextWatcher watcher) {
        try {
            cardEt.removeTextChangedListener(watcher);
            String value = cardEt.getText().toString();
            if (value != null && !value.equals("")) {
                if (value.startsWith(".")) {
                    cardEt.setText("0.");
                }
                if (value.startsWith("0") && !value.startsWith("0.")) {
                    cardEt.setText("");
                }
                String str = cardEt.getText().toString().replaceAll(",", "");
                if (!value.equals("")) {
                    str = getDecimalFormattedString(str);
                    if (str.contains(".")) {
                        str = str.substring(0, str.lastIndexOf("."));
                    }
                    cardEt.setText(str);
                }
                cardEt.setSelection(cardEt.getText().toString().length());
            }
            cardEt.addTextChangedListener(watcher);
        } catch (Exception ex) {
            ex.printStackTrace();
            cardEt.addTextChangedListener(watcher);
        }
    }

    public static String getDecimalFormattedString(String value) {
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1) {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }
        String str3 = "";
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt(-1 + str1.length()) == '.') {
            j--;
            str3 = ".";
        }
        for (int k = j; ; k--) {
            if (k < 0) {
                if (str2.length() > 0)
                    str3 = str3 + "." + str2;
                return str3;
            }
            if (i == 3) {
                str3 = "," + str3;
                i = 0;
            }
            str3 = str1.charAt(k) + str3;
            i++;
        }
    }


    public static boolean isEqual(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b) || Double.isInfinite(a) || Double.isInfinite(b)) {
            return false;
        }
        return (a - b) < 0.0000001d;
    }

    /**
     * 获取指定长度随机数
     */
    public static String randomNumber(int tokenLen) {
        if (tokenLen < 1) {
            tokenLen = 1;
        }
        StringBuilder buffer = new StringBuilder("0123456789");
        StringBuilder sb = new StringBuilder();
        SecureRandom r = new SecureRandom();
        int range = buffer.length();
        for (int i = 0; i < tokenLen; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

    /**
     * 数字相等或者连续
     *
     * @return 1相同数字，2连续数字
     */
    public static int checkAllEqualsOr(String text) {
        try {
            int tmp = -2;
            int count = 0;
            int count1 = 0;
            int count2 = 0;
            for (int i = 0; i < text.length(); i++) {
                int curr = Integer.parseInt(text.substring(i, i + 1));
                if (i == 0) {
                    tmp = curr;
                    continue;
                } else if (tmp == curr) {
                    count++;
                } else if (tmp - curr == 1) {
                    count1++;
                } else if (tmp - curr == -1) {
                    count2++;
                }
                tmp = curr;
            }
            if (count == text.length() - 1) {
                return 1;
            }
            if (count1 == text.length() - 1 || count2 == text.length() - 1) {
                return 2;
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 转化百分比
     */
    public static String getPercentStr(String data) {
        String percentStr;
        try {
            //乘以100，化为百分比(用字符串平移的方式，因为数字化计算可能会出现精度问题)
            String[] split = data.split("\\.", 2);
            percentStr = split[0] + (split.length == 1 || split[1].length() == 0 ? "00" : (split[1].length() == 1 ? split[1] + "0" : (split[1].length() == 2 ? split[1] : (split[1].substring(0, 2) + "." + split[1].substring(2)))));
            //去除前面无效的0
            String[] split1 = percentStr.split("\\.", 2);
            percentStr = Integer.parseInt(split1[0]) + (split1.length == 1 ? "" : "." + split1[1]);
        } catch (Exception e) {
            Logger.e("百分号计算异常");
            percentStr = "";
        }
        return percentStr + "%";
    }

    public static String formatTitle(String title) {
        if (title.length() > 2 && title.startsWith("《") && title.endsWith("》")) {
            String substring = title.substring(1, title.length() - 1);
            return substring;
        }
        return title;
    }
}
