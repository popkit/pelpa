package org.popkit.leap.elpa.services;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.leap.elpa.entity.ArchiveVo;
import org.popkit.leap.elpa.entity.RecipeVo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:15:45
 */
public class LocalCache {
    private static final ConcurrentHashMap<String, ArchiveVo> archive = new ConcurrentHashMap<String, ArchiveVo>();
    private static final ConcurrentHashMap<String, RecipeVo> recipes = new ConcurrentHashMap<String, RecipeVo>();

    public static Map<String, ArchiveVo> getArchive() {
        return archive;
    }

    public static void updateArchive(String pkgName, ArchiveVo archiveVo) {
        archive.put(pkgName, archiveVo);
    }

    public static void updateRecipe(String pkgName, RecipeVo recipeVo) {
        recipes.put(pkgName, recipeVo);
    }

    public static void removeArchive(String pkgName) {
        if (archive.containsKey(pkgName)) {
            archive.remove(pkgName);
        }
    }

    public static void removeRecipe(String pkgName) {
        if (recipes.containsKey(pkgName)) {
            recipes.remove(pkgName);
        }
    }

    public static String getArchiveJSON() {
        JSONObject jsonObject = new JSONObject();
        if (MapUtils.isNotEmpty(archive)) {
            for (String pkgName : archive.keySet()) {
                jsonObject.put(pkgName, convert(archive.get(pkgName)));
            }
        }
        return jsonObject.toJSONString();
    }

    private static ArchiveVo convert(ArchiveVo archiveVo) {
        ArchiveVo archiveVo1 = ArchiveVo.fromJSONString(archiveVo.toJSONString());
        if (StringUtils.isBlank(archiveVo.getDesc())) {
            archiveVo1.setDesc(" ");
            return archiveVo1;
        }

        /**
        if (archiveVo1 != null && archiveVo1.getProps() != null) {
            PropsItem propsItem = archiveVo1.getProps();
            if (StringUtils.isNotBlank(propsItem.getUrl())) {
                propsItem.setUrl(url(propsItem.getUrl()));
                return archiveVo1;
            }
        }
         **/

        return archiveVo;
    }

    public static String getRecipesJSON() {
        JSONObject jsonObject = new JSONObject();
        if (MapUtils.isNotEmpty(recipes)) {
            for (String pkgName : recipes.keySet()) {
                jsonObject.put(pkgName, recipes.get(pkgName));
            }
        }
        return jsonObject.toJSONString();
    }

    public static String url(String url) {
        return url;
        //return url.replaceAll("/","\\/");
    }

    public static void main(String[] args) {
        String url = "http://github.com/aaptel/preview-latex";
        String right = "http:\\/\\/github.com\\/aaptel\\/preview-latex";
        System.out.println("origin:" + url);
        System.out.println("after:" + url(url));
        System.out.println("right:" + right);
    }
}
