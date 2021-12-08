package com.example.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    public static final String YYYY_MM_DD_FORMAT = "yyyy-MM-dd";

    public static String dateToString(Date date, String format) {
        DateFormat dateFormatter = new SimpleDateFormat(format);
        return dateFormatter.format(date);
    }
}
