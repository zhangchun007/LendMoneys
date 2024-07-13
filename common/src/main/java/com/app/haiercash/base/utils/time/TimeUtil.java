package com.app.haiercash.base.utils.time;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.app.haiercash.base.utils.system.CheckUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/20
 * 描    述：时间工具类，提供时间和字符串之间的互相转换
 * 修订历史：
 * ================================================================
 */
public class TimeUtil {
    /**
     * Title: getCalendar
     * Description: 获取当前Calendar时间对象
     * return
     */
    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    /**
     * 获取当前时间为yyyy-MM-dd HH:mm:ss格式的字符串
     * <p/>
     * return
     */
    public static String calendarToString() {
        return calendarToString(Calendar.getInstance());
    }

    /**
     * 获取当前时间为yyyyMMddHHmmss格式的字符串
     * <p/>
     * return
     */
    public static String calendarToString3() {
        return calendarToString(Calendar.getInstance(), "yyyyMMddHHmmss");
    }

    /**
     * 将Calendar类型转换为yyyy-MM-dd HH:mm:ss格式的字符串
     * <p/>
     * param calendar
     * return
     */
    public static String calendarToString(Calendar calendar) {
        return calendarToString(calendar, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将Calendar类型转换为yyyy-MM-dd格式的字符串
     * <p/>
     * return
     */
    public static String calendarToString2() {
        return calendarToString(Calendar.getInstance(), "yyyy-MM-dd");
    }

    /**
     * 将Calendar类型转换为指定格式的字符串
     * <p/>
     * param calendar
     * return
     */
    public static String calendarToString(Calendar calendar, String pattern) {
        if (calendar == null) {
            return "";
        }
        return simpleDateFormat(pattern).format(calendar.getTime());
    }

    /**
     * 规范化日期字符串
     */
    public static String formatCalendar(String times, String pattern) {
        if (pattern == null || times == null) {
            return times;
        }
        return calendarToString(stringToCalendar(times, pattern), pattern);
    }

    /**
     * long转换成指定的时间字符串
     *
     * @param dateLong long数据
     */
    public static String longToString(long dateLong) {
        return longToString(dateLong, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * long转换成指定的时间字符串
     *
     * @param dateLong long数据
     * @param pattern  输出的时间格式
     */
    public static String longToString(long dateLong, String pattern) {
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(dateLong);
        return calendarToString(calendar, pattern);
    }

    /**
     * Date转String
     *
     * @return 格式 "yyyy-MM-dd HH:mm:ss"
     */
    public static String dateToString(Date date) {
        return simpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * Date转String
     *
     * @param pattern 格式 例如："yyyy-MM-dd HH:mm:ss"
     */
    public static String dateToString(Date date, String pattern) {
        return simpleDateFormat(pattern).format(date);
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss转换成yyyy-MM-dd
     */
    public static String stringToString(String dateStr) {
        if (dateStr == null || dateStr.trim().length() == 0) {
            return null;
        }
        return calendarToString(stringToCalendar(dateStr, "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd");
    }

    /**
     * 将string字符串转换为Calendar类型
     * <p/>
     * param calendarString
     * return
     */
    public static Calendar stringToCalendar(String dateStr, String pattern) {
        if (dateStr == null || dateStr.trim().length() == 0) {
            return null;
        }
        SimpleDateFormat formatter = simpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(formatter.parse(dateStr));
        } catch (ParseException e) {
            return null;
        }
        return calendar;
    }

    /**
     * 当前时间戳
     */
    public static long currentTimestamp() {
        return getCalendar().getTime().getTime();
    }

    /**
     * String转Date
     */
    public static Date getDateFromString(String dateStr) {
        try {
            return simpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat simpleDateFormat(String pattern) {
        return (new SimpleDateFormat(pattern));
    }

    /**
     * 获取当前时间前或者后的时间
     *
     * @return yyyy-MM-dd的时间
     */
    public static String getDataBefore(int year, int month, int day) {
        SimpleDateFormat sdf = TimeUtil.simpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, year);
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.DATE, day);
        return sdf.format(calendar.getTime());
    }


    /**
     * 获取当前时间
     *
     * @param dateStr
     * @return
     */
    public static String getNowDate(String dateStr) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateStr);// "yyyyMMdd"
        return formatter.format(currentTime);
    }

    /**
     * 获取几年前的一天
     */
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    //某天时间减yearAmount年
    public static String getBeforeYear(String startTime, int yearAmount) {
        Calendar c = Calendar.getInstance();
        Date date = getSimpleDateFromString(startTime);
        c.setTime(date);
        c.add(Calendar.YEAR, -yearAmount);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return getDateToString(c.getTime().getTime(), YYYY_MM_DD);
    }

    /**
     * 时间戳转换成事件
     *
     * @param milSecond
     * @param pattern
     * @return
     */
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static Date getSimpleDateFromString(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        Date date;
        try {
            date = sdf.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*解析指定快捷时间,按照日历来，起始日期加一天,onlyYMD是否仅仅年月日*/
    public static String[] parse(Context context, String theQueryTime, boolean onlyYMD) {
        //获取系统时间
        Calendar c = Calendar.getInstance();
        Date endRequestDate = c.getTime();
        if ("近三月".equals(theQueryTime)) {
            c.add(Calendar.MONTH, -3);
        } else if ("近半年".equals(theQueryTime)) {
            c.add(Calendar.MONTH, -6);
        } else {
            c.add(Calendar.YEAR, -1);
        }
        c.add(Calendar.DAY_OF_MONTH, 1);
        return new String[]{dateToStr(c.getTime(), onlyYMD), dateToStr(endRequestDate, onlyYMD)};
    }

    private static String dateToStr(Date date, boolean onlyYMD) {
        return new SimpleDateFormat(onlyYMD ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss").format(date);
    }


    /**
     * 获取当前时间前三天的时间
     */
    public static long getDataBeforThreeDay(String curtomDate) {
        SimpleDateFormat sdf = TimeUtil.simpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        Date parse = null;
        try {
            parse = sdf.parse(curtomDate);
            calendar.setTime(parse);
            calendar.add(Calendar.DATE, -3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    /**
     * 毫秒数转换成String字符串
     *
     * @param millisecond
     * @return
     */
    public static String getLongToStringDate(long millisecond) {
        Date date = new Date(millisecond);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否为今天
     *
     * @param day 传入的 时间
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsToday(String day, String currentTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar pre = Calendar.getInstance();
        // Date predate = new Date(currentTime);
        Calendar cal = Calendar.getInstance();
        Date date = null;
        Date predate = null;
        try {
            predate = format.parse(currentTime);
            pre.setTime(predate);
            date = format.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        Log.e("判断是否今天", day + ":::" + currentTime + ":::::" + cal.get(Calendar.DAY_OF_YEAR) + ":::" + pre.get(Calendar.DAY_OF_YEAR));
        return false;
    }

    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    public static String getOldDate(int distanceDay, String time) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = TimeUtil.getDateFromString(time);
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("获取n天", distanceDay + "::" + time + ":::" + dft.format(endDate));
        return dft.format(endDate);
    }

    public static long dateDiff(String startTime, String endTime) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long diff;
        long day = 0;
        // 获得两个时间的毫秒时间差异
        try {
            diff = dft.parse(endTime).getTime()
                    - dft.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            if (day >= 1) {
                return day;
            } else {
                if (day == 0) {
                    return 0;
                } else {
                    return -1;
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean isOver48H(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            long cha = end.getTime() - start.getTime();
            double result = cha * 1.0 / (1000 * 60 * 60);
            return result >= 48;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 是否超过20分钟
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isOver20Min(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            long cha = end.getTime() - start.getTime();
            double result = cha * 1.0 / (1000 * 60);
            return result >= 20;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 比较两个时间大小
     *
     * @return endTime比startTime大
     */
    public static boolean compareDate(String startTime, String endTime, int maxCompare) {
        Date sDate = TimeUtil.getDateFromString(startTime);
        Date eDate = TimeUtil.getDateFromString(endTime);
        long diff = -1;
        if (sDate != null && eDate != null) {
            diff = eDate.getTime() - sDate.getTime();
        }
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long day = diff / nd;// 计算差多少天
        if (day < 0) {
            System.out.println("startTime 在endTime前");
            return false;
        } else if (day > maxCompare) {
            System.out.println("endTime 比 startTime 大 " + maxCompare);
            return false;
        } else {
            return true;
        }
//        Date dt1 = getDateFromString(startTime);
//        Date dt2 = getDateFromString(endTime);
//        if (dt1 == null || dt2 == null) {
//            return false;
//        }
//        if (dt1.getTime() > dt2.getTime()) {
//            System.out.println("dt1 在dt2前");
//            return false;
//        } else if (dt1.getTime() < dt2.getTime()) {
//            return true;
//        }
//        return false;
    }

    /**
     * 根据server返回的时间做裁剪-----例如2022-01-07 00:00:00.0   ---> 2022-01-07
     */
    public static String getNeedDate(String time) {
        if (CheckUtil.isEmpty(time)) {
            return "未知";
        }
        if (time.contains(" ")) {
            return time.split(" ")[0].trim();
        }
        return time;
    }
}
