package com.dw.countanalyse.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

    public static String getDate(Date date){
        return dateFormat.format(date);
    }

    public static String getTime(Date date) {
        return timeFormat.format(date);
    }
}
