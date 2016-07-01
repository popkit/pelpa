package org.popkit.leap.elpa.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.core.logger.LeapLogger;
import org.popkit.leap.elpa.entity.DepsItem;
import org.popkit.leap.elpa.entity.OriginSource;
import org.popkit.leap.elpa.entity.RecipeDo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Aborn Jiang
 * Email : aborn.jiang@gmail.com
 * Date  : 07-01-2016
 * Time  : 9:24 AM
 */
public class OriginSourceElpaUtils {

    public static void main(String[] args) {
        List<RecipeDo> recipeDos = collectionRecipes();
        for (RecipeDo recipeDo : recipeDos) {
            System.out.println(recipeDo.toString());
        }
    }

    public static List<OriginSource> getSourceElpaList() {
        List<OriginSource> result = new ArrayList<OriginSource>();
        result.add(new OriginSource("gnu", "http://elpa.gnu.org/packages/"));
        result.add(new OriginSource("org", "http://orgmode.org/elpa/"));
        return result;
    }

    public static List<RecipeDo> collectionRecipes() {
        List<RecipeDo> recipeDos = new ArrayList<RecipeDo>();
        try {
            for (OriginSource originSource : getSourceElpaList()) {
                File acLocalFile = new File(originSource.getLocalFilePath());
                if (!acLocalFile.exists()) {   // TODO: 上线时这行删除
                    FetchRemoteFileUtils.downloadFile(originSource.getRomoteArchiveContents(), originSource.getLocalFilePath());
                }
                if (!acLocalFile.exists()) { continue; }
                
                String acOriginValue = FileUtils.readFileToString(acLocalFile);
                recipeDos.addAll(parse2list(acOriginValue.trim(), originSource));
            }
        } catch (Exception e) {
            LeapLogger.warn("error in collectionRecipes", e);
        }

        return recipeDos;
    }

    public static List<RecipeDo> parse2list(String origin, OriginSource originSource) {
        List<RecipeDo> result = new ArrayList<RecipeDo>();
        String unwrapResult = RecipeParser.extraPairContent(origin);
        List<String> recipeStringList = parseStringAsArrayList(unwrapResult);
        for (String item : recipeStringList) {
            if (StringUtils.isNotBlank(item.trim())) {
                try {
                    RecipeDo recipeDo = parseEachRecipeList(item.trim());
                    recipeDo.setFetcher(originSource.getName());
                    result.add(recipeDo);
                } catch (Exception e) {
                    LeapLogger.warn("### error in " + item, e);
                    continue;
                }
            }
        }
        return result;
    }

    public static List<String> parseStringAsArrayList(String unwrapResult) {
        List<String> result = new ArrayList<String>();
        for (int i=0; i<unwrapResult.length(); i++) {
            if (unwrapResult.charAt(i) == '(') {
                int rightPair = RecipeParser.findAnotherBracket(i, unwrapResult);
                if (rightPair > 0) {
                    result.add(unwrapResult.substring(i,rightPair+1));
                    i = rightPair + 2;
                }
            }
        }
        return result;
    }

    public static RecipeDo parseEachRecipeList(String origin) {
        RecipeDo result = new RecipeDo();
        String unwrapResult = RecipeParser.extraPairContent(origin);
        int index = unwrapResult.indexOf('.');
        result.setPkgName(unwrapResult.substring(0, index).trim());
        String attributes = RecipeParser.extraPairContent(unwrapResult.substring(index + 1), '[', ']').trim();

        boolean versionOk = false;
        boolean depsOk = false;
        boolean shortInfoOK = false;
        for (int i=0; i<attributes.length(); i++) {
            if (attributes.charAt(i) == '(') {
                int rightPair = RecipeParser.findAnotherBracket(i, attributes);
                if (rightPair > 0) {
                    String content = RecipeParser.extraPairContent(attributes.substring(i, rightPair + 1)).trim();

                    if (!versionOk) {
                        String[] versionList = content.split("\\s+");
                        result.setVersionRegexp(StringUtils.join(versionList, "."));
                        versionOk = true;
                        i = rightPair + 1;
                        continue;
                    }

                    if (versionOk && !depsOk) {
                        result.setDepsItemList(parseDepsItemList(content.trim()));
                        depsOk = true;
                        i = rightPair + 1;
                        continue;
                    }

                    // 解析其他属性
                    if (versionOk && depsOk) {
                        List<String> otherStringList = parseStringAsArrayList(content);
                        for (String otherItem : otherStringList) {
                            String unwrapOtherItem = RecipeParser.extraPairContent(otherItem);
                            if (unwrapOtherItem.contains(":url")) {
                                int start = unwrapOtherItem.indexOf('"');
                                int end = unwrapOtherItem.lastIndexOf('"');
                                if (start > 0 && end > 0) {
                                    result.setUrl(unwrapOtherItem.substring(start + 1, end));
                                }
                            }
                        }
                    }
                }
            }

            if (versionOk && !depsOk) {
                String leftString = attributes.substring(i).trim();
                if (leftString.startsWith("nil")) {
                    depsOk = true;
                }
            }

            if (versionOk && depsOk) {
                if (attributes.charAt(i) == '"' && !shortInfoOK) {
                    int rightPair = attributes.indexOf("tar");
                    if (rightPair > 0) {
                        result.setType("tar");
                    } else {
                        rightPair = attributes.indexOf("single");
                        if (rightPair > 0) {
                            result.setType("single");
                            String content = attributes.substring(i, rightPair).replace("\"", "").trim();
                            result.setShortInfo(content);
                            shortInfoOK = true;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<DepsItem> parseDepsItemList(String origin) {
        List<DepsItem> depsItemList = new ArrayList<DepsItem>();
        List<String> depsStringList = parseStringAsArrayList(origin);
        for (String each : depsStringList) {
            DepsItem depsItem = new DepsItem();
            String unwrapEach = RecipeParser.extraPairContent(each);
            String[] nameVersionArr = unwrapEach.split("\\s+");
            depsItem.setName(nameVersionArr[0].trim());
            String versionStr = RecipeParser.extraPairContent(unwrapEach);
            List<Integer> versionList = new ArrayList<Integer>();
            for (String eachVersion : versionStr.split("\\s+")) {
                versionList.add(Integer.parseInt(eachVersion));
            }
            depsItem.setVersions(versionList);
            depsItemList.add(depsItem);
        }
        return depsItemList;
    }
}
