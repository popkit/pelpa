package org.popkit.leap.log;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.popkit.core.utils.ResponseUtils;
import org.popkit.leap.common.BaseController;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.popkit.leap.log.entity.EachLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-06-04:18:24
 */
@Controller
@RequestMapping("elpa/log")
public class LogController extends BaseController {

    @Autowired
    private LogScanner logScanner;

    @RequestMapping("ajaxmonthss.json")
    public void ajaxmonthss(HttpServletResponse response) {
        DateTime now = new DateTime();
        DateTime startTime = now.minusDays(30).withTimeAtStartOfDay();
        DateTime endTime = now.plusDays(1).withTimeAtStartOfDay();

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
        eachLine.setLabel(startTime.toString(LogScanner.DAY_FORMAT) + "~" + endTime.toString(LogScanner.DAY_FORMAT));
        ResponseUtils.renderJson(response, eachLine.toString());
    }

    public void generateLatestStaticsJSON() {
        DateTime now = new DateTime();
        DateTime startTime = now.minusDays(30).withTimeAtStartOfDay();
        DateTime endTime = now.plusDays(1).withTimeAtStartOfDay();
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
