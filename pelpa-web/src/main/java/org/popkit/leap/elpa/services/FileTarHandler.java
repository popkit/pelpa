package org.popkit.leap.elpa.services;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;
import org.popkit.core.config.LeapConfigLoader;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ArchiveVo;
import org.popkit.leap.elpa.entity.PackageInfo;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.popkit.leap.elpa.utils.TimeVersionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-16:17:20
 */
public class FileTarHandler {
    private static final int BUFFER = 2048;

    private static final String TEST_WORKING = "/Users/aborn/github/pelpa/working/";
    private static final String TEST_DES_DIR = "/Users/aborn/github/popkit-elpa/html/packages/";

    public static void tar(String pkgName, List<File> fileList, String tmpTarWorking, String destTar) throws IOException {
        String wokingDir = PelpaUtils.getWorkingPath(pkgName);
        File tmpTarWorkingDir = new File(tmpTarWorking);

        try {
            for (File item : fileList) {
                String fileFullPath = item.getAbsolutePath();
                String targetRelative = fileFullPath.substring(wokingDir.length() + 1);
                File targetDir = tmpTarWorkingDir;

                // 默认 *.el文件放在最上层
                if (targetRelative.contains("/") && (!item.getName().endsWith(".el"))) {
                    String targetRelativePath = targetRelative.substring(0, targetRelative.lastIndexOf("/"));
                    targetDir = new File(tmpTarWorkingDir + "/" + targetRelativePath);
                    if (!targetDir.exists()) {
                        targetDir.mkdirs();
                    }
                }

                if (item.isFile()) {
                    FileUtils.copyFileToDirectory(item, targetDir);
                } else {
                    FileUtils.copyDirectoryToDirectory(item, targetDir);
                }
            }

            // begin do tar action
            FileOutputStream dest = new FileOutputStream(destTar);
            // Create a TarOutputStream
            TarOutputStream out = new TarOutputStream(new BufferedOutputStream(dest));
            tarFolder(null, tmpTarWorking, out);
            out.close();
        } catch (Exception e) {
            LeapLogger.warn("", e);
        }
    }

    public static void tar(String pkgName, RecipeDo recipeDo, List<File> elispFileList,
                           long lastcommit, ArchiveVo archiveVo, String repoUrl, PackageInfo pkgInfo)
            throws FileNotFoundException, IOException {

        String htmlPath = PelpaUtils.getHtmlPath();
        String packagePath = htmlPath + "packages/";
        String pkgWorkingPath = PelpaUtils.getWorkingPath(pkgName);
        File originPkgFile = new File(pkgWorkingPath + File.separator + pkgName + "-pkg.el");

        String version = TimeVersionUtils.toVersionString(lastcommit);
        String destTar = packagePath + recipeDo.getPkgName() + "-"+ version + ".tar";
        // if final package tar file exists, do not need to build it!
        File desTarFile = new File(destTar);
        if (desTarFile.exists()) {
            int hour = new DateTime().getHourOfDay();
            // 在每天的闲时,即[2, 6], 采用删除老的策略
            if ((hour > 1 && hour < 7) || "true".equals(LeapConfigLoader.get("elpa_delete_old_pkg"))) {
                desTarFile.delete();
            } else {
                return;
            }
        }

        String tmpTarWorking = pkgWorkingPath + "/" + recipeDo.getPkgName() + "-"+ version + "";
        File tmpTarWorkingFile = new File(tmpTarWorking);
        if (tmpTarWorkingFile.exists() && tmpTarWorkingFile.isDirectory()) {
            FileUtils.deleteDirectory(tmpTarWorkingFile);
        }
        tmpTarWorkingFile.mkdir();

        File destPkgDescFile = new File(tmpTarWorking + File.separator + recipeDo.getPkgName() + "-pkg.el");
        boolean pkgStatus = PelpaUtils.generatePkgElispFileContent(recipeDo.getPkgName(),
                TimeVersionUtils.toVersionString(lastcommit), archiveVo.getDesc(),
                archiveVo.getProps().getKeywords(),
                pkgInfo.getDeps(), repoUrl, destPkgDescFile, originPkgFile);

        if (!destPkgDescFile.exists()) {
            return;
        }

        List<File> fileList = new ArrayList<File>();
        for (File item : elispFileList) {
            if (!item.getName().equals(destPkgDescFile.getName())) {
                fileList.add(item);
            }
        }

        if (pkgStatus) {
            tar(pkgName, fileList, tmpTarWorking, destTar);
        }
    }

    public static void main(String[] args) {

        String reg = "^*.el";
        String fileName = "abc.el";
        System.out.println(fileName.matches(reg));
    }

    public static void tarFolder(String parent, String path, TarOutputStream out) throws IOException {
        BufferedInputStream origin = null;
        File f = new File(path);
        String files[] = f.list();

        // is file
        if (files == null) {
            files = new String[1];
            files[0] = f.getName();
        }

        parent = ((parent == null) ? (f.isFile()) ? "" : f.getName() + "/" : parent + f.getName() + "/");

        for (int i = 0; i < files.length; i++) {
            System.out.println("Adding: " + files[i]);
            File fe = f;
            byte data[] = new byte[BUFFER];

            if (f.isDirectory()) {
                fe = new File(f, files[i]);
            }

            if (fe.isDirectory()) {
                String[] fl = fe.list();
                if (fl != null && fl.length != 0) {
                    tarFolder(parent, fe.getPath(), out);
                } else {
                    TarEntry entry = new TarEntry(fe, parent + files[i] + "/");
                    out.putNextEntry(entry);
                }
                continue;
            }

            FileInputStream fi = new FileInputStream(fe);
            origin = new BufferedInputStream(fi);
            TarEntry entry = new TarEntry(fe, parent + files[i]);
            out.putNextEntry(entry);

            int count;

            while ((count = origin.read(data)) != -1) {
                out.write(data, 0, count);
            }

            out.flush();

            origin.close();
        }
    }
}
