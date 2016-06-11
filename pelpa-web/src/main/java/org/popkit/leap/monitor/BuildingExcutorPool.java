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

    private final ExecutorService exector = Executors.newFixedThreadPool(2);

    public ExecutorService getExector() {
        return exector;
    }

    public void excute() {
        new Thread(new Runnable() {
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
        }).start();
    }
}
