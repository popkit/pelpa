package org.popkit.leap.elpa.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.config.LeapConfigLoader;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.constents.EnvEnum;
import org.popkit.leap.elpa.entity.RecipeDo;

import java.io.BufferedReader;
import java.io.File;
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

    public static String getPkgElispFileName(String pkgName) {
        return getWorkingPath(pkgName) +  "/" + pkgName + "-pkg.el";
    }

    /**
     (define-package "ztree" "20150703.113" "Text mode directory tree" 'nil :keywords
     '("files" "tools")
     :url "https://github.com/fourier/ztree")
     * @param pkgName
     * @return
     */
    public static void generatePkgElispFileContent(String pkgName, String version,
                                                   String shortInfo, List<String> keywords) {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("(define-package ").append(wrap(pkgName)).append(" ")
                .append(wrap(version)).append(" ").append(wrap(shortInfo)).append(" 'nil ")
                .append(keywords(keywords));

        stringBuilder.append(")");
        File file = new File(getPkgElispFileName(pkgName));
        try {
            FileUtils.writeStringToFile(file, stringBuilder.toString());
        } catch (IOException e) {
            //
        }
    }

    private static String keywords(List<String> keywords) {
        if (CollectionUtils.isNotEmpty(keywords)) {
            StringBuilder sb = new StringBuilder();
            sb.append(":keywords").append(" '(");
            for (String k : keywords) {
                sb.append(wrap(k.trim())).append(" ");
            }
            sb.append(")");
            return sb.toString();
        }
        return "";
    }

    private static String wrap(String string) {
        if (string == null) {
            string = "";
        }
        return "\"" + string + "\"";
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
                String contetnString = FileUtils.readFileToString(item, "UTF-8");
                RecipeDo resItem = RecipeParser.parse(contetnString);
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
