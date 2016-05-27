package com.fym.core.util;

import com.fym.core.util.jackson.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;

/**
 * Owned by Planck System
 * Created by fengy on 2016/2/18.
 */
@Component
public class HttpUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    public static <T> T httpRequestGet(String requestUrl, Class<T> clz) {
        StringBuffer buffer = null;

        try {
            // 建立连接
            URL url = new URL(requestUrl);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url
                    .openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");

            // 获取输入流
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

            String str;
            buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            httpUrlConn.disconnect();
            T ret = objectMapper.readValue(buffer.toString(), clz);
            return ret;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public static <T> T httpRequestPost(String requestUrl, Object request, Class<T> clz) {
        StringBuffer buffer = null;

        try {
            // 建立连接
            URL url = new URL(requestUrl);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url
                    .openConnection();
            byte[] bytes = objectMapper.writeValueAsString(request).getBytes("UTF-8");
            httpUrlConn.setRequestProperty("Content-Type", "application/json");
            httpUrlConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            httpUrlConn.setRequestProperty("Charset", "UTF-8");
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("POST");
            httpUrlConn.setRequestProperty("Content-length", String.valueOf(bytes.length));
            httpUrlConn.setDoOutput(true);
            httpUrlConn.getOutputStream().write(bytes);
            httpUrlConn.getOutputStream().flush();


            // 获取输入流
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

            String str;
            buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            httpUrlConn.disconnect();
            T ret = objectMapper.readValue(buffer.toString(), clz);
            return ret;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("width-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 将给定的字节数组转换成IPV4的十进制分段表示格式的ip地址字符串
     */
    public static String binaryArray2Ipv4Address(byte[] addr) {
        String ip = "";
        for (int i = 0; i < addr.length; i++) {
            ip += (addr[i] & 0xFF) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }

    /**
     * 将给定的用十进制分段格式表示的ipv4地址字符串转换成字节数组
     */
    public static byte[] ipv4Address2BinaryArray(String ipAdd) {
        byte[] binIP = new byte[4];
        String[] strs = ipAdd.split("\\.");
        try {
            for (int i = 0; i < strs.length; i++) {
                binIP[i] = (byte) Integer.parseInt(strs[i]);
            }
        } catch (Exception e) {
            LOGGER.info("IP地址无效");
            return null;
        }
        return binIP;
    }

    public static String getMAC(byte[] ipBytes) {
        InetAddress ip;
        try {
            ip = InetAddress.getByAddress(ipBytes);
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i],
                        (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();

        } catch (Exception e) {
            LOGGER.info("无法从ip" + StringUtil.ByteToString(ipBytes) + "获取mac地址");
            LOGGER.warn(e.getMessage());

        }
        return "";
    }

    // public String getMACAddress(String ip){
    // String str = "";
    // String macAddress = "";
    // try {
    // Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
    // InputStreamReader ir = new InputStreamReader(p.getInputStream());
    // LineNumberReader input = new LineNumberReader(ir);
    // for (int i = 1; i < 100; i++) {
    // str = input.readLine();
    // if (str != null) {
    // if (str.indexOf("MAC Address") > 1) {
    // macAddress = str.substring(str.indexOf("MAC Address") + 14,
    // str.length());
    // break;
    // }
    // }
    // }
    // } catch (IOException e) {
    // e.printStackTrace(System.out);
    // }
    // return macAddress;
    // }

    public static Cookie getCookie(String key, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if (cookieName.equals(key)) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
 
