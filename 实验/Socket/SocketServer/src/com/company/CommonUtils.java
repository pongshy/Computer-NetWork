package com.company;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: pongshy
 * @Date: 2020/9/19 22:25
 * @Description:
 **/
public class CommonUtils {

    public static String regular(String str) {
        String regx = "\\b[0-9a-z]*\\b";
        Pattern pattern = Pattern.compile(regx);

        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }
}
