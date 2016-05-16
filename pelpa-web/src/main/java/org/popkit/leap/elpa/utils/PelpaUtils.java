package org.popkit.leap.elpa.utils;

import org.apache.commons.lang3.StringUtils;
import org.popkit.core.config.LeapConfigLoader;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.constents.EnvEnum;
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
public class PelpaUtils {
    private static final String WORKING_DIR_KEY = "elpa_working_path";
    public static final String RECIPE_FILE_PATH_KEY = "elpa_recipes_path";
    private static final String HTML_DIR_KEY = "elpa_html_path";

    private PelpaUtils() {
    }

    public static String getRecipeFilePath() {
        return LeapConfigLoader.get(RECIPE_FILE_PATH_KEY);
    }

    public static String getWorkingPath(String pkgName) {
        return LeapConfigLoader.get(WORKING_DIR_KEY) + pkgName;
    }

    public static String getHtmlPath() {
        return LeapConfigLoader.get(HTML_DIR_KEY);
    }

    public static EnvEnum getEnv() {
        String envString = LeapConfigLoader.get("elpa_env");
        if ("PRODUCTION".equalsIgnoreCase(envString)) {
            return EnvEnum.PRODUCTION;
        }

        return EnvEnum.BETA;
    }

    public static List<File> getElispFile(String dir) {
        List<File> fileList = new ArrayList<File>();
        File directory = new File(dir);
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".el")) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
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

                RecipeDo resItem = RecipeParser.convert(current.toString());
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
}
