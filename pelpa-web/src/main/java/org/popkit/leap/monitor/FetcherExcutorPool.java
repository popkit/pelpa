package org.popkit.leap.monitor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.services.PkgFetchService;
import org.popkit.leap.monitor.entity.FetcherStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:22:19
 */
@Service
public class FetcherExcutorPool {
    private static final int GROUP_SIZE = 20;

    @Autowired
    private PkgFetchService pkgFetchService;

    private final ExecutorService EXECUTOR_POOL = Executors.newFixedThreadPool(4);

    public ExecutorService getExector() {
        return EXECUTOR_POOL;
    }

    private List<FetcherTask> getFetcherGroupTask() {
        int i = GROUP_SIZE;
        List<FetcherTask> result = new ArrayList<FetcherTask>();
        while (i > 0) {
            String pkgName = RoundStatusMonitor.nexFetcherPkg();
            if (StringUtils.isBlank(pkgName)) {
                return result;
            }
            ActorStatus actorStatus = RoundStatusMonitor.getFetcherStatus(pkgName);
            if (actorStatus == ActorStatus.READY) {
                LeapLogger.info("pkgName:" + pkgName + " added to fetch working queue!");
                RoundStatusMonitor.updateFetcherStatus(pkgName, ActorStatus.WORKING);
                FetcherTask fetcherTask = new FetcherTask(pkgName, pkgFetchService);
                result.add(fetcherTask);
            }
            i--;
        }
        return result;
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

                    if (StringUtils.isBlank(pkgName)) {
                        continue;
                    }

                    List<FetcherTask> fetcherTasks = getFetcherGroupTask();
                    if (CollectionUtils.isEmpty(fetcherTasks)) {
                        continue;
                    }

                    try {
                        Iterator<FetcherTask> iterator = fetcherTasks.iterator();
                        List<Future<FetcherStatus>> futures = EXECUTOR_POOL.invokeAll(fetcherTasks, 10, TimeUnit.MINUTES);
                        for (Future<FetcherStatus> itemFuture : futures) {
                            FetcherTask task = iterator.next();
                            try {
                                FetcherStatus status = itemFuture.get();
                                if (status == null) {
                                    LeapLogger.warn("fetcher [" + task.getPkgName() + "] timeout!!");
                                    // 这里也更新状态为成功
                                } else {
                                    LeapLogger.warn("fetcher [" + task.getPkgName() + "] success!!" + status.getInfo());
                                }
                            } catch (ExecutionException e) {
                                LeapLogger.warn("fetcher [" + task.getPkgName() + "] timeout!! exception..");
                                e.printStackTrace();
                            } finally {
                                RoundStatusMonitor.updateFetcherStatus(task.getPkgName(), ActorStatus.FINISHED);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    /**
                     FetcherTask fetcherTask = new FetcherTask(pkgName, pkgFetchService);
                     FutureTask<FetcherStatus> futureTask = new FutureTask<FetcherStatus>(fetcherTask);
                     EXECUTOR_POOL.execute(futureTask);
                     try {
                     FetcherStatus status = futureTask.get(10, TimeUnit.MINUTES);
                     if (status == null) {
                     LeapLogger.warn("fetcher [" + pkgName + "] timeout!!");
                     // 这里也更新状态为成功
                     RoundStatusMonitor.updateFetcherStatus(pkgName, ActorStatus.FINISHED);
                     } else {
                     LeapLogger.warn("fetcher [" + pkgName + "] success!!" + status.getInfo());
                     }
                     } catch (InterruptedException e) {
                     e.printStackTrace();
                     } catch (ExecutionException e) {
                     e.printStackTrace();
                     } catch (TimeoutException e) {
                     e.printStackTrace();
                     }
                     **/
                }
            }
        }).start();
    }
}
