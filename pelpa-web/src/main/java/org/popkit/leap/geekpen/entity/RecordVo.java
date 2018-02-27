package org.popkit.leap.geekpen.entity;

import java.util.Date;
/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * Date  : 02-27-2018
 * Time  : 8:03 PM
 */
public class RecordVo {
    private String bookName;
    private int progress;
    private int type;
    private Date time;

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
}
