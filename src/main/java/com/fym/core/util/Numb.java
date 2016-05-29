package com.fym.core.util;

/**
 *
 * Created by fengy on 2016/3/5.
 */
public class Numb {
    //小端法
    public static int toInt(byte[] bytes) {
        int iOutcome = 0;
        for (int i = 0; i < bytes.length; i++) {
            byte bLoop = bytes[i];
            iOutcome = iOutcome + (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    //大端法
    public static int toIntBig(byte[] bytes) {
        int iOutcome = 0;
        for (int i = 0; i < bytes.length; i++) {
            byte bLoop = bytes[i];
            iOutcome = (iOutcome << (8 )) + (bLoop & 0xFF);
        }
        return iOutcome;
    }


}
 
