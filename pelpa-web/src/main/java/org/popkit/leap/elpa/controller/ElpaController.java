package org.popkit.leap.elpa.controller;

import org.popkit.core.entity.CommonResponse;
import org.popkit.leap.elpa.services.PackageFetchService;
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
    private PackageFetchService packageFetchService;

    @Autowired
    private RecipesService recipesService;

    @RequestMapping(value = "d8")
    public CommonResponse d8() {
        CommonResponse com = new CommonResponse();
        com.setData(recipesService.randomRecipe());
        packageFetchService.d8();
        return com;
    }
}
