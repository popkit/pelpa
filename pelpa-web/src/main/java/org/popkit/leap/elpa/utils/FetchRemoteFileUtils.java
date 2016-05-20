package org.popkit.leap.elpa.utils;

import org.apache.commons.lang3.StringUtils;
import org.popkit.core.logger.LeapLogger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-20:22:39
 */
public class FetchRemoteFileUtils {

    public static boolean deleteAllFiles(String directory) {
        File file = new File(directory);
        if(file.isDirectory() && file.exists()) {
            String[] files = file.list();
            boolean flag = true;
            String[] arr$ = files;
            int len$ = files.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String item = arr$[i$];
                File tmpFile = new File(directory + File.separator + item);
                if(tmpFile.exists()) {
                    flag = flag && tmpFile.delete();
                }
            }

            return flag;
        } else {
            return true;
        }
    }

    public static boolean createDirectory(String directory) {
        File file = new File(directory);
        if(!file.exists() && !file.isDirectory()) {
            file.mkdirs();
            return true;
        } else {
            return true;
        }
    }

    public static boolean createDirectoryBaseFileName(String path) {
        if(!StringUtils.isEmpty(path) && path.contains(File.separator)) {
            String filePath = path.substring(0, path.lastIndexOf(File.separator));
            return createDirectory(filePath);
        } else {
            return false;
        }
    }

    public static boolean downloadFile(String remoteFilePath, String localFilePath) {
        URL urlfile = null;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File f = new File(localFilePath);
        if(!createDirectoryBaseFileName(localFilePath)) {
            return false;
        } else {
            boolean b;
            try {
                if(f.exists()) {
                    f.delete();
                }

                urlfile = new URL(remoteFilePath);
                httpUrl = (HttpURLConnection)urlfile.openConnection();
                httpUrl.connect();
                bis = new BufferedInputStream(httpUrl.getInputStream());
                bos = new BufferedOutputStream(new FileOutputStream(f));
                short e = 2048;
                byte[] b1 = new byte[e];

                int e2;
                while((e2 = bis.read(b1)) != -1) {
                    bos.write(b1, 0, e2);
                }

                bos.flush();
                bis.close();
                httpUrl.disconnect();
                boolean e1 = f.exists();
                return e1;
            } catch (Exception var19) {
                LeapLogger.warn("## download remote file" + remoteFilePath + " to " + localFilePath + "failed. ");
                b = false;
            } finally {
                try {
                    bis.close();
                    bos.close();
                } catch (IOException var18) {
                    var18.printStackTrace();
                }

            }

            return b;
        }
    }
}
