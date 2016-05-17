package org.popkit.leap.log;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.logger.LeapLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-15:18:44
 */
public class LogScanner {

    public static String getLogFileName() {
        // return PelpaUtils.getLogFileName();
        return "/Users/aborn/github/pelpa/local/melpa.access.log";
    }

    public static Map<String, EachLogItem> readLogScanFile() {
        Map<String, EachLogItem> result = new HashedMap();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(getLogFileName()));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                if (StringUtils.isNotBlank(sCurrentLine)) {
                    Date date = extraTime(sCurrentLine);
                    String pkgName = extraPkgName(sCurrentLine);
                    if (date != null && pkgName != null) {
                        if (result.containsKey(pkgName)) {
                            result.get(pkgName).countIt();
                        } else {
                            EachLogItem eachLogItem = new EachLogItem(pkgName, date);
                            result.put(pkgName, eachLogItem);
                        }
                    }
                }
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
        return result;
    }

    public static Date extraTime(String sCurrentLine) {
        if (StringUtils.isNoneBlank(sCurrentLine)) {
            String timeSub = sCurrentLine.substring(sCurrentLine.indexOf("[") + 1, sCurrentLine.indexOf("]"));
            try {
                Date date = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z").parse(timeSub);
                return date;
            } catch (ParseException pe) {
                return null;
            }
        }
        return null;
    }

    public static String extraPkgName(String sCurrentLine) {
        if (StringUtils.isBlank(sCurrentLine)) {
            return null;
        }

        Pattern pattern = Pattern.compile("GET /packages/*[^ ]+-[0-9.]+.(?:el|tar)");
        Matcher matcher = pattern.matcher(sCurrentLine);

        while (matcher.find()) {
            String pkgFull = matcher.group().split("/packages/")[1];
            return pkgFull.substring(0, pkgFull.lastIndexOf("-"));
        }

        return null;
    }

    public static void main(String[] args) {
        String testLine = " 211.161.247.234 - - [14/May/2016:06:31:27 +0800] \"GET /packages/ztree-badge.svg HTTP/1.1\" 404 193 \"https://elpa.popkit.org/\" \"Mozill     a/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36\"";
        String testLinb = "211.161.247.234 - - [14/May/2016:06:33:33 +0800] \"GET /packages/2048-game-20151027.333.el HTTP/1.1\" 200 21282 \"https://elpa.popkit.o     rg/\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36\"";
        String testC = "211.161.247.234 - - [14/May/2016:06:34:30 +0800] \"GET /packages/ztree-20150703.113.tar HTTP/1.1\" 206 74490 \"https://elpa.popkit.org/     \" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36\"";

        System.out.println("a=" + extraPkgName(testLine));
        System.out.println("b=" + extraPkgName(testLinb));
        System.out.println("c=" + extraPkgName(testC));
        System.out.println("" + extraTime(testLine));

        Map<String, EachLogItem> logItemMap = readLogScanFile();
        for (String pkgName : logItemMap.keySet()) {
            System.out.println("pkg:" + pkgName + ", " + logItemMap.get(pkgName).getCount());
        }
    }
}
