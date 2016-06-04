package org.popkit.leap.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.popkit.core.logger.LeapLogger;
import org.popkit.core.utils.ResponseUtils;
import org.popkit.leap.common.BaseController;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.popkit.leap.log.entity.EachLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-06-04:18:24
 */
@Controller
@RequestMapping("elpa/log")
public class LogController extends BaseController {
    public static final String STATISTICS_DAY_FILE = "day.json";
    public static final ScheduledExecutorService todayScheduled = Executors.newScheduledThreadPool(1);
    public static final ScheduledExecutorService monthScheduled = Executors.newScheduledThreadPool(1);

    @Autowired
    private LogScanner logScanner;

    @RequestMapping("ajaxmonthss.json")
    public void ajaxmonthss(HttpServletResponse response,
                            @RequestParam(value = "type", defaultValue = "month") String type) {

        DateTime now = new DateTime();
        DateTime startTime = now.minusDays(30).withTimeAtStartOfDay();
        DateTime endTime = now.withTimeAtStartOfDay();
        JSONObject jsonResult = new JSONObject();

        for (String item : new String[]{"month", "today"}) {

            List<String> labels = new ArrayList<String>();
            List<Integer> data = new ArrayList<Integer>();
            String fileName = "month".equals(item) ? LogScanner.STATISTICS_MONTH_FILE : STATISTICS_DAY_FILE;
            File logStatisticsFile = new File(PelpaUtils.getStaticsPath() + fileName);

            try {
                Map<String, Integer> statisticsMap = JSON.parseObject(FileUtils.readFileToString(logStatisticsFile), Map.class);
                List<String> keyList = new ArrayList<String>();

                for (String key : statisticsMap.keySet()) {
                    keyList.add(key);
                }
                Collections.sort(keyList);

                for (String key : keyList) {
                    labels.add(key);
                    data.add(statisticsMap.get(key));
                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            EachLine eachLine = new EachLine(labels, data);
            eachLine.setLabel(startTime.toString(LogScanner.DAY_FORMAT) + "~" + endTime.minusDays(1).toString(LogScanner.DAY_FORMAT));
            jsonResult.put(item, eachLine);
        }

        ResponseUtils.renderJson(response, jsonResult.toString());
    }

    @PostConstruct
    public void init() {
        DateTime now = new DateTime();
        DateTime tomorrowFirstTime = now.plusDays(1).withTimeAtStartOfDay().plusMinutes(3);
        Minutes delayMinutes = Minutes.minutesBetween(now, tomorrowFirstTime);

        generateTodayStaticsJSON();
        todayScheduled.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                generateTodayStaticsJSON();
            }
        }, delayMinutes.getMinutes(), 5, TimeUnit.MINUTES);   // execute each 5 minutes.

        // init executed it and periodic each day executed
        generateLatestMonthStaticsJSON();
        monthScheduled.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                generateLatestMonthStaticsJSON();
            }
        }, delayMinutes.getMinutes(), 24*60, TimeUnit.MINUTES);   // execute each day
    }

    // today statistics
    public void generateTodayStaticsJSON() {
        LeapLogger.info("#generateTodayStaticsJSON# executed!");
        DateTime now = new DateTime();
        DateTime startTime = now.withTimeAtStartOfDay();
        DateTime endTime = now.plusDays(1).withTimeAtStartOfDay();
        DateTime tmp = startTime;
        List<DateTime> timeList = new ArrayList<DateTime>();
        while (tmp.isBefore(endTime)) {
            timeList.add(tmp);
            tmp = tmp.plusHours(1);
        }

        Map<String, Integer> statisticsMap = new HashMap<String, Integer>();
        for (DateTime item : timeList) {
            statisticsMap.put(item.toString(LogScanner.HOUR_FORMAT), 0);
        }
        List<EachLogItem> logItemList = logScanner.readLogFromStartTime(startTime);
        if (CollectionUtils.isEmpty(logItemList)) {
            return;
        }

        // generate ...
        for (EachLogItem logItem : logItemList) {
            String  dayKey = new DateTime(logItem.getDate()).toString(LogScanner.HOUR_FORMAT);
            if (statisticsMap.containsKey(dayKey)) {
                statisticsMap.put(dayKey, statisticsMap.get(dayKey) + 1);
            }
        }

        // write to json file
        try {
            File logStatisticsFile = new File(PelpaUtils.getStaticsPath() + STATISTICS_DAY_FILE);
            FileUtils.writeStringToFile(logStatisticsFile,
                    JSON.toJSONString(statisticsMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateLatestMonthStaticsJSON() {
        LeapLogger.info("#generateLatestStaticsJSON# executed!");
        DateTime now = new DateTime();
        DateTime startTime = now.minusDays(30).withTimeAtStartOfDay();
        DateTime endTime = now.withTimeAtStartOfDay();
        DateTime tmp = startTime;
        List<DateTime> timeList = new ArrayList<DateTime>();
        while (tmp.isBefore(endTime)) {
            timeList.add(tmp);
            tmp = tmp.plusDays(1);
        }

        Map<String, Integer> statisticsMap = new HashMap<String, Integer>();
        for (DateTime item : timeList) {
            statisticsMap.put(item.toString(LogScanner.DAY_FORMAT), 0);
        }
        List<EachLogItem> logItemList = logScanner.readLogFromStartTime(startTime);
        if (CollectionUtils.isEmpty(logItemList)) {
            return;
        }

        // generate ...
        for (EachLogItem logItem : logItemList) {
            String  dayKey = new DateTime(logItem.getDate()).toString(LogScanner.DAY_FORMAT);
            if (statisticsMap.containsKey(dayKey)) {
                statisticsMap.put(dayKey, statisticsMap.get(dayKey) + 1);
            }
        }

        // write to json file
        try {
            File logStatisticsFile = new File(PelpaUtils.getStaticsPath() + LogScanner.STATISTICS_MONTH_FILE);
            FileUtils.writeStringToFile(logStatisticsFile,
                    JSON.toJSONString(statisticsMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
