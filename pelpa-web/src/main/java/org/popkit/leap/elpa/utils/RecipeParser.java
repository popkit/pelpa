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
        String[] keyValuePair = sub.substring(sub.indexOf(suArr[0]) + suArr[0].length()).split(":");

        for (String keyValue : keyValuePair) {
            try {
                if (StringUtils.isNotBlank(keyValue) && keyValue.split("\\s+").length > 1) {
                    String key = keyValue.split("\\s+")[0].trim();
                    String value = keyValue.split("\\s+")[1].trim();
                    if ("files".equalsIgnoreCase(key)) {
                        recipeDo.update(key, fileValue(keyValue));
                    } else if ("repo".endsWith(key)) {
                        recipeDo.update(key, trimIt(value));
                    } else {
                        recipeDo.update(key, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return recipeDo;
    }

    private static String trimIt(String orgin) {
        return orgin.replaceAll("\"", "").trim();
    }

    private static String fileValue(String origin) {
        if (StringUtils.isBlank(origin)) {
            return origin;
        }
        String fileContent = origin.substring(origin.indexOf("files") + "files".length());
        return fileContent.replaceAll("\"","").replaceAll("\\(", "").replaceAll("\\)", "").trim();
    }
}
