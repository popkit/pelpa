package org.popkit.leap.elpa.entity;

import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:15:15
 */
public class PackageInfo {
    // 简单描述
    private String shortInfo;
    // 详情描述,用于reademe
    private String readmeInfo;

    private List<DepsItem> deps;

    private List<String> keywords;

    public String getShortInfo() {
        return shortInfo;
    }

    public void setShortInfo(String shortInfo) {
        if (shortInfo == null) {
            this.shortInfo = "";
        } else {
            this.shortInfo = shortInfo.trim();
        }
    }

    public String getReadmeInfo() {
        return readmeInfo;
    }

    public void setReadmeInfo(String readmeInfo) {
        this.readmeInfo = readmeInfo;
    }

    public List<DepsItem> getDeps() {
        return deps;
    }

    public void setDeps(List<DepsItem> deps) {
        this.deps = deps;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
