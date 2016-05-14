package org.popkit.leap.elpa.entity;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:14:08
 */
public class RecipeVo {
    private String fetcher;
    private String repo;
    private List<String> files;

    public RecipeVo(RecipeDo recipeDo) {
        this.fetcher = recipeDo.getFetcher();
        this.repo = recipeDo.getRepo();
    }

    public String toJSON() {
        return JSON.toJSONString(this);
    }

    @Override
    public String toString() {
        return toJSON();
    }

    public String getFetcher() {
        return fetcher;
    }

    public void setFetcher(String fetcher) {
        this.fetcher = fetcher;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
