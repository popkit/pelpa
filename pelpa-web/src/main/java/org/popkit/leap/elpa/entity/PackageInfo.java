package org.popkit.leap.elpa.entity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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

    public void setShortInfoIfAbsent(String shortInfo) {
        if (StringUtils.isBlank(this.shortInfo) && StringUtils.isNotBlank(shortInfo)) {
            this.shortInfo = shortInfo;
        }
    }

    public String getReadmeInfo() {
        return readmeInfo;
    }

    public void setReadmeInfo(String readmeInfo) {
        this.readmeInfo = readmeInfo;
    }

    public void setReadmeInfoIfAbsent(String readmeInfo) {
        if (StringUtils.isBlank(this.readmeInfo)
                && StringUtils.isNotBlank(readmeInfo)) {
            this.readmeInfo = readmeInfo;
        }
    }

    public List<DepsItem> getDeps() {
        return deps;
    }

    public void setDeps(List<DepsItem> deps) {
        this.deps = deps;
    }

    public void setDepsIfAbsent(List<DepsItem> deps) {
        if (CollectionUtils.isEmpty(this.deps)) {
            this.deps = deps;
        }
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setKeywordsIfAbsent(List<String> keywords) {
        if (CollectionUtils.isEmpty(this.keywords)) {
            this.keywords = keywords;
        }
    }
}
