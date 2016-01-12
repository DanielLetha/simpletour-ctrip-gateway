package com.simpletour.gateway.ctrip.util;

/**
 * Created by Mario on 2016/1/12.
 */
public class SecondParserUtil {
    public static Long parse(String str) {
        return SecondParserUtil.parse(str, ":");
    }

    public static Long parse(String str, String separator) {
        String[] fragment = str.split(separator, 2);
        String hour = fragment[0];
        String minute = fragment[1];
        return Long.parseLong(hour) * 3600 + Long.parseLong(minute) * 60;
    }

    public static String parse(Long second) {
        Double hour = Math.floor(second / 3600d);
        Double minute = Math.floor((second % 3600) / 60d);
        return String.format("%02d:%02d", hour.intValue(), minute.intValue());
    }

    public static String parseHour(Long second) {
        Double hour = Math.floor(second / 3600d);
        Integer h = hour.intValue();
        return h.toString();
    }

    public static String parseSecond(Long second) {
        Double minute = Math.floor((second % 3600) / 60d);
        Integer m = minute.intValue();
        return m.toString();
    }

}
