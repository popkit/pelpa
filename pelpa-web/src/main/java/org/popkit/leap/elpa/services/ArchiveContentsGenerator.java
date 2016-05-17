package org.popkit.leap.elpa.services;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.leap.elpa.entity.DepsItem;
import org.popkit.leap.elpa.entity.PropsItem;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-16:22:47
 */
@Service
public class ArchiveContentsGenerator {

    @Autowired
    private RecipesService recipesService;

    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add("abc");
        list.add("zbc");
        list.add("kkk");
        Collections.sort(list, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });

        System.out.print("" + list);
    }


    public String generator() {
        List<RecipeDo> recipeDos = recipesService.getAllRecipeList();
        if (CollectionUtils.isNotEmpty(recipeDos)) {
            List<String> recipesNames = new ArrayList<String>();
            for (RecipeDo recipeDo : recipeDos) {
                recipesNames.add(recipeDo.getPkgName());
            }
            Collections.sort(recipesNames, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
        }

        String result = "";

        return result;
    }

    public static String wrapBracket(String origin) {
        return "(" + origin + ")";
    }

    public static String wrapSBracket(String origin) {
        return "[" + origin + "]";
    }

    public static String wrapVersion(List<Integer> versionList) {
        return wrapBracket(StringUtils.join(versionList, " "));
    }

    public static String wrapQuote(String origin) {
        return "\"" + origin + "\"";
    }

    public static String buildProps(PropsItem propsItem) {
        return wrapBracket(wrapUrl(propsItem.getUrl())
        + " " + wrapKeywords(propsItem.getKeywords()));
    }

    public static String wrapUrl(String url) {
        return wrapBracket(
                "url: ." + wrapQuote(url)
        );
    }

    public static String wrapKeywords(List<String> keywords) {
        if (CollectionUtils.isEmpty(keywords)) {
            return wrapBracket(":keywords " + wrapQuote(""));
        }

        StringBuilder sb = new StringBuilder("");
        for (String keyword : keywords) {
            sb.append(" ").append(wrapQuote(keyword));
        }

        return wrapBracket(":keywords " + sb.toString());
    }

    public static String buildDeps(List<DepsItem> depsItemList) {
        if (CollectionUtils.isEmpty(depsItemList)) {
            return " nil ";
        }

        StringBuilder sb = new StringBuilder("");
        for (DepsItem depsItem : depsItemList) {
            sb.append(wrapDepsItem(depsItem)).append(" ");
        }

        return wrapBracket(sb.toString());
    }

    public static String wrapDepsItem(DepsItem depsItem) {
        return wrapBracket(
                depsItem.getName() + " " +
        wrapBracket(StringUtils.join(depsItem.getVersions(), " "))
        );
    }
}
