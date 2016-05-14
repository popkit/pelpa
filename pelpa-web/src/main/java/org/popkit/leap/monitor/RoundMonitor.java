package org.popkit.leap.monitor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.services.RecipesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public static void finishedFetcher(String pkg) {
        actors.get(pkg).setFetchFinished(true);
    }

    public static void finishedBuilder(String pkg) {
        actors.get(pkg).setBuildFinished(true);
    }

    public static String nexFetcherPkg() {
        for (String pkg : actors.keySet()) {
            if (!actors.get(pkg).isFetchFinished()) {
                return pkg;
            }
        }
        return null;
    }

    public static String nextBuildingPkg() {
        for (String pkg : actors.keySet()) {
            if (!actors.get(pkg).isBuildFinished() && actors.get(pkg).isFetchFinished()) {
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
}
