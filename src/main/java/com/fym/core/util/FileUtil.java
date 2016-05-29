package com.fym.core.util;

import java.io.*;

/**
 *
 * Created by fengy on 2016/5/5.
 * 文件Util
 */
public class FileUtil {
    public static String getContent(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        char[] buffer = new char[1024];
        while (true) {
            int len = reader.read(buffer);
            if (len > 0) {
                sb.append(buffer, 0, len);
            } else {
                break;
            }
        }
        return sb.toString();
    }
}
 
