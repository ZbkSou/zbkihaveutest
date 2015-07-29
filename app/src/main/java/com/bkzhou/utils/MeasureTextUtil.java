package com.bkzhou.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bkzhou on 15-7-27.
 */
public class MeasureTextUtil {
    /**
     * 验证是否是合格的邮箱
     *
     * @param s
     * @return
     */
    public static boolean idEmail(String s) {
        Pattern pattern_email = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");//邮箱格式匹配
        Matcher matcher = pattern_email.matcher(s);
        return matcher.matches();
    }
}
