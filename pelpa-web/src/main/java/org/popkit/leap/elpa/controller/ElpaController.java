package org.popkit.leap.elpa.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.leap.common.BaseController;
import org.popkit.leap.elpa.entity.ArchiveVo;
import org.popkit.leap.elpa.entity.PackageItemVo;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.services.LocalCache;
import org.popkit.leap.elpa.services.RecipesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-06-04:18:38
 */
@Controller
@RequestMapping(value = "elpa")
public class ElpaController extends BaseController {

    @Autowired
    private RecipesService recipesService;

    @RequestMapping(value = "index.html")
    public String index(HttpServletRequest request) {
        request.setAttribute("pkgs", getPackages());
        return "elpa/index";
    }

    private List<PackageItemVo> getPackages() {
        List<PackageItemVo> result = new ArrayList<PackageItemVo>();
        List<RecipeDo> recipeDos = recipesService.getAllRecipeList();
        if (CollectionUtils.isNotEmpty(recipeDos)) {
            List<String> recipesNames = new ArrayList<String>();
            for (RecipeDo recipeDo : recipeDos) {
                recipesNames.add(recipeDo.getPkgName());
            }
            Collections.sort(recipesNames, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);    // z -> a
                }
            });

            StringBuilder sbList = new StringBuilder("");
            for (String pkgName : recipesNames) {
                RecipeDo recipeDo = recipesService.getRecipeDo(pkgName);
                ArchiveVo archiveVo = LocalCache.getArchive(pkgName);
                if (recipeDo != null && archiveVo != null) {
                    PackageItemVo packageItemVo = new PackageItemVo();
                    packageItemVo.setPkgName(recipeDo.getPkgName());
                    packageItemVo.setDls(0);
                    packageItemVo.setDesc(archiveVo.getDesc());
                    packageItemVo.setFetcher(recipeDo.getFetcher());
                    packageItemVo.setVersion(StringUtils.join(archiveVo.getVer(), "."));
                    packageItemVo.setRecipeUrl(" ");
                    result.add(packageItemVo);
                }
            }
        }

        return result;
    }
}
