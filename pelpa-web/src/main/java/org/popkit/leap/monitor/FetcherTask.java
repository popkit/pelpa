package org.popkit.leap.monitor;

import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.services.PkgFetchService;
import org.popkit.leap.monitor.entity.FetcherStatus;

import java.util.concurrent.Callable;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:22:21
 */
public class FetcherTask implements Callable<FetcherStatus> {

    private String pkgName;
    private PkgFetchService fetchService;

    public FetcherTask(String pkgName, PkgFetchService fetchService) {
        this.pkgName = pkgName;
        this.fetchService = fetchService;
    }

    public FetcherStatus call() throws Exception {
        LeapLogger.info("pkgName=[" + pkgName + "]正在进行fetch...");
        // TODO 如果失败,会出问题!!!,会使得没有更新
        if (fetchService.downloadPackage(pkgName)) {
            RoundStatusMonitor.updateFetcherStatus(pkgName, ActorStatus.FINISHED);
        }
        LeapLogger.info("pkgName=[" + pkgName + "]fetch完成!");
        return new FetcherStatus(true, "pkgName=[" + pkgName + "]fetch完成!");
    }
}
