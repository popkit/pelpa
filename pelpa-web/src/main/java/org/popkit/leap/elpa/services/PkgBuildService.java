package org.popkit.leap.elpa.services;

import org.apache.commons.io.FileUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ArchiveVo;
import org.popkit.leap.elpa.entity.DepsItem;
import org.popkit.leap.elpa.entity.PackageInfo;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.popkit.leap.elpa.utils.TimeVersionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 将下载下来的package构建成标准格式
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:11:10
 */
@Service
public class PkgBuildService {
    private static final String SINGLE = "single";
    private static final String TAR = "tar";
    private static final String ARCHIVE_JSON = "archive.json";

    @Autowired
    private RecipesService recipesService;

    public void d8() {
        RecipeDo recipeDo = recipesService.randomRecipe();
        buildPackage(recipeDo);
    }

    public void buildPackage(RecipeDo recipeDo) {
        String workingPath = PelpaUtils.getWorkingPath(recipeDo.getPkgName());
        File workingPathFile = new File(workingPath);

        if (workingPathFile.exists() && workingPathFile.isDirectory()) {
            List<File> elispFile = PelpaUtils.getElispFile(workingPath);
            if (elispFile.size() == 1) {
                buildSingleFilePackage(elispFile.get(0), recipeDo);
            }
        }
    }

    public void witeArchiveJSON() {
        File file = new File(PelpaUtils.getHtmlPath() + ARCHIVE_JSON);
        try {
            String json = LocalCache.getArchiveJSON();
            FileUtils.writeStringToFile(file, json);
        } catch (IOException e) {
            LeapLogger.warn("error writeArchiveJson", e);
        }
    }

    public void buildSingleFilePackage(File elispfile, RecipeDo recipeDo) {
        String htmlPath = PelpaUtils.getHtmlPath();
        String version = TimeVersionUtils.toVersionString(elispfile.lastModified());

        String packagePath = htmlPath + "packages/";
        PackageInfo pkgInfo = getPkgInfo(elispfile, recipeDo.getPkgName());
        String readMeFile = packagePath + recipeDo.getPkgName() + "-readme.txt";
        try {
            FileUtils.writeStringToFile(new File(readMeFile), pkgInfo.getReadmeInfo());
            FileUtils.copyFile(elispfile, new File(packagePath + recipeDo.getPkgName() + "-"+ version + ".el"));
        } catch (Exception e) {
            LeapLogger.warn("error in copy file:", e);
        }
        String type = SINGLE;

        ArchiveVo archiveVo = new ArchiveVo();

        archiveVo.setDesc(pkgInfo.getShortInfo());
        archiveVo.setVer(TimeVersionUtils.toArr(elispfile.lastModified()));
        archiveVo.setType(type);
        archiveVo.setKeywords(pkgInfo.getKeywords());
        archiveVo.setDeps(pkgInfo.getDeps());

        LocalCache.update(recipeDo.getPkgName(), archiveVo);
    }

    public PackageInfo getPkgInfo(File elispfile, String pkgName) {
        PackageInfo packageInfo = new PackageInfo();
        BufferedReader br = null;
        StringBuilder stringBuilder = new StringBuilder("");
        try {
            br = new BufferedReader(new FileReader(elispfile));
            String sCurrentLine;
            boolean start = false; boolean end = false;
            int i = 0;
            while ((sCurrentLine = br.readLine()) != null) {
                if (i < 3 && sCurrentLine.contains(pkgName)) {
                    packageInfo.setShortInfo(sCurrentLine.replaceAll(";", "")
                            .replaceAll(pkgName, "").replaceAll(".el", "").replaceAll("-", ""));
                }
                if (sCurrentLine.contains(";;; Commentary:")) {
                    start = true;
                }
                if (sCurrentLine.contains(";;; Code:")) {
                    end = true;
                }

                if (sCurrentLine.trim().startsWith(";") && sCurrentLine.contains("Package-Requires:")) {
                    packageInfo.setDeps(convetDeps(sCurrentLine));
                }

                if (sCurrentLine.trim().startsWith(";") && sCurrentLine.contains("Keywords:")) {
                    packageInfo.setKeywords(convKeywords(sCurrentLine));
                }

                if (start && !end) {
                    stringBuilder.append(sCurrentLine.replaceAll(";", "")).append("\n");
                }
                i ++;
            }
        } catch (IOException e) {
            LeapLogger.error("error", e);
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        packageInfo.setReadmeInfo(stringBuilder.toString());
        return packageInfo;
    }


    private List<DepsItem> convetDeps(String currentline) {
        String[] lineSplit = currentline.split(":");
        if (lineSplit.length > 1) {
            return DepsItem.fromString(lineSplit[1]);
        }

        return null;
    }

    private List<String> convKeywords(String currentline) {
        String[] lineSplit = currentline.split(":");
        if (lineSplit.length > 1) {
            return Arrays.asList(lineSplit[1].split(","));
        }
        return null;
    }

}
