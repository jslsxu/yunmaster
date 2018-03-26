package com.yun.yunmaster.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class StringUtils {
    public static boolean isOnlyPointNumber(String number) {//保留两位小数正则
        Pattern pattern = Pattern.compile("^\\d+\\.?\\d{0,2}$");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }
}
