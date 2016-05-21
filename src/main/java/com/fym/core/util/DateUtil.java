package com.fym.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fengy on 2016/1/31.
 */
public class DateUtil {
    static {
//        TimeZone.setDefault(TimeZone.getTimeZone("GMT-8:00"));
    }

    public static Date getCurrent() {
        return new Date();
    }

    private static Date tdate;

    public static Date getToday() {
        Date date = Calendar.getInstance().getTime();
        if (tdate == null || tdate.getDate() != date.getDate()) {
            tdate = new Date(date.getYear(), date.getMonth(), date.getDate());
        }
        return tdate;
    }

    public static String convertToDate(Date dt) {
        if (dt == null) {
            return "1900-01-01";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(dt);
    }

    public static String convertToDatetime(Date dt) {
        if (dt == null) {
            return "1900-01-01 00:00:00";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(dt);
    }
}
