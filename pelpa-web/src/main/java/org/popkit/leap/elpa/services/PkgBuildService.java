package org.popkit.leap.elpa.services;

import org.apache.commons.io.FileUtils;
import org.popkit.core.entity.SimpleResult;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.*;
import org.popkit.leap.elpa.services.handler.GitFetchHandler;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.popkit.leap.elpa.utils.RecipeParser;
import org.popkit.leap.elpa.utils.TimeVersionUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 将下载下来的package构建成最后需要的package
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:11:10
 */
@Service
public class PkgBuildService {
    private static final String TYPE_SINGLE = "single";
    private static final String TYPE_TAR = "tar";

    public SimpleResult buildPackage(String pkgName) {
        return buildPackage(LocalCache.getRecipeDo(pkgName));
    }

    public SimpleResult buildPackage(RecipeDo recipeDo) {
        try {
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
                    buildMultiFilesPackage(recipeDo);
                }
            }

            return SimpleResult.success("成功,pkgName=" + recipeDo.getPkgName());
        } catch (Exception e) {
            LeapLogger.warn("buildPackage@@@" + recipeDo.getPkgName(), e);
            return SimpleResult.success("");
        }
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
                    File singleFile = new File(workingPath + File.separator + recipeFile);
                    if (singleFile.exists()) {
                        return singleFile;
                    } else {   // 说明是通配符

                        return null;
                    }
                }
            }
        } catch (Exception e) {
            LeapLogger.warn("exception in getSingleFile", e);
        }

        return null;   // multi files
    }

    public void buildMultiFilesPackage(RecipeDo recipeDo) {
        List<File> elispFileList = PelpaUtils.getFileListBasedRecipe(recipeDo);
        try {
            File pkgFile = null;    // PACKAGE-NAME.el
            File pkgPkgFile = null; // PACKAGE-NAME-pkg.el

            for (File file : elispFileList) {
                if ((recipeDo.getPkgName() + ".el").endsWith(file.getName())) {
                    pkgFile = file;
                    break;
                }
                if (file.getName().equals(recipeDo.getPkgName() + "-pkg.el")) {
                    pkgPkgFile = file;
                }
            }

            // TODO 当没有 PACKAGE-NAME.el时,可采用PACKAGE-NAME-pkg.el提取信息,如tao-theme
            if (pkgFile != null || pkgPkgFile != null) {
                PackageInfo pkgInfo = null;
                if (pkgFile != null) {
                    pkgInfo = getPkgInfo(pkgFile, recipeDo.getPkgName());
                    // both exists!
                    if (pkgPkgFile != null) {
                        PackageInfo pkgPkgInfo = getPkgInfoBasePkgFile(pkgPkgFile, recipeDo.getPkgName());
                        pkgInfo.setDepsIfAbsent(pkgPkgInfo.getDeps());
                        pkgInfo.setKeywordsIfAbsent(pkgPkgInfo.getKeywords());
                        pkgInfo.setReadmeInfoIfAbsent(pkgPkgInfo.getReadmeInfo());
                        pkgInfo.setShortInfoIfAbsent(pkgPkgInfo.getShortInfo());
                    }
                } else if (pkgPkgFile != null) {
                    pkgInfo = getPkgInfoBasePkgFile(pkgPkgFile, recipeDo.getPkgName());
                }
                if (pkgInfo == null) { return; }

                ArchiveVo archiveVo = new ArchiveVo();
                archiveVo.setDesc(pkgInfo.getShortInfo());

                long lastcommit;
                if (recipeDo.getLastCommit() > 0) {
                    lastcommit = recipeDo.getLastCommit();
                } else {
                    lastcommit = GitFetchHandler.getLastCommiterTime(recipeDo.getPkgName());
                }

                lastcommit = lastcommit == 0 ? pkgFile.lastModified() : lastcommit;
                archiveVo.setVer(TimeVersionUtils.toArr(lastcommit));
                archiveVo.setType(TYPE_TAR);
                archiveVo.setKeywords(pkgInfo.getKeywords());
                archiveVo.setDeps(pkgInfo.getDeps());
                String repoUrl = recipeDo.getUrl();
                if (FetcherEnum.getFetcher(recipeDo.getFetcher()) == FetcherEnum.GITHUB) {
                    repoUrl = GitFetchHandler.GITHUB_HTTPS_ROOT + recipeDo.getRepo();
                }
                archiveVo.setPropsUrl(repoUrl);

                FileTarHandler.tar(recipeDo.getPkgName(), recipeDo, elispFileList,
                        lastcommit, archiveVo, repoUrl, pkgInfo);
                LocalCache.updateArchive(recipeDo.getPkgName(), archiveVo);
            }
        } catch (Exception e) {
            LeapLogger.warn("generate tar file failed, pkgName:" + recipeDo.getPkgName(), e);
        }

    }

    public void buildSingleFilePackage(RecipeDo recipeDo, File elispfile) {
        String htmlPath = PelpaUtils.getHtmlPath();
        long lastcommit = 0;
        if (recipeDo.getLastCommit() > 0) {
            lastcommit = recipeDo.getLastCommit();
        } else {
            GitFetchHandler.getLastCommiterTime(recipeDo.getPkgName());
            lastcommit = lastcommit == 0 ? elispfile.lastModified() : lastcommit;
        }

        String version = TimeVersionUtils.toVersionString(lastcommit);
        LeapLogger.info("pkg:" + recipeDo.getPkgName() + ", 版本号:" + version);

        String packagePath = htmlPath + "packages/";
        PackageInfo pkgInfo = getPkgInfo(elispfile, recipeDo.getPkgName());
        String readMeFile = packagePath + recipeDo.getPkgName() + "-readme.txt";
        try {
            File finalPkgFile = new File(packagePath + recipeDo.getPkgName() + "-"+ version + ".el");
            // if this version package already exists, do not copy it!
            if (!finalPkgFile.exists()) {
                FileUtils.writeStringToFile(new File(readMeFile), pkgInfo.getReadmeInfo());
                FileUtils.copyFile(elispfile, finalPkgFile);
            }
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
            archiveVo.setPropsUrl(GitFetchHandler.GITHUB_HTTPS_ROOT + recipeDo.getRepo());
        }

        LocalCache.updateArchive(recipeDo.getPkgName(), archiveVo);
    }

    public PackageInfo getPkgInfoBasePkgFile(File pkgFile, String pkgName) {
        try {
            String fileContent = FileUtils.readFileToString(pkgFile);
            String content = RecipeParser.extraPairContent(fileContent).trim();
            List<Integer> indexList = new ArrayList<Integer>();
            int isInQuteCount = 0;    // 0 is Ok
            int index = -1;
            indexList.add(0);
            int rightBracket = -1;
            boolean isInBracket = false;
            for (char c : content.toCharArray()) {
                index ++;
                if ('(' == c && (!isInBracket)) {
                    isInBracket = true;
                    rightBracket = RecipeParser.findAnotherBracket(index, content);
                }

                if (index == rightBracket) {
                    isInBracket = false;
                }

                if (index <= rightBracket) {
                    continue;
                }

                if (Character.isWhitespace(c) && isInQuteCount == 0) {
                    if (index == 0) {
                        indexList.add(index);
                        continue;
                    }
                    if (!Character.isWhitespace(content.charAt(index - 1))) {
                        indexList.add(index);
                    } else {
                        continue;
                    }
                }

                if ('"' == c) {
                    if (isInQuteCount % 2 == 0) {
                        isInQuteCount ++;
                    } else {
                        isInQuteCount --;
                    }
                }
            }

            indexList.add(content.length());
            List<String> result = new ArrayList<String>();
            for (int i=0; i < indexList.size() - 1; i++) {
                result.add(content.substring(indexList.get(i), indexList.get(i+1)).trim());
            }

            PackageInfo packageInfo = new PackageInfo();
            if (result.size() >= 3) {
                packageInfo.setShortInfo(PelpaUtils.unwrap(result.get(3)));
                packageInfo.setReadmeInfo(PelpaUtils.unwrap(result.get(3)));
            }

            if (result.size() > 4) {
                String currentline = result.get(4).replace("'", ":");
                packageInfo.setDeps(convetDeps(currentline));
            }

            for (int i=0; i<(result.size() - 1); i++) {
                if (":keywords".equals(result.get(i))
                        && result.get(i+1).contains("'")
                        && result.get(i+1).contains("(")
                        && result.get(i+1).contains(")")) {
                    packageInfo.setKeywords(Arrays.asList(result.get(i+1).replace("(", "").replace(")", "").replace("'", "").replace("\"", "").split("\\s+")));
                }
            }

            return packageInfo;
        } catch (Exception e) {
            LeapLogger.warn("", e);
        }

        return null;
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
            boolean shortInfoFinished = false;
            while ((sCurrentLine = br.readLine()) != null) {
                if (i < 3 && sCurrentLine.contains(pkgName) && (!shortInfoFinished)) {
                    packageInfo.setShortInfo(sCurrentLine.replaceAll(";", "")
                            .replaceAll(pkgName, "").replaceAll(".el", "").replaceAll("-", "")
                            .trim());
                    shortInfoFinished = true;
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
