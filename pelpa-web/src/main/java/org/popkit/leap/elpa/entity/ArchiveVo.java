package org.popkit.leap.elpa.entity;

import java.util.List;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-14:14:27
 */
public class ArchiveVo {

    private List<Integer> ver;
    private String desc;
    private String type;   // "single"
    private PropsItem props;

    public List<Integer> getVer() {
        return ver;
    }

    public void setVer(List<Integer> ver) {
        this.ver = ver;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PropsItem getProps() {
        return props;
    }

    public void setProps(PropsItem props) {
        this.props = props;
    }
}
