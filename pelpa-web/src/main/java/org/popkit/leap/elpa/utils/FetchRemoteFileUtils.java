package org.popkit.leap.elpa.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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

    public static boolean downloadRemoteFile(String remoteFilePath, String localFilePath) {
        File f = new File(localFilePath);
        if(!createDirectoryBaseFileName(localFilePath)) {
            return false;
        } else {
                f.deleteOnExit();
        }

        int socketTimeout = 5000;  // 请求超时
        int connectTimeout = 10000; // 链接超时

        // 设置请求参数
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        HttpGet httpGet = new HttpGet(remoteFilePath);
        httpGet.setConfig(requestConfig);

        // httclient及数据设置
        CloseableHttpClient httpclient = HttpClients.createDefault();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            LeapLogger.info("#getJSON#" + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity responseEntity = response.getEntity();
                bis = new BufferedInputStream(responseEntity.getContent());
                bos = new BufferedOutputStream(new FileOutputStream(f));
                byte[] b1 = new byte[2048];
                int e2;
                while((e2 = bis.read(b1)) != -1) {
                    bos.write(b1, 0, e2);
                }

                EntityUtils.consume(responseEntity);
                return f.exists();
            } else {
                return false;
            }
        } catch (Exception e) {
            LeapLogger.warn("#getJSON# exception" + remoteFilePath);
        } finally {
            try {
                if (bis != null){
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
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
                f.deleteOnExit();

                urlfile = new URL(remoteFilePath);
                httpUrl = (HttpURLConnection)urlfile.openConnection();
                httpUrl.setConnectTimeout(5000);


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
                    if (bis != null) {
                        bis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                } catch (IOException var18) {
                    var18.printStackTrace();
                }

            }

            return b;
        }
    }
}