package org.popkit.leap.elpa.services;

import org.apache.commons.io.FileUtils;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;
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

    public static void tar(List<File> fileList, String tmpTarWorking, String destTar) throws IOException {
        File tmpTarWorkingDir = new File(tmpTarWorking);
        if (tmpTarWorkingDir.exists()) {
            FileUtils.deleteDirectory(tmpTarWorkingDir);
        }
        tmpTarWorkingDir.mkdir();

        for (File item : fileList) {
            if (item.isFile()) {
                FileUtils.copyFileToDirectory(item, tmpTarWorkingDir);
            } else {
                FileUtils.copyDirectoryToDirectory(item, tmpTarWorkingDir);
            }
        }

        // begin do tar action
        FileOutputStream dest = new FileOutputStream(destTar);
        // Create a TarOutputStream
        TarOutputStream out = new TarOutputStream( new BufferedOutputStream( dest ) );
        tarFolder(null, tmpTarWorking, out);
        out.close();
    }

    public static void tar(String pkgName, RecipeDo recipeDo, List<File> elispFileList, long lastcommit)
            throws FileNotFoundException, IOException {

        String htmlPath = PelpaUtils.getHtmlPath();
        String packagePath = htmlPath + "packages/";
        String pkgWorkingPath = PelpaUtils.getWorkingPath(pkgName);

        String version = TimeVersionUtils.toVersionString(lastcommit);
        String destTar = packagePath + recipeDo.getPkgName() + "-"+ version + ".tar";
        // if final package tar file exists, do not need to build it!
        File desTarFile = new File(destTar);
        if (desTarFile.exists()) {
            desTarFile.delete();   // return @TODO , 后期记得直接返回
            //return;
        }

        String tmpTarWorking = pkgWorkingPath + "/" + recipeDo.getPkgName() + "-"+ version + "";
        File tmpTarWorkingFile = new File(tmpTarWorking);
        if (tmpTarWorkingFile.exists() && tmpTarWorkingFile.isDirectory()) {
            FileUtils.deleteDirectory(tmpTarWorkingFile);
        }

        List<File> fileList = new ArrayList<File>();
        File pkgElispFile = new File(PelpaUtils.getPkgElispFileName(recipeDo.getPkgName()));
        if (pkgElispFile.exists()) {
            fileList.add(pkgElispFile);
        }

        fileList.addAll(elispFileList);
        tar(fileList, tmpTarWorking, destTar);
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
