package org.popkit.leap.elpa.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.popkit.core.entity.CommonResponse;
import org.popkit.leap.elpa.entity.ActorStatus;
import org.popkit.leap.elpa.services.PkgBuildService;
import org.popkit.leap.elpa.services.PkgFetchService;
import org.popkit.leap.elpa.services.RecipesService;
import org.popkit.leap.monitor.EachActor;
import org.popkit.leap.monitor.RoundMonitor;
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

        if (MapUtils.isNotEmpty(actorMap)) {
            for (String pkg : actorMap.keySet()) {
                EachActor actor = actorMap.get(pkg);
                if (actor.getBuildStatus() == ActorStatus.READY && unstartedNo < 50) {
                    unstarted.add(actor);
                    unstartedNo ++;
                } else if (actor.getBuildStatus() == ActorStatus.WORKING && ongingNo < 50) {
                    onging.add(actor);
                    ongingNo ++;
                } else if (actor.getBuildStatus() == ActorStatus.FINISHED && finishedNo < 50) {
                    finished.add(actor);
                    finishedNo ++;
                }
            }
        }

        request.setAttribute("unstarted", unstarted);
        request.setAttribute("finished", finished);
        request.setAttribute("onging", onging);
        return "";
    }

    @RequestMapping(value = "d8")
    public CommonResponse d8() {
        CommonResponse com = new CommonResponse();

        //pkgFetchService.d8();
        pkgFetchService.downloadPackage("nclip");
        pkgBuildService.buildPackage("nclip");
        com.setData(recipesService.randomRecipe());

        //pkgBuildService.writeArchiveJSON();
        //recipesService.writeRecipesJson();
        return com;
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
