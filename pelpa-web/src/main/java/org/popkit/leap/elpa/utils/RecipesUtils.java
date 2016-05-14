package org.popkit.leap.elpa.utils;

import org.apache.commons.lang3.StringUtils;
import org.popkit.core.config.LeapConfigLoader;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.RecipeDo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:06:59
 */
public class RecipesUtils {

    public static final String RECIPE_FILE_PATH_KEY = "elpa_recipes_path";

    private RecipesUtils() {
    }

    public static String getRecipeFilePath() {
        return LeapConfigLoader.get(RECIPE_FILE_PATH_KEY);
    }

    public static List<File> getRecipeFileList() {
        List<File> fileList = new ArrayList<File>();
        String recipesDir = getRecipeFilePath();
        File directory = new File(recipesDir);
        if (directory.isDirectory()) {
            fileList.addAll(Arrays.asList(directory.listFiles()));
        }

        Iterator<File> it = fileList.iterator();
        while (it.hasNext()) {
            String name = it.next().getName();
            if (StringUtils.isBlank(name) || name.trim().startsWith(".")) {
                it.remove();
            }
        }
        return fileList;
    }

    public static List<RecipeDo> asRecipeArch(List<File> fileList) {
        List<RecipeDo> recipeList = new ArrayList<RecipeDo>();

        for (File item : fileList) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(item));
                String sCurrentLine;
                StringBuilder current = new StringBuilder("");
                while ((sCurrentLine = br.readLine()) != null) {
                    if (StringUtils.isNotBlank(sCurrentLine) && (!sCurrentLine.trim().startsWith(";"))) {
                        current.append(sCurrentLine);
                    }
                }

                RecipeDo resItem = convert(current.toString());
                if (resItem != null) {
                    recipeList.add(resItem);
                }
            } catch (IOException e) {
                LeapLogger.error("error", e);
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return recipeList;
    }

    private static RecipeDo convert(String origin) {
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
