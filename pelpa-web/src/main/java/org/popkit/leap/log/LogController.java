package org.popkit.leap.log;

import com.alibaba.fastjson.JSON;
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
    public static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    @Autowired
    private LogScanner logScanner;

    @RequestMapping("ajaxmonthss.json")
    public void ajaxmonthss(HttpServletResponse response) {
        DateTime now = new DateTime();
        DateTime startTime = now.minusDays(30).withTimeAtStartOfDay();
        DateTime endTime = now.withTimeAtStartOfDay();

        List<String> labels = new ArrayList<String>();
        List<Integer> data = new ArrayList<Integer>();

        File logStatisticsFile = new File(PelpaUtils.getStaticsPath() + LogScanner.STATISTICS_MONTH_FILE);

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
        }

        EachLine eachLine = new EachLine(labels, data);
        eachLine.setLabel(startTime.toString(LogScanner.DAY_FORMAT) + "~" + endTime.minusDays(1).toString(LogScanner.DAY_FORMAT));
        ResponseUtils.renderJson(response, eachLine.toString());
    }

    @PostConstruct
    public void init() {
        DateTime now = new DateTime();
        DateTime tomorrowFirstTime = now.plusDays(1).withTimeAtStartOfDay().plusMinutes(3);
        Minutes delayMinutes = Minutes.minutesBetween(now, tomorrowFirstTime);

        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                //LeapLogger.info("exectud!!" + new DateTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss")));
            }
        }, 0, 5, TimeUnit.MINUTES);   // execute each 5 minutes.

        // init executed it and periodic each day executed
        generateLatestStaticsJSON();
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                generateLatestStaticsJSON();
            }
        }, delayMinutes.getMinutes(), 24*60, TimeUnit.MINUTES);   // execute each day
    }

    public void generateLatestStaticsJSON() {
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
