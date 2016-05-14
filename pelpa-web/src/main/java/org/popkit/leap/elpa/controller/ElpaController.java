package org.popkit.leap.elpa.controller;

import org.popkit.core.entity.CommonResponse;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.utils.RecipesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:06:57
 */
@Controller
@RequestMapping(value = "elpa")
public class ElpaController {

    @RequestMapping(value = "d8")
    public CommonResponse d8() {
        CommonResponse com = new CommonResponse();
        List<File> fileList = RecipesUtils.getRecipeFileList();
        List<RecipeDo> recipeDos = RecipesUtils.asRecipeArch(fileList);
        com.setData(recipeDos);
        return com;
    }
}
