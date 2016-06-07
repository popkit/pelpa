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
import org.popkit.leap.elpa.services.*;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.popkit.leap.elpa.utils.RecipeParser;
import org.popkit.leap.log.LogScanner;
import org.popkit.leap.monitor.EachActor;
import org.popkit.leap.monitor.RoundMonitor;
import org.popkit.leap.monitor.RoundSupervisor;
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
    private RecipesService recipesService;

    @Autowired
    private PkgBuildService pkgBuildService;

    @Autowired
    private ArchiveContentsGenerator archiveContentsGenerator;

    @Autowired
    private RoundSupervisor roundSupervisor;

    @Autowired
    private LogScanner logScanner;

    @Autowired
    private RoundMonitor roundMonitor;

    @RequestMapping(value = "index.html")
    public String index(HttpServletRequest request) {
        Map<String, EachActor> actorMap = roundMonitor.getActors();
        List<EachActor> unstarted = new ArrayList<EachActor>();
        List<EachActor> finished = new ArrayList<EachActor>();
        List<EachActor> onging = new ArrayList<EachActor>();

        int unstartedNo = 0;
        int finishedNo = 0;
        int ongingNo = 0;
        List<String> pkgReady = new ArrayList<String>();
        List<String> pkgFinished = new ArrayList<String>();
        List<String> pkgOnging = new ArrayList<String>();

        if (MapUtils.isNotEmpty(actorMap)) {
            for (String pkg : actorMap.keySet()) {
                EachActor actor = actorMap.get(pkg);
                if (actor.getFetchStatus() == ActorStatus.READY) {
                    if (unstartedNo < 50) {
                        unstarted.add(actor);
                        unstartedNo++;
                    }
                    pkgReady.add(pkg);
                } else if (actor.getBuildStatus() == ActorStatus.WORKING
                        || actor.getFetchStatus() == ActorStatus.WORKING) {
                    if (ongingNo < 50) {
                        onging.add(actor);
                        ongingNo++;
                    }
                    pkgOnging.add(pkg);
                } else if (actor.getBuildStatus() == ActorStatus.FINISHED) {
                    if (finishedNo < 50) {
                        finished.add(actor);
                        finishedNo++;
                    }
                    pkgFinished.add(pkg);
                }
            }
        }

        request.setAttribute("percentDesc", roundMonitor.finishedPercent());
        request.setAttribute("percent", (roundMonitor.finishedPercentValue() * 100));

        request.setAttribute("pkgReady", "共有" + pkgReady.size() + "个:" + StringUtils.join(pkgReady, ","));
        request.setAttribute("pkgOnging", "共有" + pkgOnging.size() + "个:" + StringUtils.join(pkgOnging, ","));
        request.setAttribute("pkgFinished", "共有" + pkgFinished.size() + "个:" + StringUtils.join(pkgFinished, ","));

        request.setAttribute("unstarted", unstarted);
        request.setAttribute("finished", finished);
        request.setAttribute("onging", onging);

        request.setAttribute("currentRun", roundSupervisor.getCurrentRun().tohumanable());
        return "elpa/build";
    }

    @RequestMapping(value = "ajaxBuildStatus.json")
    public void ajaxBuildStatus(HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("percentDesc", roundMonitor.finishedPercent());
        jsonObject.put("percent", (roundMonitor.finishedPercentValue() * 100));
        ResponseUtils.renderJson(response, jsonObject.toJSONString());
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

        RecipeDo recipeDo = recipesService.getRecipeDo(pkgName);
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
        com.setData(recipesService.getAllRecipeList());
        recipesService.writeRecipesJson();
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
        String result = archiveContentsGenerator.updateAC();
        commonResponse.setData(result);
        return commonResponse;
    }

    @RequestMapping(value = "build.html")
    public CommonResponse build(String pkgName) {
        CommonResponse commonResponse = new CommonResponse();
        if (StringUtils.isNotBlank(pkgName)) {
            RecipeDo recipeDo = RecipeParser.parsePkgRecipe(pkgName);
            pkgFetchService.downloadPackage(pkgName);
            SimpleResult simpleResult = pkgBuildService.buildPackage(pkgName);
            commonResponse.setData(simpleResult);
        }
        return commonResponse;
    }

    @RequestMapping(value = "missed.html")
    public CommonResponse missed() {
        CommonResponse commonResponse = new CommonResponse();
        List<String> missed = archiveContentsGenerator.diff();
        commonResponse.setData(missed);
        return commonResponse;
    }

}
