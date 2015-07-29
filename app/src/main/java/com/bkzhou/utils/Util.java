package com.bkzhou.utils;

/**
 * Created by bkzhou on 15-7-23.
 */
public class Util {

    private static long lastClickTime;

    /**
     * 防止连续点击
     * @return
     */

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
