package org.popkit.leap.elpa.services;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ArchiveVo;
import org.popkit.leap.elpa.entity.PelpaContents;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.entity.RecipeVo;
import org.popkit.leap.elpa.utils.OriginSourceElpaUtils;
import org.popkit.leap.elpa.utils.PelpaUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:15:45
 */
public class LocalCache {

    private static final long UPDATE_RECIPE_TIME = 1000*60*30; // 30 minutes

    private static Date lastUpdateTime;
    private static final ConcurrentHashMap<String, ArchiveVo> ARCHIVE = new ConcurrentHashMap<String, ArchiveVo>();
    private static final ConcurrentHashMap<String, RecipeDo> RECIPES = new ConcurrentHashMap<String, RecipeDo>();

    public static Map<String, ArchiveVo> getArchive() {
        return ARCHIVE;
    }

    public static ArchiveVo getArchive(String pkgName) {
        return ARCHIVE.get(pkgName);
    }

    public static void updateArchive(String pkgName, ArchiveVo archiveVo) {
        ARCHIVE.put(pkgName, archiveVo);
    }

    public static void updateDls(String pkgName, int dls) {
        if (RECIPES.containsKey(pkgName)) {
            RECIPES.get(pkgName).setDls(dls);
        }
    }

    public static void removeArchive(String pkgName) {
        if (ARCHIVE.containsKey(pkgName)) {
            ARCHIVE.remove(pkgName);
        }
    }

    public static void removeRecipe(String pkgName) {
        if (RECIPES.containsKey(pkgName)) {
            RECIPES.remove(pkgName);
        }
    }

    public static void writeArchiveJSON() {
        File file = new File(PelpaUtils.getHtmlPath() + PelpaContents.ARCHIVE_JSON_FILE_NAME);
        try {
            String json = LocalCache.getArchiveJSON();
            FileUtils.writeStringToFile(file, json);
        } catch (IOException e) {
            LeapLogger.warn("error writeArchiveJson", e);
        }
    }

    public static List<RecipeDo> getAllRecipeList() {
        List<RecipeDo> list = new ArrayList<RecipeDo>();
        for (String item : RECIPES.keySet()) {
            list.add(RECIPES.get(item));
        }
        return list;
    }
    /**
     * generate/update recipes.json file
     */
    public static void writeRecipesJson() {
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

    public static String getArchiveJSON() {
        JSONObject jsonObject = new JSONObject();
        if (MapUtils.isNotEmpty(ARCHIVE)) {
            for (String pkgName : ARCHIVE.keySet()) {
                jsonObject.put(pkgName, convert(ARCHIVE.get(pkgName)));
            }
        }
        return jsonObject.toJSONString();
    }

    private static ArchiveVo convert(ArchiveVo archiveVo) {
        if (StringUtils.isBlank(archiveVo.getDesc())) {
            archiveVo.setDesc(" ");
        }
        return archiveVo;
    }

    public static String getRecipesJSON() {
        JSONObject jsonObject = new JSONObject();
        if (MapUtils.isNotEmpty(RECIPES)) {
            for (String pkgName : RECIPES.keySet()) {
                jsonObject.put(pkgName, RECIPES.get(pkgName));
            }
        }
        return jsonObject.toJSONString();
    }

    public static String url(String url) {
        return url;
        //return url.replaceAll("/","\\/");
    }


    public static boolean initLocalCache() {
        // 按时间来,超时2个小时更新一次
        if (lastUpdateTime != null
                && lastUpdateTime.getTime() + UPDATE_RECIPE_TIME > new Date().getTime()) {
            return true;
        }

        if (MapUtils.isNotEmpty(RECIPES)) {
            for (String pkgName : RECIPES.keySet()) {
                RECIPES.remove(pkgName);
            }
        }

        if (MapUtils.isNotEmpty(ARCHIVE)) {
            for (String item : ARCHIVE.keySet()) {
                ARCHIVE.remove(item);
            }
        }

        List<RecipeDo> recipeDos = PelpaUtils.asRecipeArch(PelpaUtils.getRecipeFileList());
        if (CollectionUtils.isNotEmpty(recipeDos)) {
            Set<String> pkg = new HashSet<String>();
            for (RecipeDo recipeDo : recipeDos) {
                RECIPES.put(recipeDo.getPkgName(), recipeDo);
                pkg.add(recipeDo.getPkgName());
            }

            List<RecipeDo> recipeDosSource = OriginSourceElpaUtils.collectionRecipes();
            if (CollectionUtils.isNotEmpty(recipeDosSource)) {
                for (RecipeDo recipeDo : recipeDosSource) {
                    if (!pkg.contains(recipeDo.getPkgName())) {
                        RECIPES.put(recipeDo.getPkgName(), recipeDo);
                    }
                }
            }
            lastUpdateTime = new Date();
            LeapLogger.info("RECIPES updated. TimeStamp:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastUpdateTime));
        }
        return true;
    }

    // TODO 每次新的build开始,要清空ARCHIVE

    public static boolean updateLastCommit(String pkgName, long lastcommit) {
        if (RECIPES.containsKey(pkgName)) {
            RECIPES.get(pkgName).setLastCommit(lastcommit);
            return RECIPES.get(pkgName).getLastCommit() == lastcommit;
        } else {
            return false;
        }
    }

    public static RecipeDo getRecipeDo(String pkgName) {
        return RECIPES.get(pkgName);
    }

    public static void main(String[] args) {
        String url = "http://github.com/aaptel/preview-latex";
        String right = "http:\\/\\/github.com\\/aaptel\\/preview-latex";
        System.out.println("origin:" + url);
        System.out.println("after:" + url(url));
        System.out.println("right:" + right);
    }
}
