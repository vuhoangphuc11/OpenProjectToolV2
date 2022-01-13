package com.example.common;

import java.util.Collection;
import java.util.Map;

public class CommonUtils {
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static boolean isNullOrEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isNullOrEmpty(Map map) {
        return map == null || map.size() == 0;

    }
}
