package org.popkit.leap.elpa.entity;

import org.apache.commons.lang3.StringUtils;
import org.popkit.leap.elpa.annotation.Record;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:07:14
 */
public class RecipeDo {
    private String pkgName;
    private String fetcher;
    private String repo;

    @Record(key = "version-regexp")
    private String versionRegexp;   // version-regexp

    private String files;

    public String getPkgName() {
        return pkgName;
    }

    public FetcherEnum getFetcherEnum() {
        return FetcherEnum.getFetcher(this.fetcher);
    }

    public void update(String key, String value) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Record record = field.getAnnotation(Record.class);
            if (record != null && StringUtils.isNotBlank(record.key())
                    && record.key().equals(key)) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, this, value);
            }

            if (field.getName().equals(key)) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, this, value);
            }
        }
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
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

    public String getVersionRegexp() {
        return versionRegexp;
    }

    public void setVersionRegexp(String versionRegexp) {
        this.versionRegexp = versionRegexp;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }
}
