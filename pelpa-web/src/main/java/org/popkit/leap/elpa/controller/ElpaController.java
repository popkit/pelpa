package org.popkit.leap.elpa.controller;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.entity.CommonResponse;
import org.popkit.core.entity.SimpleResult;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.entity.ArchiveVo;
import org.popkit.leap.elpa.services.LocalCache;
import org.popkit.leap.elpa.services.PkgBuildService;
import org.popkit.leap.elpa.services.PkgFetchService;
import org.popkit.leap.elpa.services.RecipesService;
import org.popkit.leap.monitor.EachActor;
import org.popkit.leap.monitor.RoundMonitor;
import org.popkit.leap.monitor.RoundSupervisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:06:57
 */
@Controller
@RequestMapping(value = "elpa")
public class ElpaController {

    @Autowired
    private PkgFetchService pkgFetchService;

    @Autowired
    private RecipesService recipesService;

    @Autowired
    private PkgBuildService pkgBuildService;

    @RequestMapping(value = "index.html")
    public String index(HttpServletRequest request) {
        Map<String, EachActor> actorMap = RoundMonitor.getActors();
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
                if (actor.getBuildStatus() == ActorStatus.READY) {
                    if (unstartedNo < 50) {
                        unstarted.add(actor);
                        unstartedNo++;
                    }
                    pkgReady.add(pkg);
                } else if (actor.getBuildStatus() == ActorStatus.WORKING) {
                    if ( ongingNo < 50) {
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

        request.setAttribute("percent",  RoundMonitor.finishedPercent());

        request.setAttribute("pkgReady", "共有" + pkgReady.size() + "个:" + StringUtils.join(pkgReady, ","));
        request.setAttribute("pkgOnging", "共有" + pkgOnging.size() + "个:" + StringUtils.join(pkgOnging, ","));
        request.setAttribute("pkgFinished", "共有" + pkgFinished.size() + "个:" + StringUtils.join(pkgFinished, ","));

        request.setAttribute("unstarted", unstarted);
        request.setAttribute("finished", finished);
        request.setAttribute("onging", onging);

        request.setAttribute("currentRun", RoundSupervisor.getCurrentRun().tohumanable());
        return "elpa/index";
    }

    @RequestMapping(value = "d8")
    public CommonResponse d8() {
        CommonResponse com = new CommonResponse();

        //pkgFetchService.d8();
        //pkgFetchService.downloadPackage("nclip");
        //pkgBuildService.buildPackage("nclip");
        com.setData(recipesService.randomRecipe());
        Map<String, ArchiveVo> map = LocalCache.getArchive();

        //pkgBuildService.writeArchiveJSON();
        //recipesService.writeRecipesJson();
        pkgBuildService.writeArchiveJSON();
        return com;
    }

    @RequestMapping(value = "updateArchiveJSON")
    public CommonResponse updateArchiveJSON() {
        CommonResponse com = new CommonResponse();
        com.setData(LocalCache.getArchiveJSON());
        pkgBuildService.writeArchiveJSON();
        return com;
    }

    @RequestMapping(value = "build.html")
    public CommonResponse build(String pkgName) {
        CommonResponse commonResponse = new CommonResponse();
        if (StringUtils.isNotBlank(pkgName)) {
            SimpleResult simpleResult = pkgBuildService.buildPackage(pkgName);
            commonResponse.setData(simpleResult);
        }
        return commonResponse;
    }

    @RequestMapping(value = "monitor")
    public CommonResponse monitor() {
        CommonResponse commonResponse = new CommonResponse();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", RoundMonitor.finishedPercent());

        commonResponse.setData(jsonObject);
        return commonResponse;
    }

    // 显示有哪些finished
    @RequestMapping(value = "finished")
    public CommonResponse finished() {
        CommonResponse commonResponse = new CommonResponse();
        return commonResponse;
    }

    @RequestMapping(value = "heart")
    public CommonResponse heart() {
        CommonResponse commonResponse = new CommonResponse();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ok", "200");
        return commonResponse;
    }
}
