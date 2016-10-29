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

    private final ExecutorService exector = Executors.newFixedThreadPool(2);

    public ExecutorService getExector() {
        return exector;
    }

    public void excute() {
        // // FIXME: 6/9/16 这里有个bug,一个pkg可能会循环加入fetch任务队列
        new Thread(new Runnable() {
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

                    if (StringUtils.isNotBlank(pkgName)) {
                        ActorStatus actorStatus = RoundStatusMonitor.getFetcherStatus(pkgName);
                        if (actorStatus == ActorStatus.READY) {
                            LeapLogger.info("pkgName:" + pkgName + " added to fetch working queue!");
                            RoundStatusMonitor.updateFetcherStatus(pkgName, ActorStatus.WORKING);
                            exector.execute(new FetcherTask(pkgName, pkgFetchService));
                        }
                    }
                }
            }
        }).start();
    }
}
