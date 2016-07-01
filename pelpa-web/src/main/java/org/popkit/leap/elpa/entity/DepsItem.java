package org.popkit.leap.elpa.entity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.popkit.leap.elpa.utils.OriginSourceElpaUtils;
import org.popkit.leap.elpa.utils.RecipeParser;

import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:19:20
 */
public class DepsItem {
    private String name;
    private List<Integer> versions;

    public String getName() {
        return name;
    }

    /**
     * 将下面这种elisp格式转换成DepsItem List
     * ((emacs "24.4") (elscreen "1.4.6") (multi-term "1.3"))
     * ((cl-lib "0"))
     * @param str
     * @return
     */
    public static List<DepsItem> fromString(String str) {
        String unWrapContent = RecipeParser.extraPairContent(str).trim();
        return OriginSourceElpaUtils.parseDepsItemList(unWrapContent);
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getVersions() {
        return versions;
    }

    public String getVersionString() {
        if (CollectionUtils.isEmpty(versions)) {
            return "";
        }

        return StringUtils.join(versions, ".");
    }
    public void setVersions(List<Integer> versions) {
        this.versions = versions;
    }

    @Override
    public String toString() {
        return "DepsItem{" +
                "name='" + name + '\'' +
                ", versions=" + versions +
                '}';
    }

    public static void main(String[] args) {
        String testA = "((emacs \"24.4\") (elscreen \"1.4.6\") (multi-term \"1.3\"))";
        List<DepsItem> listA = fromString(testA);
        String testB = "((cl-lib \"0\"))";
        List<DepsItem> listB = fromString(testB);
    }
}
