package org.popkit.leap.monitor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.entity.RoundRun;
import org.popkit.leap.elpa.entity.RoundStatus;
import org.popkit.leap.elpa.services.ArchiveContentsGenerator;
import org.popkit.leap.elpa.services.LocalCache;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * monitor each round running status
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:21:53
 */
public class RoundStatusMonitor {

    private RoundStatusMonitor() {}

    private static RoundRun current = new RoundRun();

    private static ConcurrentHashMap<String, EachActor> actors = new ConcurrentHashMap<String, EachActor>();

    public static synchronized void nextRoundRun() {
        current.increase();
        // last round elapsed time
        current.updateLastRoundTimeUsed();
        current.setEndTime(null);
        current.setStartTime(new Date());
        current.setStatus(RoundStatus.READY);

    }

    public static synchronized boolean startRun() {
        current.setStatus(RoundStatus.RUNNING);
        return true;
    }

    public static synchronized boolean okFinished() {
        if (!current.isFinished()) {
            current.setStatus(RoundStatus.FINISHED);
            current.setEndTime(new Date());
            ArchiveContentsGenerator.updateAC();
            LocalCache.writeArchiveJSON();
            LocalCache.writeRecipesJson();
        }
        return true;
    }

    public static void init(int roundid, List<RecipeDo> recipeDoList) {
        if (MapUtils.isNotEmpty(actors)) {
            for (String pkg : actors.keySet()) {
                actors.remove(pkg);
            }
        }

        if (CollectionUtils.isNotEmpty(recipeDoList)) {
            for (RecipeDo recipeDo : recipeDoList) {
                EachActor eachActor = new EachActor(recipeDo.getPkgName(), roundid);
                actors.put(recipeDo.getPkgName(), eachActor);
            }
        } else {
            LeapLogger.warn("本次初始化RecipeList为空, roundId=" + roundid);
        }
    }

    public static RoundRun getCurrent() {
        return current;
    }

    public static Map<String, EachActor> getActors() {
        return actors;
    }

    public static ActorStatus getFetcherStatus(String pkg) {
        return actors.containsKey(pkg) ? actors.get(pkg).getFetchStatus() : null;
    }

    public static synchronized void updateFetcherStatus(String pkg, ActorStatus actStatus) {
        if (actStatus == ActorStatus.WORKING) {
            actors.get(pkg).setStartTime(new Date());
        }
        actors.get(pkg).setFetchStatus(actStatus);
    }

    public static synchronized void updateBuildingStatus(String pkg, ActorStatus actStatus) {
        if (actStatus == ActorStatus.FINISHED) {
            actors.get(pkg).setEndTime(new Date());
        }
        actors.get(pkg).setBuildStatus(actStatus);
    }

    public static ActorStatus getBuildingStatus(String pkg) {
        return actors.containsKey(pkg) ? actors.get(pkg).getBuildStatus() : null;
    }

    public static String nexFetcherPkg() {
        for (String pkg : actors.keySet()) {
            if (actors.get(pkg).getFetchStatus() == ActorStatus.READY) {
                return pkg;
            }
        }
        return null;
    }

    public static String nextBuildingPkg() {
        for (String pkg : actors.keySet()) {
            if ((!actors.get(pkg).isBuildFinished())
                    && actors.get(pkg).getBuildStatus() == ActorStatus.READY
                    && actors.get(pkg).getFetchStatus() == ActorStatus.FINISHED) {
                return pkg;
            }
        }
        return null;
    }

    public static boolean isFinishedThisRun() {
        for (String pkg : actors.keySet()) {
            if (!actors.get(pkg).isFinished()) {
                return false;
            }
        }
        return true;
    }

    public static String finishedPercent() {
        int finished = 0;
        int roundId = 0;
        int total = 0;
        for (String pkg : actors.keySet()) {
            if (actors.get(pkg).isFinished()) {
                finished ++;
            }
            roundId = actors.get(pkg).getRoundId();
            total ++;
        }
        return "roundId=" + roundId + ", finished: (" + finished +
                "/" + total+ ")=" + ((double) finished) / total;
    }

    public static double finishedPercentValue() {
        int finished = 0;
        int total = 0;
        for (String pkg : actors.keySet()) {
            if (actors.get(pkg).isFinished()) {
                finished ++;
            }
            total ++;
        }

        return ((double) finished)/total;
    }
}
