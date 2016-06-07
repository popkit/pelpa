package org.popkit.leap.elpa.entity;

import org.popkit.leap.elpa.utils.ToolUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 每进行一次全局构建
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:21:16
 */
public class RoundRun {
    private int roundId;      // 依次递增
    private Date startTime;
    private Date endTime = null;

    private RoundStatus status;

    private long lastRoundTimeUsed;

    @Override
    public String toString() {
        return "RoundRun{" +
                "roundId=" + roundId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", lastRoundTimeUsed=" + lastRoundTimeUsed +
                '}' + "@" + Integer.toHexString(hashCode());
    }

    public String tohumanable() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append("roundId=" + roundId);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm.SS");
        if (startTime != null) {
            stringBuilder.append("开始于:").append(simpleDateFormat.format(startTime));
        }

        if (endTime != null) {
            stringBuilder.append(", 结束于:").append(simpleDateFormat.format(endTime));
        }

        if (status != null) {
            stringBuilder.append(", 当前状态:").append(status);
        }

        if (lastRoundTimeUsed > 0) {
            stringBuilder.append(", 耗时:").append(ToolUtils.toHumanable(lastRoundTimeUsed));
        }

        return stringBuilder.toString();
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

    public RoundStatus getStatus() {
        return status;
    }

    public void setStatus(RoundStatus status) {
        this.status = status;
    }

    public long getLastRoundTimeUsed() {
        return lastRoundTimeUsed;
    }

    public void setLastRoundTimeUsed(long lastRoundTimeUsed) {
        this.lastRoundTimeUsed = lastRoundTimeUsed;
    }
}
