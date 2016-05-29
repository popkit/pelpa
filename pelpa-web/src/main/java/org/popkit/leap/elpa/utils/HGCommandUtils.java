package org.popkit.leap.elpa.utils;

import org.popkit.core.entity.SimpleResult;
import org.popkit.leap.elpa.entity.RecipeDo;

import java.io.BufferedReader;
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
        long time = getLastModified("/Users/aborn/tmp/evil");
        System.out.println("date=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
    }

    public static SimpleResult hgClone(RecipeDo recipeDo) {
        String url = BITBUCKET_HTTPS_ROOT + recipeDo.getRepo();
        return SimpleResult.fail("go");
    }

    public static long getLastModified(String localPath) {
        String comm = "hg log -l 1 -b .";
        //String comm = "pwd ";
        // Wed May 25 14:48:02 2016 -0400
        String originWoringPath = System.getProperty("user.dir");
        try {
            System.out.println("originWoringPath=" + originWoringPath);
            System.setProperty("user.dir", localPath);
            System.out.println("current=" + System.getProperty("user.dir"));
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
                SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss 2016 Z");
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
            System.setProperty("user.dir", originWoringPath);
        }
        return 0;
    }
}
