package com.fym.core.util;

/**
 *
 * Created by fengy on 2016/5/14.
 */
public class BitUtil {
    public static boolean include(int origin, int item) {
        return (origin & item) == item;
    }
}
 
