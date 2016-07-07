package org.popkit.leap.log.entity;

/**
 * Author: guobao.jiang
 * Email : guobao.jiang@dianping.com
 * Date  : 07-07-2016
 * Time  : 9:57 AM
 */
public class DownloadCount {
    private String json;
    private int total;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
