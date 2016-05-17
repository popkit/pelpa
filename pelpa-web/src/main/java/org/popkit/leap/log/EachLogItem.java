package org.popkit.leap.log;

import java.util.Date;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-17:22:14
 */
public class EachLogItem {
    private String pkgName;
    private Date date;
    private int count;

    public EachLogItem(String pkgName, Date date) {
        this.pkgName = pkgName;
        this.date = date;
        this.count = 1;
    }

    @Override
    public String toString() {
        return "EachLogItem{" +
                "pkgName='" + pkgName + '\'' +
                ", date=" + date +
                ", count=" + count +
                '}';
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public int getCount() {
        return count;
    }

    public void countIt() {
        this.count ++;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
