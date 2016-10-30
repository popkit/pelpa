package org.popkit.leap.monitor;

import org.apache.commons.lang3.StringUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.services.PkgFetchService;
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
public class FetcherExcutorPool {
    @Autowired
    private PkgFetchService pkgFetchService;

    private final ExecutorService EXECUTOR_POOL = Executors.newFixedThreadPool(4);

    private static Thread RUNNING_THREAD = null;

    public void excute() {
        // 只需要创建一次
        if (RUNNING_THREAD != null && RUNNING_THREAD.isAlive()) {
            LeapLogger.info("FetcherExcutor RUNNING_THREAD is alive, do nothing!!");
            return;
        }

        RUNNING_THREAD = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    String pkgName = RoundStatusMonitor.nexFetcherPkg();
                    try {
                        if (StringUtils.isBlank(pkgName)) {
                            TimeUnit.SECONDS.sleep(10);
                        } else {
                            TimeUnit.SECONDS.sleep(1);
                        }
                    } catch (InterruptedException e) {
                        LeapLogger.warn("InterruptedException + " + pkgName, e);
                    }

                    if (StringUtils.isBlank(pkgName)) {
                        continue;
                    }

                    ActorStatus actorStatus = RoundStatusMonitor.getFetcherStatus(pkgName);
                    if (actorStatus == ActorStatus.READY) {
                        RoundStatusMonitor.updateFetcherStatus(pkgName, ActorStatus.WORKING);
                        EXECUTOR_POOL.execute(new FetcherTask(pkgName, pkgFetchService));
                    }
                }
            }
        });
        RUNNING_THREAD.start();
    }
}
