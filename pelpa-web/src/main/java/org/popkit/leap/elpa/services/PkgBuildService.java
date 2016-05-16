package org.popkit.leap.elpa.services;

import org.apache.commons.io.FileUtils;
import org.popkit.core.entity.SimpleResult;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.*;
import org.popkit.leap.elpa.services.handler.GithubFetchHandler;
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
    private static final String TYPE_SINGLE = "single";
    private static final String TYPE_TAR = "tar";

    @Autowired
    private RecipesService recipesService;

    public SimpleResult buildPackage(String pkgName) {
        return buildPackage(recipesService.getRecipeDo(pkgName));
    }

    public SimpleResult buildPackage(RecipeDo recipeDo) {
        if (recipeDo == null) {
            return SimpleResult.fail("传入的recipeDo为null!");
        }

        String workingPath = PelpaUtils.getWorkingPath(recipeDo.getPkgName());
        File workingPathFile = new File(workingPath);

        if (workingPathFile.exists() && workingPathFile.isDirectory()) {
            File singleFile = getSingleFile(recipeDo, workingPath);
            if (singleFile != null) {
                LeapLogger.info("#single#" + recipeDo.getPkgName() + "  file:" + singleFile.getAbsolutePath());
                buildSingleFilePackage(recipeDo, singleFile);
            } else {
                buildMultiFilesPackage(recipeDo, PelpaUtils.getElispFile(workingPath));
            }
        }

        // update archive.json when each package build success
        writeArchiveJSON();
        return SimpleResult.success("成功,pkgName=" + recipeDo.getPkgName());
    }

    public File getSingleFile(RecipeDo recipeDo, String workingPath) {
        try {
            List<String> recipeDoFiles = recipeDo.getFileList();
            String recipeFile = null;

            if (recipeDoFiles.size() > 1) {
                return null;
            } else if (recipeDoFiles.size() == 1) {
                recipeFile = recipeDoFiles.get(0);
            }

            List<File> elispFiles = PelpaUtils.getElispFile(workingPath);
            if (elispFiles.size() == 1) {
                return elispFiles.get(0);
            } else {
                if (null != recipeFile) {
                    for (File file : elispFiles) {
                        if (recipeFile.equals(file.getName())) {
                            return file;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LeapLogger.warn("exception in getSingleFile", e);
        }
        return null;   // multi files
    }

    public void writeArchiveJSON() {
        File file = new File(PelpaUtils.getHtmlPath() + PelpaContents.ARCHIVE_JSON_FILE_NAME);
        try {
            String json = LocalCache.getArchiveJSON();
            FileUtils.writeStringToFile(file, json);
        } catch (IOException e) {
            LeapLogger.warn("error writeArchiveJson", e);
        }
    }

    public void buildMultiFilesPackage(RecipeDo recipeDo, List<File> elispFile) {
        ArchiveVo archiveVo = new ArchiveVo();
        archiveVo.setType(TYPE_TAR);
    }

    public void buildSingleFilePackage(RecipeDo recipeDo, File elispfile) {
        String htmlPath = PelpaUtils.getHtmlPath();
        long lastcommit = GithubFetchHandler.getLastCommiterTime(recipeDo.getPkgName());
        lastcommit = lastcommit == 0 ? elispfile.lastModified() : lastcommit;
        String version = TimeVersionUtils.toVersionString(lastcommit);
        LeapLogger.info("pkg:" + recipeDo.getPkgName() + ", 版本号:" + version);

        String packagePath = htmlPath + "packages/";
        PackageInfo pkgInfo = getPkgInfo(elispfile, recipeDo.getPkgName());
        String readMeFile = packagePath + recipeDo.getPkgName() + "-readme.txt";
        try {
            FileUtils.writeStringToFile(new File(readMeFile), pkgInfo.getReadmeInfo());
            FileUtils.copyFile(elispfile, new File(packagePath + recipeDo.getPkgName() + "-"+ version + ".el"));
        } catch (Exception e) {
            LeapLogger.warn("error in copy file:", e);
        }

        ArchiveVo archiveVo = new ArchiveVo();
        archiveVo.setDesc(pkgInfo.getShortInfo());
        archiveVo.setVer(TimeVersionUtils.toArr(lastcommit));
        archiveVo.setType(TYPE_SINGLE);
        archiveVo.setKeywords(pkgInfo.getKeywords());
        archiveVo.setDeps(pkgInfo.getDeps());
        if (FetcherEnum.getFetcher(recipeDo.getFetcher()) == FetcherEnum.GITHUB) {
            archiveVo.setPropsUrl(GithubFetchHandler.GITHUB_HTTPS_ROOT + recipeDo.getRepo());
        }

        LocalCache.updateArchive(recipeDo.getPkgName(), archiveVo);
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


    private static List<DepsItem> convetDeps(String currentline) {
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


    public static void main(String[] args) {
        String test = ";; Package-Requires: ((emacs \"24.4\") (cl-lib \"0.5\")(avy \"0.3.0\"))";
        List<DepsItem> depsItems = convetDeps(test);
        System.out.println(depsItems);
    }

}
