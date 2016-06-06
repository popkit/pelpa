package org.popkit.leap.monitor.entity;

import com.alibaba.fastjson.JSONObject;
import org.popkit.leap.elpa.entity.RoundRun;
import org.popkit.leap.monitor.RoundSupervisor;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-15:16:39
 */
public class BuildStatus {

    private long started;
    private long completed;
    private long next;
    private long duration;

    public BuildStatus() {
    }

    public BuildStatus(RoundRun run) {
        if (run.getStartTime() != null) {
            this.started = run.getStartTime().getTime() / 1000;
        }

        if (run.getStartTime() != null && run.getEndTime() != null) {
            duration = (run.getEndTime().getTime() - run.getStartTime().getTime()) / 1000;
            next = (run.getEndTime().getTime() + RoundSupervisor.REST_TIME) / 1000;
            completed = run.getEndTime().getTime() / 1000;
        }

        if (run.getEndTime() == null) {
            duration = run.getLastRoundTimeUsed() / 1000;
        }
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("started", started);
        jsonObject.put("completed", completed);
        jsonObject.put("next", next);
        jsonObject.put("duration", duration);
        return jsonObject.toJSONString();
    }

    public long getStarted() {
        return started;
    }

    public void setStarted(long started) {
        this.started = started;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getNext() {
        return next;
    }

    public void setNext(long next) {
        this.next = next;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
