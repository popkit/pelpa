package org.popkit.leap.elpa.services;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.ArchiveVo;
import org.popkit.leap.elpa.entity.DepsItem;
import org.popkit.leap.elpa.entity.PropsItem;
import org.popkit.leap.elpa.entity.RecipeDo;
import org.popkit.leap.elpa.utils.PelpaUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-16:22:47
 */
public class ArchiveContentsGenerator {

    public static void main(String[] args) throws IOException{
        String acc = "\"aaa\"";
        System.out.println("acc=" + acc);
        String result = wrapQuote(replaceInnerQuote(acc));
        FileUtils.writeStringToFile(new File("/Users/aborn/aac.txt"), result);
        System.out.println("acc=" + result);
    }

    public static String updateAC() {
        String htmlPath = PelpaUtils.getHtmlPath();
        String fileName = htmlPath + "packages/archive-contents";
        File archiveContents = new File(fileName);
        String result = generator();
        try {
            LeapLogger.info("write content to " + fileName);
            if (StringUtils.isNotBlank(result)) {
                FileUtils.writeStringToFile(archiveContents, result);
            }
        } catch (Exception e) {
            LeapLogger.warn("error write to archive-contents file failed! fileName=" + fileName);
        }
        return result;
    }

    public static String generator() {
        List<RecipeDo> recipeDos = LocalCache.getAllRecipeList();
        if (CollectionUtils.isNotEmpty(recipeDos)) {
            List<String> recipesNames = new ArrayList<String>();
            for (RecipeDo recipeDo : recipeDos) {
                recipesNames.add(recipeDo.getPkgName());
            }
            Collections.sort(recipesNames, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o2.compareTo(o1);    // z -> a
                }
            });

            StringBuilder sbList = new StringBuilder("");
            for (String pkgName : recipesNames) {
                RecipeDo recipeDo = LocalCache.getRecipeDo(pkgName);
                ArchiveVo archiveVo = LocalCache.getArchive(pkgName);
                if (recipeDo != null && archiveVo != null) {
                    String version = wrapBracket(StringUtils.join(archiveVo.getVer(), " "));
                    String deps = buildDeps(archiveVo.getDepsList());
                    String shortInfo = replaceInnerQuote(archiveVo.getDesc());
                    String type = archiveVo.getType();
                    String props = buildProps(archiveVo.getProps());
                    String itemValueString = wrapSBracket(version
                            + " " + deps + " " + wrapQuote(shortInfo) + " " + type + " " + props
                    );
                    sbList.append(wrapPair(pkgName, itemValueString));
                }
            }
            String result = wrapBracket("1 " + sbList.toString());
            return result;
        } else {
            return StringUtils.EMPTY;
        }
    }

    public static List<String> diff() {
        List<String> result = new ArrayList<String>();
        List<RecipeDo> recipeDos = LocalCache.getAllRecipeList();
        if (CollectionUtils.isNotEmpty(recipeDos)) {
            List<String> recipesNames = new ArrayList<String>();
            for (RecipeDo recipeDo : recipeDos) {
                recipesNames.add(recipeDo.getPkgName());
            }
            Collections.sort(recipesNames, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o2.compareTo(o1);    // z -> a
                }
            });

            for (String pkgName : recipesNames) {
                RecipeDo recipeDo = LocalCache.getRecipeDo(pkgName);
                ArchiveVo archiveVo = LocalCache.getArchive(pkgName);
                if (recipeDo == null || archiveVo == null) {
                    result.add(pkgName);
                }
            }
            return result;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public static String wrapPair(String key, String value) {
        return "(" + key + " . " + value + ")";
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

    public static String replaceInnerQuote(String origin) {
        if (StringUtils.isBlank(origin)) {
            return "";
        } else if (origin.contains("\"")) {
            return origin.replace("\"", "\\\"");
        } else {
            return origin;
        }
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
