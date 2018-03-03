package org.popkit.leap.geekpen.entity;

import java.util.Date;
/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * Date  : 02-27-2018
 * Time  : 8:03 PM
 */
public class RecordVo {
    private int id;
    private String bookName;
    private int progress;
    private int type;
    private String openid;
    private Date time;
    private Users user;

    public RecordVo() {
    }

    public RecordVo(Records records) {
        this.bookName = records.getBookName();
        this.progress = records.getProgress();
        this.type = records.getType();
        this.time = records.getUpdateTime();
        this.id = records.getId();
        this.openid = records.getOpenid();
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
