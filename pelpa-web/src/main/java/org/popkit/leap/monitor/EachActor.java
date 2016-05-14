package org.popkit.leap.monitor;

import org.popkit.leap.elpa.entity.ActorStatus;

import java.util.Date;

/**
 * 每个包的状态
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:21:55
 */
public class EachActor {
    private String pkgName;
    private ActorStatus fetchStatus;      // 是否下载成功
    private ActorStatus buildStatus;      // 是否构建成功
    private int roundId;
    private Date startTime;        // 这个包开始更新的时间
    private Date endTime;          // 这个包更新结束的时间

    public EachActor(String pkgName, int roundId) {
        this.pkgName = pkgName;
        this.roundId = roundId;
        this.fetchStatus = ActorStatus.READY;
        this.buildStatus = ActorStatus.READY;
        this.endTime = null;
        this.startTime = new Date();
    }

    public boolean isFinished() {
        return fetchStatus == ActorStatus.FINISHED &&
                buildStatus == ActorStatus.FINISHED;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public boolean isBuildFinished() {
        return buildStatus == ActorStatus.FINISHED;
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

    public ActorStatus getFetchStatus() {
        return fetchStatus;
    }

    public void setFetchStatus(ActorStatus fetchStatus) {
        this.fetchStatus = fetchStatus;
    }

    public ActorStatus getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(ActorStatus buildStatus) {
        this.buildStatus = buildStatus;
    }
}
