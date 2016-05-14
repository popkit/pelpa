package org.popkit.leap.elpa.services;

import org.apache.commons.collections.CollectionUtils;
import org.popkit.leap.elpa.entity.FetcherEnum;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.utils.PelpaUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

    public List<RecipeDo> getAllRecipeList() {
        List<RecipeDo> recipeDos = new ArrayList<RecipeDo>();
        for (String item : RECIPE_DO_LIST.keySet()) {
            recipeDos.add(RECIPE_DO_LIST.get(item));
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
