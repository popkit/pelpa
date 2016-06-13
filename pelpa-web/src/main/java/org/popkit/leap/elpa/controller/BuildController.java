package org.popkit.leap.elpa.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.entity.CommonResponse;
import org.popkit.core.entity.SimpleResult;
import org.popkit.core.utils.ResponseUtils;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.entity.RoundRun;
import org.popkit.leap.elpa.services.*;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.popkit.leap.elpa.utils.RecipeParser;
import org.popkit.leap.elpa.utils.ToolUtils;
import org.popkit.leap.log.LogScanner;
import org.popkit.leap.monitor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:06:57
 */
@Controller
@RequestMapping(value = "elpa/build")
public class BuildController {

    @Autowired
    private PkgFetchService pkgFetchService;

    @Autowired
    private PkgBuildService pkgBuildService;

    @Autowired
    private RoundSupervisor roundSupervisor;

    @Autowired
    private LogScanner logScanner;

    @Autowired
    private FetcherExcutorPool fetcherExcutorPool;

    @Autowired
    private BuildingExcutorPool buildingExcutorPool;

    @RequestMapping(value = "index.html")
    public String index(HttpServletRequest request) {
        Map<String, EachActor> actorMap = RoundStatusMonitor.getActors();
        List<String> pkgReady = new ArrayList<String>();
        List<String> pkgFinished = new ArrayList<String>();
        List<String> pkgOnging = new ArrayList<String>();

        if (MapUtils.isNotEmpty(actorMap)) {
            for (String pkg : actorMap.keySet()) {
                EachActor actor = actorMap.get(pkg);
                if (actor.getFetchStatus() == ActorStatus.READY) {
                    pkgReady.add(pkg);
                } else if (actor.getBuildStatus() == ActorStatus.WORKING
                        || actor.getFetchStatus() == ActorStatus.WORKING) {
                    pkgOnging.add(pkg);
                } else if (actor.getBuildStatus() == ActorStatus.FINISHED) {
                    pkgFinished.add(pkg);
                }
            }
        }

        request.setAttribute("percentDesc", RoundStatusMonitor.finishedPercent());
        request.setAttribute("percent", (RoundStatusMonitor.finishedPercentValue() * 100));

        request.setAttribute("pkgReady", "共有" + pkgReady.size() + "个:" + StringUtils.join(pkgReady, ","));
        request.setAttribute("pkgOnging", "共有" + pkgOnging.size() + "个:" + StringUtils.join(pkgOnging, ","));
        request.setAttribute("pkgFinished", "共有" + pkgFinished.size() + "个:" + StringUtils.join(pkgFinished, ","));

        List<RecipeDo> missed = ArchiveContentsGenerator.diff();
        List<String> missedList = convert(missed);

        request.setAttribute("missed", missed);
        request.setAttribute("pkgMissed","Missed:" + missedList.size() + ":"+ StringUtils.join(missedList, ","));
        request.setAttribute("currentRun", RoundStatusMonitor.getCurrent().tohumanable());
        return "elpa/build";
    }

    @RequestMapping(value = "ajaxBuildStatus.json")
    public void ajaxBuildStatus(HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("percentDesc", RoundStatusMonitor.finishedPercent());
        double finishedPercent = RoundStatusMonitor.finishedPercentValue();
        jsonObject.put("percent", (finishedPercent * 100));
        RoundRun current = RoundStatusMonitor.getCurrent();
        jsonObject.put("currentRun", current.tohumanable() + " @"+ Integer.toHexString(current.hashCode()));

        String missedInfo;
        if (finishedPercent > 0.9 && finishedPercent < 1) {
            List<RecipeDo> missed = ArchiveContentsGenerator.diff();
            List<String> missedList = convert(missed);
            missedInfo = "Missed:" + missedList.size() + ":"+ StringUtils.join(missedList, ",");
            jsonObject.put("missed", missedInfo);
        }

        ResponseUtils.renderJson(response, jsonObject.toJSONString());
    }

    private List<String> convert(List<RecipeDo> recipes) {
        List<String> result = new ArrayList<String>();
        for (RecipeDo item : recipes) {
            result.add(item.getPkgName());
        }

        return result;
    }

    @RequestMapping(value = "d8")
    public CommonResponse d8(String pkgName) {
        CommonResponse com = new CommonResponse();

        //pkgFetchService.d8();
        //pkgFetchService.downloadPackage("nclip");
        //pkgBuildService.buildPackage("nclip");
        //com.setData(recipesService.randomRecipe());
        //com.setData(recipesService.getRecipeDo("tango-2-theme"));
        //Map<String, ArchiveVo> map = LocalCache.getArchive();

        //pkgBuildService.writeArchiveJSON();
        //recipesService.writeRecipesJson();
        //pkgBuildService.writeArchiveJSON();

        RecipeDo recipeDo = LocalCache.getRecipeDo(pkgName);
        com.setData(recipeDo);

        return com;
    }

    @RequestMapping(value = "recipe.html")
    public void recipe(String pkgName) {
        //String path = "/Users/aborn/github/popkit-elpa/recipes/";
        String pathConfig = PelpaUtils.getRecipeFilePath();
        File item = new File(pathConfig + pkgName);
        try {
            String contetnString = FileUtils.readFileToString(item, "UTF-8");
            RecipeDo resItem = RecipeParser.parse(contetnString);
            System.out.println("resItem:" + JSON.toJSONString(resItem));
        } catch (Exception e) {

        }

    }

    @RequestMapping(value = "updateRecipeJSON")
    public CommonResponse updateRecipeJSON() {
        CommonResponse com = new CommonResponse();
        com.setData(LocalCache.getAllRecipeList());
        LocalCache.writeRecipesJson();
        return com;
    }

    @RequestMapping(value = "updateArchiveJSON")
    public CommonResponse updateArchiveJSON() {
        CommonResponse com = new CommonResponse();
        com.setData(LocalCache.getArchiveJSON());
        LocalCache.writeArchiveJSON();
        return com;
    }

    @RequestMapping(value = "updateAC")
    public CommonResponse updateAC() {
        CommonResponse commonResponse = new CommonResponse();
        String result = ArchiveContentsGenerator.updateAC();
        commonResponse.setData(result);
        return commonResponse;
    }

    @RequestMapping(value = "build.html")
    public CommonResponse build(String pkgName) {
        CommonResponse commonResponse = new CommonResponse();
        if (StringUtils.isNotBlank(pkgName)) {
            RecipeDo recipeDo = RecipeParser.parsePkgRecipe(pkgName);
            File workingPath = new File(PelpaUtils.getWorkingPath(pkgName));
            if (workingPath.exists() && workingPath.isDirectory()
                    && "beta".equals(PelpaUtils.getEnv())
                    && (!ToolUtils.isEmptyPath(workingPath))) {
            } else {
                pkgFetchService.downloadPackage(pkgName);
            }
            SimpleResult simpleResult = pkgBuildService.buildPackage(pkgName);
            commonResponse.setData(simpleResult);
        }
        return commonResponse;
    }

    @RequestMapping(value = "missed.html")
    public CommonResponse missed() {
        CommonResponse commonResponse = new CommonResponse();
        List<RecipeDo> missed = ArchiveContentsGenerator.diff();
        commonResponse.setData(missed);
        return commonResponse;
    }

}
