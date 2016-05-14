package org.popkit.leap.elpa.entity;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:07:15
 */
public enum FetcherEnum {

    GITHUB("github"),
    UNKNOWN("null")
    ;
    private String fetcher;

    private FetcherEnum(String fetcher) {
        this.fetcher = fetcher;
    }

    public static FetcherEnum getFetcher(String fetcher) {
        for (FetcherEnum fetcherEnum : FetcherEnum.values()) {
            if (fetcherEnum.getFetcher().equals(fetcher)) {
                return fetcherEnum;
            }
        }

        return UNKNOWN;
    }

    public String getFetcher() {
        return fetcher;
    }

    public void setFetcher(String fetcher) {
        this.fetcher = fetcher;
    }
}
