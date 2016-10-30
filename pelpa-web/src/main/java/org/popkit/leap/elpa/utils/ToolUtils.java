package org.popkit.leap.elpa.utils;

import org.apache.commons.io.FileUtils;
import org.popkit.core.logger.LeapLogger;

import java.io.File;
import java.io.IOException;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-16:09:47
 */
public class ToolUtils {
    private ToolUtils() {}

    public static void main(String[] args) {
        String pkgWorkingPath = "/Users/aborn/github/pelpa/working/base16-theme";
        String pkgName = "base16-theme";
        cleanOldTempTarWorkingFile(pkgWorkingPath, pkgName, "20161013.222");
    }

    // 刪除老的临时文件
    public static void cleanOldTempTarWorkingFile(
            String pkgWorkingPath, String pkgName, String currentVersion) {
        File pathFile = new File(pkgWorkingPath);

        if (pathFile.exists() && pathFile.isDirectory()) {
            for (File file : pathFile.listFiles()) {
                if (!file.isDirectory()) {
                    continue;
                }

                String fileName = file.getName();
                if (fileName.equals(pkgName + "-" + currentVersion)) {
                    continue;
                }
                if (fileName.startsWith(pkgName + "-20")) {
                    try {
                        LeapLogger.info("delete old temptar working path:" + file.getAbsolutePath());
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
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

    public static boolean isEmptyPath(String dirStr) {
        return isEmptyPath(new File(dirStr));
    }

    public static boolean isEmptyPath(File directory) {

        if (directory.exists() && directory.isDirectory()) {

            boolean emptypath = false;
            String[] fileStrings = directory.list();

            if (fileStrings == null || fileStrings.length == 0) {
                emptypath = true;
            } else {
                boolean containsFile = false;
                for (String item : fileStrings){
                    if (!item.startsWith(".")) {
                        containsFile = true;
                    }
                }
                emptypath = !containsFile;
            }
            return emptypath;
        }

        return false;
    }
}
