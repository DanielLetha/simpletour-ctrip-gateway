package com.simpletour.gateway.ctrip.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mario on 2016/2/16.
 */
public class StringUtils {
    /**
     * 去掉空白字符
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static void main(String[] args) {
        System.out.println(StringUtils.replaceBlank("   just do it!    \n\t\r"));
    }
}
