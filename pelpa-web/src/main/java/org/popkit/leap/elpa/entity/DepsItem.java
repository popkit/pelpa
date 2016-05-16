package org.popkit.leap.elpa.entity;

import org.popkit.core.logger.LeapLogger;

import java.util.ArrayList;
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
        String pureStr = str.trim().replaceAll("\\(", "")
                .replaceAll("\\)", "").replaceAll("\"", "");
        String[] pureStrArr = pureStr.split(" ");
        List<DepsItem> depsItems = new ArrayList<DepsItem>();
        try {
            for (int i = 0; i < pureStrArr.length; i++) {
                if (i%2 == 0) {
                    DepsItem depsItem = new DepsItem();
                    depsItem.setName(pureStrArr[i]);
                    depsItem.setVersions(toVersionList(pureStrArr[i+1]));
                    depsItems.add(depsItem);
                }
            }
        } catch (Exception e) {
            LeapLogger.warn("error parser DepsItem form string:" + str, e);
        }
        return depsItems;
    }

    private static List<Integer> toVersionList(String ver) {
        List<Integer> result = new ArrayList<Integer>();
        for (String item : ver.split("\\.")) {
            result.add(Integer.parseInt(item));
        }
        return result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getVersions() {
        return versions;
    }

    public void setVersions(List<Integer> versions) {
        this.versions = versions;
    }


    public static void main(String[] args) {
        String testA = "((emacs \"24.4\") (elscreen \"1.4.6\") (multi-term \"1.3\"))";
        List<DepsItem> listA = fromString(testA);
        String testB = "((cl-lib \"0\"))";
        List<DepsItem> listB = fromString(testB);
    }
}
