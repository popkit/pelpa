package org.popkit.leap.elpa.services;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.popkit.leap.elpa.entity.FetcherEnum;
import org.popkit.leap.elpa.entity.PelpaContents;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.entity.RecipeVo;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:06:59
 */
@Service
public class RecipesService {
    private static final ConcurrentHashMap<String, RecipeDo> RECIPE_DO_LIST = new ConcurrentHashMap<String, RecipeDo>();

    @PostConstruct
    private void init() {
        initRecipes();
    }

    public RecipeDo randomRecipe () {
        List<RecipeDo> list = getAllRecipeList();
        for (RecipeDo item : list) {
            if (item.getFetcherEnum() == FetcherEnum.GITHUB) {
                return item;
            }
        }

        return null;
    }

    // 生成 recipes.json 文件
    public void writeRecipesJson() {
        List<RecipeDo> list = getAllRecipeList();
        File file = new File(PelpaUtils.getHtmlPath() + PelpaContents.RECIPES_JSON_FILE_NAME);
        JSONObject jsonObject = new JSONObject();
        for (RecipeDo recipeDo : list) {
            jsonObject.put(recipeDo.getPkgName(), new RecipeVo(recipeDo));
        }

        String json = jsonObject.toString();
        try {
            FileUtils.writeStringToFile(file, json);
        } catch (IOException e) {
            //
        }
    }

    public List<RecipeDo> getAllRecipeList() {
        boolean debug = true;
        List<RecipeDo> recipeDos = new ArrayList<RecipeDo>();
        int i=0;
        for (String item : RECIPE_DO_LIST.keySet()) {
            if (debug && i >= 10) {   // debug时只用10
                break;
            }

            recipeDos.add(RECIPE_DO_LIST.get(item));
            i++;
        }
        return recipeDos;
    }

    private void initRecipes() {
        List<RecipeDo> recipeDos = PelpaUtils.asRecipeArch(PelpaUtils.getRecipeFileList());
        if (CollectionUtils.isNotEmpty(recipeDos)) {
            for (RecipeDo recipeDo : recipeDos) {
                RECIPE_DO_LIST.putIfAbsent(recipeDo.getPkgName(), recipeDo);
            }
        }
    }
}
