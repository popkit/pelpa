package org.popkit.leap.monitor;

import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.services.PkgFetchService;
import org.popkit.leap.monitor.entity.FetcherStatus;

import java.util.concurrent.*;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:22:21
 */
public class FetcherTask extends Thread {

    private String pkgName;
    private PkgFetchService fetchService;

    public FetcherTask(String pkgName, PkgFetchService fetchService) {
        this.pkgName = pkgName;
        this.fetchService = fetchService;
    }

    @Override
    public void run() {
        Callable<FetcherStatus> downloadTask = new Callable<FetcherStatus>() {
            public FetcherStatus call() throws Exception {
                long beginTime = System.currentTimeMillis();
                try {
                    fetchService.downloadPackage(pkgName);
                } catch (Exception throwable) {
                    LeapLogger.warn("downloadPackage [" + pkgName + "] throwable", throwable);
                }
                long timeCost = (System.currentTimeMillis() - beginTime) / (1000);
                return new FetcherStatus(true, "pkgName=[" + pkgName + "]fetch完成!" + ", 耗时:" + timeCost + "s.");
            }
        };

        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<FetcherStatus> future = service.submit(downloadTask);
        try {
            FetcherStatus status = future.get(10, TimeUnit.MINUTES);
            if (status == null) {
                LeapLogger.info("pkgName=[" + pkgName + "] fetch future timeout!");
            } else {
                LeapLogger.info("pkgName=[" + pkgName + "] fetch future finished!" + status.getInfo());
            }
        } catch (InterruptedException e) {
            LeapLogger.info("pkgName=[" + pkgName + "] fetch InterruptedException!");
        } catch (ExecutionException e) {
            LeapLogger.info("pkgName=[" + pkgName + "] fetch ExecutionException!");
        } catch (TimeoutException e) {
            future.cancel(true);
            LeapLogger.info("pkgName=[" + pkgName + "] fetch timeout TimeoutException!");
        } finally {
            RoundStatusMonitor.updateFetcherStatus(pkgName, ActorStatus.FINISHED);
        }

        /**
        // TODO 如果失败,会出问题!!!,会使得没有更新
        if (fetchService.downloadPackage(pkgName)) {
            RoundStatusMonitor.updateFetcherStatus(pkgName, ActorStatus.FINISHED);
        }
         LeapLogger.info("pkgName=[" + pkgName + "]fetch完成!");
        **/
    }

    public String getPkgName() {
        return pkgName;
    }
}
