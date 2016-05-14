package org.popkit.leap.elpa.entity;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:15:15
 */
public class PackageInfo {

    private String shortInfo;
    private String readmeInfo;

    public String getShortInfo() {
        return shortInfo;
    }

    public void setShortInfo(String shortInfo) {
        this.shortInfo = shortInfo;
    }

    public String getReadmeInfo() {
        return readmeInfo;
    }

    public void setReadmeInfo(String readmeInfo) {
        this.readmeInfo = readmeInfo;
    }
}
