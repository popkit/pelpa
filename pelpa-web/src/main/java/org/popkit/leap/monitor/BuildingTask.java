package org.popkit.leap.monitor;

import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.services.PkgBuildService;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:22:21
 */
public class BuildingTask implements Runnable {
    private String pkgName;

    private PkgBuildService pkgBuildService;
    public BuildingTask(String pkgName, PkgBuildService pkgBuildService) {
        this.pkgName = pkgName;
        this.pkgBuildService = pkgBuildService;
    }

    public void run() {
        LeapLogger.info("pkgName=[" + pkgName + "]正在进行building...");
        //if (pkgBuildService.buildPackage(pkgName).isSuccess()) {
            RoundMonitor.updateBuildingStatus(pkgName, ActorStatus.FINISHED);
        //}
        LeapLogger.info("pkgName=[" + pkgName + "]building完成!");
    }
}
