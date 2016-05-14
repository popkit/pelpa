package org.popkit.leap.elpa.controller;

import org.popkit.core.entity.CommonResponse;
import org.popkit.leap.elpa.services.PkgBuildService;
import org.popkit.leap.elpa.services.PkgFetchService;
import org.popkit.leap.elpa.services.RecipesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping(value = "d8")
    public CommonResponse d8() {
        CommonResponse com = new CommonResponse();

        //pkgFetchService.d8();
        //pkgBuildService.buildPackage(recipesService.randomRecipe());
        pkgFetchService.downloadPackage(recipesService.randomRecipe());
        com.setData(recipesService.randomRecipe());

        //pkgBuildService.writeArchiveJSON();
        //recipesService.writeRecipesJson();
        return com;
    }
}
