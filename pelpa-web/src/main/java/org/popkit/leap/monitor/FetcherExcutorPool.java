package org.popkit.leap.monitor;

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

    // 初始起10个工作线程
    private static final ExecutorService exector = Executors.newFixedThreadPool(3);

    public void excute() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    String pkgName = RoundMonitor.nexFetcherPkg();
                    if (pkgName == null) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            // TODO
                        }
                    } else {
                        RoundMonitor.updateFetcherStatus(pkgName, ActorStatus.WORKING);
                        exector.execute(new FetcherTask(pkgName, pkgFetchService));
                    }
                }
            }
        }).start();
    }
}
