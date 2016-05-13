package org.popkit.leap.demo.demo.entity;

import org.popkit.core.annotation.LeapRequest;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-12:22:01
 */
@LeapRequest
public class ParamReq {
    @LeapRequest.Param(name = "id")
    private Integer id;

    @LeapRequest.Param
    private String name;

    @LeapRequest.Param
    private Double lat;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}
