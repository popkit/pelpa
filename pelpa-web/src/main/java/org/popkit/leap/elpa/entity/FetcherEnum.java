package org.popkit.leap.elpa.entity;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:07:15
 */
public enum FetcherEnum {

    GITHUB("github"),
    GIT("git"),      // Like gist
    WIKI("wiki"),
    GITLAB("gitlab"),
    BITBUCKET("bitbucket"),
    UNKNOWN("null"),
    GNU("gnu"),      // GNU官方源
    ORG("org"),      // ORG官方源
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

    public static boolean isOriginSource(String fetcher) {
        FetcherEnum fetcherEnum = getFetcher(fetcher);
        return fetcherEnum == GNU || fetcherEnum == ORG;
    }

    public String getFetcher() {
        return fetcher;
    }

    public void setFetcher(String fetcher) {
        this.fetcher = fetcher;
    }
}
