package org.popkit.leap.monitor;

import org.apache.commons.io.FileUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.RoundRun;
import org.popkit.leap.elpa.entity.RoundStatus;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.popkit.leap.monitor.entity.BuildStatus;
import org.popkit.leap.monitor.entity.DiskStatus;
import org.popkit.leap.monitor.utils.DiskUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.DecimalFormat;
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
    private static final String BUILD_STATUS_FILE = "build-status.json";
    private static final String DISK_STATUS_FILE = "disk_status.json";

    @Autowired
    private RoundMonitor monitor;

    @Autowired
    private FetcherExcutorPool fetcherExcutorPool;

    @Autowired
    private BuildingExcutorPool buildingExcutorPool;

    public static final long REST_TIME = 2*60*60*1000;    // ms
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

                    if (RoundMonitor.isFinishedThisRun()) {
                        if (run.getEndTime() == null) {
                            run.setEndTime(new Date());
                        }
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

                    updateDiskStatus();
                    updateBuildStatus();
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
        updateBuildStatus();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm.SS");
        LeapLogger.info("新一轮构建开始!开始时间:" + simpleDateFormat.format(run.getStartTime())
                + ", roundId:" + run.getRoundId());
        fetcherExcutorPool.excute();
        buildingExcutorPool.excute();
    }

    private void updateBuildStatus() {
        BuildStatus buildStatus = new BuildStatus(run);
        String htmlPath = PelpaUtils.getHtmlPath();
        File file = new File(htmlPath + BUILD_STATUS_FILE);

        try {
            FileUtils.writeStringToFile(file, buildStatus.toJSONString());
            LeapLogger.warn("updateBuildStatus success!");
        } catch (Exception e) {
            LeapLogger.warn("error in updateBuildStatus!");
        }
    }

    private void updateDiskStatus() {
        DiskStatus diskStatus = getDiskStatus();
        String htmlPath = PelpaUtils.getHtmlPath();
        File file = new File(htmlPath + DISK_STATUS_FILE);
        try {
            if (diskStatus != null && diskStatus.getAvail() != null && diskStatus.getUsed() != null) {
                FileUtils.writeStringToFile(file, diskStatus.toJSONString());
                // LeapLogger.warn("updateDiskStatus success!");
            }
        } catch (Exception e) {
            LeapLogger.warn("error in updateDiskStatus!");
        }
    }

    private DiskStatus getDiskStatus() {
        File[] roots = File.listRoots();
        /* For each filesystem root, print some info */
        for (File root : roots) {
            if ("/".equalsIgnoreCase(root.getAbsolutePath())) {
                DiskStatus diskStatus = new DiskStatus();
                diskStatus.setAvail(DiskUtils.humanReadableByteCount(root.getFreeSpace(), false));
                DecimalFormat df = new DecimalFormat("#.00");
                diskStatus.setUsed(df.format(((double)(root.getTotalSpace() - root.getFreeSpace())/root.getTotalSpace())*100) + "%");
                return diskStatus;
            }
        }
        return null;
    }

}
