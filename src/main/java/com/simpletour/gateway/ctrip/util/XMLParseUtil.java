package com.simpletour.gateway.ctrip.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * XML工具类
 * <p>
 * Created by Jeff.Song on 2015/12/30.
 */
public class XMLParseUtil {

    /**
     * JavaBean转换成xml
     * 默认编码UTF-8
     *
     * @param obj
     * @return
     */
    public static String convertToXml(Object obj) {
        return convertToXml(obj, "UTF-8");
    }

    /**
     * JavaBean转换成xml
     *
     * @param obj
     * @param encoding
     * @return
     */
    public static String convertToXml(Object obj, String encoding) {
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            result = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * xml转换成JavaBean
     *
     * @param xml
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertToJavaBean(String xml, Class<T> c) {
        T t = null;
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            t = (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return t;
    }

    /**
     * xml去掉头信息
     *
     * @param xml xml
     * @return
     */
    public static String subStringForXML(String xml) {
        String xmlString;
        try {
            int pos = xml.lastIndexOf("?>");
            xmlString = xml.substring(pos + 2, xml.length());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return xmlString;
    }

    /**
     * xml过滤出body信息
     *
     * @param xml xml
     * @return
     */
    public static String subBodyStringForXml(String xml) {
        String xmlBodyString;
        try {
            int startPos = xml.indexOf("<body");
            int endPos = xml.lastIndexOf("</body>") + "</body>".length();
            xmlBodyString = xml.substring(startPos, endPos);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return xmlBodyString;
    }

    public static void main(String[] args) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<request>\n" +
                "    <header>\n" +
                "        <accountId>1</accountId>\n" +
                "        <requestTime>2015-10-19 16:05:31</requestTime>\n" +
                "        <serviceName>VerifyOrder</serviceName>\n" +
                "        <sign>E18005B1378F7BC28ADBA3BB8B9E72E0</sign>\n" +
                "        <version>2.0</version>\n" +
                "    </header>\n" +
                "    <body>\n" +
                "        <contactMobile>130111111111</contactMobile>\n" +
                "        <contactName>偏分偏出三分</contactName>\n" +
                "        <count>2</count>\n" +
                "        <extendInfo>\n" +
                "            <productType>product</productType>\n" +
                "        </extendInfo>\n" +
                "        <passengerInfos>\n" +
                "            <passengerInfo>\n" +
                "                <cardNo>511102199107200011</cardNo>\n" +
                "                <cardType>1</cardType>\n" +
                "                <mobile>010101010101010</mobile>\n" +
                "                <name>大毛</name>\n" +
                "            </passengerInfo>\n" +
                "            <passengerInfo>\n" +
                "                <cardNo>511102199107200011</cardNo>\n" +
                "                <cardType>1</cardType>\n" +
                "                <mobile>010101010101010</mobile>\n" +
                "                <name>毛线</name>\n" +
                "            </passengerInfo>\n" +
                "        </passengerInfos>\n" +
                "        <price>100</price>\n" +
                "        <productId>1</productId>\n" +
                "        <useDate>2016-01-26</useDate>\n" +
                "    </body>\n" +
                "</request>\n";
        String bodyString = subBodyStringForXml(xml);
        System.out.println(bodyString);
    }

}
