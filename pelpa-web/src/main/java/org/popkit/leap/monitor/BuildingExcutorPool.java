package org.popkit.leap.monitor;

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

    private static final ExecutorService exector = Executors.newFixedThreadPool(2);

    public void excute() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    String pkgName = RoundMonitor.nextBuildingPkg();
                    if (pkgName == null) {
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            // TODO
                        }
                    } else {
                        exector.execute(new BuildingTask(pkgName, pkgBuildService));
                        RoundMonitor.updateBuildingStatus(pkgName, ActorStatus.WORKING);
                    }
                }
            }
        }).start();
    }
}
