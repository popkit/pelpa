package org.popkit.leap.monitor;

import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.RoundRun;
import org.popkit.leap.elpa.entity.RoundStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:21:21
 */
@Service
public class RoundSupervisor {

    @Autowired
    private RoundMonitor monitor;

    @Autowired
    private FetcherExcutorPool fetcherExcutorPool;

    @Autowired
    private BuildingExcutorPool buildingExcutorPool;

    private static final long REST_TIME = 2*60*60*1000;    // ms
    private static volatile RoundRun run = new RoundRun();

    @PostConstruct
    public void init() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        LeapLogger.warn("InterruptedException in init in roundsupervisor", e);
                        return;
                    }

                    if (run == null || run.getStartTime() == null
                            || (RoundStatus.FINISHED == run.getStatus()
                            && run.getEndTime().getTime() + REST_TIME < new Date().getTime())) {
                        nextRun();    // 新一轮构建开始
                    }

                    if (RoundMonitor.isFinishedThisRun() && run.getEndTime() != null) {
                        run.setEndTime(new Date());
                        run.setStatus(RoundStatus.FINISHED);
                        LeapLogger.info("roundId:" + run.getRoundId() + "完成!");
                    }

                    if (run.getEndTime() != null && run.getStatus() == RoundStatus.FINISHED) {
                        if (run.getEndTime().getTime() + REST_TIME > new Date().getTime()) {
                            long next = ((run.getEndTime().getTime() + REST_TIME) - new Date().getTime())/1000;
                            LeapLogger.info("roundId:" + run.getRoundId()
                                    + "已经完成, 正在进行休息中! 离下次开始还有:" + next + "s!");
                        }
                    } else {
                        LeapLogger.info("roundId:" + run.getStatus() + ",完成度:" + RoundMonitor.finishedPercent());
                    }
                }
            }
        }).start();
    }

    private void nextRun() {
        run.setStartTime(new Date());
        run.setEndTime(null);
        run.setStatus(RoundStatus.RUNNING);
        run.setRoundId(run.getRoundId() + 1);
        monitor.init(run.getRoundId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm.SS");
        LeapLogger.info("新一轮构建开始!开始时间:" + simpleDateFormat.format(run.getStartTime())
                + ", roundId:" + run.getRoundId());
        fetcherExcutorPool.excute();
        buildingExcutorPool.excute();
    }
}
