package org.popkit.leap.geekpen.entity;

import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * Date  : 02-27-2018
 * Time  : 7:56 PM
 */
public class ReadRecords {
    private String openid;
    private List<RecordVo> records;


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public List<RecordVo> getRecords() {
        return records;
    }

    public void setRecords(List<RecordVo> records) {
        this.records = records;
    }
}
