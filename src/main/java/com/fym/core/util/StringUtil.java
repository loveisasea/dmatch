package com.fym.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class StringUtil {
    public final static String Unknown = "<未知>";
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);

    // 例子："1 2" -> ("1","2")
    // 例子：null -> null
    // 例子：" " -> null
    public static List<String> splitToList(String words) {
        List<String> keywords = null;
        if (words != null) {
            String[] splits = words.split(" ");
            if (splits != null && splits.length > 0) {
                keywords = new ArrayList<String>();
                for (String word : splits) {
                    if (word != null && !word.isEmpty())
                        keywords.add(word);
                }
            }
        }
        return keywords;
    }


    /**
     * [1,2,3,]返回"1,2,3"
     */
    public static String compact(Object[] objects) {
        if (objects == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            sb.append(objects[i]);
            if (i < objects.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * [1,2,3,]返回"1,2,3"
     */
    public static String compact(Collection objects) {
        if (objects == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int size = objects.size() - 1;
        Iterator iter = objects.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
            if (size > 0) {
                sb.append(",");
            }
            size--;
        }
        return sb.toString();
    }

    /**
     * 默认的字符集编码 UTF-8 一个汉字占三个字节
     */
    private static String CHAR_ENCODE = "UTF-8";

    /**
     * 设置全局的字符编码
     *
     * @param charEncode
     */
    public static void configCharEncode(String charEncode) {
        CHAR_ENCODE = charEncode;
    }

    /**
     * @param str 源字符串转换成字节数组的字符串
     * @return
     */
    public static byte[] StringToByte(String str) {
        return StringToByte(str, CHAR_ENCODE);
    }

    /**
     * @param srcObj 源字节数组转换成String的字节数组
     * @return
     */
    public static String ByteToString(byte[] srcObj) {
        return ByteToString(srcObj, CHAR_ENCODE);
    }

    /**
     * UTF-8 一个汉字占三个字节
     *
     * @param str 源字符串 转换成字节数组的字符串
     * @return
     */
    public static byte[] StringToByte(String str, String charEncode) {
        byte[] destObj = null;
        try {
            if (null == str || str.equals("")) {
                destObj = new byte[0];
                return destObj;
            } else {
                destObj = str.getBytes(charEncode);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
        }
        return destObj;
    }

    /**
     * @param srcObj 源字节数组转换成String的字节数组
     * @return
     */
    public static String ByteToString(byte[] srcObj, String charEncode) {
        String destObj = null;
        try {
            destObj = new String(srcObj, charEncode);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
        }
        return destObj.replaceAll("\0", " ");
    }

    /**
     * 将一个字符串转化为输入流
     */
    public static InputStream getStringStream(String sInputString) {
        if (sInputString != null && !sInputString.trim().equals("")) {
            try {
                ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
                return tInputStringStream;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将一个输入流转化为字符串
     */
    public static String getStreamString(InputStream tInputStream) {
        if (tInputStream != null) {
            try {
                BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(tInputStream, Charset.forName("UTF-8")));
                StringBuffer tStringBuffer = new StringBuffer();
                String sTempOneLine = new String("");
                while ((sTempOneLine = tBufferedReader.readLine()) != null) {
                    tStringBuffer.append(sTempOneLine);
                }
                return tStringBuffer.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

}
