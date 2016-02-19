package com.simpletour.gateway.ctrip.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mario on 2016/2/16.
 */
public class StringUtils {
    /**
     * 去掉所有的空白字符
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

    /**
     * 去掉包括空格、制表符、换页符等等
     *
     * @param str
     * @return
     */
    public static String replaceTransfer(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("[\\f\\n\\r\\t\\v]*");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 去掉“><”之间的空白字符
     *
     * @param xml
     * @return
     */
    public static String formatXml(String xml) {
        xml = replaceTransfer(xml);

        int start, end;
        String xmlSubString = null, xmlRestString = null;

        for (int i = 0; i < xml.length(); i++) {
            if (i == 0) {
                start = xml.indexOf(">", 0);
                xmlSubString = xml.substring(0, start + 1).trim();
                xmlRestString = xml.substring(start + 1, xml.length()).trim();
            }

            start = xmlRestString.indexOf(">");
            end = xmlRestString.indexOf("<", start);
            if (end < 0){
                xmlSubString = xmlSubString + xmlRestString.trim();
                break;
            }

            String temp = xmlRestString.substring(start + 1, end).trim();
            if (temp.matches("\\s*") || temp.trim().isEmpty()) {
                temp = "";
            }
            xmlSubString = xmlSubString + xmlRestString.substring(0, start + 1) + temp;
            xmlRestString = xmlRestString.substring(end, xmlRestString.length());
        }
        return xmlSubString;
    }

    public static void main(String[] args) {
        System.out.println(StringUtils.replaceBlank("   just do it!    \n\t\r"));
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<request>\n" +
                "   \t <header>\n" +
                "        <accountId>CF7C85B44C6B3A93</accountId>\n" +
                "        <requestTime>2016-02-19 02:19:21</requestTime>\n" +
                "        <serviceName>NoticeOrderCancel</serviceName>\n" +
                "        <sign>4de336911d4de905c73418fb5df6dd0d</sign>\n" +
                "        <version>2.0</version>\n" +
                "    </header>\n" +
                "    <body>\n" +
                "        <cancelCount>3</cancelCount>\n" +
                "        <orderStatus>3</orderStatus>\n" +
                "        <otaOrderId>2340002231-341564</otaOrderId>\n" +
                "        <vendorOrderId>36524703744</vendorOrderId>\n" +
                "    </body>\n" +
                "</request>";
        System.out.println(replaceTransfer(xml));

        String xml1 =
                "<request>\n" +
                        "  <header>\n" +
                        "       <accountId>71</accountId>\n" +
                        "       <serviceName>CreateOrder</serviceName>\n" +
                        "       <requestTime>2016-02-17 17:44:46</requestTime>\n" +
                        "       <version>2.0</version>\n" +
                        "       <sign>4cbc7393329d0946930b9342b1592853</sign>\n" +
                        "   </header>\n" +
                        "   <body>\n" +
                        "       <otaOrderId>2340002223-341553</otaOrderId>\n" +
                        "       <productId>471</productId>\n" +
                        "       <price>1.00</price>\n" +
                        "       <count>3</count>\n" +
                        "       <contactName>携程测试</contactName>\n" +
                        "       <contactMobile>13301136788</contactMobile>\n" +
                        "       <passengerInfos>\n" +
                        "           <passengerInfo>\n" +
                        "               <name>携程测试</name>\n" +
                        "               <mobile>13301136788</mobile>\n" +
                        "               <cardType>1</cardType>\n" +
                        "               <cardNo>110108197605295440</cardNo>\n" +
                        "           </passengerInfo>\n" +
                        "           </passengerInfo>\n" +
                        "               <name>但还是</name>\n" +
                        "               <mobile>15917902131</mobile>\n" +
                        "               <cardType>1</cardType>\n" +
                        "               <cardNo>412823199204068013</cardNo>\n" +
                        "           </passengerInfo>\n" +
                        "           </passengerInfo>\n" +
                        "               <name>携程测试</name>\n" +
                        "               <mobile>13301136789</mobile>\n" +
                        "               <cardType>1</cardType>\n" +
                        "               <cardNo>341126197709218366</cardNo>\n" +
                        "           </passengerInfo>\n" +
                        "       </passengerInfos>\n" +
                        "       <useDate>2016-02-18</useDate>\n" +
                        "       <useEndDate>2016-02-18</useEndDate>\n" +
                        "       <payMode>1</payMode>\n" +
                        "       <extendInfo>\n" +
                        "           <productType><![CDATA[0]]></productType>\n" +
                        "       </extendInfo>\n" +
                        "   </body>\n" +
                        "</request>";
        System.out.println(formatXml(xml1));
    }

}
