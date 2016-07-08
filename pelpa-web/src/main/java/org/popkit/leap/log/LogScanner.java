package org.popkit.leap.log;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.services.LocalCache;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.popkit.leap.log.entity.DownloadCount;
import org.popkit.leap.monitor.utils.BadgeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-15:18:44
 */
@Service
public class LogScanner {

    public static final String STATISTICS_MONTH_FILE = "month.json";
    public static final DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter HOUR_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH");

    public static String getLogFileName() {
        return PelpaUtils.getLogFileName();
        //return "/Users/aborn/github/pelpa/local/melpa.access.log";
    }

    @PostConstruct
    public void generatorLogFile() {
        new Thread(
                new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                TimeUnit.MINUTES.sleep(15);    // generate it every 15 minutes
                            } catch (InterruptedException e) {
                                LeapLogger.warn("write log download_counts thread InterruptedException!");
                                e.printStackTrace();
                            }

                            DownloadCount downloadCount = toJSONString();
                            String log = downloadCount.getJson();
                            if (StringUtils.isNotBlank(log)) {
                                File logFile = new File(PelpaUtils.getHtmlPath() + "download_counts.json");
                                File downloadBadge = new File(PelpaUtils.getHtmlPath() + "packages/" + "download_counts.svg");
                                try {
                                    FileUtils.writeStringToFile(logFile, log);
                                    FileUtils.writeStringToFile(downloadBadge, BadgeUtils.getDownloadCount(downloadCount.getTotal()));
                                    LeapLogger.info("write log to file: " + logFile.getAbsolutePath() + " success!");
                                } catch (IOException ie) {
                                    LeapLogger.warn("write log to file: " + logFile.getAbsolutePath() + " failed!");
                                }
                            } else {
                                LeapLogger.warn("write log to download_counts failed because it empty!");
                            }
                        }
                    }
                }
        ).start();
    }

    public DownloadCount toJSONString() {
        DownloadCount downloadCount = new DownloadCount();
        Map<String, EachLogItem> logItemMap = readLogScanFile();
        JSONObject jsonObject = new JSONObject();
        int total = 0;
        if (MapUtils.isNotEmpty(logItemMap)) {
            for (String item : logItemMap.keySet()) {
                int currentCount = logItemMap.get(item).getCount();
                total += currentCount;
                jsonObject.put(item, currentCount);
                LocalCache.updateDls(item, currentCount);
            }
        }

        downloadCount.setJson(jsonObject.toJSONString());
        downloadCount.setTotal(total);
        return downloadCount;
    }

    public List<EachLogItem> readLogFromStartTime(DateTime startTime) {
        List<EachLogItem> result = new ArrayList<EachLogItem>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(getLogFileName()));
            String sCurrentLine;
            boolean needContinue = true;
            while ((sCurrentLine = br.readLine()) != null && needContinue) {
                if (StringUtils.isBlank(sCurrentLine)) {
                    continue;
                }

                Date date = extraTime(sCurrentLine);
                // 时间是从老到旧,这是一个问题,难道要返过来读!从最后一行到第一行!
                // TODO 优化点
                // if (startTime != null && startTime.isAfter(date.getTime())) {
                // needContinue = false;
                //}

                String pkgName = extraPkgName(sCurrentLine);
                if (date != null && pkgName != null) {
                    result.add(new EachLogItem(pkgName, date));
                }
            }
        } catch (IOException e) {
            LeapLogger.error("error readLogFromStartTime", e);
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static Map<String, EachLogItem> readLogScanFile() {
        Map<String, EachLogItem> result = new HashedMap();

        BufferedReader br = null;
        try {
            String logfileName = getLogFileName();
            if (StringUtils.isBlank(logfileName)) {
                return result;
            }

            br = new BufferedReader(new FileReader(logfileName));
            String sCurrentLine;
            boolean needContinue = true;
            while ((sCurrentLine = br.readLine()) != null && needContinue) {
                if (StringUtils.isBlank(sCurrentLine)) {
                    continue;
                }

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
        } catch (IOException e) {
            LeapLogger.error("error in readLogScanFile", e);
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
