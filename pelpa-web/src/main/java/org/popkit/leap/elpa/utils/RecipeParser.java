package org.popkit.leap.elpa.utils;

import org.apache.commons.lang3.StringUtils;
import org.popkit.leap.elpa.entity.RecipeDo;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-16:12:02
 */
public class RecipeParser {

    public static RecipeDo convert(String origin) {
        String sub = null;
        try {
            sub = origin.substring(origin.indexOf('(') + 1, origin.indexOf(')'));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sub == null) {
            return null;
        }

        String[] suArr = sub.split(" ");
        RecipeDo recipeDo = new RecipeDo();
        recipeDo.setPkgName(suArr[0]);
        String[] keyValuePair = sub.substring(sub.indexOf(suArr[0]) + suArr[0].length()).split(":");

        for (String keyValue : keyValuePair) {
            try {
                if (StringUtils.isNotBlank(keyValue) && keyValue.split("").length > 1) {
                    String key = keyValue.split(" ")[0];
                    String value = keyValue.split(" ")[1];
                    recipeDo.update(key, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return recipeDo;
    }
}
