package org.popkit.leap.monitor;

import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.services.PkgBuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:22:19
 */
@Service
public class BuildingExcutorPool {

    @Autowired
    private PkgBuildService pkgBuildService;

    private final ExecutorService exector = Executors.newFixedThreadPool(1);

    private static Thread RUNNING_THREAD = null;

    public void excute() {
        // 只需要创建一次
        if (RUNNING_THREAD != null && RUNNING_THREAD.isAlive()) {
            LeapLogger.info("BuildingExcutor RUNNING_THREAD is alive, do nothing!!");
            return;
        }

        RUNNING_THREAD = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    String pkgName = RoundStatusMonitor.nextBuildingPkg();
                    if (pkgName == null) {
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            // TODO log
                        }
                    } else {
                        ActorStatus actorStatus = RoundStatusMonitor.getBuildingStatus(pkgName);
                        if (actorStatus == ActorStatus.READY) {
                            LeapLogger.info("pkgName:" + pkgName + " added to build working queue!");
                            RoundStatusMonitor.updateBuildingStatus(pkgName, ActorStatus.WORKING);
                            exector.execute(new BuildingTask(pkgName, pkgBuildService));
                        }
                    }
                }
            }
        });
        RUNNING_THREAD.start();
    }
}
