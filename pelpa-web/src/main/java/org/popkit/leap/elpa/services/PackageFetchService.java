package org.popkit.leap.elpa.services;

import org.apache.commons.collections.CollectionUtils;
import org.popkit.core.config.LeapConfigLoader;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:09:52
 */
@Service
public class PackageFetchService {
    private static final String WORKING_DIR_KEY = "elpa_working_path";

    @Autowired
    private RecipesService recipesService;

    @Autowired
    private List<FetchHandler> fetchHandlerList;

    public void d8() {
        RecipeDo recipeDo = recipesService.randomRecipe();
        downloadPackage(recipeDo);
    }

    public void downloadPackage(RecipeDo recipeDo) {
        if (recipeDo == null) {
            return;
        }

        String workPath = LeapConfigLoader.get(WORKING_DIR_KEY);
        File pkgPath = new File(workPath + recipeDo.getPkgName());
        if (!pkgPath.exists()) {
            pkgPath.mkdir();
        }
        Map<String, Object> extra = new HashMap<String, Object>();
        extra.put("pkgPath", pkgPath);

        if (CollectionUtils.isNotEmpty(fetchHandlerList)) {
            for (FetchHandler fetchHandler : fetchHandlerList) {
                if (fetchHandler.validate(recipeDo, extra)) {
                    fetchHandler.execute(recipeDo, extra);
                }
            }
        }
    }
}
