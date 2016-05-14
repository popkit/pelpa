package org.popkit.leap.elpa.services;

import org.popkit.core.logger.LeapLogger;
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

    public void buildSingleFilePackage(File elispfile, RecipeDo recipeDo) {
        String htmlPath = PelpaUtils.getHtmlPath();
        String version = TimeVersionUtils.toVersionString(elispfile.lastModified());
        PackageInfo desc = getDesc(elispfile, recipeDo.getPkgName());
        String type = SINGLE;
    }


    public PackageInfo getDesc(File elispfile, String pkgName) {
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
                if (start && !end) {
                    stringBuilder.append(sCurrentLine.replaceAll(";", ""));
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

}
