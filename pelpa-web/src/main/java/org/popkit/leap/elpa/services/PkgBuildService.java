package org.popkit.leap.elpa.services;

import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * 将下载下来的package构建成标准格式
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:11:10
 */
@Service
public class PkgBuildService {
    @Autowired
    private RecipesService recipesService;

    public void d8() {
        RecipeDo recipeDo = recipesService.randomRecipe();
        buildPackage(recipeDo);
    }

    public void buildPackage(RecipeDo recipeDo) {
        String workingPath = PelpaUtils.getWorkingPath(recipeDo.getPkgName());
        File workingPathFile = new File(workingPath);

        if (workingPathFile.exists() && workingPathFile.isDirectory()) {
            List<File> elispFile = PelpaUtils.getElispFile(workingPath);
            if (elispFile.size() == 1) {
                buildSingleFilePackage(elispFile.get(0), recipeDo);
            }
        }
    }

    public void buildSingleFilePackage(File elispfile, RecipeDo recipeDo) {
        String htmlPath = PelpaUtils.getHtmlPath();

    }

}
