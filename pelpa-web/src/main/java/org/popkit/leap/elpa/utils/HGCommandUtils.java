package org.popkit.leap.elpa.utils;

import org.apache.commons.io.FileUtils;
import org.popkit.core.entity.SimpleResult;
import org.popkit.leap.elpa.entity.RecipeDo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-30:07:20
 */
public class HGCommandUtils {
    public static final String BITBUCKET_HTTPS_ROOT = "https://bitbucket.org/";

    public static void main(String[] args) {
        String pkgName = "evil";
        RecipeDo re = new RecipeDo();
        re.setPkgName(pkgName);
        re.setRepo("lyro/evil");
        SimpleResult simpleResult = hgFetchRemote2Local(re);
        long time = getHGLastModified(PelpaUtils.getWorkingPath(re.getPkgName()));
        System.out.println("simpleResult:" + simpleResult);
        System.out.println("date=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
    }


    public static SimpleResult hgFetchRemote2Local(RecipeDo recipeDo) {
        String url = BITBUCKET_HTTPS_ROOT + recipeDo.getRepo();
        String wokingPath = PelpaUtils.getWorkingPath(recipeDo.getPkgName());
        // hg clone https://bitbucket.org/lyro/evil /Users/aborn/tmp/evil
        // hg pull --cwd /Users/aborn/tmp/evil

        String hgCloneCmd = "hg clone " + url + " " + wokingPath;
        String hgPullCmd = "hg pull --cwd " + wokingPath;
        String command;
        if (existsHgRepo(wokingPath)) {
            command = hgPullCmd;
            if (new File(wokingPath).exists()) {
                try {
                    FileUtils.deleteDirectory(new File(wokingPath));
                } catch (IOException e) {
                    //
                }
            }
        } else {
            command = hgCloneCmd;
        }

        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            if (existsHgRepo(wokingPath)) {
                return SimpleResult.success("hg clone success");
            }
        } catch (InterruptedException e) {
        } catch (IOException ioe) {
        }
        return SimpleResult.fail("error in hgclone for pkgName=" + recipeDo.getPkgName());
    }

    public static boolean existsHgRepo(String wokingPath) {
        File _hg = new File(wokingPath + "/.hg");
        File _hg_woking = new File(wokingPath);
        return _hg_woking.exists() && _hg.exists()
                && _hg.isDirectory() && _hg_woking.isDirectory();
    }

    public static long getHGLastModified(String localPath) {
        String comm = "hg log -l 1 " + localPath;
        try {
            Process p = Runtime.getRuntime().exec(comm);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            String lastModifiedString = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("date:")) {
                    lastModifiedString = line.replaceAll("date:", "").trim();
                }
                System.out.println(line);
            }
            if (null != lastModifiedString) {
                SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
                Date d = format.parse(lastModifiedString);
                System.out.println(d);
                return d.getTime();
            }
        } catch (ParseException pe) {
            return 0;
        } catch (InterruptedException e) {
            return 0;
        } catch (IOException ioe) {
            return 0;
        } finally {
            //
        }
        return 0;
    }
}
