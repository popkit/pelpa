package org.popkit.leap.monitor;

import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.services.PkgFetchService;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:22:21
 */
public class FetcherTask implements Runnable {
    private String pkgName;
    private PkgFetchService fetchService;

    public FetcherTask(String pkgName, PkgFetchService fetchService) {
        this.pkgName = pkgName;
        this.fetchService = fetchService;
    }

    public void run() {
        LeapLogger.info("pkgName=[" + pkgName + "]正在进行fetch...");
        if (fetchService.downloadPackage(pkgName)) {  // TODO 如果失败,会出问题!!!,会使得没有更新
            RoundStatusMonitor.updateFetcherStatus(pkgName, ActorStatus.FINISHED);
        }
        LeapLogger.info("pkgName=[" + pkgName + "]fetch完成!");
    }
}
