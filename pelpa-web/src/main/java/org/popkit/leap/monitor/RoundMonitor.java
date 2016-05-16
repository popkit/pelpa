package org.popkit.leap.monitor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.services.RecipesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:21:53
 */
@Service
public class RoundMonitor {

    @Autowired
    private RecipesService recipesService;

    private static final ConcurrentHashMap<String, EachActor> actors = new ConcurrentHashMap<String, EachActor>();

    public void init(int roundid) {
        if (MapUtils.isNotEmpty(actors)) {
            for (String pkg : actors.keySet()) {
                actors.remove(pkg);
            }
        }

        List<RecipeDo> recipeDoList = recipesService.getAllRecipeList();
        if (CollectionUtils.isNotEmpty(recipeDoList)) {
            for (RecipeDo recipeDo : recipeDoList) {
                EachActor eachActor = new EachActor(recipeDo.getPkgName(), roundid);
                actors.putIfAbsent(recipeDo.getPkgName(), eachActor);
            }
        } else {
            LeapLogger.warn("本次初始化RecipeList为空, roundId=" + roundid);
        }
    }

    public static Map<String, EachActor> getActors() {
        return actors;
    }

    public static void updateFetcherStatus(String pkg, ActorStatus actStatus) {
        if (actStatus == ActorStatus.WORKING) {
            actors.get(pkg).setStartTime(new Date());
        }
        actors.get(pkg).setFetchStatus(actStatus);
    }

    public static void updateBuildingStatus(String pkg, ActorStatus actStatus) {
        if (actStatus == ActorStatus.FINISHED) {
            actors.get(pkg).setEndTime(new Date());
        }
        actors.get(pkg).setBuildStatus(actStatus);
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
            if (!actors.get(pkg).isBuildFinished()
                    && actors.get(pkg).getBuildStatus() == ActorStatus.READY) {
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
}
