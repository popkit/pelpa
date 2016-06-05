package org.popkit.leap.elpa.entity;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-06-05:18:10
 */
public class PackageItemVo {

    private String pkgName;

    private String desc;

    private String version;

    private String fetcher;

    private String recipeUrl;

    private int dls;

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        if (desc.contains("'") || desc.contains("\"")
                || desc.contains("`")) {
            this.desc = desc.replace("'", "").replace("\"", "").replace("`", "");
        } else {
            this.desc = desc;
        }

    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFetcher() {
        return fetcher;
    }

    public void setFetcher(String fetcher) {
        this.fetcher = fetcher;
    }

    public String getRecipeUrl() {
        return recipeUrl;
    }

    public void setRecipeUrl(String recipeUrl) {
        this.recipeUrl = recipeUrl;
    }

    public int getDls() {
        return dls;
    }

    public void setDls(int dls) {
        this.dls = dls;
    }
}
