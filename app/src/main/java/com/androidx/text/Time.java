package com.androidx.text;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Author: Relin
 * Describe:时间日期
 * Date:2020/11/28 16:40
 */
public class Time {

    /**
     * 年-月-日
     */
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    /**
     * 24小时 - 时：分：秒
     */
    public static final String H24_MM_SS = "HH:mm:ss";
    /**
     * 12小时 - 时：分：秒
     */
    public static final String H12_MM_SS = "hh:mm:ss";
    /**
     * 24小时 - 时：分
     */
    public static final String H24_MM = "24H:mm";
    /**
     * 12小时 - 时：分
     */
    public static final String H12_MM = "12H:mm";
    /**
     * 月-日
     */
    public static final String MM_DD = "MM-DD";
    /**
     * 24小时，年-月-日 时：分
     */
    public static final String YYYY_MM_DD_H24_MM = "yyyy-MM-dd HH:mm";
    /**
     * 24小时，年-月-日 时：分：秒
     */
    public static final String YYYY_MM_DD_H24_MM_SS = "yyyy-MM-dd HH:mm:ss";
    /**
     * 12小时，年-月-日 时：分
     */
    public static final String YYYY_MM_DD_H12_MM = "yyyy-MM-dd hh:mm";
    /**
     * 12小时，年-月-日 时：分：秒
     */
    public static final String YYYY_MM_DD_H12_MM_SS = "yyyy-MM-dd hh:mm:ss";


    /**
     * 现在的日期时间
     *
     * @return
     */
    public static String now() {
        return format(YYYY_MM_DD_H24_MM_SS).format(new Date());
    }

    /**
     * 现在的日期时间
     *
     * @param dateFormat 日期格式
     * @return
     */
    public static String now(String dateFormat) {
        return format(dateFormat).format(new Date());
    }

    /**
     * 时间戳转时间
     *
     * @param timestamp 时间戳，单位秒
     * @return
     */
    public static String parseTime(String timestamp) {
        return parseTime(timestamp, TimeUnit.SECONDS);
    }

    /**
     * 时间戳转时间
     *
     * @param timestamp 时间戳
     * @param unit      单位
     * @return
     */
    public static String parseTime(String timestamp, TimeUnit unit) {
        if (TextUtils.isEmpty(timestamp)) {
            return "";
        }
        long time = Long.parseLong(timestamp);
        if (unit == TimeUnit.SECONDS) {
            time *= 1000;
        }
        if (unit == TimeUnit.MILLISECONDS) {
            time *= 1;
        }
        if (unit == TimeUnit.MINUTES) {
            time = 1000 * 60;
        }
        return format(YYYY_MM_DD_H24_MM_SS).format(new Date(time));
    }

    /**
     * 时间戳转时间
     *
     * @param timestamp 时间戳
     * @param format    格式
     * @param unit      单位
     * @return
     */
    public static String parseTime(String timestamp, String format, TimeUnit unit) {
        if (TextUtils.isEmpty(timestamp)) {
            return "";
        }
        long time = Long.parseLong(timestamp);
        if (unit == TimeUnit.SECONDS) {
            time *= 1000;
        }
        if (unit == TimeUnit.MILLISECONDS) {
            time *= 1;
        }
        if (unit == TimeUnit.MINUTES) {
            time = 1000 * 60;
        }
        return format(format).format(new Date(time));
    }

    /**
     * 时间转时间戳
     *
     * @param time 时间字符串
     * @return
     */
    public static long parseTimestamp(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0;
        }
        return parseTimestamp(time, YYYY_MM_DD_H24_MM_SS, TimeUnit.SECONDS);
    }

    /**
     * 时间转时间戳
     *
     * @param time   时间字符
     * @param format 时间格式
     * @param unit   单位
     * @return
     */
    public static long parseTimestamp(String time, String format, TimeUnit unit) {
        if (TextUtils.isEmpty(time)) {
            return 0;
        }
        if (unit == TimeUnit.SECONDS) {
            return parse(time, format).getTime() / 1000;
        }
        if (unit == TimeUnit.MILLISECONDS) {
            return parse(time, format).getTime();
        }
        if (unit == TimeUnit.MINUTES) {
            return parse(time, format).getTime() / 1000 / 60;
        }
        return parse(time, format).getTime() / 1000;
    }

    /**
     * 字符串转时间
     *
     * @param time 时间字符串
     * @return
     */
    public static Date parse(String time) {
        Date date = null;
        try {
            date = format(YYYY_MM_DD_H24_MM_SS).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 字符串转时间
     *
     * @param time   时间字符
     * @param format 时间格式
     * @return
     */
    public static Date parse(String time, String format) {
        Date date = null;
        try {
            date = format(format).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 日期时间差
     *
     * @param start  开始时间
     * @param end    结束时间
     * @param format 时间格式
     * @return
     */
    public static long timeDiff(String start, String end, String format) {
        return parse(end, format).getTime() - parse(start, format).getTime();
    }

    /**
     * 创建时间日期格式对象
     *
     * @param format 格式
     * @return
     */
    public static SimpleDateFormat format(String format) {
        return new SimpleDateFormat(format);
    }

    /**
     * 通过年份和月份获取对应的月份的天数
     *
     * @param year  年
     * @param month 月
     * @return
     */
    public static int days(int year, int month) {
        if (year % 100 == 0 && year % 400 == 0 && month == 2) return 29;
        else {
            switch (month) {
                case 2:
                    return 28;
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    return 31;
                case 4:
                case 6:
                case 9:
                case 11:
                    return 30;
            }
        }
        return 0;
    }

    /**
     * 分割时间
     *
     * @param time 时间字符串，yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static int[] split(String time) {
        int[] result = new int[6];
        Calendar calendar = Calendar.getInstance();
        result[0] = calendar.get(Calendar.YEAR);
        result[1] = calendar.get(Calendar.MONTH + 1);
        result[2] = calendar.get(Calendar.DAY_OF_MONTH);
        result[3] = calendar.get(Calendar.HOUR_OF_DAY);
        result[4] = calendar.get(Calendar.MINUTE);
        result[5] = calendar.get(Calendar.SECOND);
        if (Null.isNull(time)) {
            return result;
        }
        String arr[] = time.split(" ");
        for (int i = 0; i < arr.length; i++) {
            if (i == 0) {
                String before[] = arr[i].split("-");
                for (int j = 0; j < before.length; j++) {
                    result[j] = Number.parseInt(before[j]);
                }
            }
            if (i == 1) {
                String after[] = arr[i].split(":");
                for (int j = 0; j < after.length; j++) {
                    result[j + 3] = Number.parseInt(after[j]);
                }
            }
        }
        return result;
    }

}
