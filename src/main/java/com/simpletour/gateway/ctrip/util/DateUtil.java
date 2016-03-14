package com.simpletour.gateway.ctrip.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mario on 2016/1/6.
 */
public class DateUtil {

    /**
     * 将指定格式字符串转换为日期类型
     *
     * @param dateStr
     * @param dateFormat
     * @return
     * @throws ParseException
     */
    public static Date convertStrToDate(String dateStr, String dateFormat) throws ParseException {
        if (dateStr == null || dateStr.isEmpty() || dateFormat == null || dateFormat.isEmpty()) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (Exception e) {
            throw new RuntimeException("DateUtil.convertStrToDate():" + e.getMessage());
        }
    }

    /**
     * 将指定日期转化为格式化后的日期格式
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String convertDateToStr(Date date, String dateFormat) {
        String dateStr = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            dateStr = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    /**
     * 将long型时间转化为String
     *
     * @param time
     * @return
     */
    public static String convertLongToTime(Long time) {

        String hour = SecondParserUtil.parseHour(time);
        if (Integer.parseInt(hour) % 24 < 10) {
            hour = "0" + Integer.parseInt(hour) % 24;
        } else {
            hour = String.valueOf(Integer.parseInt(hour) % 24);
        }
        String second = SecondParserUtil.parseSecond(time);
        if (Integer.parseInt(second) < 10) {
            second = "0" + second;
        }
        return hour + ":" + second;
    }

    /**
     * 获取昨天
     *
     * @return
     */
    public static Date getDayBeforeYesterDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        return cal.getTime();
    }

    public static void main(String[] args) throws ParseException {
//        try {
//            Date date = convertStrToDate("2016-01-06", "yyyy-MM-dd");
//            System.out.println(date.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        System.out.println(convertDateToStr(new Date(), "yyyy-MM-dd"));
//        Long time = 178260L;
//        System.out.println(convertLongToTime(time));
//        Date date = new Date();
//        String dateFormat = convertDateToStr(date, "yyyy-MM-dd");
//        System.out.println(convertStrToDate(dateFormat, "yyyy-MM-dd"));
        System.out.println(getDayBeforeYesterDay());
    }
}
