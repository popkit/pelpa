package org.popkit.leap.elpa.utils;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-16:09:47
 */
public class ToolUtils {
    private ToolUtils() {}

    public static String toHumanable(long time_millsecod) {
        long second = time_millsecod / 1000;
        if (second < 60) {
            return second + "s";
        } else if (second < 60*60) {
            return second / (60) + "min";
        } else if (second < 60*60*24) {
            return second / (60*60) + "hour";
        } else {
            return second / (60*60*24) + "day";
        }
    }
}
