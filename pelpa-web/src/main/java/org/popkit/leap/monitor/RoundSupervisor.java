package org.popkit.leap.monitor;

import org.apache.commons.io.FileUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.constents.EnvEnum;
import org.popkit.leap.elpa.entity.PelpaContents;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.entity.RoundRun;
import org.popkit.leap.elpa.services.LocalCache;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.popkit.leap.monitor.entity.BuildStatus;
import org.popkit.leap.monitor.entity.DiskStatus;
import org.popkit.leap.monitor.utils.BadgeUtils;
import org.popkit.leap.monitor.utils.DiskUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:21:21
 */
@Service
public class RoundSupervisor {
    private static final String BUILD_STATUS_FILE = "build-status.json";
    private static final String DISK_STATUS_FILE = "disk_status.json";
    private static final String BUILD_STATUS_FILE_BADGE = "build-status.svg";
    private static AtomicBoolean initStatus = new AtomicBoolean(false);

    @Autowired
    private FetcherExcutorPool fetcherExcutorPool;

    @Autowired
    private BuildingExcutorPool buildingExcutorPool;

    @PostConstruct
    public void init() {
        if (initStatus.compareAndSet(false, true)) {
            LeapLogger.info("@PostConstruct" + Integer.toHexString(this.hashCode()));
            run();
        }
    }

    private void run() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        LeapLogger.warn("InterruptedException in init in roundsupervisor", e);
                        return;
                    }

                    RoundRun current = RoundStatusMonitor.getCurrent();
                    if (current.isReady()) {
                        if (LocalCache.initLocalCache()) {
                            List<RecipeDo> recipeDoList = LocalCache.getAllRecipeList();
                            LeapLogger.info("isReady roundId:" + current.getRoundId() + "recipeDoList.size=" + recipeDoList.size()
                                    + " @" + Integer.toHexString(current.hashCode()));
                            RoundStatusMonitor.init(current.getRoundId(), recipeDoList);
                            if (RoundStatusMonitor.startRun()
                                    && PelpaUtils.getEnv() == EnvEnum.PRODUCTION) {
                                fetcherExcutorPool.excute();
                                buildingExcutorPool.excute();
                            }
                        } else {
                            continue;
                        }
                    } else if (current.isRunning()) {
                        if (RoundStatusMonitor.isFinishedThisRun()) {
                            RoundStatusMonitor.okFinished();
                        }
                        LeapLogger.info("roundId:" + current.getRoundId() + ", status:" + current.getStatus() +
                                ",完成度:" + RoundStatusMonitor.finishedPercent() + " @" + Integer.toHexString(current.hashCode()));
                        try {
                            TimeUnit.SECONDS.sleep(30);
                        } catch (InterruptedException e) {
                            //
                        }
                    } else if (current.isFinished()) {
                        if (current.getEndTime().getTime() + PelpaContents.REST_TIME > new Date().getTime()) {
                            long next = ((current.getEndTime().getTime() + PelpaContents.REST_TIME) - new Date().getTime())/1000;
                            LeapLogger.info("roundId:" + current.getRoundId()
                                    + "已经完成, 正在进行休息中! 离下次开始还有:" + next + "s!"
                                    + " ##" + current.toString() + " @" + Integer.toHexString(current.hashCode()));
                            try {
                                TimeUnit.MINUTES.sleep(5);
                            } catch (InterruptedException e) {
                                //
                            }
                            continue;
                        } else {
                            RoundRun nexRun = RoundStatusMonitor.nextRoundRun();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm.SS");
                            LeapLogger.info("新一轮构建开始!开始时间:" + simpleDateFormat.format(nexRun.getStartTime())
                                    + ", roundId:" + nexRun.getRoundId() + " @" + Integer.toHexString(nexRun.hashCode()));
                        }
                    }

                    updateDiskStatus();
                    updateBuildStatus();
                    //LeapLogger.info(current.tohumanable() + current.toString());
                }
            }
        }).start();
    }

    private void updateBuildStatus() {
        BuildStatus buildStatus = new BuildStatus(RoundStatusMonitor.getCurrent());
        String htmlPath = PelpaUtils.getHtmlPath();
        File file = new File(htmlPath + BUILD_STATUS_FILE);
        File statusBadge = new File(htmlPath + BUILD_STATUS_FILE_BADGE);

        try {
            FileUtils.writeStringToFile(file, buildStatus.toJSONString());
            FileUtils.writeStringToFile(statusBadge, BadgeUtils.getCurrentStatus(RoundStatusMonitor.getCurrent()));
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
            }
        } catch (Exception e) {
            LeapLogger.warn("error in updateDiskStatus!");
        }
    }

    public static DiskStatus getDiskStatus() {
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
