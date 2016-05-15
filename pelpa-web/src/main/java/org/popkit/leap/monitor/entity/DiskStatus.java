package org.popkit.leap.monitor.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-15:17:19
 */
public class DiskStatus {
    private String used;
    private String avail;

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("used", used);
        jsonObject.put("avail", avail);
        return jsonObject.toJSONString();
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getAvail() {
        return avail;
    }

    public void setAvail(String avail) {
        this.avail = avail;
    }
}
