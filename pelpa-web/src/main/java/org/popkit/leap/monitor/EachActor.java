package org.popkit.leap.monitor;

import java.util.Date;

/**
 * 每个包的状态
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:21:55
 */
public class EachActor {
    private String pkgName;
    private boolean fetchFinished;      // 是否下载成功
    private boolean buildFinished;      // 是否构建成功
    private int roundId;
    private Date startTime;        // 这个包开始更新的时间
    private Date endTime;          // 这个包更新结束的时间

    public EachActor(String pkgName, int roundId) {
        this.pkgName = pkgName;
        this.roundId = roundId;
        this.fetchFinished = false;
        this.buildFinished = false;
        this.endTime = null;
        this.startTime = new Date();
    }

    public boolean isFinished() {
        return fetchFinished && buildFinished;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public boolean isFetchFinished() {
        return fetchFinished;
    }

    public void setFetchFinished(boolean fetchFinished) {
        this.fetchFinished = fetchFinished;
    }

    public boolean isBuildFinished() {
        return buildFinished;
    }

    public void setBuildFinished(boolean buildFinished) {
        this.buildFinished = buildFinished;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
