package com.app.haiercash.base.utils.system;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Arith {
    /**
     * 保留两位小数，并四舍五入
     */
    public static String format(Double value) {
        BigDecimal decimal = BigDecimal.valueOf(value);
        BigDecimal setScale = decimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(setScale.doubleValue());
    }

    /**
     * 提供精确加法计算的add方法
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static double add(Double value1, String value2) {
        return add(value1, Double.valueOf(value2));
    }

    /**
     * 提供精确加法计算的add方法
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static double add(Double value1, Double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确减法运算的sub方法
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double sub(Double value1, String value2) {
        return sub(value1, Double.valueOf(value2));
    }

    /**
     * 提供精确减法运算的sub方法
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double sub(Double value1, Double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确乘法运算的mul方法
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static double mul(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供精确乘法运算的mul方法
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static double mul(Double value1, Double value2) {
        BigDecimal b1 =  BigDecimal.valueOf(value1);
        BigDecimal b2 =  BigDecimal.valueOf(value2);
        return b1.multiply(b2).doubleValue();
    }

    public static double mulMore(Double value1, Double... value2) {
        BigDecimal bSum =  BigDecimal.valueOf(value1);
        for (Double d : value2) {
            BigDecimal b2 =  BigDecimal.valueOf(d);
            bSum = bSum.multiply(b2);
        }
        return bSum.doubleValue();
    }

    /**
     * 提供精确的除法运算方法div
     *
     * @param value1 被除数
     * @param value2 除数
     * @param scale  精确范围
     * @return 两个参数的商
     */
    public static double div(String value1, String value2, int scale) throws IllegalAccessException {
        return div(Double.valueOf(value1), Double.valueOf(value2), scale);
    }

    /**
     * 提供精确的除法运算方法div
     *
     * @param value1 被除数
     * @param value2 除数
     * @param scale  精确范围
     * @return 两个参数的商
     */
    public static double div(Double value1, Double value2, int scale) throws IllegalAccessException {
        //如果精确范围小于0，抛出异常信息
        if (scale < 0) {
            throw new IllegalAccessException("精确度不能小于0");
        }
        BigDecimal b1 =  BigDecimal.valueOf(value1);
        BigDecimal b2 =  BigDecimal.valueOf(value2);
        return b1.divide(b2, scale).doubleValue();
    }
}