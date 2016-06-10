package org.popkit.leap.elpa.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.config.LeapConfigLoader;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.constents.EnvEnum;
import org.popkit.leap.elpa.entity.DepsItem;
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
    private static final String LOG_FILE_KEY = "elpa_log_file";

    private PelpaUtils() {
    }

    public static String getStaticsPath() {
        return LeapConfigLoader.get("elpa_statics_path");
    }

    public static String getLogFileName() {
        return LeapConfigLoader.get(LOG_FILE_KEY);
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
    public static boolean generatePkgElispFileContent(String pkgName, String version,
                                                   String shortInfo, List<String> keywords,
                                                   List<DepsItem> deps, String url,
                                                   File destPkgDescFile, File originPkgFile) {
        String resultContent = null;
        if (originPkgFile != null && originPkgFile.exists() && originPkgFile.isFile()) {
            try {
                String originContent = FileUtils.readFileToString(originPkgFile);
                String[] originContentArr = originContent.split("\\s+");
                if (originContentArr.length > 2 && pkgName.equals(PelpaUtils.unwrap(originContentArr[1]))) {
                    String versionOrigin = originContentArr[2];
                    resultContent = originContent.replace(versionOrigin, PelpaUtils.wrap(version));
                }
            } catch (Exception e) {
                //
            }
        } else {
            StringBuilder stringBuilder = new StringBuilder("");
            stringBuilder.append("(define-package ").append(wrap(pkgName)).append(" ")
                    .append(wrap(version)).append(" ").append(wrap(shortInfo))
                    .append(" ").append(deps(deps))
                    .append(" ").append(url(url))
                    .append(" ").append(keywords(keywords));

            stringBuilder.append(")");
            resultContent = stringBuilder.toString();
        }

        try {
            if (StringUtils.isNotBlank(resultContent)) {
                FileUtils.writeStringToFile(destPkgDescFile, resultContent);
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static String deps(List<DepsItem> depsItems) {
        if (CollectionUtils.isNotEmpty(depsItems)) {
            StringBuilder sb = new StringBuilder();
            sb.append(" '(");
            for (DepsItem item : depsItems) {
                if (CollectionUtils.isEmpty(depsItems)) {
                    continue;
                }
                sb.append("(");
                sb.append(item.getName()).append(" ").append(wrap(item.getVersionString()));
                sb.append(") ");
            }
            sb.append(")");
            return sb.toString();
        }

        return " 'nil ";
    }

    private static String url(String url) {
        if (StringUtils.isNotBlank(url)) {
            StringBuilder sb = new StringBuilder();
            sb.append(":url").append(" ").append(wrap(url.trim()));
            return sb.toString();
        }

        return " ";
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

    public static String wrap(String string) {
        if (string == null) {
            string = "";
        }
        return "\"" + string + "\"";
    }

    public static String unwrap(String origin) {
        if (origin == null) {
            return StringUtils.EMPTY;
        }

        return origin.replace("\"", "").trim();
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
        if (directory.exists() && directory.isDirectory()) {
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

    public static List<File> getFileListBasedRecipe(RecipeDo recipeDo) {
        List<File> elispFileList = new ArrayList<File>();
        String workingPath = PelpaUtils.getWorkingPath(recipeDo.getPkgName());

        // get all files which will be tar.
        if (CollectionUtils.isNotEmpty(recipeDo.getFileList())) {
            for (String fileName : recipeDo.getFileList()) {
                if (StringUtils.isBlank(fileName)) {
                    continue;
                }

                if (fileName.endsWith("*.el")) {
                    String sub = "";
                    if (fileName.contains("/")) {
                        sub = fileName.substring(0, fileName.lastIndexOf("/"));
                    }
                    elispFileList.addAll(PelpaUtils.getElispFile(workingPath + File.separator + sub));
                } else {
                    if (fileName.endsWith("*")) {   // 某目录下所有文件
                        String sub = "";
                        if (fileName.contains("/")) {
                            sub = fileName.substring(0, fileName.lastIndexOf("/"));
                        }
                        File pathFile = new File(workingPath + File.separator + sub);
                        if (pathFile.exists() && pathFile.isDirectory()) {
                            for (File file : pathFile.listFiles()) {
                                if (!file.getName().startsWith(recipeDo.getPkgName() + "-")) {
                                    elispFileList.add(file);
                                }
                            }
                        }
                    } else {
                        if (fileName.contains("*.")) { // 特定文件类型,如 *.js *.tmp
                            String sub = fileName.substring(0, fileName.lastIndexOf("*."));
                            String[] tmp = fileName.split("\\*\\.");
                            if (tmp.length > 0) {
                                String suffix = tmp[1];
                                File pathFile = new File(workingPath + File.separator + sub);
                                if (pathFile.exists() && pathFile.isDirectory()) {
                                    for (File file : pathFile.listFiles()) {
                                        if (file.getName().endsWith(suffix)) {
                                            elispFileList.add(file);
                                        }
                                    }
                                }
                            }
                        } else {
                            File fileTmp = new File(workingPath + File.separator + fileName);
                            if (fileTmp.exists()) {
                                elispFileList.add(fileTmp);
                            }
                        }
                    }
                }
            }
        } else {
            elispFileList.addAll(PelpaUtils.getElispFile(workingPath));
        }

        return elispFileList;
    }
}
