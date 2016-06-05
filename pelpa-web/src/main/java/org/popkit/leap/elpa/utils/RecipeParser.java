package org.popkit.leap.elpa.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.leap.elpa.entity.RecipeDo;

import java.io.File;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-16:12:02
 */
public class RecipeParser {

    public static RecipeDo parsePkgRecipe(String pkgName) {
        String recipe = PelpaUtils.getRecipeFilePath() + pkgName;
        File recipeFile = new File(recipe);
        if (recipeFile.exists() && recipeFile.isFile()) {
            try {
                String content = FileUtils.readFileToString(recipeFile, "UTF-8");
                return parse(content);
            } catch (Exception e) {

            }
        }
        return null;
    }

    public static RecipeDo parse(String origin) {
        String sub = null;
        try {
            sub = origin.substring(origin.indexOf('(') + 1, origin.lastIndexOf(')'));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sub == null) {
            return null;
        }

        String[] suArr = sub.split("\\s+");
        RecipeDo recipeDo = new RecipeDo();
        recipeDo.setPkgName(suArr[0].trim());

        String[] keyValueStringPair =  sub.substring(sub.indexOf(suArr[0]) + suArr[0].length()).trim().split(":");
        for (String keyValueString : keyValueStringPair) {
            if (StringUtils.isBlank(keyValueString)) {
                continue;
            }

            String[] keyValuePair = keyValueString.split("\\s+");
            if (keyValuePair.length <= 1) {
                continue;
            }
            String key = keyValuePair[0].trim();
            String value = keyValueString.substring(keyValueString.indexOf(keyValuePair[0]) + keyValuePair[0].length());
            if ("repo".equals(key) || "url".equals(key)) {
                recipeDo.update(key, trimIt(value));
            } else if ("files".endsWith(key)) {
                recipeDo.update(key, fileValue(value));
            } else {
                recipeDo.update(key, value.trim());
            }
        }
        return recipeDo;
    }

    private static String trimIt(String orgin) {
        return orgin.replaceAll("\"", "").trim();
    }

    private static String fileValue(String origin) {
        return origin.replaceAll("\"","").replaceAll("\\(", "").replaceAll("\\)", "").trim();
    }
}
